package com.aayush.todo_backend.controller;

import com.aayush.todo_backend.dto.CreateTodoRequest;
import com.aayush.todo_backend.dto.UpdateTodoRequest;
import com.aayush.todo_backend.model.Todo;
import com.aayush.todo_backend.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {

	private final TodoService todoService;

	public TodoController(TodoService todoService) {
		this.todoService = todoService;
	}

	@GetMapping
	public ResponseEntity<List<Todo>> getAllTodos(
			@RequestParam(required = false) Boolean completed,
			@RequestParam(required = false) String search) {
		List<Todo> todos = todoService.getAllTodos(completed, search);
		return ResponseEntity.ok(todos);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
		Todo todo = todoService.getTodoById(id);
		return ResponseEntity.ok(todo);
	}

	@PostMapping
	public ResponseEntity<Todo> createTodo(@RequestBody CreateTodoRequest request) {
		Todo createdTodo = todoService.createTodo(request);
		return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Todo> updateTodo(
			@PathVariable Long id,
			@RequestBody UpdateTodoRequest request) {
		Todo updatedTodo = todoService.updateTodo(id, request);
		return ResponseEntity.ok(updatedTodo);
	}

	@PatchMapping("/{id}/toggle")
	public ResponseEntity<Todo> toggleTodoStatus(@PathVariable Long id) {
		Todo updatedTodo = todoService.toggleTodoStatus(id);
		return ResponseEntity.ok(updatedTodo);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
		todoService.deleteTodo(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteAllTodos() {
		todoService.deleteAllTodos();
		return ResponseEntity.noContent().build();
	}
}
