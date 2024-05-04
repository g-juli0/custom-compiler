import java.util.ArrayList;



public class VariableTable {
    
    private ArrayList<VariableEntry> entries;

    public VariableTable() {
        entries = new ArrayList<>();
    }

    public void addEntry(String id, int scope) {
        VariableEntry entry = new VariableEntry(id, entries.size(), scope);
        entries.add(entry);
    }

    public ArrayList<VariableEntry> getTable() {
        return entries;
    }

    public VariableEntry lookup(String id, int scope) {
        for(VariableEntry entry : entries) {
            if(entry.getId().equals(id) && entry.getScope() == scope) {
                return entry;
            }
        }
        return null;
    }

    public String toString() {
        StringBuilder table = new StringBuilder("");

        // build header
        table.append("+------+----+-------+--------+");
        table.append("| addr | id | scope | offset |");
        table.append("+------+----+-------+--------+");

        // append entries
        for(VariableEntry entry : entries) {
            table.append("| " + entry.getTempAddress() + " |\n");
            table.append("| " + entry.getId() + "  |\n");
            table.append("| " + entry.getScope() + "     |\n");
            table.append("| " + entry.getOffset() + "      |\n");
        }
        // close table
        table.append("+------+----+-------+--------+");

        return table.toString();
    }
}
