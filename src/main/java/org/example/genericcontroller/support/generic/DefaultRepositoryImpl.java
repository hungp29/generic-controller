package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.exception.generic.DataTransferObjectInvalidException;
import org.example.genericcontroller.support.generic.utils.MappingUtils;
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
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

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
    public List<Tuple> findAll(Class<?> dtoType, @Nullable String[] filter, GenericSpecification<T> spec) {
        List<Tuple> tuples = getDataTransferObjectQuery(dtoType, filter, spec, Sort.unsorted()).getResultList();

        List<Map<String, Object>> records = MappingUtils.convertTupleToMapRecord(tuples, MappingUtils.getEntityMappingFieldPaths(dtoType, filter));
        try {
            MappingUtils.convertToListDataTransferObject(records, dtoType);
            System.out.println("ASD");
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new DataTransferObjectInvalidException("Cannot found default constructor for " + dtoType.getSimpleName(), e);
        }

        return tuples;
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
                                                                         @Nullable GenericSpecification<S> spec, Class<S> domainClass, Sort sort) {
        Assert.notNull(dtoType, "DTO Type must not be null!");
        Validator.validateObjectConfiguration(dtoType, MappingClass.class);

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createQuery(Tuple.class);

        Root<S> root = applySpecificationToCriteria(spec, domainClass, query, dtoType, filter);

        if (sort.isSorted()) {
            query.orderBy(toOrders(sort, root, builder));
        }

        return applyRepositoryMethodMetadata(em.createQuery(query));
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
     * @param <S>         Generic of Query Type
     * @param <U>         Generic of entity
     * @return Root instance
     */
    private <S, U extends T> Root<U> applySpecificationToCriteria(@Nullable GenericSpecification<U> spec, Class<U> domainClass,
                                                                  CriteriaQuery<S> query, Class<?> dtoType, @Nullable String[] filter) {

        Assert.notNull(domainClass, "Domain class must not be null!");
        Assert.notNull(query, "CriteriaQuery must not be null!");

        Root<U> root = query.from(domainClass);

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        spec.buildCriteria(root, query, builder, dtoType, filter);

        return root;
    }
}
