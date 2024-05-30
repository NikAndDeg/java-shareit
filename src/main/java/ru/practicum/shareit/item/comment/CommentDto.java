package ru.practicum.shareit.item.comment;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
	private Integer id;

	@Size(max = 250, message = "size must be between 0 and 250")
	@NotBlank
	private String text;

	private String authorName;

	private LocalDateTime created;

	public static CommentDto toDto(Comment comment, String authorName) {
		return CommentDto.builder()
				.id(comment.getId())
				.text(comment.getText())
				.authorName(authorName)
				.created(comment.getCreated())
				.build();
	}

	public static Comment toModel(CommentDto dto) {
		Comment comment = new Comment();
		comment.setText(dto.getText());
		comment.setCreated(LocalDateTime.now());
		return comment;
	}
}
