package org.example.genericcontroller.app.author.dto;

import org.example.genericcontroller.entity.Author;
import org.example.genericcontroller.support.generic.MappingClass;

import java.time.LocalDateTime;

@MappingClass(Author.class)
public class AuthorDTO {

    private int id;

    private String name;

    private LocalDateTime dob;
}
