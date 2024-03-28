package ru.practicum.shareit.excaption.user;

import ru.practicum.shareit.excaption.DataAlreadyExistsException;

public class UserAlreadyExistsException extends DataAlreadyExistsException {
	public UserAlreadyExistsException(String message) {
		super(message);
	}
}
