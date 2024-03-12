import java.util.ArrayList;

/**
 * parse tree class
 */
public class SyntaxTree {
    
    public Node root;

    public SyntaxTree(Node r) {
        this.root = r;
    }

    /**
     * getter for root Node
     * @return
     */
    public Node getRoot() {
        return this.root;
    }

    // dont need?
    /*
    public void addNode(Node newChild) {
        this.root.addChild(newChild);
    }

    public void addTree(SyntaxTree newTree) {
        Node newTreeRoot = newTree.getRoot();

        this.root.addChild(newTreeRoot);
        newTreeRoot.setParent(this.root);
    }*/

    /**
     * returns depth of Node n
     * @param n start Node
     * @return depth of Node in CST
     */
    public int getDepth(Node n) {
        int depth = 0;

        // traverses through Node parents beginning at specified start
        while (n.getParent() != null) {
            n = n.getParent();
            depth++;
        }

        return depth;
    }

    /**
     * adds dashes to output string based on specified depth
     * @param depth
     * @return
     */
    public String dashes(int depth) {
        String dashes = "";

        // add a dash to output string for how "deep" the Node is in the CST
        for(int i = 0; i < depth; i++) {
            dashes += "-";
        }

        return dashes;
    }

    /**
     * performs a depth first traversal of the CST and constructs
     *      a formatted output string of the CST 
     * @param start
     * @return
     */
    public String depthFirstTraversal(Node start) {
        String cst = "";

        cst += dashes(getDepth(start));
        cst += start.getValue() + "\n";
        
        // if the node has children, perform a depth first traversal on each child
        if(start.hasChildren()) {
            for(Node child : start.getChildren()) {
                cst += depthFirstTraversal(child);
            }
        }

        return cst;
    }
}
