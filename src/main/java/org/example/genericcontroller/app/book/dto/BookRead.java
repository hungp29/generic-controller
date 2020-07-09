package org.example.genericcontroller.app.book.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.genericcontroller.entity.Book;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.support.generic.dto.DTOTemplate;

@Data
@MappingClass(Book.class)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BookRead extends DTOTemplate {

    private Integer id;

    private String name;

    private String description;

    private Integer year;
}
