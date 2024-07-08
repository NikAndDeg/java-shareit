package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestToSaveDto {
	@Size(max = 200, message = "size must be between 0 and 200")
	@NotNull
	private String name;

	@Email
	@Size(max = 200, message = "size must be between 0 and 200")
	@NotNull
	private String email;
}
