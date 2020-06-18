package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;

/**
 * Default Repository.
 *
 * @param <T> generic of Entity
 * @author hungp
 */
@NoRepositoryBean
public interface DefaultRepository<T extends Audit> extends JpaRepository<T, Serializable>, JpaSpecificationExecutor<T> {

    /**
     * Find all entity and get data return as Data Transfer Object.
     *
     * @param dtoType Data Transfer Object type
     * @param filter  filter fields
     * @param spec    Generic Specification
     * @return list Data Transfer Object
     */
    List<Object> findAll(Class<?> dtoType, String[] filter, @Nullable GenericSpecification<T> spec);

    /**
     * Find one page of entity and get data return as Data Transfer Object.
     *
     * @param dtoType  Data Transfer Object type
     * @param filter   filter fields
     * @param spec     Generic Specification
     * @param pageable Paging info
     * @return page data
     */
    Page<Object> findAll(Class<?> dtoType, String[] filter, @Nullable GenericSpecification<T> spec, Pageable pageable);
}
