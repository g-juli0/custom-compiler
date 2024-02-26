import java.util.ArrayList;

/**
 * Lexer class for compiler
 * HIERARCHY
 *  1) keyword
 *  2) id
 *  3) symbol
 *  4) digit
 *  5) char
 * 
 * lex warnings 	- missing last $, unclosed "
 *                  - unclosed comment
 *                  - no EOP symbol $
 * 
 * lex errors 	    - unrecognized character
 */
public class Lexer extends Component {
    
    private int warningCount;   // number of detected warnings
    private int errorCount;     // number of detected errors

    private ArrayList<Token> tokenStream;
    
    public Lexer(String program, int programNo, boolean verbose) {
        // initialize flags and local variables
        super(verbose);

        int line = 1;   // line number of program text
        int pos = 0;    // char position in line

        warningCount = 0;
        errorCount = 0;

        tokenStream = new ArrayList<Token>();

        this.log("INFO", "Lexing started...");

        for(int i = 0; i < program.length(); i++) {
            String current = Character.toString(program.charAt(i));

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