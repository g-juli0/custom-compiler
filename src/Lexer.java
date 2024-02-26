import java.util.ArrayList;

/**
 * Lexer class for compiler
 */
public class Lexer extends Component {
    
    private int warningCount;   // number of detected warnings
    private int errorCount;     // number of detected errors

    private ArrayList<Token> tokenStream;
    
    public Lexer(String program, int programNo, boolean verbose) {
        // initialize flags and local variables
        super(verbose);

        //int line = 1;   // line number of program text
        int pos = 0;    // char position in line

        warningCount = 0;
        errorCount = 0;

        tokenStream = new ArrayList<Token>();

        this.log("INFO", "Lexing program " + Integer.toString(programNo) + "...");

        ArrayList<String> programLines = breakIntoLineList(program);

        for(String line : programLines) {
            String current = Character.toString(line.charAt(i));

            if(isComment(current)) {
                // adjust position
            } else if (isWhiteSpace(current)) {
                continue;
            } else if ( true /*isValid(current)*/) {
                this.log("DEBUG", "[ " + current + " ] detected at (" + Integer.toString(line) 
                    + ":" + Integer.toString(pos) + ")");
            } else {
                errorCount++;
                this.log("ERROR", "Unrecognized token [ " + current + " ] detected at (" 
                    + Integer.toString(line) + ":" + Integer.toString(pos) + ")");
            }

            // if new line or carriage return is detected, increment line number
            if(current.equals(System.getProperty("line.separator"))) {
                line++;
                pos = 0;
            }
            // increase character position after all processing and checking is done
            pos++;
        }

        // print success or failure message
        if(errorCount == 0) {
            this.log("INFO", "Lex completed with " + Integer.toString(errorCount) + " errors\n");
        } else {
            this.log("ERROR", "Lex failed with " + Integer.toString(errorCount) + " errors\n");
        }
    }

    /**
     * Further breaks program string into an ArrayList of lines
     * @param program String containing whole program
     * @return ArrayList of Strings containing each line of the program
     */
    private ArrayList<String> breakIntoLineList(String program) {
        ArrayList<String> lines = new ArrayList<String>();

        return lines;
    }

    /*
    private boolean isValid(String c) {
        return grammar.contains(c);
    }
    */

    private boolean isWhiteSpace(String c) {
        return c.equals("\t") || c.equals(" ") || c.equals("\r") || c.equals("\n");
    }

    private boolean isComment(String c) {
        return (c.equals("/") || c.equals("*"));
    }

    private int processComment(String c) {
        int pos = 0;

        return pos;
    }

    /*
    private Token tokenize() {
        return new Token(Kind.EOP, "");
    }*/

    private void log(String alert, String msg) {
        super.log(alert, "Lexer", msg);
    }
}