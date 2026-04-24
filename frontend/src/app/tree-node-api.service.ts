import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AddNodeRequest, TreeNode} from './tree-node.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TreeNodeApiService {
  private readonly basePath = 'http://localhost:8080/api/tree/nodes';

  constructor(private readonly http: HttpClient) {
  }

  addNode(node: AddNodeRequest): Observable<TreeNode> {
    return this.http.post<TreeNode>(`${this.basePath}`, node);
  }
}
