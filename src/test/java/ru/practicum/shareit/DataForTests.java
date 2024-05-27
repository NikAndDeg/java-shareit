package ru.practicum.shareit;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class DataForTests {
	public static final List<User> savedUsers = List.of(
			createUser(1, "1", "1@email.com"),
			createUser(2, "2", "2@email.com"),
			createUser(3, "3", "3@email.com"),
			createUser(4, "4", "4@email.com")
	);

	public static final List<Item> savedItems = List.of(
			createItem(1, "1", "description", true, savedUsers.get(3)),
			createItem(2, "2", "description", false, savedUsers.get(1)),
			createItem(3, "3", "description", false, savedUsers.get(3))
	);

	public static User createUser(Integer id, String name, String email) {
		User user = new User();
		user.setId(id);
		user.setName(name);
		user.setEmail(email);
		return user;
	}

	public static Item createItem(Integer id, String name, String description, Boolean available, User owner) {
		Item item = new Item();
		item.setId(id);
		item.setName(name);
		item.setDescription(description);
		item.setAvailable(available);
		item.setOwner(owner);
		return item;
	}
}
