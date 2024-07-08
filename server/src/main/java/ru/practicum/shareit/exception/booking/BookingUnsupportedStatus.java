package ru.practicum.shareit.exception.booking;

import ru.practicum.shareit.exception.DataNotFoundException;

public class BookingUnsupportedStatus extends DataNotFoundException {
	public BookingUnsupportedStatus(String message) {
		super(message);
	}
}
