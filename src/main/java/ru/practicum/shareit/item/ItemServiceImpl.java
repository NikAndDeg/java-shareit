package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.item.ItemNotFoundException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.exception.user.UserNotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;

	@Override
	public Item addItem(Item item, Integer ownerId) throws UserNotFoundException {
		if (item.getName() == null || item.getName().isBlank())
			throw new BadRequestException("Item not saved. Item with empty name.");
		if (item.getDescription() == null || item.getDescription().isBlank())
			throw new BadRequestException("Item not saved. Item with empty description.");
		if (item.getAvailable() == null)
			throw new BadRequestException("Item not saved. Item with empty available.");
		if (ownerId == null)
			throw new BadRequestException("Item not saved. Item with empty owner's id.");
		User owner = userRepository.findById(ownerId).orElseThrow(
				() -> new UserNotFoundException("Item not saved. Owner with id [" + ownerId + "] not exists.")
		);
		item.setOwner(owner);
		return itemRepository.save(item);
	}

	@Override
	public Item updateItem(Item dataToUpdate, Integer ownerId) throws ItemNotFoundException, UserNotOwnerException {
		Item updatableItem = itemRepository.findItemWithOwnerById(dataToUpdate.getId()).orElseThrow(
				() -> new ItemNotFoundException("Item not updated. Item with id [" + dataToUpdate.getId() + "] not found.")
		);
		if (!updatableItem.getOwner().getId().equals(ownerId))
			throw new UserNotOwnerException("Item not updated. User isn't owner of item.");
		if (dataToUpdate.getName() != null)
			updatableItem.setName(dataToUpdate.getName());
		if (dataToUpdate.getDescription() != null)
			updatableItem.setDescription(dataToUpdate.getDescription());
		if (dataToUpdate.getAvailable() != null)
			updatableItem.setAvailable(dataToUpdate.getAvailable());
		return itemRepository.save(updatableItem);
	}

	@Override
	public List<Item> getAllItemsByUserId(Integer userId) throws UserNotFoundException {
		User user = userRepository.findUserWithItemsById(userId).orElseThrow(
				() -> new UserNotFoundException("Items not received. User with id [" + userId + "] doesn't exist.")
		);
		return user.getItems();
	}

	@Override
	public Item getItemById(Integer itemId) throws ItemNotFoundException {
		return itemRepository.findById(itemId).orElseThrow(
				() -> new ItemNotFoundException("Item not found. Item with id [" + itemId + "] not exists.")
		);
	}

	@Override
	public Item deleteItemById(Integer itemId, Integer ownerId) throws ItemNotFoundException, UserNotOwnerException {
		Item removableItem = itemRepository.findItemWithOwnerById(itemId).orElseThrow(
				() -> new ItemNotFoundException("Item not deleted. Item with id [" + itemId + "] not found.")
		);
		if (!removableItem.getOwner().getId().equals(ownerId))
			throw new UserNotOwnerException("Item not deleted. User isn't owner of item.");
		itemRepository.deleteById(itemId);
		return removableItem;
	}

	@Override
	public List<Item> searchByText(String text) {
		if (text.isBlank())
			return new ArrayList<>();
		return itemRepository.findItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIs(text, text, true);
	}
}
