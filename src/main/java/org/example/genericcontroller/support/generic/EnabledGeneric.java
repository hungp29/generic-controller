package org.example.genericcontroller.support.generic;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enabled Generic.
 *
 * @author hungp
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(GenericConfiguration.class)
public @interface EnabledGeneric {

    /**
     * Package to scan generic object
     *
     * @return package
     */
    String scan() default "";
}
