package ru.practicum.shareit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ShareItAppTest {

	@Test
	void testMain() {
		assertDoesNotThrow(ShareItApp::new);
		assertDoesNotThrow(() -> ShareItApp.main(new String[]{}));
	}
}