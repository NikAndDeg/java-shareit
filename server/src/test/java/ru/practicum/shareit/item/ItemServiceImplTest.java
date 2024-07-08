package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.item.ItemNotFoundException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.exception.user.UserNotOwnerException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemBookingsCommentsDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {
	private final Pageable pageable = PageRequest.of(0, 20);
	private UserRepository userRepository;
	private ItemRepository itemRepository;
	private BookingRepository bookingRepository;
	private CommentRepository commentRepository;
	private ItemRequestRepository requestRepository;
	private ItemServiceImpl service;
	private ItemDto itemDtoTo;
	private ItemDto itemDtoFrom;

	@BeforeEach
	void setUp() {
		userRepository = mock(UserRepository.class);
		itemRepository = mock(ItemRepository.class);
		bookingRepository = mock(BookingRepository.class);
		commentRepository = mock(CommentRepository.class);
		requestRepository = mock(ItemRequestRepository.class);
		service = new ItemServiceImpl(userRepository,
				itemRepository,
				bookingRepository,
				commentRepository,
				requestRepository);
	}

	@Test
	void test_addItem_without_request() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1)));
		when(requestRepository.findById(anyInt()))
				.thenReturn(Optional.of(createRequest(1)));
		when(itemRepository.save(any()))
				.thenReturn(createItem(1));

		itemDtoTo = ItemDto.builder()
				.name("n")
				.description("d")
				.available(true)
				.build();
		itemDtoFrom = service.addItem(itemDtoTo, 1);
		assertEquals(1, itemDtoFrom.getId());
		assertNull(itemDtoFrom.getRequestId());
	}

	@Test
	void test_addItem_with_request() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1)));
		when(requestRepository.findById(anyInt()))
				.thenReturn(Optional.of(createRequest(1)));
		when(itemRepository.save(any()))
				.thenReturn(createItem(1));

		itemDtoTo = ItemDto.builder()
				.name("n")
				.description("d")
				.available(true)
				.requestId(2)
				.build();
		itemDtoFrom = service.addItem(itemDtoTo, 1);
		assertEquals(1, itemDtoFrom.getId());
		assertEquals(2, itemDtoFrom.getRequestId());
	}

	@Test
	void test_addItem_owner_not_exists() {

		itemDtoTo = ItemDto.builder()
				.name("n")
				.description("d")
				.available(true)
				.build();
		Exception exception = assertThrows(UserNotFoundException.class,
				() -> service.addItem(itemDtoTo, 1));
		assertEquals("Item not saved. Owner of item with id [1] not exists.", exception.getMessage());
	}

	@Test
	void test_addItem_request_not_exists() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1)));
		itemDtoTo = ItemDto.builder()
				.name("n")
				.description("d")
				.available(true)
				.requestId(2)
				.build();
		Exception exception = assertThrows(DataNotFoundException.class,
				() -> service.addItem(itemDtoTo, 1));
		assertEquals("Request with id [2] not found.", exception.getMessage());
	}

	@Test
	void test_updateItem() {
		when(itemRepository.findWithOwnerById(anyInt()))
				.thenReturn(Optional.of(createItemWithOwner(1, createUser(1))));
		when(itemRepository.save(any()))
				.thenReturn(createItem(1));
		itemDtoTo = ItemDto.builder()
				.id(1)
				.name("n")
				.description("d")
				.available(true)
				.build();
		itemDtoFrom = service.updateItem(itemDtoTo, 1);
		assertEquals(1, itemDtoFrom.getId());
	}

	@Test
	void test_updateItem_item_not_exists() {
		itemDtoTo = ItemDto.builder()
				.id(1)
				.name("n")
				.description("d")
				.available(true)
				.build();
		Exception exception = assertThrows(ItemNotFoundException.class,
				() -> service.updateItem(itemDtoTo, 1));
		assertEquals("Item not updated. Item with id [1] not found.", exception.getMessage());
	}

	@Test
	void test_updateItem_user_is_not_owner() {
		when(itemRepository.findWithOwnerById(anyInt()))
				.thenReturn(Optional.of(createItemWithOwner(1, createUser(1))));
		itemDtoTo = ItemDto.builder()
				.id(1)
				.name("n")
				.description("d")
				.available(true)
				.build();
		Exception exception = assertThrows(UserNotOwnerException.class,
				() -> service.updateItem(itemDtoTo, 2));
		assertEquals("Item not updated. User isn't owner of item.", exception.getMessage());
	}

	@Test
		//согласовать тест
	void test_getAllItemsByUserId() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1)));

		when(itemRepository.findWithBookingsWithOwnerByOwnerId(anyInt(), any()))
				.thenReturn(
						List.of(
								createItemWithBookings(1, List.of(
										createCurrentBookingWithUser(1, createUser(2)),
										createCurrentBookingWithUser(2, createUser(3))
								)),
								createItemWithBookings(2, List.of(
										createCurrentBookingWithUser(3, createUser(2))
								))
						)
				);

		when(commentRepository.findWithItemWithCommenterAllByItemIdIn(any()))
				.thenReturn(
						List.of(
								createComment(1, createItem(1), createUser(1)),
								createComment(2, createItem(1), createUser(2))
						)
				);

		List<ItemBookingsCommentsDto> items = service.getAllItemsByUserId(1, pageable);
		assertEquals(1, items.get(0).getId());
		assertEquals(2, items.get(1).getId());
	}

	@Test
	void test_getAllItemsByUserId_user_not_found() {
		Exception exception = assertThrows(UserNotFoundException.class,
				() -> service.getAllItemsByUserId(1, pageable));
		assertEquals("Items not received. User with id [1] not exists.", exception.getMessage());
	}

	@Test
	void test_getItemById_with_no_comments_and_no_bookings() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1)));
		when(itemRepository.findWithOwnerById(anyInt()))
				.thenReturn(Optional.of(createItemWithOwner(1, createUser(1))));
		ItemBookingsCommentsDto item = service.getItemById(1, 1);
		assertEquals(1, item.getId());
	}

	@Test
	void test_getItemById_with_comments() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1)));
		when(itemRepository.findWithOwnerById(anyInt()))
				.thenReturn(Optional.of(createItemWithOwner(1, createUser(1))));
		when(commentRepository.findWithCommenterAllByItemId(anyInt()))
				.thenReturn(List.of(createComment(1, null, createUser(1))));
		ItemBookingsCommentsDto item = service.getItemById(1, 1);
		assertEquals(1, item.getId());
		List<CommentDto> comments = item.getComments();
		assertEquals(1, comments.get(0).getId());
	}

	@Test
	void test_getItemById_with_bookings() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1)));
		when(itemRepository.findWithOwnerById(anyInt()))
				.thenReturn(Optional.of(createItemWithOwner(1, createUser(1))));
		when(bookingRepository.findWithBookerAllByItemIdAndStatusIn(anyInt(), any()))
				.thenReturn(List.of(
						createLastBookingWithUser(1, createUser(3)),
						createNextBookingWithUser(2, createUser(4))
				));
		ItemBookingsCommentsDto item = service.getItemById(1, 1);
		assertEquals(1, item.getId());
		assertEquals(1, item.getLastBooking().getId());
		assertEquals(2, item.getNextBooking().getId());
	}

	@Test
	void test_getItemById_with_bookings_user_not_owner() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(2)));
		when(itemRepository.findWithOwnerById(anyInt()))
				.thenReturn(Optional.of(createItemWithOwner(1, createUser(1))));
		when(bookingRepository.findWithBookerAllByItemIdAndStatusIn(anyInt(), any()))
				.thenReturn(List.of(
						createLastBookingWithUser(1, createUser(3)),
						createNextBookingWithUser(2, createUser(4))
				));
		ItemBookingsCommentsDto item = service.getItemById(1, 2);
		assertEquals(1, item.getId());
		assertNull(item.getLastBooking());
		assertNull(item.getNextBooking());
	}

	@Test
	void test_getItemById_user_not_exists() {
		Exception exception = assertThrows(UserNotFoundException.class,
				() -> service.getItemById(1, 1));
		assertEquals("User not found. User with id [1] not exists.", exception.getMessage());
	}

	@Test
	void test_getItemById_item_not_exists() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1)));
		Exception exception = assertThrows(ItemNotFoundException.class,
				() -> service.getItemById(1, 1));
		assertEquals("Item not found. Item with id [1] not exists.", exception.getMessage());
	}

	@Test
	void test_deleteItemById() {
		when(itemRepository.findWithOwnerById(anyInt()))
				.thenReturn(Optional.of(createItemWithOwner(
						1,
						createUser(1)
				)));
		itemDtoFrom = service.deleteItemById(1, 1);
		assertEquals(1, itemDtoFrom.getId());
	}

	@Test
	void test_deleteItemById_item_not_exist() {
		Exception exception = assertThrows(ItemNotFoundException.class,
				() -> service.deleteItemById(1, 1));
		assertEquals("Item not deleted. Item with id [1] not found.", exception.getMessage());
	}

	@Test
	void test_deleteItemById_user_is_not_owner() {
		when(itemRepository.findWithOwnerById(anyInt()))
				.thenReturn(Optional.of(createItemWithOwner(
						1,
						createUser(2)
				)));
		Exception exception = assertThrows(UserNotOwnerException.class,
				() -> service.deleteItemById(1, 1));
		assertEquals("Item not deleted. User isn't owner of item.", exception.getMessage());
	}

	@Test
	void test_searchByText() {
		when(itemRepository.findItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIs(
				anyString(),
				anyString(),
				eq(true),
				any())
		).thenReturn(List.of(createItem(1), createItem(2)));
		List<ItemDto> items = service.searchByText("text", pageable);
		assertEquals(2, items.size());
	}

	@Test
	void test_searchByText_text_is_blank() {
		List<ItemDto> items = service.searchByText(" ", pageable);
		assertEquals(0, items.size());
	}

	@Test
	void test_addComment() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1)));
		when(itemRepository.findWithOwnerById(anyInt()))
				.thenReturn(Optional.of(createItemWithOwner(1, createUser(2))));
		when(bookingRepository.findAllByUserIdAndItemIdAndStatusAndEndBefore(
				anyInt(),
				anyInt(),
				any(),
				any())
		).thenReturn(List.of(createCurrentBookingWithUser(1, createUser(1))));
		when(commentRepository.save(any()))
				.thenReturn(createComment(1, null, null));

		CommentDto commentDtoTo = CommentDto.builder()
				.text("comment")
				.build();

		CommentDto comment = service.addComment(1, 1, commentDtoTo);
		assertEquals(1, comment.getId());
	}

	@Test
	void test_addComment_user_not_exists() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.empty());
		when(itemRepository.findWithOwnerById(anyInt()))
				.thenReturn(Optional.of(createItemWithOwner(1, createUser(2))));
		when(bookingRepository.findAllByUserIdAndItemIdAndStatusAndEndBefore(
				anyInt(),
				anyInt(),
				any(),
				any())
		).thenReturn(List.of(createCurrentBookingWithUser(1, createUser(1))));
		when(commentRepository.save(any()))
				.thenReturn(createComment(1, null, null));
		CommentDto commentDtoTo = CommentDto.builder()
				.text("comment")
				.build();
		Exception exception = assertThrows(UserNotFoundException.class,
				() -> service.addComment(1, 1, commentDtoTo));
		assertEquals("Comment not saved. User-commenter with id [1] not found.", exception.getMessage());
	}

	@Test
	void test_addComment_item_not_exists() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1)));
		when(itemRepository.findWithOwnerById(anyInt()))
				.thenReturn(Optional.empty());
		when(bookingRepository.findAllByUserIdAndItemIdAndStatusAndEndBefore(
				anyInt(),
				anyInt(),
				any(),
				any())
		).thenReturn(List.of(createCurrentBookingWithUser(1, createUser(1))));
		when(commentRepository.save(any()))
				.thenReturn(createComment(1, null, null));
		CommentDto commentDtoTo = CommentDto.builder()
				.text("comment")
				.build();
		Exception exception = assertThrows(ItemNotFoundException.class,
				() -> service.addComment(1, 1, commentDtoTo));
		assertEquals("Comment not saved. Item with id [1] not found.", exception.getMessage());
	}

	@Test
	void test_addComment_commenter_is_owner() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1)));
		when(itemRepository.findWithOwnerById(anyInt()))
				.thenReturn(Optional.of(createItemWithOwner(1, createUser(1))));
		when(bookingRepository.findAllByUserIdAndItemIdAndStatusAndEndBefore(
				anyInt(),
				anyInt(),
				any(),
				any())
		).thenReturn(List.of(createCurrentBookingWithUser(1, createUser(1))));
		when(commentRepository.save(any()))
				.thenReturn(createComment(1, null, null));
		CommentDto commentDtoTo = CommentDto.builder()
				.text("comment")
				.build();
		Exception exception = assertThrows(BadRequestException.class,
				() -> service.addComment(1, 1, commentDtoTo));
		assertEquals("Owner cannot comment his item.", exception.getMessage());
	}

	@Test
	void test_addComment_commenter_is_not_renter() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1)));
		when(itemRepository.findWithOwnerById(anyInt()))
				.thenReturn(Optional.of(createItemWithOwner(1, createUser(2))));
		when(bookingRepository.findAllByUserIdAndItemIdAndStatusAndEndBefore(
				anyInt(),
				anyInt(),
				any(),
				any())
		).thenReturn(List.of());
		when(commentRepository.save(any()))
				.thenReturn(createComment(1, null, null));
		CommentDto commentDtoTo = CommentDto.builder()
				.text("comment")
				.build();
		Exception exception = assertThrows(BadRequestException.class,
				() -> service.addComment(1, 1, commentDtoTo));
		assertEquals("Commenter not renter of item.", exception.getMessage());
	}

	private User createUser(Integer id) {
		User user = new User();
		user.setId(id);
		user.setName("name");
		user.setEmail("email");
		return user;
	}

	private ItemRequest createRequest(int id) {
		ItemRequest request = new ItemRequest();
		request.setId(id);
		request.setDescription("description");
		request.setCreated(LocalDateTime.now());
		return request;
	}

	private Item createItem(int id) {
		Item item = new Item();
		item.setId(id);
		item.setName("name");
		item.setDescription("description");
		item.setAvailable(true);
		return item;
	}

	private Item createItemWithOwner(int id, User owner) {
		Item item = new Item();
		item.setId(id);
		item.setName("name");
		item.setDescription("description");
		item.setAvailable(true);
		item.setOwner(owner);
		return item;
	}

	private Booking createCurrentBookingWithUser(Integer id, User user) {
		Booking booking = new Booking();
		booking.setId(id);
		booking.setUser(user);
		booking.setStart(LocalDateTime.now().minusHours(1));
		booking.setEnd(LocalDateTime.now().plusHours(1));
		return booking;
	}

	private Booking createLastBookingWithUser(Integer id, User user) {
		Booking booking = new Booking();
		booking.setId(id);
		booking.setUser(user);
		booking.setStart(LocalDateTime.now().minusHours(2));
		booking.setEnd(LocalDateTime.now().minusHours(1));
		return booking;
	}

	private Booking createNextBookingWithUser(Integer id, User user) {
		Booking booking = new Booking();
		booking.setId(id);
		booking.setUser(user);
		booking.setStart(LocalDateTime.now().plusHours(1));
		booking.setEnd(LocalDateTime.now().plusHours(2));
		return booking;
	}

	private Item createItemWithBookings(int id, List<Booking> bookings) {
		Item item = new Item();
		item.setId(id);
		item.setName("name");
		item.setDescription("description");
		item.setAvailable(true);
		item.setBookings(bookings);
		return item;
	}

	private Comment createComment(Integer id, Item item, User user) {
		Comment comment = new Comment();
		comment.setId(1);
		comment.setItem(item);
		comment.setUser(user);
		return comment;
	}
}