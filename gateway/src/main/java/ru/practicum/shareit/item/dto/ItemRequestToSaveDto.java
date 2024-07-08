package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemRequestToSaveDto {
	@Size(max = 200, message = "size must be between 0 and 200")
	@NotBlank
	@NotNull
	private String name;

	@Size(max = 200, message = "size must be between 0 and 200")
	@NotBlank
	private String description;

	@NotNull
	private Boolean available;

	private Integer requestId;
}
