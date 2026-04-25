package me.trigoczki.contenttree.domain.dto;

import java.util.List;
import java.util.Objects;

public class TreeNodeResponse {
    private Long id;
    private String name;
    private String content;
    private boolean hasChildren;
    private Long parentId;
    private boolean match;
    private List<TreeNodeResponse> children;

    public TreeNodeResponse() {
    }

    public TreeNodeResponse(Long id, String name, String content, boolean hasChildren, List<TreeNodeResponse> children) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.hasChildren = hasChildren;
        this.children = children;
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

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }

    public List<TreeNodeResponse> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNodeResponse> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TreeNodeResponse that = (TreeNodeResponse) o;
        return hasChildren == that.hasChildren && match == that.match && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(content, that.content) && Objects.equals(parentId, that.parentId) && Objects.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, content, hasChildren, parentId, match, children);
    }

    @Override
    public String toString() {
        return "TreeNodeResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", hasChildren=" + hasChildren +
                ", parentId=" + parentId +
                ", match=" + match +
                ", children=" + children +
                '}';
    }
}