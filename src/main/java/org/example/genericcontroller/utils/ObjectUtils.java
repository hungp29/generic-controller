package org.example.genericcontroller.utils;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectUtils {

    private ObjectUtils() {
    }

    public static Class<?> getGenericClass(Class<?> clazz, int index) {
        if (null != clazz && index >= 0) {
            Type[] types = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
            if (null != types && types.length > 0 && index < types.length) {
                return (Class<?>) types[index];
            }
        }
        return null;
    }

    public static Class<?> getGenericClass(Class<?> clazz) {
        return ObjectUtils.getGenericClass(clazz, 0);
    }

    public static boolean hasAnnotation(AnnotatedElement annotatedElement, Class<? extends Annotation> annotationType) {
        return AnnotatedElementUtils.hasAnnotation(annotatedElement, annotationType);
    }

    public static <T extends Annotation> T getAnnotation(AnnotatedElement annotatedElement, Class<T> annotationType) {
        return getAnnotation(annotatedElement, annotationType, false);
    }

    public static <T extends Annotation> T getAnnotation(AnnotatedElement annotatedElement, Class<T> annotationType, boolean merged) {
        if (merged) {
            return AnnotatedElementUtils.findMergedAnnotation(annotatedElement, annotationType);
        } else {
            return AnnotationUtils.getAnnotation(annotatedElement, annotationType);
        }
    }

    public static Object getValueOfAnnotation(Annotation annotation, String attribute) {
        if (null != annotation && !StringUtils.isEmpty(attribute)) {
            return AnnotationUtils.getValue(annotation, attribute);
        }
        return null;
    }

    public static <T> T getValueOfAnnotation(Annotation annotation, String attribute, Class<T> valueType) {
        Object value = getValueOfAnnotation(annotation, attribute);
        if (null != value && null != valueType && valueType.isAssignableFrom(value.getClass())) {
            return valueType.cast(value);
        }
        return null;
    }

    public static List<Field> getFields(Class<?> objectClass) {
        List<Field> fields = new ArrayList<>();
        if (null != objectClass) {
            Class<?> checkingClass = objectClass;
            do {
                fields.addAll(Arrays.asList(checkingClass.getDeclaredFields()));
                checkingClass = checkingClass.getSuperclass();
            } while (null != checkingClass);
        }
        return fields;
    }

    public static Field getField(Object object, String fieldName) {
        if (null != object && !StringUtils.isEmpty(fieldName)) {
            Class<?> checkingClass;
            if (object instanceof Class) {
                checkingClass = (Class<?>) object;
            } else {
                checkingClass = object.getClass();
            }
            do {
                try {
                    return checkingClass.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    checkingClass = checkingClass.getSuperclass();
                }
            } while (null != checkingClass);
        }
        return null;
    }
}
