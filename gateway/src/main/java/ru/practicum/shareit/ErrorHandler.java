package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import java.util.Map;

@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class ErrorHandler {
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> handleConstraintViolationException(final ConstraintViolationException e) {
		log.warn(e.getMessage());
		return Map.of("error", "Unknown state: " + e.getMessage());
	}
}
