import java.util.ArrayList;

/**
 * Parser Component of Compiler
 */
public class Parser extends Component {
    
    private int warningCount;   // number of detected warnings
    private int errorCount;     // number of detected errors

    private ArrayList<Token> tokenStream;
    private SyntaxTree CST;

    /**
     * constructor for parse component. begins recursive descent parse on constructor call
     * @param stream ArrayList of Tokens recognized by the lexer
     * @param programNo program number for debug printing
     */
    public Parser(ArrayList<Token> stream, int programNo) {
        // initialize flags and variables
        tokenStream = stream;

        warningCount = 0;
        errorCount = 0;

        log("INFO", "Parsing program " + Integer.toString(programNo) + "...");

        // entry point for recursive descent parsing
        parse();

        // print success or failure message, as well as CST on success
        if(success()) {
            log("INFO", "Parse completed with " + errorCount + " error(s) and " + warningCount + " warning(s)\n");
            printCST(programNo);
        } else {
            log("ERROR", "Parse failed with " + errorCount + " error(s) and " + warningCount + " warning(s)\n");
        }
    }

    /**
     * removes and returns next Token off of Token stream
     * @return next Token
     */
    private Token pop() {
        return tokenStream.remove(0);
    }

    /**
     * peeks at next Token in the Token stream
     * @return next Token
     */
    private Token peek() {
        return tokenStream.get(0);
    }

    /**
     * matches expected value to next expected Token in stream
     * @param expectedValue next expected value
     */
    private void match(String expectedValue) {
        // if end of stream reached, error
        if(tokenStream.isEmpty()) {
            log("ERROR", "Expected [ " + expectedValue + " ], found end of stream.");
            errorCount++;
        } else {
            Token currentToken = peek();
            // if current Token value equals expected value
            if(currentToken.getValue().equals(expectedValue)) {
                // remove Token and continue
                pop();
                // VALID EXPECTED MSG
            } else {
                log("ERROR", "Expected [ " + expectedValue + " ], found [ " + currentToken.getValue() + " ] at (" + currentToken.getLine() + ":" + currentToken.getPos() + ")");
                errorCount++;
            }
        }
    }

    /**
     * entry point for recursive descent parsing
     */
    private void parse() {
        log("DEBUG", "parse()");
        parseProgram();
    }

    /**
     * Program ::== Block $
     */
    private void parseProgram() {
        // log debug message
        log("DEBUG", "parseProgram()");

        // create new Node and add it to tree
        Node root = new Node("Program");
        CST = new SyntaxTree(root);

        // parse Block
        parseBlock(root);

        // match EOP symbol and add child Node
        match("$");
        root.addChild(new Node("$", root));
    }

    /**
     * Block ::== { StatementList }
     */
    private void parseBlock(Node parent) {
        // log debug message
        log("DEBUG", "parseBlock()");

        // create new Node and add it to tree
        Node blockNode = new Node("Block", parent);
        parent.addChild(blockNode);

        // match { and add child to Block Node
        match("{");
        blockNode.addChild(new Node("{", blockNode));

        // parse StatementList
        parseStatementList(blockNode);

        // match } and add child to Block Node
        match("}");
        blockNode.addChild(new Node("}", blockNode));
    }

    /**
     * StatementList ::== Statement StatementList
     *               ::== epsilon (empty) production
     */
    private void parseStatementList(Node parent) {
        // log debug message
        log("DEBUG", "parseStatementList()");

        // create new Node and add it to tree
        Node statementListNode = new Node("StatementList", parent);
        parent.addChild(statementListNode);

        // peek at current Token for Kind checking
        Kind currentKind = peek().getKind();

        // if the expected Kind of Token is in the first set for a Statement
        if(currentKind == Kind.PRINT || 
                currentKind == Kind.ID || 
                currentKind == Kind.TYPE_INT || 
                currentKind == Kind.TYPE_STRING || 
                currentKind == Kind.TYPE_BOOLEAN || 
                currentKind == Kind.WHILE || 
                currentKind == Kind.IF ||
                currentKind == Kind.OPEN_BLOCK) {
            parseStatement(statementListNode);
            parseStatementList(statementListNode);
        } else {
            // do nothing, epsilon (empty) production
        }
    }

