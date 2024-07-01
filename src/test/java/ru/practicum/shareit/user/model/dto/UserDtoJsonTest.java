package ru.practicum.shareit.user.model.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class UserDtoJsonTest {
	@Autowired
	private JacksonTester<UserDto> json;
	@Autowired
	private ObjectMapper mapper;

	private UserDto user = UserDto.builder()
			.id(1)
			.name("Anthony John Soprano")
			.email("ajohns@email.com")
			.build();

	@Test
	void serializationTest() throws Exception {
		JsonContent<UserDto> result = json.write(user);

		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Anthony John Soprano");
		assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("ajohns@email.com");
	}

	@Test
	void deserializationTest() throws IOException {
		String userJson = mapper.writeValueAsString(user);
		UserDto userFromJson = json.parseObject(userJson);
		assertEquals(user, userFromJson);
	}
}