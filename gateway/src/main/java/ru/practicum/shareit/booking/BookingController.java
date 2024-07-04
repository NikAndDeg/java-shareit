package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.booking.dto.BookingRequestToSaveDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.exception.BookingUnsupportedStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping("${shareit-server.uri.path.booking}")
@Validated
public class BookingController {
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";
	private final WebClient client = WebClient.create();
	@Value("${shareit-server.url}")
	private String shareItServerUrl;
	@Value("${shareit-server.uri.path.booking}")
	private String bookingPath;

	@PostMapping
	public Mono<?> addBooking(@RequestBody @Valid BookingRequestToSaveDto bookingDto,
							  @RequestHeader(USER_ID_HEADER) int userId) {
		log.info("Request to add booking [{}] with userId [{}]", bookingDto, userId);
		return client.post()
				.uri(shareItServerUrl + bookingPath)
				.bodyValue(bookingDto)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.header(USER_ID_HEADER, Integer.toString(userId))
				.retrieve()
				.onStatus(FORBIDDEN::equals,
						response -> Mono.error(new ResponseStatusException(FORBIDDEN)))
				.onStatus(NOT_FOUND::equals,
						response -> Mono.error(new ResponseStatusException(NOT_FOUND)))
				.onStatus(BAD_REQUEST::equals,
						response -> Mono.error(new ResponseStatusException(BAD_REQUEST)))
				.bodyToMono(String.class);
	}

	@PatchMapping("/{bookingId}")
	public Mono<?> approveBooking(@RequestHeader(USER_ID_HEADER) int userId,
								  @PathVariable int bookingId,
								  @RequestParam boolean approved) {
		log.info("Request to approve booking with userId [{}], bookingId [{}], approved [{}]",
				userId,
				bookingId,
				approved);
		return client.patch()
				.uri(shareItServerUrl + bookingPath + "/" + bookingId + "?approved=" + approved)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.header(USER_ID_HEADER, Integer.toString(userId))
				.retrieve()
				.onStatus(FORBIDDEN::equals,
						response -> Mono.error(new ResponseStatusException(FORBIDDEN)))
				.onStatus(NOT_FOUND::equals,
						response -> Mono.error(new ResponseStatusException(NOT_FOUND)))
				.onStatus(BAD_REQUEST::equals,
						response -> Mono.error(new ResponseStatusException(BAD_REQUEST)))
				.bodyToMono(String.class);
	}

	@GetMapping("/{bookingId}")
	public Mono<?> getBooking(@RequestHeader(USER_ID_HEADER) int userId,
							  @PathVariable int bookingId) {
		log.info("Request to get booking by id [{}] with userId [{}]", bookingId, userId);
		return client.get()
				.uri(shareItServerUrl + bookingPath + "/" + bookingId)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.header(USER_ID_HEADER, Integer.toString(userId))
				.retrieve()
				.onStatus(FORBIDDEN::equals,
						response -> Mono.error(new ResponseStatusException(FORBIDDEN)))
				.onStatus(NOT_FOUND::equals,
						response -> Mono.error(new ResponseStatusException(NOT_FOUND)))
				.onStatus(BAD_REQUEST::equals,
						response -> Mono.error(new ResponseStatusException(BAD_REQUEST)))
				.bodyToMono(String.class);
	}

	@GetMapping
	public Mono<?> getBookings(@RequestHeader(USER_ID_HEADER) int userId,
							   @RequestParam(required = false, defaultValue = "ALL") String state,
							   @RequestParam(defaultValue = "0") @Min(0) int from,
							   @RequestParam(defaultValue = "20") @Min(1) int size) {
		log.info("Request to get all user's bookings with userId [{}] and state [{}]", userId, state);
		return client.get()
				.uri(shareItServerUrl + bookingPath + "?state=" + getState(state) + "&from=" + from + "&size=" + size)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.header(USER_ID_HEADER, Integer.toString(userId))
				.retrieve()
				.onStatus(FORBIDDEN::equals,
						response -> Mono.error(new ResponseStatusException(FORBIDDEN)))
				.onStatus(NOT_FOUND::equals,
						response -> Mono.error(new ResponseStatusException(NOT_FOUND)))
				.onStatus(BAD_REQUEST::equals,
						response -> Mono.error(new ResponseStatusException(BAD_REQUEST)))
				.bodyToMono(String.class);
	}

	@GetMapping("/owner")
	public Mono<?> getOwnerBookings(@RequestHeader(USER_ID_HEADER) int ownerId,
									@RequestParam(required = false, defaultValue = "ALL") String state,
									@RequestParam(defaultValue = "0") @Min(0) int from,
									@RequestParam(defaultValue = "20") @Min(1) int size) {
		log.info("Request to get all owner's bookings with ownerId [{}] and state [{}]", ownerId, state);
		return client.get()
				.uri(shareItServerUrl + bookingPath + "/owner?state=" + getState(state) + "&from=" + from + "&size=" + size)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.header(USER_ID_HEADER, Integer.toString(ownerId))
				.retrieve()
				.onStatus(FORBIDDEN::equals,
						response -> Mono.error(new ResponseStatusException(FORBIDDEN)))
				.onStatus(NOT_FOUND::equals,
						response -> Mono.error(new ResponseStatusException(NOT_FOUND)))
				.onStatus(BAD_REQUEST::equals,
						response -> Mono.error(new ResponseStatusException(BAD_REQUEST)))
				.bodyToMono(String.class);
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> handleBookingUnsupportedStatus(final BookingUnsupportedStatus exception) {
		log.warn(exception.getMessage());
		return Map.of("error", "Unknown state: " + exception.getMessage());
	}

	private State getState(String state) {
		for (State s : State.values()) {
			if (state.equalsIgnoreCase(s.toString()))
				return s;
		}
		throw new BookingUnsupportedStatus(state);
	}
}
