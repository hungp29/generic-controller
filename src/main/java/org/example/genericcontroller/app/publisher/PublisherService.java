package org.example.genericcontroller.app.publisher;

import lombok.RequiredArgsConstructor;
import org.example.genericcontroller.entity.Book;
import org.example.genericcontroller.entity.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PublisherService {

    public final PublisherRepository publisherRepository;

    public boolean create() {
        Publisher publisher = new Publisher();
        publisher.setName("Name");
        publisher.setDescription("Desc");

        Book book1 = new Book();
        book1.setName("Book 1 in Publisher Controller");
        book1.setDescription("Book 1 in Publisher Controller");
        book1.setPublisher(publisher);

        Book book2 = new Book();
        book2.setName("Book 2 in Publisher Controller");
        book2.setDescription("Book 2 in Publisher Controller");
        book2.setPublisher(publisher);

        publisher.setBooks(Arrays.asList(book1, book2));

        save(publisher);
        return true;
    }

    public Optional<Publisher> findById(int id) {
        return publisherRepository.findById(id);
    }

    @Transactional
    public Publisher save(Publisher publisher) {
        return publisherRepository.save(publisher);
    }
}
