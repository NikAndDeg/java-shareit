package ru.practicum.shareit.user;

import ru.practicum.shareit.excaption.user.UserAlreadyExistsException;
import ru.practicum.shareit.excaption.user.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
	User addUser(User user) throws UserAlreadyExistsException;
	User updateUser(User user, int userId) throws UserNotFoundException;
	List<User> getAllUsers();
	User getUserById(int userId) throws UserNotFoundException;
	User deleteUserById(int userId) throws UserNotFoundException;
}
