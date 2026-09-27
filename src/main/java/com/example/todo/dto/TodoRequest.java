package com.example.todo.dto;

import com.example.todo.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Payload for creating or fully replacing a todo. */
public record TodoRequest(
        @NotBlank(message = "title must not be blank")
        @Size(max = 255, message = "title must be at most 255 characters")
        String title,

        @Size(max = 2000, message = "description must be at most 2000 characters")
        String description,

        Boolean completed,

        /** Optional deadline. Past dates are allowed so existing work can be backfilled. */
        LocalDate dueDate,

        /** Defaults to MEDIUM when omitted. */
        Priority priority) {

    public boolean completedOrFalse() {
        return Boolean.TRUE.equals(completed);
    }

    public Priority priorityOrDefault() {
        return priority == null ? Priority.DEFAULT : priority;
    }
}
