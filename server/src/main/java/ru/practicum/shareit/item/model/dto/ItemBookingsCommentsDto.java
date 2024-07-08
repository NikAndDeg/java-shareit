package ru.practicum.shareit.item.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ItemBookingsCommentsDto {
	private Integer id;

	private String name;

	private String description;

	private Boolean available;

	private BookingForItemDto lastBooking;

	private BookingForItemDto nextBooking;

	private List<CommentDto> comments;

	public static ItemBookingsCommentsDto toDto(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
		ItemBookingsCommentsDto dto = ItemBookingsCommentsDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getAvailable())
				.lastBooking(null)
				.nextBooking(null)
				.comments(new ArrayList<>())
				.build();
		if (lastBooking != null) {
			BookingForItemDto lastBookingDto = BookingForItemDto.builder()
					.id(lastBooking.getId())
					.start(lastBooking.getStart())
					.end(lastBooking.getEnd())
					.status(lastBooking.getStatus())
					.bookerId(lastBooking.getUser().getId())
					.build();
			dto.setLastBooking(lastBookingDto);
		}
		if (nextBooking != null) {
			BookingForItemDto nextBookingDto = BookingForItemDto.builder()
					.id(nextBooking.getId())
					.start(nextBooking.getStart())
					.end(nextBooking.getEnd())
					.status(nextBooking.getStatus())
					.bookerId(nextBooking.getUser().getId())
					.build();
			dto.setNextBooking(nextBookingDto);
		}
		if (comments != null && !comments.isEmpty()) {
			List<CommentDto> commentDtos = comments.stream()
					.map(
							comment -> CommentDto.toDto(comment, comment.getUser().getName())
					)
					.collect(Collectors.toList());
			dto.setComments(commentDtos);
		}
		return dto;
	}
}
