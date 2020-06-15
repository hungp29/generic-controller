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

    List<Object> findAll(Class<?> dtoType, String[] filter, @Nullable GenericSpecification<T> spec);

    Page<Object> findAll(Class<?> dtoType, String[] filter, @Nullable GenericSpecification<T> spec, Pageable pageable);
}
