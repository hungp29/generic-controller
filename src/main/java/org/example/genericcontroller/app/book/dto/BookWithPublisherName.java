package org.example.genericcontroller.app.book.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookWithPublisherName {

    private int bookId;

    private String bookName;

    private String publisherName;
}
