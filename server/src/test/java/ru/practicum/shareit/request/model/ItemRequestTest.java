package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemRequestTest {
	private ItemRequest request1;
	private ItemRequest request2;

	@Test
	void request_equals_test() {
		LocalDateTime ldt = LocalDateTime.now();
		request1 = createRequest(1, ldt);
		request2 = createRequest(1, ldt);
		assertTrue(request1.equals(request2));
	}

	private ItemRequest createRequest(int id, LocalDateTime ldt) {
		ItemRequest request = new ItemRequest();
		request.setId(id);
		request.setDescription("description");
		request.setCreated(ldt);
		return request;
	}
}