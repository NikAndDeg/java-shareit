package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.item.ItemNotFoundException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.exception.user.UserNotOwnerException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemBookingsCommentsDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
	private static final int ITEM_MAX_NAME_SIZE = 200;
	private static final int ITEM_MAX_DESCRIPTION_SIZE = 200;

	private final UserRepository userRepository;
	private final ItemRepository itemRepository;
	private final BookingRepository bookingRepository;
	private final CommentRepository commentRepository;

	@Override
	@Transactional
	public ItemDto addItem(ItemDto itemDto, int ownerId) {
		Item item = ItemDto.toModel(itemDto);
		User owner = userRepository.findById(ownerId).orElseThrow(
				() -> new UserNotFoundException("Item not saved. Owner of item with id [" + ownerId + "] not exists.")
		);
		item.setOwner(owner);
		Item savedItem = itemRepository.save(item);
		return ItemDto.toDto(savedItem);
	}

	@Override
	@Transactional
	public ItemDto updateItem(ItemDto itemDto, int ownerId) {
		Item updatableItem = itemRepository.findWithOwnerById(itemDto.getId()).orElseThrow(
				() -> new ItemNotFoundException("Item not updated. Item with id [" + itemDto.getId() + "] not found.")
		);
		if (!updatableItem.getOwner().getId().equals(ownerId))
			throw new UserNotOwnerException("Item not updated. User isn't owner of item.");

		setUpdatedFieldsToUpdatableItem(itemDto, updatableItem);
		Item updatedItem = itemRepository.save(updatableItem);
		return ItemDto.toDto(updatedItem);
	}

	@Override
	public List<ItemBookingsCommentsDto> getAllItemsByUserId(Integer userId) throws UserNotFoundException {
		User user = userRepository.findWithItemsById(userId).orElseThrow(
				() -> new UserNotFoundException("Items not received. User with id [" + userId + "] not exists.")
		);
		if (user.getItems().isEmpty())
			return new ArrayList<>();
		List<Integer> itemsIds = user.getItems().stream()
				.map(Item::getId)
				.collect(Collectors.toList());
		List<Item> itemsWithBookings = itemRepository.findWithBookingsWithOwnerByIdIn(itemsIds);
		List<Comment> comments = commentRepository.findWithItemWithCommenterAllByItemIdIn(itemsIds);
		Map<Integer, List<Comment>> itemIdComments = getItemIdComments(comments);
		return itemsWithBookings.stream()
				.map(
						item -> {
							List<Optional<Booking>> lastNextBookings = findLastNextBooking(item.getBookings());
							List<Comment> itemComments = itemIdComments.get(item.getId());
							return ItemBookingsCommentsDto.toDto(item,
									lastNextBookings.get(0).orElse(null),
									lastNextBookings.get(1).orElse(null),
									itemComments);
						}
				)
				.collect(Collectors.toList());
	}

	@Override
	public ItemBookingsCommentsDto getItemById(int itemId, int userId) {
		User userRequester = userRepository.findById(userId).orElseThrow(
				() -> new UserNotFoundException("User not found. User with id [" + userId + "] not exists.")
		);
		Item item = itemRepository.findWithOwnerById(itemId).orElseThrow(
				() -> new ItemNotFoundException("Item not found. Item with id [" + itemId + "] not exists.")
		);

		List<Comment> itemComments = commentRepository.findWithCommenterAllByItemId(itemId);
		if (!userRequester.getId().equals(item.getOwner().getId()))
			return ItemBookingsCommentsDto.toDto(item, null, null, itemComments);

		List<Booking> itemBookings = bookingRepository.findWithBookerAllByItemIdAndStatusIn(itemId,
				List.of(BookingStatus.WAITING, BookingStatus.APPROVED));
		List<Optional<Booking>> lastNextBookings = findLastNextBooking(itemBookings);
		return ItemBookingsCommentsDto.toDto(item,
				lastNextBookings.get(0).orElse(null),
				lastNextBookings.get(1).orElse(null),
				itemComments);
	}

	@Override
	@Transactional
	public ItemDto deleteItemById(int itemId, int ownerId) {
		Item removableItem = itemRepository.findWithOwnerById(itemId).orElseThrow(
				() -> new ItemNotFoundException("Item not deleted. Item with id [" + itemId + "] not found.")
		);
		if (!isUserOwner(ownerId, removableItem))
			throw new UserNotOwnerException("Item not deleted. User isn't owner of item.");
		itemRepository.deleteById(itemId);
		return ItemDto.toDto(removableItem);
	}

	@Override
	public List<ItemDto> searchByText(String text) {
		if (text.isBlank())
			return new ArrayList<>();
		List<Item> items = itemRepository.findItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIs(text, text, true);
		return items.stream()
				.map(ItemDto::toDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public CommentDto addComment(int itemId, int commenterId, CommentDto commentDto) {
		Comment comment = CommentDto.toModel(commentDto);
		User userCommenter = userRepository.findById(commenterId).orElseThrow(
				() -> new UserNotFoundException("Comment not saved. User-commenter with id [" + commenterId + "] not found.")
		);
		Item item = itemRepository.findWithOwnerById(itemId).orElseThrow(
				() -> new ItemNotFoundException("Comment not saved. Item with id [" + itemId + "] not found.")
		);
		if (isUserOwner(commenterId, item))
			throw new BadRequestException("Owner cannot comment his item.");
		if (!isCommenterBooker(commenterId, itemId))
			throw new BadRequestException("Commenter not renter of item.");
		comment.setItem(item);
		comment.setUser(userCommenter);
		Comment savedComment = commentRepository.save(comment);
		return CommentDto.toDto(savedComment, userCommenter.getName());
	}

	private Map<Integer, List<Comment>> getItemIdComments(List<Comment> comments) {
		return comments.stream()
				.collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
	}

	private boolean isUserOwner(int userId, Item itemWithOwner) {
		return itemWithOwner.getOwner().getId().equals(userId);
	}

	private boolean isCommenterBooker(int commenterId, int itemId) {
		List<Booking> completedBookings = bookingRepository.findAllByUserIdAndItemIdAndStatusAndEndBefore(commenterId,
				itemId,
				BookingStatus.APPROVED,
				getTimeNow());
		return !completedBookings.isEmpty();
	}

	private void setUpdatedFieldsToUpdatableItem(ItemDto itemDto, Item updatableItem) {
		String newName = itemDto.getName();
		if (newName != null && !newName.isBlank() && newName.length() < ITEM_MAX_NAME_SIZE)
			updatableItem.setName(itemDto.getName());

		String newDescription = itemDto.getDescription();
		if (newDescription != null && !newDescription.isBlank() && newDescription.length() < ITEM_MAX_DESCRIPTION_SIZE)
			updatableItem.setDescription(itemDto.getDescription());


		if (itemDto.getAvailable() != null)
			updatableItem.setAvailable(itemDto.getAvailable());
	}

	private List<Optional<Booking>> findLastNextBooking(List<Booking> bookings) {
		if (bookings == null || bookings.isEmpty())
			return List.of(Optional.empty(), Optional.empty());

		if (bookings.size() == 1) {
			Booking booking = bookings.get(0);
			if (booking.getStart().isAfter(getTimeNow()))
				return List.of(Optional.empty(), Optional.of(booking));
			else
				return List.of(Optional.of(booking), Optional.empty());
		}

		Booking lastBooking = null;
		Booking nextBooking = null;
		for (Booking booking : bookings) {
			if (booking.getEnd().isBefore(getTimeNow())) {
				if (lastBooking == null)
					lastBooking = booking;
				else if (booking.getEnd().isAfter(lastBooking.getEnd()))
					lastBooking = booking;
			}

			if (booking.getStart().isAfter(getTimeNow())) {
				if (nextBooking == null)
					nextBooking = booking;
				else if (booking.getStart().isBefore(nextBooking.getStart()))
					nextBooking = booking;
			}
		}
		return List.of(Optional.ofNullable(lastBooking), Optional.ofNullable(nextBooking));
	}

	private LocalDateTime getTimeNow() {
		return LocalDateTime.now();
	}
}
