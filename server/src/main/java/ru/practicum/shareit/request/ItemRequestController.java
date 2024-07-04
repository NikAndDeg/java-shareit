package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.util.Pagenator;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";
	private final ItemRequestService requestService;

	@PostMapping
	public ItemRequestDto addRequest(@RequestBody ItemRequestDto requestDto,
									 @RequestHeader(USER_ID_HEADER) int userId) {
		log.info("Request to save request [{}] by user with id [{}].", requestDto, userId);
		ItemRequestDto savedRequest = requestService.addRequest(requestDto, userId);
		log.info("Request [{}] saved.", savedRequest);
		return savedRequest;
	}

	@GetMapping("/{requestId}")
	private ItemRequestDto getRequestById(@RequestHeader(USER_ID_HEADER) int userId,
										  @PathVariable int requestId) {
		log.info("Request by user with id [{}] to get request by id [{}] ", userId, requestId);
		ItemRequestDto request = requestService.getRequestById(userId, requestId);
		log.info("Request [{}] received.", request);
		return request;
	}

	@GetMapping
	public List<ItemRequestDto> getAllRequestsByOwner(@RequestHeader(USER_ID_HEADER) int userId) {
		log.info("Request to get request by owner with id [{}].", userId);
		List<ItemRequestDto> requests = requestService.getRequestsByOwner(userId);
		log.info("Owner's requests received.");
		return requests;
	}

	@GetMapping("/all")
	public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID_HEADER) int userId,
											   @RequestParam(defaultValue = "0") Integer from,
											   @RequestParam(defaultValue = "20") Integer size) {
		log.info("Request to get all requests by user with id [{}] from [{}] size [{}] ", userId, from, size);
		Pageable pageable = Pagenator.getPage(from, size, Sort.by(Sort.Order.desc("created")));
		List<ItemRequestDto> requests = requestService.getAllRequests(userId, pageable);
		log.info("All requests received.");
		return requests;
	}
}
