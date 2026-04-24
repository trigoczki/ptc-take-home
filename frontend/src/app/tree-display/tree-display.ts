import {Component, inject} from '@angular/core';
import {NgTemplateOutlet} from '@angular/common';
import {TreeNodeService} from '../tree-node.service';

@Component({
  selector: 'app-tree-display',
  imports: [NgTemplateOutlet],
  templateUrl: './tree-display.html',
  styleUrl: './tree-display.css',
})
export class TreeDisplay {
  private treeNodeService = inject(TreeNodeService);

  readonly nodes = this.treeNodeService.nodes;

  toggleNode(id: number): void {
    this.treeNodeService.toggleNode(id);
  }

  selectNode(id: number): void {
    this.treeNodeService.selectNode(id);
  }
}
