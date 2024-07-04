package ru.practicum.shareit.item.model.dto;

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
class ItemDtoJsonTest {
	@Autowired
	private JacksonTester<ItemDto> json;
	@Autowired
	private ObjectMapper mapper;

	private ItemDto item = ItemDto.builder()
			.id(1)
			.name("item")
			.description("description")
			.available(false)
			.requestId(2)
			.build();

	@Test
	void serializationTest() throws Exception {
		JsonContent<ItemDto> result = json.write(item);

		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
		assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
		assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(false);
		assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
	}

	@Test
	void deserializationTest() throws IOException {
		String itemJson = mapper.writeValueAsString(item);
		ItemDto itemFromJson = json.parseObject(itemJson);
		assertEquals(item, itemFromJson);
	}
}