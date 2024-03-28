package ru.practicum.shareit.exception.user;

import ru.practicum.shareit.exception.DataNotFoundException;

public class UserNotFoundException extends DataNotFoundException {
	public UserNotFoundException(String message) {
		super(message);
	}
}
