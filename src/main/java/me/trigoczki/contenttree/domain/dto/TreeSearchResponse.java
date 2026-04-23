package me.trigoczki.contenttree.domain.dto;

import java.util.List;

public record TreeSearchResponse(List<FilteredTreeNodeResponse> nodes) {
}
