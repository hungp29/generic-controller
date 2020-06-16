package org.example.genericcontroller.app.author.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.genericcontroller.entity.Address;
import org.example.genericcontroller.support.generic.MappingClass;

@Data
@NoArgsConstructor
@MappingClass(Address.class)
public class AddressDTO {

    private int id;

    private String name;
}
