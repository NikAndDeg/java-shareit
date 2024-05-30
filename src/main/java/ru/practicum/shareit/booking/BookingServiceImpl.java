package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingDtoMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.booking.BookingNotFoundException;
import ru.practicum.shareit.exception.item.ItemNotFoundException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.exception.user.UserNotOwnerException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
	private final BookingRepository bookingRepository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;
	private final BookingDtoMapper bookingMapper;

	@Override
	public BookingDto addBooking(BookingDto bookingDto, int bookerId) {
		int itemId = bookingDto.getItemId();
		Booking booking = bookingMapper.toModel(bookingDto);
		if (!isValidBookingStartEndTime(booking.getStart(), booking.getEnd()))
			throw new BadRequestException("Wrong booking start-end time!");
		User booker = userRepository.findById(bookerId).orElseThrow(
				() -> new UserNotFoundException("Requester with id [" + bookerId + "] not exists.")
		);
		Item item = itemRepository.findWithOwnerById(itemId).orElseThrow(
				() -> new ItemNotFoundException("Item for booking with id [" + itemId + "] not exists.")
		);
		if (!item.getAvailable())
			throw new BadRequestException("Item not available.");
		if (item.getOwner().getId() == bookerId)
			throw new UserNotFoundException("Owner of item cannot be a booker.");
		booking.setUser(booker);
		booking.setItem(item);
		booking.setStatus(WAITING);
		Booking savedBooking = bookingRepository.save(booking);
		return bookingMapper.toDto(savedBooking, item, booker);
	}

	@Override
	public BookingDto approveBooking(int userId, int bookingId, String approved) {
		BookingStatus status = getStatusByApproved(approved);
		Booking booking = bookingRepository.findWithRequesterAndItemAndOwnerOfItemById(bookingId).orElseThrow(
				() -> new BookingNotFoundException("Booking with id [" + bookingId + "] not exists.")
		);
		User itemOwner = booking.getItem().getOwner();
		if (userId != itemOwner.getId())
			throw new UserNotOwnerException("User with id [" + userId + "] not owner of Item.");
		if (booking.getStatus() == APPROVED)
			throw new BadRequestException("Owner cannot change status after approval.");
		booking.setStatus(status);
		Booking savedBooking = bookingRepository.save(booking);
		return bookingMapper.toDto(savedBooking, booking.getItem(), booking.getUser());
	}

	@Override
	public BookingDto getBooking(int bookingId, int userId) {
		Booking booking = bookingRepository.findWithRequesterAndItemAndOwnerOfItemById(bookingId)
				.orElseThrow(
						() -> new BookingNotFoundException("Booking with id [" + bookingId + "] not exists.")
				);
		int bookerId = booking.getUser().getId();
		int ownerOfItemId = booking.getItem().getOwner().getId();
		if (userId != bookerId && userId != ownerOfItemId)
			throw new UserNotFoundException("User with id [" + userId + "] isn't owner or requester.");
		return bookingMapper.toDto(booking, booking.getItem(), booking.getUser());
	}

	@Override
	public List<BookingDto> getUserBookings(int userId, State state) {
		userRepository.findById(userId).orElseThrow(
				() -> new UserNotFoundException("User with id [" + userId + "] not exists.")
		);
		List<Booking> bookings;
		switch (state) {
			case CURRENT:
				bookings = bookingRepository.findAllByUserIdAndStartBeforeAndEndAfter(userId,
						LocalDateTime.now(),
						LocalDateTime.now());
				break;
			case PAST:
				bookings = bookingRepository.findWithItemAndUserAllByUserIdAndEndBefore(userId, LocalDateTime.now());
				break;
			case FUTURE:
				bookings = bookingRepository.findAllByUserIdAndStartAfter(userId, LocalDateTime.now());
				break;
			case WAITING:
				bookings = bookingRepository.findAllByUserIdAndStatusIs(userId, WAITING);
				break;
			case REJECTED:
				bookings = bookingRepository.findAllByUserIdAndStatusIs(userId, REJECTED);
				break;
			default:
				bookings = bookingRepository.findAllByUserId(userId);
		}
		return bookings.stream()
				.map(booking -> bookingMapper.toDto(booking, booking.getItem(), booking.getUser()))
				.sorted((BookingDto b1, BookingDto b2) -> {
					if (b1.getStart().isBefore(b2.getStart()))
						return 1;
					if (b1.getStart().isAfter(b2.getStart()))
						return -1;
					else
						return 0;
				})
				.collect(Collectors.toList());
	}

	@Override
	public List<BookingDto> getOwnerBookings(int ownerId, State state) {
		userRepository.findById(ownerId).orElseThrow(
				() -> new UserNotFoundException("User with id [" + ownerId + "] not exists.")
		);
		List<Booking> bookings;
		switch (state) {
			case CURRENT:
				bookings = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(ownerId,
						LocalDateTime.now(),
						LocalDateTime.now());
				break;
			case PAST:
				bookings = bookingRepository.findAllByOwnerIdAndEndBefore(ownerId, LocalDateTime.now());
				break;
			case FUTURE:
				bookings = bookingRepository.findAllByOwnerIdAndStartAfter(ownerId, LocalDateTime.now());
				break;
			case WAITING:
				bookings = bookingRepository.findAllByOwnerIdAndStatusIs(ownerId, WAITING);
				break;
			case REJECTED:
				bookings = bookingRepository.findAllByOwnerIdAndStatusIs(ownerId, REJECTED);
				break;
			default:
				bookings = bookingRepository.findAllByOwnerId(ownerId);
		}
		return bookings.stream()
				.map(booking -> bookingMapper.toDto(booking, booking.getItem(), booking.getUser()))
				.sorted((BookingDto b1, BookingDto b2) -> {
					if (b1.getStart().isBefore(b2.getStart()))
						return 1;
					if (b1.getStart().isAfter(b2.getStart()))
						return -1;
					else
						return 0;
				}).collect(Collectors.toList());
	}

	private boolean isValidBookingStartEndTime(LocalDateTime start, LocalDateTime end) {
		return start.isAfter(LocalDateTime.now())
				&& end.isAfter(LocalDateTime.now())
				&& start.isBefore(end)
				&& !start.isEqual(end);
	}

	private BookingStatus getStatusByApproved(String approved) {
		if (approved.equalsIgnoreCase("true"))
			return APPROVED;
		else if (approved.equalsIgnoreCase("false"))
			return REJECTED;
		else
			throw new BadRequestException("Invalid approved value [" + approved + "].");
	}
}
