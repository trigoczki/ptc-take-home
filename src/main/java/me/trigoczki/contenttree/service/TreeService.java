package me.trigoczki.contenttree.service;

import me.trigoczki.contenttree.domain.dto.FilteredTreeNodeResponse;
import me.trigoczki.contenttree.domain.dto.TreeSearchResponse;
import me.trigoczki.contenttree.domain.entity.TreeNode;
import me.trigoczki.contenttree.repository.TreeNodeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TreeService {

    private final ModelMapper modelMapper;
    private final TreeNodeRepository treeNodeRepository;

    public TreeService(ModelMapper modelMapper, TreeNodeRepository treeNodeRepository) {
        this.modelMapper = modelMapper;
        this.treeNodeRepository = treeNodeRepository;
    }

    @Transactional(readOnly = true)
    public TreeSearchResponse searchTree(String query) {
        List<TreeNode> searchResults = treeNodeRepository.findAllByNameContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByIdDesc(query, query);

        List<FilteredTreeNodeResponse> rootNodes = new ArrayList<>();
        Set<Long> matchedNodeIds = new HashSet<>(searchResults.stream().map(TreeNode::getId).toList());
        Map<Long, FilteredTreeNodeResponse> responseNodesById = new HashMap<>();

        searchResults.forEach(matchedNode -> {
            if (!responseNodesById.containsKey(matchedNode.getId())) {
                FilteredTreeNodeResponse nodeResponse = modelMapper.map(matchedNode, FilteredTreeNodeResponse.class);
                nodeResponse.setMatch(true);
                responseNodesById.put(matchedNode.getId(), nodeResponse);

                TreeNode parentNode = matchedNode.getParent();

                while (parentNode != null) {
                    FilteredTreeNodeResponse parentNodeResponse = modelMapper.map(parentNode, FilteredTreeNodeResponse.class);
                    parentNodeResponse.getChildren().add(nodeResponse);
                    if (matchedNodeIds.contains(parentNode.getId())) {
                        parentNodeResponse.setMatch(true);
                    }
                    responseNodesById.put(parentNode.getId(), parentNodeResponse);
                    nodeResponse = parentNodeResponse;
                    parentNode = parentNode.getParent();
                }

                rootNodes.add(nodeResponse);
            }
        });

        return new TreeSearchResponse(rootNodes);
    }


}
