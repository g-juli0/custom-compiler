import java.util.HashMap;


public class JumpTable {

    // temp address mapped to distance
    private HashMap<String, Integer> table;

    public JumpTable() {
        table = new HashMap<>();
    }

    public String toString() {
        StringBuilder output = new StringBuilder("");

        // build header
        output.append("+------+------+\n");
        output.append("| addr | jump |\n");
        output.append("+------+------+\n");

        return output.toString();
    }
}