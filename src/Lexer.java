import java.util.ArrayList;

/**
 * Lexer class for compiler
 */
public class Lexer extends Component {
    
    private int warningCount;   // number of detected warnings
    private int errorCount;     // number of detected errors

    private ArrayList<Token> tokenStream;
    
    /**
     * constructor for lexer. tokenizes program given on constructor call
     * @param program String of program to be lexed
     * @param programNo program number for debug printing
     * @param verbose verbose mode for debugging, true by default
     */
    public Lexer(String program, int programNo, boolean verbose) {
        // initialize flags and local variables
        super(verbose);

        int line = 1;   // line number of program text
        int pos = 0;    // char position in line

        warningCount = 0;
        errorCount = 0;

        tokenStream = new ArrayList<Token>();

        this.log("INFO", "Lexing program " + Integer.toString(programNo) + "...");

        // breaks program up into list of lines (delimiter = "\n")
        ArrayList<String> programLines = convertToLineList(program);

        // for each line in the list of program lines
        for(String progLine : programLines) {
            // break into list of characters to lex each one individually
            char[] charList = progLine.toCharArray();
            // send to helper function to tokenize entire line of chars and add result to token stream
            tokenStream.addAll(tokenize(charList, line));
            // increment line number at the end of each line
            line++;
        }

        // print success or failure message
        if(this.success()) {
            this.log("INFO", "Lex completed with " + Integer.toString(errorCount) + " errors and " + Integer.toString(warningCount) + "warnings\n");
        } else {
            this.log("ERROR", "Lex failed with " + Integer.toString(errorCount) + " errors and " + Integer.toString(warningCount) + "warnings\n");
        }
    }

    /**
     * Further breaks program string into an ArrayList of lines
     * @param program String containing whole program
     * @return ArrayList of Strings containing each line of the program
     */
    private ArrayList<String> convertToLineList(String program) {
        ArrayList<String> lines = new ArrayList<String>();

        if(program.contains("\n")) {
            while(program.contains("\n")) {
                lines.add(program.substring(0, program.indexOf("\n")+1));
                program = program.substring(program.indexOf("\n")+1);
            }
        }

        return lines;
    }

    /**
     * checks if given character is a valid letter
     * @param c current character
     * @return true if valid letter
     */
    private boolean isValid(String c) {
        return "abcdefghijklmnopqrstuvwxyz".contains(c);
    }

    /**
     * checks if current character is whitespace
     * @param c current character
     * @return true if whitespace
     */
    private boolean isWhiteSpace(String c) {
        return c.equals("\t") || 
                c.equals(" ") || 
                c.equals("\r") || 
                c.equals("\n");
    }

    /**
     * checks if current character is part of a comment
     * @param c current character
     * @return true if comment character
     */
    private boolean isComment(String c) {
        return (c.equals("/") || 
                c.equals("*"));
    }

    /**
     * checks if current token is a keyword
     * @param c current token
     * @return true if recognized keyword
     */
    private boolean isKeyword(String c) {
        return (c.equals("print") || 
                c.equals("while") || 
                c.equals("if") || 
                c.equals("int") || 
                c.equals("boolean") || 
                c.equals("string") || 
                c.equals("true") || 
                c.equals("false"));
    }

    /**
     * iterates through comment until end of comment is reached
     * @param c current character
     * @return position on comment end to continue lexing
     */
    private int processComment(String c) {
        int pos = 0;

        return pos;
    }

    /**
     * turns entire list of characters into ArrayList of Tokens to add to the token stream
     * @param charList list of characters in the current line
     * @param line line number for debug output
     * @return ArrayList of Tokens
     */
    private ArrayList<Token> tokenize(char[] charList, int line) {
        ArrayList<Token> lineTokens = new ArrayList<Token>();

        /*
        if(isComment(current)) {
            // adjust position
        } else if (isWhiteSpace(current)) {
            continue;
        } else if ( true /*isValid(current)) {
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
        pos++; */

        return lineTokens;
    }

    /**
     * determines if lexer completed without errors
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
        super.log(alert, "Lexer", msg);
    }
}