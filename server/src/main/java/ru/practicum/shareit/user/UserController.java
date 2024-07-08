package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping
	public UserDto addUser(@RequestBody UserDto userDto) {
		log.info("Request to save user [{}].", userDto);
		UserDto savedUser = userService.addUser(userDto);
		log.info("User [{}] saved.", savedUser);
		return savedUser;
	}

	@PatchMapping("/{userId}")
	public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable int userId) {
		log.info("Request to update user [{}] with id [{}].", userDto, userId);
		UserDto updatedUser = userService.updateUser(userDto, userId);
		log.info("User [{}] updated.", updatedUser);
		return updatedUser;
	}

	@GetMapping
	public List<UserDto> getAllUsers() {
		log.info("Request to get all users.");
		List<UserDto> users = userService.getAllUsers();
		log.info("All users received.");
		return users;
	}

	@GetMapping("/{userId}")
	public UserDto getUserById(@PathVariable int userId) {
		log.info("Request to get user by id [{}]", userId);
		UserDto user = userService.getUserById(userId);
		log.info("User [{}] found.", user);
		return user;
	}

	@DeleteMapping("/{userId}")
	public UserDto deleteUserById(@PathVariable int userId) {
		log.info("Request to delete user by id [{}]", userId);
		UserDto deletedUser = userService.deleteUserById(userId);
		log.info("User [{}] deleted.", deletedUser);
		return deletedUser;
	}
}
