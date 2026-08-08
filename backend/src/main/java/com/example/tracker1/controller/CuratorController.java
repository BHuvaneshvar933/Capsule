package com.example.tracker1.controller;

import com.example.tracker1.model.dto.CuratorItemRequest;
import com.example.tracker1.model.dto.CuratorItemResponse;
import com.example.tracker1.service.CuratorItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/curator")
@RequiredArgsConstructor
public class CuratorController {

    private final CuratorItemService curatorItemService;

    @GetMapping
    public ResponseEntity<List<CuratorItemResponse>> getAll() {
        return ResponseEntity.ok(curatorItemService.getAllForCurrentUser());
    }

    @PostMapping
    public ResponseEntity<CuratorItemResponse> create(@Valid @RequestBody CuratorItemRequest request) {
        return ResponseEntity.ok(curatorItemService.createItem(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuratorItemResponse> update(
            @PathVariable String id,
            @Valid @RequestBody CuratorItemRequest request) {
        return ResponseEntity.ok(curatorItemService.updateItem(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        curatorItemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/favorite")
    public ResponseEntity<CuratorItemResponse> toggleFavorite(@PathVariable String id) {
        return ResponseEntity.ok(curatorItemService.toggleFavorite(id));
    }
}
