package com.todolist.v2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ComTodolistV2Application {

	static void main(String[] args) {
		SpringApplication.run(ComTodolistV2Application.class, args);
	}

}
