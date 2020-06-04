package org.example.genericcontroller.app.author;

import lombok.RequiredArgsConstructor;
import org.example.genericcontroller.app.publisher.PublisherService;
import org.example.genericcontroller.entity.Author;
import org.example.genericcontroller.entity.Book;
import org.example.genericcontroller.entity.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final PublisherService publisherService;

    public boolean create() {
        Publisher publisher = publisherService.findById(1).orElse(null);

        Author author = new Author();
        author.setName("Author Controller");
        author.setDob(LocalDate.now());

        Book book1 = new Book();
        book1.setName("Book 1 in Author Controller");
        book1.setDescription("Book 1 in Author Controller");
        book1.setPublisher(publisher);
        book1.setAuthors(Collections.singletonList(author));

        Book book2 = new Book();
        book2.setName("Book 2 in Author Controller");
        book2.setDescription("Book 2 in Author Controller");
        book2.setPublisher(publisher);
        book2.setAuthors(Collections.singletonList(author));

        author.setBooks(Arrays.asList(book1, book2));
        save(author);
        return true;
    }

    @Transactional
    public Author save(Author author) {
        return authorRepository.save(author);
    }
}
