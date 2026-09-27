package com.example.todo.service;

import com.example.todo.dto.TodoPatchRequest;
import com.example.todo.dto.TodoRequest;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRepository;
import com.example.todo.repository.TodoSpecifications;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TodoService {

    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Todo> findAll(TodoFilter filter) {
        LocalDate today = LocalDate.now();
        Specification<Todo> spec = combine(
                TodoSpecifications.completedIs(filter.completed()),
                TodoSpecifications.priorityIs(filter.priority()),
                TodoSpecifications.overdueIs(filter.overdue(), today),
                TodoSpecifications.matches(filter.search()));

        List<Todo> todos = new ArrayList<>(repository.findAll(spec));
        todos.sort(filter.sortOrDefault().comparator());
        return todos;
    }

    @Transactional(readOnly = true)
    public Todo findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
    }

    public Todo create(TodoRequest request) {
        Todo todo = new Todo(
                request.title().trim(),
                request.description(),
                request.completedOrFalse(),
                request.dueDate(),
                request.priorityOrDefault());
        return repository.save(todo);
    }

    /** Full replacement (PUT): every writable field is set from the request. */
    public Todo replace(Long id, TodoRequest request) {
        Todo todo = findById(id);
        todo.setTitle(request.title().trim());
        todo.setDescription(request.description());
        todo.setCompleted(request.completedOrFalse());
        todo.setDueDate(request.dueDate());
        todo.setPriority(request.priorityOrDefault());
        return repository.save(todo);
    }

    /** Partial update (PATCH): only non-null fields are applied. */
    public Todo patch(Long id, TodoPatchRequest request) {
        Todo todo = findById(id);
        if (request.title() != null) {
            todo.setTitle(request.title().trim());
        }
        if (request.description() != null) {
            todo.setDescription(request.description());
        }
        if (request.completed() != null) {
            todo.setCompleted(request.completed());
        }
        if (request.dueDate() != null) {
            todo.setDueDate(request.dueDate());
        }
        if (request.priority() != null) {
            todo.setPriority(request.priority());
        }
        return repository.save(todo);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        repository.deleteById(id);
    }

    /**
     * ANDs together the specifications that were actually supplied. Returns
     * null when nothing was, which Spring Data reads as "no restriction".
     */
    @SafeVarargs
    private static Specification<Todo> combine(Specification<Todo>... specs) {
        Specification<Todo> combined = null;
        for (Specification<Todo> spec : specs) {
            if (spec == null) {
                continue;
            }
            combined = combined == null ? spec : combined.and(spec);
        }
        return combined;
    }
}
