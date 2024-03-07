/**
 * Parent class for all compiler components
 * contains master debug switch and log funtion
 */
public class Component {
    
    public boolean debug;

    /**
     * constructor
     * @param verbose debug flag
     */
    public Component(boolean verbose) {
        // verbose mode on by default
        this.debug = verbose;
    }

    /**
     * standard logging message for each component of the compiler
     * @param alert info, debug, warning error
     * @param step - lex, parse, semantic analysis, code gen
     * @param msg - message to log
     */
    public void log(String alert, String step, String msg) {
        if(debug) {
            System.out.println(alert + " - " + step + " - " + msg);
        }
    }
}
