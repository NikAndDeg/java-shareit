package ru.practicum.shareit.item.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class CommentRequestToSaveDto {
	@Size(max = 250, message = "size must be between 0 and 250")
	@NotBlank
	private String text;
}
