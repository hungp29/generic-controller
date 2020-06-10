package org.example.genericcontroller.app.book.old;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
public class OldBookController {

    private final OldBookService oldBookService;

    @GetMapping("/{id}")
    public ResponseEntity<String> get(@PathVariable int id) {
        oldBookService.get(id);
        return ResponseEntity.ok("ASD");
    }

    @PostMapping
    public ResponseEntity<Boolean> create() {
        return ResponseEntity.ok(oldBookService.create());
    }
}
