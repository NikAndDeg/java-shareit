package ru.practicum.shareit.exception.item;

import ru.practicum.shareit.exception.DataNotFoundException;

public class ItemNotFoundException extends DataNotFoundException {
	public ItemNotFoundException(String message) {
		super(message);
	}
}
