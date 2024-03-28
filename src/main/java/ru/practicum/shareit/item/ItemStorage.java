package ru.practicum.shareit.item;

import ru.practicum.shareit.Storage;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage extends Storage<Integer, Item> {
	List<Item> searchByText(String text);
	boolean isOwner(int itemId, int userId);
	List<Item> getAllByUserId(int userId);
}
