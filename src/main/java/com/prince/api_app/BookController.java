package com.prince.api_app;

import org.springframework.web.bind.annotation.GetMapping;
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
                books.add(new Book(2,"Land of Shadows","Roland Mills",59.00));
                books.add(new Book(3,"Antimony","King Pappy",45.60));
                books.add(new Book(4,"Renewed Being","JK Rolling",59.00));
        }

        @GetMapping
        public List<Book> getBooks(){
                return books;
        }


}
