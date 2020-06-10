package org.example.genericcontroller.support.defaulthttp;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.exception.GenericFieldNameIncorrectException;
import org.example.genericcontroller.exception.GenericSelectionEmptyException;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

public class DefaultRepositoryImpl<T extends Audit> extends SimpleJpaRepository<T, Serializable> implements DefaultRepository<T> {

    private EntityManager em;

    public DefaultRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.em = em;
    }

    @Override
    public List<Tuple> findAll(Class<?> dtoType, @Nullable String[] filter, Specification<T> spec) {
        System.out.println("FIND ALL");
        List<Tuple> tuples = getDTOQuery(dtoType, filter, spec, Sort.unsorted()).getResultList();
        return tuples;
    }

    protected TypedQuery<Tuple> getDTOQuery(Class<?> dtoType, @Nullable String[] filter, @Nullable Specification<T> spec, Sort sort) {
        return getDTOQuery(dtoType, filter, spec, getDomainClass(), sort);
    }

    protected <S extends T> TypedQuery<Tuple> getDTOQuery(Class<?> dtoType, @Nullable String[] filter, @Nullable Specification<S> spec,
                                                          Class<S> domainClass, Sort sort) {
        Validator.validateObjectConfiguration(dtoType, MapClass.class);

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createQuery(Tuple.class);

        Root<S> root = applySpecificationToCriteria(spec, domainClass, query);

        Selection<?>[] selections = buildMultiSelect(dtoType, filter, root);
        if (selections.length > 0) {
            query.multiselect(selections);
        } else {
            throw new GenericSelectionEmptyException("Cannot found any selection");
        }

        if (sort.isSorted()) {
            query.orderBy(toOrders(sort, root, builder));
        }

        return applyRepositoryMethodMetadata(em.createQuery(query));
    }

    private <S extends T> Selection<?>[] buildMultiSelect(Class<?> dtoType, @Nullable String[] filter, Root<S> root) {
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
                        selections.add(path.alias(filterField));
                    }
                }
            }
        }
        return selections.toArray(new Selection<?>[0]);
    }

    private Path<?> buildPath(From<?, ?> from, String entityFieldPath, Class<?> entityClass) {
        if (null != from && !StringUtils.isEmpty(entityFieldPath) && null != entityClass) {
            String[] entityPaths = entityFieldPath.split(Constants.DOT_REGEX);

            Field entityField = ObjectUtils.getField(entityClass, entityPaths[0]);
            if (null != entityField) {
                if (entityPaths.length > 1 && Converter.isForeignKeyField(entityField)) {
                    Join<?, ?> join = JoinChecker.existJoin(from, entityField.getType());
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

    private <S> TypedQuery<S> applyRepositoryMethodMetadata(TypedQuery<S> query) {
        CrudMethodMetadata metadata = getRepositoryMethodMetadata();
        if (metadata == null) {
            return query;
        }

        LockModeType type = metadata.getLockModeType();
        return type == null ? query : query.setLockMode(type);
    }

    private <S, U extends T> Root<U> applySpecificationToCriteria(@Nullable Specification<U> spec, Class<U> domainClass,
                                                                  CriteriaQuery<S> query) {

        Assert.notNull(domainClass, "Domain class must not be null!");
        Assert.notNull(query, "CriteriaQuery must not be null!");

        Root<U> root = query.from(domainClass);

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
    }
}
