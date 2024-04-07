import java.util.ArrayList;

/**
 * Node class for use in creating a parse tree
 */
public class Node {

    private String value;
    private Node parent;
    private ArrayList<Node> children;

    /**
     * constructor for Node given only a value (null parent)
     * @param v value of Node
     */
    public Node(String v) {
        this.children = new ArrayList<>();
        this.value = v;
    }

    /**
     * constructor for Node given parent and value
     * @param p parent Node
     * @param v value of Node
     */
    public Node(String v, Node p) {
        this.children = new ArrayList<>();
        this.value = v;
        this.parent = p;
    }
    
    /**
     * getter for Node value
     * @return String value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * getter for Node parent
     * @return Node parent
     */
    public Node getParent() {
        return this.parent;
    }

    /**
     * setter for Node parent
     * @param p new parent Node
     */
    public void setParent(Node p) {
        this.parent = p;
    }

    /**
     * adds new child Node to ArrayList of child Nodes
     * @param child
     */
    public void addChild(Node child) {
        children.add(child);
    }

    /**
     * getter for children ArrayList
     * @return ArrayList of child Nodes
     */
    public ArrayList<Node> getChildren() {
        return this.children;
    }

    /**
     * checks if Node has children
     * @return true if children ArrayList not empty
     */
    public boolean hasChildren() {
        return !this.children.isEmpty();
    }
}
