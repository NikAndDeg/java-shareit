package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
