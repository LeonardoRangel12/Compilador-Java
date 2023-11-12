import java.util.List;

abstract class AstNode {
    // static int varCont = 0;
    static int labelCont = 0;
    static SymbolTable symbolTable = new SymbolTable();

    public String printIntermediateCode() {
        return "WIP";
    };

    public void printAST() {
    }
}

class Program extends AstNode {
    static List<Decl> decls;
    List<AstNode> stmts;

    public Program(List<Decl> decls, List<AstNode> stmts) {
        Program.decls = decls;
        this.stmts = stmts;
        System.out.println(printIntermediateCode());
    }

    public String printIntermediateCode() {
        String program = "";
        program = program + "BEGIN\n";
        for (Decl decl : decls) {
            program = program + decl.printIntermediateCode();
        }
        for (AstNode stmt : stmts) {
            program = program + stmt.printIntermediateCode();
        }
        program = program + "END";
        return program;

    }

    public static Decl getDecl(String id) {
        for (Decl decl : decls) {
            if (decl.getName() == id) {
                return decl;
            }
        }
        return null;
    }

    public void printAST() {
        System.out.println("\n\nProgram");
        System.out.println("  decls: ");
        for (Decl decl : decls) {
            decl.printAST();
        }
        System.out.println("  stmts: ");
        for (AstNode stmt : stmts) {
            stmt.printAST();
        }
    }

}

class Scope extends AstNode {
    List<Decl> decls;
    List<AstNode> stmts;

    public Scope(List<Decl> decls, List<AstNode> stmts) {
        this.decls = decls;
        this.stmts = stmts;
    }

    public String printIntermediateCode() {
        String scope = "";
        for (Decl decl : decls) {
            scope = scope + decl.printIntermediateCode();
        }
        for (AstNode stmt : stmts) {
            scope = scope + stmt.printIntermediateCode();
        }
        return scope + "\n";
    }

    public void printAST() {
        System.out.println("Scope");

        System.out.println("  decls: ");
        for (Decl decl : decls) {
            decl.printAST();
        }

        System.out.println("  stmts: ");
        for (AstNode stmt : stmts) {
            stmt.printAST();
        }
    }

}

class Decl extends AstNode {
    private Type type;
    private String id;
    private Expr expr;

    public Decl(Type type, String id) {
        this.type = type;
        this.id = id;
        this.expr = null;
        symbolTable.setVar(this);
    }

    public Decl(Type type, String id, Expr expr) {
        this.type = type;
        this.id = id;
        this.expr = expr;
        // Check if the variable and the expression are of the same type
        if(!expr.getType().equals(type) || type.equals(Type.FLOAT) && expr.getType().equals(Type.INT)){
            throw new IllegalArgumentException("Error: Variable " + id + " and expression " + expr.getValue() + " are not of the same type");
        }
        symbolTable.setVar(this);
    }

    public String printIntermediateCode() {

        String response = symbolTable.getEquivalent(id) + " = " + expr.printIntermediateCode() + "\n";
        return response;
    }

    public String getName() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public Object getValue() {
        return expr.getValue();
    }

    public void printAST() {
        System.out.println("Decl");
        System.out.println("  id: " + id);
        System.out.println("  expr: ");
        expr.printAST();
    }
}

class Assign extends AstNode {
    String id;
    Expr expr;

    public Assign(String id, Expr expr) {
        Decl decl = symbolTable.getVar(id);
        // Check if the variable is declared
        if (decl == null) {
            throw new IllegalArgumentException("Error: Variable " + id + " not declared");
        }
        // Check if the variable and the expression are of the same type
        if (decl.getType() == expr.getType() || decl.getType() == Type.FLOAT && expr.getType() == Type.INT) {
            throw new IllegalArgumentException("Error: Variable " + id + " and expression are not of the same type");
        }

        this.id = id;
        this.expr = expr;
    }

