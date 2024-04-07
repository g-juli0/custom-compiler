import java.util.ArrayList;

/**
 * SymbolTable object used to keep track of variables
 *      during the semantic analysis phase
 */
public class SymbolTable {

    private ArrayList<Symbol> symbols;

    public SymbolTable() {
        symbols = new ArrayList<Symbol>();
    }

    public void addSymbol(Symbol s) {
        symbols.add(s);
    }

    public Symbol lookup(String id, String type, int scope) {
        for (Symbol s : symbols) {
            if(s.getName().equals(id) && s.getType().equals(type) && s.getScope() == scope) {
                return s;
            }
        }
        return null;
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
