/**
 * Token examples:
 *      <id, x> <assign> <id, y> <add> <id, y>
 */
public class Token extends Component {

    private Kind kindOfToken;
    private String value;

    /**
     * Token constructor
     * @param k Kind of Token
     * @param v value of Token
     */
    public Token(Kind k, String v, boolean verbose) {
        super(verbose);
        this.kindOfToken = k;
        this.value = v;
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
        return this.kindOfToken.toString() + " [ " + this.value + " ]";
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
    TYPE_BOOLEAN,
    TYPE_STRING,
    TYPE_INT,
    WHILE,
    IF,
    PRINT,
    TRUE,
    FALSE,

    // symbols
    DIGIT,
    CHAR,
    QUOTE,
    ID,
    EOP,

    ERROR
}