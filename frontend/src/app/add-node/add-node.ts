import {Component, inject, output} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {TreeNodeApiService} from '../tree-node-api.service';

@Component({
  selector: 'app-add-node',
  imports: [ReactiveFormsModule],
  templateUrl: './add-node.html',
  styleUrl: './add-node.css',
})
export class AddNode {
  private fb = inject(FormBuilder);
  private api = inject(TreeNodeApiService);

  onSaved = output<void>();

  protected nodeForm = this.fb.nonNullable.group({
    name: ['', [Validators.required]],
    content: ['', [Validators.required]],
    parentId: [null as number | null]
  });

  onAdd(): void {
    if (this.nodeForm.valid) {
      this.api.addNode(this.nodeForm.getRawValue()).subscribe({
        next: (response) => {
          console.log('Data saved successfully!', response);
          this.onSaved.emit();
        },
        error: (error) => console.error('There was an error!', error)
      })
    }
  }
}
