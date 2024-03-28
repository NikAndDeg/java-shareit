package ru.practicum.shareit.excaption.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.excaption.BadRequestException;
import ru.practicum.shareit.excaption.DataAlreadyExistsException;
import ru.practicum.shareit.excaption.DataNotFoundException;
import ru.practicum.shareit.excaption.user.UserNotOwnerException;

@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {
	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleDataAlreadyExists(final DataAlreadyExistsException exception) {
		return new ErrorResponse("error", exception.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleDataNotFound(final DataNotFoundException exception) {
		return new ErrorResponse("error", exception.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleBadRequest(final BadRequestException exception) {
		return new ErrorResponse("error", exception.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleUserNotOwner(final UserNotOwnerException exception) {
		return new ErrorResponse("error", exception.getMessage());
	}
}