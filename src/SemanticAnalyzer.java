import java.util.ArrayList;

/**
 * Semantic Analysis Component of Compiler
 */
public class SemanticAnalyzer extends Component {
    
    private int warningCount;   // number of detected warnings
    private int errorCount;     // number of detected errors

    private ArrayList<Token> tokenStream;
    private SyntaxTree AST;
    private SymbolTable table;

    private int scope;

    /**
     * constructor for semantic analyzer component. pseudo "parses" token stream again,
     *      only adding minimally necessary Tokens to the AST
     * builds a symbol table, scope checks and type checks
     * @param stream ArrayList of Tokens recognized by the lexer
     * @param programNo program number for debug printing
     */
    public SemanticAnalyzer(ArrayList<Token> stream, int programNo) {
        // initialize flags and variables
        tokenStream = stream;

        scope = 0;
        table = new SymbolTable(scope);

        warningCount = 0;
        errorCount = 0;

        log("INFO", "Semantically analyzing program " + Integer.toString(programNo) + "...");

        // entry point for pseudo parse
        block(null);

        if(success()) {
            log("INFO", "Semantic analysis completed with " + errorCount + " error(s) and " + warningCount + " warning(s)\n");
            printAST(programNo);
            //populateSymbolTable(AST.getRoot(), table);
            printSymbolTable(programNo);
        } else {
            log("ERROR", "Semantic analysis failed with " + errorCount + " error(s) and " + warningCount + " warning(s)\n");
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
        // since parse step has already passed successfully, no need for error checking here
        Token currentToken = peek();
        // if current Token value equals expected value
        if(currentToken.getValue().equals(expectedValue)) {
            // remove Token and continue
            pop();
        }
    }

    /**
     * Block ::== { StatementList }
     */
    private void block(Node parent) {
        // log debug message
        log("DEBUG", "Block");

        match("{");

        // if root has not already been created
        if(parent == null) {
            // create new root for AST
            Node root = new Node("Block");
            AST = new SyntaxTree(root);
            statementList(root);
        } else {
            Node blockNode = new Node("Block", parent);
            parent.addChild(blockNode);
            statementList(blockNode);
        }

        match("}");
        match("$");
    }

    /**
     * StatementList ::== Statement StatementList
     *               ::== epsilon (empty) production
     * 
     * no Node added to AST
     */
    private void statementList(Node parent) {
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
            statement(parent);
            statementList(parent);
        }
    }

    /**
     * Statement ::== PrintStatement
     *           ::== AssignStatement
     *           ::== VarDecl
     *           ::== WhileStatement
     *           ::== IfStatement
     *           ::== Block
     * 
     * no Node added to AST
     */
    private void statement(Node parent) {
        // peek at current Token for Kind checking
        Kind currentKind = peek().getKind();
        
        // PrintStatement
        if(currentKind == Kind.PRINT) {
            printStatement(parent);
        // AssignStatement
        } else if(currentKind == Kind.ID) {
            assignmentStatement(parent);
        // VarDecl
        } else if(currentKind == Kind.TYPE_INT || 
                currentKind == Kind.TYPE_STRING ||
                currentKind == Kind.TYPE_BOOLEAN) {
            varDecl(parent);
        // WhileStatement
        } else if(currentKind == Kind.WHILE) {
            whileStatement(parent);
        // IfStatement
        } else if(currentKind == Kind.IF) {
            ifStatement(parent);
        // Block
        } else if(currentKind == Kind.OPEN_BLOCK) {
            block(parent);
        }
    }

    /**
     * PrintStatement ::== print ( Expr )
     */
    private void printStatement(Node parent) {
        // log debug message
        log("DEBUG", "PrintStatement");

        // create new Node and add it to tree
        Node printStatementNode = new Node("PrintStatement", parent);
        parent.addChild(printStatementNode);

        match("print");
        match("(");

        expr(printStatementNode);

        match(")");
    }

    /**
     * AssignmentStatement ::== Id = Expr
     */
    private void assignmentStatement(Node parent) {
        // log debug message
        log("DEBUG", "AssignmentStatement");

        // create new Node and add it to tree
        Node assignStatementNode = new Node("AssignmentStatement", parent);
        parent.addChild(assignStatementNode);

        id(assignStatementNode);

        match("=");

        expr(assignStatementNode);
    }

    /**
     * VarDecl ::== type Id
     */
    private void varDecl(Node parent) {
        // log debug message
        log("DEBUG", "VarDecl");

        // create new Node and add it to tree
        Node varDeclNode = new Node("VarDecl", parent);
        parent.addChild(varDeclNode);
        
        String type = type(varDeclNode);
        String id = id(varDeclNode);

        table.addSymbol(new Symbol(id, type, scope, false, false));
    }

    /**
     * WhileStatement ::== while BooleanExpr Block
     */
    private void whileStatement(Node parent) {
        // log debug message
        log("DEBUG", "WhileStatement");

        // create new Node and add it to tree
        Node whileStatementNode = new Node("WhileStatement", parent);
        parent.addChild(whileStatementNode);

        match("while");

        booleanExpr(whileStatementNode);
        block(whileStatementNode);
    }

    /**
     * IfStatement ::== if BooleanExpr Block
     */
    private void ifStatement(Node parent) {
        // log debug message
        log("DEBUG", "IfStatement");

        // create new Node and add it to tree
        Node ifStatementNode = new Node("IfStatement", parent);
        parent.addChild(ifStatementNode);

        match("if");

        booleanExpr(ifStatementNode);
        block(ifStatementNode);
    }

