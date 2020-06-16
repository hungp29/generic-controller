package org.example.genericcontroller.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.genericcontroller.app.book.dto.BookBasicInfo;
import org.example.genericcontroller.support.generic.DataTransferObjectMapping;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DataTransferObjectMapping(forRead = BookBasicInfo.class)
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

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "book_history_publisher",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "history_publisher_id"))
    private List<HistoryPublisher> historyPublishers;
}
