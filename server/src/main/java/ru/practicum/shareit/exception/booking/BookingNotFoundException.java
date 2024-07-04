package ru.practicum.shareit.exception.booking;

import ru.practicum.shareit.exception.DataNotFoundException;

public class BookingNotFoundException extends DataNotFoundException {
	public BookingNotFoundException(String message) {
		super(message);
	}
}
