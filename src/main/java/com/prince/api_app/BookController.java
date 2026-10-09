package com.prince.api_app;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
        private final BookRepository bookRepository;


        public  BookController(BookRepository bookRepository){
            this.bookRepository = bookRepository;
        }

        @GetMapping
        public List<Book> getBooks(){
                return bookRepository.findAll();
        }

        @GetMapping("/{id}")
        public Book getBooks(@PathVariable int id){
                return bookRepository.findById(id).orElse(null);
        }

        @GetMapping("/total")
        public int getTotal(){
                return (int) bookRepository.count();
        }

        @GetMapping("/cheapest")
        public Book getCheapest(){
                List<Book> books = bookRepository.findAll();
                Book cheapest = books.get(0);
                for (Book b : books){
                        if (b.getPrice() < cheapest.getPrice() ) {
                                cheapest = b;
                        }

                }
                return cheapest;
        }

        @GetMapping("/mostexpensive")
        public Book getMostExpensive(){
                List<Book> books = bookRepository.findAll();
                Book expensive = books.get(0);
                for (Book b : books){
                        if (b.getPrice() > expensive.getPrice() ) {
                                expensive = b;
                        }

                }
                return expensive;
        }

        @GetMapping("/author/{name}")
        public List<Book> getByAuthor(@PathVariable String name){
                return bookRepository.findByAuthorContainingIgnoreCase(name);
        }

        @PostMapping
        public Book addBook(@RequestBody Book book){
                return bookRepository.save(book);
        }




}
