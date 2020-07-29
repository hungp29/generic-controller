package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.support.generic.RootFilterData.FilterData;
import org.springframework.lang.Nullable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Generic Specification. It use to build criteria (select field, where condition, ...)
 *
 * @author hungp
 */
public interface GenericSpecification {

    /**
     * Build Criteria for query.
     *
     * @param root            Root Entity
     * @param query           Criteria query
     * @param criteriaBuilder Criteria builder
     * @param filterData      contain DTO Type, Filter field and params
     * @param collection      flat to detect build criteria for collection fields
     * @param <T>             generic of entity
     */
    @Nullable
    <T> Predicate buildCriteria(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder,
                                FilterData filterData, boolean collection);
}
