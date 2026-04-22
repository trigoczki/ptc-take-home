package me.trigoczki.contenttree.service;

import me.trigoczki.contenttree.domain.dto.CreateNodeRequest;
import me.trigoczki.contenttree.domain.dto.TreeNodeResponse;
import me.trigoczki.contenttree.domain.dto.UpdateNodeRequest;
import me.trigoczki.contenttree.domain.entity.TreeNode;
import me.trigoczki.contenttree.exception.BadRequestException;
import me.trigoczki.contenttree.exception.NotFoundException;
import me.trigoczki.contenttree.repository.TreeNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TreeNodeService {

    private final TreeNodeRepository treeNodeRepository;

    public TreeNodeService(TreeNodeRepository treeNodeRepository) {
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
        return new TreeNodeResponse(saved.getId(), saved.getName(), saved.getContent(), false, List.of());
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public TreeNodeResponse updateNode(UpdateNodeRequest request) {
        TreeNode node = treeNodeRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Parent node not found: " + request.getId()));

        node.setName(request.getName());
        node.setContent(request.getContent());

        TreeNode originalParent = node.getParent();
        if (!originalParent.getId().equals(request.getParentId())) {
            TreeNode newParent = treeNodeRepository.findById(request.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent node not found: " + request.getParentId()));
            newParent.setHasChildren(true);
            node.setParent(newParent);
            if (!treeNodeRepository.existsByParentId(originalParent.getId())) {
                originalParent.setHasChildren(false);
            }
        }

        List<TreeNodeResponse> childNodes = treeNodeRepository.findAllByParentId(node.getId()).stream()
                .map(child -> new TreeNodeResponse(child.getId(), child.getName(), child.getContent(), child.isHasChildren(), List.of()))
                .toList();

        return new TreeNodeResponse(node.getId(), node.getName(), node.getContent(), node.isHasChildren(), childNodes);
    }


    @Transactional(readOnly = true)
    public List<TreeNodeResponse> listNodes(Long parentId) {
        List<TreeNode> nodes;
        if (parentId == null) {
            nodes = treeNodeRepository.findAllByParentId(null);
        } else {
            if (parentId < 0L) {
                throw new BadRequestException("Parent ID must be a positive number");
            }
            if (!treeNodeRepository.existsById(parentId)) {
                throw new NotFoundException("Parent node not found: " + parentId);
            }

            nodes = treeNodeRepository.findAllByParentId(parentId);
        }

        return nodes.stream()
                .map(node -> new TreeNodeResponse(node.getId(), node.getName(), node.getContent(), node.isHasChildren(), List.of())).toList();
    }
}