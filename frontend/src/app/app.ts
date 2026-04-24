import {Component, signal} from '@angular/core';
import {AddNode} from './add-node/add-node';

@Component({
  selector: 'app-root',
  imports: [AddNode],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');
  showAddNode = signal(false);

  toggleAddNode() {
    this.showAddNode.update(value => !value);
  }
}
