package org.example.genericcontroller.app.book;

import lombok.RequiredArgsConstructor;
import org.example.genericcontroller.entity.Book;
import org.example.genericcontroller.support.generic.DefaultRestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/book")
@RequiredArgsConstructor
public class BookController extends DefaultRestController<Book> {
}
