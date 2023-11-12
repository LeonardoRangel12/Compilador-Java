import java.util.ArrayList;
import java.util.List;

public class SymbolTable {
    static int varCont = 0;
    private List<Symbol> symbols = new ArrayList<Symbol>(); 

    public SymbolTable() {
    }

    public void setVar(Decl decl){
        symbols.add(new Symbol(decl, "t" + varCont++));
    }

    public Decl getVar(String id) {
        for (Symbol symbol : symbols) {
            Decl decl = symbol.getVar(id);
            if(decl != null) {
                return decl;
            }
        }
        return null;
    }
}
