
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

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getScope() {
        return scope;
    }

    public boolean getIsInit() {
        return isInit;
    }

    public void setIsInit(boolean init) {
        isInit = init;
    }

    public boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(boolean used) {
        isUsed = used;
    }
}
