package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest {
	@EqualsAndHashCode.Exclude
	private int id;
	@NotBlank
	@Size(max = 200, message = "size must be between 0 and 200")
	private String description;
	private int requesterId;
	@EqualsAndHashCode.Exclude
	private LocalDateTime created;
}
