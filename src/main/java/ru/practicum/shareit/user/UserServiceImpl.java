package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Storage;
import ru.practicum.shareit.excaption.user.UserAlreadyExistsException;
import ru.practicum.shareit.excaption.user.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
	private final Storage<Integer, User> storage;

	@Autowired
	public UserServiceImpl(Storage<Integer, User> storage) {
		this.storage = storage;
	}

	@Override
	public User addUser(User user) throws UserAlreadyExistsException {
		if (storage.containsData(user)) {
			log.info("User not saved. User with the same email or name already exists.");
			throw new UserAlreadyExistsException("User with the same email or name already exists.");
		}
		return storage.add(user);
	}

	@Override
	public User updateUser(User user, int userId) throws UserNotFoundException {
		user.setId(userId);
		if (storage.containsData(user)) {
			log.info("User not updated. User with the same email or name already exists.");
			throw new UserAlreadyExistsException("User with the same email or name already exists.");
		}
		if (!storage.containsKey(userId)) {
			log.info("User not updated. User with id [" + userId + "] not exists.");
			throw new UserNotFoundException("User with id [" + userId + "] not exists.");
		}
		return storage.update(user);
	}

	@Override
	public List<User> getAllUsers() {
		return storage.getAll();
	}

	@Override
	public User getUserById(int userId) throws UserNotFoundException {
		Optional<User> user = storage.get(userId);
		if (user.isEmpty()) {
			log.info("User not found. User with id [" + userId + "] not exists.");
			throw new UserNotFoundException("User with id [" + userId + "] not exists.");
		}
		return user.get();
	}

	@Override
	public User deleteUserById(int userId) throws UserNotFoundException {
		Optional<User> user = storage.deleteByKey(userId);
		if (user.isEmpty()) {
			log.info("User not deleted. User with id [" + userId + "] not exists.");
			throw new UserNotFoundException("User with id [" + userId + "] not exists.");
		}
		return user.get();
	}
}
