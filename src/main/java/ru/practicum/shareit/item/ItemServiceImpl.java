package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Storage;
import ru.practicum.shareit.exception.item.ItemNotFoundException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.exception.user.UserNotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
	private final Storage<Integer, User> userStorage;
	private final ItemStorage itemStorage;

	@Autowired
	public ItemServiceImpl(Storage<Integer, User> userStorage,
						   ItemStorage itemStorage) {
		this.userStorage = userStorage;
		this.itemStorage = itemStorage;
	}

	@Override
	public Item addItem(Item item) throws UserNotFoundException {
		int userId = item.getOwnerId();
		if (!userStorage.containsKey(userId)) {
			log.info("Item not saved. User with id [{}] not found.", userId);
			throw new UserNotFoundException("User with id [" + userId + "] doesn't exist.");
		}
		return itemStorage.add(item);
	}

	@Override
	public Item updateItem(Item item, int itemId) throws ItemNotFoundException, UserNotOwnerException {
		item.setId(itemId);
		int userId = item.getOwnerId();
		if (!itemStorage.isOwner(itemId, userId)) {
			log.info("Item not updated. User with id [{}] not owner item with id [{}].", userId, itemId);
			throw new UserNotOwnerException("User not owner item.");
		}
		if (!itemStorage.containsKey(itemId)) {
			log.info("Item not updated. Item with id [{}] not exists.", itemId);
			throw new ItemNotFoundException("Item with id [" + itemId + "] npt found.");
		}
		return itemStorage.update(item);
	}

	@Override
	public List<Item> getAllItemsByUserId(int userId) throws UserNotFoundException {
		if (!userStorage.containsKey(userId)) {
			log.info("Items not received. User with id [{}] not found.", userId);
			throw new UserNotFoundException("User with id [" + userId + "] doesn't exist.");
		}
		return itemStorage.getAllByUserId(userId);
	}

	@Override
	public Item getItemById(int itemId) throws ItemNotFoundException {
		Optional<Item> item = itemStorage.get(itemId);
		if (item.isEmpty()) {
			log.info("Item not found. Item with id [{}] not exists.", itemId);
			throw new ItemNotFoundException("Item with id [" + itemId + "] npt found.");
		}
		return item.get();
	}

	@Override
	public Item deleteItemById(int itemId, int userId) throws ItemNotFoundException, UserNotOwnerException {
		if (!itemStorage.isOwner(itemId, userId)) {
			log.info("Item not deleted. User with id [{}] not owner item with id [{}].", userId, itemId);
			throw new UserNotOwnerException("User not owner item.");
		}
		Optional<Item> item = itemStorage.deleteByKey(itemId);
		if (item.isEmpty()) {
			log.info("Item not deleted. Item with id [{}] not exists.", itemId);
			throw new ItemNotFoundException("Item with id [" + itemId + "] npt found.");
		}
		return item.get();
	}

	@Override
	public List<Item> searchByText(String text) {
		if (text.isBlank())
			return new ArrayList<>();
		return itemStorage.searchByText(text);
	}
}
