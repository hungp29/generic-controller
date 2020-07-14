package org.example.genericcontroller.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "book")
@Data
@EqualsAndHashCode(callSuper = true)
public class BookMinor extends Audit {

    private String name;

    @Column(columnDefinition = "text")
    private String description;

    private Integer year;

}
