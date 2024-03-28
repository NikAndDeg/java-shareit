package ru.practicum.shareit.excaption.item;

import ru.practicum.shareit.excaption.DataAlreadyExistsException;

public class ItemAlreadyExistsException extends DataAlreadyExistsException {
	public ItemAlreadyExistsException(String message) {
		super(message);
	}
}
