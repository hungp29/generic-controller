package org.example.genericcontroller.app.book;

import org.example.genericcontroller.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {
}
