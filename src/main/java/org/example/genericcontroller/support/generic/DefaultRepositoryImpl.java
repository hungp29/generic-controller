package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.support.generic.utils.EntityUtils;
import org.example.genericcontroller.support.generic.utils.MappingUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

/**
 * Implementation for DefaultRepository.
 *
 * @param <T> generic of Entity
 * @author hungp
 */
public class DefaultRepositoryImpl<T extends Audit> extends SimpleJpaRepository<T, Serializable> implements DefaultRepository<T> {

    private EntityManager em;

    public DefaultRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.em = em;
    }

    /**
     * Find All.
     *
     * @param dtoType Data Transfer Object type
     * @param filter  filter field
     * @param spec    GenericSpecification intance
     * @return
     */
    @Override
    public List<Object> findAll(Class<?> dtoType, @Nullable String[] filter, GenericSpecification<T> spec) {
        List<Tuple> tuples = getDataTransferObjectQuery(dtoType, filter, spec, Sort.unsorted()).getResultList();
        // Convert list tuple to map
        List<Map<String, Object>> records = MappingUtils.convertTupleToMapRecord(tuples, MappingUtils.getEntityMappingFieldPaths(dtoType, filter, false));

        List<Tuple> collectionTuples = getDataTransferObjectCollectionQuery(dtoType, filter, spec, getDomainClass(), records).getResultList();
        List<Map<String, Object>> collectionRecords = MappingUtils.convertTupleToMapRecord(collectionTuples, MappingUtils.getEntityMappingFieldPaths(dtoType, filter, true));

        List<Map<String, Object>> mergeRecords = MappingUtils.merge(records, collectionRecords, dtoType);
        return MappingUtils.convertToListDataTransferObject(mergeRecords, dtoType, filter);
    }

    /**
     * Get Data Transfer Object.
     *
     * @param dtoType Data Transfer Object type
     * @param filter  filter field
     * @param spec    Generic Specification
     * @param sort    Sort instance
     * @return TypeQuery instance
     */
    protected TypedQuery<Tuple> getDataTransferObjectQuery(Class<?> dtoType, @Nullable String[] filter,
                                                           @Nullable GenericSpecification<T> spec, Sort sort) {
        return getDataTransferObjectQuery(dtoType, filter, spec, getDomainClass(), sort);
    }

    /**
     * Get Data Transfer Object.
     *
     * @param dtoType     Data Transfer Object type
     * @param filter      filter field
     * @param spec        Generic Specification
     * @param domainClass Entity class
     * @param sort        Sort instance
     * @param <S>         generic of entity
     * @return TypeQuery instance
     */
    protected <S extends T> TypedQuery<Tuple> getDataTransferObjectQuery(Class<?> dtoType, @Nullable String[] filter,
                                                                         @Nullable GenericSpecification<S> spec,
                                                                         Class<S> domainClass, Sort sort) {
        Assert.notNull(dtoType, "DTO Type must not be null!");

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createQuery(Tuple.class);

        Root<S> root = applySpecificationToCriteria(spec, domainClass, query, dtoType, filter, false, false);

        if (sort.isSorted()) {
            query.orderBy(toOrders(sort, root, builder));
        }

        return applyRepositoryMethodMetadata(em.createQuery(query));
    }

    protected <S extends T> TypedQuery<Tuple> getDataTransferObjectCollectionQuery(Class<?> dtoType, @Nullable String[] filter,
                                                                                   @Nullable GenericSpecification<S> spec,
                                                                                   Class<S> domainClass, List<Map<String, Object>> records) {
        Assert.notNull(dtoType, "DTO Type must not be null!");

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createQuery(Tuple.class);

        Root<S> root = applySpecificationToCriteria(spec, domainClass, query, dtoType, filter, false, true);

//        if (sort.isSorted()) {
//            query.orderBy(toOrders(sort, root, builder));
//        }

        List<String> primaryKeys = EntityUtils.getPrimaryKey(domainClass);

        List<Predicate> conditions = new ArrayList<>();
        for (String key : primaryKeys) {
            List<Object> conditionValue = records.stream().map(record -> record.get(key)).distinct().collect(Collectors.toList());
            conditions.add(root.get(key).in(conditionValue));
        }
        Predicate predicate = builder.and(conditions.toArray(new Predicate[0]));

        query.where(predicate);


        return applyRepositoryMethodMetadata(em.createQuery(query));
    }

    protected <S extends T> TypedQuery<Long> getCountDataTransferObjectQuery(Class<?> dtoType,
                                                                             @Nullable GenericSpecification<S> spec,
                                                                             Class<S> domainClass) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<S> root = applySpecificationToCriteria(spec, domainClass, query, dtoType, null, true, false);

        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        // Remove all Orders the Specifications might have applied
        query.orderBy(Collections.<Order>emptyList());

        return em.createQuery(query);
    }

    /**
     * Apply repository method metadata.
     *
     * @param query Type query
     * @param <S>   Generic of Query
     * @return Type Query instance
     */
    private <S> TypedQuery<S> applyRepositoryMethodMetadata(TypedQuery<S> query) {
        CrudMethodMetadata metadata = getRepositoryMethodMetadata();
        if (metadata == null) {
            return query;
        }

        LockModeType type = metadata.getLockModeType();
        return type == null ? query : query.setLockMode(type);
    }

    /**
     * Apply Specification to criteria.
     *
     * @param spec        Generic Specification
     * @param domainClass Entity class
     * @param query       Criteria Query
     * @param dtoType     Data Transfer Object type
     * @param filter      filter field
     * @param count       count query
     * @param <S>         Generic of Query Type
     * @param <U>         Generic of entity
     * @return Root instance
     */
    private <S, U extends T> Root<U> applySpecificationToCriteria(@Nullable GenericSpecification<U> spec, Class<U> domainClass,
                                                                  CriteriaQuery<S> query, Class<?> dtoType, @Nullable String[] filter,
                                                                  boolean count, boolean collection) {

        Assert.notNull(domainClass, "Domain class must not be null!");
        Assert.notNull(query, "CriteriaQuery must not be null!");

        Root<U> root = query.from(domainClass);

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        spec.buildCriteria(root, query, builder, dtoType, filter, count, collection);

        return root;
    }

    private static boolean isUnpaged(Pageable pageable) {
        return pageable.isUnpaged();
    }

    protected Page<Object> readPage(Class<?> dtoType, String[] filter, TypedQuery<Tuple> query, final Class<T> domainClass, Pageable pageable,
                                    @Nullable GenericSpecification<T> spec) {

        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        List<Tuple> tuples = query.getResultList();
        List<Object> contents;
        // Convert list tuple to map
        List<Map<String, Object>> records = MappingUtils.convertTupleToMapRecord(tuples, MappingUtils.getEntityMappingFieldPaths(dtoType, filter, false));
        if (records.size() > 0) {
            List<Tuple> collectionTuples = getDataTransferObjectCollectionQuery(dtoType, filter, spec, getDomainClass(), records).getResultList();
//            List<Map<String, Object>> collectionRecords = MappingUtils.convertTupleToMapRecord(collectionTuples, MappingUtils.getEntityMappingFieldPathsCollection(dtoType, filter));
            List<Map<String, Object>> collectionRecords = MappingUtils.convertTupleToMapRecord(collectionTuples, MappingUtils.getEntityMappingFieldPaths(dtoType, filter, true));

            List<Map<String, Object>> mergeRecords = MappingUtils.merge(records, collectionRecords, dtoType);

            contents = MappingUtils.convertToListDataTransferObject(mergeRecords, dtoType, filter);
        } else {
            contents = MappingUtils.convertToListDataTransferObject(records, dtoType, filter);
        }
        return getPage(dtoType, filter, contents, pageable,
                () -> executeCountQuery(getCountDataTransferObjectQuery(dtoType, spec, domainClass)));
    }

    public static Page<Object> getPage(Class<?> dtoType, String[] filter, List<Object> content, Pageable pageable, LongSupplier totalSupplier) {

        Assert.notNull(content, "Content must not be null!");
        Assert.notNull(pageable, "Pageable must not be null!");
        Assert.notNull(totalSupplier, "TotalSupplier must not be null!");

        if (pageable.isUnpaged() || pageable.getOffset() == 0) {

            if (pageable.isUnpaged() || pageable.getPageSize() > content.size()) {
                return new PageImpl<>(content, pageable, content.size());
            }

            return new PageImpl<>(content, pageable, totalSupplier.getAsLong());
        }

        if (content.size() != 0 && pageable.getPageSize() > content.size()) {
            return new PageImpl<>(content, pageable, pageable.getOffset() + content.size());
        }

        return new PageImpl<>(content, pageable, totalSupplier.getAsLong());
    }

    private static long executeCountQuery(TypedQuery<Long> query) {

        Assert.notNull(query, "TypedQuery must not be null!");

        List<Long> totals = query.getResultList();
        long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }


    @Override
    public Page<Object> findAll(Class<?> dtoType, String[] filter, GenericSpecification<T> spec, Pageable pageable) {
        Page<Object> page = null;
        if (isUnpaged(pageable)) {
            page = new PageImpl<>(findAll(dtoType, filter, spec));
        } else {
            TypedQuery<Tuple> query = getDataTransferObjectQuery(dtoType, filter, spec, pageable);
            page = readPage(dtoType, filter, query, getDomainClass(), pageable, spec);
        }
        return page;
    }

    protected TypedQuery<Tuple> getDataTransferObjectQuery(Class<?> dtoType, @Nullable String[] filter,
                                                           @Nullable GenericSpecification<T> spec, Pageable pageable) {
        Sort sort = pageable.isPaged() ? pageable.getSort() : Sort.unsorted();
        return getDataTransferObjectQuery(dtoType, filter, spec, getDomainClass(), sort);
    }
}
