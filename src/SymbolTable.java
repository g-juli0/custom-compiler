import java.util.ArrayList;

/**
 * SymbolTable object used to keep track of variables
 *      during the semantic analysis phase
 */
public class SymbolTable {

    private ArrayList<SymbolTable> children;
    private ArrayList<Symbol> symbols;

    public SymbolTable() {
        children = new ArrayList<>();
        symbols = new ArrayList<>();
    }

    public void addSymbol(Symbol s) {
        symbols.add(s);
    }

    public Symbol lookup(String id, Node scope) {
        // if not found, look up in parent scopes
        Node temp = scope;
        while(temp != null) {
            for (Symbol s : symbols) {
                // if id name is the same and if scope value is the same
                //System.out.println(id + ", " + scope.getValue());
                if(s.getName().equals(id) && Integer.toString(s.getScope()).equals(temp.getValue())) {
                    return s;
                }
            }
            temp = temp.getParent();
        }
        // not found
        return null;
    }

    public ArrayList<SymbolTable> getChildren() {
        return children;
    }

    public ArrayList<Symbol> getSymbols() {
        return symbols;
    }

    public String toString() {
        StringBuilder table = new StringBuilder();

        table.append("+------+---------+-------+--------+--------+\n");
        table.append("| Name | Type    | Scope | isInit | isUsed |\n");
        table.append("+------+---------+-------+--------+--------+\n");

        for(Symbol s : symbols) {
            table.append("| " + s.getName() + "    | ");

            String type = s.getType();
            table.append(type);
            int spaces = 8 - type.length();

            for (int i = 0; i < spaces; i++) {
                table.append(" ");
            }

            table.append("| " + s.getScope() + "     | ");

            boolean init = s.getIsInit();
            table.append(init + "  ");
            if(init) {
                table.append(" ");
            }
            table.append("| ");

            boolean used = s.getIsUsed();
            table.append(used + "  ");
            if(used) {
                table.append(" ");
            }
            table.append("|\n");
        }

        table.append("+------+---------+-------+--------+--------+\n");

        return table.toString();
    }
    
}
