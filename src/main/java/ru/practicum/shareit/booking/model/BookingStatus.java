package ru.practicum.shareit.booking.model;

public enum BookingStatus {
	BLANK, //костыль для БД
	WAITING, //новое бронирование
	APPROVED, //бронирование подтверждено
	REJECTED, //бронирование отклонено владельцем
	CANCELED //бронирование отменено создателем
}
