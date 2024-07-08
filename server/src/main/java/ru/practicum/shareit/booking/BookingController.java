package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.exception.booking.BookingUnsupportedStatus;
import ru.practicum.shareit.util.Pagenator;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";
	private final BookingService service;

	@PostMapping
	public BookingDto addBooking(@RequestBody BookingDto bookingDto,
								 @RequestHeader(USER_ID_HEADER) int userId) {
		log.info("Request to add booking [{}] with userId [{}]", bookingDto, userId);
		BookingDto savedBooking = service.addBooking(bookingDto, userId);
		log.info("Booking [{}] saved.", savedBooking);
		return savedBooking;
	}

	@PatchMapping("/{bookingId}")
	public BookingDto approveBooking(@RequestHeader(USER_ID_HEADER) int userId,
									 @PathVariable int bookingId,
									 @RequestParam boolean approved) {
		log.info("Request to approve booking with userId [{}], bookingId [{}], approved [{}]",
				userId,
				bookingId,
				approved);
		BookingDto approvedBooking = service.approveBooking(userId, bookingId, approved);
		log.info("Booking [{}] approved.", approvedBooking);
		return approvedBooking;
	}

	@GetMapping("/{bookingId}")
	public BookingDto getBooking(@RequestHeader(USER_ID_HEADER) int userId,
								 @PathVariable int bookingId) {
		log.info("Request to get booking by id [{}] with userId [{}]", bookingId, userId);
		BookingDto booking = service.getBooking(bookingId, userId);
		log.info("Booking [{}] found.", booking);
		return booking;
	}

	@GetMapping
	public List<BookingDto> getBookings(@RequestHeader(USER_ID_HEADER) int userId,
										@RequestParam(required = false, defaultValue = "ALL") String state,
										@RequestParam(defaultValue = "0") int from,
										@RequestParam(defaultValue = "20") int size) {
		log.info("Request to get all user's bookings with userId [{}] and state [{}]", userId, state);
		Pageable pageable = Pagenator.getPage(from, size, Sort.by(Sort.Order.desc("start")));
		List<BookingDto> bookings = service.getUserBookings(userId, getState(state), pageable);
		log.info("All user's bookings received.");
		return bookings;
	}

	@GetMapping("/owner")
	public List<BookingDto> getOwnerBookings(@RequestHeader(USER_ID_HEADER) int ownerId,
											 @RequestParam(required = false, defaultValue = "ALL") String state,
											 @RequestParam(defaultValue = "0") int from,
											 @RequestParam(defaultValue = "20") int size) {
		log.info("Request to get all owner's bookings with ownerId [{}] and state [{}]", ownerId, state);
		Pageable pageable = Pagenator.getPage(from, size, Sort.by(Sort.Order.desc("start")));
		List<BookingDto> bookings = service.getOwnerBookings(ownerId, getState(state), pageable);
		log.info("All owner's bookings received.");
		return bookings;
	}

	private State getState(String state) {
		for (State s : State.values()) {
			if (state.equalsIgnoreCase(s.toString()))
				return s;
		}
		throw new BookingUnsupportedStatus(state);
	}
}
