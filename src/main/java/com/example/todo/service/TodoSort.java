package com.example.todo.service;

import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Supported orderings for the todo list.
 *
 * <p>Ordering is applied in memory rather than in SQL so that "no due date"
 * can sort last and priority can sort by urgency instead of alphabetically
 * (the enum is persisted as a string). That is safe while the endpoint returns
 * every match; introducing pagination would mean pushing these comparators
 * down into the query.
 */
public enum TodoSort {

    /**
     * Newest first. The default. Ties break on id descending, so todos created
     * within the same clock tick still come back newest-first.
     */
    CREATED_AT("createdAt", byCreatedAt()),

    /** Soonest deadline first; todos with no due date come last. */
    DUE_DATE("dueDate", byDueDate()),

    /** Most urgent first, then by soonest deadline. */
    PRIORITY("priority", byPriority()),

    /** Alphabetical, case-insensitive. */
    TITLE("title", byTitle());

    private final String parameterValue;
    private final Comparator<Todo> comparator;

    TodoSort(String parameterValue, Comparator<Todo> comparator) {
        this.parameterValue = parameterValue;
        this.comparator = comparator;
    }

    public Comparator<Todo> comparator() {
        return comparator;
    }

    /** Resolves a {@code ?sort=} value, ignoring case. Blank means the default. */
    public static TodoSort from(String value) {
        if (value == null || value.isBlank()) {
            return CREATED_AT;
        }
        String normalised = value.trim().toLowerCase(Locale.ROOT);
        for (TodoSort candidate : values()) {
            if (candidate.parameterValue.toLowerCase(Locale.ROOT).equals(normalised)) {
                return candidate;
            }
        }
        throw new IllegalArgumentException(
                "Unknown sort '" + value + "'. Valid values: " + validValues());
    }

    private static String validValues() {
        return Arrays.stream(values())
                .map(sort -> sort.parameterValue)
                .collect(Collectors.joining(", "));
    }

    private static Comparator<Todo> byCreatedAt() {
        Comparator<Todo> oldestFirst = Comparator.comparing(Todo::getCreatedAt, Comparator.<Instant>naturalOrder());
        return oldestFirst.thenComparing(Todo::getId).reversed();
    }

    private static Comparator<Todo> byDueDate() {
        Comparator<Todo> soonestFirst = Comparator.comparing(
                Todo::getDueDate, Comparator.nullsLast(Comparator.<LocalDate>naturalOrder()));
        return soonestFirst.thenComparing(Todo::getId);
    }

    private static Comparator<Todo> byPriority() {
        Comparator<Todo> mostUrgentFirst =
                Comparator.comparing(Todo::getPriority, Comparator.<Priority>naturalOrder());
        return mostUrgentFirst
                .thenComparing(Todo::getDueDate, Comparator.nullsLast(Comparator.<LocalDate>naturalOrder()))
                .thenComparing(Todo::getId);
    }

    private static Comparator<Todo> byTitle() {
        Comparator<Todo> alphabetical = Comparator.comparing(Todo::getTitle, String.CASE_INSENSITIVE_ORDER);
        return alphabetical.thenComparing(Todo::getId);
    }
}
