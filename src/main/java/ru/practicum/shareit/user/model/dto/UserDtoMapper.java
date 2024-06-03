package ru.practicum.shareit.user.model.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public class UserDtoMapper {

	public UserDto toDto(User model) {
		return UserDto.builder()
				.id(model.getId())
				.name(model.getName())
				.email(model.getEmail())
				.build();
	}

	public User toModel(UserDto dto) {
		User user = new User();
		user.setId(dto.getId());
		user.setName(dto.getName());
		user.setEmail(dto.getEmail());
		return user;
	}
}
