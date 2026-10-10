package com.prince.api_app;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

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
        System.out.println("TOOL CALLED: getCheapestBook()");
        return bookRepository.findFirstByOrderByPriceAsc();
    }

    @Tool(description = "Get the most Expensive book in the database")
    public Book getMostExpensive() {
        System.out.println("TOOL CALLED: getMostExpensive()");
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

    @Tool(description = "Get a list of all authors in Ascending order of book price")
    public List<Book> getAscending(){
        System.out.println("TOOL CALLED: getAscending");
        return
    }

    @Tool(description = "Get a list of all authors in descending order of book price")
    public List<Book> getAscending(){
        System.out.println("TOOL CALLED: getAscending");
        return
    }


}