package ru.practicum.shareit.booking.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Comparator;

@Data
@Builder
public class BookingDto {
	public static final Comparator<BookingDto> startComparator = (BookingDto b1, BookingDto b2) -> {
		if (b1.getStart().isBefore(b2.getStart()))
			return 1;
		if (b1.getStart().isAfter(b2.getStart()))
			return -1;
		else
			return 0;
	};
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


	public static BookingDto toDto(Booking model, Item item, User requester) {
		BookingDto bookingDto = BookingDto.builder()
				.id(model.getId())
				.start(model.getStart())
				.end(model.getEnd())
				.itemId(model.getItem().getId())
				.status(model.getStatus())
				.build();
		bookingDto.setItem(ItemDto.toDto(item));
		bookingDto.setBooker(UserDto.toDto(requester));
		return bookingDto;
	}

	public static Booking toModel(BookingDto dto) {
		Booking booking = new Booking();
		booking.setStart(dto.getStart());
		booking.setEnd(dto.getEnd());
		booking.setStatus(dto.getStatus());
		return booking;
	}
}
