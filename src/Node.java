import java.util.*;

public class Node{
	public int value, depth;
	boolean color;
	char[][] state;
	// list of child nodes
	public Vector children = new Vector();
	public Node Parent;

	/**
	 * @param value the current value of this node based on it's children
	 * @param parent		parent node
	 * @param color  true for black
	 * @param depth			depth of node
	 */
	public Node(int value, Node parent, char[][] state, int depth, boolean color){
	this.value = value;
	this.state = state;
	this.Parent = parent;
	this.depth = depth;
	this.color = color;
	
	}
	
	public void addChild(Node n){
	children.add(n);
	}
    
	public void deleteChild(Node n){
	children.remove(n);
	}	
}
