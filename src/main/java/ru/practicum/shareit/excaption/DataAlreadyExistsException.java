package ru.practicum.shareit.excaption;

public class DataAlreadyExistsException extends RuntimeException {
	public DataAlreadyExistsException(String message) {
		super(message);
	}
}
