package ru.practicum.shareit;

import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.*;

@UtilityClass
public class ResponseHandler {
	public static Mono<?> handleResponseSpec(WebClient.ResponseSpec responseSpec) {
		return responseSpec
				.onStatus(FORBIDDEN::equals,
						response -> Mono.error(new ResponseStatusException(FORBIDDEN)))
				.onStatus(NOT_FOUND::equals,
						response -> Mono.error(new ResponseStatusException(NOT_FOUND)))
				.onStatus(BAD_REQUEST::equals,
						response -> Mono.error(new ResponseStatusException(BAD_REQUEST)))
				.onStatus(CONFLICT::equals,
						response -> Mono.error(new ResponseStatusException(CONFLICT)))
				.bodyToMono(String.class);
	}
}
