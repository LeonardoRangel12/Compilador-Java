public class Symbol {
    Decl decl;
    String id;

    public Symbol(Decl decl, String id) {
        this.decl = decl;
        this.id = id;
    }

    public Decl getVar(String id) {
        if(this.id.equals(id)) {
            return decl;
        }
        return null;
    }
}
