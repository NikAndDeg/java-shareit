package ru.practicum.shareit.booking.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.user.model.dto.UserDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
	private Integer id;
	@NotNull
	private LocalDateTime start;
	@NotNull
	private LocalDateTime end;
	@NotNull
	private Integer itemId;
	private BookingStatus status;
	private UserDto booker;
	private ItemDto item;
}
