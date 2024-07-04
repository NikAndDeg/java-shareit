package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.item.dto.CommentRequestToSaveDto;
import ru.practicum.shareit.item.dto.ItemRequestToSaveDto;
import ru.practicum.shareit.item.dto.ItemRequestToUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping("${shareit-server.uri.path.item}")
@Validated
public class ItemController {
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";
	private final WebClient client = WebClient.create();
	@Value("${shareit-server.url}")
	private String shareItServerUrl;
	@Value("${shareit-server.uri.path.item}")
	private String itemPath;

	@PostMapping
	public Mono<?> addItem(@RequestBody @Valid ItemRequestToSaveDto itemDto,
						   @RequestHeader(USER_ID_HEADER) int userId) {
		log.info("Request to save item [{}] with userId [{}]", itemDto, userId);
		return client.post()
				.uri(shareItServerUrl + itemPath)
				.bodyValue(itemDto)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.header(USER_ID_HEADER, Integer.toString(userId))
				.retrieve()
				.onStatus(NOT_FOUND::equals,
						response -> Mono.error(new ResponseStatusException(NOT_FOUND)))
				.bodyToMono(String.class);
	}

	@PatchMapping("/{itemId}")
	public Mono<?> updateItem(@RequestBody ItemRequestToUpdateDto itemDto,
							  @RequestHeader(USER_ID_HEADER) int userId,
							  @PathVariable int itemId) {
		log.info("Request to update item [{}] with userId [{}].", itemDto, userId);
		return client.patch()
				.uri(shareItServerUrl + itemPath + "/" + itemId)
				.bodyValue(itemDto)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.header(USER_ID_HEADER, Integer.toString(userId))
				.retrieve()
				.onStatus(NOT_FOUND::equals,
						response -> Mono.error(new ResponseStatusException(NOT_FOUND)))
				.bodyToMono(String.class);
	}

	@GetMapping
	public Mono<?> getAllItems(@RequestHeader(USER_ID_HEADER) int userId,
							   @RequestParam(defaultValue = "0") @Min(0) int from,
							   @RequestParam(defaultValue = "20") @Min(1) int size) {
		log.info("Request to get all items with userId [{}].", userId);
		return client.get()
				.uri(shareItServerUrl + itemPath + "?from=" + from + "&size=" + size)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.header(USER_ID_HEADER, Integer.toString(userId))
				.retrieve()
				.bodyToMono(String.class);
	}

	@GetMapping("/{itemId}")
	public Mono<?> getItemById(@RequestHeader(USER_ID_HEADER) int userId,
							   @PathVariable int itemId) {
		log.info("Request to get item by id [{}].", itemId);
		return client.get()
				.uri(shareItServerUrl + itemPath + "/" + itemId)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.header(USER_ID_HEADER, Integer.toString(userId))
				.retrieve()
				.onStatus(NOT_FOUND::equals,
						response -> Mono.error(new ResponseStatusException(NOT_FOUND)))
				.bodyToMono(String.class);
	}

	@DeleteMapping("/{itemId}")
	public Mono<?> deleteItemById(@PathVariable int itemId,
								  @RequestHeader(USER_ID_HEADER) int userId) {
		log.info("Request to delete item by id [{}], with userId [{}].", itemId, userId);
		return client.delete()
				.uri(shareItServerUrl + itemPath + "/" + itemId)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.header(USER_ID_HEADER, Integer.toString(userId))
				.retrieve()
				.onStatus(NOT_FOUND::equals,
						response -> Mono.error(new ResponseStatusException(NOT_FOUND)))
				.bodyToMono(String.class);
	}

	@GetMapping("/search")
	public Mono<?> searchByText(@RequestParam String text,
								@RequestParam(defaultValue = "0") @Min(0) int from,
								@RequestParam(defaultValue = "20") @Min(1) int size) {
		log.info("Request to search item by text [{}].", text);
		return client.get()
				.uri(shareItServerUrl + itemPath + "/search?text=" + text + "&from=" + from + "&size=" + size)
				.header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
				.retrieve()
				.bodyToMono(String.class);
	}

	@PostMapping("/{itemId}/comment")
	public Mono<?> addComment(@PathVariable int itemId,
							  @RequestHeader(USER_ID_HEADER) int userId,
							  @RequestBody @Valid CommentRequestToSaveDto commentDto) {
		log.info("Request to add comment [{}] with itemId [{}] and userId [{}]",
				commentDto, itemId, userId);
		return client.post()
				.uri(shareItServerUrl + itemPath + "/" + itemId + "/comment")
				.bodyValue(commentDto)
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
}
