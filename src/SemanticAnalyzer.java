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

    private SyntaxTree scopeTree;
    private Node scopePointer;
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
        table = new SymbolTable();

        warningCount = 0;
        errorCount = 0;

        log("INFO", "Semantically analyzing program " + Integer.toString(programNo) + "...");

        // entry point for pseudo parse
        block(null);
        match("$");

        warningCheck();

        if(success()) {
            log("INFO", "Semantic analysis completed with " + errorCount + " error(s) and " + warningCount + " warning(s)\n");
            printAST(programNo);
            printScopeTree(programNo);
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
     * type checks an id and a type or another id
     * @param symbol symbol to evaluate
     * @param type specified type for assignment
     * @return true if types match
     */
    private boolean typeCheck(Symbol symbol, String type) {
        String sType = symbol.getType();

        // if the other side of the expression is an id
        if(type.length() == 1) {
            Symbol other = table.lookup(type, scopePointer);
            if(sType.equals(other.getType())) {
                return true;
            }
        // int, string, or boolean expr
        } else {
            if(sType.equals(type)) {
                return true;
            }
        }
        // if neither check returns true
        return false;
    }

    /**
     * Block ::== { StatementList }
     */
    private void block(Node astParent) {
        // log debug message
        log("DEBUG", "Block");

        match("{");

        // if AST root has not already been created
        if(astParent == null) {
            // create new root for AST
            Node astRoot = new Node("Block");
            AST = new SyntaxTree(astRoot);

            // create new root for scope tree
            Node scopeRoot = new Node(Integer.toString(scope));
            scopeTree = new SyntaxTree(scopeRoot);

            scopePointer = scopeRoot;
            statementList(astRoot);
        } else {
            // create new Block Node with parent and add to children list
            Node blockNode = new Node("Block", astParent);
            astParent.addChild(blockNode);

            // increase scope counter, create new scope Node with parent, and add to children list
            scope++;
            Node scopeNode = new Node(Integer.toString(scope), scopePointer);
            scopePointer.addChild(scopeNode);

            scopePointer = scopeNode;
            statementList(blockNode);
        }

        match("}");
        // close scope
        scopePointer = scopePointer.getParent();
        
    }

    /**
     * StatementList ::== Statement StatementList
     *               ::== epsilon (empty) production
     * 
     * no Node added to AST
     */
    private void statementList(Node astParent) {
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
            statement(astParent);
            statementList(astParent);
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
    private void statement(Node astParent) {
        // peek at current Token for Kind checking
        Kind currentKind = peek().getKind();
        
        // PrintStatement
        if(currentKind == Kind.PRINT) {
            printStatement(astParent);
        // AssignStatement
        } else if(currentKind == Kind.ID) {
            assignmentStatement(astParent);
        // VarDecl
        } else if(currentKind == Kind.TYPE_INT || 
                currentKind == Kind.TYPE_STRING ||
                currentKind == Kind.TYPE_BOOLEAN) {
            varDecl(astParent);
        // WhileStatement
        } else if(currentKind == Kind.WHILE) {
            whileStatement(astParent);
        // IfStatement
        } else if(currentKind == Kind.IF) {
            ifStatement(astParent);
        // Block
        } else if(currentKind == Kind.OPEN_BLOCK) {
            block(astParent);
        }
    }

    /**
     * PrintStatement ::== print ( Expr )
     */
    private void printStatement(Node astParent) {
        // log debug message
        log("DEBUG", "PrintStatement");

        // create new Node and add it to tree
        Node printStatementNode = new Node("PrintStatement", astParent);
        astParent.addChild(printStatementNode);

        match("print");
        match("(");

        expr(printStatementNode);

        match(")");
    }

    /**
     * AssignmentStatement ::== Id = Expr
     */
    private void assignmentStatement(Node astParent) {
        // log debug message
        log("DEBUG", "AssignmentStatement");

        // create new Node and add it to tree
        Node assignStatementNode = new Node("AssignmentStatement", astParent);
        astParent.addChild(assignStatementNode);

        String symbol = id(assignStatementNode);

        match("=");

        String type = expr(assignStatementNode);

        // check if symbol is declared in table at all
        Symbol s = table.lookup(symbol, scopePointer);
        if(s != null) {
            // perform type check
            if(typeCheck(s, type)) {
                // symbol has now been initialized to a value
                s.initialize();
            } else {
                // mismatched type
                log("ERROR", "Mismatched types. Unable to assign symbol " + symbol + " to type " + type);
                errorCount++;
            }
        } else {
            // not in symbol table
            log("ERROR", "Variable not declared. Symbol " + symbol + " not found in symbol table.");
            errorCount++;
        } 
    }

    /**
     * VarDecl ::== type Id
     */
    private void varDecl(Node astParent) {
        // log debug message
        log("DEBUG", "VarDecl");

        // create new Node and add it to tree
        Node varDeclNode = new Node("VarDecl", astParent);
        astParent.addChild(varDeclNode);
        
        String type = type(varDeclNode);
        String id = id(varDeclNode);

        table.addSymbol(new Symbol(id, type, scope, false, false));
    }

    /**
     * WhileStatement ::== while BooleanExpr Block
     */
    private void whileStatement(Node astParent) {
        // log debug message
        log("DEBUG", "WhileStatement");

        // create new Node and add it to tree
        Node whileStatementNode = new Node("WhileStatement", astParent);
        astParent.addChild(whileStatementNode);

        match("while");

        booleanExpr(whileStatementNode);
        block(whileStatementNode);
    }

    /**
     * IfStatement ::== if BooleanExpr Block
     */
    private void ifStatement(Node astParent) {
        // log debug message
        log("DEBUG", "IfStatement");

        // create new Node and add it to tree
        Node ifStatementNode = new Node("IfStatement", astParent);
        astParent.addChild(ifStatementNode);

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
    private String expr(Node astParent) {
        // peek at current Token for Kind checking
        Kind currentKind = peek().getKind();

        if(currentKind == Kind.DIGIT) {
            intExpr(astParent);
            return "int";
        } else if(currentKind == Kind.QUOTE) {
            stringExpr(astParent);
            return "string";
        } else if(currentKind == Kind.OPEN_PAREN ||
                    currentKind == Kind.FALSE ||
                    currentKind == Kind.TRUE) {
            booleanExpr(astParent);
            return "boolean";
        } else if(currentKind == Kind.ID) {
            String s = id(astParent);
            // mark id as used
            Symbol symbol = table.lookup(s, scopePointer);
            if(symbol != null) {
                symbol.use();
            } else {
                // not in symbol table 
                log("ERROR", "Variable not declared. Symbol " + symbol + " not found in symbol table.");
                errorCount++;
            }
            return s;
        }
        return null;
    }

    /**
     * IntExpr ::== digit intop Expr
     *         ::== digit
     */
    private void intExpr(Node astParent) {
        // log debug message
        log("DEBUG", "IntExpr");
        
        // parse first digit, then get current Token after digit is matched
        digit(astParent);

        // peek at current Token for Kind checking
        Kind currentKind = peek().getKind();

        if(currentKind == Kind.ADD_OP) {
            intOp(astParent);
            expr(astParent);
        }
    }

    /**
     * StringExpr ::== " CharList "
     */
    private void stringExpr(Node astParent) {
        // log debug message
        log("DEBUG", "StringExpr");

        match("\"");

        charList(astParent);

        match("\"");
    }

    /**
     * BooleanExpr ::== ( Expr boolop Expr )
     *             ::== boolval
     */
    private void booleanExpr(Node astParent) {
        // log debug message
        log("DEBUG", "BooleanExpr");

        // peek at current Token for Kind checking
        Kind currentKind = peek().getKind();

        if(currentKind == Kind.TRUE || currentKind == Kind.FALSE) {
            boolVal(astParent);
        } else {
            match("(");

            expr(astParent);
            boolOp(astParent);
            expr(astParent);

            match(")");
        }
    }

    /**
     * Id ::== char
     * 
     * no Node added to AST
     */
    private String id(Node astParent) {
        return character(astParent);
    }

    /**
     * CharList ::== char CharList
     *          ::== space CharList
     *          ::== epsilon (empty) production
     * 
     * convert CharList to a single string for AST Node
     */
    private void charList(Node astParent) {
        StringBuilder strBuilder = new StringBuilder();

        // peek at current Token for Kind and Value checking
        Token current = peek();

        while(current.getKind() == Kind.CHAR) {
            strBuilder.append(current.getValue());

            pop();
            current = peek();
        }

        // create new Node and add it to tree
        Node charListNode = new Node(strBuilder.toString(), astParent);
        astParent.addChild(charListNode);
    }

    /**
     * type ::== int | string | boolean
     */
    private String type(Node astParent) {
        // peek at current Token for Kind checking
        Kind currentKind = peek().getKind();

        if(currentKind == Kind.TYPE_INT) {
            match("int");
            astParent.addChild(new Node("int", astParent));
            return "int";
        } else if(currentKind == Kind.TYPE_STRING) {
            match("string");
            astParent.addChild(new Node("string", astParent));
            return "string";
        } else if(currentKind == Kind.TYPE_BOOLEAN) {
            match("boolean");
            astParent.addChild(new Node("boolean", astParent));
            return "boolean";
        }
        return null;
    }
    
    /**
     * char ::== a | b | c | ... | z
     */
    private String character(Node astParent) {
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
        astParent.addChild(new Node(expectedLetter, astParent));
        return expectedLetter;
    }

    /**
     * digit ::== 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
     */
    private void digit(Node astParent) {
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
        astParent.addChild(new Node(expectedDigit, astParent));
    }

    /**
     * boolop ::== == | !=
     */
    private void boolOp(Node astParent) {
        // peek at current Token for Value checking
        Kind currentKind = peek().getKind();

        if(currentKind == Kind.EQUALITY_OP) {
            match("==");
            astParent.addChild(new Node("==", astParent));
        } else if(currentKind == Kind.INEQUALITY_OP) {
            match("!=");
            astParent.addChild(new Node("!=", astParent));
        }
    }

    /**
     * boolval ::== false | true
     */
    private void boolVal(Node astParent) {
        // peek at current Token for Value checking
        Kind currentKind = peek().getKind();

        if(currentKind == Kind.TRUE) {
            match("true");
            astParent.addChild(new Node("true", astParent));
        } else if(currentKind == Kind.FALSE) {
            match("false");
            astParent.addChild(new Node("false", astParent));
        }
    }

    /**
     * intop ::== +
     */
    private void intOp(Node astParent) {
        match("+");
        astParent.addChild(new Node("+", astParent));
    }

    private void warningCheck() {
        for(Symbol s : table.getSymbols()) {
            if(!s.getIsInit()) {
                warningCount++;
                log("WARNING", "Symbol " + s.getName() + " is not initialized. Set to default value for type " + s.getType());
            }
            if(!s.getIsUsed()) {
                warningCount++;
                log("WARNING", "Unused symbol " + s.getName() + " of type " + s.getType());
            }
        }
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
     * prints formatted scope tree
     * @param programNo
     */
    public void printScopeTree(int programNo) {
        System.out.println("Program " + programNo + " Scope Tree");
        System.out.println("------------------------------------");
        System.out.println(scopeTree.toString());
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
