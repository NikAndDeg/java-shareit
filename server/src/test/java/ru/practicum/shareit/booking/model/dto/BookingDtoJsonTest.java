package ru.practicum.shareit.booking.model.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class BookingDtoJsonTest {
	@Autowired
	private JacksonTester<BookingDto> json;
	@Autowired
	private ObjectMapper mapper;

	private BookingDto bookingDto;

	@BeforeEach
	void setUp() {
		bookingDto = BookingDto.builder()
				.id(1)
				.start(LocalDateTime.parse("2024-01-01T10:10:10"))
				.end(LocalDateTime.parse("2025-01-01T10:10:10"))
				.itemId(2)
				.status(BookingStatus.REJECTED)
				.booker(null)
				.item(null)
				.build();

		UserDto userDto = UserDto.builder()
				.id(10)
				.name("Anthony John Soprano")
				.email("ajohns@email.com")
				.build();
		bookingDto.setBooker(userDto);

		ItemDto itemDto = ItemDto.builder()
				.id(2)
				.name("Garbage shredder")
				.description("Ideal for debris in your yard.")
				.available(false)
				.build();
		bookingDto.setItem(itemDto);
	}

	@Test
	void serializationTest() throws Exception {
		JsonContent<BookingDto> result = json.write(bookingDto);

		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-01-01T10:10:10");
		assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-01-01T10:10:10");
		assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
		assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("REJECTED");

		assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(10);
		assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Anthony John Soprano");
		assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("ajohns@email.com");

		assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
		assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Garbage shredder");
		assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("Ideal for debris in your yard.");
		assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(false);
		assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isNull();
	}

	@Test
	void deserializationTest() throws IOException {
		String bookingDtoJson = mapper.writeValueAsString(bookingDto);
		BookingDto bookingDtoFromJson = json.parseObject(bookingDtoJson);
		assertEquals(bookingDto, bookingDtoFromJson);
	}
}