package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Storage;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.user.UserAlreadyExistsException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final Storage<Integer, User> storage;

	@Override
	public User addUser(User user) throws UserAlreadyExistsException {
		if (user.getEmail() == null || user.getName() == null
				|| user.getEmail().isBlank() || user.getName().isBlank()) {
			log.warn("User not saved. User with empty name or email.");
			throw new BadRequestException("User with empty name or email.");
		}
		if (storage.containsData(user)) {
			log.warn("User not saved. User with the same email or name already exists.");
			throw new UserAlreadyExistsException("User with the same email or name already exists.");
		}
		return storage.add(user);
	}

	@Override
	public User updateUser(User user, int userId) throws UserNotFoundException {
		user.setId(userId);
		if (storage.containsData(user)) {
			log.warn("User not updated. User with the same email or name already exists.");
			throw new UserAlreadyExistsException("User with the same email or name already exists.");
		}
		if (!storage.containsKey(userId)) {
			log.warn("User not updated. User with id [" + userId + "] not exists.");
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
			log.warn("User not found. User with id [" + userId + "] not exists.");
			throw new UserNotFoundException("User with id [" + userId + "] not exists.");
		}
		return user.get();
	}

	@Override
	public User deleteUserById(int userId) throws UserNotFoundException {
		Optional<User> user = storage.deleteByKey(userId);
		if (user.isEmpty()) {
			log.warn("User not deleted. User with id [" + userId + "] not exists.");
			throw new UserNotFoundException("User with id [" + userId + "] not exists.");
		}
		return user.get();
	}
}
