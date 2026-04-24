import {Component, inject, OnDestroy} from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {debounceTime, distinctUntilChanged, Subscription} from 'rxjs';
import {TreeNodeService} from '../tree-node.service';

@Component({
  selector: 'search-by-id',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './search-by-id.html',
  styleUrl: './search-by-id.css',
})
export class SearchById implements OnDestroy {
  private treeService = inject(TreeNodeService);
  private searchSubscription?: Subscription;

  searchControl = new FormControl('');
  nodes = this.treeService.nodes;

  constructor() {
    this.searchSubscription = this.searchControl.valueChanges
      .pipe(
        debounceTime(400),
        distinctUntilChanged()
      )
      .subscribe((value) => {
        if (value !== '') {
          const id = value ? Number(value) : null;
          this.treeService.searchId.set(id);
        }
      });
  }

  ngOnDestroy() {
    this.searchSubscription?.unsubscribe();
  }
}
