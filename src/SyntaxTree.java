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

    public void addNode(Node newChild) {
        this.root.addChild(newChild);
    }

    public void addTree(SyntaxTree newTree) {
        Node newTreeRoot = newTree.getRoot();

        this.root.addChild(newTreeRoot);
        newTreeRoot.setParent(this.root);
    }

    public int getDepth(Node n) {
        int depth = 0;

        while (n.getParent() != null) {
            n = n.getParent();
            depth++;
        }

        return depth;
    }

    public String dashes(int depth) {
        String dashes = "";

        for(int i = 0; i < depth; i++) {
            dashes += "-";
        }

        return dashes;
    }

    public String depthFirstTraversal(Node start) {
        String cst = "";

        cst += dashes(getDepth(start));
        cst += start.getValue() + "\n";
        
        if(start.hasChildren()) {
            for(Node child : start.getChildren()) {
                cst += depthFirstTraversal(child);
            }
        }

        return cst;
    }
}
