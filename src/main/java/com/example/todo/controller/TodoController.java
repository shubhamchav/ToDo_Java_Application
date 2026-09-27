package com.example.todo.controller;

import com.example.todo.dto.TodoPatchRequest;
import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.model.Priority;
import com.example.todo.service.TodoFilter;
import com.example.todo.service.TodoService;
import com.example.todo.service.TodoSort;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    /**
     * Lists todos. Every parameter is optional; supplying several narrows the
     * result by all of them.
     *
     * @param completed only finished (true) or only open (false) todos
     * @param priority  only todos at this priority
     * @param overdue   only past-due unfinished todos (true), or everything else (false)
     * @param q         case-insensitive substring match on title or description
     * @param sort       one of createdAt (default), dueDate, priority, title
     */
    @GetMapping
    public List<TodoResponse> list(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Boolean overdue,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String sort) {

        TodoFilter filter = new TodoFilter(completed, priority, overdue, q, TodoSort.from(sort));
        return service.findAll(filter).stream().map(TodoResponse::from).toList();
    }

    @GetMapping("/{id}")
    public TodoResponse get(@PathVariable Long id) {
        return TodoResponse.from(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<TodoResponse> create(@Valid @RequestBody TodoRequest request) {
        TodoResponse created = TodoResponse.from(service.create(request));
        return ResponseEntity.created(URI.create("/api/todos/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public TodoResponse replace(@PathVariable Long id, @Valid @RequestBody TodoRequest request) {
        return TodoResponse.from(service.replace(id, request));
    }

    @PatchMapping("/{id}")
    public TodoResponse patch(@PathVariable Long id, @Valid @RequestBody TodoPatchRequest request) {
        return TodoResponse.from(service.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
