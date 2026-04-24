import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AddNodeRequest, TreeNode, TreeNodeResponse, UpdateNodeRequest} from './tree-node.model';

@Injectable({
  providedIn: 'root'
})
export class TreeNodeService {
  private readonly basePath = 'http://localhost:8080/api/tree/nodes';
  private http = inject(HttpClient);

  readonly nodes = signal<TreeNode[]>([]);
  selectedNodeId = signal<number | null>(null);
  selectedNode = signal<TreeNode | null>(null);
  openNodes = signal<Set<number>>(new Set<number>());

  addNode(node: AddNodeRequest): void {
    this.http.post<TreeNodeResponse>(`${this.basePath}`, node)
      .subscribe({next: (response) => this.insertNode(response)});
  }

  updateNode(node: UpdateNodeRequest): void {
    this.http.put<TreeNodeResponse>(`${this.basePath}`, node)
      .subscribe({next: (response) => this.updateNodeInTree(response)});
  }

  loadNodes(parentId?: number | null): void {
    const url = parentId != null
      ? `${this.basePath}?parentId=${parentId}`
      : this.basePath;
    this.http.get<TreeNodeResponse[]>(url).subscribe({
      next: (response) => {
        if (parentId == null) {
          this.nodes.set(response.map(n => this.toTreeNode(n)));
        } else {
          response.forEach(n =>
            this.nodes.update(nodes => this.insertInTree(nodes, parentId, this.toTreeNode(n)))
          );
        }
      },
      error: (err) => console.error('Failed to load nodes', err)
    });
  }

  selectNode(id: number): void {
    this.selectedNodeId.set(id);
    this.selectedNode.set(this.getNodeById(id));
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

  openNode(id: number): void {
    this.openNodes.update(nodes => {
      nodes.add(id);
      return nodes;
    });
  }

  closeNode(id: number): void {
    this.openNodes.update(nodes => {
      const newSet = new Set(nodes);
      newSet.delete(id);
      return newSet;
    });
    this.nodes.update(nodes => this.setIsOpenInTree(nodes, id, false));
  }

  deleteChildren(parentId: number): void {
    this.nodes.update(nodes => this.removeChildrenInTree(nodes, parentId));
  }

  private removeChildrenInTree(nodes: TreeNode[], parentId: number): TreeNode[] {
    return nodes.map(node => {
      if (node.id === parentId) {
        return {...node, children: []};
      }
      if (node.children && node.children.length > 0) {
        return {...node, children: this.removeChildrenInTree(node.children, parentId)};
      }
      return node;
    });
  }

  toggleNode(id: number): void {
    if (this.openNodes().has(id)) {
      this.closeNode(id);
      this.deleteChildren(id);
    } else {
      this.openNode(id);
      this.loadNodes(id);
    }
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

  private setIsOpenInTree(nodes: TreeNode[], id: number, isOpen: boolean): TreeNode[] {
    return nodes.map(node => {
      if (node.id === id) {
        return {...node, isOpen};
      }
      if (node.children && node.children.length > 0) {
        return {...node, children: this.setIsOpenInTree(node.children, id, isOpen)};
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
      this.openNode(newNode.parentId);
      this.nodes.update(nodes => this.setIsOpenInTree(nodes, newNode.parentId!, true));
      this.nodes.update(nodes => this.setHasChildrenInTree(nodes, newNode.parentId!, true));
    }
  }

  private insertInTree(nodes: TreeNode[], parentId: number, newNode: TreeNode): TreeNode[] {
    return nodes.map(node => {
      if (node.id === parentId) {
        return {...node, isOpen: true, children: [...(node.children ?? []), newNode]};
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
      isOpen: this.openNodes().has(r.id),
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
