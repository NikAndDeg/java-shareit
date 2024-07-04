package ru.practicum.shareit.user.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
//Не забыть аннотировать поля со связями @ToString.Exclude
@Entity
@Table(name = "users", schema = "public")
@NamedEntityGraph(
		name = "user-requests-items",
		attributeNodes = @NamedAttributeNode(value = "requests", subgraph = "requests-items"),
		subgraphs = @NamedSubgraph(name = "requests-items", attributeNodes = @NamedAttributeNode("items"))
)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Integer id;

	@Column(name = "user_name", length = 200, nullable = false, unique = true)
	private String name;

	@Column(name = "email", length = 200, nullable = false, unique = true)
	private String email;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@ToString.Exclude
	private Set<Item> items;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@ToString.Exclude
	private List<Booking> bookings;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@ToString.Exclude
	private List<Comment> comments;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@ToString.Exclude
	private List<ItemRequest> requests;

	//equals() и hashCode() подрезал отсюда
	//https://jpa-buddy.com/blog/hopefully-the-final-article-about-equals-and-hashcode-for-jpa-entities-with-db-generated-ids/
	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		User user = (User) o;
		return getId() != null && Objects.equals(getId(), user.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}
}
