package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

	ItemRequestDto addRequest(ItemRequestDto requestDto, int requesterId);

	ItemRequestDto getRequestById(int userId, int requestId);

	List<ItemRequestDto> getRequestsByOwner(int requesterId);

	List<ItemRequestDto> getAllRequests(int userId, Pageable pageable);
}
