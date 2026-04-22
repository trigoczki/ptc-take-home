package me.trigoczki.contenttree.repository;

import me.trigoczki.contenttree.domain.entity.TreeNode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreeNodeRepository extends JpaRepository<TreeNode, Long> {
}