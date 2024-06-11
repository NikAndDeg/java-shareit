package ru.practicum.shareit.user.model.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDtoMapperTest {
	private static final Integer id = 1;
	private static final String name = "Anthony John Soprano";
	private static final String email = "tony@email.com";
	private static UserDto dto;
	private static User model;

	@Test
	void test_toDto() {
		dto = UserDto.toDto(createUser(id, name, email));
		assertEquals(id, dto.getId());
		assertEquals(name, dto.getName());
		assertEquals(email, dto.getEmail());
	}

	@Test
	void test_toModel() {
		model = UserDto.toModel(
				UserDto.builder()
						.id(id)
						.name(name)
						.email(email)
						.build()
		);

		assertEquals(id, model.getId());
		assertEquals(name, model.getName());
		assertEquals(email, model.getEmail());
	}

	private User createUser(Integer id, String name, String email) {
		User user = new User();
		user.setId(id);
		user.setName(name);
		user.setEmail(email);
		return user;
	}
}