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
        if(tokenStream.size() < 1) {
            this.log("ERROR", "Expected [ " + expectedValue + " ], found end of stream.");
            errorCount++;
        } else {
            Token currentToken = this.peek();
            // if current Token value equals expected value
            if(currentToken.getValue().equals(expectedValue)) {
                // remove Token and continue
                this.pop();
            } else {
                this.log("ERROR", "Expected [ " + expectedValue + " ], found " + currentToken.getKind().toString());
                errorCount++;
            }
        }
    }

    /**
     * Program ::== Block $
     */
    private void parse() {
        // create new tree and log debug message
        this.CST = new SyntaxTree(new Node("<Program>"));
        this.log("DEBUG", "parse()");

        parseBlock();
        match("$");
    }

    /**
     * Block ::== { StatementList }
     */
    private void parseBlock() {
        match("{");
        parseStatementList();
        match("}");
    }

    /**
     * StatementList ::== Statement StatementList
     *               ::== epsilon (empty) production
     */
    private void parseStatementList() {
        /*
        if(print || assign || vardecl || while || if || {)
        parseStatement();
        parseStatementList();
        else {
            // do nothing, epsilon (empty) production
        }
        */
    }

    /**
     * Statement ::== PrintStatement
     *           ::== AssignStatement
     *           ::== VarDecl
     *           ::== WhileStatement
     *           ::== IfStatement
     *           ::== Block
     */
    private void parseStatement() {
        /* 
        if(print) {
            parsePrintStatement();
        } else if (assign) {
            parseAssignmentStatement();
        } else if (vardecl) {
            parseVarDecl();
        } else if (while) {
            parseWhileStatement();
        } else if (if) {
            parseIfStatement();
        } else if ({) { // new block
            parseBlock();
        } else {
            // error: expected [^^^], found ____
        }
         */
    }

    /**
     * PrintStatement ::== print ( Expr )
     */
    private void parsePrintStatement() {
        match("print");
        match("(");
        parseExpr();
        match(")");
    }

    /**
     * AssignmentStatement ::== Id = Expr
     */
    private void parseAssignmentStatement() {
        parseId();
        match("=");
        parseExpr();
    }

    /**
     * VarDecl ::== type Id
     */
    private void parseVarDecl() {
        parseType();
        parseId();
    }

    /**
     * WhileStatement ::== while BooleanExpr Block
     */
    private void parseWhileStatement() {
        match("while");
        parseBooleanExpr();
        parseBlock();
    }

    /**
     * IfStatement ::== if BooleanExpr Block
     */
    private void parseIfStatement() {
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
    private void parseExpr() {
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
    private void parseIntExpr() {
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
    private void parseStringExpr() {
        match("\"");
        parseCharList();
        match("\"");
    }

    /**
     * BooleanExpr ::== ( Expr boolop Expr )
     *             ::== boolval
     */
    private void parseBooleanExpr() {
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
    private void parseId() {
        parseChar();
    }

    /**
     * CharList ::== char CharList
     *          ::== space CharList
     */
    private void parseCharList() {
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
    private void parseType() {
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
    private void parseChar() {
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
    private void parseSpace() {
        match(" ");
    }

    /**
     * digit ::== 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
     */
    private void parseDigit() {
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
    private void parseBoolOp() {
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
    private void parseBoolVal() {
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
    private void parseIntOp() {
        match("+");
    }

    private void printCST() {
        // print concrete syntax tree
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
    private void log(String alert, String msg) {
        super.log(alert, "Parser", msg);
    }
}
