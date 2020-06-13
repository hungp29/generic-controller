package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define entity class for Data Transfer Object.
 *
 * @author hungp
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface MappingClass {

    /**
     * Class type of entity.
     *
     * @return Class type
     */
    Class<? extends Audit> value();
}
