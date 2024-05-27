package ru.practicum.shareit.user.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.mapper.DtoMapper;

@Component
public class UserDtoMapper implements DtoMapper<UserDto, User> {

	@Override
	public UserDto toDto(User model, String... args) {
		return UserDto.builder()
				.id(model.getId())
				.name(model.getName())
				.email(model.getEmail())
				.build();
	}

	@Override
	public User toModel(UserDto dto, String... args) {
		User user = new User();
		user.setId(dto.getId());
		user.setName(dto.getName());
		user.setEmail(dto.getEmail());
		return user;
	}
}
