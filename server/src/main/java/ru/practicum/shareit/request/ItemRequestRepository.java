package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

	@EntityGraph(attributePaths = {"requester", "items"})
	Optional<ItemRequest> findWithRequesterAndItemsById(int requestId);

	@EntityGraph(attributePaths = "items")
	List<ItemRequest> findWithItemsAllByRequesterId(int requesterId);

	@EntityGraph(attributePaths = {"requester", "items"})
	List<ItemRequest> findWithRequesterAndItemsAllByRequesterIdNot(Integer requesterId, Pageable pageable);
}
