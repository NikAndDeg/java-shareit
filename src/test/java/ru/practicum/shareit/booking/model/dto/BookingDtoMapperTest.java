package ru.practicum.shareit.booking.model.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingDtoMapperTest {
	private final int bookingId = 1;
	private final LocalDateTime start = LocalDateTime.parse("2024-01-01T10:10:10");
	private final LocalDateTime end = LocalDateTime.parse("2025-01-01T10:10:10");
	private final BookingStatus status = BookingStatus.WAITING;

	private final int itemId = 10;
	private final String itemName = "item";
	private final String itemDescription = "description";
	private final boolean itemAvailable = true;

	private final int userId = 2;
	private final String userName = "user";
	private final String userEmail = "user@email.com";

	private Booking booking;
	private Item item;
	private User user;

	@BeforeEach
	void createBookingItemUser() {
		booking = new Booking();
		booking.setId(bookingId);
		booking.setStart(start);
		booking.setEnd(end);
		booking.setStatus(status);

		item = new Item();
		item.setId(itemId);
		item.setName(itemName);
		item.setDescription(itemDescription);
		item.setAvailable(itemAvailable);

		user = new User();
		user.setId(userId);
		user.setName(userName);
		user.setEmail(userEmail);
	}

	@Test
	void toDtoTest() {
		BookingDto dto = BookingDto.toDto(booking, item, user);

		assertEquals(bookingId, dto.getId());
		assertEquals(start, dto.getStart());
		assertEquals(end, dto.getEnd());
		assertEquals(status, dto.getStatus());

		assertEquals(userId, dto.getBooker().getId());
		assertEquals(userName, dto.getBooker().getName());
		assertEquals(userEmail, dto.getBooker().getEmail());

		assertEquals(itemId, dto.getItem().getId());
		assertEquals(itemName, dto.getItem().getName());
		assertEquals(itemDescription, dto.getItem().getDescription());
		assertEquals(itemAvailable, dto.getItem().getAvailable());
	}

	@Test
	void toModelTest() {
		BookingDto dto = BookingDto.builder()
				.start(start)
				.end(end)
				.status(status)
				.build();

		Booking actualBooking = BookingDto.toModel(dto);

		assertEquals(start, actualBooking.getStart());
		assertEquals(end, actualBooking.getEnd());
		assertEquals(status, actualBooking.getStatus());
	}
}