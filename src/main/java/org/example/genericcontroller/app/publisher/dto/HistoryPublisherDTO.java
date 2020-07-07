package org.example.genericcontroller.app.publisher.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.genericcontroller.entity.HistoryPublisher;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.support.generic.template.DTOTemplate;

@Data
@NoArgsConstructor
@MappingClass(HistoryPublisher.class)
public class HistoryPublisherDTO extends DTOTemplate {

    private int id;

    private String name;
}