    /**
     * Statement ::== PrintStatement
     *           ::== AssignmentStatement
     *           ::== VarDecl
     *           ::== WhileStatement
     *           ::== IfStatement
     *           ::== Block
     */
    private void parseStatement(Node parent) {
        // log debug message
        log("DEBUG", "parseStatement()");

        // create new Node and add it to tree
        Node statementNode = new Node("Statement", parent);
        parent.addChild(statementNode);

        // peek at current Token for Kind checking
        Token currentToken = peek();
        Kind currentKind = currentToken.getKind();
        
        // PrintStatement
        if(currentKind == Kind.PRINT) {
            parsePrintStatement(statementNode);
        // AssignmentStatement
        } else if (currentKind == Kind.ID) {
            parseAssignmentStatement(statementNode);
        // VarDecl
        } else if (currentKind == Kind.TYPE_INT || 
                currentKind == Kind.TYPE_STRING ||
                currentKind == Kind.TYPE_BOOLEAN) {
            parseVarDecl(parent);
        // WhileStatement
        } else if (currentKind == Kind.WHILE) {
            parseWhileStatement(statementNode);
        // IfStatement
        } else if (currentKind == Kind.IF) {
            parseIfStatement(statementNode);
        // Block
        } else if (currentKind == Kind.OPEN_BLOCK) {
            parseBlock(statementNode);
        // error - unexpected token
        } else {
            log("ERROR", "Expected Statement [PRINT, ID, TYPE_INT, TYPE_STRING, TYPE_BOOLEAN, WHILE, IF, OPEN_BLOCK] , found " + currentKind + " with value [ " + currentToken.getValue() + " ] at (" + currentToken.getLine() + ":" + currentToken.getPos() + ")");
        }
    }

    /**
     * PrintStatement ::== print ( Expr )
     */
    private void parsePrintStatement(Node parent) {
        // log debug message
        log("DEBUG", "parsePrintStatement()");

        // create new Node and add it to tree
        Node printStatementNode = new Node("PrintStatement", parent);
        parent.addChild(printStatementNode);

        match("print");
        printStatementNode.addChild(new Node("print", printStatementNode));
        
        match("(");
        printStatementNode.addChild(new Node("(", printStatementNode));

        parseExpr(printStatementNode);

        match(")");
        printStatementNode.addChild(new Node(")", printStatementNode));
    }

    /**
     * AssignmentStatement ::== Id = Expr
     */
    private void parseAssignmentStatement(Node parent) {
        // log debug message
        log("DEBUG", "parseAssignmentStatement()");

        // create new Node and add it to tree
        Node assignStatementNode = new Node("AssignmentStatement", parent);
        parent.addChild(assignStatementNode);

        parseId(assignStatementNode);

        match("=");
        assignStatementNode.addChild(new Node("=", assignStatementNode));

        parseExpr(assignStatementNode);
    }

    /**
     * VarDecl ::== type Id
     */
    private void parseVarDecl(Node parent) {
        // log debug message
        log("DEBUG", "parseVarDecl()");

        // create new Node and add it to tree
        Node varDeclNode = new Node("VarDecl", parent);
        parent.addChild(varDeclNode);
        
        parseType(varDeclNode);
        parseId(varDeclNode);
    }

    /**
     * WhileStatement ::== while BooleanExpr Block
     */
    private void parseWhileStatement(Node parent) {
        // log debug message
        log("DEBUG", "parseWhileStatement()");

        // create new Node and add it to tree
        Node whileStatementNode = new Node("WhileStatement", parent);
        parent.addChild(whileStatementNode);

        match("while");
        whileStatementNode.addChild(new Node("while", whileStatementNode));

        parseBooleanExpr(whileStatementNode);
        parseBlock(whileStatementNode);
    }

