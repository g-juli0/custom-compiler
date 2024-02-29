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

        // check for eop - warning
        checkEOP();

        // print success or failure message
        if(this.success()) {
            this.log("INFO", "Lex completed with " + errorCount + " error(s) and " + warningCount + " warning(s)\n");
        } else {
            this.log("ERROR", "Lex failed with " + errorCount + " error(s) and " + warningCount + " warning(s)\n");
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
     * checks if given character is a letter
     * @param v current token value
     * @return true if letter
     */
    private boolean isLetter(String v) {
        return "abcdefghijklmnopqrstuvwxyz".contains(v);
    }

    /**
     * checks if given string is a digit
     * @param v current token value
     * @return true if digit
     */
    private boolean isDigit(String v) {
        return "0123456789".contains(v);
    }

    /**
     * checks if given string is a valid symbol
     * @param v current token value
     * @return true if valid symbol
     */
    private boolean isSymbol(String v) {
        return "{}()+!==$".contains(v);
    }

    /**
     * checks if current character is whitespace
     * @param v current token value
     * @return true if whitespace
     */
    private boolean isWhiteSpace(String v) {
        return v.equals("\t") || 
                v.equals(" ") || 
                v.equals("\r") || 
                v.equals("\n");
    }

    /**
     * checks if current token is a keyword
     * @param v current token value
     * @return true if recognized keyword
     */
    private boolean isKeyword(String v) {
        return (v.equals("print") || 
                v.equals("while") || 
                v.equals("if") || 
                v.equals("int") || 
                v.equals("boolean") || 
                v.equals("string") || 
                v.equals("true") || 
                v.equals("false"));
    }

    /**
     * checks if current char and lookahead denote open comment
     * @param v
     * @return
     */
    private boolean isComment(String v) {
        return false;
    }

    /**
     * turns entire list of characters into ArrayList of Tokens to add to the token stream
     * @param charList list of characters in the current line
     * @param line line number for debug output
     * @return ArrayList of Tokens
     */
    private ArrayList<Token> tokenize(char[] charList, int line) {
        ArrayList<Token> lineTokens = new ArrayList<Token>();

        for(int i = 0; i < charList.length-1; i++) {
            // convert current character to string for easier token checking
            String tokenBuilder = Character.toString(charList[i]);
            String lookahead = Character.toString(charList[i+1]);

            if(isDigit(tokenBuilder)) {
                lineTokens.add(new Token(Kind.DIGIT, tokenBuilder, debug));
                this.log("DEBUG", "DIGIT [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
            } else if(isLetter(tokenBuilder)) {
                // loop to check for keywords
                // else, ID - check lookahead (ID if not another letter)
            } else if(isSymbol(tokenBuilder)) {
                if(tokenBuilder.equals("+")) {
                    lineTokens.add(new Token(Kind.ADD_OP, tokenBuilder, debug));
                    this.log("DEBUG", "ADD_OP [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                } else if(tokenBuilder.equals("{")) {
                    lineTokens.add(new Token(Kind.OPEN_BLOCK, tokenBuilder, debug));
                    this.log("DEBUG", "OPEN_BLOCK [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                } else if(tokenBuilder.equals("}")) {
                    lineTokens.add(new Token(Kind.CLOSE_BLOCK, tokenBuilder, debug));
                    this.log("DEBUG", "CLOSE_BLOCK [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                } else if(tokenBuilder.equals("(")) {
                    lineTokens.add(new Token(Kind.OPEN_PAREN, tokenBuilder, debug));
                    this.log("DEBUG", "OPEN_PAREN [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                } else if(tokenBuilder.equals(")")) {
                    lineTokens.add(new Token(Kind.CLOSE_PAREN, tokenBuilder, debug));
                    this.log("DEBUG", "CLOSE_PAREN [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                } else if(tokenBuilder.equals("$")) {
                    lineTokens.add(new Token(Kind.EOP, tokenBuilder, debug));
                    this.log("DEBUG", "EOP [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                }
                // if =, check lookahead
                //      if =, ==
                //      else =
                // if !, check lookahead
                //      if =, !=
                //      else, error
            } else if(tokenBuilder.equals("\"")) {
                // add and log open quote
                lineTokens.add(new Token(Kind.QUOTE, tokenBuilder, debug));
                this.log("DEBUG", "QUOTE [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                
                // set temp string to keep track of quote characters
                String temp = "";
                while(!temp.equals("\"")) {
                    i++;
                    temp = Character.toString(charList[i]);
                    // add and log chars within quote
                    lineTokens.add(new Token(Kind.CHAR, temp, debug));
                    this.log("DEBUG", "CHAR [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                }
                // last increment to pass end quote
                i++;
                // add and log close quote
                lineTokens.add(new Token(Kind.QUOTE, tokenBuilder, debug));
                this.log("DEBUG", "QUOTE [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
            } else if (isComment(tokenBuilder)) {
                // pass until comment close is found
                // adjust position
            } else if(isWhiteSpace(tokenBuilder)) {
                // do nothing on whitespace detection
                continue;
            } else {
                errorCount++;
                this.log("ERROR", "Unrecognized token [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
            }
            //this.log("DEBUG", t.getKind().toString()+ " [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
        }
        return lineTokens;
    }

    /**
     * returns tokenStream for use in parse phase
     * @return ArrayList of Tokenized program
     */
    public ArrayList<Token> getTokenStream() {
        return tokenStream;
    }

    /**
     * outputs warning message if end of program symbol is not the last symbol in the tokenStream
     */
    private void checkEOP() {
        if(!tokenStream.get(tokenStream.size()-1).getKind().equals(Kind.EOP)) {
            this.log("WARNING", "missing EOP symbol [ $ ]");
            warningCount++;
            this.log("INFO", "EOP symbol [ $ ] added to token stream");
            tokenStream.add(new Token(Kind.EOP, "$", debug));
        }
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