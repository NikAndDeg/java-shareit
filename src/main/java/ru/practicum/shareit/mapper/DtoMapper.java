package ru.practicum.shareit.mapper;

public interface DtoMapper<D, M> {

	D toDto(M model, String... args);

	M toModel(D dto, String... args);
}
