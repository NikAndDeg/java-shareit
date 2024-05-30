package ru.practicum.shareit.user.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserDto {
	private Integer id;

	@Size(max = 200, message = "size must be between 0 and 200")
	private String name;

	@Email
	@Size(max = 200, message = "size must be between 0 and 200")
	private String email;
}
