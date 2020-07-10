package org.example.genericcontroller.app.publisher.dto;

import lombok.Data;
import org.example.genericcontroller.entity.Publisher;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.support.generic.obj.DTOTemplate;

@Data
@MappingClass(Publisher.class)
public class PublisherDTO extends DTOTemplate {

    private int id;

    private String name;

    private String description;
}
