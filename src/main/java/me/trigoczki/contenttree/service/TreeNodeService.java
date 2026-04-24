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

import java.util.List;

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
                .map(child -> modelMapper.map(child, TreeNodeResponse.class))
                .toList();
        TreeNodeResponse response = modelMapper.map(node, TreeNodeResponse.class);
        response.setChildren(childNodes);

        return response;
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
                .map(node -> modelMapper.map(node, TreeNodeResponse.class))
                .toList();
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void deleteNode(Long id) {
        TreeNode existing = treeNodeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Node not found: " + id));

        deleteRecursively(existing.getId());

        TreeNode parent = existing.getParent();
        if (!treeNodeRepository.existsByParentId(parent.getId())) {
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
}