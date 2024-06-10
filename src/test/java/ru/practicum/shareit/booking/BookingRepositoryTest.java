package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Sql("/test_booking_repository_schema.sql")
class BookingRepositoryTest {
	@Autowired
	private BookingRepository repository;

	private Pageable pageable = PageRequest.of(0, 20);

	private List<Booking> bookings;

	private Booking booking;

	@Test
	void test_findAllByOwnerId() {
		bookings = repository.findAllByOwnerId(1, pageable);

		booking = bookings.get(0);
		assertEquals(1, booking.getId());

		booking = bookings.get(1);
		assertEquals(2, booking.getId());

		booking = bookings.get(2);
		assertEquals(3, booking.getId());
	}

	@Test
	void test_findAllByOwnerIdAndStartBeforeAndEndAfter() {
		bookings = repository.findAllByOwnerIdAndStartBeforeAndEndAfter(1, getTimeNow(), getTimeNow(), pageable);

		booking = bookings.get(0);
		assertEquals(2, booking.getId());
		assertTrue(booking.getStart().isBefore(getTimeNow()));
		assertTrue(booking.getEnd().isAfter(getTimeNow()));
	}

	@Test
	void test_findAllByOwnerIdAndEndBefore() {
		bookings = repository.findAllByOwnerIdAndEndBefore(1, getTimeNow(), pageable);

		booking = bookings.get(0);
		assertEquals(1, booking.getId());
		assertTrue(booking.getEnd().isBefore(getTimeNow()));
	}

	@Test
	void test_findAllByOwnerIdAndStartAfter() {
		bookings = repository.findAllByOwnerIdAndStartAfter(1, getTimeNow(), pageable);

		booking = bookings.get(0);
		assertEquals(3, booking.getId());
		assertTrue(booking.getStart().isAfter(getTimeNow()));
	}

	@Test
	void test_findAllByOwnerIdAndStatusIs_WAITING() {
		bookings = repository.findAllByOwnerIdAndStatusIs(1, BookingStatus.WAITING, pageable);

		booking = bookings.get(0);
		assertEquals(1, booking.getId());
		assertEquals(BookingStatus.WAITING, booking.getStatus());
	}

	@Test
	void test_findAllByOwnerIdAndStatusIs_APPROVED() {
		bookings = repository.findAllByOwnerIdAndStatusIs(1, BookingStatus.APPROVED, pageable);

		booking = bookings.get(0);
		assertEquals(2, booking.getId());
		assertEquals(BookingStatus.APPROVED, booking.getStatus());
	}

	@Test
	void test_findAllByOwnerIdAndStatusIs_REJECTED() {
		bookings = repository.findAllByOwnerIdAndStatusIs(1, BookingStatus.REJECTED, pageable);

		booking = bookings.get(0);
		assertEquals(3, booking.getId());
		assertEquals(BookingStatus.REJECTED, booking.getStatus());
	}

	@Test
	void test_findWithBookerAllByItemIdAndStatusIn() {
		bookings = repository.findWithBookerAllByItemIdAndStatusIn(1,
				List.of(BookingStatus.WAITING, BookingStatus.APPROVED));

		booking = bookings.get(0);
		assertEquals(1, booking.getId());
		assertEquals(BookingStatus.WAITING, booking.getStatus());

		booking = bookings.get(1);
		assertEquals(2, booking.getId());
		assertEquals(BookingStatus.APPROVED, booking.getStatus());
	}

	private LocalDateTime getTimeNow() {
		return LocalDateTime.now();
	}
}