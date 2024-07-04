package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.user.UserAlreadyExistsException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceImplTest {
	UserDto userDtoTo;
	UserDto userDtoFrom;
	private UserRepository userRepository;
	private UserService service;

	@BeforeEach
	void setUp() {
		userRepository = mock(UserRepository.class);
		service = new UserServiceImpl(userRepository);
	}

	@Test
	void test_addUser() {
		when(userRepository.save(any()))
				.thenReturn(createUser(1, "user", "user@emai.com"));

		userDtoTo = UserDto.builder()
				.name("user")
				.email("user@email.com")
				.build();
		userDtoFrom = service.addUser(userDtoTo);
		assertEquals(1, userDtoFrom.getId());
		assertEquals("user", userDtoFrom.getName());
		assertEquals("user@emai.com", userDtoFrom.getEmail());
	}

	@Test
	void test_addUser_with_same_email_or_name() {
		when(userRepository.save(any()))
				.thenThrow(DataIntegrityViolationException.class);
		userDtoTo = UserDto.builder()
				.name("user")
				.email("user@email.com")
				.build();
		Exception exception = assertThrows(UserAlreadyExistsException.class,
				() -> service.addUser(userDtoTo));
		assertEquals("User not saved. User with same name or email already exists.", exception.getMessage());
	}

	@Test
	void test_updateUser() {
		when(userRepository.findByIdOrNameOrEmail(anyInt(), anyString(), anyString()))
				.thenReturn(List.of(createUser(1, "user", "user@email.com")));
		when(userRepository.save(any()))
				.thenReturn(createUser(1, "user", "user@email.com"));

		userDtoTo = UserDto.builder()
				.name("user")
				.email("user@email.com")
				.build();
		userDtoFrom = service.updateUser(userDtoTo, 1);
		assertEquals(1, userDtoFrom.getId());
	}

	@Test
	void test_updateUser_user_with_new_name_already_exists() {
		when(userRepository.findByIdOrNameOrEmail(anyInt(), anyString(), anyString()))
				.thenReturn(List.of(
						createUser(1, "user", "user@email.com"),
						createUser(2, "new name", "newName@email.com")
				));

		userDtoTo = UserDto.builder()
				.name("new name")
				.email("user@email.com")
				.build();
		Exception exception = assertThrows(UserAlreadyExistsException.class,
				() -> service.updateUser(userDtoTo, 1)
		);
		assertEquals("User not updated. User with the name [new name] already exists.", exception.getMessage());
	}

	@Test
	void test_updateUser_user_with_new_email_already_exists() {
		when(userRepository.save(any()))
				.thenReturn(createUser(1, "user", "user@emai.com"));
		when(userRepository.findByIdOrNameOrEmail(anyInt(), anyString(), anyString()))
				.thenReturn(List.of(
						createUser(1, "user", "user@email.com"),
						createUser(2, "new name", "newName@email.com")
				));

		userDtoTo = UserDto.builder()
				.name("user")
				.email("newName@email.com")
				.build();
		Exception exception = assertThrows(UserAlreadyExistsException.class,
				() -> service.updateUser(userDtoTo, 1)
		);
		assertEquals("User not updated. User with the email [newName@email.com] already exists.", exception.getMessage());
	}

	@Test
	void test_updateUser_updatable_user_not_exists() {
		when(userRepository.findByIdOrNameOrEmail(anyInt(), anyString(), anyString()))
				.thenReturn(List.of(
						createUser(2, "new name", "newName@email.com")
				));

		userDtoTo = UserDto.builder()
				.name("user")
				.email("user@email.com")
				.build();
		Exception exception = assertThrows(UserNotFoundException.class,
				() -> service.updateUser(userDtoTo, 1)
		);
		assertEquals("User not updated. User with id [1] not exists.", exception.getMessage());
	}

	@Test
	void test_getAllUsers() {
		when(userRepository.findAll())
				.thenReturn(List.of(
						createUser(1, "name1", "email1"),
						createUser(2, "name2", "email2")
				));
		List<UserDto> users = service.getAllUsers();
		assertEquals(1, users.get(0).getId());
		assertEquals("name1", users.get(0).getName());
		assertEquals("email1", users.get(0).getEmail());
		assertEquals(2, users.get(1).getId());
		assertEquals("name2", users.get(1).getName());
		assertEquals("email2", users.get(1).getEmail());
	}

	@Test
	void test_getUserById() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1, "user", "user@emai.com")));
		userDtoFrom = service.getUserById(1);
		assertEquals(1, userDtoFrom.getId());
		assertEquals("user", userDtoFrom.getName());
		assertEquals("user@emai.com", userDtoFrom.getEmail());
	}

	@Test
	void test_getUserById_user_not_exists() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.empty());
		Exception exception = assertThrows(UserNotFoundException.class,
				() -> service.getUserById(1)
		);
		assertEquals("User with id [1] not exists.", exception.getMessage());
	}

	@Test
	void test_deleteUserById() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(createUser(1, "user", "user@emai.com")));
		userDtoFrom = service.deleteUserById(1);
		assertEquals(1, userDtoFrom.getId());
		assertEquals("user", userDtoFrom.getName());
		assertEquals("user@emai.com", userDtoFrom.getEmail());
	}

	@Test
	void test_deleteUserById_user_not_exists() {
		when(userRepository.findById(anyInt()))
				.thenReturn(Optional.empty());
		Exception exception = assertThrows(UserNotFoundException.class,
				() -> service.deleteUserById(1)
		);
		assertEquals("User with id [1] not exists.", exception.getMessage());
	}

	private User createUser(Integer id, String name, String email) {
		User user = new User();
		user.setId(id);
		user.setName(name);
		user.setEmail(email);
		return user;
	}
}