package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.exception.generic.GenericSelectionEmptyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
import java.util.List;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

public class DefaultRepositoryImpl<T extends Audit> extends SimpleJpaRepository<T, Serializable> implements DefaultRepository<T> {

    private EntityManager em;

    public DefaultRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.em = em;
    }

    @Override
    public List<Tuple> findAll(Class<?> dtoType, @Nullable String[] filter, Specification<T> spec) {
        List<Tuple> tuples = getDTOQuery(dtoType, filter, spec, Sort.unsorted()).getResultList();
        return tuples;
    }

    protected TypedQuery<Tuple> getDTOQuery(Class<?> dtoType, @Nullable String[] filter,
                                            @Nullable Specification<T> spec, Sort sort) {
        return getDTOQuery(dtoType, filter, spec, getDomainClass(), sort);
    }

    protected <S extends T> TypedQuery<Tuple> getDTOQuery(Class<?> dtoType, @Nullable String[] filter,
                                                          @Nullable Specification<S> spec, Class<S> domainClass, Sort sort) {
        Assert.notNull(dtoType, "DTO Type must not be null!");
        Validator.validateObjectConfiguration(dtoType, MapClass.class);

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createQuery(Tuple.class);

        Root<S> root = applySpecificationToCriteria(spec, domainClass, query);

        if (sort.isSorted()) {
            query.orderBy(toOrders(sort, root, builder));
        }

        return applyRepositoryMethodMetadata(em.createQuery(query));
    }

    private <S, U extends T> CriteriaQuery<S> applySelectionToCriteria(Root<U> root, Class<?> dtoType, @Nullable String[] filter,
                                                                       CriteriaQuery<S> query, SelectionCriteria<U> selection) {
        Assert.notNull(query, "CriteriaQuery must not be null!");
        Assert.notNull(selection, "SelectionCriteria must not be null!");

        Selection<?>[] selections = selection.buildMultiSelect(root, dtoType, filter);
        if (selections.length == 0) {
            throw new GenericSelectionEmptyException("Selection must not be empty");
        }

        return query.multiselect(selections);
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
