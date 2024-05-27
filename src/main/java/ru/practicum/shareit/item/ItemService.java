package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.item.ItemNotFoundException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.exception.user.UserNotOwnerException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

	Item addItem(Item item, Integer userId) throws UserNotFoundException;

	Item updateItem(Item dataToUpdate, Integer ownerId) throws ItemNotFoundException, UserNotOwnerException;

	List<Item> getAllItemsByUserId(Integer userId) throws UserNotFoundException;

	Item getItemById(Integer itemId) throws ItemNotFoundException;

	Item deleteItemById(Integer itemId, Integer userId) throws ItemNotFoundException, UserNotOwnerException;

	List<Item> searchByText(String text);
}
