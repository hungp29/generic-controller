package org.example.genericcontroller.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Object Utils.
 *
 * @author hungp
 */
@Slf4j
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
                    } else {
                        checkingClass = null;
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

    /**
     * Check array is empty or not.
     *
     * @param array the array need to check
     * @return true if array is empty, otherwise return false
     */
    public static boolean isEmpty(Object[] array) {
        return null == array || array.length == 0;
    }

    /**
     * New instance of class.
     *
     * @param type           type of object
     * @param parameterTypes parameter types
     * @param parameters     parameter values
     * @param <T>            generic of object
     * @return new instance
     * @throws NoSuchMethodException     if a matching method is not found.
     * @throws IllegalAccessException    if this {@code Constructor} object
     *                                   is enforcing Java language access control and the underlying
     *                                   constructor is inaccessible.
     * @throws InvocationTargetException if the underlying constructor
     *                                   throws an exception.
     * @throws InstantiationException    if the class that declares the
     *                                   underlying constructor represents an abstract class.
     */
    public static <T> T newInstance(Class<T> type, Class<?>[] parameterTypes, Object[] parameters)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (null != type) {
            Constructor<T> constructors = type.getDeclaredConstructor(parameterTypes);
            return constructors.newInstance(parameters);
        }
        return null;
    }

    /**
     * New instance of class by default constructor.
     *
     * @param type type of object
     * @param <T>  generic of object
     * @return new instance
     * @throws NoSuchMethodException     if a matching method is not found.
     * @throws IllegalAccessException    if this {@code Constructor} object
     *                                   is enforcing Java language access control and the underlying
     *                                   constructor is inaccessible.
     * @throws InvocationTargetException if the underlying constructor
     *                                   throws an exception.
     * @throws InstantiationException    if the class that declares the
     *                                   underlying constructor represents an abstract class.
     */
    public static <T> T newInstance(Class<T> type)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return newInstance(type, null, null);
    }

    /**
     * Get value of field of object.
     *
     * @param object    object
     * @param fieldName the field name of object
     * @return the value of field
     * @throws IllegalAccessException if this {@code Field} object
     *                                is enforcing Java language access control and the underlying
     *                                field is inaccessible.
     */
    public static Object getValueOfField(Object object, String fieldName) throws IllegalAccessException {
        Object value = null;
        Field field = ObjectUtils.getField(object.getClass(), fieldName, true);
        if (null != field) {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            // Set value for field of object
            value = field.get(object);
            field.setAccessible(accessible);
        }
        return value;
    }

    /**
     * Get value of field of object.
     *
     * @param object    object
     * @param fieldName the field name of object
     * @param fieldType field type
     * @param <T>       generic of field
     * @return the value of field
     * @throws IllegalAccessException if this {@code Field} object
     *                                is enforcing Java language access control and the underlying
     *                                field is inaccessible.
     */
    public static <T> T getValueOfField(Object object, String fieldName, Class<T> fieldType) throws IllegalAccessException {
        Object value = ObjectUtils.getValueOfField(object, fieldName);
        if (null != value && fieldType.isAssignableFrom(value.getClass())) {
            return fieldType.cast(value);
        }
        return null;
    }

    /**
     * Set value for field of object.
     *
     * @param object     object to set value
     * @param fieldName  field name of object
     * @param value      value to set
     * @param acceptNull flag to detect set null value to field
     * @throws IllegalAccessException if this {@code Field} object
     *                                is enforcing Java language access control and the underlying
     *                                field is either inaccessible or final.
     */
    public static void setValueForField(Object object, String fieldName, Object value, boolean acceptNull)
            throws IllegalAccessException {
        Field field = ObjectUtils.getField(object.getClass(), fieldName, true);
        if (null != field) {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            // Set value for field of object
            if (null == value && field.getType().isPrimitive()) {
                log.warn("Cannot set null for primitive field '" + fieldName + "'");
            } else if (acceptNull || null != value) {
                field.set(object, value);
            }
            field.setAccessible(accessible);
        }
    }

    /**
     * Set value for field of object (not accept null value).
     *
     * @param object    object to set value
     * @param fieldName field name of object
     * @param value     value to set
     * @throws IllegalAccessException if this {@code Field} object
     *                                is enforcing Java language access control and the underlying
     *                                field is either inaccessible or final.
     */
    public static void setValueForField(Object object, String fieldName, Object value) throws IllegalAccessException {
        setValueForField(object, fieldName, value, false);
    }

    /**
     * New instance collection.
     *
     * @param collectionType collection type
     * @return collection instance
     * @throws NoSuchMethodException if collection type is not supported
     */
    public static Collection newInstanceCollection(Class<?> collectionType) throws NoSuchMethodException {
        if (List.class.equals(collectionType)) {
            return new ArrayList<>();
        } else if (Deque.class.equals(collectionType)) {
            return new ArrayDeque<>();
        } else if (Queue.class.equals(collectionType)) {
            return new PriorityQueue<>();
        } else if (SortedSet.class.equals(collectionType)) {
            return new TreeSet<>();
        } else if (Set.class.equals(collectionType)) {
            return new HashSet<>();
        }
        throw new NoSuchMethodException("Cannot found '" + (null != collectionType ? collectionType.getSimpleName() : "null") + "' class");
    }

    /**
     * Checking field is number.
     *
     * @param field field need to check
     * @return true if field is number
     */
    public static boolean isNumber(Field field) {
        return Number.class.isAssignableFrom(field.getType()) ||
                short.class.equals(field.getType()) ||
                int.class.equals(field.getType()) ||
                long.class.equals(field.getType()) ||
                float.class.equals(field.getType()) ||
                double.class.equals(field.getType());
    }

    /**
     * Convert map data to object.
     *
     * @param mapData map data
     * @param type    object class
     * @return new instance of object
     */
    public static Object convertMapToObject(Map<String, ?> mapData, Class<?> type) {
        if (null != mapData && null != type) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.convertValue(mapData, type);
        }
        return null;
    }
}
