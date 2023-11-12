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
    private String id;
    private Expr expr;

    public Decl(String id, Expr expr) {
        this.id = id;
        this.expr = expr;
        symbolTable.setVar(this);
    }

    public String printIntermediateCode() {

        String response = symbolTable.getEquivalent(id) + " = " + expr.printIntermediateCode() + "\n";
        return response;
    }

    public String getName() {
        return id;
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
        if (symbolTable.getVar(id) == null) {
            throw new IllegalArgumentException("Error: Variable " + id + " not declared");
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

    public Condition(Expr left, Object op, Expr right) {
        this.left = left;
        switch (op.toString()) {

            // EQ
            case "25":
                this.op = OpRel.EQ;
                break;
            // NEQ
            case "26":
                this.op = OpRel.NEQ;
                break;
            // LE
            case "27":
                this.op = OpRel.LE;
                break;
            // GT
            case "28":
                this.op = OpRel.GT;
                break;
            // LEQ
            case "29":
                this.op = OpRel.LEQ;
                break;
            // GTQ
            case "30":
                this.op = OpRel.GTQ;
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + op.toString());
        }
        this.right = right;
    }

    public String printIntermediateCode() {
        String response = left.printIntermediateCode() + " " + getOp() + " " + right.printIntermediateCode();
        return response;
    }

    private String getOp() {
        if (op == OpRel.EQ) {
            return "==";
        }
        if (op == OpRel.NEQ) {
            return "!=";
        }
        if (op == OpRel.LE) {
            return "<";
        }
        if (op == OpRel.GT) {
            return ">";
        }
        if (op == OpRel.LEQ) {
            return "<=";
        }
        if (op == OpRel.GTQ) {
            return ">=";
        }
        return "";
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

    Type type;
    private Object value;

    public Expr(Object value, Type type) {
        this.type = type;
        this.value = value;
    }

    public String getValue() {
        return value.toString();
    }

    public Expr(Expr e1, Object OP, Expr e2) {
        // Check if both expressions are of the same type
        if (e1.type == Type.STRING || e2.type == Type.STRING) {
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
        if (type == Type.STRING)
            return "\"" + value.toString() + "\"";
        else if (type == Type.IDENT) {
            Decl decl = symbolTable.getVar(value.toString());
            if (decl == null) {
                throw new IllegalArgumentException("Error: Variable " + value.toString() + " not declared");
            }
            return decl.getValue().toString();

        } else
            return value.toString();
    }

    private static Float makeOperation(Expr e1, Object OP, Expr e2) {
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

        switch (OP.toString()) {
            // PLUS
            case "18":
                return Float.parseFloat(left) + Float.parseFloat(right);
            // MINUS
            case "19":
                return Float.parseFloat(left) - Float.parseFloat(right);
            // MULT
            case "20":
                return Float.parseFloat(left) * Float.parseFloat(right);
            // DIV
            case "21":
                if (isCero(e2.value))
                    throw new IllegalArgumentException("Error: Division by zero");
                return Float.parseFloat(left) / Float.parseFloat(right);
            // MOD
            case "22":
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
        if(symbolTable.getEquivalent(id) == null){
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
