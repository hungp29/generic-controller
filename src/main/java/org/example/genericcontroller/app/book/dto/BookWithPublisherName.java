package org.example.genericcontroller.app.book.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.genericcontroller.support.generic.obj.DTOTemplate;

@Data
@AllArgsConstructor
public class BookWithPublisherName extends DTOTemplate {

    private int bookId;

    private String bookName;

    private String publisherName;
}
