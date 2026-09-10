package com.aayush.todo_backend;

import com.aayush.todo_backend.dto.CreateTodoRequest;
import com.aayush.todo_backend.dto.UpdateTodoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testGetAllTodos() throws Exception {
		mockMvc.perform(get("/api/todos"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void testCreateTodo() throws Exception {
		CreateTodoRequest request = new CreateTodoRequest("Write Tests", "Write unit and integration tests", false);

		mockMvc.perform(post("/api/todos")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.title").value("Write Tests"))
				.andExpect(jsonPath("$.completed").value(false));
	}

	@Test
	void testCreateTodoWithBlankTitle() throws Exception {
		CreateTodoRequest request = new CreateTodoRequest("", "Empty title", false);

		mockMvc.perform(post("/api/todos")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Bad Request"));
	}

	@Test
	void testGetTodoById() throws Exception {
		mockMvc.perform(get("/api/todos/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	void testGetTodoByIdNotFound() throws Exception {
		mockMvc.perform(get("/api/todos/99999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Not Found"));
	}

	@Test
	void testUpdateTodo() throws Exception {
		UpdateTodoRequest request = new UpdateTodoRequest("Updated Title", "Updated Description", true);

		mockMvc.perform(put("/api/todos/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Updated Title"))
				.andExpect(jsonPath("$.completed").value(true));
	}

	@Test
	void testToggleTodoStatus() throws Exception {
		mockMvc.perform(patch("/api/todos/1/toggle"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	void testDeleteTodo() throws Exception {
		CreateTodoRequest request = new CreateTodoRequest("Temporary Todo", "To be deleted", false);
		String response = mockMvc.perform(post("/api/todos")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

		Long id = objectMapper.readTree(response).get("id").asLong();

		mockMvc.perform(delete("/api/todos/" + id))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/todos/" + id))
				.andExpect(status().isNotFound());
	}
}