    /**
     * IfStatement ::== if BooleanExpr Block
     */
    private void parseIfStatement(Node parent) {
        // log debug message
        log("DEBUG", "parseIfStatement()");

        // create new Node and add it to tree
        Node ifStatementNode = new Node("IfStatement", parent);
        parent.addChild(ifStatementNode);

        match("if");
        ifStatementNode.addChild(new Node("if", ifStatementNode));

        parseBooleanExpr(ifStatementNode);
        parseBlock(ifStatementNode);
    }

    /**
     * Expr ::== IntExpr
     *      ::== StringExpr
     *      ::== BooleanExpr
     *      ::== Id
     */
    private void parseExpr(Node parent) {
        // log debug message
        log("DEBUG", "parseExpr()");

        // create new Node and add it to tree
        Node exprNode = new Node("Expr", parent);
        parent.addChild(exprNode);

        // peek at current Token for Kind checking
        Token currentToken = peek();
        Kind currentKind = currentToken.getKind();

        if(currentKind == Kind.DIGIT) {
            parseIntExpr(exprNode);
        } else if (currentKind == Kind.QUOTE) {
            parseStringExpr(exprNode);
        } else if (currentKind == Kind.OPEN_PAREN ||
                    currentKind == Kind.FALSE ||
                    currentKind == Kind.TRUE) {
            parseBooleanExpr(exprNode);
        } else if (currentKind == Kind.ID) {
            parseId(exprNode);
        } else {
            log("ERROR", "Expected Expr [TYPE_INT, TYPE_STRING, TYPE_BOOLEAN, FALSE, TRUE, ID] , found " + currentKind + " with value [ " + currentToken.getValue() + " ] at (" + currentToken.getLine() + ":" + currentToken.getPos() + ")");
        }
    }

    /**
     * IntExpr ::== digit intop Expr
     *         ::== digit
     */
    private void parseIntExpr(Node parent) {
        // log debug message
        log("DEBUG", "parseIntExpr()");

        // create new Node and add it to tree
        Node intExprNode = new Node("IntExpr", parent);
        parent.addChild(intExprNode);
        
        // parse first digit, then get current Token after digit is matched
        parseDigit(intExprNode);

        // peek at current Token for Kind checking
        Kind currentKind = peek().getKind();

        if(currentKind == Kind.ADD_OP) {
            parseIntOp(intExprNode);
            parseExpr(intExprNode);
        }
    }

    /**
     * StringExpr ::== " CharList "
     */
    private void parseStringExpr(Node parent) {
        // log debug message
        log("DEBUG", "parseStringExpr()");

        // create new Node and add it to tree
        Node stringExprNode = new Node("StringExpr", parent);
        parent.addChild(stringExprNode);

        match("\"");
        stringExprNode.addChild(new Node("\"", stringExprNode));

        parseCharList(stringExprNode);

        match("\"");
        stringExprNode.addChild(new Node("\"", stringExprNode));
    }

    /**
     * BooleanExpr ::== ( Expr boolop Expr )
     *             ::== boolval
     */
    private void parseBooleanExpr(Node parent) {
        // log debug message
        log("DEBUG", "parseBooleanExpr()");

        // create new Node and add it to tree
        Node booleanExprNode = new Node("BooleanExpr", parent);
        parent.addChild(booleanExprNode);

        // peek at current Token for Kind checking
        Kind currentKind = peek().getKind();

        if(currentKind == Kind.TRUE || currentKind == Kind.FALSE) {
            parseBoolVal(booleanExprNode);
        } else {
            match("(");
            booleanExprNode.addChild(new Node("(", booleanExprNode));

            parseExpr(booleanExprNode);
            parseBoolOp(booleanExprNode);
            parseExpr(booleanExprNode);

            match(")");
            booleanExprNode.addChild(new Node(")", booleanExprNode));
        }
    }

