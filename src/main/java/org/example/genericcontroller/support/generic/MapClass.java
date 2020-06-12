package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface MapClass {

    Class<? extends Audit> value();
}