    public String printIntermediateCode() {
        String response = symbolTable.getEquivalent(id) + " = " + expr.printIntermediateCode() + "\n";
        return response;
    }

    public void printAST() {
        System.out.println("Assign");
        System.out.println("  id: " + id);
        System.out.println("  expr: ");
        expr.printAST();
    }
}

class While extends AstNode {
    Condition cond;
    Scope stmts;

    public While(Condition cond, Scope stmts) {
        this.cond = cond;
        this.stmts = stmts;
    }

    public String printIntermediateCode() {
        String response = "L" + labelCont++ + ": ";
        response = response + "if " + cond.printIntermediateCode() + " GOTO L" + labelCont++ + "\n";
        response = response + stmts.printIntermediateCode();
        response = response + "GOTO L" + (labelCont - 2) + "\n";
        response = response + "L" + (labelCont - 1) + ": \n";
        return response;
    }

    public void printAST() {
        System.out.println("While");
        System.out.println("  cond: ");
        cond.printAST();
        System.out.println("  stmts: ");
        stmts.printAST();
    }
}

class If extends AstNode {
    Condition cond;
    Scope ifStmts;
    // Scope elseStmts;

    // public If(Condition cond, Scope ifStmts, Scope elseStmts) {
    // this.cond = cond;
    // this.ifStmts = ifStmts;
    // this.elseStmts = elseStmts;
    // }

    public If(Condition cond, Scope ifStmts) {
        this.cond = cond;
        this.ifStmts = ifStmts;
        // this.elseStmts = null;
    }

    public String printIntermediateCode() {
        String response = "L" + labelCont++ + ": ";
        response = response + "if " + cond.printIntermediateCode() + " GOTO L" + labelCont++ + "\n";
        response = response + "GOTO L" + (labelCont++) + "\n";
        response = response + "L" + (labelCont - 2) + ":\n";
        response = response + ifStmts.printIntermediateCode();
        response = response + "GOTO L" + (labelCont - 1) + "\n";
        response = response + "L" + (labelCont - 1) + ": \n";
        return response;
    }

    public void printAST() {
        System.out.println("IF");
        System.out.println("  COND: ");
        cond.printAST();
        System.out.println("  IFSTMTS: ");
        ifStmts.printAST();
        // if (elseStmts != null) {
        // System.out.println(" ELSESTMTS: ");
        // elseStmts.printAST();
        // }
    }
}

class Condition extends AstNode {

    Expr left;
    private OpRel op;
    Expr right;

    public Condition(Expr left, OpRel op, Expr right) {
        // Check if both expressions are of the same type or if they are int and float
        if(!left.getType().equals(right.getType()) && !(left.getType().equals(Type.FLOAT) && right.getType().equals(Type.INT))){
            throw new IllegalArgumentException("Error: Expressions " + left.getValue() + " and " + right.getValue() + " are not of the same type");
        }
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public String printIntermediateCode() {
        String response = left.printIntermediateCode() + " " + OpRel.getOp(op) + " " + right.printIntermediateCode();
        return response;
    }

    public void printAST() {
        System.out.println("Condition");
        System.out.println("  left: ");
        left.printAST();
        System.out.println("  right: ");
        right.printAST();
    }
}

class Expr extends AstNode {

    private Type type;
    private Object value;

    public Expr(Object value, Type type) {
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        // If it is an identifier, get its type from the symbol table
        if(type == Type.IDENT){
            Decl decl = symbolTable.getVar(value.toString());
            if (decl == null) {
                throw new IllegalArgumentException("Error: Variable " + value.toString() + " not declared");
            }
            return decl.getType();
        }
        // If it is another type, return it
        return type;
    }

    public String getValue() {
        if(type == Type.IDENT){
            return symbolTable.getEquivalent(value.toString());
        }
        if (type == Type.STRING) {
            return "\"" + value.toString() + "\"";
            
        }
        return value.toString();
    }

