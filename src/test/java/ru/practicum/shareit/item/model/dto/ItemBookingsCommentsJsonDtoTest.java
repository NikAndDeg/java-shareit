package ru.practicum.shareit.item.model.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.comment.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class ItemBookingsCommentsJsonDtoTest {
	@Autowired
	private JacksonTester<ItemBookingsCommentsDto> json;
	@Autowired
	private ObjectMapper mapper;

	private ItemBookingsCommentsDto item;

	@BeforeEach
	void setUp() {
		item = ItemBookingsCommentsDto.builder()
				.id(1)
				.name("item")
				.description("description")
				.available(true)
				.build();

		BookingForItemDto lastBooking = BookingForItemDto.builder()
				.id(1)
				.start(LocalDateTime.parse("2020-01-01T10:10:10"))
				.end(LocalDateTime.parse("2021-01-01T10:10:10"))
				.status(BookingStatus.CANCELED)
				.bookerId(2)
				.build();
		item.setLastBooking(lastBooking);

		BookingForItemDto nextBooking = BookingForItemDto.builder()
				.id(2)
				.start(LocalDateTime.parse("2030-01-01T10:10:10"))
				.end(LocalDateTime.parse("2031-01-01T10:10:10"))
				.status(BookingStatus.APPROVED)
				.bookerId(2)
				.build();
		item.setNextBooking(nextBooking);

		List<CommentDto> comments = List.of(
				CommentDto.builder()
						.id(1)
						.text("In the end, your friends are gonna let you down. Family. They're the ones you can depend on.")
						.authorName("Anthony John Soprano")
						.created(LocalDateTime.parse("2024-01-01T10:10:10"))
						.build(),
				CommentDto.builder()
						.id(2)
						.text("In the end, your friends are gonna let you down. Family. They're the ones you can depend on.")
						.authorName("Anthony John Soprano")
						.created(LocalDateTime.parse("2034-01-01T10:10:10"))
						.build()
		);

		item.setComments(comments);
	}

	@Test
	void serializationTest() throws Exception {
		JsonContent<ItemBookingsCommentsDto> result = json.write(item);

		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
		assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
		assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);

		assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.lastBooking.start").isEqualTo("2020-01-01T10:10:10");
		assertThat(result).extractingJsonPathStringValue("$.lastBooking.end").isEqualTo("2021-01-01T10:10:10");
		assertThat(result).extractingJsonPathStringValue("$.lastBooking.status").isEqualTo("CANCELED");
		assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(2);

		assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
		assertThat(result).extractingJsonPathStringValue("$.nextBooking.start").isEqualTo("2030-01-01T10:10:10");
		assertThat(result).extractingJsonPathStringValue("$.nextBooking.end").isEqualTo("2031-01-01T10:10:10");
		assertThat(result).extractingJsonPathStringValue("$.nextBooking.status").isEqualTo("APPROVED");
		assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(2);

		assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.comments[0].text")
				.isEqualTo("In the end, your friends are gonna let you down. Family. They're the ones you can depend on.");
		assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName")
				.isEqualTo("Anthony John Soprano");
		assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
				.isEqualTo("2024-01-01T10:10:10");

		assertThat(result).extractingJsonPathNumberValue("$.comments[1].id").isEqualTo(2);
		assertThat(result).extractingJsonPathStringValue("$.comments[1].text")
				.isEqualTo("In the end, your friends are gonna let you down. Family. They're the ones you can depend on.");
		assertThat(result).extractingJsonPathStringValue("$.comments[1].authorName")
				.isEqualTo("Anthony John Soprano");
		assertThat(result).extractingJsonPathStringValue("$.comments[1].created")
				.isEqualTo("2034-01-01T10:10:10");
	}

	@Test
	void deserializationTest() throws IOException {
		String itemJson = mapper.writeValueAsString(item);
		ItemBookingsCommentsDto itemFromJson = json.parseObject(itemJson);
		assertEquals(item, itemFromJson);
	}
}