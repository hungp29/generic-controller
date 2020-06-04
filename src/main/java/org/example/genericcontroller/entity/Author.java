package org.example.genericcontroller.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Author extends Audit {

    private String name;

    private LocalDate dob;

    @ManyToMany(mappedBy = "authors", cascade = CascadeType.PERSIST)
    private List<Book> books;
}
