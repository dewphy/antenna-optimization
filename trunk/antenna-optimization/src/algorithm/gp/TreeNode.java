package algorithm.gp;



public class TreeNode {
	
	private String value;
	private TreeNode right;
	private TreeNode left;
	
	
	private int size;
	private int depth;
	public TreeNode(){
	
	}
	
	public TreeNode(String value) {
		this.value=value;
		right=null;
		left=null;
		
		size=0;
		depth=1;
	}
	
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public TreeNode getRight() {
		return right;
	}
	
	
	public void setRight(TreeNode right) {
		if (left!=null){
			//size=left.getSize()+right.getSize()+1;
			
			depth=Math.max(left.getDepth(),right.getDepth())+1;
//			if (left.getDepth()<right.getDepth()){
//				depth=right.getDepth()+1;
//			} else {depth=left.getDepth()+1;}
			
		}	
		else{	//size=right.getSize()+1;
				depth=right.getDepth()+1;
		}
		
		this.right = right; 
	}
	
	
	public TreeNode getLeft() {
		return left;
	}
	public void setLeft(TreeNode left) {
		
		if (right!=null){
			//size=left.getSize()+right.getSize()+1;
			depth=Math.max(left.getDepth(),right.getDepth())+1;
//			if (right.getDepth()<left.getDepth()){
//				depth=left.getDepth()+1;
//			} else {depth=right.getDepth()+1;}
		}
			
		else{	
			//size=left.getSize()+1;
			depth=left.getDepth()+1;
		}
		
		this.left = left; 
		
	}
	
	public int getDepth(){
		return depth;
	}
	public TreeNode copy(){
		TreeNode newNode=new TreeNode(this.value);
		if (this!=null){
			
			if (left!=null){
				newNode.setLeft(left.copy());
			}
			if (right!=null) {newNode.setRight(right.copy());}
			//newNode.setSize(this.getSize());
		}
		//newNode.setSize(this.getSize());
		return newNode;
	}
	
	public void printNode(){
		printNode(this);
	}
	
	public void printNode(TreeNode current){
		if (current!=null){
			System.out.print(current.getValue()+" ");
			printNode(current.getLeft());
			printNode(current.getRight());
		}
	}
	public int getSize(){
		size=0;
		return getSize(this);
	}
	public int getSize(TreeNode node){
		
		if (node!=null){
			size++;
			getSize(node.getLeft());
			getSize(node.getRight());
		}
		return size;
	}
	public void setSize(int size){
		this.size=size;
	}
	
	public TreeNode getParentNode(TreeNode node){
		//System.out.println("Entered getParentNode");
		
		if (node!=null && this!=null){
			//System.out.println("Entered 1: Left node");
			//left.printNode();
			//System.out.println("Right node: ");
			//right.printNode();
			//System.out.println();
			if (node.equals(left) || node.equals(right)){
				return this;
			
			}
			else {
//				System.out.println("Entered 2");
				TreeNode foundNode=null;
				if (left!=null){
					foundNode=left.getParentNode(node);
				}
				if (foundNode!=null){
					return foundNode;
				}
//				System.out.println("Entered 3");
				
				if (right!=null){
					foundNode=right.getParentNode(node);
				}
				
				if (foundNode!=null){
					return foundNode;
				}
			}
		}
		//System.out.println("Entered 3");
		return null;
		
	}
	
	public boolean isLeftNode(TreeNode node){
		if (left!=null && node!=null){
			if (left.equals(node)){
				return true;
			}
		}
	 return false;
	}
	
	public boolean isRightNode(TreeNode node){
		if (right!=null && node!=null){
			if (right.equals(node)){
				return true;
			}
		}
		return false;
	}
	
	public boolean equals(Object anotherNode){
//		this.printNode();
//		System.out.println();
//		((TreeNode) anotherNode).printNode();
		if (this!=null && anotherNode!=null){
			if (((TreeNode) anotherNode).getValue().equals(value)){
				boolean result=true;
				if (((TreeNode) anotherNode).getLeft()!=null && this.getLeft()!=null){
					result=this.getLeft().equals(((TreeNode) anotherNode).getLeft());
				}
				else if (((TreeNode) anotherNode).getLeft()==null && this.getLeft()==null){
					result=true;
				}
				else result=false;
				
				if (((TreeNode) anotherNode).getRight()!=null && this.getRight()!=null){
					return result && this.getRight().equals(((TreeNode) anotherNode).getRight());
				}
				else if (((TreeNode) anotherNode).getRight()==null && this.getRight()==null){
					result=result && true;
				}
				else result=false;
				
				return result;
			}
			
			return false;
		}else if (this==null && anotherNode==null){return true;}
			
		return false;
			
	}
}