    public Expr(Expr e1, OpArit OP, Expr e2) {
        // Check if both expressions are of the same type
        if (e1.type.equals(Type.STRING) || e2.type.equals(Type.STRING)) {
            System.out.println("Error: Both expressions must be of the same type");
            System.exit(1);
        }

        // Makes the operation
        this.value = makeOperation(e1, OP, e2);

        // Check if the result is an integer or a float
        if (Double.parseDouble(this.value.toString()) % 1 == 0) {
            this.type = Type.INT;
            this.value = (int) Double.parseDouble(this.value.toString());
        } else {
            this.type = Type.FLOAT;
            this.value = Float.parseFloat(this.value.toString());
        }
    }

    public String printIntermediateCode() {
        // If it is a string, return it with quotes
        if (type.equals(Type.STRING))
            return "\"" + value.toString() + "\"";
        // If it is an identifier, get its value from the symbol table
        else if (type.equals(Type.IDENT)) {
            String equivalent = symbolTable.getEquivalent(value.toString());
            if (equivalent == null) {
                throw new IllegalArgumentException("Error: Variable " + value.toString() + " not declared");
            }
            return equivalent;

        } else
        // If it is an integer or a float, return it as it is
            return value.toString();
    }

    private static Float makeOperation(Expr e1, OpArit OP, Expr e2) {
        String left = e1.value.toString();
        String right = e2.value.toString();

        if (e1.type == Type.IDENT) {
            Decl decl = symbolTable.getVar(left);
            if (decl == null) {
                throw new IllegalArgumentException("Error: Variable " + left + " not declared");
            }
            left = decl.getValue().toString();
        }
        if (e2.type == Type.IDENT) {
            Decl decl = symbolTable.getVar(right);

            if (decl == null) {
                throw new IllegalArgumentException("Error: Variable " + right + " not declared");
            }
            right = decl.getValue().toString();
        }

        switch (OP) {
            // PLUS
            case PLUS:
                return Float.parseFloat(left) + Float.parseFloat(right);
            // MINUS
            case MINUS:
                return Float.parseFloat(left) - Float.parseFloat(right);
            // MULT
            case MULT:
                return Float.parseFloat(left) * Float.parseFloat(right);
            // DIV
            case DIV:
                if (isCero(e2.value))
                    throw new IllegalArgumentException("Error: Division by zero");
                return Float.parseFloat(left) / Float.parseFloat(right);
            // MOD
            case MOD:
                if (isCero(e2.value))
                    throw new IllegalArgumentException("Error: Division by zero");
                return Float.parseFloat(left) % Float.parseFloat(right);

            default:
                throw new IllegalArgumentException("Unexpected value: " + OP.toString());
        }

    }

    private static boolean isCero(Object value) {
        if (value instanceof Integer && (Integer) value == 0) {
            return true;
        }
        if (value instanceof Float && (Float) value == 0.0f) {
            return true;
        }
        return false;
    }

    public void printAST() {
        System.out.println("Expr");
        System.out.println("  type: " + type);
        System.out.println("  value: " + value);
    }
}

class Read extends AstNode {
    String id;

    public Read(String id) {
        if (symbolTable.getEquivalent(id) == null) {
            throw new IllegalArgumentException("Error: Variable " + id + " not declared");
        }
        this.id = id;
    }

    public String printIntermediateCode() {
        String response = symbolTable.getEquivalent(id) + " = " + "read()\n";
        return response;
    }

    public void printAST() {
        System.out.println("Read");
        System.out.println("  id: " + id);
    }
}

class Write extends AstNode {
    Expr expr;

    public Write(Expr expr) {
        this.expr = expr;
    }

    public String printIntermediateCode() {
        String response = "write(" + expr.printIntermediateCode() + ")\n";
        return response;
    }

    public void printAST() {
        System.out.println("Write");
        System.out.println("  expr: ");
        expr.printAST();
    }

}
