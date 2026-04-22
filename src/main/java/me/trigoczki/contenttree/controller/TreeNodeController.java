package me.trigoczki.contenttree.controller;

import jakarta.validation.Valid;
import me.trigoczki.contenttree.domain.dto.CreateNodeRequest;
import me.trigoczki.contenttree.domain.dto.TreeNodeResponse;
import me.trigoczki.contenttree.service.TreeNodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}