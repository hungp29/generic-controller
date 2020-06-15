package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

/**
 * Default Rest Controller.
 *
 * @param <T> generic of Entity
 * @author hungp
 */
public class DefaultRestController<T extends Audit> {

    private DefaultService<T> defaultService;

    @GetMapping("/{id}")
    public <ID extends Serializable> ResponseEntity<Object> get(@PathVariable ID id, HttpServletRequest request) {
        return ResponseEntity.ok(defaultService.getEntity(id, request));
    }

    @GetMapping
    public ResponseEntity<List<Object>> getAll(HttpServletRequest request) {
        return ResponseEntity.ok(defaultService.getAllEntity(request));
    }

    @Autowired
    public void setDefaultService(DefaultService<T> defaultService) {
        this.defaultService = defaultService;
    }
}
