package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Default Repository.
 *
 * @param <T> generic of Entity
 * @author hungp
 */
@NoRepositoryBean
public interface GenericRepository<T extends Audit> extends JpaRepository<T, Serializable>, JpaSpecificationExecutor<T> {

    /**
     * Find all entity and get data return as Data Transfer Object.
     *
     * @param dtoType Data Transfer Object type
     * @param filter  filter fields
     * @param params  request params
     * @return list Data Transfer Object
     */
    List<Object> findAll(Class<?> dtoType, String[] filter, Map<String, String> params, FilterData filterData);

    /**
     * Find one page of entity and get data return as Data Transfer Object.
     *
     * @param dtoType  Data Transfer Object type
     * @param filter   filter fields
     * @param params   request params
     * @param pageable Paging info
     * @return page data
     */
    Page<Object> findAll(Class<?> dtoType, String[] filter, Map<String, String> params, FilterData filterData, Pageable pageable);

    /**
     * Find all data of entity and return as Data Transfer Object.
     *
     * @param dtoType Data Transfer Object type
     * @param filter  filter fields
     * @param params  request params
     * @param sort    Sort instance
     * @return list data as page
     */
    Page<Object> findAll(Class<?> dtoType, String[] filter, Map<String, String> params, FilterData filterData, Sort sort);

    /**
     * Find one entity by id.
     *
     * @param id      the id of entity
     * @param dtoType Data Transfer Object type
     * @param filter  filter fields
     * @param <ID>    generic of id
     * @return Data Transfer Object of entity
     */
    <ID extends Serializable> Object findOneById(ID id, Class<?> dtoType, String[] filter, FilterData filterData);

    /**
     * Save Data Transfer Object.
     *
     * @param dto data
     * @return entity
     */
    T saveDataTransferObject(Object dto);
}
