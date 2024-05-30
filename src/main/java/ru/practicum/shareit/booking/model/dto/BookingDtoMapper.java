package ru.practicum.shareit.booking.model.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDtoMapper;

@Component
@RequiredArgsConstructor
public class BookingDtoMapper {
	private final UserDtoMapper userMapper;

	public BookingDto toDto(Booking model) {
		return BookingDto.builder()
				.id(model.getId())
				.start(model.getStart())
				.end(model.getEnd())
				.itemId(model.getItem().getId())
				.status(model.getStatus())
				.build();
	}

	public BookingDto toDto(Booking model, Item item, User requester) {
		BookingDto bookingDto = BookingDto.builder()
				.id(model.getId())
				.start(model.getStart())
				.end(model.getEnd())
				.itemId(model.getItem().getId())
				.status(model.getStatus())
				.build();
		bookingDto.setItem(ItemDto.toDto(item));
		bookingDto.setBooker(userMapper.toDto(requester));
		return bookingDto;
	}

	public Booking toModel(BookingDto dto) {
		Booking booking = new Booking();
		booking.setStart(dto.getStart());
		booking.setEnd(dto.getEnd());
		booking.setStatus(dto.getStatus());
		return booking;
	}
}
