package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

	private final UserRepository repository;

	@Override
	public User addUser(User userToSave) throws UserAlreadyExistsException, BadRequestException {
		if (userToSave.getName() == null || userToSave.getName().isBlank())
			throw new BadRequestException("User not saved. User with empty name.");
		if (userToSave.getEmail() == null || userToSave.getEmail().isBlank())
			throw new BadRequestException("User not saved. User with empty email.");
		List<User> existingUsers = repository.findByNameOrEmail(userToSave.getName(), userToSave.getEmail());
		if (!existingUsers.isEmpty())
			throw new UserAlreadyExistsException("User not saved. User with same name or email already exists.");
		return repository.save(userToSave);
	}

	@Override
	public User updateUser(User dataToUpdate, int userId) throws UserNotFoundException, UserAlreadyExistsException {
		List<User> existingUsers = repository.findByIdOrNameOrEmail(userId, dataToUpdate.getName(), dataToUpdate.getEmail());
		User updatableUser = getUpdatableUser(dataToUpdate, userId, existingUsers).orElseThrow(
				() -> new UserNotFoundException("User not updated. User with id [" + userId + "] not exists.")
		);
		if (dataToUpdate.getName() != null)
			updatableUser.setName(dataToUpdate.getName());
		if (dataToUpdate.getEmail() != null)
			updatableUser.setEmail(dataToUpdate.getEmail());
		return repository.save(updatableUser);
	}

	@Override
	public List<User> getAllUsers() {
		return repository.findAll();
	}

	@Override
	public User getUserById(int userId) throws UserNotFoundException {
		return repository.findById(userId).orElseThrow(
				() -> new UserNotFoundException("User with id [" + userId + "] not exists.")
		);
	}

	@Override
	public User deleteUserById(int userId) throws UserNotFoundException {
		User userToDelete = repository.findById(userId).orElseThrow(
				() -> new UserNotFoundException("User with id [" + userId + "] not exists.")
		);
		repository.deleteById(userId);
		return userToDelete;
	}

	private Optional<User> getUpdatableUser(User dataToUpdate, int userId, List<User> existingUsers) {
		User updatableUser = null;
		for (User existingUser : existingUsers) {
			if (!existingUser.getId().equals(userId)) {
				if (existingUser.getName().equals(dataToUpdate.getName()))
					throw new UserAlreadyExistsException("User not updated. User with the name [" + dataToUpdate.getName()
							+ "] already exists.");
				if (existingUser.getEmail().equals(dataToUpdate.getEmail()))
					throw new UserAlreadyExistsException("User not updated. User with the email [" + dataToUpdate.getEmail()
							+ "] already exists.");
			} else {
				updatableUser = existingUser;
			}
		}
		return Optional.ofNullable(updatableUser);
	}
}
