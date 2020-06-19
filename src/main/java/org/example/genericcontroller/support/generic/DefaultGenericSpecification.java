package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.exception.generic.GenericFieldNameIncorrectException;
import org.example.genericcontroller.support.generic.utils.DataTransferObjectUtils;
import org.example.genericcontroller.support.generic.utils.DuplicateChecker;
import org.example.genericcontroller.support.generic.utils.EntityUtils;
import org.example.genericcontroller.support.generic.utils.MappingUtils;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Default Generic Specification.
 *
 * @author hungp
 */
public class DefaultGenericSpecification implements GenericSpecification {

    /**
     * Auto build criteria.
     *
     * @param root            Root Entity
     * @param query           Criteria query
     * @param criteriaBuilder Criteria builder
     * @param dtoType         Data Transfer Object type
     * @param filter          list field accepted to get from database
     * @param collection      flat to detect build criteria for collection fields
     * @param <T>             generic of entity
     * @return Predicate instance
     */
    @Override
    public <T> Predicate buildCriteria(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, Class<?> dtoType, String[] filter, boolean collection) {
        Class<? extends Audit> entityType = DataTransferObjectUtils.getEntityType(dtoType);
        List<String> entityFieldPaths;
        if (!collection) {
            entityFieldPaths = MappingUtils.getEntityMappingFieldPaths(dtoType, filter, false);
        } else {
            entityFieldPaths = MappingUtils.getEntityMappingFieldPaths(dtoType, filter, true);
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
        }
        return null;
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
                if (entityPaths.length > 1 && EntityUtils.isForeignKey(entityField)) {
                    Class<?> innerClass = MappingUtils.getFieldType(entityField);

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
