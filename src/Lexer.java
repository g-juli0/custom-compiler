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

    // all valid characters in this grammar
    private static String grammar = "abcdefghijklmnopqrstuvwxyz0123456789=+${}()\"/*! ";
    private static int[][] transitionTable = {
        /*                      a,  b, c, d, e, f, g, h,  i, j, k, l, m, n, o,  p, q, r,  s,  t, u, v,  w, x, y, z, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,  =, +, $, {, }, (, ),  ", /, *, ! */
        /* state 0 - error */  {0,  0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0,  0, 0, 0,  0,  0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0,  0, 0, 0,  0},
        /* state 1 - start */  {2, 41, 2, 2, 2, 2, 2, 2, 48, 2, 2, 2, 2, 2, 2, 16, 2, 2, 35, 26, 2, 2, 21, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 18, 5, 5, 5, 5, 5, 5, 12, 6, 1, 10}};
    
    private int line = 1;    // line number

    private boolean debug = true;   // debug flag
    private String input;           // program
    private int pos = 1;            // character position
    private int errorCount = 0;     // number of detected errors

    private boolean commentToggle;

    private boolean unmatchedParen = false;
    private boolean unmatchedQuote = false;
    private boolean unmatchedComment = false;

    private ArrayList<Token> tokenStream;
    
    public Lexer(String program) {
        this.log("INFO", "Lexing started...");

        for(int i = 0; i < program.length(); i++) {
            String current = Character.toString(program.charAt(i));

            if(isComment(current)) {
                // adjust position
            } else if (isWhiteSpace(current)) {
                continue;
            } else if (isValid(current)) {
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

    private boolean isValid(String c) {
        return grammar.contains(c);
    }

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