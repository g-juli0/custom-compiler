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
                if(s.getName().equals(id) && s.getScope().getValue().equals(temp.getValue())) {
                    return s;
                }
            }
            temp = temp.getParent();
        }
        // not found
        return null;
    }

    public Symbol lookup(String id, String type) {
        // if not found, look up in parent scopes{
        for (Symbol s : symbols) {
            // if id name is the same and if scope value is the same
            //System.out.println(id + ", " + scope.getValue());
            if(s.getName().equals(id) && s.getType().equals(type)) {
                return s;
            }
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
            // name
            table.append("| " + s.getName() + "    | ");

            // type
            String type = s.getType();
            table.append(type);
            int spaces = 8 - type.length();

            // buffer for type
            for (int i = 0; i < spaces; i++) {
                table.append(" ");
            }

            // scope
            table.append("| " + s.getScope().getValue() + "     | ");

            // isInit
            boolean init = s.getIsInit();
            table.append(init + "  ");
            if(init) {
                table.append(" ");
            }
            table.append("| ");

            // isUsed
            boolean used = s.getIsUsed();
            table.append(used + "  ");
            if(used) {
                table.append(" ");
            }
            table.append("|\n");
        }

        // close table
        table.append("+------+---------+-------+--------+--------+\n");

        return table.toString();
    }
    
}
