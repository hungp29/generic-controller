package org.example.genericcontroller.app.book.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.genericcontroller.app.author.dto.AuthorDTO;
import org.example.genericcontroller.app.publisher.dto.PublisherDTO;
import org.example.genericcontroller.entity.Book;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.support.generic.MappingField;

import java.util.List;

@Data
@MappingClass(Book.class)
@NoArgsConstructor
public class BookBasicInfo {

    private int id;

    private String name;

    private String description;

    @MappingField(entityField = "publisher.id")
    private int publisherId;

    @MappingField(entityField = "publisher.name")
    private String publisherName;

    private PublisherDTO publisher;

//    @MappingField(entityField = "publisher.name")
//    private String publisherName2;

    private List<AuthorDTO> authors;
}
