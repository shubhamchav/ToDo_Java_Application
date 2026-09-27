package com.example.todo.service;

import com.example.todo.model.Priority;

/**
 * Optional list criteria. Every field is nullable and a null means "do not
 * narrow by this".
 */
public record TodoFilter(
        Boolean completed,
        Priority priority,
        Boolean overdue,
        String search,
        TodoSort sort) {

    public static TodoFilter unfiltered() {
        return new TodoFilter(null, null, null, null, TodoSort.CREATED_AT);
    }

    public TodoSort sortOrDefault() {
        return sort == null ? TodoSort.CREATED_AT : sort;
    }
}
