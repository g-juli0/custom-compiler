


public class VariableEntry {

    private String tempAddress;
    private String id;
    private int scope;

    public VariableEntry(String id, int numEntries, int scope) {
        tempAddress = "T" + numEntries + "XX";
        this.id = id;
        this.scope = scope;
    }

    public String getTempAddress() {
        return tempAddress;
    }

    public String getId() {
        return id;
    }

    public int getScope() {
        return scope;
    }
    
}
