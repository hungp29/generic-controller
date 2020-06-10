package org.example.genericcontroller.support.defaulthttp;

import org.example.genericcontroller.entity.Audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class DefaultRestController<T extends Audit> {

    private DefaultService<T> defaultService;

    @GetMapping("/{id}")
    public <ID extends Serializable> ResponseEntity<Object> get(@PathVariable ID id, HttpServletRequest request) {
        return ResponseEntity.ok(defaultService.getEntity(id, request));
    }

    @GetMapping
    public ResponseEntity<Page<Object>> getAll(HttpServletRequest request) {
        return ResponseEntity.ok(defaultService.getAllEntity(request));
    }

    @Autowired
    public void setDefaultService(DefaultService<T> defaultService) {
        this.defaultService = defaultService;
    }
}
