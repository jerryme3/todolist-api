package com.todolist.v2.user;

import com.todolist.v2.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUserName(String userName);
	Optional<User> findByAccount(Account account);
	boolean existsByUserName(String userName);



}
