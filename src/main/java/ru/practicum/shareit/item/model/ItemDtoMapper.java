package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.DtoMapper;

@Component
public class ItemDtoMapper implements DtoMapper<ItemDto, Item> {
	@Override
	public ItemDto toDto(Item item) {
		return ItemDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getAvailable())
				.build();
	}

	@Override
	public Item toModel(ItemDto itemDto, String... args) {
		return Item.builder()
				.name(itemDto.getName())
				.description(itemDto.getDescription())
				.available(itemDto.getAvailable())
				.ownerId(Integer.parseInt(args[0]))
				.build();
	}
}
