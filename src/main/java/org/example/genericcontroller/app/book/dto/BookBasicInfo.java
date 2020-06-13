package org.example.genericcontroller.app.book.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.genericcontroller.app.publisher.dto.PublisherDTO;
import org.example.genericcontroller.entity.Book;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.support.generic.MappingField;

@Data
@MappingClass(Book.class)
@AllArgsConstructor
public class BookBasicInfo {

    private int id;

    private String name;

    private String description;

    @MappingField(entityField = "publisher.id")
    private int publisherId;

    @MappingField(entityField = "publisher.name")
    private String publisherName;

    private PublisherDTO publisher;

//    @MapField(entityField = "publisher.name")
//    private String publisherName2;

//    private String authors;
}
