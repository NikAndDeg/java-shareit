package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.Storage;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class FakeUserStorage implements Storage<Integer, User> {
	private final Map<Integer, User> users = new HashMap<>();

	private int counter = 0;

	@Override
	public User add(User user) {
		user.setId(++counter);
		users.put(user.getId(), user);
		return user;
	}

	@Override
	public Optional<User> get(Integer id) {
		return Optional.ofNullable(users.get(id));
	}

	@Override
	public List<User> getByKeys(List<Integer> ids) {
		List<User> userList = new ArrayList<>();
		users.forEach(
				(id, user) -> {
					if (ids.contains(id))
						userList.add(user);
				}
		);
		return userList;
	}

	@Override
	public List<User> getAll() {
		return new ArrayList<>(users.values());
	}

	@Override
	public User update(User updatedUser) {
		User oldUser = users.get(updatedUser.getId());
		if (updatedUser.getName() == null)
			updatedUser.setName(oldUser.getName());
		if (updatedUser.getEmail() == null)
			updatedUser.setEmail(oldUser.getEmail());
		users.put(updatedUser.getId(), updatedUser);
		return updatedUser;
	}

	@Override
	public Optional<User> deleteByData(User user) {
		return Optional.ofNullable(users.remove(user.getId()));
	}

	@Override
	public Optional<User> deleteByKey(Integer id) {
		return Optional.ofNullable(users.remove(id));
	}

	@Override
	public boolean containsData(User user) {
		return users.values().stream()
				.anyMatch(
						u -> (u.getEmail().equals(user.getEmail()) || u.getName().equals(user.getName()))
								&& u.getId() != user.getId()
				);
	}

	@Override
	public boolean containsKey(Integer id) {
		return users.containsKey(id);
	}
}
