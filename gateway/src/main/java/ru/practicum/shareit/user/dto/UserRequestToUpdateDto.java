package ru.practicum.shareit.user.dto;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
public class UserRequestToUpdateDto {
	@Size(max = 200, message = "size must be between 0 and 200")
	private String name;

	@Email
	@Size(max = 200, message = "size must be between 0 and 200")
	private String email;
}
