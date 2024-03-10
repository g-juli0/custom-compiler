/**
 * Parent class for all compiler components
 * contains master debug switch, log, and type check methods
 */
public class Component {
    
    public boolean debug;

    /**
     * constructor
     * @param verbose debug flag
     */
    public Component(boolean verbose) {
        // verbose mode on by default
        this.debug = verbose;
    }

        /**
     * checks if given character is a letter
     * @param v current token value
     * @return true if letter
     */
    public boolean isLetter(String v) {
        return "abcdefghijklmnopqrstuvwxyz".contains(v);
    }

    /**
     * checks if given string is a digit
     * @param v current token value
     * @return true if digit
     */
    public boolean isDigit(String v) {
        return "0123456789".contains(v);
    }

    /**
     * checks if given string is a valid symbol
     * @param v current token value
     * @return true if valid symbol
     */
    public boolean isSymbol(String v) {
        return "{}()+!==$".contains(v);
    }

    /**
     * checks if current character is whitespace
     * @param v current token value
     * @return true if whitespace
     */
    public boolean isWhiteSpace(String v) {
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
    public boolean isKeyword(String v) {
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
     * returns Kind of keyword given string of possible keyword
     * @param v current token value
     * @return Kind of keyword
     */
    public Kind getKeyword(String v) {
        switch (v) {
            case "print": return Kind.PRINT;
            case "while": return Kind.WHILE;
            case "if": return Kind.IF;
            case "int": return Kind.TYPE_INT;
            case "boolean": return Kind.TYPE_BOOLEAN;
            case "string": return Kind.TYPE_STRING;
            case "true": return Kind.TRUE;
            case "false": return Kind.FALSE;
            default: return Kind.ERROR;
        }
    }

    /**
     * standard logging message for each component of the compiler
     * @param alert info, debug, warning error
     * @param step - lex, parse, semantic analysis, code gen
     * @param msg - message to log
     */
    public void log(String alert, String step, String msg) {
        if(debug) {
            System.out.println(alert + " - " + step + " - " + msg);
        }
    }
}
