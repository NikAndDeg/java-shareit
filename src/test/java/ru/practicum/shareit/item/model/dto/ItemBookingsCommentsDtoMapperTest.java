package ru.practicum.shareit.item.model.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemBookingsCommentsDtoMapperTest {

	private Integer id = 1;
	private String name = "item";
	private String description = "description";
	private Boolean available = true;

	private Booking lastBooking = createBooking(1,
			LocalDateTime.parse("2020-01-01T10:10:10"),
			LocalDateTime.parse("2021-01-01T10:10:10"),
			BookingStatus.CANCELED,
			createUser(1, "Anthony John Soprano", "tony@emai.com")
	);

	private Booking nextBooking = createBooking(1,
			LocalDateTime.parse("2030-01-01T10:10:10"),
			LocalDateTime.parse("2031-01-01T10:10:10"),
			BookingStatus.APPROVED,
			createUser(1, "Anthony John Soprano", "tony@emai.com")
	);

	private List<Comment> comments = List.of(
			createComment(1,
					"In the end, your friends are gonna let you down. Family. They're the ones you can depend on.",
					createItem(1, "item", "d", true),
					createUser(1, "Anthony John Soprano", "tony@emai.com"),
					LocalDateTime.now()
			),
			createComment(2,
					"In the end, your friends are gonna let you down. Family. They're the ones you can depend on.",
					createItem(1, "item", "d", true),
					createUser(1, "Anthony John Soprano", "tony@emai.com"),
					LocalDateTime.now().plusYears(10)
			)
	);

	private ItemBookingsCommentsDto dto;
	private Item model;

	@Test
	void test_toDto() {
		model = createItem(id, name, description, available);
		dto = ItemBookingsCommentsDto.toDto(model, lastBooking, nextBooking, comments);

		assertEquals(id, dto.getId());
		assertEquals(name, dto.getName());
		assertEquals(description, dto.getDescription());
		assertEquals(available, dto.getAvailable());

		assertEquals(lastBooking.getId(), dto.getLastBooking().getId());
		assertEquals(lastBooking.getStart(), dto.getLastBooking().getStart());
		assertEquals(lastBooking.getEnd(), dto.getLastBooking().getEnd());
		assertEquals(lastBooking.getStatus(), dto.getLastBooking().getStatus());
		assertEquals(lastBooking.getUser().getId(), dto.getLastBooking().getBookerId());

		assertEquals(nextBooking.getId(), dto.getNextBooking().getId());
		assertEquals(nextBooking.getStart(), dto.getNextBooking().getStart());
		assertEquals(nextBooking.getEnd(), dto.getNextBooking().getEnd());
		assertEquals(nextBooking.getStatus(), dto.getNextBooking().getStatus());
		assertEquals(nextBooking.getUser().getId(), dto.getNextBooking().getBookerId());

		assertEquals(comments.get(0).getId(), dto.getComments().get(0).getId());
		assertEquals(comments.get(0).getText(), dto.getComments().get(0).getText());
		assertEquals(comments.get(0).getUser().getName(), dto.getComments().get(0).getAuthorName());
		assertEquals(comments.get(0).getCreated(), dto.getComments().get(0).getCreated());

		assertEquals(comments.get(1).getId(), dto.getComments().get(1).getId());
		assertEquals(comments.get(1).getText(), dto.getComments().get(1).getText());
		assertEquals(comments.get(1).getUser().getName(), dto.getComments().get(1).getAuthorName());
		assertEquals(comments.get(1).getCreated(), dto.getComments().get(1).getCreated());
	}

	@Test
	void test_toDto_without_comments() {
		model = createItem(id, name, description, available);
		dto = ItemBookingsCommentsDto.toDto(model, lastBooking, nextBooking, null);

		assertEquals(id, dto.getId());
		assertEquals(name, dto.getName());
		assertEquals(description, dto.getDescription());
		assertEquals(available, dto.getAvailable());

		assertEquals(lastBooking.getId(), dto.getLastBooking().getId());
		assertEquals(lastBooking.getStart(), dto.getLastBooking().getStart());
		assertEquals(lastBooking.getEnd(), dto.getLastBooking().getEnd());
		assertEquals(lastBooking.getStatus(), dto.getLastBooking().getStatus());
		assertEquals(lastBooking.getUser().getId(), dto.getLastBooking().getBookerId());

		assertEquals(nextBooking.getId(), dto.getNextBooking().getId());
		assertEquals(nextBooking.getStart(), dto.getNextBooking().getStart());
		assertEquals(nextBooking.getEnd(), dto.getNextBooking().getEnd());
		assertEquals(nextBooking.getStatus(), dto.getNextBooking().getStatus());
		assertEquals(nextBooking.getUser().getId(), dto.getNextBooking().getBookerId());

		assertEquals(dto.getComments().size(), 0);
	}

	@Test
	void test_toDto_without_last_and_next_bookings() {
		model = createItem(id, name, description, available);
		dto = ItemBookingsCommentsDto.toDto(model, null, null, comments);

		assertEquals(id, dto.getId());
		assertEquals(name, dto.getName());
		assertEquals(description, dto.getDescription());
		assertEquals(available, dto.getAvailable());

		assertNull(dto.getLastBooking());
		assertNull(dto.getNextBooking());

		assertEquals(comments.get(0).getId(), dto.getComments().get(0).getId());
		assertEquals(comments.get(0).getText(), dto.getComments().get(0).getText());
		assertEquals(comments.get(0).getUser().getName(), dto.getComments().get(0).getAuthorName());
		assertEquals(comments.get(0).getCreated(), dto.getComments().get(0).getCreated());

		assertEquals(comments.get(1).getId(), dto.getComments().get(1).getId());
		assertEquals(comments.get(1).getText(), dto.getComments().get(1).getText());
		assertEquals(comments.get(1).getUser().getName(), dto.getComments().get(1).getAuthorName());
		assertEquals(comments.get(1).getCreated(), dto.getComments().get(1).getCreated());
	}

	private Item createItem(int id, String name, String description, boolean available) {
		Item item = new Item();
		item.setId(id);
		item.setName(name);
		item.setDescription(description);
		item.setAvailable(available);
		return item;
	}

	private Booking createBooking(int id, LocalDateTime start, LocalDateTime end, BookingStatus status, User user) {
		Booking booking = new Booking();
		booking.setId(id);
		booking.setStart(start);
		booking.setEnd(end);
		booking.setStatus(status);
		booking.setUser(user);
		return booking;
	}

	private Comment createComment(Integer id, String text, Item item, User user, LocalDateTime created) {
		Comment comment = new Comment();
		comment.setId(id);
		comment.setText(text);
		comment.setItem(item);
		comment.setUser(user);
		comment.setCreated(created);
		return comment;
	}

	private User createUser(Integer id, String name, String email) {
		User user = new User();
		user.setId(id);
		user.setName(name);
		user.setEmail(email);
		return user;
	}
}