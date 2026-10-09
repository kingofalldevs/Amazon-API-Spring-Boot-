# Amazon Books API

My first Spring Boot project: a small REST API for a book collection, backed by PostgreSQL.

I built this as an introduction to Spring Boot. It covers REST controllers, Spring Data JPA, and connecting an API to a real database.

## Tech stack

- Java
- Spring Boot 4.1 (Spring Web, Spring Data JPA, DevTools)
- PostgreSQL
- Maven

## Endpoints

All endpoints live under `/books`.

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/books` | List all books |
| GET | `/books/{id}` | Get one book by id |
| GET | `/books/total` | Count the books |
| GET | `/books/cheapest` | The book with the lowest price |
| GET | `/books/mostexpensive` | The book with the highest price |
| GET | `/books/author/{name}` | Search by author (partial, case-insensitive) |

### Example

```bash
curl http://localhost:8080/books/author/pappy
```

```json
[
  {"id": 3, "title": "Antimony", "author": "King Pappy", "price": 45.6},
  {"id": 11, "title": "Echoes of Tomorrow", "author": "King Pappy", "price": 88.0}
]
```

## Project structure

```
src/main/java/com/prince/api_app/
├── ApiAppApplication.java   # app entry point
├── Book.java                # @Entity, maps to the "book" table
├── BookRepository.java      # Spring Data JPA repository
├── BookController.java      # REST endpoints
└── DataSeeder.java          # inserts starter books when the table is empty
```

## Running it locally

### 1. Requirements

- Java (JDK) installed
- PostgreSQL running locally (I use Postgres.app)

### 2. Create the database

```sql
CREATE DATABASE booksdb;
```

### 3. Configure the connection

In `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/booksdb
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

Hibernate creates the `book` table automatically on first run.

### 4. Start the app

```bash
./mvnw spring-boot:run
```

Or run `ApiAppApplication` from your IDE. The API is then available at `http://localhost:8080`.

## What I learned

- How a `@RestController` maps URLs to Java methods
- Using `@PathVariable` to read values from the URL
- Why the controller, repository, and entity are separate
- Turning a class into a table with `@Entity`, `@Id`, and `@GeneratedValue`
- Spring Data derived queries, for example `findByAuthorContainingIgnoreCase`
- Reading stack traces (including a `ConcurrentModificationException` from modifying a list while looping over it)

## Roadmap

- [ ] `POST /books` to add a book
- [ ] `PUT /books/{id}` and `DELETE /books/{id}`
- [ ] Proper 404 responses instead of returning `null`
- [ ] Move logic into a `BookService`
- [ ] Expose the endpoints as tools for an AI agent