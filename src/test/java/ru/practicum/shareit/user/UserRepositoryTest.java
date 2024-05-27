package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.DataForTests.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql("/test_schema.sql")
class UserRepositoryTest {
	@Autowired
	private UserRepository repository;

	@Test
	void get_user_by_id_1_from_DB() {
		Optional<User> user = repository.findById(1);
		assertTrue(user.isPresent());
		assertEquals(savedUsers.get(0), user.get());
	}

	@Test
	void get_users_by_ids_1_2_3_4_from_DB() {
		List<User> users = repository.findAllById(List.of(1, 2, 3, 4));
		users.forEach(user -> assertTrue(savedUsers.contains(user)));
	}

	@Test
	void save_new_user() {
		User userToSave = createUser(null, "new user", "newuser@mail.com");
		User savedUser = repository.save(userToSave);
		User expectedUser = createUser(5, "new user", "newuser@mail.com");
		assertEquals(expectedUser, savedUser);
	}

	@Test
	void update_user() {
		User userToUpdate = repository.findById(1).get();
		userToUpdate.setName("updated");
		User updatedUser = repository.save(userToUpdate);
		User expectedUser = createUser(1, "updated", "1@email.com");
		assertEquals(updatedUser, expectedUser);
	}

	@Test
	void get_user_by_name_or_email() {
		List<User> users = repository.findByNameOrEmail("1", "2@email.com");
		assertEquals(savedUsers.get(0), users.get(0));
		assertEquals(savedUsers.get(1), users.get(1));
	}

	@Test
	void get_user_by_id_or_name_or_email() {
		List<User> users = repository.findByIdOrNameOrEmail(1, "2", "3@email.com");
		assertEquals(savedUsers.get(0), users.get(0));
		assertEquals(savedUsers.get(1), users.get(1));
		assertEquals(savedUsers.get(2), users.get(2));
	}

	@Test
	void get_user_by_id_with_items() {
		User expectedUser = savedUsers.get(3);
		List<Item> expectedItems = List.of(
				savedItems.get(0),
				savedItems.get(2)
		);
		User user = repository.findUserWithItemsById(4).get();
		List<Item> items = user.getItems();
		assertEquals(expectedUser, user);
		assertEquals(expectedItems.get(0), items.get(0));
		assertEquals(expectedItems.get(1), items.get(1));
	}
}