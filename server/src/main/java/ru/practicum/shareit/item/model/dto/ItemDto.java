package ru.practicum.shareit.item.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDto {
	private static final int ITEM_MAX_NAME_SIZE = 200;
	private static final int ITEM_MAX_DESCRIPTION_SIZE = 200;

	private Integer id;

	@Size(max = ITEM_MAX_NAME_SIZE, message = "size must be between 0 and " + ITEM_MAX_NAME_SIZE)
	@NotBlank
	private String name;

	@Size(max = ITEM_MAX_DESCRIPTION_SIZE, message = "size must be between 0 and " + ITEM_MAX_DESCRIPTION_SIZE)
	@NotBlank
	private String description;

	@NotNull
	private Boolean available;

	private Integer requestId;

	public static ItemDto toDto(Item item) {
		return ItemDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getAvailable())
				.build();
	}

	public static ItemDto toDto(Item item, int requestId) {
		return ItemDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getAvailable())
				.requestId(requestId)
				.build();
	}

	public static Item toModel(ItemDto itemDto) {
		Item item = new Item();
		item.setId(itemDto.getId());
		item.setName(itemDto.getName());
		item.setDescription(itemDto.getDescription());
		item.setAvailable(itemDto.getAvailable());
		return item;
	}
}
