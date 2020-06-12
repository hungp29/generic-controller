package org.example.genericcontroller.support.generic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DTOMappingEntity {

    Class<?> createRequest() default void.class;

    Class<?> createResponse() default void.class;

    Class<?> read() default void.class;

    Class<?> updateRequest() default void.class;

    Class<?> updateResponse() default void.class;

}
