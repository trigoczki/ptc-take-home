package me.trigoczki.contenttree.controller;

import jakarta.validation.Valid;
import me.trigoczki.contenttree.domain.dto.CreateNodeRequest;
import me.trigoczki.contenttree.domain.dto.TreeNodeResponse;
import me.trigoczki.contenttree.domain.dto.UpdateNodeRequest;
import me.trigoczki.contenttree.service.TreeNodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tree/nodes")
public class TreeNodeController {

    private final TreeNodeService treeNodeService;

    public TreeNodeController(TreeNodeService treeNodeService) {
        this.treeNodeService = treeNodeService;
    }

    @PostMapping
    public ResponseEntity<TreeNodeResponse> create(@Valid @RequestBody CreateNodeRequest request) {
        return ResponseEntity.ok(treeNodeService.insertNode(request));
    }

    @GetMapping
    public List<TreeNodeResponse> listNodes() {
        return treeNodeService.listTree();
    }

    @PutMapping
    public ResponseEntity<TreeNodeResponse> update(@Valid @RequestBody UpdateNodeRequest request) {
        return ResponseEntity.ok(treeNodeService.updateNode(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNode(@PathVariable Long id) {
        treeNodeService.deleteNode(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public TreeNodeResponse getNode(@PathVariable Long id) {
        return treeNodeService.getNode(id);
    }
}