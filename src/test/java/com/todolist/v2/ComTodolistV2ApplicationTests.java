package com.todolist.v2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.jwt.secret=local-test-secret-with-at-least-32-bytes")
class ComTodolistV2ApplicationTests {

	@Test
	void contextLoads() {
	}

}
