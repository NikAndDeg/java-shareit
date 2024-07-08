package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemBookingsCommentsDto;
import ru.practicum.shareit.item.model.dto.ItemDto;

import java.util.List;

public interface ItemService {

	ItemDto addItem(ItemDto itemDto, int userId);

	ItemDto updateItem(ItemDto itemDto, int ownerId);

	List<ItemBookingsCommentsDto> getAllItemsByUserId(int userId, Pageable pageable);

	ItemBookingsCommentsDto getItemById(int itemId, int userId);

	ItemDto deleteItemById(int itemId, int userId);

	List<ItemDto> searchByText(String text, Pageable pageable);

	CommentDto addComment(int itemId, int userId, CommentDto commentDto);
}
