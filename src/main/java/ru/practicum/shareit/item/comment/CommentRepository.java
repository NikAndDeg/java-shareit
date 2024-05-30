package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
	@EntityGraph(attributePaths = "user")
	List<Comment> findWithCommenterAllByItemId(Integer itemId);

	@EntityGraph(attributePaths = {"user", "item"})
	List<Comment> findWithItemWithCommenterAllByItemIdIn(List<Integer> itemsIds);
}
