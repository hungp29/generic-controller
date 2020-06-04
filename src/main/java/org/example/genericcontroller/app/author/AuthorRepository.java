package org.example.genericcontroller.app.author;

import org.example.genericcontroller.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
}
