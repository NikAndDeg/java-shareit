package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.DataForTests.*;

@DataJpaTest
@Sql("/test_schema.sql")
class BookingRepositoryTest {
	@Autowired
	private BookingRepository repository;

	@Test
	void get_booking_with_user_and_item_and_item_owner_by_booking_id_1() {
		Booking booking = repository.findWithRequesterAndItemAndOwnerOfItemById(1).get();
		Item item = booking.getItem();
		assertEquals(savedItems.get(0), item);
		User itemOwner = item.getOwner();
		assertEquals(savedUsers.get(3), itemOwner);
		User requester = booking.getUser();
		assertEquals(savedUsers.get(0), requester);
	}

	@Test
	void get_all_bookings_by_user_id_1() {
		List<Booking> bookings = repository.findAllByUserId(1);
		assertEquals(savedBookings.get(0), bookings.get(0));
		assertEquals(savedBookings.get(2), bookings.get(1));
	}

	@Test
	void get_all_by_owner_id() {
		List<Booking> bookings = repository.findAllByOwnerId(2);
		bookings.forEach(System.out::println);
	}
}