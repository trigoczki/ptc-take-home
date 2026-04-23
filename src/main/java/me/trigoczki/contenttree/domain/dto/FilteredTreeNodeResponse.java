package me.trigoczki.contenttree.domain.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FilteredTreeNodeResponse {
    private Long id;
    private String name;
    private String content;
    private Long parentId;
    private boolean hasChildren;
    private boolean match;
    private List<FilteredTreeNodeResponse> children = new ArrayList<>();

    public FilteredTreeNodeResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public List<FilteredTreeNodeResponse> getChildren() {
        return children;
    }

    public void setChildren(List<FilteredTreeNodeResponse> children) {
        this.children = children;
    }

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FilteredTreeNodeResponse that = (FilteredTreeNodeResponse) o;
        return hasChildren == that.hasChildren && match == that.match && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(content, that.content) && Objects.equals(parentId, that.parentId) && Objects.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, content, parentId, hasChildren, children, match);
    }

    @Override
    public String toString() {
        return "FilteredTreeNodeResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", parentId=" + parentId +
                ", hasChildren=" + hasChildren +
                ", children=" + children +
                ", match=" + match +
                '}';
    }
}
