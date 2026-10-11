package com.todolist.v2.config;

import com.todolist.v2.auth.AuthController;
import com.todolist.v2.auth.AuthService;
import com.todolist.v2.task.TaskController;
import com.todolist.v2.task.TaskService;
import com.todolist.v2.user.UserController;
import com.todolist.v2.user.UserService;
import com.todolist.v2.utils.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, TaskController.class, UserController.class})
@Import(SecurityConfig.class)
class SecurityEndpointsTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private JwtDecoder jwtDecoder;

	@MockitoBean
	private JpaMetamodelMappingContext jpaMappingContext;

	@MockitoBean
	private TaskService taskService;

	@MockitoBean
	private AuthService authService;

	@MockitoBean
	private UserService userService;

	@Test
	void protectsTaskEndpointsWhenNoBearerTokenIsProvided() throws Exception {
		mockMvc.perform(get("/tasks/your-tasks")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"number\":0,\"size\":10,\"ascending\":true}"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void returnsApiResponseForInvalidRequestBodies() throws Exception {
		mockMvc.perform(get("/tasks/your-tasks")
						.with(jwt().jwt(token -> token.claim("userId", 42L)))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"size\":10,\"ascending\":true}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.successful").value(false))
				.andExpect(jsonPath("$.message").value("Number of pages is required."));
	}

	@Test
	void permitsAuthenticationEndpointsWithoutABearerToken() throws Exception {
		when(authService.login(any())).thenReturn(ApiResponse.successful(null, "logged in"));

		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\":\"user@example.com\",\"password\":\"Correct-Horse-42!\"}"))
				.andExpect(status().isOk());
	}

	@Test
	void taskAndUserOperationsUseTheJwtIdentityInsteadOfRequestUserIds() throws Exception {
		when(taskService.getSortedTask(eq(42L), any())).thenReturn(Page.empty());
		when(taskService.createTask(eq(42L), any()))
				.thenReturn(ApiResponse.successful(null, "created"));

		mockMvc.perform(get("/tasks/your-tasks")
						.with(jwt().jwt(token -> token.claim("userId", 42L)))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"number\":0,\"size\":10,\"ascending\":true,\"userId\":99}"))
				.andExpect(status().isOk())
				.andExpect(result -> assertNull(result.getRequest().getSession(false)));

		mockMvc.perform(post("/tasks/create")
						.with(jwt().jwt(token -> token.claim("userId", 42L)))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"Task\",\"description\":\"Description\",\"dueAt\":\"2030-01-01T00:00:00Z\",\"userId\":99}"))
				.andExpect(status().isOk());

		mockMvc.perform(put("/tasks/update/name/7")
						.with(jwt().jwt(token -> token.claim("userId", 42L)))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"toApply\":\"Renamed\",\"userId\":99}"))
				.andExpect(status().isOk());
		mockMvc.perform(put("/tasks/update/description/7")
						.with(jwt().jwt(token -> token.claim("userId", 42L)))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"toApply\":\"Updated description\",\"userId\":99}"))
				.andExpect(status().isOk());
		mockMvc.perform(put("/tasks/update/due/7")
						.with(jwt().jwt(token -> token.claim("userId", 42L)))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"toApply\":\"2030-02-01T00:00:00Z\",\"userId\":99}"))
				.andExpect(status().isOk());
		mockMvc.perform(put("/tasks/update/mark-complete/7")
						.with(jwt().jwt(token -> token.claim("userId", 42L)))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"toApply\":true,\"userId\":99}"))
				.andExpect(status().isOk());

		mockMvc.perform(delete("/tasks/delete")
						.with(jwt().jwt(token -> token.claim("userId", 42L)))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"taskId\":7,\"userId\":99}"))
				.andExpect(status().isOk());

		mockMvc.perform(put("/user/update/name")
						.with(jwt().jwt(token -> token.claim("userId", 42L)))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"toApply\":\"new-name\",\"userId\":99}"))
				.andExpect(status().isOk());

		verify(taskService).getSortedTask(eq(42L), any());
		verify(taskService).createTask(eq(42L), any());
		verify(taskService).updateTaskName(eq(7L), eq(42L), any());
		verify(taskService).updateTaskDescription(eq(7L), eq(42L), any());
		verify(taskService).updateTaskDue(eq(7L), eq(42L), any());
		verify(taskService).updateTaskStatus(eq(7L), eq(42L), any());
		verify(taskService).deleteTask(eq(42L), any());
		verify(userService).updateUsername(eq(42L), any());
	}
}
