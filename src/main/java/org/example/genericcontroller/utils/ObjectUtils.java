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
import java.util.Collection;
import java.util.List;

/**
 * Object Utils.
 *
 * @author hungp
 */
public class ObjectUtils {

    /**
     * Prevent new instance.
     */
    private ObjectUtils() {
    }

    /**
     * Get generic class type from index.
     *
     * @param type  class type
     * @param index index of generic
     * @return type of generic
     */
    public static Class<?> getGenericClass(Class<?> type, int index) {
        if (null != type && index >= 0) {
            Type[] types = ((ParameterizedType) type.getGenericSuperclass()).getActualTypeArguments();
            if (null != types && types.length > 0 && index < types.length) {
                return (Class<?>) types[index];
            }
        }
        return null;
    }

    /**
     * Get generic class type by index 0.
     *
     * @param type class type
     * @return type of generic
     */
    public static Class<?> getGenericClass(Class<?> type) {
        return ObjectUtils.getGenericClass(type, 0);
    }

    /**
     * Get generic type of field by index.
     *
     * @param field the field of object
     * @param index the index of generic
     * @return type of generic
     */
    public static Class<?> getGenericField(Field field, int index) {
        if (null != field && index >= 0) {
            Type[] types = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
            if (null != types && types.length > 0 && index < types.length) {
                return (Class<?>) types[index];
            }
        }
        return null;
    }

    /**
     * Get generic type of field by index 0.
     *
     * @param field the field of object
     * @return type of generic
     */
    public static Class<?> getGenericField(Field field) {
        return getGenericField(field, 0);
    }

    /**
     * Check element has annotation
     *
     * @param annotatedElement the annotation element (class, method, field, annotation, ...)
     * @param annotationType   annotation type
     * @return true if element has annotation, otherwise return false
     */
    public static boolean hasAnnotation(AnnotatedElement annotatedElement, Class<? extends Annotation> annotationType) {
        return AnnotatedElementUtils.hasAnnotation(annotatedElement, annotationType);
    }

    /**
     * Get annotation from element.
     *
     * @param annotatedElement the annotation element (class, method, field, annotation, ...)
     * @param annotationType   annotation type
     * @param <T>              generic type of annotation
     * @return the annotation if if exist
     */
    public static <T extends Annotation> T getAnnotation(AnnotatedElement annotatedElement, Class<T> annotationType) {
        return getAnnotation(annotatedElement, annotationType, false);
    }

    /**
     * Get annotation from element (merge).
     *
     * @param annotatedElement he annotation element (class, method, field, annotation, ...)
     * @param annotationType   annotation type
     * @param merged           flag to detect find annotation with merge mode
     * @param <T>              generic type of annotation
     * @return
     */
    public static <T extends Annotation> T getAnnotation(AnnotatedElement annotatedElement, Class<T> annotationType, boolean merged) {
        if (merged) {
            return AnnotatedElementUtils.findMergedAnnotation(annotatedElement, annotationType);
        } else {
            return AnnotationUtils.getAnnotation(annotatedElement, annotationType);
        }
    }

    /**
     * Get value from annotation.
     *
     * @param annotation the annotation
     * @param attribute  the attribute name
     * @return the value
     */
    public static Object getValueOfAnnotation(Annotation annotation, String attribute) {
        if (null != annotation && !StringUtils.isEmpty(attribute)) {
            return AnnotationUtils.getValue(annotation, attribute);
        }
        return null;
    }

    /**
     * Get value from annotation.
     *
     * @param annotation the annotation
     * @param attribute  the attribute name
     * @param valueType  the type of value
     * @param <T>        generic type of value
     * @return the value after convert to type generic
     */
    public static <T> T getValueOfAnnotation(Annotation annotation, String attribute, Class<T> valueType) {
        Object value = getValueOfAnnotation(annotation, attribute);
        if (null != value && null != valueType && valueType.isAssignableFrom(value.getClass())) {
            return valueType.cast(value);
        }
        return null;
    }

    /**
     * Get all fields of object.
     *
     * @param type         type of object
     * @param lookingSuper looking to super type
     * @return list field
     */
    public static List<Field> getFields(Class<?> type, boolean lookingSuper) {
        List<Field> fields = new ArrayList<>();
        if (null != type) {
            Class<?> checkingClass = type;
            do {
                fields.addAll(Arrays.asList(checkingClass.getDeclaredFields()));
                if (lookingSuper) {
                    checkingClass = checkingClass.getSuperclass();
                } else {
                    checkingClass = null;
                }
            } while (null != checkingClass);
        }
        return fields;
    }

    /**
     * Get all fields of object (not looking to super).
     *
     * @param type type of object
     * @return list field
     */
    public static List<Field> getFields(Class<?> type) {
        return getFields(type, false);
    }

    /**
     * Get field of object.
     *
     * @param type         type of object
     * @param fieldName    field name
     * @param lookingSuper flag to detect looking to super
     * @return the field of object
     */
    public static Field getField(Class<?> type, String fieldName, boolean lookingSuper) {
        if (null != type && !StringUtils.isEmpty(fieldName)) {
            Class<?> checkingClass = type;
            do {
                try {
                    return checkingClass.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    if (lookingSuper) {
                        checkingClass = checkingClass.getSuperclass();
                    }
                }
            } while (null != checkingClass);
        }
        return null;
    }

    /**
     * Get field of object (not looking to super).
     *
     * @param type      type of object
     * @param fieldName field name
     * @return the field of object
     */
    public static Field getField(Class<?> type, String fieldName) {
        return getField(type, fieldName, false);
    }

    /**
     * Checking field is collection or not.
     *
     * @param field the field object
     * @return true if field is collection, otherwise return false
     */
    public static boolean fieldIsCollection(Field field) {
        return Collection.class.isAssignableFrom(field.getType());
    }
}
