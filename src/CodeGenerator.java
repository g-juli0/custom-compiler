
/**
 * Code Generator phase of Compiler
 */
public class CodeGenerator extends Component {

    private int warningCount;   // number of detected warnings
    private int errorCount;     // number of detected errors

    private SyntaxTree AST;
    // need symbol table

    // array of strings (to be able to store temp values), size 256 MAX (bytes)
    // ArrayList<String> executableImage = new ArrayList<>();
    // initialize() to all "00"

    // static table to hold temp addresses
    // jump distance table

    public CodeGenerator(SyntaxTree ast, int programNo) {
        // initialize flags and variables
        AST = ast;
        warningCount = 0;
        errorCount = 0;

        log("INFO", "Generating code for program " + Integer.toString(programNo) + "...");

        if(success()) {
            log("INFO", "Code generation completed with " + errorCount + " error(s) and " + warningCount + " warning(s)\n");
        } else {
            log("ERROR", "Code generation failed with " + errorCount + " error(s) and " + warningCount + " warning(s)\n");
        }
    }

    /**
     * determines if code generation completed without errors
     * @return true if no errors
     */
    public boolean success() {
        return errorCount == 0;
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
