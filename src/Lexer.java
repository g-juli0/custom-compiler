/*
 * Lexer class for compiler
 * HIERARCHY
 *  1) keyword
 *  2) id
 *  3) symbol
 *  4) digit
 *  5) char
 * 
 * lex warnings 	- missing last $, unclosed "
 *                  - unclosed comment
 *                  - no EOP symbol $
 * 
 * lex errors 	    - unrecognized character
 */

public class Lexer {
    
    public Lexer(String program) {
        
    }

    public static void lex() {
        System.out.println("lex");
    }

}

