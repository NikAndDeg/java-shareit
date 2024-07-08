package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ItemRequestServiceImplTest {
	private final Pageable pageable = PageRequest.of(0, 20);
	private UserRepository userRepository;
	private ItemRequestRepository requestRepository;
	private ItemRequestServiceImpl service;
	private ItemRequestDto requestTo;
	private ItemRequestDto requestFrom;

	@BeforeEach
	void setUp() {
		userRepository = mock(UserRepository.class);
		requestRepository = mock(ItemRequestRepository.class);
		service = new ItemRequestServiceImpl(userRepository, requestRepository);
	}

	@Test
	void test_addRequest() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1, "name", "email")));
		when(requestRepository.save(any()))
				.thenReturn(createRequest(1));
		requestTo = ItemRequestDto.builder()
				.description("description")
				.build();
		requestFrom = service.addRequest(requestTo, 1);

		assertEquals(1, requestFrom.getId());
		assertEquals("description", requestFrom.getDescription());

		assertEquals(1, requestFrom.getRequester().getId());
		assertEquals("name", requestFrom.getRequester().getName());
		assertEquals("email", requestFrom.getRequester().getEmail());
	}

	@Test
	void test_addRequest_user_not_exists() {
		Exception exception = assertThrows(UserNotFoundException.class,
				() -> service.addRequest(requestTo, 1));
		assertEquals("Request not saved. User with id [1] not found.", exception.getMessage());
	}

	@Test
	void test_getRequestById() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1, "name", "email")));
		when(requestRepository.findWithRequesterAndItemsById(anyInt()))
				.thenReturn(
						Optional.of(
								createRequestWithRequesterAndItems(
										1,
										createUser(1, "name", "email"),
										Set.of(createItem(1), createItem(2))
								)
						)
				);

		requestFrom = service.getRequestById(1, 1);

		assertEquals(1, requestFrom.getId());
		assertEquals("description", requestFrom.getDescription());

		assertEquals(1, requestFrom.getRequester().getId());

		List<ItemDto> items = requestFrom.getItems();
		items.sort((item1, item2) -> Integer.compare(item1.getId(), item2.getId()));
		assertEquals(1, items.get(0).getId());
		assertEquals(2, items.get(1).getId());
	}

	@Test
	void test_getRequestById_user_not_exists() {
		Exception exception = assertThrows(UserNotFoundException.class,
				() -> service.getRequestById(1, 1));
		assertEquals("Request not received. User with id [1] not found.", exception.getMessage());
	}

	@Test
	void test_getRequestById_request_not_exists() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1, "name", "email")));
		Exception exception = assertThrows(DataNotFoundException.class,
				() -> service.getRequestById(1, 1));
		assertEquals("Request not received. Request with id [1] not found.", exception.getMessage());
	}

	@Test
	void test_getRequestsByOwner() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1, "name", "email")));
		when(requestRepository.findWithItemsAllByRequesterId(anyInt()))
				.thenReturn(
						List.of(
								createRequestWithItems(1, Set.of(createItem(1))),
								createRequestWithItems(2, Set.of(createItem(2), createItem(3)))
						)
				);

		List<ItemRequestDto> requestDtos = service.getRequestsByOwner(1);

		UserDto requester = requestDtos.get(0).getRequester();
		assertEquals(1, requester.getId());
		requester = requestDtos.get(1).getRequester();
		assertEquals(1, requester.getId());

		List<ItemDto> items = requestDtos.get(0).getItems();
		assertEquals(1, items.get(0).getId());
		items = requestDtos.get(1).getItems();
		items.sort((item1, item2) -> Integer.compare(item1.getId(), item2.getId()));
		assertEquals(2, items.get(0).getId());
		assertEquals(3, items.get(1).getId());
	}

	@Test
	void test_getRequestsByOwner_user_not_exists() {
		Exception exception = assertThrows(UserNotFoundException.class,
				() -> service.getRequestsByOwner(1));
		assertEquals("User with id [1] not found.", exception.getMessage());
	}

	@Test
	void test_getAllRequests() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1, "name", "email")));
		when(requestRepository.findWithRequesterAndItemsAllByRequesterIdNot(anyInt(), any()))
				.thenReturn(
						List.of(
								createRequestWithRequesterAndItems(
										1,
										createUser(1, "name", "email"),
										Set.of(createItem(1), createItem(2))
								),
								createRequestWithRequesterAndItems(
										2,
										createUser(2, "name", "email"),
										Set.of(createItem(3))
								)
						)
				);
		List<ItemRequestDto> requests = service.getAllRequests(1, pageable);
		ItemRequestDto request = requests.get(0);
		assertEquals(1, request.getId());

		List<ItemDto> items = request.getItems();
		items.sort((item1, item2) -> Integer.compare(item1.getId(), item2.getId()));
		assertEquals(1, items.get(0).getId());
		assertEquals(2, items.get(1).getId());

		UserDto requester = request.getRequester();
		assertEquals(1, requester.getId());

		request = requests.get(1);

		items = request.getItems();
		assertEquals(3, items.get(0).getId());

		requester = request.getRequester();
		assertEquals(2, requester.getId());
	}

	@Test
	void test_getAllRequests_user_not_found() {
		Exception exception = assertThrows(UserNotFoundException.class,
				() -> service.getAllRequests(1, pageable));
		assertEquals("Request not received. User with id [1] not found.", exception.getMessage());
	}

	private User createUser(Integer id, String name, String email) {
		User user = new User();
		user.setId(id);
		user.setName(name);
		user.setEmail(email);
		return user;
	}

	private ItemRequest createRequest(int id) {
		ItemRequest request = new ItemRequest();
		request.setId(id);
		request.setDescription("description");
		request.setCreated(LocalDateTime.now());
		return request;
	}

	private ItemRequest createRequestWithItems(int id, Set<Item> items) {
		ItemRequest request = new ItemRequest();
		request.setId(id);
		request.setDescription("description");
		request.setCreated(LocalDateTime.now());
		request.setItems(items);
		return request;
	}

	private ItemRequest createRequestWithRequesterAndItems(int id, User user, Set<Item> items) {
		ItemRequest request = new ItemRequest();
		request.setId(id);
		request.setDescription("description");
		request.setCreated(LocalDateTime.now());
		request.setRequester(user);
		request.setItems(items);
		return request;
	}

	private Item createItem(int id) {
		Item item = new Item();
		item.setId(id);
		item.setName("name");
		item.setDescription("description");
		item.setAvailable(true);
		return item;
	}
}