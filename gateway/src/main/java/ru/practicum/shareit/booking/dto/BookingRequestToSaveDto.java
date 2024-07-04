package ru.practicum.shareit.booking.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingRequestToSaveDto {
	@NotNull
	private LocalDateTime start;
	@NotNull
	private LocalDateTime end;
	@NotNull
	private Integer itemId;
}
