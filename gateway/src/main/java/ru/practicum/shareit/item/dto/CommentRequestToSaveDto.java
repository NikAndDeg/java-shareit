package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequestToSaveDto {
	@Size(max = 250, message = "size must be between 0 and 250")
	@NotBlank
	private String text;
}
