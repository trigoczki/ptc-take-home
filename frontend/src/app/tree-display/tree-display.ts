import {Component, inject, OnInit} from '@angular/core';
import {NgTemplateOutlet} from '@angular/common';
import {TreeNodeService} from '../tree-node.service';
import {TreeNode} from '../tree-node.model';

@Component({
  selector: 'app-tree-display',
  imports: [NgTemplateOutlet],
  templateUrl: './tree-display.html',
  styleUrl: './tree-display.css',
})
export class TreeDisplay implements OnInit {
  private treeNodeService = inject(TreeNodeService);

  readonly nodes = this.treeNodeService.nodes;

  ngOnInit(): void {
    this.treeNodeService.loadNodes();
  }

  toggleNode(id: number): void {
    this.treeNodeService.toggleNode(id);
  }

  selectNode(id: number): void {
    this.treeNodeService.selectNode(id);
  }

  trackById(_index: number, node: TreeNode): number {
    return node.id;
  }
}
