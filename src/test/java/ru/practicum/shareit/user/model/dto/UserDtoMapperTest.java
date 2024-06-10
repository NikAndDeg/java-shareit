package ru.practicum.shareit.user.model.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDtoMapperTest {
	private Integer id = 1;
	private String name = "Anthony John Soprano";
	private String email = "tony@email.com";
	private UserDto dto;
	private User model;

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