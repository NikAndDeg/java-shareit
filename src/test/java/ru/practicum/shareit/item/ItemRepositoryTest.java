package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static ru.practicum.shareit.DataForTests.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql("/test_schema.sql")
class ItemRepositoryTest {
	@Autowired
	ItemRepository repository;

	@Test
	void get_item_by_id_with_user() {
		Item item = repository.findItemWithOwnerById(1).get();
		Item expectedItem = savedItems.get(0);
		User expectedUser = savedUsers.get(3);
		assertEquals(expectedItem, item);
		assertEquals(expectedUser, item.getOwner());
	}
}