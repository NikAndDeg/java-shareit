package ru.practicum.shareit;

public interface DtoMapper<D, M> {

	D toDto(M model);

	M toModel(D dto, String... ars);
}
