package com.prince.api_app;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
                return bookRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        }

        @GetMapping("/title/{title}")
        public List<Book> getByTitle(@PathVariable String title){
                return bookRepository.findByTitleContainingIgnoreCase(title);
        }

        @GetMapping("/total")
        public int getTotal(){
                return (int) bookRepository.count();
        }

        @GetMapping("/cheapest")
        public Book getCheapest(){
                List<Book> books = bookRepository.findAll();
                if (books.isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                }
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
                if (books.isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                }
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

         @PostMapping("/bulk")
        public List<Book> addBulkBooks(@RequestBody List<Book> books){
                return bookRepository.saveAll(books);
        }

        @GetMapping("/priceRange")
        public List<Book> getByPriceRange(@RequestParam double minPrice, @RequestParam double maxPrice){
                return bookRepository.findByPriceBetween(minPrice, maxPrice);
        }

        @PutMapping("/{id}")
        public Book updateBook(@PathVariable int id, @RequestBody Book book){
                Book existingBook = bookRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            
                existingBook.setTitle(book.getTitle());
                existingBook.setAuthor(book.getAuthor());
                existingBook.setPrice(book.getPrice());
                return bookRepository.save(existingBook);
                
        }

        @DeleteMapping("/{id}")
        public void deleteBook(@PathVariable int id){
              bookRepository.deleteById(id);
        }




}
