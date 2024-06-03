package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

	@EntityGraph(value = "booking-requester-item-owner-graph")
	Optional<Booking> findWithRequesterAndItemAndOwnerOfItemById(Integer id);

	@EntityGraph(attributePaths = {"item", "user"})
	List<Booking> findAllByUserId(Integer userId);

	@EntityGraph(attributePaths = {"item", "user"})
	List<Booking> findAllByUserIdAndStartBeforeAndEndAfter(Integer userId,
														   LocalDateTime startTimeBefore,
														   LocalDateTime endTimeAfter);

	@EntityGraph(attributePaths = {"item", "user"})
	List<Booking> findWithItemAndUserAllByUserIdAndEndBefore(Integer userId, LocalDateTime endTimeBefore);

	List<Booking> findAllByUserIdAndItemIdAndStatusAndEndBefore(Integer userId,
																Integer itemId,
																BookingStatus status,
																LocalDateTime endTimeBefore);

	@EntityGraph(attributePaths = {"item", "user"})
	List<Booking> findAllByUserIdAndStartAfter(Integer userId, LocalDateTime startTimeAfter);

	@EntityGraph(attributePaths = {"item", "user"})
	List<Booking> findAllByUserIdAndStatusIs(Integer userId, BookingStatus status);

	@Query("SELECT b FROM Booking b " +
			"JOIN b.item i " +
			"JOIN i.owner o " +
			"WHERE o.id = :ownerId ")
	@EntityGraph(attributePaths = {"item", "user"})
	List<Booking> findAllByOwnerId(Integer ownerId);

	@Query("SELECT b FROM Booking b " +
			"JOIN b.item i " +
			"JOIN i.owner o " +
			"WHERE o.id = :ownerId " +
			"AND b.start < :startTimeBefore " +
			"AND b.end > :endTimeAfter")
	@EntityGraph(attributePaths = {"item", "user"})
	List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfter(Integer ownerId,
															LocalDateTime startTimeBefore,
															LocalDateTime endTimeAfter);

	@Query("SELECT b FROM Booking b " +
			"JOIN b.item i " +
			"JOIN i.owner o " +
			"WHERE o.id = :ownerId " +
			"AND b.end < :endTimeBefore")
	@EntityGraph(attributePaths = {"item", "user"})
	List<Booking> findAllByOwnerIdAndEndBefore(Integer ownerId, LocalDateTime endTimeBefore);

	@Query("SELECT b FROM Booking b " +
			"JOIN b.item i " +
			"JOIN i.owner o " +
			"WHERE o.id = :ownerId " +
			"AND b.start > :startTimeAfter")
	@EntityGraph(attributePaths = {"item", "user"})
	List<Booking> findAllByOwnerIdAndStartAfter(Integer ownerId, LocalDateTime startTimeAfter);

	@Query("SELECT b FROM Booking b " +
			"JOIN b.item i " +
			"JOIN i.owner o " +
			"WHERE o.id = :ownerId " +
			"AND b.status = :status")
	@EntityGraph(attributePaths = {"item", "user"})
	List<Booking> findAllByOwnerIdAndStatusIs(Integer ownerId, BookingStatus status);

	@EntityGraph(attributePaths = "user")
	List<Booking> findWithBookerAllByItemIdAndStatusIn(Integer itemId, List<BookingStatus> statuses);
}
