package org.example.genericcontroller.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Address extends Audit {

    private String name;

    @ManyToMany(mappedBy = "addresses", cascade = CascadeType.PERSIST)
    private List<Author> authors;
}
