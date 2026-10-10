package com.prince.api_app;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {
  List<Book> findByAuthorContainingIgnoreCase(String author);
  Book findFirstByOrderByPriceAsc();
  Book findFirstByOrderByPriceDesc();
 ;

}
