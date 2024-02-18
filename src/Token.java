
/**
 * Token examples:
 *      <id, x> <assign> <id, y> <add> <id, y>
 */
public class Token {

    private Kind kindOfToken;
    private String value;

    public Token(Kind k, String v) {
        kindOfToken = k;
        value = v;
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
    SUB_OP,
    MULT_OP,
    DIV_OP,

    // keywords
    ID_TYPE,
    WHILE,
    IF,
    PRINT,

    // symbols
    DIGIT,
    CHAR,
    QUOTE,
    ID,
    EOP,
}
