package org.example.genericcontroller.support.generic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generic disabled.
 *
 * @author hungp
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GenericDisabled {

    boolean create() default false;

    boolean read() default false;

    boolean readAll() default false;

    boolean update() default false;

    boolean delete() default false;
}
