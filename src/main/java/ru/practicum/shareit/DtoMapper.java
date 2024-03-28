package ru.practicum.shareit;

public interface DtoMapper<Dto, Model> {
	Dto toDto(Model model);
	Model toModel(Dto dto, String... ars);
}
