import {Component, inject, output} from '@angular/core';
import {TreeNodeService} from '../tree-node.service';

@Component({
  selector: 'delete-node',
  imports: [],
  templateUrl: './delete-node.html',
  styleUrl: './delete-node.css',
})
export class DeleteNode {
  private api = inject(TreeNodeService);
  onSaved = output<void>();
  onCancel = output<void>();
  selectedNode = this.api.selectedNode;

  onDelete(): void {
    const node = this.selectedNode();
    if (node) {
      this.api.deleteNode(node.id);
      this.onSaved.emit();
    }
  }

  cancel(): void {
    this.onCancel.emit();
  }
}
