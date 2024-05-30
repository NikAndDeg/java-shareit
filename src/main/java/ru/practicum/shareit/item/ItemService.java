package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemBookingsCommentsDto;
import ru.practicum.shareit.item.model.dto.ItemDto;

import java.util.List;

public interface ItemService {

	ItemDto addItem(ItemDto itemDto, int userId);

	ItemDto updateItem(ItemDto itemDto, int ownerId);

	List<ItemBookingsCommentsDto> getAllItemsByUserId(Integer userId);

	ItemBookingsCommentsDto getItemById(int itemId, int userId);

	ItemDto deleteItemById(int itemId, int userId);

	List<ItemDto> searchByText(String text);

	CommentDto addComment(int itemId, int userId, CommentDto commentDto);
}
