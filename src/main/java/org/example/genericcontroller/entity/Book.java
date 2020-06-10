package org.example.genericcontroller.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.genericcontroller.app.book.dto.BookBasicInfo;
import org.example.genericcontroller.support.defaulthttp.EntityMapClass;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@EntityMapClass(classForRead = BookBasicInfo.class)
public class Book extends Audit {

    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "author_book",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id"))
    private List<Author> authors;
}
