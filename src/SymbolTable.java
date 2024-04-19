import java.util.ArrayList;

/**
 * SymbolTable object used to keep track of variables
 *      during the semantic analysis phase
 */
public class SymbolTable {

    //private SyntaxTree scopeTree;
    private SymbolTable parent;
    private ArrayList<SymbolTable> children;

    private ArrayList<Symbol> symbols;
    private int scope;

    public SymbolTable(int s) {
        parent = null;
        children = new ArrayList<>();

        symbols = new ArrayList<>();
        scope = s;
    }

    public void addSymbol(Symbol s) {
        symbols.add(s);
    }

    public Symbol lookup(String id) {
        // look up in current scope first
        for (Symbol s : symbols) {
            if(s.getName().equals(id)) {
                return s;
            }
        }

        // if not found, look up in parent scopes
        SymbolTable temp = parent;
        while(temp != null) {
            for (Symbol s : temp.getSymbols()) {
                if(s.getName().equals(id)) {
                    return s;
                }
            }
            temp = temp.getParent();
        }
        // not found
        return null;
    }

    public SymbolTable getParent() {
        return parent;
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
