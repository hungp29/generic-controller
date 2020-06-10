package org.example.genericcontroller.app.book.old;

import org.example.genericcontroller.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OldBookRepository extends JpaRepository<Book, Integer> {
}
