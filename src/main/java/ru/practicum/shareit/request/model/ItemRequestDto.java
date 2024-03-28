package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
	private int id;
	private String description;
	private int requesterId;
	private LocalDateTime created;
}
