package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

	List<User> findByIdOrNameOrEmail(int id, String name, String email);

	@EntityGraph(attributePaths = "items")
	Optional<User> findWithItemsById(int id);

	@EntityGraph(value = "user-requests-items")
	Optional<User> findWithRequestsWithItemsById(int id);
}