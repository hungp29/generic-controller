package org.example.genericcontroller.app.book.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.genericcontroller.app.author.dto.AuthorDTO;
import org.example.genericcontroller.app.publisher.dto.HistoryPublisherDTO;
import org.example.genericcontroller.app.publisher.dto.PublisherDTO;
import org.example.genericcontroller.entity.Book;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.support.generic.MappingField;
import org.example.genericcontroller.support.generic.converter.LocalDateTimeConverter;
import org.example.genericcontroller.support.generic.obj.DTOTemplate;

import java.util.List;

@Data
@MappingClass(Book.class)
@NoArgsConstructor
public class BookBasicInfo extends DTOTemplate {

    private Integer id;

    private String name;

    private String description;

    private Integer year;

    @MappingField(entityField = "publisher.id")
    private int publisherId;
//
    @MappingField(entityField = "publisher.name")
    private String publisherName;

    private PublisherDTO publisher;

//    @MappingField(entityField = "publisher.name")
//    private String publisherName2;

    @MappingField(entityField = "authors")
    private List<AuthorDTO> authorTests;

    private List<HistoryPublisherDTO> historyPublishers;

    @MappingField(converter = LocalDateTimeConverter.class)
    private Long createAt;
}
