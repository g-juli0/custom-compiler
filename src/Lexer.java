/*
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
    private char[] grammar = {  'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 
                                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '=', '+', '$',
                                '{', '}', '(', ')', '"', '/', '*', ' ', '!'};

    private boolean debug = true;   // debug flag
    private String input;           // program
    private int line;               // line number
    private int pos;                // character position
    private int errorCount = 0;     // number of detected errors
    
    public Lexer(String program) {
        this.log("INFO", "Lexing started...");

        // regurgitate program for now
        System.out.println(program);

        if(errorCount == 0) {
            this.log("INFO", "Lex completed with " + Integer.toString(errorCount) + " errors\n");
        } else {
            this.log("ERROR", "Lex failed with " + Integer.toString(errorCount) + " errors\n");
        }
    }

    public void log(String alert, String msg) {
        super.log(alert, "Lexer", msg);
    }
}