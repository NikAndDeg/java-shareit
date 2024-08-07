package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemRequestRequestToSaveDto {
	@Size(max = 200, message = "size must be between 0 and 200")
	@NotBlank
	private String description;
}
