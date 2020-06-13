package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
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
import java.util.List;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

public class DefaultRepositoryImpl<T extends Audit> extends SimpleJpaRepository<T, Serializable> implements DefaultRepository<T> {

    private EntityManager em;

    public DefaultRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.em = em;
    }

    @Override
    public List<Tuple> findAll(Class<?> dtoType, @Nullable String[] filter, GenericSpecification<T> spec) {
        List<Tuple> tuples = getDTOQuery(dtoType, filter, spec, Sort.unsorted()).getResultList();
        return tuples;
    }

    protected TypedQuery<Tuple> getDTOQuery(Class<?> dtoType, @Nullable String[] filter,
                                            @Nullable GenericSpecification<T> spec, Sort sort) {
        return getDTOQuery(dtoType, filter, spec, getDomainClass(), sort);
    }

    protected <S extends T> TypedQuery<Tuple> getDTOQuery(Class<?> dtoType, @Nullable String[] filter,
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

    private <S> TypedQuery<S> applyRepositoryMethodMetadata(TypedQuery<S> query) {
        CrudMethodMetadata metadata = getRepositoryMethodMetadata();
        if (metadata == null) {
            return query;
        }

        LockModeType type = metadata.getLockModeType();
        return type == null ? query : query.setLockMode(type);
    }

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
