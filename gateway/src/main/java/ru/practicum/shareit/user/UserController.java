package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.user.dto.UserRequestToSaveDto;
import ru.practicum.shareit.user.dto.UserRequestToUpdateDto;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequestMapping(path = "${shareit-server.uri.path.user}")
public class UserController {
	private final WebClient client = WebClient.create();
	@Value("${shareit-server.url}")
	private String shareItServerUrl;
	@Value("${shareit-server.uri.path.user}")
	private String userPath;

	@PostMapping
	public Mono<?> addUser(@RequestBody @Valid UserRequestToSaveDto userDto) {
		log.info("Request to save user [{}].", userDto);
		return client.post()
				.uri(shareItServerUrl + userPath)
				.bodyValue(userDto)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.retrieve()
				.onStatus(CONFLICT::equals,
						response -> Mono.error(new ResponseStatusException(CONFLICT)))
				.bodyToMono(String.class);
	}

	@PatchMapping("/{userId}")
	public Mono<?> updateUser(@RequestBody @Valid UserRequestToUpdateDto userDto, @PathVariable int userId) {
		log.info("Request to update user [{}] with id [{}].", userDto, userId);
		return client.patch()
				.uri(shareItServerUrl + userPath + "/" + userId)
				.bodyValue(userDto)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.retrieve()
				.onStatus(CONFLICT::equals,
						response -> Mono.error(new ResponseStatusException(CONFLICT)))
				.bodyToMono(String.class);
	}

	@GetMapping
	public Mono<?> getAllUsers() {
		log.info("Request to get all users.");
		return client.get()
				.uri(shareItServerUrl + userPath)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.retrieve()
				.bodyToMono(String.class);
	}

	@GetMapping("/{userId}")
	public Mono<?> getUserById(@PathVariable int userId) {
		log.info("Request to get user by id [{}]", userId);
		return client.get()
				.uri(shareItServerUrl + userPath + "/" + userId)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.retrieve()
				.onStatus(NOT_FOUND::equals,
						response -> Mono.error(new ResponseStatusException(NOT_FOUND)))
				.bodyToMono(String.class);
	}

	@DeleteMapping("/{userId}")
	public Mono<?> deleteUserById(@PathVariable int userId) {
		log.info("Request to delete user by id [{}]", userId);
		return client.delete()
				.uri(shareItServerUrl + userPath + "/" + userId)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.retrieve()
				.bodyToMono(String.class);
	}
}
