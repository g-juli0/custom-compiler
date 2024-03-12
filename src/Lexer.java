import java.util.ArrayList;

/**
 * Lexer class for compiler
 * 
 * warnings         - unclosed quote warning
 * (to implement)   - unclosed comment warning
 * digits should be invlaid within quotes (charList)
 * 
 * errors           - multiline quote handling
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

        warningCount = 0;
        errorCount = 0;

        tokenStream = new ArrayList<Token>();

        int line = 1;   // line number of program text

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

        // debug output - prints each Token kind and value 
        //System.out.println(tokenStream.toString());

        // check for last EOP symbol
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

        // while a newline is detected, break each program line into substrings and add to ArrayList
        if(program.contains("\n")) {
            while(program.contains("\n")) {
                lines.add(program.substring(0, program.indexOf("\n")+1));
                program = program.substring(program.indexOf("\n")+1);
            }
        }
        return lines;
    }

    /**
     * turns entire list of characters into ArrayList of Tokens to add to the token stream
     * @param charList list of characters in the current line
     * @param line line number for debug output
     * @return ArrayList of Tokens
     */
    private ArrayList<Token> tokenize(char[] charList, int line) {
        ArrayList<Token> lineTokens = new ArrayList<Token>();

        for(int i = 0; i < charList.length; i++) {
            // convert current character to string for easier token checking
            String tokenBuilder = Character.toString(charList[i]);
            String lookahead;
            
            // LETTER DETECTION
            if(isLetter(tokenBuilder)) {
                lookahead = Character.toString(charList[i+1]);

                // ID DETECTION
                if(!isLetter(lookahead)) {
                    lineTokens.add(new Token(Kind.ID, tokenBuilder, debug));
                    this.log("DEBUG", "ID [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                
                // KEYWORD DETECTION
                } else {
                    String temp = lookahead;
                    // adjust current position
                    i++;
                    while(isLetter(temp)) {
                        tokenBuilder += temp;
                        i++; // increment position
                        temp = Character.toString(charList[i]);

                        // once a keyword is detected, add the token and break out of the loop
                        if(isKeyword(tokenBuilder)) {
                            lineTokens.add(new Token(getKeyword(temp), temp, debug));
                            break;
                        }
                    }
                    this.log("DEBUG", getKeyword(tokenBuilder).toString() + " [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                    i--; // back up one position to read the close quote properly
                }

            // SYMBOL DETECTION
            } else if(isSymbol(tokenBuilder)) {
                if(tokenBuilder.equals("+")) {          // +
                    lineTokens.add(new Token(Kind.ADD_OP, tokenBuilder, debug));
                    this.log("DEBUG", "ADD_OP [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                } else if(tokenBuilder.equals("{")) {   // {
                    lineTokens.add(new Token(Kind.OPEN_BLOCK, tokenBuilder, debug));
                    this.log("DEBUG", "OPEN_BLOCK [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                } else if(tokenBuilder.equals("}")) {   // }
                    lineTokens.add(new Token(Kind.CLOSE_BLOCK, tokenBuilder, debug));
                    this.log("DEBUG", "CLOSE_BLOCK [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                } else if(tokenBuilder.equals("(")) {   // (
                    lineTokens.add(new Token(Kind.OPEN_PAREN, tokenBuilder, debug));
                    this.log("DEBUG", "OPEN_PAREN [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                } else if(tokenBuilder.equals(")")) {   // )
                    lineTokens.add(new Token(Kind.CLOSE_PAREN, tokenBuilder, debug));
                    this.log("DEBUG", "CLOSE_PAREN [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                } else if(tokenBuilder.equals("$")) {   // $
                    lineTokens.add(new Token(Kind.EOP, tokenBuilder, debug));
                    this.log("DEBUG", "EOP [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                } else if (tokenBuilder.equals("=")) {
                    lookahead = Character.toString(charList[i+1]);
                    if(lookahead.equals("=")) {         // ==
                        i++; // increment position
                        tokenBuilder += lookahead;
                        lineTokens.add(new Token(Kind.EQUALITY_OP, tokenBuilder, debug));
                        this.log("DEBUG", "EQUALITY_OP [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                    } else {                                    // =
                        lineTokens.add(new Token(Kind.ASSIGN_OP, tokenBuilder, debug));
                        this.log("DEBUG", "ASSIGN_OP [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                    }
                } else if (tokenBuilder.equals("!")) {  // !
                    lookahead = Character.toString(charList[i+1]);
                    if(lookahead.equals("=")) {         // !=
                        i++; // increment position
                        tokenBuilder += lookahead;
                        lineTokens.add(new Token(Kind.INEQUALITY_OP, tokenBuilder, debug));
                        this.log("DEBUG", "INEQUALITY_OP [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                    } else {
                        errorCount++;
                        this.log("ERROR", "Unrecognized token [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                    }
                }

            // DIGIT DETECTION
            } else if(isDigit(tokenBuilder)) {
                lineTokens.add(new Token(Kind.DIGIT, tokenBuilder, debug));
                this.log("DEBUG", "DIGIT [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");

            // QUOTE DETECTION
            } else if(tokenBuilder.equals("\"")) {
                // add and log open quote
                lineTokens.add(new Token(Kind.QUOTE, tokenBuilder, debug));
                this.log("DEBUG", "QUOTE [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                
                // increment to first expected char and set temp string to keep track of quote characters
                i++;
                String temp = Character.toString(charList[i]);

                while(!temp.equals("\"")) {
                    // add and log chars within quote
                    lineTokens.add(new Token(Kind.CHAR, temp, debug));
                    this.log("DEBUG", "CHAR [ " + temp + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
                    i++; // increment position
                    temp = Character.toString(charList[i]);
                }
                // add and log close quote
                lineTokens.add(new Token(Kind.QUOTE, tokenBuilder, debug));
                this.log("DEBUG", "QUOTE [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
            
            // COMMENT DETECTION
            } else if (tokenBuilder.equals("/")) {
                lookahead = Character.toString(charList[i+1]);

                if(lookahead.equals("*")) {
                    i = i+2; // adjust position to first symbol after comment open
                    String current = Character.toString(charList[i+1]);
                    
                    while(!current.equals("*")) {
                        current = Character.toString(charList[i+1]);
                        i++; // increment position

                        if(current.equals("*") && Character.toString(charList[i+1]).equals("/")) {
                            i = i+2; // adjust position to first symbol after comment close
                            break;
                        }
                    }
                }

            // WHITESPACE DETECTION
            } else if(isWhiteSpace(tokenBuilder)) {
                // do nothing on whitespace detection
                continue;
            
            // ERROR DETECTION
            } else {
                errorCount++;
                this.log("ERROR", "Unrecognized token [ " + tokenBuilder + " ] detected at (" + Integer.toString(line) + ":" + Integer.toString(i) + ")");
            }
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
    public void log(String alert, String msg) {
        super.log(alert, "Lexer", msg);
    }
}