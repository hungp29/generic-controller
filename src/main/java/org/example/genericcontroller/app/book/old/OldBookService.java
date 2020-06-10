package org.example.genericcontroller.app.book.old;

import lombok.RequiredArgsConstructor;
import org.example.genericcontroller.app.book.BookCustomRepository;
import org.example.genericcontroller.entity.Author;
import org.example.genericcontroller.entity.Book;
import org.example.genericcontroller.entity.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class OldBookService {

    private final OldBookRepository oldBookRepository;
    private final BookCustomRepository bookCustomRepository;

    public void get(int id) {
        bookCustomRepository.get(id);
    }

    public boolean create() {
        Publisher publisher = new Publisher();
        publisher.setName("Publisher in Book Controller");
        publisher.setDescription("Publisher in Book Controller");


        Book book = new Book();
        book.setName("Book Controller");
        book.setDescription("Book Controller");
        book.setPublisher(publisher);

        Author author1 = new Author();
        author1.setName("Author 1 in Book Controller");
        author1.setDob(LocalDate.now());

        Author author2 = new Author();
        author2.setName("Author 2 in Book Controller");
        author2.setDob(LocalDate.now());

        book.setAuthors(Arrays.asList(author1, author2));
        save(book);
        return true;
    }

    @Transactional
    public Book save(Book book) {
        return oldBookRepository.save(book);
    }
}
