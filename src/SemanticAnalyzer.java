import java.util.ArrayList;

/**
 * Semantic Analysis Component of Compiler
 */
public class SemanticAnalyzer extends Component {
    
    private int warningCount;   // number of detected warnings
    private int errorCount;     // number of detected errors

    private ArrayList<Token> tokenStream;
    private SyntaxTree AST;

    /**
     * constructor for semantic analyzer component. pseudo "parses" token stream again,
     *      only adding minimally necessary Tokens to the AST and building a symbol table
     * @param stream ArrayList of Tokens recognized by the lexer
     * @param programNo program number for debug printing
     */
    public SemanticAnalyzer(ArrayList<Token> stream, int programNo) {
        // initialize flags and variables
        this.tokenStream = stream;

        warningCount = 0;
        errorCount = 0;

        this.log("INFO", "Semantically analyzing program " + Integer.toString(programNo) + "...");

        // entry point for pseudo parse

        if(this.success()) {
            this.log("INFO", "Semantic analysis completed with " + errorCount + " error(s) and " + warningCount + " warning(s)\n");
            this.printAST();
        } else {
            this.log("ERROR", "Semantic analysis failed with " + errorCount + " error(s) and " + warningCount + " warning(s)\n");
        }
    }

    /**
     * prints formatted symbol table
     */
    public void printSymbolTable() {
        // print symbol table
    }

    /**
     * prints formatted abstract syntax tree
     */
    public void printAST() {
        // print AST
        System.out.println(this.AST.depthFirstTraversal(this.AST.getRoot()));
    }

    /**
     * determines if semantic analyzer completed without errors
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
        super.log(alert, "Semantic Analyzer", msg);
    }
}
