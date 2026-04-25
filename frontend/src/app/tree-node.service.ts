import {effect, inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AddNodeRequest, TreeNode, TreeNodeResponse, UpdateNodeRequest} from './tree-node.model';

@Injectable({
  providedIn: 'root'
})
export class TreeNodeService {
  private readonly baseUrl = 'http://localhost:8080';
  private readonly basePath = `${this.baseUrl}/api/tree/nodes`;
  private readonly searchPath = `${this.baseUrl}/api/tree/search`;
  private http = inject(HttpClient);

  readonly nodes = signal<TreeNode[]>([]);
  selectedNodeId = signal<number | null>(null);
  selectedNode = signal<TreeNode | null>(null);
  searchId = signal<number | null>(null);
  showDeleteDialog = signal(false);
  isSearching = signal(false);

  constructor() {
    effect(() => {
      const id = this.searchId();
      if (id != null) {
        this.fetchNodeById(id);
      } else {
        this.loadNodes();
      }
    });
  }

  searchByContent(query: string): void {
    this.nodes.set([]);
    this.isSearching.set(true);
    this.http.get<TreeNodeResponse[]>(`${this.searchPath}?searchTerm=${encodeURIComponent(query)}`)
      .subscribe({
        next: (response) => {
          console.log(response);
          this.nodes.set(response.map(n => this.toTreeNode(n)));
        },
        error: (err) => console.error('Failed to search by content', err)
      });
  }

  loadNodes(): void {
    this.isSearching.set(false);
    this.http.get<TreeNodeResponse[]>(this.basePath).subscribe({
      next: (response) => {
        response.forEach(n =>
          this.nodes.set(response.map(n => this.toTreeNode(n)))
        );

      },
      error: (err) => console.error('Failed to load nodes', err)
    });
  }

  fetchNodeById(id: number): void {
    this.nodes.set([]);
    this.http.get<TreeNodeResponse>(`${this.basePath}/${id}`)
      .subscribe({
        next: (response) => this.nodes.set([this.toTreeNode(response)]),
        error: (err) => console.error('Failed to fetch node by id', err)
      });
  }

  addNode(node: AddNodeRequest): void {
    this.http.post<TreeNodeResponse>(`${this.basePath}`, node)
      .subscribe({next: (response) => this.insertNode(response)});
  }

  updateNode(node: UpdateNodeRequest): void {
    this.http.put<TreeNodeResponse>(`${this.basePath}`, node)
      .subscribe({next: (response) => this.updateNodeInTree(response)});
  }

  deleteNode(id: number): void {
    this.http.delete(`${this.basePath}/${id}`)
      .subscribe({next: () => this.removeNodeFromTree(id)});
  }

  private removeNodeFromTree(id: number): void {
    let deletableNode = this.getNodeById(id);
    let parentId = deletableNode?.parentId;
    this.nodes.update(nodes => this.filterNodeFromTree(nodes, id));
    if (this.selectedNode()?.id === id) {
      this.selectedNode.set(null);
      this.selectedNodeId.set(null);
    }
    if (parentId != null) {
      let parent = this.getNodeById(parentId);
      if (parent?.children?.length === 0) {
        this.nodes.update(nodes => this.setHasChildrenInTree(nodes, parentId, false));
      }
    }
  }

  private filterNodeFromTree(nodes: TreeNode[], id: number): TreeNode[] {
    return nodes
      .filter(node => node.id !== id)
      .map(node => ({...node, children: this.filterNodeFromTree(node.children, id)}));
  }

  selectNode(id: number | null): void {
    this.selectedNodeId.set(id);
    if (id == null) {
      this.selectedNode.set(null);
    } else {
      this.selectedNode.set(this.getNodeById(id));
    }
  }

  getNodeById(id: number): TreeNode | null {
    return this.findNodeById(this.nodes(), id);
  }

  private findNodeById(nodes: TreeNode[], id: number): TreeNode | null {
    for (const node of nodes) {
      if (node.id === id) {
        return node;
      }
      if (node.children && node.children.length > 0) {
        const found = this.findNodeById(node.children, id);
        if (found) {
          return found;
        }
      }
    }

    return null;
  }


  private setHasChildrenInTree(nodes: TreeNode[], id: number, hasChildren: boolean): TreeNode[] {
    return nodes.map(node => {
      if (node.id === id) {
        return {...node, hasChildren: hasChildren};
      }
      if (node.children && node.children.length > 0) {
        return {...node, children: this.setHasChildrenInTree(node.children, id, hasChildren)};
      }
      return node;
    });
  }

  insertNode(newNode: TreeNodeResponse): void {
    const treeNode = this.toTreeNode(newNode);
    if (newNode.parentId == null) {
      this.nodes.update(nodes => [...nodes, treeNode]);
    } else {
      this.nodes.update(nodes => this.insertInTree(nodes, newNode.parentId!, treeNode));
      this.nodes.update(nodes => this.setHasChildrenInTree(nodes, newNode.parentId!, true));
    }
  }

  private insertInTree(nodes: TreeNode[], parentId: number, newNode: TreeNode): TreeNode[] {
    return nodes.map(node => {
      if (node.id === parentId) {
        return {...node, children: [...(node.children ?? []), newNode]};
      }
      if (node.children && node.children.length > 0) {
        return {...node, children: this.insertInTree(node.children, parentId, newNode)};
      }
      return node;
    });
  }

  private toTreeNode(r: TreeNodeResponse): TreeNode {
    return {
      id: r.id,
      name: r.name,
      content: r.content,
      match: r.match,
      hasChildren: r.hasChildren,
      parentId: r.parentId,
      children: (r.children ?? []).map(c => this.toTreeNode(c))
    };
  }

  private updateNodeInTree(response: TreeNodeResponse) {
    const treeNode = this.toTreeNode(response);
    this.nodes.update(nodes => this.replaceNodeInTree(nodes, treeNode));
    if (this.selectedNode()?.id === treeNode.id) {
      this.selectedNode.set(treeNode);
    }
  }

  private replaceNodeInTree(nodes: TreeNode[], updatedNode: TreeNode): TreeNode[] {
    return nodes.map(node => {
      if (node.id === updatedNode.id) {
        return {...updatedNode, children: node.children};
      }
      if (node.children && node.children.length > 0) {
        return {...node, children: this.replaceNodeInTree(node.children, updatedNode)};
      }
      return node;
    });
  }
}
