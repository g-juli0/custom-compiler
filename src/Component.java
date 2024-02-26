/**
 * Parent class for all compiler components
 * contains master debug switch and log funtion
 */
public class Component {
    
    public boolean debug;

    public Component(boolean verbose) {
        this.debug = verbose;
    }

    public void log(String alert, String step, String msg) {
        if(debug) {
            System.out.println(alert + "\t- " + step + " - " + msg);
        }
    }
}
