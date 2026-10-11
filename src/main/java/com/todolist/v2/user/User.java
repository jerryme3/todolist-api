package com.todolist.v2.user;

import com.todolist.v2.account.Account;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Table(name = "users")
@NoArgsConstructor
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_name", nullable = false, unique = true)
	private String userName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;

	@Column(name = "created_at")
	private Instant createdAt;

	@Column(name = "updated_at")
	private Instant updatedAt;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, unique = true)
	private Account account;

	public User(String userName, UserRole role, Instant createdAt, Instant updatedAt, Account account) {
        this.userName = userName;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.account = account;
    }

    public String getEmail() {
		return this.account.getEmail();
	}

}
