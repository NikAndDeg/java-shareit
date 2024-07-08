package ru.practicum.shareit.booking.exception;

public class BookingUnsupportedStatus extends RuntimeException {
	public BookingUnsupportedStatus(String message) {
		super(message);
	}
}
