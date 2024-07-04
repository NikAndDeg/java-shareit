package ru.practicum.shareit.item.model.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class BookingForItemDtoJsonTest {
	@Autowired
	private JacksonTester<BookingForItemDto> json;
	@Autowired
	private ObjectMapper mapper;

	private BookingForItemDto booking = BookingForItemDto.builder()
			.id(1)
			.start(LocalDateTime.parse("2024-01-01T10:10:10"))
			.end(LocalDateTime.parse("2025-01-01T10:10:10"))
			.status(BookingStatus.APPROVED)
			.bookerId(2)
			.build();

	@Test
	void serializationTest() throws Exception {
		JsonContent<BookingForItemDto> result = json.write(booking);

		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-01-01T10:10:10");
		assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-01-01T10:10:10");
		assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
		assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
	}

	@Test
	void deserializationTest() throws IOException {
		String bookingJson = mapper.writeValueAsString(booking);
		BookingForItemDto bookingFromJson = json.parseObject(bookingJson);
		assertEquals(booking, bookingFromJson);
	}
}