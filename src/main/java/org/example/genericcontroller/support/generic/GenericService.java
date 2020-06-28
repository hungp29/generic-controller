package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Map;

/**
 * Default Service.
 *
 * @param <T> generic of Entity
 * @author hungp
 */
public class GenericService<T extends Audit> {

    public static final String PAGE = "page";
    public static final String LIMIT = "limit";
    public static final String FILTER = "filter";
    public static final String ORDER_BY = "orderBy";
    public static final String[] NOT_PARAM_FIELDS = {PAGE, LIMIT, ORDER_BY, FILTER};

    private GenericRepository<T> genericRepository;

    private int defaultPage = 1;
    private int defaultLimit = 10;

    /**
     * Create and save new entity.
     *
     * @param createRequestDTO data
     * @return response instance
     */
    @Transactional
    public Object createAndSave(Object createRequestDTO) {
        return genericRepository.saveDataTransferObject(createRequestDTO);
    }

    /**
     * Get one entity data.
     *
     * @param id      id of entity
     * @param request Http Servlet Request
     * @param <ID>    generic of Id
     * @return Data Transfer Object of Entity
     */
    public <ID extends Serializable> Object get(ID id, HttpServletRequest request) {
        return null;
    }

    /**
     * Get all entity by condition and pagination.
     *
     * @param readDTOType     Read Data Transfer Object type
     * @param params          request params
     * @param pagination      pagination info
     * @param disabledCaching flag to detect disabled caching
     * @param filter          filter fields
     * @return page data
     */
    @Cacheable(value = "findAll", key = "#params")
    public Page<Object> getAll(Class<?> readDTOType, Map<String, String> params, Pagination pagination, String[] filter, boolean disabledCaching) {
        if (!pagination.isUnPaged()) {
            return genericRepository.findAll(readDTOType, filter, params, pagination.getPageable());
        } else {
            return genericRepository.findAll(readDTOType, filter, params, pagination.getSort());
        }
    }

//    /**
//     * Get Entity class and validate entity has configuration DataTransferObjectMapping.
//     *
//     * @return entity type
//     */
//    private Class<T> getEntityConfigMapping() {
//        @SuppressWarnings("unchecked")
//        Class<T> entityClass = (Class<T>) ObjectUtils.getGenericClass(this.getClass());
//
//        // Validate configurations of entity
//        Validator.validateObjectConfiguration(entityClass, DataTransferObjectMapping.class);
//        return entityClass;
//    }

    @Autowired
    public void setGenericRepository(GenericRepository<T> genericRepository) {
        this.genericRepository = genericRepository;
    }
}
