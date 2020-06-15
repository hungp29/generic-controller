package org.example.genericcontroller.support.generic;

import org.springframework.lang.Nullable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Generic Specification. It use to build criteria (select field, where condition, ...)
 *
 * @param <T> generic of Entity
 * @author hungp
 */
public interface GenericSpecification<T> {

    /**
     * Build Criteria for query.
     *
     * @param root            Root Entity
     * @param query           Criteria query
     * @param criteriaBuilder Criteria builder
     * @param dtoType         Data Transfer Object type
     * @param filter          list field accepted to get from database
     * @param count           count query
     */
    @Nullable
    void buildCriteria(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder,
                       Class<?> dtoType, @Nullable String[] filter, boolean count, boolean collection);
}
