package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {

	@EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = "owner")
	Optional<Item> findItemWithOwnerById(int id);

	List<Item> findItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIs(String infix1, String infix2, Boolean isAvailable);
}