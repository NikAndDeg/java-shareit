package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.user.dto.UserRequestToSaveDto;
import ru.practicum.shareit.user.dto.UserRequestToUpdateDto;

import static ru.practicum.shareit.ResponseHandler.handleResponseSpec;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "${shareit-server.uri.path.user}")
public class UserController {
	private final WebClient client;
	@Value("${shareit-server.url}")
	private String shareItServerUrl;
	@Value("${shareit-server.uri.path.user}")
	private String userPath;

	@PostMapping
	public Mono<?> addUser(@RequestBody @Valid UserRequestToSaveDto userDto) {
		log.info("Request to save user [{}].", userDto);
		WebClient.RequestHeadersSpec<?> request = client.post()
				.uri(shareItServerUrl + userPath)
				.bodyValue(userDto)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
		return handleResponseSpec(request.retrieve());
	}

	@PatchMapping("/{userId}")
	public Mono<?> updateUser(@RequestBody @Valid UserRequestToUpdateDto userDto, @PathVariable int userId) {
		log.info("Request to update user [{}] with id [{}].", userDto, userId);
		WebClient.RequestHeadersSpec<?> request =
				client.patch()
						.uri(shareItServerUrl + userPath + "/" + userId)
						.bodyValue(userDto)
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
		return handleResponseSpec(request.retrieve());
	}

	@GetMapping
	public Mono<?> getAllUsers() {
		log.info("Request to get all users.");
		WebClient.RequestHeadersSpec<?> request = client.get()
				.uri(shareItServerUrl + userPath)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
		return handleResponseSpec(request.retrieve());
	}

	@GetMapping("/{userId}")
	public Mono<?> getUserById(@PathVariable int userId) {
		log.info("Request to get user by id [{}]", userId);
		WebClient.RequestHeadersSpec<?> request = client.get()
				.uri(shareItServerUrl + userPath + "/" + userId)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
		return handleResponseSpec(request.retrieve());
	}

	@DeleteMapping("/{userId}")
	public Mono<?> deleteUserById(@PathVariable int userId) {
		log.info("Request to delete user by id [{}]", userId);
		WebClient.RequestHeadersSpec<?> request = client.delete()
				.uri(shareItServerUrl + userPath + "/" + userId)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
		return handleResponseSpec(request.retrieve());
	}
}
