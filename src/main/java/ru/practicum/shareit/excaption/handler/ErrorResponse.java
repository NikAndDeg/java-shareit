package ru.practicum.shareit.excaption.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
	String error;
	String message;
}
