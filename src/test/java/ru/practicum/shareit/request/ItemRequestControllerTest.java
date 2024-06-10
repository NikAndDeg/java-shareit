package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
	private final static String HEADER_X_SHARER_USER_ID = "X-Sharer-User-Id";

	private ItemRequestDto requestDto;

	@MockBean
	ItemRequestService requestService;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@Test
	void test_addRequest() throws Exception {
		when(requestService.addRequest(any(), anyInt()))
				.thenReturn(ItemRequestDto.builder().id(1).build());
		requestDto = ItemRequestDto.builder()
				.description("description")
				.build();
		mvc.perform(
						post("/requests")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.ALL_VALUE)
								.header(HEADER_X_SHARER_USER_ID, 1)
								.content(mapper.writeValueAsString(requestDto))
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)));
	}

	@Test
	void test_addRequest_without_description() throws Exception {
		requestDto = ItemRequestDto.builder()
				.build();
		mvc.perform(
						post("/requests")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.ALL_VALUE)
								.header(HEADER_X_SHARER_USER_ID, 1)
								.content(mapper.writeValueAsString(requestDto))
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(MethodArgumentNotValidException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_addRequest_description_is_blank() throws Exception {
		requestDto = ItemRequestDto.builder()
				.description(" ")
				.build();
		mvc.perform(
						post("/requests")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.ALL_VALUE)
								.header(HEADER_X_SHARER_USER_ID, 1)
								.content(mapper.writeValueAsString(requestDto))
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(MethodArgumentNotValidException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_addRequest_without_HEADER_X_SHARER_USER_ID() throws Exception {
		requestDto = ItemRequestDto.builder()
				.description("description")
				.build();
		mvc.perform(
						post("/requests")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.ALL_VALUE)
								.content(mapper.writeValueAsString(requestDto))
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(MissingRequestHeaderException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_getRequestById() throws Exception {
		when(requestService.getRequestById(anyInt(), anyInt()))
				.thenReturn(ItemRequestDto.builder().id(1).build());
		mvc.perform(
						get("/requests/1")
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)));
	}

	@Test
	void test_getRequestById_without_HEADER_X_SHARER_USER_ID() throws Exception {
		mvc.perform(
						get("/requests/1")
								.accept(MediaType.ALL_VALUE)
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(MissingRequestHeaderException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_getAllRequestsByOwner() throws Exception {
		when(requestService.getRequestsByOwner(anyInt()))
				.thenReturn(List.of(ItemRequestDto.builder().id(1).build()));
		mvc.perform(
						get("/requests")
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	void test_getAllRequestsByOwner_without_HEADER_X_SHARER_USER_ID() throws Exception {
		mvc.perform(
						get("/requests")
								.accept(MediaType.ALL_VALUE)
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(MissingRequestHeaderException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_getAllRequests() throws Exception {
		when(requestService.getAllRequests(anyInt(), any()))
				.thenReturn(List.of(ItemRequestDto.builder().id(1).build()));
		mvc.perform(
						get("/requests/all")
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	void test_getAllRequests_without_HEADER_X_SHARER_USER_ID() throws Exception {
		mvc.perform(
						get("/requests/all")
								.accept(MediaType.ALL_VALUE)
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(MissingRequestHeaderException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_getAllRequests_negative_from() throws Exception {
		mvc.perform(
						get("/requests/all?from=-1&size=20")
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(BadRequestException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_getAllRequests_negative_size() throws Exception {
		mvc.perform(
						get("/requests/all?from=0&size=-1")
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(BadRequestException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_getAllRequests_from_is_zero() throws Exception {
		mvc.perform(
						get("/requests/all?from=0&size=0")
								.header(HEADER_X_SHARER_USER_ID, 1)
								.accept(MediaType.ALL_VALUE)
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(BadRequestException.class,
						result.getResolvedException().getClass()));
	}
}