package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping
	public User addUser(@RequestBody @Valid User user) {
		log.info("Request to save user [{}].", user);
		User savedUser = userService.addUser(user);
		log.info("User [{}] saved.", savedUser);
		return savedUser;
	}

	@PatchMapping("/{userId}")
	public User updateUser(@RequestBody @Valid User user, @PathVariable int userId) {
		log.info("Request to update user [{}] with id [{}].", user, userId);
		User updatedUser = userService.updateUser(user, userId);
		log.info("User [{}] updated.", updatedUser);
		return updatedUser;
	}

	@GetMapping
	public List<User> getAllUsers() {
		log.info("Request to get all users.");
		List<User> users = userService.getAllUsers();
		log.info("All users received.");
		return users;
	}

	@GetMapping("/{userId}")
	public User getUserById(@PathVariable int userId) {
		log.info("Request to get user by id [{}]", userId);
		User user = userService.getUserById(userId);
		log.info("User [{}] found.", user);
		return user;
	}

	@DeleteMapping("/{userId}")
	public User deleteUserById(@PathVariable int userId) {
		log.info("Request to delete user by id [{}]", userId);
		User deletedUser = userService.deleteUserById(userId);
		log.info("User [{}] deleted.", deletedUser);
		return deletedUser;
	}
}
