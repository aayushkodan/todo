package com.aayush.todo_backend.dto;

public class CreateTodoRequest {

	private String title;
	private String description;
	private boolean completed;

	public CreateTodoRequest() {
	}

	public CreateTodoRequest(String title, String description, boolean completed) {
		this.title = title;
		this.description = description;
		this.completed = completed;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
}
