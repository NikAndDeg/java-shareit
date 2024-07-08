package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.booking.BookingNotFoundException;
import ru.practicum.shareit.exception.item.ItemNotFoundException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.exception.user.UserNotOwnerException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {
	private final Pageable pageable = PageRequest.of(0, 20);
	private BookingRepository bookingRepository;
	private UserRepository userRepository;
	private ItemRepository itemRepository;
	private BookingService bookingService;

	@BeforeEach
	void setUpService() {
		bookingRepository = mock(BookingRepository.class);
		userRepository = mock(UserRepository.class);
		itemRepository = mock(ItemRepository.class);
		bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
	}

	private void setUp_addBooking() {
		when(bookingRepository.save(any()))
				.thenReturn(createBooking(1, BookingStatus.WAITING));

		when(userRepository.findById(1))
				.thenReturn(Optional.of(createUser(1, "user", "user@email.com")));
		when(userRepository.findById(2))
				.thenReturn(Optional.of(createUser(2, "user2", "user2@email.com")));

		Item itemWithOwner = createItemWithOwner(1, "item", "d", true,
				createUser(2, "user2", "user2@email.com"));
		when(itemRepository.findWithOwnerById(1))
				.thenReturn(Optional.of(itemWithOwner));
		when(itemRepository.findWithOwnerById(2))
				.thenReturn(Optional.empty());

		Item itemWithOwnerNotAvailable = createItemWithOwner(3, "item", "d", false,
				createUser(2, "user2", "user2@email.com"));
		when(itemRepository.findWithOwnerById(3))
				.thenReturn(Optional.of(itemWithOwnerNotAvailable));
	}

	@Test
	void test_addBooking() {
		setUp_addBooking();

		BookingDto dtoToAdd = BookingDto.builder()
				.itemId(1)
				.start(LocalDateTime.parse("2025-01-01T10:10:10"))
				.end(LocalDateTime.parse("2026-01-01T10:10:10"))
				.build();

		BookingDto dto = bookingService.addBooking(dtoToAdd, 1);

		UserDto expectedBooker = UserDto.builder()
				.id(1)
				.name("user")
				.email("user@email.com")
				.build();
		ItemDto expectedItem = ItemDto.builder()
				.id(1)
				.name("item")
				.description("d")
				.available(true)
				.build();

		assertEquals(expectedBooker, dto.getBooker());
		assertEquals(expectedItem, dto.getItem());
	}

	@Test
	void test_addBooking_start_before_now() {
		setUp_addBooking();

		BookingDto dtoToAdd = BookingDto.builder()
				.itemId(1)
				.start(LocalDateTime.parse("2020-01-01T10:10:10"))
				.end(LocalDateTime.parse("2026-01-01T10:10:10")).build();
		Exception exception = assertThrows(BadRequestException.class,
				() -> bookingService.addBooking(dtoToAdd, 1));
		assertEquals("Wrong booking start-end time!", exception.getMessage());
	}

	@Test
	void test_addBooking_end_after_now() {
		setUp_addBooking();

		BookingDto dtoToAdd = BookingDto.builder()
				.itemId(1)
				.start(LocalDateTime.parse("2025-01-01T10:10:10"))
				.end(LocalDateTime.parse("2020-01-01T10:10:10")).build();
		Exception exception = assertThrows(BadRequestException.class,
				() -> bookingService.addBooking(dtoToAdd, 1));
		assertEquals("Wrong booking start-end time!", exception.getMessage());
	}

	@Test
	void test_addBooking_start_before_end() {
		setUp_addBooking();

		BookingDto dtoToAdd = BookingDto.builder()
				.itemId(1)
				.start(LocalDateTime.parse("2025-01-01T10:10:10"))
				.end(LocalDateTime.parse("2020-01-01T10:10:10")).build();
		Exception exception = assertThrows(BadRequestException.class,
				() -> bookingService.addBooking(dtoToAdd, 1));
		assertEquals("Wrong booking start-end time!", exception.getMessage());
	}

	@Test
	void test_addBooking_non_existing_user() {
		setUp_addBooking();

		BookingDto dtoToAdd = BookingDto.builder()
				.itemId(1)
				.start(LocalDateTime.parse("2025-01-01T10:10:10"))
				.end(LocalDateTime.parse("2026-01-01T10:10:10"))
				.build();
		Exception exception = assertThrows(UserNotFoundException.class,
				() -> bookingService.addBooking(dtoToAdd, 99));
		assertEquals("Requester with id [99] not exists.", exception.getMessage());
	}

	@Test
	void test_addBooking_non_existing_item() {
		setUp_addBooking();
		BookingDto dtoToAdd = BookingDto.builder()
				.itemId(2)
				.start(LocalDateTime.parse("2025-01-01T10:10:10"))
				.end(LocalDateTime.parse("2026-01-01T10:10:10"))
				.build();
		Exception exception = assertThrows(ItemNotFoundException.class,
				() -> bookingService.addBooking(dtoToAdd, 1));
		assertEquals("Item for booking with id [2] not exists.", exception.getMessage());
	}

	@Test
	void test_addBooking_booker_is_owner() {
		setUp_addBooking();
		BookingDto dtoToAdd = BookingDto.builder()
				.itemId(1)
				.start(LocalDateTime.parse("2025-01-01T10:10:10"))
				.end(LocalDateTime.parse("2026-01-01T10:10:10"))
				.build();
		Exception exception = assertThrows(UserNotFoundException.class,
				() -> bookingService.addBooking(dtoToAdd, 2));
		assertEquals("Owner of item cannot be a booker.", exception.getMessage());
	}

	@Test
	void test_addBooking_item_not_available() {
		setUp_addBooking();
		BookingDto dtoToAdd = BookingDto.builder()
				.itemId(3)
				.start(LocalDateTime.parse("2025-01-01T10:10:10"))
				.end(LocalDateTime.parse("2026-01-01T10:10:10"))
				.build();
		Exception exception = assertThrows(BadRequestException.class,
				() -> bookingService.addBooking(dtoToAdd, 1));
		assertEquals("Item not available.", exception.getMessage());
	}

	private void setUpFor_approveBooking() {
		Booking booking = createBooking(1, BookingStatus.WAITING);
		booking.setUser(createUser(1, " ", " "));
		Item itemWithOwner = createItemWithOwner(1, " ", " ", true,
				createUser(2, " ", " "));
		booking.setItem(itemWithOwner);

		Booking apprevedBooking = createBooking(3, BookingStatus.APPROVED);
		apprevedBooking.setUser(createUser(1, " ", " "));
		apprevedBooking.setItem(itemWithOwner);

		when(bookingRepository.findWithRequesterAndItemAndOwnerOfItemById(1))
				.thenReturn(Optional.of(booking));
		when(bookingRepository.findWithRequesterAndItemAndOwnerOfItemById(2))
				.thenReturn(Optional.empty());
		when(bookingRepository.findWithRequesterAndItemAndOwnerOfItemById(3))
				.thenReturn(Optional.of(apprevedBooking));

		when(bookingRepository.save(createBooking(1, BookingStatus.APPROVED)))
				.thenReturn(createBooking(1, BookingStatus.APPROVED));
		when(bookingRepository.save(createBooking(1, BookingStatus.REJECTED)))
				.thenReturn(createBooking(1, BookingStatus.REJECTED));
	}

	@Test
	void test_approveBooking_status_false() {
		setUpFor_approveBooking();
		BookingDto dto = bookingService.approveBooking(2, 1, false);
		assertEquals(1, dto.getId());
		assertEquals(BookingStatus.REJECTED, dto.getStatus());
		assertEquals(1, dto.getBooker().getId());
		assertEquals(1, dto.getItem().getId());
	}

	@Test
	void test_approveBooking_status_true() {
		setUpFor_approveBooking();
		BookingDto dto = bookingService.approveBooking(2, 1, true);
		assertEquals(1, dto.getId());
		assertEquals(BookingStatus.APPROVED, dto.getStatus());
		assertEquals(1, dto.getBooker().getId());
		assertEquals(1, dto.getItem().getId());
	}

	@Test
	void test_approveBooking_booking_not_found() {
		setUpFor_approveBooking();
		Exception exception = assertThrows(BookingNotFoundException.class,
				() -> bookingService.approveBooking(1, 2, true));
		assertEquals("Booking with id [2] not exists.", exception.getMessage());
	}

	@Test
	void test_approveBooking_approved_by_non_owner() {
		setUpFor_approveBooking();
		Exception exception = assertThrows(UserNotOwnerException.class,
				() -> bookingService.approveBooking(1, 1, true));
		assertEquals("User with id [1] not owner of Item.", exception.getMessage());
	}

	@Test
	void test_approveBooking_already_approved_booking() {
		setUpFor_approveBooking();
		Exception exception = assertThrows(BadRequestException.class,
				() -> bookingService.approveBooking(2, 3, true));
		assertEquals("Owner cannot change status after approval.", exception.getMessage());
	}

	private void setUp_getBooking() {
		Booking booking = createBooking(1, BookingStatus.WAITING);
		booking.setUser(createUser(1, " ", " "));
		Item itemWithOwner = createItemWithOwner(1, " ", " ", true,
				createUser(2, " ", " "));
		booking.setItem(itemWithOwner);
		when(bookingRepository.findWithRequesterAndItemAndOwnerOfItemById(1))
				.thenReturn(Optional.of(booking));
	}

	@Test
	void test_getBooking_by_requester() {
		setUp_getBooking();
		BookingDto dto = bookingService.getBooking(1, 1);
		assertEquals(1, dto.getId());
		assertEquals(1, dto.getBooker().getId());
		assertEquals(1, dto.getItem().getId());
	}

	@Test
	void test_getBooking_by_owner_of_item() {
		setUp_getBooking();
		BookingDto dto = bookingService.getBooking(1, 2);
		assertEquals(1, dto.getId());
		assertEquals(1, dto.getBooker().getId());
		assertEquals(1, dto.getItem().getId());
	}

	@Test
	void test_getBooking_booking_not_found() {
		setUp_getBooking();
		Exception exception = assertThrows(BookingNotFoundException.class,
				() -> bookingService.getBooking(2, 2));
		assertEquals("Booking with id [2] not exists.", exception.getMessage());
	}

	@Test
	void test_getBooking_by_non_requester_or_owner_of_item() {
		setUp_getBooking();
		Exception exception = assertThrows(UserNotFoundException.class,
				() -> bookingService.getBooking(1, 999));
		assertEquals("User with id [999] isn't owner or requester.", exception.getMessage());
	}

	private void setUp_test_getUserBookings() {
		when(userRepository.findById(any()))
				.thenReturn(Optional.of(createUser(1, " ", " ")));
	}

	@Test
	void test_getBooking_user_not_found() {
		setUp_getBooking();
		Exception exception = assertThrows(UserNotFoundException.class,
				() -> bookingService.getUserBookings(999, State.CURRENT, pageable));
		assertEquals("User with id [999] not exists.", exception.getMessage());
	}

	@Test
	void test_getUserBookings_state_CURRENT() {
		setUp_test_getUserBookings();
		bookingService.getUserBookings(1, State.CURRENT, pageable);
		verify(userRepository).findById(1);
		verify(bookingRepository).findAllByUserIdAndStartBeforeAndEndAfter(any(), any(), any(), any());
	}

	@Test
	void test_getUserBookings_state_PAST() {
		setUp_test_getUserBookings();
		bookingService.getUserBookings(1, State.PAST, pageable);
		verify(userRepository).findById(1);
		verify(bookingRepository).findWithItemAndUserAllByUserIdAndEndBefore(any(), any(), any());
	}

	@Test
	void test_getUserBookings_state_FUTURE() {
		setUp_test_getUserBookings();
		bookingService.getUserBookings(1, State.FUTURE, pageable);
		verify(userRepository).findById(1);
		verify(bookingRepository).findAllByUserIdAndStartAfter(any(), any(), any());
	}

	@Test
	void test_getUserBookings_state_WAITING() {
		setUp_test_getUserBookings();
		bookingService.getUserBookings(1, State.WAITING, pageable);
		verify(userRepository).findById(1);
		verify(bookingRepository).findAllByUserIdAndStatusIs(any(), eq(BookingStatus.WAITING), any());
	}

	@Test
	void test_getUserBookings_state_REJECTED() {
		setUp_test_getUserBookings();
		bookingService.getUserBookings(1, State.REJECTED, pageable);
		verify(userRepository).findById(1);
		verify(bookingRepository).findAllByUserIdAndStatusIs(any(), eq(BookingStatus.REJECTED), any());
	}

	@Test
	void test_getUserBookings_state_All() {
		setUp_test_getUserBookings();
		bookingService.getUserBookings(1, State.ALL, pageable);
		verify(userRepository).findById(1);
		verify(bookingRepository).findAllByUserId(any(), any());
	}

	private void setUp_getOwnerBookings() {
		when(userRepository.findById(any()))
				.thenReturn(Optional.of(createUser(1, " ", " ")));
	}

	@Test
	void test_getOwnerBookings_user_not_found() {
		setUp_getBooking();
		Exception exception = assertThrows(UserNotFoundException.class,
				() -> bookingService.getOwnerBookings(999, State.CURRENT, pageable));
		assertEquals("User with id [999] not exists.", exception.getMessage());
	}

	@Test
	void test_getOwnerBookings_state_CURRENT() {
		setUp_getOwnerBookings();
		bookingService.getOwnerBookings(1, State.CURRENT, pageable);
		verify(userRepository).findById(1);
		verify(bookingRepository).findAllByOwnerIdAndStartBeforeAndEndAfter(any(), any(), any(), any());
	}

	@Test
	void test_getOwnerBookings_state_PAST() {
		setUp_getOwnerBookings();
		bookingService.getOwnerBookings(1, State.PAST, pageable);
		verify(userRepository).findById(1);
		verify(bookingRepository).findAllByOwnerIdAndEndBefore(any(), any(), any());
	}

	@Test
	void test_getOwnerBookings_state_FUTURE() {
		setUp_getOwnerBookings();
		bookingService.getOwnerBookings(1, State.FUTURE, pageable);
		verify(userRepository).findById(1);
		verify(bookingRepository).findAllByOwnerIdAndStartAfter(any(), any(), any());
	}

	@Test
	void test_getOwnerBookings_state_WAITING() {
		setUp_getOwnerBookings();
		bookingService.getOwnerBookings(1, State.WAITING, pageable);
		verify(userRepository).findById(1);
		verify(bookingRepository).findAllByOwnerIdAndStatusIs(any(), eq(BookingStatus.WAITING), any());
	}

	@Test
	void test_getOwnerBookings_state_REJECTED() {
		setUp_getOwnerBookings();
		bookingService.getOwnerBookings(1, State.REJECTED, pageable);
		verify(userRepository).findById(1);
		verify(bookingRepository).findAllByOwnerIdAndStatusIs(any(), eq(BookingStatus.REJECTED), any());
	}

	@Test
	void test_getOwnerBookings_state_ALL() {
		setUp_getOwnerBookings();
		bookingService.getOwnerBookings(1, State.ALL, pageable);
		verify(userRepository).findById(1);
		verify(bookingRepository).findAllByOwnerId(any(), any());
	}

	private Booking createBooking(int id, BookingStatus status) {
		Booking booking = new Booking();
		booking.setId(id);
		booking.setStatus(status);
		return booking;
	}

	private User createUser(int id, String name, String email) {
		User user = new User();
		user.setId(id);
		user.setName(name);
		user.setEmail(email);
		return user;
	}

	private Item createItemWithOwner(int id, String name, String description, boolean available, User owner) {
		Item item = new Item();
		item.setId(id);
		item.setName(name);
		item.setDescription(description);
		item.setAvailable(available);
		item.setOwner(owner);
		return item;
	}
}