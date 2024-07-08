package ru.practicum.shareit.request.model.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@JsonTest
class ItemRequestDtoJsonTest {
	@Autowired
	private JacksonTester<ItemRequestDto> json;
	@Autowired
	private ObjectMapper mapper;

	private ItemRequestDto request;

	@BeforeEach
	void setUp() {
		request = ItemRequestDto.builder()
				.id(1)
				.description("description")
				.created(LocalDateTime.parse("2024-01-01T10:10:10"))
				.build();

		List<ItemDto> items = List.of(
				ItemDto.builder()
						.id(1)
						.name("item")
						.description("description")
						.available(true)
						.requestId(25)
						.build(),
				ItemDto.builder()
						.id(2)
						.name("item2")
						.description("description")
						.available(false)
						.requestId(125)
						.build()
		);
		request.setItems(items);

		UserDto requester = UserDto.builder()
				.id(1)
				.name("Anthony John Soprano")
				.email("ajohns@email.com")
				.build();
		request.setRequester(requester);
	}

	@Test
	void serializationTest() throws Exception {
		JsonContent<ItemRequestDto> result = json.write(request);

		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
		assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-01-01T10:10:10");

		assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("item");
		assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo("description");
		assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
		assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(25);

		assertThat(result).extractingJsonPathNumberValue("$.items[1].id").isEqualTo(2);
		assertThat(result).extractingJsonPathStringValue("$.items[1].name").isEqualTo("item2");
		assertThat(result).extractingJsonPathStringValue("$.items[1].description").isEqualTo("description");
		assertThat(result).extractingJsonPathBooleanValue("$.items[1].available").isEqualTo(false);
		assertThat(result).extractingJsonPathNumberValue("$.items[1].requestId").isEqualTo(125);

		assertThat(result).extractingJsonPathNumberValue("$.requester.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.requester.name").isEqualTo("Anthony John Soprano");
		assertThat(result).extractingJsonPathStringValue("$.requester.email").isEqualTo("ajohns@email.com");
	}

	@Test
	void deserializationTest() throws IOException {
		String requestJson = mapper.writeValueAsString(request);
		ItemRequestDto requestFromJson = json.parseObject(requestJson);
		assertEquals(request, requestFromJson);
	}
}