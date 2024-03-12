import java.util.ArrayList;

public class Parser extends Component {
    
    private int warningCount;   // number of detected warnings
    private int errorCount;     // number of detected errors

    private ArrayList<Token> tokenStream;
    private SyntaxTree CST;

    public Parser(ArrayList<Token> stream, int programNo, boolean verbose) {
        super(verbose);
        this.tokenStream = stream;

        warningCount = 0;
        errorCount = 0;

        this.log("INFO", "Parsing program " + Integer.toString(programNo) + "...");

        parse();

        if(this.success()) {
            this.printCST();
            this.log("INFO", "Parse completed with " + errorCount + " error(s) and " + warningCount + " warning(s)\n");
        } else {
            this.log("ERROR", "Parse failed with " + errorCount + " error(s) and " + warningCount + " warning(s)\n");
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
            this.log("ERROR", "Expected [ " + expectedValue + " ], found end of stream.");
            errorCount++;
        } else {
            Token currentToken = this.peek();
            // if current Token value equals expected value
            if(currentToken.getValue().equals(expectedValue)) {
                // remove Token and continue
                this.pop();
            } else {
                this.log("ERROR", "Expected [ " + expectedValue + " ], found " + currentToken.getValue());
                errorCount++;
            }
        }
    }

    /**
     * Program ::== Block $
     */
    private void parse() {
        // log debug message
        this.log("DEBUG", "parse()");

        // create new Node and add it to tree
        Node root = new Node("<Program>");
        this.CST = new SyntaxTree(root);

        // parse Block
        parseBlock(root);

        // match EOP symbol and add child Node
        match("$");
        root.addChild(new Node("[$]", root));
    }

    /**
     * Block ::== { StatementList }
     */
    private void parseBlock(Node root) {
        // log debug message
        this.log("DEBUG", "parseBlock()");

        // create new Node and add it to tree
        Node blockNode = new Node("<Block>");

        blockNode.addChild(new Node("{", blockNode));
        match("{");
        parseStatementList(blockNode);
        match("}");
    }

    /**
     * StatementList ::== Statement StatementList
     *               ::== epsilon (empty) production
     */
    private void parseStatementList(Node root) {
        Kind expectedKind = this.peek().getKind();

        // if the expected Kind of Token is a statement
        if(expectedKind == Kind.PRINT || 
                expectedKind == Kind.ID || 
                expectedKind == Kind.TYPE_INT || 
                expectedKind == Kind.TYPE_STRING || 
                expectedKind == Kind.TYPE_BOOLEAN || 
                expectedKind == Kind.WHILE || 
                expectedKind == Kind.IF) {
            parseStatement();
            parseStatementList();
        } else {
            // do nothing, epsilon (empty) production
        }
    }

    /**
     * Statement ::== PrintStatement
     *           ::== AssignStatement
     *           ::== VarDecl
     *           ::== WhileStatement
     *           ::== IfStatement
     *           ::== Block
     */
    private void parseStatement(Node root) {
        Kind expectedKind = this.peek().getKind();
        String expectedValue = this.peek().getValue();
        
        if(expectedKind == Kind.PRINT) {
            parsePrintStatement();
        } else if (expectedKind == Kind.ASSIGN_OP) {
            parseAssignmentStatement();
        } else if (expectedKind == Kind.TYPE_INT || 
                expectedKind == Kind.TYPE_STRING ||
                expectedKind == Kind.TYPE_BOOLEAN) {
            parseVarDecl();
        } else if (expectedKind == Kind.WHILE) {
            parseWhileStatement();
        } else if (expectedKind == Kind.IF) {
            parseIfStatement();
        } else if (expectedValue.equals("{")) { // new block
            parseBlock();
        } else {
            this.log("ERROR", "Expected ___ , found" + expectedKind + " with value [ " + expectedValue + " ]");
        }
    }

    /**
     * PrintStatement ::== print ( Expr )
     */
    private void parsePrintStatement(Node root) {
        match("print");
        match("(");
        parseExpr();
        match(")");
    }

