package org.example.genericcontroller.app.author;

import org.example.genericcontroller.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

    @Query("SELECT author.name FROM Author author where author.id = :id")
    String getName(int id);

    Author getFirstById(int id);
}
