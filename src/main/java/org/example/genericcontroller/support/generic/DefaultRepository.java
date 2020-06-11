package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;

import javax.persistence.Tuple;
import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface DefaultRepository<T extends Audit> extends JpaRepository<T, Serializable>, JpaSpecificationExecutor<T> {

    List<Tuple> findAll(Class<?> dtoType, String[] filter, @Nullable Specification<T> spec, SelectionCriteria<T> selection);
}
