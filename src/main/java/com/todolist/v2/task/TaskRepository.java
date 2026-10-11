package com.todolist.v2.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

	@Query(value = "SELECT t FROM Task t JOIN FETCH t.user u WHERE u.id = :userId",
			countQuery = "SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId")
	Page<Task> findAll(@Param("userId") Long userId, Pageable pageable);

	Optional<Task> findByIdAndUser_Id(Long taskId, Long userId);

	@Transactional
	@Modifying
	@Query("DELETE FROM Task t WHERE t.id = :taskId AND t.user.id = :userId")
	int deleteTask(@Param("userId") Long userId, @Param("taskId") Long taskId);

}
