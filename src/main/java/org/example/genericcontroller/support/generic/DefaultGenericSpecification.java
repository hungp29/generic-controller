package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.exception.generic.GenericFieldNameIncorrectException;
import org.example.genericcontroller.exception.generic.GenericSelectionEmptyException;
import org.example.genericcontroller.support.generic.utils.MappingUtils;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Default Generic Specification.
 *
 * @author hungp
 */
public class DefaultGenericSpecification {

    /**
     * Auto build specification.
     *
     * @param <T> generic of Entity
     * @return Generic Specification instance
     */
    public static <T extends Audit> GenericSpecification<T> autoBuildSpecification() {
        return (root, query, cb, dtoType, filter, count, collection) -> {
            Class<? extends Audit> entityType = ObjectUtils.getAnnotation(dtoType, MappingClass.class).value();
            List<String> entityFieldPaths;
            if (!collection) {
                entityFieldPaths = MappingUtils.getEntityMappingFieldPaths(dtoType, filter, count);
            } else {
                entityFieldPaths = MappingUtils.getEntityMappingFieldPathsCollection(dtoType, filter);
            }

            if (!CollectionUtils.isEmpty(entityFieldPaths)) {
                List<Selection<?>> selections = new ArrayList<>();
                for (String entityFieldPath : entityFieldPaths) {
                    Path<?> path = buildPath(root, entityFieldPath, entityType);
                    if (null != path) {
                        selections.add(path.alias(entityFieldPath));
                    }
                }

                query.multiselect(selections);
            } else if (!count) {
                throw new GenericSelectionEmptyException("Cannot found any selection to build select clause");
            }
        };

    }

    /**
     * Build path for selection.
     *
     * @param from            From instance (root, join)
     * @param entityFieldPath entity field path
     * @param entityType      entity type
     * @return path
     */
    private static Path<?> buildPath(From<?, ?> from, String entityFieldPath, Class<?> entityType) {
        if (null != from && !StringUtils.isEmpty(entityFieldPath) && null != entityType) {
            String[] entityPaths = entityFieldPath.split(Constants.DOT_REGEX);

            Field entityField = ObjectUtils.getField(entityType, entityPaths[0], true);
            if (null != entityField) {
                if (entityPaths.length > 1 && Converter.isForeignKeyField(entityField)) {
                    Class<?> innerClass = entityField.getType();
                    // Override inner class if field is collection
                    if (ObjectUtils.fieldIsCollection(entityField)) {
                        innerClass = ObjectUtils.getGenericField(entityField);
                    }

                    // If join is exist, get join from From instance, otherwise create new join
                    Join<?, ?> join = DuplicateChecker.existJoin(from, innerClass);
                    if (null == join) {
                        join = from.join(entityPaths[0], JoinType.LEFT);
                    }
                    String nextPath = entityFieldPath.substring(entityFieldPath.indexOf(Constants.DOT) + 1);

                    return buildPath(join, nextPath, innerClass);
                } else {
                    return from.get(entityPaths[0]);
                }
            } else {
                throw new GenericFieldNameIncorrectException(String
                        .format("Cannot found field '%s' in entity '%s'", entityPaths[0], entityType.getSimpleName()));
            }
        }
        return null;
    }
}
