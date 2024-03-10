import java.util.ArrayList;

/**
 * Node class for use in creating a parse tree
 */
public class Node {

    String value;
    Node parent;
    ArrayList<Node> children;

    /**
     * constructor for Node given only a value (no parent)
     * @param v value of Node
     */
    public Node(String v) {
        this.value = v;
    }

    /**
     * constructor for Node given parent and value
     * @param p parent Node
     * @param v value of Node
     */
    public Node(Node p, String v) {
        this.parent = p;
        this.value = v;
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

    public boolean hasChildren() {
        return this.children.size() > 0;
    }
}
