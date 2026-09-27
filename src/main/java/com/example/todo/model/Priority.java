package com.example.todo.model;

/** How urgent a todo is. Declared most urgent first. */
public enum Priority {
    HIGH,
    MEDIUM,
    LOW;

    public static final Priority DEFAULT = MEDIUM;
}
