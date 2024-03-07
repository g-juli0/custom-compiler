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
}
