package com.todolist.v2.task;

import com.todolist.v2.exception.ResourceNotFoundException;
import com.todolist.v2.task.dto.*;
import com.todolist.v2.user.UserRepository;
import com.todolist.v2.utils.ApiResponse;
import com.todolist.v2.utils.BasicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TaskService implements BasicService<Task, TaskRequest, TaskResponse> {

	private final TaskRepository taskRepository;
	private final UserRepository userRepository;

	public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
		this.taskRepository = taskRepository;
		this.userRepository = userRepository;
	}

	public ApiResponse<TaskResponse> createTask(Long userId, TaskRequest request) {
		var user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Invalid user."));

		if (request.dueAt().isBefore(Instant.now())) {
			throw new IllegalArgumentException("You cannot have a due from the past.");
		}

		var task = new Task(request.name(), request.description(), Boolean.TRUE.equals(request.completed()),
				request.dueAt(), user);
		var taskResponse = toResponse(taskRepository.save(task), this::toTaskResponse);

		return ApiResponse.successful(taskResponse, "Task has been created! Make sure to finish it! ;)");
	}

	public ApiResponse<TaskResponse> updateTaskName(Long taskId, Long userId, UpdateTaskRequest<String> request) {
		var task = getOwnedTaskOrThrow(taskId, userId);
		task.setName(request.toApply());

		return ApiResponse.successful(update(task), "Task name has been updated!");
	}

	public ApiResponse<TaskResponse> updateTaskDescription(Long taskId, Long userId, UpdateTaskRequest<String> request) {
		var task = getOwnedTaskOrThrow(taskId, userId);
		task.setDescription(request.toApply());

		return ApiResponse.successful(update(task), "Task description has been updated!");
	}

	public ApiResponse<TaskResponse> updateTaskDue(Long taskId, Long userId, UpdateTaskRequest<Instant> request) {
		var task = getOwnedTaskOrThrow(taskId, userId);
		task.setDueAt(request.toApply());

		return ApiResponse.successful(update(task), "Task due has been updated!");
	}

	public ApiResponse<TaskResponse> updateTaskStatus(Long taskId, Long userId, UpdateTaskRequest<Boolean> request) {
		var task = getOwnedTaskOrThrow(taskId, userId);

		task.setCompleted(request.toApply());

		return ApiResponse.successful(update(task), "Task has been marked as completed!");
	}

	private TaskResponse update(Task task) {
		return toResponse(taskRepository.save(task), this::toTaskResponse);
	}

	public Page<TaskResponse> getSortedTask(Long userId, GetPageRequest request) {
		var property = request.property() == null || request.property().isBlank() ? "createdAt" : request.property();
		var sort = request.ascending() ? Sort.by(property).ascending() : Sort.by(property).descending();
		var pageable = PageRequest.of(Math.max(0, request.number()), request.size(), sort);

		return taskRepository.findAll(userId, pageable).map(this::toTaskResponse);
	}

	public void deleteTask(Long userId, DeletionRequest request) {
		if (taskRepository.deleteTask(userId, request.taskId()) == 0) {
			throw new ResourceNotFoundException("Invalid task ID.");
		}
	}

	private Task getOwnedTaskOrThrow(Long taskId, Long userId) {
		var task = taskRepository.findByIdAndUser_Id(taskId, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Invalid task ID."));

		System.out.println(task);

		return task;
	}

	private TaskResponse toTaskResponse(Task task) {
		return new TaskResponse(task.getId(), task.getName(), task.getDescription(), task.getDueAt(),
				task.getUser().getId(), task.isCompleted(), task.getCreatedAt(), task.getUpdatedAt());
	}
}
