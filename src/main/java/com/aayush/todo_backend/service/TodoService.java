package com.aayush.todo_backend.service;

import com.aayush.todo_backend.dto.CreateTodoRequest;
import com.aayush.todo_backend.dto.UpdateTodoRequest;
import com.aayush.todo_backend.exception.ResourceNotFoundException;
import com.aayush.todo_backend.model.Todo;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class TodoService {

	private final List<Todo> todos = new CopyOnWriteArrayList<>();
	private final AtomicLong idCounter = new AtomicLong(1);

	@PostConstruct
	public void init() {
		// Seed with initial sample todos for demonstration and testing
		createTodo(new CreateTodoRequest("Learn Spring Boot", "Explore REST APIs and service architecture", true));
		createTodo(new CreateTodoRequest("Build Todo Application", "Create frontend and backend services", false));
		createTodo(new CreateTodoRequest("Deploy with Kubernetes", "Setup CI/CD pipeline and k8s manifests", false));
	}

	public List<Todo> getAllTodos(Boolean completed, String search) {
		return todos.stream()
				.filter(todo -> completed == null || todo.isCompleted() == completed)
				.filter(todo -> search == null || search.isBlank() ||
						(todo.getTitle() != null && todo.getTitle().toLowerCase().contains(search.trim().toLowerCase())) ||
						(todo.getDescription() != null && todo.getDescription().toLowerCase().contains(search.trim().toLowerCase())))
				.collect(Collectors.toList());
	}

	public Todo getTodoById(Long id) {
		return todos.stream()
				.filter(todo -> todo.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));
	}

	public Todo createTodo(CreateTodoRequest request) {
		if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
			throw new IllegalArgumentException("Todo title cannot be empty");
		}

		LocalDateTime now = LocalDateTime.now();
		Todo todo = new Todo(
				idCounter.getAndIncrement(),
				request.getTitle().trim(),
				request.getDescription() != null ? request.getDescription().trim() : "",
				request.isCompleted(),
				now,
				now
		);
		todos.add(todo);
		return todo;
	}

	public Todo updateTodo(Long id, UpdateTodoRequest request) {
		Todo todo = getTodoById(id);

		if (request.getTitle() != null) {
			if (request.getTitle().trim().isEmpty()) {
				throw new IllegalArgumentException("Todo title cannot be empty");
			}
			todo.setTitle(request.getTitle().trim());
		}

		if (request.getDescription() != null) {
			todo.setDescription(request.getDescription().trim());
		}

		if (request.getCompleted() != null) {
			todo.setCompleted(request.getCompleted());
		}

		todo.setUpdatedAt(LocalDateTime.now());
		return todo;
	}

	public Todo toggleTodoStatus(Long id) {
		Todo todo = getTodoById(id);
		todo.setCompleted(!todo.isCompleted());
		todo.setUpdatedAt(LocalDateTime.now());
		return todo;
	}

	public void deleteTodo(Long id) {
		getTodoById(id);
		todos.removeIf(todo -> todo.getId().equals(id));
	}

	public void deleteAllTodos() {
		todos.clear();
	}
}
