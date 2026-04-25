import {Component, inject, signal} from '@angular/core';
import {TreeNodeService} from './tree-node.service';
import {AddNode} from './add-node/add-node';
import {TreeDisplay} from './tree-display/tree-display';
import {EditNode} from './edit-node/edit-node';
import {SearchById} from './search-by-id/search-by-id';
import {SearchByContent} from './search-by-content/search-by-content';

@Component({
  selector: 'app-root',
  imports: [AddNode, EditNode, SearchById, SearchByContent, TreeDisplay],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private treeNodeService = inject(TreeNodeService);
  showAddNode = signal(false);
  showEditNode = signal(false);
  readonly selectedNodeId = this.treeNodeService.selectedNodeId;
  readonly selectedNode = this.treeNodeService.selectedNode;

  toggleAddNode() {
    this.showAddNode.update(value => !value);
  }
}
