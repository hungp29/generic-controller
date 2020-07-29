package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.support.generic.annotation.APICreate;
import org.example.genericcontroller.support.generic.annotation.APIReadAll;
import org.example.genericcontroller.support.generic.annotation.APIReadOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

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


    /**
     * Get one entity by id.
     *
     * @param id          the id of entity
     * @param readDTOType Read Data Transfer Object type
     * @param filter      filter fields
     * @param <ID>        generic of id
     * @return Data Transfer Object of entity
     */
    @APIReadOne("/{id}")
    public <ID extends Serializable> ResponseEntity<Object> get(@PathVariable ID id, Class<?> readDTOType, String[] filter) {
        return ResponseEntity.ok(genericService.get(id, readDTOType, filter, null));
    }

    /**
     * Get all entity by condition and pagination.
     *
     * @param readDTOType Read Data Transfer Object type
     * @param params      request params
     * @param pagination  pagination info
     * @param filter      filter fields
     * @return page data
     */
    @APIReadAll
    public ResponseEntity<Page<Object>> getAll(Class<?> readDTOType, Map<String, String> params, Pagination pagination,
                                               String[] filter, RootFilterData rootFilterData) {
        return ResponseEntity.ok(genericService.getAll(readDTOType, params, pagination, filter, rootFilterData));
    }

    @Autowired
    public void setGenericService(GenericService<T> genericService) {
        this.genericService = genericService;
    }
}
