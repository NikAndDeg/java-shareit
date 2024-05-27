package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

	List<User> findByNameOrEmail(String name, String email);

	List<User> findByIdOrNameOrEmail(int id, String name, String email);

	@EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = "items")
	Optional<User> findUserWithItemsById(int id);
}