package org.example.genericcontroller.app.publisher.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.genericcontroller.entity.HistoryPublisher;
import org.example.genericcontroller.support.generic.MappingClass;

@Data
@NoArgsConstructor
@MappingClass(HistoryPublisher.class)
public class HistoryPublisherDTO {

    private int id;

    private String name;
}
