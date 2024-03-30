package ru.practicum.shareit.booking.model;

public enum BookingStatus {
	WAITING, //новое бронирование
	APPROVED, //бронирование подтверждено
	REJECTED, //бронирование отклонено владельцем
	CANCELED //бронирование отменено создателем
}
