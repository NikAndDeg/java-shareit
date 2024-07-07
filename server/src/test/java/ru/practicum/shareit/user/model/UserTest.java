package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {
	private User user1;
	private User user2;

	@Test
	void user_equals_test() {
		user1 = createUser(1, "user", "email@email.com");
		user2 = createUser(1, "user", "email@email.com");
		assertTrue(user1.equals(user2));
	}

	private User createUser(Integer id, String name, String email) {
		User user = new User();
		user.setId(id);
		user.setName(name);
		user.setEmail(email);
		return user;
	}
}