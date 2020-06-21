package org.example.genericcontroller.support.generic.utils;

import org.example.genericcontroller.exception.generic.GenericClassInvalidException;
import org.example.genericcontroller.exception.generic.GenericException;
import org.example.genericcontroller.utils.ObjectUtils;

import java.lang.annotation.Annotation;

/**
 * Validator Utils.
 *
 * @author hungp
 */
public class Validator {

    /**
     * Prevent new instance.
     */
    private Validator() {
    }

    /**
     * Validate Object has annotation configuration.
     *
     * @param objectType      object type
     * @param annotationTypes annotation types
     */
    @SafeVarargs
    public static void validateObjectConfiguration(Class<?> objectType, Class<? extends Annotation>... annotationTypes) {
        if (void.class.equals(objectType)) {
            throw new GenericClassInvalidException("Class is invalid");
        }
        if (null != objectType && null != annotationTypes) {
            for (Class<? extends Annotation> annotation : annotationTypes) {
                if (!ObjectUtils.hasAnnotation(objectType, annotation)) {
                    throw new GenericException(String.format("%s class don't have %s configuration", objectType.getSimpleName(), annotation.getSimpleName()));
                }
            }
        }
    }
}