import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Code Generator phase of Compiler
 * 
 * Note: strings are considered equal if they point to the same address
 */
public class CodeGenerator extends Component {

    private int warningCount;

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
        warningCount = 0;

        AST = ast;
        symbolTable = t;
        scopeTree = scope;

        varTable = new VariableTable();
        jumpTable = new JumpTable();

        byteCount = 0;
        
        // strings "true" and "false" stored in heap
        endOfHeap = 244;
        storedStrings = convertToASCII("false") + convertToASCII("true");

        opcodes = "";
        executableImage = new ArrayList<>(256);

        log("INFO", "Generating code for program " + Integer.toString(programNo) + "...");

        // begin code generation
        generate(AST);

        if(success()) {
            log("INFO", "Code generation completed with 0 error(s) and " + warningCount + " warning(s)\n");
            System.out.println(varTable.toString());
            System.out.println(jumpTable.toString());
            printExecutableImage(programNo);
        } else {
            log("ERROR", "Generated image exceeds maximum storage (256 bytes)\n");
            log("ERROR", "Code generation failed with 1 error(s) and " + warningCount + " warning(s)\n");
        }
    }

    /**
     * fill all empty space in the execution environment with "00"
     */
    private void fill() {
        while(opcodes.length()/2 < 256) {
            opcodes += "00";
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

        // fill remaining bytes with 00s
        fill();

        // add string values at end of heap
        opcodes = opcodes.substring(0, (endOfHeap-1)*2) + storedStrings;

        fill();
        
        // separate and split into array for easier formatted printing and checking
        opcodes = opcodes.replaceAll("..(?!$)", "$0 ");
        executableImage = new ArrayList<>(Arrays.asList(opcodes.split(" ")));
    }

    /**
     * performs depth-first in-order traversal on AST to generate opcodes
     * @param children children to traverse
     * @param scope current scope to lookup ids
     */
    private void depthFirstTraversal(ArrayList<Node> children, int scope) {

        for(Node child : children) {
            String val = child.getValue();

            // branch node
            if(child.hasChildren()) {
                ArrayList<Node> grandchildren = child.getChildren();
                
                if(val.equals("PrintStatement")) {
                    log("DEBUG", "generating code for print statement");
                    // get item to be printed
                    String toPrint = grandchildren.get(0).getValue();

                    if(Pattern.matches("[a-z]", toPrint)) {
                        // print contents of id
                        opcodes += "AC"; // load Y reg with temp address
                        opcodes += varTable.lookup(toPrint, scope).getTempAddress();

                        if(symbolTable.lookup(toPrint, "string") != null) {
                            opcodes += "A202FF"; // load X reg with const "02" and print (string)
                        } else {
                            opcodes += "A201FF"; // load X reg with const "01" and print (int or bool)
                        }
                        byteCount += 6;
                    } else if (Pattern.matches("[0-9]", toPrint)) {
                        // print const digit
                        // load Y reg with const digit, load X reg with const "01", and print
                        opcodes += "A00" + Integer.toHexString(Integer.parseInt(toPrint)).toUpperCase() + "A201FF";
                        byteCount += 5;
                    } else if (Pattern.matches("true", toPrint)) {
                        // print "true" (stored at 0xFB)
                        // load Y reg with address FB 00, load X reg with const "02", and print
                        opcodes += "ACFB00A202FF";
                        byteCount += 6;
                    } else if (Pattern.matches("false", toPrint)) {
                        // print "false" (stored at 0xF5)
                        // load Y reg with address F5 00, load X reg with const "02", and print
                        opcodes += "ACF500A202FF";
                        byteCount += 6;
                    } else {
                        // print const string = add to heap, print from mem
                        // add new string value onto front of heap (heap works from bottom up)
                        endOfHeap -= (toPrint.length()-1);
                        storedStrings = convertToASCII(toPrint) + storedStrings;

                        // load Y reg with new string location pointer, load X red with const "02", and print
                        opcodes += "AC";
                        opcodes += Integer.toHexString(endOfHeap-1).toUpperCase();
                        opcodes += "A202FF";
                        byteCount += 5;
                    }

                } else if(val.equals("IfStatement")) {
                    log("DEBUG", "generating code for if statement");
                    if(grandchildren.size() > 2) { // value, comparator, value, block
                        int bytesBefore = byteCount;
                        compare(grandchildren.get(0), grandchildren.get(2), grandchildren.get(1).getValue().equals("=="));
                        
                        String jump = "J"+jumpTable.getTable().size();
                        opcodes += jump;
                        byteCount++;

                        jumpTable.addEntry(jump, Integer.toHexString(byteCount-bytesBefore).toUpperCase());
                    } else { // value, block
                        // boolean value
                        boolean bool = Boolean.parseBoolean(grandchildren.get(0).getValue());

                        if(bool) {
                            // always true
                            opcodes += "A201ECFF00D000";
                            byteCount += 8;
                            depthFirstTraversal(grandchildren.get(4).getChildren(), scope++);

                        } else {
                            // otherwise, dead code
                            log("WARNING", "Dead code detected. Code block within if statement will not be executed.");
                            warningCount++;
                        }
                    }
                } else if (val.equals("WhileStatement")) {
                    log("DEBUG", "generating code for while statement");
                    if(grandchildren.size() > 2) { // value, comparator, value, block
                        compare(grandchildren.get(0), grandchildren.get(2), grandchildren.get(1).getValue().equals("=="));
                    } else { // value, block
                        // boolean value
                        boolean bool = Boolean.parseBoolean(grandchildren.get(0).getValue());

                        if(bool) {
                            // always true
                            log("WARNING", "Infinite loop detected. Run generated image with caution.");
                            warningCount++;

                            opcodes += "A201ECFF00D0J" + jumpTable.getTable().size() + "XX";

                            int bytesBefore = byteCount;
                            byteCount += 8;
                            
                            depthFirstTraversal(grandchildren.get(4).getChildren(), scope++);
                            int difference = byteCount - bytesBefore;

                            jumpTable.addEntry("J"+jumpTable.getTable().size(), Integer.toHexString(difference).toUpperCase());

                        } else {
                            // otherwise, dead code
                            log("WARNING", "Dead code detected. Code block within while loop will not be executed.");
                            warningCount++;
                        }
                    }
                } else if (val.equals("VarDecl")) {
                    // initialize variable with id name and scope
                    String type = grandchildren.get(0).getValue();
                    String id = grandchildren.get(1).getValue();

                    // add variable entry to table
                    varTable.addEntry(id, scope);
                    log("DEBUG", "generating code to initialize variable " + id + " at scope " + scope);

                    // assign ids with default values
                    if(type.equals("int")) {
                        assignInt(id, 0, scope); // int default value = 00
                    } else if (type.equals("boolean")) {
                        assignBoolean(id, false, scope); // boolean default value = 00
                    } else if (type.equals("string")) {
                        assignString(id, "", scope);
                    } else {
                        log("ERROR", "If you are reading this message something is very broken."); break;
                    }

                } else if (val.equals("AssignmentStatement")) {
                    log("DEBUG", "generating code for assignment statement");
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
            } // leaf node
        }
    }

    /**
     * comparison between left and right nodes
     * @param left left node
     * @param right right node
     * @param equal true if comparator is "==", false if comparator is "!="
     */
    private void compare(Node left, Node right, boolean equal){
        String compareEqual = "";
        boolean firstPass = true;
        Node[] nodes = {left, right};
        char c = 'A';
    
        for(Node n : nodes) {
          c++;
    
          // Compare integers
          if (Pattern.matches("\\d+", n.getValue())) {
            if(firstPass) {
              compareEqual += "A2";
            }
            compareEqual += String.format("%02X", Integer.parseInt(n.getValue()));
    
            // Compare strings
          } /*else if (Pattern.matches("\\[[a-z]*]", n.getValue())) {
            if(firstPass) {
              compareEqual += "AE";
            }
            initializeVar(c, AST.getDepth(n)-1);
            assignString(c, n.getValue(), AST.getDepth(n)-1);
            compareEqual += varTable.getTemp(c, AST.getDepth(n)-1);
    
            // Compare booleans
          }*/ else if (Pattern.matches("true|false", n.getValue())) {
            if(firstPass) {
              compareEqual += "A2";
            }
            int bool = 0;
            if(n.getValue().equals("true")) {
              bool = 1;
            }
    
            compareEqual += String.format("%02X", bool);
    
            // Compare variables
          } /*else if (Pattern.matches("[a-z]", n.getValue())) {
            if(firstPass) {
              compareEqual += "AE";
            }
            compareEqual += varTable.lookup(n.getValue(), AST.getDepth(n)).getTempAddress();
          } */
          if(firstPass) {
            compareEqual += "EC";
          }
          firstPass = false;
        }
        compareEqual += "D0";
        if(!equal) {
          compareEqual += "20";
        }
    
        byteCount += compareEqual.length() / 2;
        opcodes += compareEqual;
      }

    /**
     * assigns given int value to specified variable
     * @param id name of id that value will be assigned to
     * @param str int value that will be assigned to id
     * @param scope scope of id for lookup
     */
    private void assignInt(String id, int digit, int scope) {
        opcodes += "A9";        // load accumulator
        opcodes += "0" + digit; // with specified digit

        opcodes += "8D"; // store accumulator contents at specified address in little endian format

        opcodes += varTable.lookup(id, scope).getTempAddress(); // find temp address and add it to opcodes

        byteCount += 5; // increment byte count with # of bytes used for above opcodes
    }

    /**
     * assigns given boolean value to specified variable
     * @param id name of id that value will be assigned to
     * @param str boolean value that will be assigned to id
     * @param scope scope of id for lookup
     */
    private void assignBoolean(String id, boolean value, int scope) {
        opcodes += "A9"; // load accumulator

        if(value) {
            opcodes += "01";  // true
        } else {
            opcodes += "00";  // false
        }

        opcodes += "8D"; // store accumulator contents
        opcodes += varTable.lookup(id, scope).getTempAddress(); // find temp address and add it to opcodes

        // increment bytes used
        byteCount += 5;
    }

    /**
     * assigns given string value to specified variable
     * @param id name of id that value will be assigned to
     * @param str String value that will be assigned to id
     * @param scope scope of id for lookup
     */
    private void assignString(String id, String str, int scope) {
        opcodes += "A9"; // load accumulator

        if(!str.isEmpty()) {
            // add new string value onto front of heap (heap works from bottom up)
            endOfHeap -= (str.length()-1);
            storedStrings = convertToASCII(str) + storedStrings;
            opcodes += Integer.toHexString(endOfHeap-1).toUpperCase();
        } else {             // if string is empty (either default value for vardecl or just assigned an empty string),
            opcodes += "FF"; // point to the last byte, which is always a "00" since it terminates the heap
        }

        opcodes += "8D";
        opcodes += varTable.lookup(id, scope).getTempAddress(); // find temp address and add it to opcodes

        // increment bytes used
        byteCount += 5;
    }

    /**
     * print executable image in 8x32 grid of bytes
     * @param programNo program number for output header
     */
    private void printExecutableImage(int programNo) {
        // print header
        System.out.println("Program " + programNo + " Executable Image");
        System.out.println("------------------------------------");

        StringBuilder output = new StringBuilder("");

        // print in 8x32 grid
        for(int i = 0; i < 256; i++) {
            output.append(executableImage.get(i) + "\t");
            if((i + 1) % 8 == 0) {
                output.append("\n");
            }
        }

        System.out.println(output.toString());
    }

    /**
     * replaces temporary memory addresses with formatted real addresses
     */
    private void calculateRealAddresses() {
        for(VariableEntry entry : varTable.getTable()) {
            // replace all temp addresses with next available byte address
            opcodes = opcodes.replaceAll(entry.getTempAddress(), (String.format("%02X", byteCount++)+"00"));
        }
    }

    /**
     * replaces temporary jump addresses with formatted real addresses
     */
    private void calculateJumpAddresses() {
        jumpTable.getTable().forEach((key, value) -> {
            opcodes = opcodes.replaceAll(key, value);
        });
    }

    /**
     * converts a string to hex ASCII code to add to heap
     * @param input string to convert
     * @return string of ASCII characters (plus 00 terminator)
     */
    private String convertToASCII(String input) {
        String output = "";

        for(char ch : input.toCharArray()) {
            switch(ch){
                // symbols
                case ' ': output += "20"; break;
                case '!': output += "21"; break;
                case '\"': output += "22"; break;
                case '#': output += "23"; break;
                case '$': output += "24"; break;
                case '%': output += "25"; break;
                case '&': output += "26"; break;
                case '\'': output += "27"; break;
                case '(': output += "28"; break;
                case ')': output += "29"; break;
                case '*': output += "2A"; break;
                case '+': output += "2B"; break;
                case ',': output += "2C"; break;
                case '-': output += "2D"; break;
                case '.': output += "2E"; break;
                case '/': output += "2F"; break;

                // digits
                case '0': output += "30"; break;
                case '1': output += "31"; break;
                case '2': output += "32"; break;
                case '3': output += "33"; break;
                case '4': output += "34"; break;
                case '5': output += "35"; break;
                case '6': output += "36"; break;
                case '7': output += "37"; break;
                case '8': output += "38"; break;
                case '9': output += "39"; break;

                // more symbols
                case ':': output += "3A"; break;
                case ';': output += "3B"; break;
                case '<': output += "3C"; break;
                case '=': output += "3D"; break;
                case '>': output += "3E"; break;
                case '?': output += "3F"; break;
                case '@': output += "40"; break;

                // uppercase letters
                case 'A': output += "41"; break;
                case 'B': output += "42"; break;
                case 'C': output += "43"; break;
                case 'D': output += "44"; break;
                case 'E': output += "45"; break;
                case 'F': output += "46"; break;
                case 'G': output += "47"; break;
                case 'H': output += "48"; break;
                case 'I': output += "49"; break;
                case 'J': output += "4A"; break;
                case 'K': output += "4B"; break;
                case 'L': output += "4C"; break;
                case 'M': output += "4D"; break;
                case 'N': output += "4E"; break;
                case 'O': output += "4F"; break;
                case 'P': output += "50"; break;
                case 'Q': output += "51"; break;
                case 'R': output += "52"; break;
                case 'S': output += "53"; break;
                case 'T': output += "54"; break;
                case 'U': output += "55"; break;
                case 'V': output += "56"; break;
                case 'W': output += "57"; break;
                case 'X': output += "58"; break;
                case 'Y': output += "59"; break;
                case 'Z': output += "5A"; break;

                // even more symbols
                case '[': output += "5B"; break;
                case '\\': output += "5C"; break;
                case ']': output += "5D"; break;
                case '^': output += "5E"; break;
                case '_': output += "5F"; break;
                case '`': output += "60"; break;

                // lowercase letters
                case 'a': output += "61"; break;
                case 'b': output += "62"; break;
                case 'c': output += "63"; break;
                case 'd': output += "64"; break;
                case 'e': output += "65"; break;
                case 'f': output += "66"; break;
                case 'g': output += "67"; break;
                case 'h': output += "68"; break;
                case 'i': output += "69"; break;
                case 'j': output += "6A"; break;
                case 'k': output += "6B"; break;
                case 'l': output += "6C"; break;
                case 'm': output += "6D"; break;
                case 'n': output += "6E"; break;
                case 'o': output += "6F"; break;
                case 'p': output += "70"; break;
                case 'q': output += "71"; break;
                case 'r': output += "72"; break;
                case 's': output += "73"; break;
                case 't': output += "74"; break;
                case 'u': output += "75"; break;
                case 'v': output += "76"; break;
                case 'w': output += "77"; break;
                case 'x': output += "78"; break;
                case 'y': output += "79"; break;
                case 'z': output += "7A"; break;

                // other symbols
                case '{': output += "7B"; break;
                case '|': output += "7C"; break;
                case '}': output += "7D"; break;
                case '~': output += "7E"; break;
                
                default: output += "00"; break;
            }
        }

        return output + "00";
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