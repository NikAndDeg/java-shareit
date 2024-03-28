package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;

@Data
@Builder
public class Item {
	private int id;
	@Size(max = 200, message = "size must be between 0 and 200")
	private String name;
	@Size(max = 200, message = "size must be between 0 and 200")
	private String description;
	@EqualsAndHashCode.Exclude
	private Boolean available;
	private int ownerId;
	@EqualsAndHashCode.Exclude
	private int requestId;
	@EqualsAndHashCode.Exclude
	private int bookingCounter;
}
