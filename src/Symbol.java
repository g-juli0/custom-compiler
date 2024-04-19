
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
 
    /**
     * constructor for Symbol object
     * @param name name or id of variable
     * @param type type of variable (int, string, boolean)
     * @param scope scope in which variable was declared
     * @param isInit true if variable was initialized
     * @param isUsed true if variable is used in code
     */
    public Symbol(String name, String type, int scope, boolean isInit, boolean isUsed) {
        this.name = name;
        this.type = type;
        this.scope = scope;
        this.isInit = isInit;
        this.isUsed = isUsed;
    }

    /**
     * getter for Symbol name
     * @return name or id of variable
     */
    public String getName() {
        return name;
    }

    /**
     * getter for Symbol type
     * @return type of variable (int, string, boolean)
     */
    public String getType() {
        return type;
    }

    /**
     * getter for Symbol scope
     * @return scope in which variable was declared
     */
    public int getScope() {
        return scope;
    }

    /**
     * getter for Symbol isInit
     * @return true if variable was initialized
     */
    public boolean getIsInit() {
        return isInit;
    }

    /**
     * getter for Symbol isUsed
     * @return true if variable is used in code
     */
    public boolean getIsUsed() {
        return isUsed;
    }

    /**
     * sets isInit value to true
     */
    public void initialize() {
        isInit = true;
    }

    /**
     * sets isUsed to true
     */
    public void use() {
        isUsed = true;
    }
}
