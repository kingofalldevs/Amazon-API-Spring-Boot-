# Amazon Books API
<img width="1196" height="677" alt="WhatsApp Image 2026-10-09 at 9 29 56 AM" src="https://github.com/user-attachments/assets/40eef2f1-bb72-48c6-ab06-677664fb0920" />

My first Spring Boot project: a REST API for a book collection, backed by PostgreSQL, with AI tool calling capabilities and a chat endpoint that can search, retrieve, add, update, and delete books by calling Java tools.

I built this as an introduction to Spring Boot. It covers REST controllers, Spring Data JPA, connecting an API to a real database, and letting an LLM use the API through tool calling.

## Tech stack

- Java
- Spring Boot 4.1.1 (Java 17)
- Spring Web MVC, Spring Data JPA, Thymeleaf, and DevTools
- Spring AI 2.0.1 with DeepSeek (tool calling)
- PostgreSQL
- Maven

## Endpoints

### Books

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/books` | List all books |
| GET | `/books/{id}` | Get one book by id; returns 404 when it does not exist |
| GET | `/books/total` | Count the books |
| GET | `/books/cheapest` | The book with the lowest price; returns 404 when no books exist |
| GET | `/books/mostexpensive` | The book with the highest price; returns 404 when no books exist |
| GET | `/books/author/{name}` | Search by author (partial, case-insensitive) |
| GET | `/books/title/{title}` | Search by title (partial, case-insensitive) |
| GET | `/books/priceRange?minPrice={min}&maxPrice={max}` | Find books within an inclusive price range |
| POST | `/books` | Add a new book (the database assigns the id) |
| POST | `/books/bulk` | Add multiple books |
| PUT | `/books/{id}` | Replace a book's title, author, and price; returns 404 when the id does not exist |
| DELETE | `/books/{id}` | Delete a book by id |

### Other

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/orders` | Return a placeholder orders message |

```bash
curl http://localhost:8080/books/author/pappy
curl http://localhost:8080/books/title/dune

curl -X POST http://localhost:8080/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Night Market","author":"Ama Boateng","price":21.50}'

# Use an existing book id and provide all three fields.
curl -X PUT http://localhost:8080/books/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated title","author":"Ama Boateng","price":19.50}'

curl -i -X DELETE http://localhost:8080/books/1
```

The PUT handler replaces all three fields; omitted fields are overwritten with `null` or `0`. If the id does not exist, it returns HTTP 404.

### AI tools

| Tool | Related REST endpoint | Description |
|------|-----------------------|-------------|
| `getAllBooks` | `GET /books` | List all books with ids, titles, authors, and prices |
| `findBooksByAuthor` | `/books/author/{name}` | Find books by partial, case-insensitive author name |
| `findBooksByTitle` | `GET /books/title/{title}` | Find books by partial, case-insensitive title |
| `getCheapestBook` | `/books/cheapest` | Get the cheapest book |
| `getMostExpensive` | `/books/mostexpensive` | Get the most expensive book |
| `getTotal` | `/books/total` | Count the books |
| `getBookById` | `/books/{id}` | Get a book by id |
| `bookByPriceRange` | `/books/priceRange` | Find books within a price range |
| `addBook` | `POST /books` | Add one book |
| `addBulk` | `POST /books/bulk` | Add multiple books |
| `updateBook` | `PUT /books/{id}` | Update a book by id |
| `deleteBook` | `DELETE /books/{id}` | Delete a book by id |

The `getBookById` and `updateBook` tools throw `IllegalArgumentException` when the book id is missing. Tool calls are not HTTP requests, so these exceptions do not represent HTTP status codes.

`getCheapestBook` and `getMostExpensive` return a `BookToolResult` containing a `book` and a `message`. When no matching book exists, `book` is `null` and the message says no book was found.

The `deleteBook` tool deletes the supplied id. Its description asks the model to get human approval, but this is guidance to the model and is not enforced by the application.


### AI chat
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/chat` | Send a JSON `ChatRequest` with a conversation id and message. The model decides which tool to call. |

```bash
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"conversationId":"demo-1","message":"Who wrote the cheapest book?"}'

curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"conversationId":"demo-1","message":"What was the title of that book?"}'

curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"conversationId":"demo-2","message":"Add a book called Night Market by Ama Boateng priced at 21.50"}'
```

Send the same `conversationId` for each turn in one conversation; use a different id for a separate conversation. Chat memory currently keeps a window of 20 messages in memory, so it is lost when the application restarts and is not shared across application instances.

## How the AI part works

`BookTools` holds ordinary Java methods marked with `@Tool` and a short description. The model reads those descriptions, decides when a method is useful, and Spring AI runs it against Postgres. `ChatController` uses Spring AI chat memory to include recent messages for the request's `conversationId`.

```
curl /chat -> ChatController -> DeepSeek -> BookTools -> BookRepository -> PostgreSQL
                                    ^                                          ^
                                    |                                          |
                                    +------------- result ---------------------+
```

Each tool call prints `TOOL CALLED: ...` in the console so you can watch the model's choices.

## Project structure

```
src/main/java/com/prince/api_app/
├── ApiAppApplication.java   # app entry point
├── Book.java                # @Entity, maps to the "book" table
├── BookRepository.java      # Spring Data JPA repository
├── BookController.java      # REST endpoints under /books, including DELETE
├── BookTools.java           # @Tool methods the AI can call
├── BookToolResult.java      # result returned by the cheapest/most expensive tools
├── ChatController.java      # /chat endpoint
├── ChatRequest.java         # /chat request body: conversationId and message
└── orderController.java     # placeholder /orders endpoint
src/main/resources/
├── application.properties   # database and AI configuration
└── static/
  ├── layout.html
  └── css/style.css
src/test/java/com/prince/api_app/
└── ApiAppApplicationTests.java # application context test
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

The API is then at `http://localhost:8080`. Hibernate creates or updates the `book` table on startup. There is currently no data seeder, so the table starts empty unless you add books through the API or database.

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
- [x] `POST /books/bulk`
- [x] `PUT /books/{id}`
- [x] `DELETE /books/{id}`
- [x] AI chat endpoint with tool calling
- [ ] Validation (`@Valid`) and proper 404 and 400 responses
- [ ] Move logic into a `BookService`
- [ ] Prevent duplicate books
- [ ] Docker setup
- [ ] Flutter front end
