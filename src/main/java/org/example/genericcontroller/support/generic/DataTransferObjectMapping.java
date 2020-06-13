package org.example.genericcontroller.support.generic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Data Transfer Object Mapping.
 *
 * @author hungp
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DataTransferObjectMapping {

    /**
     * Define for request object of Create method.
     *
     * @return Data Transfer Object of Create method (request)
     */
    Class<?> forCreateRequest() default None.class;

    /**
     * Define for response object of Create method.
     *
     * @return Data Transfer Object of Create method (response)
     */
    Class<?> forCreateResponse() default None.class;

    /**
     * Define for response object of Read method.
     *
     * @return Data Transfer Object of Read method (response)
     */
    Class<?> forRead() default None.class;

    /**
     * Define for request object of Update  method.
     *
     * @return Data Transfer Object of Update method (request)
     */
    Class<?> forUpdateRequest() default None.class;

    /**
     * Define for response object of Update method.
     *
     * @return Data Transfer Object of Update method (response)
     */
    Class<?> forUpdateResponse() default None.class;

    /**
     * Default None Class.
     */
    class None {
    }
}
