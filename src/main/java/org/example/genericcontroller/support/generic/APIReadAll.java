package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.support.generic.APIGeneric.APIGenericMethod;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for mapping Read All.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@APIGeneric(method = RequestMethod.GET, genericMethod = APIGenericMethod.READ_ALL)
public @interface APIReadAll {
    /**
     * Alias for {@link APIGeneric#name}.
     */
    @AliasFor(annotation = APIGeneric.class)
    String name() default "";

    /**
     * Alias for {@link APIGeneric#value}.
     */
    @AliasFor(annotation = APIGeneric.class)
    String[] value() default {};

    /**
     * Alias for {@link APIGeneric#path}.
     */
    @AliasFor(annotation = APIGeneric.class)
    String[] path() default {};

    /**
     * Alias for {@link APIGeneric#params}.
     */
    @AliasFor(annotation = APIGeneric.class)
    String[] params() default {};

    /**
     * Alias for {@link APIGeneric#headers}.
     */
    @AliasFor(annotation = APIGeneric.class)
    String[] headers() default {};

    /**
     * Alias for {@link APIGeneric#consumes}.
     */
    @AliasFor(annotation = APIGeneric.class)
    String[] consumes() default {};

    /**
     * Alias for {@link APIGeneric#produces}.
     */
    @AliasFor(annotation = APIGeneric.class)
    String[] produces() default {};
}
