import {Component, inject, OnDestroy} from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {debounceTime, distinctUntilChanged, Subscription} from 'rxjs';
import {TreeNodeService} from '../tree-node.service';

@Component({
  selector: 'search-by-content',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './search-by-content.html',
  styleUrl: './search-by-content.css',
})
export class SearchByContent implements OnDestroy {
  private treeService = inject(TreeNodeService);
  private searchSubscription?: Subscription;

  searchControl = new FormControl('');

  constructor() {
    this.searchSubscription = this.searchControl.valueChanges
      .pipe(
        debounceTime(400),
        distinctUntilChanged()
      )
      .subscribe((value) => {
        if (value && value.length >= 3) {
          this.treeService.searchByContent(value);
        } else if (!value) {
          this.treeService.loadNodes();
        }
      });
  }

  ngOnDestroy() {
    this.searchSubscription?.unsubscribe();
  }
}
