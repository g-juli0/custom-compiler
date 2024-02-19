/**
 * Parent class for all compiler components
 * contains debug switch and log funtion
 */
public class Component {
    
    public boolean debug = true;

    public void log(String alert, String step, String msg) {
        if(debug) {
            System.out.println(alert + " - " + step + " - " + msg);
        }
    }
}
