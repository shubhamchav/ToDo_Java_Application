package com.example.todo.dto;

import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import java.time.Instant;
import java.time.LocalDate;

/** Read model returned by the API. */
public record TodoResponse(
        Long id,
        String title,
        String description,
        boolean completed,
        LocalDate dueDate,
        Priority priority,
        boolean overdue,
        Instant createdAt,
        Instant updatedAt) {

    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isCompleted(),
                todo.getDueDate(),
                todo.getPriority(),
                todo.isOverdue(),
                todo.getCreatedAt(),
                todo.getUpdatedAt());
    }
}