    /**
     * AssignmentStatement ::== Id = Expr
     */
    private void parseAssignmentStatement(Node root) {
        parseId();
        match("=");
        parseExpr();
    }

    /**
     * VarDecl ::== type Id
     */
    private void parseVarDecl(Node root) {
        parseType();
        parseId();
    }

    /**
     * WhileStatement ::== while BooleanExpr Block
     */
    private void parseWhileStatement(Node root) {
        match("while");
        parseBooleanExpr();
        parseBlock();
    }

    /**
     * IfStatement ::== if BooleanExpr Block
     */
    private void parseIfStatement(Node root) {
        match("if");
        parseBooleanExpr();
        parseBlock();
    }

    /**
     * Expr ::== IntExpr
     *      ::== StringExpr
     *      ::== BooleanExpr
     *      ::== Id
     */
    private void parseExpr(Node root) {
        /*
        if(int) {
            parseIntExpr();
        } else if (string) {
            parseStringExpr();
        } else if (boolean) {
            parseBooleanExpr();
        } else if (id) {
            parseId();
        } else {
            // error: expected [^^^], found ____
        }
         */
    }

    /**
     * IntExpr ::== digit intop Expr
     *         ::== digit
     */
    private void parseIntExpr(Node root) {
        /*
        parseDigit();
        if(next == intop) {
            parseIntOp();
            parseExpr();
        }
        */
    }

    /**
     * StringExpr ::== " CharList "
     */
    private void parseStringExpr(Node root) {
        match("\"");
        parseCharList();
        match("\"");
    }

    /**
     * BooleanExpr ::== ( Expr boolop Expr )
     *             ::== boolval
     */
    private void parseBooleanExpr(Node root) {
        /*
        if(true or false) {
            parseBoolVal();
        } else {
            match("(");
            parseExpr();
            parseBoolOp();
            parseExpr();
            match(")");
        }
        */
    }

    /**
     * Id ::== char
     */
    private void parseId(Node root) {
        parseChar();
    }

    /**
     * CharList ::== char CharList
     *          ::== space CharList
     */
    private void parseCharList(Node root) {
        /*
        if(char) {
            parseChar();
        } else if (space) {
            parseSpace();
        } else {
            // do nothing, epsilon (empty) production
        }
         */
    }

    /**
     * type ::== int | string | boolean
     */
    private void parseType(Node root) {
        /*
        if(int) {
            match("int");
        } else if (string) {
            match("string")
        } else if (boolean) {
            match("boolean");
        }
         */
    }
    
    /**
     * char ::== a | b | c | ... | z
     */
    private void parseChar(Node root) {
        /*
        switch(c) {
            case "a": match("a"); break;
            case "b": match("b"); break;
            // ...
            // default:  log error: expected [a-z], found ____
        }
         */
    }

    /**
     * space ::== ' '
     *            (space character)
     */
    private void parseSpace(Node root) {
        match(" ");
    }

    /**
     * digit ::== 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
     */
    private void parseDigit(Node root) {
        /*
        switch(d) {
            case "0": match("0"); break;
            case "1": match("1"); break;
            // ...
            // default:  log error: expected [0-9], found ____
        }
         */
    }

    /**
     * boolop ::== == | !=
     */
    private void parseBoolOp(Node root) {
        /*
        if(==) {
            match("==");
        } else if (!=) {
            match("!=")
        } else {
            error: expected [boolop: == or !=], found ____
        }
         */
    }

    /**
     * boolval ::== false | true
     */
    private void parseBoolVal(Node root) {
        /*
        if(true) {
            match("true");
        } else if (false) {
            match("false");
        } else {
            // error: expected [boolval: true or false], found ____
        }
         */
    }

    /**
     * intop ::== +
     */
    private void parseIntOp(Node root) {
        match("+");
    }

    /**
     * prints formatted concrete syntax tree
     */
    private void printCST() {
        // print concrete syntax tree
        System.out.println(this.CST.depthFirstTraversal(this.CST.root));
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
