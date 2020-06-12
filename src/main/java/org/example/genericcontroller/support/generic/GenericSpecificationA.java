package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.exception.generic.GenericDuplicateException;
import org.example.genericcontroller.exception.generic.GenericFieldNameIncorrectException;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Selection;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GenericSpecificationA {

    public static <T extends Audit> GenericSpecification<T> autoBuildSpecification() {
        return (root, query, cb, dtoType, filter) -> {

            List<Selection<?>> selections = new ArrayList<>();
            List<String> dtoFieldNames = Converter.getFieldNames(dtoType);
            Class<?> entityClass = ObjectUtils.getAnnotation(dtoType, MapClass.class).value();
            if (!CollectionUtils.isEmpty(dtoFieldNames)) {
                if (null != filter) {
                    List<String> filterField = Arrays.asList(filter);
                    dtoFieldNames = dtoFieldNames.stream().filter(filterField::contains).collect(Collectors.toList());
                }

                for (String filterField : dtoFieldNames) {
                    Field dtoField = ObjectUtils.getField(dtoType, filterField);
                    String entityFieldName = Converter.getEntityFieldNameByDTOField(dtoField);

                    if (!StringUtils.isEmpty(entityFieldName)) {
                        Path<?> path = buildPath(root, entityFieldName, entityClass);
                        if (null != path) {
                            if (!StringUtils.isEmpty(path.getAlias()) && !filterField.equals(path.getAlias())) {
                                throw new GenericDuplicateException(String
                                        .format("Alias for field '%s.%s' is exist: '%s' and '%s'",
                                                dtoType.getSimpleName(), dtoField.getName(), path.getAlias(), filterField));
                            }
                            selections.add(path.alias(filterField));
                        }
                    }
                }//((SingularAttributePath) selections.get(5)).getAttribute()
            }

            if (!CollectionUtils.isEmpty(selections)) {
                query.multiselect(selections);
            }
        };

    }

    private static Path<?> buildPath(From<?, ?> from, String entityFieldPath, Class<?> entityClass) {
        if (null != from && !StringUtils.isEmpty(entityFieldPath) && null != entityClass) {
            String[] entityPaths = entityFieldPath.split(Constants.DOT_REGEX);

            Field entityField = ObjectUtils.getField(entityClass, entityPaths[0]);
            if (null != entityField) {
                if (entityPaths.length > 1 && Converter.isForeignKeyField(entityField)) {
                    Join<?, ?> join = DuplicateChecker.existJoin(from, entityField.getType());
                    if (null == join) {
                        join = from.join(entityPaths[0]);
                    }
                    String nextPath = entityFieldPath.substring(entityFieldPath.indexOf(Constants.DOT) + 1);
                    return buildPath(join, nextPath, entityField.getType());
                } else {
                    return from.get(entityPaths[0]);
                }
            } else {
                throw new GenericFieldNameIncorrectException(String
                        .format("Cannot found field '%s' in entity '%s'", entityPaths[0], entityClass.getSimpleName()));
            }
        }

        return null;
    }
}
