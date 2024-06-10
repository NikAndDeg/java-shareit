package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentDtoMapperTest {
	private final Integer id = 1;
	private final String text = "In the end, your friends are gonna let you down. Family. They're the ones you can depend on.";
	private final String authorName = "Anthony John Soprano";
	private final LocalDateTime created = LocalDateTime.parse("2024-01-01T10:10:10");

	private CommentDto dto;
	private Comment model;

	@Test
	void test_toDto() {
		model = createComment(id,
				text,
				createUser(1, authorName, "tony@email.com"),
				created);

		dto = CommentDto.toDto(model, model.getUser().getName());

		assertEquals(id, dto.getId());
		assertEquals(text, dto.getText());
		assertEquals(authorName, dto.getAuthorName());
		assertEquals(created, dto.getCreated());
	}

	@Test
	void test_toModel() {
		dto = CommentDto.builder()
				.id(id)
				.text(text)
				.authorName(authorName)
				.created(created)
				.build();

		model = CommentDto.toModel(dto);
		assertEquals(text, model.getText());
		assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
				model.getCreated().truncatedTo(ChronoUnit.SECONDS));
	}

	private Comment createComment(int id, String text, User user, LocalDateTime created) {
		Comment comment = new Comment();
		comment.setId(id);
		comment.setText(text);
		comment.setUser(user);
		comment.setCreated(created);
		return comment;
	}

	private User createUser(int id, String name, String email) {
		User user = new User();
		user.setId(id);
		user.setName(name);
		user.setEmail(email);
		return user;
	}
}