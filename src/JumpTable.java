import java.util.HashMap;


public class JumpTable {

    // temp address mapped to distance
    private HashMap<String, Integer> table;

    public JumpTable() {
        table = new HashMap<>();
    }

    public void addEntry(String key, int value) {
        table.put(key, Integer.valueOf(value));
    }

    public HashMap<String, Integer> getTable() {
        return table;
    }

    public String toString() {
        StringBuilder output = new StringBuilder("");

        // build header
        output.append("+------+------+\n");
        output.append("| addr | jump |\n");
        output.append("+------+------+\n");

        // append entries
        table.forEach((key, value) -> {
            output.append("| " + key + " | " + value + "    |\n");
        });

        // close table
        output.append("+------+------+\n");

        return output.toString();
    }
}