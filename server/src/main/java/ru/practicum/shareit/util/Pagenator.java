package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.BadRequestException;


@UtilityClass
public class Pagenator {
	public static Pageable getPage(int from, int size, Sort sort) {
		if (from < 0 || size < 1)
			throw new BadRequestException("Illegal argument for pagination: from ["
					+ from + "], size [" + size + "].");
		return PageRequest.of(from > 0 ? from / size : 0, size, sort);
	}

	public static Pageable getPage(int from, int size) {
		if (from < 0 || size < 1)
			throw new BadRequestException("Illegal argument for pagination: from ["
					+ from + "], size [" + size + "].");
		return PageRequest.of(from > 0 ? from / size : 0, size);
	}

}
