package org.example.genericcontroller.app.book;

import lombok.RequiredArgsConstructor;
import org.example.genericcontroller.app.book.dto.BookBasicInfo;
import org.example.genericcontroller.app.book.dto.BookCreateRequest;
import org.example.genericcontroller.entity.Book;
import org.example.genericcontroller.support.generic.DataTransferObjectMapping;
import org.example.genericcontroller.support.generic.GenericRestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/book")
@RequiredArgsConstructor
@DataTransferObjectMapping(
        forRead = BookBasicInfo.class,
        forCreateRequest = BookCreateRequest.class,
        forCreateResponse = BookBasicInfo.class
)
public class BookController extends GenericRestController<Book> {
}
