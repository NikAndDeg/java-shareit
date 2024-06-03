package ru.practicum.shareit.item.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingForItemDto {
	private Integer id;

	private LocalDateTime start;

	private LocalDateTime end;

	private BookingStatus status;

	private Integer bookerId;
}
