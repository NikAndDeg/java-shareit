package ru.practicum.shareit.exception.user;

public class UserNotOwnerException extends RuntimeException {
	public UserNotOwnerException(String message) {
		super(message);
	}
}
