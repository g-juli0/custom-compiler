import java.util.ArrayList;

public class SemanticAnalyzer extends Component {
    
    private int warningCount;   // number of detected warnings
    private int errorCount;     // number of detected errors

    private ArrayList<Token> tokenStream;
    private SyntaxTree AST;

    public SemanticAnalyzer(ArrayList<Token> stream, int programNo) {
        this.tokenStream = stream;

        warningCount = 0;
        errorCount = 0;

        this.log("INFO", "Semantically analyzing program " + Integer.toString(programNo) + "...");

        if(this.success()) {
            this.printAST();
            this.log("INFO", "Semantic analysis completed with " + errorCount + " error(s) and " + warningCount + " warning(s)\n");
        } else {
            this.log("ERROR", "Semantic analysis failed with " + errorCount + " error(s) and " + warningCount + " warning(s)\n");
        }
    }

    public void printAST() {
        // print AST
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
