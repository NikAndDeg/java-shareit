package ru.practicum.shareit.exception.item;

import ru.practicum.shareit.exception.DataAlreadyExistsException;

public class ItemAlreadyExistsException extends DataAlreadyExistsException {
	public ItemAlreadyExistsException(String message) {
		super(message);
	}
}
