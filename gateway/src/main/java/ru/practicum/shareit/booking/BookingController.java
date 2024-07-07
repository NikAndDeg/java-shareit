package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.booking.dto.BookingRequestToSaveDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.exception.BookingUnsupportedStatus;

import static ru.practicum.shareit.ResponseHandler.handleResponseSpec;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("${shareit-server.uri.path.booking}")
@Validated
public class BookingController {
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";
	private final WebClient client;
	@Value("${shareit-server.url}")
	private String shareItServerUrl;
	@Value("${shareit-server.uri.path.booking}")
	private String bookingPath;

	@PostMapping
	public Mono<?> addBooking(@RequestBody @Valid BookingRequestToSaveDto bookingDto,
							  @RequestHeader(USER_ID_HEADER) int userId) {
		log.info("Request to add booking [{}] with userId [{}]", bookingDto, userId);
		WebClient.RequestHeadersSpec<?> request = client.post()
				.uri(shareItServerUrl + bookingPath)
				.bodyValue(bookingDto)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.header(USER_ID_HEADER, Integer.toString(userId));
		return handleResponseSpec(request.retrieve());
	}

	@PatchMapping("/{bookingId}")
	public Mono<?> approveBooking(@RequestHeader(USER_ID_HEADER) int userId,
								  @PathVariable int bookingId,
								  @RequestParam boolean approved) {
		log.info("Request to approve booking with userId [{}], bookingId [{}], approved [{}]",
				userId,
				bookingId,
				approved);
		WebClient.RequestHeadersSpec<?> request = client.patch()
				.uri(shareItServerUrl + bookingPath + "/" + bookingId + "?approved=" + approved)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.header(USER_ID_HEADER, Integer.toString(userId));
		return handleResponseSpec(request.retrieve());
	}

	@GetMapping("/{bookingId}")
	public Mono<?> getBooking(@RequestHeader(USER_ID_HEADER) int userId,
							  @PathVariable int bookingId) {
		log.info("Request to get booking by id [{}] with userId [{}]", bookingId, userId);
		WebClient.RequestHeadersSpec<?> request = client.get()
				.uri(shareItServerUrl + bookingPath + "/" + bookingId)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.header(USER_ID_HEADER, Integer.toString(userId));
		return handleResponseSpec(request.retrieve());
	}

	@GetMapping
	public Mono<?> getBookings(@RequestHeader(USER_ID_HEADER) int userId,
							   @RequestParam(required = false, defaultValue = "ALL") String state,
							   @RequestParam(defaultValue = "0") @Min(0) int from,
							   @RequestParam(defaultValue = "20") @Min(1) int size) {
		log.info("Request to get all user's bookings with userId [{}] and state [{}]", userId, state);
		WebClient.RequestHeadersSpec<?> request = client.get()
				.uri(shareItServerUrl + bookingPath + "?state=" + getState(state) + "&from=" + from + "&size=" + size)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.header(USER_ID_HEADER, Integer.toString(userId));
		return handleResponseSpec(request.retrieve());
	}

	@GetMapping("/owner")
	public Mono<?> getOwnerBookings(@RequestHeader(USER_ID_HEADER) int ownerId,
									@RequestParam(required = false, defaultValue = "ALL") String state,
									@RequestParam(defaultValue = "0") @Min(0) int from,
									@RequestParam(defaultValue = "20") @Min(1) int size) {
		log.info("Request to get all owner's bookings with ownerId [{}] and state [{}]", ownerId, state);
		WebClient.RequestHeadersSpec<?> request = client.get()
				.uri(shareItServerUrl + bookingPath + "/owner?state=" + getState(state) + "&from=" + from + "&size=" + size)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.header(USER_ID_HEADER, Integer.toString(ownerId));
		return handleResponseSpec(request.retrieve());
	}

	private State getState(String state) {
		for (State s : State.values()) {
			if (state.equalsIgnoreCase(s.toString()))
				return s;
		}
		throw new BookingUnsupportedStatus(state);
	}
}
