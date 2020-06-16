package org.example.genericcontroller.app.author.dto;

import lombok.Data;
import org.example.genericcontroller.entity.Author;
import org.example.genericcontroller.support.generic.MappingClass;

import java.time.LocalDate;
import java.util.List;

@Data
@MappingClass(Author.class)
public class AuthorDTO {

    private int id;

    private String name;

    private LocalDate dob;

    private List<AddressDTO> addresses;
}
