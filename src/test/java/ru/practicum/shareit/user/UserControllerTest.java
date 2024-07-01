package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
	private static final String HEADER_X_SHARER_USER_ID = "X-Sharer-User-Id";

	private UserDto userDto;

	@MockBean
	private UserService userService;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@Test
	void test_addUser() throws Exception {
		when(userService.addUser(any()))
				.thenReturn(UserDto.builder().id(1).build());

		userDto = UserDto.builder()
				.name("user")
				.email("user@email.com")
				.build();

		mvc.perform(
						post("/users")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.ALL)
								.content(mapper.writeValueAsString(userDto))
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)));
	}

	@Test
	void test_addUser_without_user() throws Exception {
		mvc.perform(
						post("/users")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.ALL)
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(HttpMessageNotReadableException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_updateUser() throws Exception {
		when(userService.updateUser(any(), anyInt()))
				.thenReturn(UserDto.builder().id(1).build());

		userDto = UserDto.builder()
				.name("user")
				.email("user@email.com")
				.build();

		mvc.perform(
						patch("/users/1")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.ALL)
								.content(mapper.writeValueAsString(userDto))
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)));
	}

	@Test
	void test_updateUser_without_userId() throws Exception {
		userDto = UserDto.builder()
				.name("user")
				.email("user@email.com")
				.build();

		mvc.perform(
						patch("/users")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.ALL)
								.content(mapper.writeValueAsString(userDto))
				)
				.andExpect(status().isMethodNotAllowed())
				.andExpect(result -> assertEquals(HttpRequestMethodNotSupportedException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_update_user_without_user() throws Exception {
		mvc.perform(
						patch("/users/1")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.ALL)
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(HttpMessageNotReadableException.class,
						result.getResolvedException().getClass()));
	}

	@Test
	void test_getAllUsers() throws Exception {
		when(userService.getAllUsers())
				.thenReturn(List.of(UserDto.builder().id(1).build()));

		mvc.perform(
						get("/users")
								.accept(MediaType.ALL)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	void test_getUserById() throws Exception {
		when(userService.getUserById(anyInt()))
				.thenReturn(UserDto.builder().id(1).build());

		mvc.perform(
						get("/users/1")
								.accept(MediaType.ALL)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)));
	}

	@Test
	void test_deleteUserById() throws Exception {
		when(userService.deleteUserById(anyInt()))
				.thenReturn(UserDto.builder().id(1).build());

		mvc.perform(
						delete("/users/1")
								.accept(MediaType.ALL)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)));
	}

	@Test
	void test_deleteUserById_without_userId() throws Exception {
		mvc.perform(
						delete("/users")
								.accept(MediaType.ALL)
				)
				.andExpect(status().isMethodNotAllowed())
				.andExpect(result -> assertEquals(HttpRequestMethodNotSupportedException.class,
						result.getResolvedException().getClass()));
	}
}