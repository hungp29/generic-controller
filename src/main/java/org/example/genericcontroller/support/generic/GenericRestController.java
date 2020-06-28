package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Map;

/**
 * Default Rest Controller.
 *
 * @param <T> generic of Entity
 * @author hungp
 */
public class GenericRestController<T extends Audit> {

    private GenericService<T> genericService;

    /**
     * API create.
     *
     * @param createRequestDTO Create Request Data
     * @return Response data
     */
    @APICreate
    public ResponseEntity<Object> create(@RequestBody Object createRequestDTO) {
        return ResponseEntity.ok(genericService.createAndSave(createRequestDTO));
    }

    @GetMapping("/{id}")
    public <ID extends Serializable> ResponseEntity<Object> get(@PathVariable ID id, HttpServletRequest request) {
        return ResponseEntity.ok(genericService.get(id, request));
    }

    /**
     * Get all entity by condition and pagination.
     *
     * @param readDTOType     Read Data Transfer Object type
     * @param params          request params
     * @param pagination      pagination info
     * @param filter          filter fields
     * @param disabledCaching flag to detect disabled caching
     * @param request         {@link HttpServletRequest} instance (don't remove this)
     * @return page data
     */
    @APIReadAll
    public ResponseEntity<Page<Object>> getAll(Class<?> readDTOType, Map<String, String> params, Pagination pagination,
                                               String[] filter, boolean disabledCaching, HttpServletRequest request) {
        return ResponseEntity.ok(genericService.getAll(readDTOType, params, pagination, filter, disabledCaching));
    }

    @Autowired
    public void setGenericService(GenericService<T> genericService) {
        this.genericService = genericService;
    }
}
