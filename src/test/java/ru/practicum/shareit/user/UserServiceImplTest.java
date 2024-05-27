package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import static ru.practicum.shareit.DataForTests.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql("/test_schema.sql")
class UserServiceImplTest {
	@Autowired
	UserServiceImpl service;

	@Test
	void get_user_by_id_1() {
		User user = service.getUserById(1);
		assertEquals(savedUsers.get(0), user);
	}

	@Test
	void get_user_by_id_999() {
		Exception exception = assertThrows(UserNotFoundException.class, () -> service.getUserById(999));
		assertEquals("User with id [999] not exists.", exception.getMessage());
	}

	@Test
	void delete_user_by_id_1() {
		User deletedUser = service.deleteUserById(1);
		assertEquals(savedUsers.get(0), deletedUser);
		Exception exception = assertThrows(UserNotFoundException.class, () -> service.getUserById(1));
		assertEquals("User with id [1] not exists.", exception.getMessage());
	}

	@Test
	void delete_user_by_id_999() {
		Exception exception = assertThrows(UserNotFoundException.class, () -> service.deleteUserById(999));
		assertEquals("User with id [999] not exists.", exception.getMessage());
	}

	@Test
	void update_only_name_user_by_id_1() {
		User updatedUser = service.updateUser(createUser(null, "updated", null), 1);
		User expectedUser = createUser(1, "updated", "1@email.com");
		assertEquals(expectedUser, updatedUser);
	}
}