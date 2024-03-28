package ru.practicum.shareit.excaption.item;

import ru.practicum.shareit.excaption.DataNotFoundException;

public class ItemNotFoundException extends DataNotFoundException {
	public ItemNotFoundException(String message) {
		super(message);
	}
}
