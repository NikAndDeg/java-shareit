package ru.practicum.shareit.request.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
public class ItemRequestDto {
	private Integer id;
	@Size(max = 200, message = "size must be between 0 and 200")
	@NotBlank
	private String description;
	private LocalDateTime created;
	private List<ItemDto> items;
	private UserDto requester;

	public static ItemRequestDto toDto(ItemRequest request, User requester, Set<Item> items) {
		ItemRequestDto requestDto = ItemRequestDto.builder()
				.id(request.getId())
				.description(request.getDescription())
				.created(request.getCreated())
				.build();

		List<ItemDto> itemDtos = items.stream()
				.map(item -> ItemDto.toDto(item, request.getId()))
				.collect(Collectors.toList());
		requestDto.setItems(itemDtos);

		UserDto requesterDto = UserDto.toDto(requester);
		requestDto.setRequester(requesterDto);
		return requestDto;
	}

	public static ItemRequestDto toDto(ItemRequest request, User requester) {
		ItemRequestDto requestDto = ItemRequestDto.builder()
				.id(request.getId())
				.description(request.getDescription())
				.created(request.getCreated())
				.build();
		UserDto requesterDto = UserDto.toDto(requester);
		requestDto.setRequester(requesterDto);
		return requestDto;
	}

	public static ItemRequest toModel(ItemRequestDto dto, User requester) {
		ItemRequest request = new ItemRequest();
		request.setId(dto.getId());
		request.setDescription(dto.getDescription());
		request.setCreated(dto.getCreated());
		request.setRequester(requester);
		return request;
	}
}
