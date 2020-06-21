package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/{id}")
    public <ID extends Serializable> ResponseEntity<Object> get(@PathVariable ID id, HttpServletRequest request) {
        return ResponseEntity.ok(genericService.getEntity(id, request));
    }

    @GetMapping
    public ResponseEntity<Page<Object>> getAll(HttpServletRequest request) {
        return ResponseEntity.ok(genericService.getAllEntity(request));
    }

    @Autowired
    public void setGenericService(GenericService<T> genericService) {
        this.genericService = genericService;
    }
}
