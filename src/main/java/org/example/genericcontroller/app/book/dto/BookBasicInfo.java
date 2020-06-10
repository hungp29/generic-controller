package org.example.genericcontroller.app.book.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.genericcontroller.entity.Author;
import org.example.genericcontroller.entity.Book;
import org.example.genericcontroller.entity.Publisher;
import org.example.genericcontroller.support.defaulthttp.MapClass;

import java.util.List;

@Data
@MapClass(Book.class)
@AllArgsConstructor
public class BookBasicInfo {

    private int id;

    private String name;

    private String description;
}
