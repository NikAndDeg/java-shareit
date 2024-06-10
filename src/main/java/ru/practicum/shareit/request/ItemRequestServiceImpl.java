package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
	private final UserRepository userRepository;
	private final ItemRequestRepository requestRepository;

	@Override
	@Transactional
	public ItemRequestDto addRequest(ItemRequestDto requestDto, int requesterId) {
		User requester = userRepository.findById(requesterId).orElseThrow(
				() -> new UserNotFoundException("Request not saved. User with id [" + requesterId + "] not found.")
		);
		ItemRequest requestToSave = ItemRequestDto.toModel(requestDto, requester);
		setTimeNowToNewRequest(requestToSave);
		ItemRequest savedRequest = requestRepository.save(requestToSave);
		return ItemRequestDto.toDto(savedRequest, requester);
	}

	@Override
	public ItemRequestDto getRequestById(int userId, int requestId) {
		userRepository.findById(userId).orElseThrow(
				() -> new UserNotFoundException("Request not received. User with id [" + userId + "] not found.")
		);
		ItemRequest request = requestRepository.findWithRequesterAndItemsById(requestId).orElseThrow(
				() -> new DataNotFoundException("Request not received. Request with id [" + requestId + "] not found.")
		);
		return ItemRequestDto.toDto(request, request.getRequester(), request.getItems());
	}

	@Override
	public List<ItemRequestDto> getRequestsByOwner(int requesterId) {
		User requester = userRepository.findById(requesterId).orElseThrow(
				() -> new UserNotFoundException("User with id [" + requesterId + "] not found.")
		);
		List<ItemRequest> requests = requestRepository.findWithItemsAllByRequesterId(requesterId);
		return requests.stream()
				.map(itemRequest -> ItemRequestDto.toDto(itemRequest, requester, itemRequest.getItems()))
				.collect(Collectors.toList());
	}

	@Override
	public List<ItemRequestDto> getAllRequests(int userId, Pageable pageable) {
		userRepository.findById(userId).orElseThrow(
				() -> new UserNotFoundException("Request not received. User with id [" + userId + "] not found.")
		);
		List<ItemRequest> requests = requestRepository.findWithRequesterAndItemsAllByRequesterIdNot(userId, pageable);
		return requests.stream()
				.map(request -> ItemRequestDto.toDto(request, request.getRequester(), request.getItems()))
				.collect(Collectors.toList());
	}

	private void setTimeNowToNewRequest(ItemRequest request) {
		request.setCreated(LocalDateTime.now());
	}
}
