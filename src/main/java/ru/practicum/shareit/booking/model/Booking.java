package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
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
	private Item item;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@ToString.Exclude
	private User user;

	@Column(name = "booking_start", nullable = false)
	private LocalDateTime start;

	@Column(name = "booking_end", nullable = false)
	private LocalDateTime end;

	//equals() и hashCode() подрезал отсюда
	//https://jpa-buddy.com/blog/hopefully-the-final-article-about-equals-and-hashcode-for-jpa-entities-with-db-generated-ids/
	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Booking booking = (Booking) o;
		return getId() != null && Objects.equals(getId(), booking.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}
}
