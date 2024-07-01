package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.user.UserAlreadyExistsException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.util.List;

public interface UserService {

	UserDto addUser(UserDto user) throws UserAlreadyExistsException;

	UserDto updateUser(UserDto user, int userId) throws UserNotFoundException;

	List<UserDto> getAllUsers();

	UserDto getUserById(int userId) throws UserNotFoundException;

	UserDto deleteUserById(int userId) throws UserNotFoundException;
}
