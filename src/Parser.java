import java.util.ArrayList;

public class Parser extends Component {
    
    private int warningCount;   // number of detected warnings
    private int errorCount;     // number of detected errors

    private ArrayList<Token> tokenStream;

    public Parser(ArrayList<Token> stream, int programNo, boolean verbose) {
        super(verbose);
        this.tokenStream = stream;

        this.log("INFO", "Parsing program " + Integer.toString(programNo) + "...");

    }

    /**
     * determines if parser completed without errors
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
    private void log(String alert, String msg) {
        super.log(alert, "Parser", msg);
    }
}
