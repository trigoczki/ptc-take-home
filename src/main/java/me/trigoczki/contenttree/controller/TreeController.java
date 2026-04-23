package me.trigoczki.contenttree.controller;

import me.trigoczki.contenttree.domain.dto.TreeSearchResponse;
import me.trigoczki.contenttree.service.TreeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tree")
public class TreeController {

    private final TreeService treeService;

    public TreeController(TreeService treeService) {
        this.treeService = treeService;
    }

    @GetMapping("/search")
    public TreeSearchResponse searchTree(@RequestParam("searchTerm") String query) {
        return treeService.searchTree(query);
    }
}
