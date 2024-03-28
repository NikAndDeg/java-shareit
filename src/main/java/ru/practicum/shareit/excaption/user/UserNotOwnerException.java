package ru.practicum.shareit.excaption.user;

public class UserNotOwnerException extends RuntimeException {
	public UserNotOwnerException(String message) {
		super(message);
	}
}
