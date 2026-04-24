export interface TreeNode {
  id: number;
  name: string;
  content: string;
  match: boolean;
  children: TreeNode[];
}

export interface ContentResponse {
  id: number;
  content: string;
}

export interface AddNodeRequest {
  name: string;
  content: string;
  parentId: number | null;
}

export interface ReorganizeRequest {
  nodeId: number;
  newParentId: number | null;
}
