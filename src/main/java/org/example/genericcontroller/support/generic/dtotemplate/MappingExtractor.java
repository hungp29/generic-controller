package org.example.genericcontroller.support.generic.dtotemplate;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.support.generic.exception.ConstructorInvalidException;
import org.example.genericcontroller.utils.ObjectUtils;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;

/**
 * Mapping extractor.
 *
 * @param <D> generic of DTO
 * @author hungp
 */
public class MappingExtractor<D extends DTOTemplate> {

    private static final String DTO_MUST_NOT_BE_NULL = "The given DTO must not be null!";
    private static final String DTO_TYPE_MUST_NOT_BE_NULL = "The given DTO type must not be null!";

    private D dtoObject;

    public MappingExtractor(D dtoObject) {
        Assert.notNull(dtoObject, DTO_MUST_NOT_BE_NULL);
        this.dtoObject = dtoObject;
    }

    public MappingExtractor(Class<D> dtoType) {
        Assert.notNull(dtoType, DTO_TYPE_MUST_NOT_BE_NULL);
        try {
            this.dtoObject = ObjectUtils.newInstance(dtoType);
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new ConstructorInvalidException("Cannot new instance for " + dtoType.getName());
        }
    }

    /**
     * Get Entity is mapping with DTO.
     *
     * @return Entity class
     */
    public Class<? extends Audit> getEntityMapping() {
        return dtoObject.getEntityMapping();
    }
}
