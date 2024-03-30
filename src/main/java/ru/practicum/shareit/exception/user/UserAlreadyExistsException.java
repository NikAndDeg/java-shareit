package ru.practicum.shareit.exception.user;

import ru.practicum.shareit.exception.DataAlreadyExistsException;

public class UserAlreadyExistsException extends DataAlreadyExistsException {
	public UserAlreadyExistsException(String message) {
		super(message);
	}
}
