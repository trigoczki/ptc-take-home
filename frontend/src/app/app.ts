import {Component, inject, signal} from '@angular/core';
import {TreeNodeService} from './tree-node.service';
import {AddNode} from './add-node/add-node';
import {TreeDisplay} from './tree-display/tree-display';

@Component({
  selector: 'app-root',
  imports: [AddNode, TreeDisplay],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private treeNodeService = inject(TreeNodeService);
  protected readonly title = signal('frontend');
  showAddNode = signal(false);
  readonly selectedNodeId = this.treeNodeService.selectedNodeId;

  toggleAddNode() {
    this.showAddNode.update(value => !value);
  }
}
