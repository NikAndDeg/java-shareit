package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@ToString
//Не забыть аннотировать поля со связями @ToString.Exclude
@Entity
@Table(name = "bookings", schema = "public")
@NamedEntityGraph(
		name = "booking-requester-item-owner-graph",
		attributeNodes = {
				@NamedAttributeNode("user"),
				@NamedAttributeNode(value = "item", subgraph = "item-owner-graph")
		},
		subgraphs = @NamedSubgraph(name = "item-owner-graph", attributeNodes = @NamedAttributeNode("owner"))
)
public class Booking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "booking_id")
	private Integer id;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "booking_status_id")
	private BookingStatus status;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Item item;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private User user;

	@Column(name = "booking_start", nullable = false)
	private LocalDateTime start;

	@Column(name = "booking_end", nullable = false)
	private LocalDateTime end;
}
