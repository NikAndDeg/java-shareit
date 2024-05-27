package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.DataAlreadyExistsException;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.user.UserNotOwnerException;

@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class ErrorHandler {
	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleDataAlreadyExists(final DataAlreadyExistsException exception) {
		log.warn(exception.getMessage());
		return new ErrorResponse("error", exception.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleDataNotFound(final DataNotFoundException exception) {
		log.warn(exception.getMessage());
		return new ErrorResponse("error", exception.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleBadRequest(final BadRequestException exception) {
		log.warn(exception.getMessage());
		return new ErrorResponse("error", exception.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleUserNotOwner(final UserNotOwnerException exception) {
		log.warn(exception.getMessage());
		return new ErrorResponse("error", exception.getMessage());
	}
}
