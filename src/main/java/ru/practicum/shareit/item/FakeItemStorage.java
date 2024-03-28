package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FakeItemStorage implements ItemStorage {
	private final Map<Integer, Item> items = new HashMap<>();

	private int counter = 0;

	@Override
	public Item add(Item item) {
		item.setId(++counter);
		items.put(item.getId(), item);
		return item;
	}

	@Override
	public Optional<Item> get(Integer id) {
		return Optional.ofNullable(items.get(id));
	}

	@Override
	public List<Item> getByKeys(List<Integer> IDs) {
		List<Item> itemList = new ArrayList<>();
		items.forEach(
				(id, item) -> {
					if (IDs.contains(id))
						itemList.add(item);
				}
		);
		return itemList;
	}

	@Override
	public List<Item> getAll() {
		return new ArrayList<>(items.values());
	}

	@Override
	public Item update(Item updatedItem) {
		Item oldItem = items.get(updatedItem.getId());
		if (updatedItem.getName() == null)
			updatedItem.setName(oldItem.getName());
		if (updatedItem.getDescription() == null)
			updatedItem.setDescription(oldItem.getDescription());
		if (updatedItem.getAvailable() == null)
			updatedItem.setAvailable(oldItem.getAvailable());
		updatedItem.setOwnerId(oldItem.getOwnerId());
		updatedItem.setRequestId(oldItem.getRequestId());
		updatedItem.setBookingCounter(oldItem.getBookingCounter());
		items.put(updatedItem.getId(), updatedItem);
		return updatedItem;
	}

	@Override
	public Optional<Item> deleteByData(Item item) {
		return Optional.ofNullable(items.remove(item.getId()));
	}

	@Override
	public Optional<Item> deleteByKey(Integer id) {
		return Optional.ofNullable(items.remove(id));
	}

	@Override
	public boolean containsData(Item item) {
		return items.values().stream()
				.anyMatch(
						i -> (i.getName().equals(item.getName()) || i.getDescription().equals(item.getDescription()))
								&& i.getId() != item.getId()
				);
	}

	@Override
	public boolean containsKey(Integer id) {
		return items.containsKey(id);
	}

	@Override
	public List<Item> searchByText(String text) {
		String lowCaseText = text.toLowerCase();
		List<Item> itemList = new ArrayList<>();
		items.values().forEach(
				item -> {
					if (
							(item.getName().toLowerCase().contains(lowCaseText)
							|| item.getDescription().toLowerCase().contains(lowCaseText))
									&& item.getAvailable()
					)
						itemList.add(item);
				}
		);
		return itemList;
	}

	@Override
	public boolean isOwner(int itemId, int userId) {
		Item item = items.get(itemId);
		if (item == null)
			return false;
		return item.getOwnerId() == userId;
	}

	@Override
	public List<Item> getAllByUserId(int userId) {
		return items.values().stream()
				.filter(item -> item.getOwnerId() == userId)
				.collect(Collectors.toList());
	}
}
