package org.example.genericcontroller.support.generic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define field path mapping between Data Transfer Object and Entity.
 *
 * @author hungp
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MappingField {

    /**
     * Path of Data Transfer Object field.
     *
     * @return path of field
     */
    String dtoField() default "";

    /**
     * Path of Entity field.
     *
     * @return path of field
     */
    String entityField() default "";
}
