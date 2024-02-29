/**
 * Token examples:
 *      <id, x> <assign> <id, y> <add> <id, y>
 */
public class Token extends Component {

    private Kind kindOfToken;
    private String value;
    private int line;
    private int pos;

    /**
     * Token constructor
     * @param k Kind of Token
     * @param v value of Token
     */
    public Token(String k, String v, int l, int p, boolean verbose) {
        super(verbose);
        this.kindOfToken = this.setKind(k);
        this.value = v;
        this.line = l;
        this.pos = p;
    }

    private Kind setKind(String k) {
        switch(k) {
            // block types
            case "{": return Kind.OPEN_BLOCK;
            case "}": return Kind.CLOSE_BLOCK;
            case "(": return Kind.OPEN_PAREN;
            case ")": return Kind.CLOSE_PAREN;

            // operators
            case "=": return Kind.ASSIGN_OP;
            case "!=": return Kind.INEQUALITY_OP;
            case "==": return Kind.EQUALITY_OP;
            case "+": return Kind.ADD_OP;

            // keywords
            case "int": return Kind.INT;
            case "string": return Kind.STRING;
            case "boolean": return Kind.BOOLEAN;
            case "while": return Kind.WHILE;
            case "if": return Kind.IF;
            case "print": return Kind.PRINT;
        
            // symbols
            //DIGIT,
            //CHAR,
            case "\"": return Kind.QUOTE;
            //ID,
            case "$": return Kind.EOP;

            // default - unrecognized
            default: return Kind.ERROR;
        }
    }

    /**
     * getter for kind of Token
     * @return Kind of Token
     */
    public Kind getKind() {
        return this.kindOfToken;
    }

    /**
     * getter for value of Token
     * @return value of token
     */
    public String getValue() {
        return this.value;
    }
    
    /**
     * @Override
     * Token toString method
     * @return Kind of Token converted to a String
     */
    public String toString() {
        return "DEBUG - Lexer - " + this.kindOfToken.toString() + " [ " + this.value + " ] found at (" + this.line + ":" + this.pos + ")";
    }
}

enum Kind {
    // block types
    OPEN_BLOCK,
    CLOSE_BLOCK,
    OPEN_PAREN,
    CLOSE_PAREN,

    // operators
    ASSIGN_OP,
    INEQUALITY_OP,
    EQUALITY_OP,
    ADD_OP,

    // keywords
    INT,
    STRING,
    BOOLEAN,
    WHILE,
    IF,
    PRINT,

    // symbols
    DIGIT,
    CHAR,
    QUOTE,
    ID,
    EOP,

    // error
    ERROR
}