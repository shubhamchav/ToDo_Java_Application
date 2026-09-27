# Todo REST API

A Spring Boot REST API for managing todos, backed by an in-memory H2 database.

## Requirements

- **JDK 17 or newer** — not currently installed on this machine. Get it from
  [Adoptium](https://adoptium.net/) or via `winget install EclipseAdoptium.Temurin.21.JDK`.
- **Maven 3.9+** — from [maven.apache.org](https://maven.apache.org/download.cgi) or
  `winget install Apache.Maven`.

Verify with `java -version` and `mvn -v` in a fresh terminal.

## Run

```powershell
mvn spring-boot:run
```

The API listens on `http://localhost:8080`. The H2 web console is at
`http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:tododb`, user `sa`, no password).

Data lives in memory only and is discarded when the app stops.

## Test

```powershell
mvn test
```

## API

| Method | Path              | Description                                  |
| ------ | ----------------- | -------------------------------------------- |
| GET    | `/api/todos`      | List todos, newest first                     |
| GET    | `/api/todos?completed=true` | List only completed (or `false` for open) |
| GET    | `/api/todos/{id}` | Fetch one todo                               |
| POST   | `/api/todos`      | Create a todo → `201` + `Location` header    |
| PUT    | `/api/todos/{id}` | Replace every field of a todo                |
| PATCH  | `/api/todos/{id}` | Update only the fields you send              |
| DELETE | `/api/todos/{id}` | Delete a todo → `204`                        |

### Todo shape

```json
{
  "id": 1,
  "title": "Buy milk",
  "description": "2 litres",
  "completed": false,
  "createdAt": "2026-09-02T10:15:30Z",
  "updatedAt": "2026-09-02T10:15:30Z"
}
```

`title` is required and capped at 255 characters; `description` is optional, capped at 2000.

### Examples

```powershell
# Create
curl -X POST http://localhost:8080/api/todos -H "Content-Type: application/json" -d '{\"title\":\"Buy milk\",\"description\":\"2 litres\"}'

# List
curl http://localhost:8080/api/todos

# Mark complete (PATCH leaves title/description untouched)
curl -X PATCH http://localhost:8080/api/todos/1 -H "Content-Type: application/json" -d '{\"completed\":true}'

# Delete
curl -X DELETE http://localhost:8080/api/todos/1
```

### Errors

Failures return a consistent JSON body:

```json
{
  "timestamp": "2026-09-02T10:15:30Z",
  "status": 404,
  "error": "Not Found",
  "message": "Todo 42 not found"
}
```

`404` for an unknown id, `400` for validation failures (the `message` names the offending fields).

## Layout

```
src/main/java/com/example/todo/
  TodoApplication.java              entry point
  controller/TodoController.java    HTTP layer
  service/TodoService.java          business logic, transactions
  repository/TodoRepository.java    Spring Data JPA
  model/Todo.java                   JPA entity
  dto/                              request/response records
  exception/                        not-found type + @RestControllerAdvice
src/test/java/com/example/todo/
  TodoControllerTest.java           end-to-end CRUD tests via MockMvc
```

## Switching to a persistent database

Replace the datasource block in `src/main/resources/application.properties` — e.g. for
file-backed H2 that survives restarts:

```properties
spring.datasource.url=jdbc:h2:file:./data/tododb
```
