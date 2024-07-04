package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.request.dto.ItemRequestRequestToSaveDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping(path = "${shareit-server.uri.path.request}")
@Validated
public class ItemRequestController {
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";
	private final WebClient client = WebClient.create();
	@Value("${shareit-server.url}")
	private String shareItServerUrl;
	@Value("${shareit-server.uri.path.request}")
	private String requestPath;

	@PostMapping
	public Mono<?> addRequest(@RequestBody @Valid ItemRequestRequestToSaveDto requestDto,
							  @RequestHeader(USER_ID_HEADER) int userId) {
		log.info("Request to save request [{}] by user with id [{}].", requestDto, userId);
		return client.post()
				.uri(shareItServerUrl + requestPath)
				.bodyValue(requestDto)
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

	@GetMapping("/{requestId}")
	public Mono<?> getRequestById(@RequestHeader(USER_ID_HEADER) int userId,
								  @PathVariable int requestId) {
		log.info("Request by user with id [{}] to get request by id [{}] ", userId, requestId);
		return client.get()
				.uri(shareItServerUrl + requestPath + "/" + requestId)
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
	public Mono<?> getAllRequestsByOwner(@RequestHeader(USER_ID_HEADER) int userId) {
		log.info("Request to get request by owner with id [{}].", userId);
		return client.get()
				.uri(shareItServerUrl + requestPath)
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

	@GetMapping("/all")
	public Mono<?> getAllRequests(@RequestHeader(USER_ID_HEADER) int userId,
								  @RequestParam(defaultValue = "0") @Min(0) int from,
								  @RequestParam(defaultValue = "20") @Min(1) int size) {
		log.info("Request to get all requests by user with id [{}] from [{}] size [{}] ", userId, from, size);
		return client.get()
				.uri(shareItServerUrl + requestPath + "/all?from=" + from + "&size=" + size)
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
}
