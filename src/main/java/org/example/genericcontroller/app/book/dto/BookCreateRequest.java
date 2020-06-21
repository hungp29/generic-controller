package org.example.genericcontroller.app.book.dto;

import lombok.Data;
import org.example.genericcontroller.entity.Book;
import org.example.genericcontroller.support.generic.MappingClass;

/**
 * Book Create Request.
 *
 * @author hungp
 */
@Data
@MappingClass(Book.class)
public class BookCreateRequest {

    private String name;

    private String description;
}