    /**
     * Id ::== char
     */
    private void parseId(Node parent) {
        // log debug message
        log("DEBUG", "parseId()");

        // create new Node and add it to tree
        Node idNode = new Node("Id", parent);
        parent.addChild(idNode);

        parseChar(idNode);
    }

    /**
     * CharList ::== char CharList
     *          ::== space CharList
     *          ::== epsilon (empty) production
     */
    private void parseCharList(Node parent) {
        // log debug message
        log("DEBUG", "parseCharList()");

        // create new Node and add it to tree
        Node charListNode = new Node("CharList", parent);
        parent.addChild(charListNode);

        // peek at current Token for Kind and Value checking
        Kind currentKind = peek().getKind();
        String currentValue = peek().getValue();

        if(currentKind == Kind.CHAR) {
            if(currentValue.equals(" ")) {
                parseSpace(charListNode);
            } else {
                parseChar(charListNode);
            }
            parseCharList(charListNode);
        } else {
            // do nothing, epsilon (empty) production
        }
    }

    /**
     * type ::== int | string | boolean
     */
    private void parseType(Node parent) {
        // log debug message
        log("DEBUG", "parseType()");

        // create new Node and add it to tree
        Node typeNode = new Node("type", parent);
        parent.addChild(typeNode);

        // peek at current Token for Kind checking
        Kind currentKind = peek().getKind();

        if(currentKind == Kind.TYPE_INT) {
            match("int");
            typeNode.addChild(new Node("int", typeNode));
        } else if (currentKind == Kind.TYPE_STRING) {
            match("string");
            typeNode.addChild(new Node("string", typeNode));
        } else if (currentKind == Kind.TYPE_BOOLEAN) {
            match("boolean");
            typeNode.addChild(new Node("boolean", typeNode));
        }
    }
    
    /**
     * char ::== a | b | c | ... | z
     */
    private void parseChar(Node parent) {
        // log debug message
        log("DEBUG", "parseChar()");

        // create new Node and add it to tree
        Node charNode = new Node("char", parent);
        parent.addChild(charNode);

        // peek at current Token for Value checking
        Token currentToken = peek();
        String currentValue = currentToken.getValue();

        String expectedLetter = "";

        switch(currentValue) {
            case "a": expectedLetter = "a"; break;
            case "b": expectedLetter = "b"; break;
            case "c": expectedLetter = "c"; break;
            case "d": expectedLetter = "d"; break;
            case "e": expectedLetter = "e"; break;
            case "f": expectedLetter = "f"; break;
            case "g": expectedLetter = "g"; break;
            case "h": expectedLetter = "h"; break;
            case "i": expectedLetter = "i"; break;
            case "j": expectedLetter = "j"; break;
            case "k": expectedLetter = "k"; break;
            case "l": expectedLetter = "l"; break;
            case "m": expectedLetter = "m"; break;
            case "n": expectedLetter = "n"; break;
            case "o": expectedLetter = "o"; break;
            case "p": expectedLetter = "p"; break;
            case "q": expectedLetter = "q"; break;
            case "r": expectedLetter = "r"; break;
            case "s": expectedLetter = "s"; break;
            case "t": expectedLetter = "t"; break;
            case "u": expectedLetter = "u"; break;
            case "v": expectedLetter = "v"; break;
            case "w": expectedLetter = "w"; break;
            case "x": expectedLetter = "x"; break;
            case "y": expectedLetter = "y"; break;
            case "z": expectedLetter = "z"; break;
            default: log("ERROR", "Expected char [a-z] , found [ " + currentValue + " ] at (" + currentToken.getLine() + ":" + currentToken.getPos() + ")"); break;
        }

        match(expectedLetter);
        charNode.addChild(new Node(expectedLetter, charNode));
    }

