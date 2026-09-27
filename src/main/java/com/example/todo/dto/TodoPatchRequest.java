package com.example.todo.dto;

import com.example.todo.model.Priority;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Payload for a partial update. Any {@code null} field is left unchanged, so a
 * caller can flip {@code completed} without resending the title.
 *
 * <p>Because null means "leave alone", PATCH cannot clear a value. To remove a
 * due date, send a PUT that omits {@code dueDate}.
 */
public record TodoPatchRequest(
        @Size(min = 1, max = 255, message = "title must be 1 to 255 characters")
        String title,

        @Size(max = 2000, message = "description must be at most 2000 characters")
        String description,

        Boolean completed,

        LocalDate dueDate,

        Priority priority) {
}
