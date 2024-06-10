package ru.practicum.shareit.item.model.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemDtoMapperTest {
	private Integer id = 1;
	private String name = "item";
	private String description = "description";
	private Boolean available = true;
	private Integer requestId = 2;

	private ItemDto dto;
	private Item model;

	@Test
	void test_toDto() {
		dto = ItemDto.toDto(createItem(id, name, description, available));
		assertEquals(id, dto.getId());
		assertEquals(name, dto.getName());
		assertEquals(description, dto.getDescription());
		assertEquals(available, dto.getAvailable());
	}

	@Test
	void test_toDto_with_requester() {
		dto = ItemDto.toDto(createItem(id, name, description, available), requestId);
		assertEquals(id, dto.getId());
		assertEquals(name, dto.getName());
		assertEquals(description, dto.getDescription());
		assertEquals(available, dto.getAvailable());
		assertEquals(requestId, dto.getRequestId());
	}

	@Test
	void test_toModel() {
		model = ItemDto.toModel(
				ItemDto.builder()
						.id(id)
						.name(name)
						.description(description)
						.available(available)
						.build()
		);
		assertEquals(id, model.getId());
		assertEquals(name, model.getName());
		assertEquals(description, model.getDescription());
		assertEquals(available, model.getAvailable());
	}

	private Item createItem(int id, String name, String description, boolean available) {
		Item item = new Item();
		item.setId(id);
		item.setName(name);
		item.setDescription(description);
		item.setAvailable(available);
		return item;
	}
}