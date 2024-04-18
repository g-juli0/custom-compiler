import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

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

                int programNo = i+1;
                System.out.println("INFO - Compiling program " + programNo + "...");

                // do lex
                Lexer lex = new Lexer(programList.get(i), programNo);
                
                if(lex.success()) {
                    // do parse
                    Parser parse = new Parser(lex.getTokenStream(), programNo);
                    
                    if(parse.success()) {
                        // do semantic analysis
                        SemanticAnalyzer analyzer = new SemanticAnalyzer(lex.getTokenStream(), programNo);

                        if(analyzer.success()) {
                            // generate opcode
                            CodeGenerator generator = new CodeGenerator(analyzer.getAST(), analyzer.getSymbolTable(), programNo);

                            if(generator.success()) {
                                System.out.println("INFO - Program " + programNo + " successfully compiled.\n");
                            } else {
                                System.err.println("INFO - Code generation failed on program " + programNo + ". Compilation unsuccessful.\n");
                            }
                        } else {
                            System.err.println("INFO - Semantic analyzer failed on program " + programNo + ". Skipping remaining compiler phases.\n");
                        }
                    } else {
                        System.err.println("INFO - Parse failed on program " + programNo + ". Skipping remaining compiler phases.\n");
                    }
                } else {
                    System.err.println("INFO - Lex failed on program " + programNo + ". Skipping remaining compiler phases.\n");
                }
            }
        // error catching
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.err.println("Compiler requires test file to be specified. Enter name of file and try again.");
        } catch (FileNotFoundException ex) {
            System.err.println("Unable to locate file. Try again.");
        }
    }

    /**
     * reads file parameter provided in run command and loads each program into an 
     * ArrayList of Strings for compiler processes
     * @param fileName name of test program(s) file
     * @return ArrayList<String> of program(s) in file, delineated by '$' character
     */
    public static ArrayList<String> readFile(String fileName) throws FileNotFoundException {

        // initialize empty list for programs
        ArrayList<String> programs = new ArrayList<>();

        // initialize Scanner object to read from file specified
        Scanner inFile = new Scanner(new File(fileName));

        StringBuilder program = new StringBuilder();

        while(inFile.hasNextLine()) {
            // read in next line
            String next = inFile.nextLine();
            // append line to current program
            program.append(next + "\n");
            
            // if program delimiter is detected within the next line,
            // reset StringBuilder and save program to ArrayList
            if(next.contains("$")) {
                programs.add(program.toString());
                program = new StringBuilder();
            }
        }

        // close file after reading
        inFile.close();

        return programs;
    }
}
