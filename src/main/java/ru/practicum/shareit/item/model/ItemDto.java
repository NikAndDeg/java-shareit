package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDto {
	private int id;
	@Size(max = 200, message = "size must be between 0 and 200")
	private String name;
	@Size(max = 200, message = "size must be between 0 and 200")
	private String description;
	private Boolean available;
}
