package ru.practicum.shareit.excaption.user;

import ru.practicum.shareit.excaption.DataNotFoundException;

public class UserNotFoundException extends DataNotFoundException {
	public UserNotFoundException(String message) {
		super(message);
	}
}
