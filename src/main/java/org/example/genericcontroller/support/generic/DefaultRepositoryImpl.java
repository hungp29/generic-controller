package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;

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
        return MappingUtils.convertToListDataTransferObject(records, dtoType, filter);
    }

//    @Override
//    public Page<Object> findAll(Class<?> dtoType, String[] filter, GenericSpecification<T> spec, Pageable pageable) {
//        Page<Object> page = null;
//        if (isUnpaged(pageable)) {
//            List<Tuple> tuples = getDataTransferObjectQuery(dtoType, filter, spec, Sort.unsorted()).getResultList();
//            // Convert list tuple to map
//            List<Map<String, Object>> records = MappingUtils.convertTupleToMapRecord(tuples, MappingUtils.getEntityMappingFieldPaths(dtoType, filter, false));
//            page = new PageImpl<>(MappingUtils.convertToListDataTransferObject(records, dtoType, filter));
//        } else {
//            TypedQuery<Tuple> query = getDataTransferObjectQuery(dtoType, filter, spec, pageable);
//            page = readPage(dtoType, filter, query, getDomainClass(), pageable, spec);
//        }
//        return page;
//    }

//    protected TypedQuery<Tuple> getDataTransferObjectQuery(Class<?> dtoType, @Nullable String[] filter,
//                                                           @Nullable GenericSpecification<T> spec, Pageable pageable) {
//        Sort sort = pageable.isPaged() ? pageable.getSort() : Sort.unsorted();
//        return getDataTransferObjectQuery(dtoType, filter, spec, getDomainClass(), sort);
//    }

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
        Validator.validateObjectConfiguration(dtoType, MappingClass.class);

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createQuery(Tuple.class);

        Root<S> root = applySpecificationToCriteria(spec, domainClass, query, dtoType, filter, false);

        if (sort.isSorted()) {
            query.orderBy(toOrders(sort, root, builder));
        }

        return applyRepositoryMethodMetadata(em.createQuery(query));
    }

//    protected <S extends T> TypedQuery<Long> getCountDataTransferObjectQuery(Class<?> dtoType,
//                                                                             @Nullable GenericSpecification<S> spec,
//                                                                             Class<S> domainClass) {
//
//        CriteriaBuilder builder = em.getCriteriaBuilder();
//        CriteriaQuery<Long> query = builder.createQuery(Long.class);
//
//        Root<S> root = applySpecificationToCriteria(spec, domainClass, query, dtoType, null, true);
//
//        if (query.isDistinct()) {
//            query.select(builder.countDistinct(root));
//        } else {
//            query.select(builder.count(root));
//        }
//
//        // Remove all Orders the Specifications might have applied
//        query.orderBy(Collections.<Order>emptyList());
//
//        return em.createQuery(query);
//    }

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
                                                                  boolean count) {

        Assert.notNull(domainClass, "Domain class must not be null!");
        Assert.notNull(query, "CriteriaQuery must not be null!");

        Root<U> root = query.from(domainClass);

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        spec.buildCriteria(root, query, builder, dtoType, filter, count);

        return root;
    }

    private static boolean isUnpaged(Pageable pageable) {
        return pageable.isUnpaged();
    }

//    protected <S extends T> Page<Object> readPage(Class<?> dtoType, String[] filter, TypedQuery<Tuple> query, final Class<S> domainClass, Pageable pageable,
//                                                  @Nullable GenericSpecification<S> spec) {
//
//        if (pageable.isPaged()) {
//            query.setFirstResult((int) pageable.getOffset());
//            query.setMaxResults(pageable.getPageSize());
//        }
//
//        return getPage(dtoType, filter, query.getResultList(), pageable,
//                () -> executeCountQuery(getCountDataTransferObjectQuery(dtoType, spec, domainClass)));
//    }

    public static Page<Object> getPage(Class<?> dtoType, String[] filter, List<Tuple> tuples, Pageable pageable, LongSupplier totalSupplier) {

        Assert.notNull(tuples, "Content must not be null!");
        Assert.notNull(pageable, "Pageable must not be null!");
        Assert.notNull(totalSupplier, "TotalSupplier must not be null!");

        List<Map<String, Object>> records = MappingUtils.convertTupleToMapRecord(tuples, MappingUtils.getEntityMappingFieldPaths(dtoType, filter, false));
        List<Object> content = MappingUtils.convertToListDataTransferObject(records, dtoType, filter);

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
}