    /**
     * Expr ::== IntExpr
     *      ::== StringExpr
     *      ::== BooleanExpr
     *      ::== Id
     * 
     * no Node added to AST
     */
    private void expr(Node parent) {
        // peek at current Token for Kind checking
        Kind currentKind = peek().getKind();

        if(currentKind == Kind.DIGIT) {
            intExpr(parent);
        } else if(currentKind == Kind.QUOTE) {
            stringExpr(parent);
        } else if(currentKind == Kind.OPEN_PAREN ||
                    currentKind == Kind.FALSE ||
                    currentKind == Kind.TRUE) {
            booleanExpr(parent);
        } else if(currentKind == Kind.ID) {
            id(parent);
        }
    }

    /**
     * IntExpr ::== digit intop Expr
     *         ::== digit
     */
    private void intExpr(Node parent) {
        // log debug message
        log("DEBUG", "IntExpr");
        
        // parse first digit, then get current Token after digit is matched
        digit(parent);

        // peek at current Token for Kind checking
        Kind currentKind = peek().getKind();

        if(currentKind == Kind.ADD_OP) {
            intOp(parent);
            expr(parent);
        }
    }

    /**
     * StringExpr ::== " CharList "
     */
    private void stringExpr(Node parent) {
        // log debug message
        log("DEBUG", "StringExpr");

        match("\"");

        charList(parent);

        match("\"");
    }

    /**
     * BooleanExpr ::== ( Expr boolop Expr )
     *             ::== boolval
     */
    private void booleanExpr(Node parent) {
        // log debug message
        log("DEBUG", "BooleanExpr");

        // peek at current Token for Kind checking
        Kind currentKind = peek().getKind();

        if(currentKind == Kind.TRUE || currentKind == Kind.FALSE) {
            boolVal(parent);
        } else {
            match("(");

            expr(parent);
            boolOp(parent);
            expr(parent);

            match(")");
        }
    }

    /**
     * Id ::== char
     * 
     * no Node added to AST
     */
    private String id(Node parent) {
        return character(parent);
    }

    /**
     * CharList ::== char CharList
     *          ::== space CharList
     *          ::== epsilon (empty) production
     * 
     * convert CharList to a single string for AST Node
     */
    private void charList(Node parent) {
        StringBuilder strBuilder = new StringBuilder();

        // peek at current Token for Kind and Value checking
        Token current = peek();

        while(current.getKind() == Kind.CHAR) {
            strBuilder.append(current.getValue());

            pop();
            current = peek();
        }

        // create new Node and add it to tree
        Node charListNode = new Node(strBuilder.toString(), parent);
        parent.addChild(charListNode);
    }

    /**
     * type ::== int | string | boolean
     */
    private String type(Node parent) {
        // peek at current Token for Kind checking
        Kind currentKind = peek().getKind();

        if(currentKind == Kind.TYPE_INT) {
            match("int");
            parent.addChild(new Node("int", parent));
            return "int";
        } else if(currentKind == Kind.TYPE_STRING) {
            match("string");
            parent.addChild(new Node("string", parent));
            return "string";
        } else if(currentKind == Kind.TYPE_BOOLEAN) {
            match("boolean");
            parent.addChild(new Node("boolean", parent));
            return "boolean";
        }
        return null;
    }
    
    /**
     * char ::== a | b | c | ... | z
     */
    private String character(Node parent) {
        // peek at current Token for Value checking
        String currentValue = peek().getValue();

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
            default: log("ERROR", "Expected char [a-z] , found [ " + currentValue + " ]"); break;
        }

        match(expectedLetter);
        parent.addChild(new Node(expectedLetter, parent));
        return expectedLetter;
    }

    /**
     * digit ::== 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
     */
    private void digit(Node parent) {
        // peek at current Token for Value checking
        String currentValue = peek().getValue();

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
            default: log("ERROR", "Expected digit [0-9] , found [ " + currentValue + " ]"); break;
        }

        match(expectedDigit);
        parent.addChild(new Node(expectedDigit, parent));
    }

    /**
     * boolop ::== == | !=
     */
    private void boolOp(Node parent) {
        // peek at current Token for Value checking
        Kind currentKind = peek().getKind();

        if(currentKind == Kind.EQUALITY_OP) {
            match("==");
            parent.addChild(new Node("==", parent));
        } else if(currentKind == Kind.INEQUALITY_OP) {
            match("!=");
            parent.addChild(new Node("!=", parent));
        }
    }

    /**
     * boolval ::== false | true
     */
    private void boolVal(Node parent) {
        // peek at current Token for Value checking
        Kind currentKind = peek().getKind();

        if(currentKind == Kind.TRUE) {
            match("true");
            parent.addChild(new Node("true", parent));
        } else if(currentKind == Kind.FALSE) {
            match("false");
            parent.addChild(new Node("false", parent));
        }
    }

    /**
     * intop ::== +
     */
    private void intOp(Node parent) {
        match("+");
        parent.addChild(new Node("+", parent));
    }

    /**
     * prints formatted symbol table
     */
    public void printSymbolTable(int programNo) {
        // print symbol table
        System.out.println("Program " + programNo + " Symbol Table");
        System.out.println(table.toString());
    }

    /**
     * getter for SymbolTable
     * @return complete SymbolTable
     */
    public SymbolTable getSymbolTable() {
        return table;
    }

    /**
     * prints formatted abstract syntax tree
     */
    public void printAST(int programNo) {
        // print AST
        System.out.println("Program " + programNo + " Abstract Syntax Tree");
        System.out.println("------------------------------------");
        System.out.println(AST.toString());
    }

    /**
     * getter for abstract syntax tree
     * @return SyntaxTree AST
     */
    public SyntaxTree getAST() {
        return AST;
    }

    /**
     * determines if semantic analyzer completed without errors
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
        super.log(alert, "Semantic Analyzer", msg);
    }
}