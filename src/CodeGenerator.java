import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Code Generator phase of Compiler
 * 
 * Note: strings are considered equal if they point to the same address
 */
public class CodeGenerator extends Component {

    private int byteCount;      // number of bytes used
    private int endOfHeap;      // undex of byte marking the end of the heap

    private SyntaxTree AST;
    private SymbolTable symbolTable;
    private SyntaxTree scopeTree;

    // array of strings (can also store temp values), size 256 MAX (bytes)
    private ArrayList<String> executableImage;

    // static table to hold temp addresses
    private VariableTable varTable;

    // jump distance table
    private JumpTable jumpTable;

    public CodeGenerator(SyntaxTree ast, SymbolTable t, SyntaxTree scope, int programNo) {
        // initialize flags and variables
        AST = ast;
        symbolTable = t;
        scopeTree = scope;

        varTable = new VariableTable();
        jumpTable = new JumpTable();

        byteCount = 0;
        endOfHeap = 0;

        executableImage = new ArrayList<>(256);
        initialize();

        log("INFO", "Generating code for program " + Integer.toString(programNo) + "...");

        generate(AST);

        if(success()) {
            log("INFO", "Code generation completed with 0 error(s) and 0 warning(s)\n");
            printExecutableImage(programNo);
        } else {
            log("ERROR", "Code generation failed with 1 error(s) and 0 warning(s)\n");
        }
    }

    /**
     * initialize all bytes in the execution environment to "00"
     */
    private void initialize() {
        for(int i = 0; i < 256; i++) {
            executableImage.add("00");
        }
    }

    /**
     * begins the generation of opcodes using the AST
     * @param ast
     */
    private void generate(SyntaxTree ast) {
        Node astRoot = ast.getRoot();
        ArrayList<Node> children = astRoot.getChildren();

        Node scopeRoot = scopeTree.getRoot();
        depthFirstTraversal(children, scopeRoot);

        // halt the program (BRK = 00)
        executableImage.set(byteCount, "00");
        byteCount++;

        // calculate address indexes
        // replace temporary addresses with real addresses (backpatching)
        // replace temporary jump values with actual values (^^^)

        // string values at end of heap
    }

    private void depthFirstTraversal(ArrayList<Node> children, Node scope) {

        for(Node child : children) {
            String val = child.getValue();

            // branch node
            if(child.hasChildren()) {
                ArrayList<Node> grandchildren = child.getChildren();
                
                if(val.equals("PrintStatement")) {
                    //
                } else if(val.equals("IfStatement")) {
                    //
                } else if (val.equals("WhileStatement")) {
                    //
                } else if (val.equals("VarDecl")) {
                    String type = grandchildren.get(0).getValue();
                    if(Pattern.matches("int|string|boolean", type)) {
                        // initialize variable with id name and scope
                        String id = grandchildren.get(1).getValue();
                        int s = Integer.parseInt(scope.getValue());
                        varTable.addEntry(id, s);
                        initializeVar(id, s);
                    }
                } else if (val.equals("AssignmentStatement")) {
                    //
                } else { 
                    // Block
                    depthFirstTraversal(grandchildren, scope);
                }
                
            // leaf node
            } else {
                // im not sure
            }
        }
    }

    private void initializeVar(String id, int scope) {
        executableImage.set(byteCount++, "A9"); // load accumulator
        executableImage.set(byteCount++, "00"); // with constant (default value = 00)

        executableImage.set(byteCount++, "8D");                                          // store accumulator contents
        executableImage.set(byteCount++, varTable.lookup(id, scope).getTempAddress());   // at specified address
        executableImage.set(byteCount++, "XX");                                          // in little endian format
    }

    private void assignInt(String id, int digit, int scope) {
        executableImage.set(byteCount++, "A9");      // load accumulator
        executableImage.set(byteCount++, "0"+digit); // with specified digit

        executableImage.set(byteCount++, "8D"); // store accumulator contents
        executableImage.set(byteCount++, "TX"); // at specified address
        executableImage.set(byteCount++, "XX"); // in little endian format
    }

    private void assignBoolean(String id, boolean value, int scope) {
        executableImage.set(byteCount++, "A9");      // load accumulator

        if(value) {
            executableImage.set(byteCount++, "01");  // true
        } else {
            executableImage.set(byteCount++, "00");  // false
        }

        executableImage.set(byteCount++, "8D"); // store accumulator contents
        executableImage.set(byteCount++, "TX"); // at specified address
        executableImage.set(byteCount++, "XX"); // in little endian format
    }

    private void assignString(String id, String str, int scope) {
        executableImage.set(byteCount++, "A9"); // load accumulator
        executableImage.set(byteCount++, "XX"); // with temp address
        executableImage.set(byteCount++, "A9");
    }

    private void printExecutableImage(int programNo) {
        // print header
        System.out.println("Program " + programNo + " Executable Image");
        System.out.println("------------------------------------");

        StringBuilder output = new StringBuilder("");

        // print in 32x8 grid
        for(int i = 0; i < 256; i++) {
            output.append(executableImage.get(i) + "\t");
            if((i + 1) % 8 == 0) {
                output.append("\n");
            }
        }

        System.out.println(output.toString());
    }

    /**
     * determines if code generation completed without errors
     * @return true if no errors
     */
    public boolean success() {
        // only error occurs if image exceeds maximum storage (256 bytes)
        return executableImage.size() <= 256;
    }

    /**
     * logs formatted debug message (only if verbose mode is enabled)
     * @param alert type of alert
     * @param msg specific message
     */
    public void log(String alert, String msg) {
        super.log(alert, "Code Generator", msg);
    }
}