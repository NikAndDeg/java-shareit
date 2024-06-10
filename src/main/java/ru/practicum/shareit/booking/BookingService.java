package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.dto.BookingDto;

import java.util.List;

public interface BookingService {

	BookingDto addBooking(BookingDto bookingDto, int bookerId);

	BookingDto approveBooking(int userId, int bookingId, boolean approved);

	BookingDto getBooking(int bookingId, int userId);

	List<BookingDto> getUserBookings(int userId, State state, Pageable pageable);

	List<BookingDto> getOwnerBookings(int ownerId, State state, Pageable pageable);
}
