package com.example.todo.repository;

import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

/** Composable predicates for todo queries. */
public final class TodoSpecifications {

    private TodoSpecifications() {
    }

    public static Specification<Todo> completedIs(Boolean completed) {
        if (completed == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("completed"), completed);
    }

    public static Specification<Todo> priorityIs(Priority priority) {
        if (priority == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("priority"), priority);
    }

    /** Case-insensitive match of {@code text} against the title or description. */
    public static Specification<Todo> matches(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String pattern = "%" + text.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), pattern),
                cb.like(cb.lower(root.get("description")), pattern));
    }

    /**
     * Unfinished todos whose due date is before {@code today}. Passing
     * {@code false} matches everything that is <em>not</em> overdue, so the
     * filter is symmetric.
     */
    public static Specification<Todo> overdueIs(Boolean overdue, LocalDate today) {
        if (overdue == null) {
            return null;
        }
        return (root, query, cb) -> {
            var isOverdue = cb.and(
                    cb.isFalse(root.get("completed")),
                    cb.isNotNull(root.get("dueDate")),
                    cb.lessThan(root.get("dueDate"), today));
            return overdue ? isOverdue : cb.not(isOverdue);
        };
    }
}
