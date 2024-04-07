import java.util.ArrayList;

/**
 * SymbolTable object used to keep track of variables
 *      during the semantic analysis phase
 */
public class SymbolTable {

    private ArrayList<Symbol> table;

    public SymbolTable() {

    }

    public String toString() {
        String table = "";

        table += "+------+------+-------+--------+--------+";
        table += "| Name | Type | Scope | isInit | isUsed |";
        table += "+------+------+-------+--------+--------+";

        return table;
    }
    
}
