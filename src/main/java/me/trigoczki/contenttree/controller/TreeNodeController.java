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
@RequestMapping("/api/tree")
public class TreeNodeController {

    private final TreeNodeService treeNodeService;

    public TreeNodeController(TreeNodeService treeNodeService) {
        this.treeNodeService = treeNodeService;
    }

    @PostMapping("/nodes")
    public ResponseEntity<TreeNodeResponse> create(@Valid @RequestBody CreateNodeRequest request) {
        return ResponseEntity.ok(treeNodeService.insertNode(request));
    }

    @GetMapping("/nodes")
    public List<TreeNodeResponse> listNodes(@RequestParam(name = "parentId", required = false) Long parentId) {
        return treeNodeService.listNodes(parentId);
    }

    @PutMapping("/nodes")
    public ResponseEntity<TreeNodeResponse> update(@Valid @RequestBody UpdateNodeRequest request) {
        return ResponseEntity.ok(treeNodeService.updateNode(request));
    }

    @DeleteMapping("/nodes/{id}")
    public ResponseEntity<Void> deleteNode(@PathVariable Long id) {
        treeNodeService.deleteNode(id);
        return ResponseEntity.noContent().build();
    }
}