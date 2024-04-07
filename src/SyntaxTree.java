
/**
 * SyntaxTree class used to create parse tree
 */
public class SyntaxTree {
    
    private Node root;

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
        StringBuilder dashes = new StringBuilder();

        // add a dash to output string for how "deep" the Node is in the CST
        for(int i = 0; i < depth; i++) {
            dashes.append("-");
        }
        return dashes.toString();
    }

    /**
     * performs a depth first traversal of the CST and constructs
     *      a formatted output string of the CST 
     * @param start Node to start traversal at, enables recursion
     * @return formatted String of Tree for output
     */
    public String depthFirstTraversal(Node start) {
        StringBuilder cst = new StringBuilder();

        // formatting and adding "name" of Node to output string
        cst.append(dashes(getDepth(start)));
        String val = start.getValue();
        if(val.length() == 1) {
            cst.append("[" + val + "]\n");
        } else {
            cst.append("<" + val + ">\n");
        }
        
        // if the node has children, perform a depth first traversal on each child
        if(start.hasChildren()) {
            for(Node child : start.getChildren()) {
                cst.append(depthFirstTraversal(child));
            }
        }
        return cst.toString();
    }

    /**
     * toString function, calls depth first traversal function starting at root
     * @return
     */
    public String toString() {
        return depthFirstTraversal(root);
    }
}
