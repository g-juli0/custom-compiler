
/**
 * Symbol Object that contains all of the information about variables
 *      in the symbol table: name, type, scope, isInitialized, isUsed
 */
public class Symbol {

    private String name;
    private String type;
    private int scope;
    private boolean isInit;
    private boolean isUsed;
 
    public Symbol(String name, String type, int scope, boolean isInit, boolean isUsed) {
        this.name = name;
        this.type = type;
        this.scope = scope;
        this.isInit = isInit;
        this.isUsed = isUsed;
    }
}
