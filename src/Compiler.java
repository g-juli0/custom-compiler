import java.util.ArrayList;

/**
 * entry point / main program for compiler
 */
public class Compiler {
    public static void main(String[] args) {
        
        try {
            // read file
            ArrayList<String> programList = readFile(args[0]);

            System.out.println("INFO - Compilation started");

            // for each program
            for(int i = 0; i < programList.size(); i++) {
                System.out.println("INFO - Compiling Program " + Integer.toString(i) + "...");
                // do lex
                Lexer lex = new Lexer(programList.get(i));

                // do parse

                // do semantic analysis

                // generate opcode
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.err.println("Compiler requires test file to be specified. Try again.");
        }
    }

    /**
     * reads file parameter provided in run command and loads each program into an 
     * ArrayList of Strings for compiler processes
     * @param fileName name of test program(s) file
     * @return ArrayList<String> of program(s) in file, delineated by '$' character
     */
    public static ArrayList<String> readFile(String fileName) {
        System.out.println(fileName); // regurgitates fileName for now
        return new ArrayList<>();
    }
}
