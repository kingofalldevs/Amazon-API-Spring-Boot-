package com.prince.api_app;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.ai.tool.annotation.ToolParam;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Component
public class BookTools {

    private final BookRepository bookRepository;

    public BookTools(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Tool(description = "Find books whose author name contains the given text")
    public List<Book> findBooksByAuthor(String author) {
        System.out.println("TOOL CALLED: findBooksByAuthor(" + author + ")");
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    @Tool(description = "Get the cheapest book in the database")
    public Book getCheapestBook() {
        if (bookRepository.count() == 0) {
            return "No books found";
        }
        System.out.println("TOOL CALLED: getCheapestBook()");
        return bookRepository.findFirstByOrderByPriceAsc();
    }

    @Tool(description = "Get the most Expensive book in the database")
    public Book getMostExpensive() {
        System.out.println("TOOL CALLED: getMostExpensive()");
        if (bookRepository.count() == 0) {
            return "No books found";
        }
        return bookRepository.findFirstByOrderByPriceDesc();
    }


    @Tool(description = "Add one new book to the database")
    public Book addBook(String title, String author, double price) {
        System.out.println("TOOL CALLED: addBook(" + title + ")");
        return bookRepository.save(new Book(null, title, author, price));
    }

    @Tool(description = "Get the total of books ")
    public int getTotal() {
        System.out.println("TOOL CALLED: getTotal()");
        return (int) bookRepository.count();
    }

    @Tool(description = "add more than one book to the database")
    public List<Book> addBulk(
        @ToolParam(description = "Books to save; each needs a title, author, and price. Omit id.")
        List<Book> books) {
        System.out.println("TOOL CALLED: addBulk(" + books.size() + ")");
        return bookRepository.saveAll(books);
    }

    @Tool(description = "Get book by id from the database")
    public Book getBookById(int id) {
        System.out.println("TOOL CALLED: getBookById(" + id + ")");
        return bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Book not found"));
    }

    @Tool(description = "Get books from the database within a price range")
    public List<Book> bookByPriceRange(double minPrice, double maxPrice) {
        System.out.println("TOOL CALLED: bookByPriceRange(" + minPrice + ", " + maxPrice + ")");
        return bookRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Tool(description = "Use this tool to update content of a book in the database by id")
    public Book updateBook(int id , Book book){
        System.out.println("TOOL CALLED: updateBook("+id+")");
        Book existingBook = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Book not found"));
      
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPrice(book.getPrice());
        return bookRepository.save(existingBook);
        

    }



}