    /**
     * space ::== ' ' (space character)
     */
    private void parseSpace(Node parent) {
        // log debug message
        log("DEBUG", "parseSpace()");

        // create new Node and add it to tree
        Node spaceNode = new Node("space", parent);
        parent.addChild(spaceNode);

        match(" ");
        spaceNode.addChild(new Node(" ", spaceNode));
    }

    /**
     * digit ::== 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
     */
    private void parseDigit(Node parent) {
        // log debug message
        log("DEBUG", "parseDigit()");

        // create new Node and add it to tree
        Node digitNode = new Node("digit", parent);
        parent.addChild(digitNode);

        // peek at current Token for Value checking
        Token currentToken = peek();
        String currentValue = currentToken.getValue();

        String expectedDigit = "";

        switch(currentValue) {
            case "0": expectedDigit = "0"; break;
            case "1": expectedDigit = "1"; break;
            case "2": expectedDigit = "2"; break;
            case "3": expectedDigit = "3"; break;
            case "4": expectedDigit = "4"; break;
            case "5": expectedDigit = "5"; break;
            case "6": expectedDigit = "6"; break;
            case "7": expectedDigit = "7"; break;
            case "8": expectedDigit = "8"; break;
            case "9": expectedDigit = "9"; break;
            default: log("ERROR", "Expected digit [0-9] , found [ " + currentValue + " ] at (" + currentToken.getLine() + ":" + currentToken.getPos() + ")"); break;
        }

        match(expectedDigit);
        digitNode.addChild(new Node(expectedDigit, digitNode));
    }

    /**
     * boolop ::== == | !=
     */
    private void parseBoolOp(Node parent) {
        // log debug message
        log("DEBUG", "parseBoolOp()");

        // create new Node and add it to tree
        Node boolOpNode = new Node("boolop", parent);
        parent.addChild(boolOpNode);

        // peek at current Token for Value checking
        Token currentToken = peek();
        Kind currentKind = currentToken.getKind();

        if(currentKind == Kind.EQUALITY_OP) {
            match("==");
            boolOpNode.addChild(new Node("==", boolOpNode));
        } else if (currentKind == Kind.INEQUALITY_OP) {
            match("!=");
            boolOpNode.addChild(new Node("!=", boolOpNode));
        } else {
            log("ERROR", "Expected boolop [==, !=] , found [ " + currentToken.getValue() + " ] at (" + currentToken.getLine() + ":" + currentToken.getPos() + ")");
        }
    }

    /**
     * boolval ::== false | true
     */
    private void parseBoolVal(Node parent) {
        // log debug message
        log("DEBUG", "parseBoolVal()");

        // create new Node and add it to tree
        Node boolValNode = new Node("boolval", parent);
        parent.addChild(boolValNode);

        // peek at current Token for Value checking
        Token currentToken = peek();
        Kind currentKind = currentToken.getKind();

        if(currentKind == Kind.TRUE) {
            match("true");
            boolValNode.addChild(new Node("true", boolValNode));
        } else if (currentKind == Kind.FALSE) {
            match("false");
            boolValNode.addChild(new Node("false", boolValNode));
        } else {
            log("ERROR", "Expected boolval [true, false] , found [ " + currentKind + " ] at (" + currentToken.getLine() + ":" + currentToken.getPos() + ")");
        }
    }

    /**
     * intop ::== +
     */
    private void parseIntOp(Node parent) {
        // log debug message
        log("DEBUG", "parseIntOp()");

        // create new Node and add it to tree
        Node intOpNode = new Node("intop", parent);
        parent.addChild(intOpNode);

        match("+");
        intOpNode.addChild(new Node("+", intOpNode));
    }

    /**
     * prints formatted concrete syntax tree
     */
    private void printCST(int programNo) {
        // print concrete syntax tree
        System.out.println("Program " + programNo + " Concrete Syntax Tree");
        System.out.println("------------------------------------");
        System.out.println(CST.toString());
    }

    /**
     * determines if parser completed without errors
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
        super.log(alert, "Parser", msg);
    }
}
