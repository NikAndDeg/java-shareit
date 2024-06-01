package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping
	public UserDto addUser(@RequestBody @Valid UserDto userDto) {
		log.info("Request to save user [{}].", userDto);
		User user = UserDto.toModel(userDto);
		User savedUser = userService.addUser(user);
		log.info("User [{}] saved.", savedUser);
		return UserDto.toDto(savedUser);
	}

	@PatchMapping("/{userId}")
	public UserDto updateUser(@RequestBody @Valid UserDto userDto, @PathVariable int userId) {
		log.info("Request to update user [{}] with id [{}].", userDto, userId);
		User user = UserDto.toModel(userDto);
		User updatedUser = userService.updateUser(user, userId);
		log.info("User [{}] updated.", updatedUser);
		return UserDto.toDto(updatedUser);
	}

	@GetMapping
	public List<UserDto> getAllUsers() {
		log.info("Request to get all users.");
		List<User> users = userService.getAllUsers();
		log.info("All users received.");
		return users.stream()
				.map(UserDto::toDto)
				.collect(Collectors.toList());
	}

	@GetMapping("/{userId}")
	public UserDto getUserById(@PathVariable int userId) {
		log.info("Request to get user by id [{}]", userId);
		User user = userService.getUserById(userId);
		log.info("User [{}] found.", user);
		return UserDto.toDto(user);
	}

	@DeleteMapping("/{userId}")
	public UserDto deleteUserById(@PathVariable int userId) {
		log.info("Request to delete user by id [{}]", userId);
		User deletedUser = userService.deleteUserById(userId);
		log.info("User [{}] deleted.", deletedUser);
		return UserDto.toDto(deletedUser);
	}
}
