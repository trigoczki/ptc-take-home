package me.trigoczki.contenttree.config;

import me.trigoczki.contenttree.domain.dto.FilteredTreeNodeResponse;
import me.trigoczki.contenttree.domain.dto.TreeNodeResponse;
import me.trigoczki.contenttree.domain.entity.TreeNode;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return createMappingConfig();
    }

    @NonNull
    private static ModelMapper createMappingConfig() {
        ModelMapper mapper = new ModelMapper();
        mapper.typeMap(TreeNode.class, TreeNodeResponse.class)
                .addMappings(m -> {
                    m.map(TreeNode::getId, TreeNodeResponse::setId);
                    m.map(TreeNode::getName, TreeNodeResponse::setName);
                    m.map(TreeNode::getContent, TreeNodeResponse::setContent);
                    m.map(TreeNode::isHasChildren, TreeNodeResponse::setHasChildren);
                });

        mapper.typeMap(TreeNode.class, FilteredTreeNodeResponse.class)
                .addMappings(m -> {
                    m.map(TreeNode::getId, FilteredTreeNodeResponse::setId);
                    m.map(TreeNode::getName, FilteredTreeNodeResponse::setName);
                    m.map(TreeNode::getContent, FilteredTreeNodeResponse::setContent);
                    m.map(TreeNode::isHasChildren, FilteredTreeNodeResponse::setHasChildren);
                    m.map(src -> src.getParent().getId(), FilteredTreeNodeResponse::setParentId);
                });

        return mapper;
    }
}
