package com.todolist.v2.task;

import com.todolist.v2.auth.JwtPrincipal;
import com.todolist.v2.task.dto.*;
import com.todolist.v2.utils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

	private final TaskService taskService;

	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	@GetMapping("/your-tasks")
	public Page<TaskResponse> getTasks(
			@Valid @RequestBody GetPageRequest request,
			@AuthenticationPrincipal Jwt jwt
	) {
		return taskService.getSortedTask(JwtPrincipal.userId(jwt), request);
	}

	@PostMapping("/create")
	public ResponseEntity<ApiResponse<TaskResponse>> createTask(
			@Valid @RequestBody TaskRequest request,
			@AuthenticationPrincipal Jwt jwt
	) {
		var response = taskService.createTask(JwtPrincipal.userId(jwt), request);

		if (!response.successful()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		
		return ResponseEntity.ok(response);
	}

	@PutMapping("/update/name/{id}")
	public ApiResponse<TaskResponse> updateTaskName(
			@PathVariable("id") Long id,
			@Valid @RequestBody UpdateTaskRequest<String> request,
			@AuthenticationPrincipal Jwt jwt
	) {
		return taskService.updateTaskName(id, JwtPrincipal.userId(jwt), request);
	}

	@PutMapping("/update/description/{id}")
	public ApiResponse<TaskResponse> updateTaskDescription(
			@PathVariable("id") Long id,
			@Valid @RequestBody UpdateTaskRequest<String> request,
			@AuthenticationPrincipal Jwt jwt
	) {
		return taskService.updateTaskDescription(id, JwtPrincipal.userId(jwt), request);
	}

	@PutMapping("/update/due/{id}")
	public ApiResponse<TaskResponse> updateTaskDue(
			@PathVariable("id") Long id,
			@Valid @RequestBody UpdateTaskRequest<Instant> request,
			@AuthenticationPrincipal Jwt jwt
	) {
		return taskService.	updateTaskDue(id, JwtPrincipal.userId(jwt), request);
	}

	@PutMapping("/update/mark-complete/{id}")
	public ApiResponse<TaskResponse> markTaskAsComplete(
			@PathVariable("id") Long id,
			@Valid @RequestBody UpdateTaskRequest<Boolean> request,
			@AuthenticationPrincipal Jwt jwt
	) {
		return taskService.updateTaskStatus(id, JwtPrincipal.userId(jwt), request);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Map<String, String>> deleteTask(
			@Valid @RequestBody DeletionRequest request,
			@AuthenticationPrincipal Jwt jwt
	) {
		taskService.deleteTask(JwtPrincipal.userId(jwt), request);

		return ResponseEntity.ok(Map.of("message", "Task has been successfully deleted!"));
	}
}
