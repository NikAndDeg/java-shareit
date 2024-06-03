package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {

	@EntityGraph(attributePaths = "owner")
	Optional<Item> findWithOwnerById(int id);

	List<Item> findItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIs(String infix1, String infix2, Boolean isAvailable);

	@EntityGraph(value = "item-bookings-owner-graph")
	List<Item> findWithBookingsWithOwnerByIdIn(List<Integer> ids);

	@EntityGraph(attributePaths = "bookings")
	List<Item> findWithBookingsAllByOwnerId(int id);
}