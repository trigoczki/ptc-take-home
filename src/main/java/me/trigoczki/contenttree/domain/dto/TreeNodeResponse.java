package me.trigoczki.contenttree.domain.dto;

import java.util.List;

public record TreeNodeResponse(Long id, String name, String content, List<TreeNodeResponse> children) {
}