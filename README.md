# Amazon Books API

My first Spring Boot project: a REST API for a book collection, backed by PostgreSQL, with AI tool calling capabilities and a chat endpoint that can search and add books by calling Java tools.

I built this as an introduction to Spring Boot. It covers REST controllers, Spring Data JPA, connecting an API to a real database, and letting an LLM use the API through tool calling.

## Tech stack

- Java
- Spring Boot 4.1 (Spring Web, Spring Data JPA, DevTools)
- Spring AI 2.0 with DeepSeek (tool calling)
- PostgreSQL
- Maven

## Endpoints

### Books

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/books` | List all books |
| GET | `/books/{id}` | Get one book by id |
| GET | `/books/total` | Count the books |
| GET | `/books/cheapest` | The book with the lowest price |
| GET | `/books/mostexpensive` | The book with the highest price |
| GET | `/books/author/{name}` | Search by author (partial, case-insensitive) |
| POST | `/books` | Add a new book (the database assigns the id) |

```bash
curl http://localhost:8080/books/author/pappy

curl -X POST http://localhost:8080/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Night Market","author":"Ama Boateng","price":21.50}'
```

### AI TOOLS

| Method | Tool | Equivalent Endpoint     | Description              |
|--------|---|-------------------------|--------------------------|
| GET    | findBooksByAuthor  | `/books/author/{name}`  | find book by author name |
| GET    | getCheapestBook  | `/books/cheapest`       | Get cheapest book        |
|GET|getTotal`/books/mostexpensive`| Get most expensive book |
|GET|getTotal| `books/total`           | Get total of books       |
| POST   | addBook | `/books`                | Add book to the database |


### AI chat
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/chat?message=...` | Ask a question in plain language. The model decides which tool to call. |

```bash
curl -G http://localhost:8080/chat --data-urlencode "message=Who wrote the cheapest book?"
curl -G http://localhost:8080/chat --data-urlencode "message=Add a book called Night Market by Ama Boateng priced at 21.50"
```

## How the AI part works

`BookTools` holds ordinary Java methods marked with `@Tool` and a short description. The model reads those descriptions, decides when a method is useful, and Spring AI runs it against Postgres.

```
curl /chat -> ChatController -> DeepSeek -> BookTools -> BookRepository -> PostgreSQL
                                    ^                                          ^
                                    |                                          |
                                    +------------- result ---------------------+
```

Available tools: find books by author, get the cheapest book, add a book. Each call prints `TOOL CALLED: ...` in the console so you can watch the model's choices.

## Project structure

```
src/main/java/com/prince/api_app/
├── ApiAppApplication.java   # app entry point
├── Book.java                # @Entity, maps to the "book" table
├── BookRepository.java      # Spring Data JPA repository
├── BookController.java      # REST endpoints under /books
├── BookTools.java           # @Tool methods the AI can call
├── ChatController.java      # /chat endpoint
└── DataSeeder.java          # inserts starter books when the table is empty
```

## Running it locally

### 1. Requirements

- Java (JDK)
- PostgreSQL running locally (I use Postgres.app)
- A DeepSeek API key (only needed for `/chat`)

### 2. Create the database

```sql
CREATE DATABASE booksdb;
```

### 3. Configure the app

`src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/booksdb
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.profiles.active=local
spring.ai.deepseek.chat.model=deepseek-chat
```

Keep the API key out of Git. Create `src/main/resources/application-local.properties` (listed in `.gitignore`):

```properties
spring.ai.deepseek.api-key=YOUR_DEEPSEEK_KEY
```

### 4. Start the app

```bash
./mvnw spring-boot:run
```

The API is then at `http://localhost:8080`. Hibernate creates the `book` table on first run, and the seeder fills it with starter books.

## What I learned

- How a `@RestController` maps URLs to Java methods
- `@PathVariable` for values in the URL and `@RequestBody` for JSON bodies
- Turning a class into a table with `@Entity`, `@Id` and `@GeneratedValue`
- Why the id must be an `Integer`, so a missing id means "new row" instead of a parse error
- Spring Data derived queries, such as `findByAuthorContainingIgnoreCase`
- Why the controller, repository and entity are separate
- Reading stack traces, including a `ConcurrentModificationException` from changing a list while looping over it
- Keeping secrets out of Git with a profile-specific properties file
- Exposing Java methods as tools for an LLM with Spring AI

## Roadmap

- [x] GET endpoints backed by PostgreSQL
- [x] `POST /books`
- [x] AI chat endpoint with tool calling
- [ ] `PUT /books/{id}` and `DELETE /books/{id}`
- [ ] Validation (`@Valid`) and proper 404 and 400 responses
- [ ] Move logic into a `BookService`
- [ ] Prevent duplicate books
- [ ] Bulk insert tool for the agent
- [ ] Docker setup
- [ ] Flutter front end