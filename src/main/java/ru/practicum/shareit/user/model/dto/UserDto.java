package ru.practicum.shareit.user.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

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


	public static UserDto toDto(User model) {
		return UserDto.builder()
				.id(model.getId())
				.name(model.getName())
				.email(model.getEmail())
				.build();
	}

	public static User toModel(UserDto dto) {
		User user = new User();
		user.setId(dto.getId());
		user.setName(dto.getName());
		user.setEmail(dto.getEmail());
		return user;
	}
}
