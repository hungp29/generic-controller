package org.example.genericcontroller.app.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/publisher")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping
    public ResponseEntity<String> get() {
        return ResponseEntity.ok("ASDASD");
    }

    @PostMapping
    public ResponseEntity<Boolean> create() {
        return ResponseEntity.ok(publisherService.create());
    }
}
