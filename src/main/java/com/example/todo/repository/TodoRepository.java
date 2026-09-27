package com.example.todo.repository;

import com.example.todo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Filtering is done with {@link TodoSpecifications} rather than derived query
 * methods, so the optional criteria (completed, priority, search, overdue) can
 * be combined freely without one method per combination.
 */
public interface TodoRepository extends JpaRepository<Todo, Long>, JpaSpecificationExecutor<Todo> {
}
