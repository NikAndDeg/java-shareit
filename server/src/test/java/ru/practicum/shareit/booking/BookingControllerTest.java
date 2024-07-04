package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MissingRequestHeaderException;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.booking.BookingUnsupportedStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
	private static final String HEADER_X_SHARER_USER_ID = "X-Sharer-User-Id";

	private final BookingDto dtoTo = BookingDto.builder()
			.itemId(1)
			.start(LocalDateTime.parse("2024-01-01T10:10:10"))
			.end(LocalDateTime.parse("2025-01-01T10:10:10"))
			.build();
	private final BookingDto dtoFrom = BookingDto.builder()
			.id(999)
			.build();

	@MockBean
	private BookingService bookingService;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@BeforeEach
	void setUp() {
		when(bookingService.addBooking(dtoTo, 1))
				.thenReturn(dtoFrom);

		when(bookingService.approveBooking(1, 2, true))
				.thenReturn(dtoFrom);

		when(bookingService.getBooking(3, 1))
				.thenReturn(dtoFrom);

		when(bookingService.getUserBookings(eq(1), eq(State.FUTURE), any()))
				.thenReturn(List.of(dtoFrom));

		when(bookingService.getUserBookings(eq(1), eq(State.ALL), any()))
				.thenReturn(List.of(dtoFrom, dtoFrom));

		when(bookingService.getOwnerBookings(eq(1), eq(State.ALL), any()))
				.thenReturn(List.of(dtoFrom));
	}

	@Test
	void testAddBooking() throws Exception {
		mvc.perform(post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.ALL_VALUE)
						.header(HEADER_X_SHARER_USER_ID, 1)
						.content(mapper.writeValueAsString(dtoTo))
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(dtoFrom.getId())));
	}

	@Test
	void testAddBooking_without_HEADER_X_SHARER_USER_ID() throws Exception {
		mvc.perform(post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.ALL_VALUE)
						.content(mapper.writeValueAsString(dtoTo))
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(MissingRequestHeaderException.class, result.getResolvedException().getClass()));
	}

	@Test
	void testApproveBooking() throws Exception {
		mvc.perform(patch("/bookings/2?approved=true")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.ALL_VALUE)
						.header(HEADER_X_SHARER_USER_ID, 1)
						.content(mapper.writeValueAsString(dtoTo))
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(dtoFrom.getId())));
	}

	@Test
	void testGetBooking() throws Exception {
		mvc.perform(get("/bookings/3")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.ALL_VALUE)
						.header(HEADER_X_SHARER_USER_ID, 1)
						.content(mapper.writeValueAsString(dtoTo))
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(dtoFrom.getId())));
	}

	@Test
	void testGetBookings() throws Exception {
		mvc.perform(get("/bookings?state=FUTURE&from=0&size=10")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.ALL_VALUE)
						.header(HEADER_X_SHARER_USER_ID, 1)
						.content(mapper.writeValueAsString(dtoTo))
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(dtoFrom.getId())));
	}

	@Test
	void testGetBookings_with_no_state() throws Exception {
		mvc.perform(get("/bookings?state=&from=0&size=10")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.ALL_VALUE)
						.header(HEADER_X_SHARER_USER_ID, 1)
						.content(mapper.writeValueAsString(dtoTo))
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[1].id", is(dtoFrom.getId())));
	}

	@Test
	void testGetBookings_invalid_state() throws Exception {
		mvc.perform(get("/bookings?state=QWERTY&from=0&size=10")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.ALL_VALUE)
						.header(HEADER_X_SHARER_USER_ID, 1)
						.content(mapper.writeValueAsString(dtoTo))
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(BookingUnsupportedStatus.class, result.getResolvedException().getClass()));
	}

	@Test
	void testGetBookings_invalid_from() throws Exception {
		mvc.perform(get("/bookings?state=FUTURE&from=-1&size=10")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.ALL_VALUE)
						.header(HEADER_X_SHARER_USER_ID, 1)
						.content(mapper.writeValueAsString(dtoTo))
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(BadRequestException.class, result.getResolvedException().getClass()));
	}

	@Test
	void testGetBookings_invalid_size() throws Exception {
		mvc.perform(get("/bookings?state=FUTURE&from=0&size=0")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.ALL_VALUE)
						.header(HEADER_X_SHARER_USER_ID, 1)
						.content(mapper.writeValueAsString(dtoTo))
				)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertEquals(BadRequestException.class, result.getResolvedException().getClass()));
	}

	@Test
	void testGetOwnerBookings() throws Exception {
		mvc.perform(get("/bookings/owner?from=0&size=10")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.ALL_VALUE)
						.header(HEADER_X_SHARER_USER_ID, 1)
						.content(mapper.writeValueAsString(dtoTo))
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(dtoFrom.getId())));
	}
}