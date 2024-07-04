package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemRequestToUpdateDto {
	private Integer id;

	private String name;

	private String description;

	private Boolean available;
}
