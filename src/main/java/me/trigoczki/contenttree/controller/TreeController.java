package me.trigoczki.contenttree.controller;

import me.trigoczki.contenttree.domain.dto.TreeNodeResponse;
import me.trigoczki.contenttree.service.TreeNodeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/tree")
public class TreeController {

    private final TreeNodeService treeService;

    public TreeController(TreeNodeService treeService) {
        this.treeService = treeService;
    }


    @GetMapping("/search")
    public List<TreeNodeResponse> searchTree(@RequestParam("searchTerm") String query) {
        return treeService.searchTree(query);
    }
}
