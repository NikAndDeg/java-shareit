package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.mapper.DtoMapper;

@Component
public class ItemDtoMapper implements DtoMapper<ItemDto, Item> {
	@Override
	public ItemDto toDto(Item item, String... args) {
		return ItemDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getAvailable())
				.build();
	}

	@Override
	public Item toModel(ItemDto itemDto, String... args) {
		Item item = new Item();
		item.setId(itemDto.getId());
		item.setName(itemDto.getName());
		item.setDescription(itemDto.getDescription());
		item.setAvailable(itemDto.getAvailable());
		return item;
	}
}
