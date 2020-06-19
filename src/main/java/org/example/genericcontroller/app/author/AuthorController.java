package org.example.genericcontroller.app.author;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/author")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    public ResponseEntity<Boolean> create() {
        return ResponseEntity.ok(authorService.create());
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> get(@PathVariable int id) {
        return ResponseEntity.ok(authorService.getName(id));
    }
}
