package ru.practicum.shareit.request.model.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemRequestMapperDtoTest {
	private Integer id = 1;
	private String description = "description";
	private LocalDateTime created = LocalDateTime.parse("2024-01-01T10:10:10");
	private Set<Item> items = Set.of(
			createItem(1, "item", "d", true),
			createItem(2, "item2", "d", true)
	);
	private User requester = createUser(1, "Anthony John Soprano", "tony@email.com");

	private Item createItem(int id, String name, String description, boolean available) {
		Item item = new Item();
		item.setId(id);
		item.setName(name);
		item.setDescription(description);
		item.setAvailable(available);
		return item;
	}

	private ItemRequest model;
	private ItemRequestDto dto;

	@Test
	void test_toDto_with_items() {
		dto = ItemRequestDto.toDto(createRequest(id, description, created),
				requester,
				items
		);

		assertEquals(id, dto.getId());
		assertEquals(description, dto.getDescription());
		assertEquals(created, dto.getCreated());

		dto.getItems().sort((i1, i2) -> Integer.compare(i1.getId(), i2.getId()));

		assertEquals(1, dto.getItems().get(0).getId());
		assertEquals(id, dto.getItems().get(0).getRequestId());

		assertEquals(2, dto.getItems().get(1).getId());
		assertEquals(id, dto.getItems().get(1).getRequestId());

		assertEquals(requester.getId(), dto.getRequester().getId());
		assertEquals(requester.getName(), dto.getRequester().getName());
		assertEquals(requester.getEmail(), dto.getRequester().getEmail());
	}

	@Test
	void test_toDto_without_items() {
		dto = ItemRequestDto.toDto(createRequest(id, description, created), requester);

		assertEquals(id, dto.getId());
		assertEquals(description, dto.getDescription());
		assertEquals(created, dto.getCreated());

		assertNull(dto.getItems());

		assertEquals(requester.getId(), dto.getRequester().getId());
		assertEquals(requester.getName(), dto.getRequester().getName());
		assertEquals(requester.getEmail(), dto.getRequester().getEmail());
	}

	@Test
	void test_toModel() {
		model = ItemRequestDto.toModel(
				ItemRequestDto.builder()
						.id(id)
						.description(description)
						.created(created)
						.build(),
				requester
		);

		assertEquals(id, model.getId());
		assertEquals(description, model.getDescription());
		assertEquals(created, model.getCreated());

		assertEquals(requester.getId(), model.getRequester().getId());
		assertEquals(requester.getName(), model.getRequester().getName());
		assertEquals(requester.getEmail(), model.getRequester().getEmail());
	}

	private User createUser(Integer id, String name, String email) {
		User user = new User();
		user.setId(id);
		user.setName(name);
		user.setEmail(email);
		return user;
	}

	private ItemRequest createRequest(int id, String description, LocalDateTime created) {
		ItemRequest request = new ItemRequest();
		request.setId(id);
		request.setDescription(description);
		request.setCreated(created);
		return request;
	}
}