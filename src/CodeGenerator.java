import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.HashMap;

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
    private String opcodes;

    // static table to hold temp addresses
    private VariableTable varTable;

    // jump distance table
    private JumpTable jumpTable;

    private String storedStrings;

    public CodeGenerator(SyntaxTree ast, SymbolTable t, SyntaxTree scope, int programNo) {
        // initialize flags and variables
        AST = ast;
        symbolTable = t;
        scopeTree = scope;

        varTable = new VariableTable();
        jumpTable = new JumpTable();

        byteCount = 0;
        endOfHeap = 255;

        storedStrings = "";

        opcodes = "";
        executableImage = new ArrayList<>(256);

        log("INFO", "Generating code for program " + Integer.toString(programNo) + "...");

        generate(AST);

        if(success()) {
            log("INFO", "Code generation completed with 0 error(s) and 0 warning(s)\n");
            System.out.println(varTable.toString());
            System.out.println(jumpTable.toString());
            printExecutableImage(programNo);
        } else {
            log("ERROR", "Generated image exceeds maximum storage (256 bytes)\n");
            log("ERROR", "Code generation failed with 1 error(s) and 0 warning(s)\n");
        }
    }

    /**
     * fill all empty space in the execution environment with "00"
     */
    private void fill() {
        while(executableImage.size() < 256) {
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

        // should just be 0, but save root as local variable for later scope checking
        Node scopeRoot = scopeTree.getRoot();
        depthFirstTraversal(children, Integer.parseInt(scopeRoot.getValue()));

        // halt the program (BRK = 00)
        opcodes += "00";
        byteCount++;

        // calculate real addresses and replace temporary addresses (backpatching)
        calculateRealAddresses();

        // replace temporary jump values with actual values (^^^)
        calculateJumpAddresses();

        opcodes = opcodes.replaceAll("..(?!$)", "$0 ");
        executableImage = new ArrayList<>(Arrays.asList(opcodes.split(" ")));

        fill();

        // string values at end of heap
    }

    private void depthFirstTraversal(ArrayList<Node> children, int scope) {

        for(Node child : children) {
            String val = child.getValue();

            // branch node
            if(child.hasChildren()) {
                ArrayList<Node> grandchildren = child.getChildren();
                
                if(val.equals("PrintStatement")) {
                    // get item to be printed
                    String toPrint = grandchildren.get(0).getValue();

                    if(Pattern.matches("[a-z]", toPrint)) {
                        // print contents of id
                        // print(varTable.lookup(toPrint, scope).getTempAddress());
                    } else if (Pattern.matches("true|false", toPrint)) {
                        // print("true" or "false");
                    } else {
                        // print string
                    }

                } else if(val.equals("IfStatement")) {
                    // compare(val1, val2, comparator)
                } else if (val.equals("WhileStatement")) {
                    // compare(val1, val2, comparator)
                } else if (val.equals("VarDecl")) {
                    // initialize variable with id name and scope
                    String id = grandchildren.get(1).getValue();

                    varTable.addEntry(id, scope);
                    generateVarDecl(id, scope);

                } else if (val.equals("AssignmentStatement")) {
                    // checks if the id is being assigned to an expr
                    if(grandchildren.size() > 2 && grandchildren.get(2).getValue().equals("+")) {
                        // increment id
                        // either index 1 or index 3 is the id, the other is the digit
                    } else {
                        // get id, value to be assigned, and find type of new value
                        // type checking has already occured, so type is only used to determine which
                        // method of assignment is used
                        String id = grandchildren.get(0).getValue();
                        String newValue = grandchildren.get(1).getValue();
                        String newValueType = getType(newValue);

                        switch (newValueType) {
                            case "int": assignInt(id, Integer.parseInt(newValue), scope); break;
                            case "boolean": assignBoolean(id, Boolean.parseBoolean(newValue), scope); break;
                            case "string": assignString(id, newValue, scope); break;
                            default: log("ERROR", "If you are reading this message something is very broken."); break;
                        }
                    }

                } else { 
                    // Block
                    depthFirstTraversal(grandchildren, scope++);
                }
                
            // leaf node
            } else {
                // im not sure
            }
        }
    }

    /**
     * load accumulator with default value
     * @param id
     * @param scope
     */
    private void generateVarDecl(String id, int scope) {
        log("DEBUG", "generating code to initialize variable " + id + " at scope " + scope);

        opcodes += "A9"; // load accumulator
        opcodes += "00"; // with constant (default value = 00)

        opcodes += "8D"; // store accumulator contents at specified address in little endian format
        opcodes += varTable.lookup(id, scope).getTempAddress();

        byteCount += 5; // increment byte count with # of bytes used for above opcodes
    }

    private void assignInt(String id, int digit, int scope) {
        opcodes += "A9";        // load accumulator
        opcodes += "0" + digit; // with specified digit

        opcodes += "8D"; // store accumulator contents at specified address in little endian format

        opcodes += varTable.lookup(id, scope).getTempAddress(); // find temp address and add it to opcodes

        byteCount += 5; // increment byte count with # of bytes used for above opcodes
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

    private void calculateRealAddresses() {
        for(VariableEntry entry : varTable.getTable()) {
            // replace all temp addresses with next available byte address
            opcodes = opcodes.replaceAll(entry.getTempAddress(), String.format("%1$02X00", byteCount++));
        }
    }

    private void calculateJumpAddresses() {
        jumpTable.getTable().forEach((key, value) -> {
            opcodes = opcodes.replaceAll(key, String.format("%1$02X00", value));
        });
    }

    /**
     * determines if code generation completed without errors
     * @return true if no errors
     */
    public boolean success() {
        // only error occurs if image exceeds maximum storage (256 bytes)
        return executableImage.size() <= 256 || byteCount >= endOfHeap;
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