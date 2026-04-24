export interface TreeNode {
  id: number;
  children: TreeNode[];
  content: string;
  hasChildren: boolean;
  isOpen: boolean;
  match: boolean;
  name: string;
  parentId: number | null;
}

export interface TreeNodeResponse {
  id: number;
  name: string;
  content: string;
  match: boolean;
  hasChildren: boolean;
  parentId: number | null;
  children: TreeNodeResponse[];
}

export interface AddNodeRequest {
  name: string;
  content: string;
  parentId: number | null;
}
