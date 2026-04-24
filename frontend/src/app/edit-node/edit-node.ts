import {Component, effect, inject, output} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {TreeNodeService} from '../tree-node.service';
import {DeleteNode} from '../delete-node/delete-node';

@Component({
  selector: 'edit-node',
  imports: [
    ReactiveFormsModule,
    DeleteNode
  ],
  templateUrl: './edit-node.html',
  styleUrl: './edit-node.css',
})
export class EditNode {
  private fb = inject(FormBuilder);
  private api = inject(TreeNodeService);

  onSaved = output<void>();
  selectedNode = this.api.selectedNode;
  showDeleteDialog = this.api.showDeleteDialog;
  protected isEditMode: boolean = false;

  protected nodeForm = this.fb.nonNullable.group({
    id: [0],
    name: ['', [Validators.required]],
    content: ['', [Validators.required]],
    parentId: [null as number | null]
  });

  constructor() {
    effect(() => {
      const node = this.selectedNode();
      this.nodeForm.patchValue({
        parentId: node?.parentId,
        id: node?.id,
        name: node?.name,
        content: node?.content
      });
    });
  }

  toggleEditMode(): void {
    this.isEditMode = !this.isEditMode;
  }

  onEdit(): void {
    if (this.nodeForm.valid) {
      this.api.updateNode(this.nodeForm.getRawValue());
      this.isEditMode = false;
      this.onSaved.emit();
    }
  }

  protected toggleDeleteMode() {
    this.showDeleteDialog.set(true);
  }
}
