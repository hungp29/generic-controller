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

    @APIReadAll
    public ResponseEntity<Page<Object>> getAll(HttpServletRequest request) {
        return ResponseEntity.ok(genericService.getAll(request));
    }

    @Autowired
    public void setGenericService(GenericService<T> genericService) {
        this.genericService = genericService;
    }
}
