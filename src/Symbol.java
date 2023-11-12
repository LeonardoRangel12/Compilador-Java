public class Symbol {
    private Decl decl;
    private String id;

    public Symbol(Decl decl, String id) {
        this.decl = decl;
        this.id = id;
    }

    // Returns the variable from the symbol table
    public Decl getVar() {
            return decl;
    }
    public String getVarName(){
        return decl.getName();
    }

    // Returns the equivalent variable from the symbol table for intermediate code
    public String getEquivalent() {
            return this.id;
    }
}
