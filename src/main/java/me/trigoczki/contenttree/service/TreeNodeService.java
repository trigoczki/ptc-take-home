package me.trigoczki.contenttree.service;

import me.trigoczki.contenttree.domain.dto.CreateNodeRequest;
import me.trigoczki.contenttree.domain.dto.TreeNodeResponse;
import me.trigoczki.contenttree.domain.dto.UpdateNodeRequest;
import me.trigoczki.contenttree.domain.entity.TreeNode;
import me.trigoczki.contenttree.exception.BadRequestException;
import me.trigoczki.contenttree.exception.NotFoundException;
import me.trigoczki.contenttree.repository.TreeNodeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TreeNodeService {

    private final ModelMapper modelMapper;
    private final TreeNodeRepository treeNodeRepository;

    public TreeNodeService(ModelMapper modelMapper, TreeNodeRepository treeNodeRepository) {
        this.modelMapper = modelMapper;
        this.treeNodeRepository = treeNodeRepository;
    }

    @Transactional
    public TreeNodeResponse insertNode(CreateNodeRequest request) {
        TreeNode node = new TreeNode();

        node.setName(request.getName());
        node.setContent(request.getContent());

        if (request.getParentId() != null) {
            TreeNode parent = treeNodeRepository.findById(request.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent node not found: " + request.getParentId()));
            parent.setHasChildren(true);

            node.setParent(parent);
        } else {
            node.setParent(null);
        }

        TreeNode saved = treeNodeRepository.save(node);
        TreeNodeResponse response = modelMapper.map(saved, TreeNodeResponse.class);
        response.setParentId(request.getParentId());

        return response;
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public TreeNodeResponse updateNode(UpdateNodeRequest request) {
        TreeNode node = treeNodeRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Parent node not found: " + request.getId()));

        node.setName(request.getName());
        node.setContent(request.getContent());
        if (request.getParentId() == null) {
            node.setParent(null);
        }

        TreeNode originalParent = node.getParent();
        if ((originalParent != null && !originalParent.getId().equals(request.getParentId())) ||
                (originalParent == null && request.getParentId() != null)) {
            TreeNode newParent = treeNodeRepository.findById(request.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent node not found: " + request.getParentId()));
            newParent.setHasChildren(true);
            node.setParent(newParent);
            if (originalParent != null && !treeNodeRepository.existsByParentId(originalParent.getId())) {
                originalParent.setHasChildren(false);
            }
        }

        List<TreeNodeResponse> childNodes = treeNodeRepository.findAllByParentId(node.getId()).stream()
                .map(child -> modelMapper.map(child, TreeNodeResponse.class))
                .toList();
        TreeNodeResponse response = modelMapper.map(node, TreeNodeResponse.class);
        response.setChildren(childNodes);

        return response;
    }


    @Transactional(readOnly = true)
    public List<TreeNodeResponse> listTree() {
        List<TreeNode> allNodes = treeNodeRepository.findAll();
        Map<Long, List<TreeNode>> childrenByParent = allNodes.stream()
                .filter(node -> node.getParent() != null)
                .collect(Collectors.groupingBy(node -> node.getParent().getId()));

        return allNodes.stream()
                .filter(node -> node.getParent() == null)
                .sorted(Comparator.comparing(TreeNode::getId))
                .map(node -> buildTree(node, childrenByParent))
                .toList();
    }

    private TreeNodeResponse buildTree(TreeNode node, Map<Long, List<TreeNode>> childrenByParent) {
        List<TreeNodeResponse> children = childrenByParent.getOrDefault(node.getId(), List.of())
                .stream()
                .sorted(Comparator.comparing(TreeNode::getId))
                .map(child -> buildTree(child, childrenByParent))
                .toList();

        TreeNodeResponse treeNodeResponse = new TreeNodeResponse(node.getId(), node.getName(), node.getContent(), !CollectionUtils.isEmpty(children), children);
        TreeNode parent = node.getParent();
        if (parent != null) {
            treeNodeResponse.setParentId(parent.getId());
        }

        return treeNodeResponse;
    }


    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void deleteNode(Long id) {
        TreeNode existing = treeNodeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Node not found: " + id));

        deleteRecursively(existing.getId());

        TreeNode parent = existing.getParent();
        if (parent != null && !treeNodeRepository.existsByParentId(parent.getId())) {
            parent.setHasChildren(false);
        }
    }

    @Transactional(readOnly = true)
    public TreeNodeResponse getNode(Long id) {
        if (id < 1L) {
            throw new BadRequestException("Parent ID must be a positive number");
        }
        TreeNode node = treeNodeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Parent node not found: " + id));

        return modelMapper.map(node, TreeNodeResponse.class);
    }

    private void deleteRecursively(Long id) {
        List<TreeNode> children = treeNodeRepository.findAllByParentId(id);
        for (TreeNode child : children) {
            deleteRecursively(child.getId());
        }
        treeNodeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TreeNodeResponse> searchTree(String query) {
        if (query == null || query.isBlank()) {
            return listTree();
        }

        String normalized = query.toLowerCase(Locale.ROOT);
        List<TreeNode> allNodes = treeNodeRepository.findAll();
        Map<Long, List<TreeNode>> childrenByParent = allNodes.stream()
                .filter(node -> node.getParent() != null)
                .collect(Collectors.groupingBy(node -> node.getParent().getId()));

        return allNodes.stream()
                .filter(node -> node.getParent() == null)
                .sorted(Comparator.comparing(TreeNode::getId))
                .map(root -> buildTreeWithMatch(root, childrenByParent, normalized))
                .toList();
    }

    private TreeNodeResponse buildTreeWithMatch(TreeNode node,
                                                Map<Long, List<TreeNode>> childrenByParent,
                                                String normalizedQuery) {
        List<TreeNodeResponse> children = childrenByParent.getOrDefault(node.getId(), List.of())
                .stream()
                .sorted(Comparator.comparing(TreeNode::getId))
                .map(child -> buildTreeWithMatch(child, childrenByParent, normalizedQuery))
                .toList();

        TreeNodeResponse response = new TreeNodeResponse(node.getId(), node.getName(), node.getContent(), node.isHasChildren(), children);
        TreeNode parent = node.getParent();
        if (parent != null) {
            response.setParentId(parent.getId());
        }
        response.setMatch(matches(node, normalizedQuery));
        return response;
    }

    private boolean matches(TreeNode node, String normalizedQuery) {
        return node.getName().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                || node.getContent().toLowerCase(Locale.ROOT).contains(normalizedQuery);
    }
}