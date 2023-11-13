import java.util.ArrayList;
import java.util.List;

public class SymbolTable {
    static int varCont = 0;
    private List<Symbol> symbols = new ArrayList<Symbol>();

    public SymbolTable() {
    }

    // Adds the variable to the symbol table
    public void setVar(Decl decl) {
        symbols.add(new Symbol(decl, "t" + varCont++));
    }

    // Returns the variable from the symbol table
    public Decl getVar(String id) {
        for (Symbol symbol : symbols) {
            Decl decl = symbol.getVar();
            if (decl.getName().equals(id)) {
                return decl;
            }
        }
        // If the variable is not found, return null
        return null;
    }

    public String getEquivalent(String id) {
        for (Symbol symbol : symbols) {
            String equivalent = symbol.getVarName();
            if (equivalent.equals(id)) {
                return symbol.getEquivalent();
            }
        }
        return null;
    }

    public String printIntermediateCode(){
        String intermediateCode = "";
        for (Symbol symbol : symbols) {
            intermediateCode += symbol.getEquivalent() + " = " + symbol.getVar().getValue() + "\n";
        }
        return intermediateCode;
        
    }
}
