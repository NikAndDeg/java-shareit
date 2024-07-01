package ru.practicum.shareit.item.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class CommentDtoJsonTest {
	@Autowired
	private JacksonTester<CommentDto> json;
	@Autowired
	private ObjectMapper mapper;

	private final CommentDto commentDto = CommentDto.builder()
			.id(1)
			.text("In the end, your friends are gonna let you down. Family. They're the ones you can depend on.")
			.authorName("Anthony John Soprano")
			.created(LocalDateTime.parse("2024-01-01T10:10:10"))
			.build();

	@Test
	void serializationTest() throws Exception {
		JsonContent<CommentDto> result = json.write(commentDto);

		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("In the end, your friends are gonna let you down. Family. They're the ones you can depend on.");
		assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Anthony John Soprano");
		assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-01-01T10:10:10");
	}

	@Test
	void deserializationTest() throws IOException {
		String commentDtoJson = mapper.writeValueAsString(commentDto);
		CommentDto commentDtoFromJson = json.parseObject(commentDtoJson);
		assertEquals(commentDto, commentDtoFromJson);
	}
}