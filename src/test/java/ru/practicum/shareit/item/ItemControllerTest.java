package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemBookingsCommentsDto;
import ru.practicum.shareit.item.model.dto.ItemDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
	private static final String HEADER_X_SHARER_USER_ID = "X-Sharer-User-Id";

	private ItemDto itemDto = ItemDto.builder()
			.id(1)
			.name("item")
			.description("description")
			.available(true)
			.build();

	private ItemDto toAdd;

	@MockBean
	private ItemService itemService;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@Test
	void test_addItem() throws Exception {
		when(itemService.addItem(any(), anyInt()))
				.thenReturn(itemDto);
		toAdd = ItemDto.builder()
				.name("item")
				.description("description")
				.available(true)
				.build();
		mvc.perform(
						post("/items")
								.contentType(MediaType.APPLICATION_JSON)
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
								.content(mapper.writeValueAsString(toAdd))
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(itemDto.getId())));
	}

	@Test
	void test_addItem_without_HEADER_X_SHARER_USER_ID() throws Exception {
		toAdd = ItemDto.builder()
				.name("item")
				.description("description")
				.available(true)
				.build();
		mvc.perform(
						post("/items")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.ALL_VALUE)
								.content(mapper.writeValueAsString(toAdd))
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(MissingRequestHeaderException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_addItem_item_without_available() throws Exception {
		toAdd = ItemDto.builder()
				.name("item")
				.description("description")
				.build();
		mvc.perform(
						post("/items")
								.contentType(MediaType.APPLICATION_JSON)
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
								.content(mapper.writeValueAsString(toAdd))
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(MethodArgumentNotValidException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_addItem_item_without_name() throws Exception {
		toAdd = ItemDto.builder()
				.description("description")
				.available(true)
				.build();
		mvc.perform(
						post("/items")
								.contentType(MediaType.APPLICATION_JSON)
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
								.content(mapper.writeValueAsString(toAdd))
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(MethodArgumentNotValidException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_addItem_item_blank_name() throws Exception {
		toAdd = ItemDto.builder()
				.name("   ")
				.description("description")
				.available(true)
				.build();
		mvc.perform(
						post("/items")
								.contentType(MediaType.APPLICATION_JSON)
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
								.content(mapper.writeValueAsString(toAdd))
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(MethodArgumentNotValidException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_addItem_item_without_description() throws Exception {
		toAdd = ItemDto.builder()
				.description("description")
				.available(true)
				.build();
		mvc.perform(
						post("/items")
								.contentType(MediaType.APPLICATION_JSON)
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
								.content(mapper.writeValueAsString(toAdd))
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(MethodArgumentNotValidException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_addItem_item_blank_description() throws Exception {
		toAdd = ItemDto.builder()
				.name("item")
				.description("  ")
				.available(true)
				.build();
		mvc.perform(
						post("/items")
								.contentType(MediaType.APPLICATION_JSON)
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
								.content(mapper.writeValueAsString(toAdd))
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(MethodArgumentNotValidException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_updateItem() throws Exception {
		toAdd = ItemDto.builder()
				.available(false)
				.build();
		when(itemService.updateItem(any(), anyInt()))
				.thenReturn(itemDto);
		mvc.perform(
						patch("/items/1")
								.contentType(MediaType.APPLICATION_JSON)
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
								.content(mapper.writeValueAsString(toAdd))
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(itemDto.getId())));
	}

	@Test
	void test_updateItem_without_item() throws Exception {
		mvc.perform(
						patch("/items/1")
								.contentType(MediaType.APPLICATION_JSON)
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
				).andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(HttpMessageNotReadableException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_updateItem_without_HEADER_X_SHARER_USER_ID() throws Exception {
		toAdd = ItemDto.builder()
				.available(false)
				.build();
		mvc.perform(
						patch("/items/1")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.ALL_VALUE)
								.content(mapper.writeValueAsString(toAdd))
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(MissingRequestHeaderException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_getAllItems() throws Exception {
		when(itemService.getAllItemsByUserId(anyInt(), any()))
				.thenReturn(List.of(
						ItemBookingsCommentsDto.builder()
								.id(1)
								.build()
				));
		mvc.perform(
						get("/items?from=0&size=20")
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(itemDto.getId())));
	}

	@Test
	void test_getAllItems_negative_from() throws Exception {
		mvc.perform(
						get("/items?from=-10&size=20")
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(BadRequestException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_getAllItems_negative_size() throws Exception {
		mvc.perform(
						get("/items?from=0&size=0")
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(BadRequestException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_getAllItems_size_is_zero() throws Exception {
		mvc.perform(
						get("/items?from=0&size=0")
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(BadRequestException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_getItemById() throws Exception {
		when(itemService.getItemById(anyInt(), anyInt()))
				.thenReturn(ItemBookingsCommentsDto.builder().id(1).build());
		mvc.perform(
						get("/items/1")
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(itemDto.getId())));
	}

	@Test
	void test_getItemById_without_HEADER_X_SHARER_USER_ID() throws Exception {
		when(itemService.getItemById(anyInt(), anyInt()))
				.thenReturn(ItemBookingsCommentsDto.builder().id(1).build());
		mvc.perform(
						get("/items/1")
								.accept(MediaType.ALL_VALUE)
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(MissingRequestHeaderException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_deleteItemById() throws Exception {
		when(itemService.deleteItemById(anyInt(), anyInt()))
				.thenReturn(itemDto);
		mvc.perform(
						delete("/items/1")
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(itemDto.getId())));
	}

	@Test
	void test_deleteItemById_without_HEADER_X_SHARER_USER_ID() throws Exception {
		when(itemService.getItemById(anyInt(), anyInt()))
				.thenReturn(ItemBookingsCommentsDto.builder().id(1).build());
		mvc.perform(
						delete("/items/1")
								.accept(MediaType.ALL_VALUE)
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(MissingRequestHeaderException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_searchByText() throws Exception {
		when(itemService.searchByText(eq("qwerty"), any()))
				.thenReturn(List.of(itemDto));
		mvc.perform(
						get("/items/search?text=qwerty")
								.accept(MediaType.ALL_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(itemDto.getId())));
	}

	@Test
	void test_addComment() throws Exception {
		when(itemService.addComment(anyInt(), anyInt(), any()))
				.thenReturn(CommentDto.builder()
						.id(1)
						.build());
		CommentDto commentToAdd = CommentDto.builder()
				.text("comment")
				.build();
		mvc.perform(
						post("/items/1/comment")
								.contentType(MediaType.APPLICATION_JSON)
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
								.content(mapper.writeValueAsString(commentToAdd))
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)));
	}

	@Test
	void test_addComment_comment_without_text() throws Exception {
		when(itemService.addComment(anyInt(), anyInt(), any()))
				.thenReturn(CommentDto.builder()
						.id(1)
						.build());
		CommentDto commentToAdd = CommentDto.builder()
				.build();
		mvc.perform(
						post("/items/1/comment")
								.contentType(MediaType.APPLICATION_JSON)
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
								.content(mapper.writeValueAsString(commentToAdd))
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(MethodArgumentNotValidException.class,
						result.getResolvedException().getClass()));
	}
}