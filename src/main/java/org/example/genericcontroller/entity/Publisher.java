package org.example.genericcontroller.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Publisher extends Audit {

    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @OneToMany(mappedBy = "publisher", cascade = CascadeType.PERSIST)
    private List<Book> books;
}
