package org.example.genericcontroller.app.book.dto;

import lombok.Data;
import org.example.genericcontroller.entity.Book;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.support.generic.MappingField;
import org.example.genericcontroller.support.generic.converter.LocalDateTimeConverter;
import org.example.genericcontroller.support.generic.obj.DTOTemplate;

import java.util.List;

/**
 * Book Create Request.
 *
 * @author hungp
 */
@Data
@MappingClass(Book.class)
public class BookCreateRequest extends DTOTemplate {

    private String name;

    private String description;

    private Publisher publisher;

    private List<Author> authors;

    @MappingField(converter = LocalDateTimeConverter.class)
    private Long createAt;

    @MappingClass(org.example.genericcontroller.entity.Publisher.class)
    @Data
    public static class Publisher {

        private String name;

        private String description;
    }

    @Data
    @MappingClass(org.example.genericcontroller.entity.Author.class)
    public static class Author {
        private String name;
    }
}
