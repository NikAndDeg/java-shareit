package ru.practicum.shareit.booking.model.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingDtoStartComparatorTest {
	private final LocalDateTime start1 = LocalDateTime.parse("2024-01-01T10:10:10");
	private final LocalDateTime start2 = LocalDateTime.parse("2025-01-01T10:10:10");
	private final LocalDateTime start3 = LocalDateTime.parse("2026-01-01T10:10:10");

	@Test
	void startComparatorTest() {
		BookingDto dto1 = BookingDto.builder()
				.id(1)
				.start(start1)
				.build();
		BookingDto dto2 = BookingDto.builder()
				.id(2)
				.start(start2)
				.build();
		BookingDto dto3 = BookingDto.builder()
				.id(3)
				.start(start3)
				.build();
		List<BookingDto> dtos = new ArrayList<>(List.of(dto3, dto1, dto2));

		dtos.sort(BookingDto.startComparator);
		checkOrder(dtos);

		Collections.shuffle(dtos);
		dtos.sort(BookingDto.startComparator);
		checkOrder(dtos);

		Collections.shuffle(dtos);
		dtos.sort(BookingDto.startComparator);
		checkOrder(dtos);

		Collections.shuffle(dtos);
		dtos.sort(BookingDto.startComparator);
		checkOrder(dtos);
	}

	private void checkOrder(List<BookingDto> dtos) {
		assertEquals(dtos.get(0).getId(), 3);
		assertEquals(dtos.get(1).getId(), 2);
		assertEquals(dtos.get(2).getId(), 1);
	}
}