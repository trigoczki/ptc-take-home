import {Component, inject, input, OnInit, output} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {TreeNodeService} from '../tree-node.service';

@Component({
  selector: 'app-add-node',
  imports: [ReactiveFormsModule],
  templateUrl: './add-node.html',
  styleUrl: './add-node.css',
})
export class AddNode implements OnInit {
  private fb = inject(FormBuilder);
  private api = inject(TreeNodeService);

  onSaved = output<void>();
  parentId = input<number | null>(null);

  protected nodeForm = this.fb.nonNullable.group({
    name: ['', [Validators.required]],
    content: ['', [Validators.required]],
    parentId: [null as number | null]
  });

  ngOnInit(): void {
    this.nodeForm.patchValue({parentId: this.parentId()});
  }

  onAdd(): void {
    if (this.nodeForm.valid) {
      this.nodeForm.patchValue({parentId: this.parentId()});
      this.api.addNode(this.nodeForm.getRawValue());
      this.onSaved.emit();
    }
  }
}
