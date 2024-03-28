package ru.practicum.shareit.item;

import ru.practicum.shareit.excaption.item.ItemNotFoundException;
import ru.practicum.shareit.excaption.user.UserNotFoundException;
import ru.practicum.shareit.excaption.user.UserNotOwnerException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
	Item addItem(Item item) throws UserNotFoundException;
	Item updateItem(Item item, int itemId) throws ItemNotFoundException, UserNotOwnerException;
	List<Item> getAllItemsByUserId(int userId) throws UserNotFoundException;
	Item getItemById(int itemId) throws ItemNotFoundException;
	Item deleteItemById(int itemId, int userId) throws ItemNotFoundException, UserNotOwnerException;
	List<Item> searchByText(String text);
}
