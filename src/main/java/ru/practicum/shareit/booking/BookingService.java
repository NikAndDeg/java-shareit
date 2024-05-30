package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.dto.BookingDto;

import java.util.List;

public interface BookingService {

	BookingDto addBooking(BookingDto bookingDto, int bookerId);

	BookingDto approveBooking(int userId, int bookingId, String approved);

	BookingDto getBooking(int bookingId, int userId);

	List<BookingDto> getUserBookings(int userId, State state);

	List<BookingDto> getOwnerBookings(int ownerId, State state);
}
