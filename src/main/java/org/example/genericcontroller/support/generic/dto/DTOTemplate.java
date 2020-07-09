package org.example.genericcontroller.support.generic.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.support.generic.exception.ConfigurationInvalidException;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.utils.ObjectUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * DTO Template.
 *
 * @author hungp
 */
public class DTOTemplate implements Serializable {

    /**
     * Get Entity is mapping with DTO.
     *
     * @return Entity class
     */
    @JsonIgnore
    public Class<? extends Audit> getEntityMapping() {
        MappingClass mappingClass = ObjectUtils.getAnnotation(this.getClass(), MappingClass.class);
        if (null != mappingClass) {
            return mappingClass.value();
        }
        throw new ConfigurationInvalidException(this.getClass().getName() + " isn't config MappingClass");
    }

    /**
     * Get all fields of DTO class.
     *
     * @return list {@link Field}
     */
    @JsonIgnore
    public List<Field> getFields() {
        return ObjectUtils.getFields(this.getClass(), true);
    }
}
