package org.example.genericcontroller.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "author_address",
            joinColumns = @JoinColumn(name = "author_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id"))
    private List<Address> addresses;
}
