package com.prince.api_app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
        private List<Book> books = new ArrayList<>();

        public  BookController(){
                books.add(new Book(1,"Golf of the City","Joshua kingsley",45.60));
                books.add(new Book(2,"Land of Shadows","Roland Mills",58.00));
                books.add(new Book(3,"Antimony","King Pappy",45.60));
                books.add(new Book(4,"Renewed Being","JK Rolling",59.00));
                books.add(new Book(5, "The Quiet Harbor", "Roland Mills", 32.50));
                books.add(new Book(6, "Salt and Iron", "Amara Osei", 72.00));
                books.add(new Book(7, "Paper Kingdoms", "Joshua Kingsley", 18.99));
                books.add(new Book(8, "Midnight in Accra", "Kofi Mensah", 41.00));
                books.add(new Book(9, "The Last Cartographer", "Elena Duarte", 65.75));
                books.add(new Book(10, "Small Fires", "Amara Osei", 27.30));
                books.add(new Book(11, "Echoes of Tomorrow", "King Pappy", 88.00));
                books.add(new Book(12, "A Garden of Clocks", "Elena Duarte", 14.50));
        }

        @GetMapping
        public List<Book> getBooks(){
                return books;
        }

        @GetMapping("/{id}")
        public Book getBooks(@PathVariable int id){
                for (Book b : books){
                        if (b.getId() == id ){
                                return b;
                        }
                }
                return null;
        }

        @GetMapping("/total")
        public int getTotal(){

                return books.size();
        }

        @GetMapping("/cheapest")
        public Book getCheapest(){
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
                List<Book> result = new ArrayList<>();
                for (Book b : books){
                        if (b.getAuthor().equalsIgnoreCase(name)){
                                result.add(b);
                        }
                }
                return result;
        }


}
