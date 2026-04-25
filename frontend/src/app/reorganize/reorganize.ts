import {Component, inject, signal} from '@angular/core';
import {TreeNodeService} from '../tree-node.service';
import {TreeNode} from '../tree-node.model';
import {NgTemplateOutlet} from '@angular/common';

@Component({
  selector: 'reorganize',
  imports: [NgTemplateOutlet],
  templateUrl: './reorganize.html',
  styleUrl: './reorganize.css',
})
export class Reorganize {
  private treeNodeService = inject(TreeNodeService);

  readonly nodes = this.treeNodeService.nodes;
  draggedNode = signal<TreeNode | null>(null);
  dropTargetId = signal<number | null>(null);
  rootDropActive = signal(false);

  onDragStart(node: TreeNode): void {
    this.draggedNode.set(node);
  }

  onDragOver(event: DragEvent, targetId: number): void {
    const dragged = this.draggedNode();
    if (dragged && dragged.id !== targetId && !this.isDescendant(dragged, targetId)) {
      event.preventDefault();
      this.dropTargetId.set(targetId);
    }
  }

  onDragLeave(targetId: number): void {
    if (this.dropTargetId() === targetId) {
      this.dropTargetId.set(null);
    }
  }

  onDrop(event: DragEvent, targetId: number): void {
    event.preventDefault();
    const dragged = this.draggedNode();
    if (!dragged || dragged.id === targetId || this.isDescendant(dragged, targetId)) {
      this.resetDrag();
      return;
    }
    this.treeNodeService.updateNode({
      id: dragged.id,
      name: dragged.name,
      content: dragged.content,
      parentId: targetId
    });
    this.treeNodeService.loadNodes();
    this.resetDrag();
  }

  onRootDragOver(event: DragEvent): void {
    const dragged = this.draggedNode();
    if (dragged) {
      event.preventDefault();
      this.rootDropActive.set(true);
    }
  }

  onRootDragLeave(): void {
    this.rootDropActive.set(false);
  }

  onRootDrop(event: DragEvent): void {
    event.preventDefault();
    const dragged = this.draggedNode();
    if (!dragged) {
      this.resetDrag();
      return;
    }
    this.treeNodeService.updateNode({
      id: dragged.id,
      name: dragged.name,
      content: dragged.content,
      parentId: null
    });
    this.treeNodeService.loadNodes();
    this.resetDrag();
  }

  private resetDrag(): void {
    this.draggedNode.set(null);
    this.dropTargetId.set(null);
    this.rootDropActive.set(false);
  }

  private isDescendant(node: TreeNode, targetId: number): boolean {
    for (const child of node.children ?? []) {
      if (child.id === targetId) return true;
      if (this.isDescendant(child, targetId)) return true;
    }
    return false;
  }
}
