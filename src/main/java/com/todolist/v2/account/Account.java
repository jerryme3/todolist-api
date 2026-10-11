package com.todolist.v2.account;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "accounts")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(name = "created_at")
	private Instant createdAt;

	@Column(name = "updated_at")
	private Instant updatedAt;

	@Column(nullable = false, length = 100)
	private String password;

	public Account(String email, Instant createdAt, Instant updatedAt, String password) {
		this.email = email;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.password = password;
	}
}
