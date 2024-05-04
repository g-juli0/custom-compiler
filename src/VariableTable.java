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
        table.append("+------+----+-------+--------+\n");
        table.append("| addr | id | scope | offset |\n");
        table.append("+------+----+-------+--------+\n");

        // append entries
        for(VariableEntry entry : entries) {
            table.append("| " + entry.getTempAddress() + " ");
            table.append("| " + entry.getId() + "  ");
            table.append("| " + entry.getScope() + "     ");
            table.append("| " + entry.getOffset() + "      |\n");
        }
        // close table
        table.append("+------+----+-------+--------+\n");

        return table.toString();
    }
}
