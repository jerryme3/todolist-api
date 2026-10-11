package com.todolist.v2.task;

import com.todolist.v2.exception.ResourceNotFoundException;
import com.todolist.v2.task.dto.DeletionRequest;
import com.todolist.v2.task.dto.UpdateTaskRequest;
import com.todolist.v2.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceOwnershipTest {

	@Mock
	private TaskRepository taskRepository;

	@Mock
	private UserRepository userRepository;

	@Test
	void updateLoadsOnlyATaskOwnedByTheJwtUser() {
		when(taskRepository.findByIdAndUser_Id(7L, 42L)).thenReturn(Optional.empty());
		var service = new TaskService(taskRepository, userRepository);

		assertThrows(ResourceNotFoundException.class,
				() -> service.updateTaskName(7L, 42L, new UpdateTaskRequest<>("changed")));

		verify(taskRepository).findByIdAndUser_Id(7L, 42L);
		verify(taskRepository, never()).findById(7L);
		verify(taskRepository, never()).save(any());
		verify(userRepository, never()).existsById(42L);
	}

	@Test
	void deleteIsScopedToTheJwtUserWithoutSeparateOwnershipLookups() {
		when(taskRepository.deleteTask(42L, 7L)).thenReturn(0);
		var service = new TaskService(taskRepository, userRepository);

		assertThrows(ResourceNotFoundException.class, () -> service.deleteTask(42L, new DeletionRequest(7L)));

		verify(taskRepository).deleteTask(42L, 7L);
		verify(taskRepository, never()).existsById(7L);
		verify(taskRepository, never()).findById(7L);
		verify(userRepository, never()).existsById(42L);
	}
}
