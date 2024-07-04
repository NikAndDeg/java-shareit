package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ItemRequestRequestToSaveDto {
	@Size(max = 200, message = "size must be between 0 and 200")
	@NotBlank
	private String description;
}
