package ru.practicum.shareit.excaption;

public class DataNotFoundException extends RuntimeException {
	public DataNotFoundException(String message) {
		super(message);
	}
}
