package org.example.genericcontroller.support.generic.fieldconverter;


/**
 * Field Converter.
 *
 * @param <E> generic of Entity field
 * @param <D> generic of Data Transfer Object field
 * @author hungp
 */
public interface FieldConverter<E, D> {

    /**
     * Convert to Data Transfer Object field.
     *
     * @param entityField entity field value
     * @return Data Transfer Object field value
     */
    D convertToFieldDataTransferObject(E entityField);

    /**
     * Convert to Entity field.
     *
     * @param dtoField Data Transfer Object field value
     * @return Entity field value
     */
    E convertToFieldEntity(D dtoField);
}
