package me.trigoczki.contenttree.domain.dto;

import java.util.List;

public record TreeNodeResponse(Long id, String name, String content, boolean hasChildren,
                               List<TreeNodeResponse> children) {
}