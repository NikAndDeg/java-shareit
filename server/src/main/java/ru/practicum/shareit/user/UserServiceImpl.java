package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.user.UserAlreadyExistsException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;

	@Override
	@Transactional
	public UserDto addUser(UserDto userDtoToSave) {
		User userToSave = UserDto.toModel(userDtoToSave);
		try {
			User savedUser = userRepository.save(userToSave);
			return UserDto.toDto(savedUser);
		} catch (DataIntegrityViolationException exception) {
			throw new UserAlreadyExistsException("User not saved. User with same name or email already exists.");
		}
	}

	@Override
	@Transactional
	public UserDto updateUser(UserDto userDtoToUpdate, int userId) {
		User userToUpdate = UserDto.toModel(userDtoToUpdate);
		List<User> existingUsers = userRepository.findByIdOrNameOrEmail(userId, userToUpdate.getName(), userToUpdate.getEmail());
		User updatableUser = getUpdatableUser(userToUpdate, userId, existingUsers).orElseThrow(
				() -> new UserNotFoundException("User not updated. User with id [" + userId + "] not exists.")
		);
		if (userToUpdate.getName() != null)
			updatableUser.setName(userToUpdate.getName());
		if (userToUpdate.getEmail() != null)
			updatableUser.setEmail(userToUpdate.getEmail());
		return UserDto.toDto(userRepository.save(updatableUser));
	}

	@Override
	public List<UserDto> getAllUsers() {
		return userRepository.findAll().stream()
				.map(UserDto::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public UserDto getUserById(int userId) {
		User user = userRepository.findById(userId).orElseThrow(
				() -> new UserNotFoundException("User with id [" + userId + "] not exists.")
		);
		return UserDto.toDto(user);
	}

	@Override
	@Transactional
	public UserDto deleteUserById(int userId) {
		User userToDelete = userRepository.findById(userId).orElseThrow(
				() -> new UserNotFoundException("User with id [" + userId + "] not exists.")
		);
		userRepository.deleteById(userId);
		return UserDto.toDto(userToDelete);
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
