import java.util.List;

abstract class AstNode {
    public void printAST() {
    }
}

class Program extends AstNode {
    List<Decl> decls;
    List<AstNode> stmts;

    public Program(List<Decl> decls, List<AstNode> stmts) {
        this.decls = decls;
        this.stmts = stmts;
        printAST();
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
    String id;
    Expr expr;

    public Decl(String id, Expr expr) {
        this.id = id;
        this.expr = expr;
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
    AstNode expr;

    public Assign(String id, AstNode expr) {
        this.id = id;
        this.expr = expr;
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
    Scope elseStmts;

    public If(Condition cond, Scope ifStmts, Scope elseStmts) {
        this.cond = cond;
        this.ifStmts = ifStmts;
        this.elseStmts = elseStmts;
    }

    public If(Condition cond, Scope ifStmts) {
        this.cond = cond;
        this.ifStmts = ifStmts;
        this.elseStmts = null;
    }

    public void printAST() {
        System.out.println("IF");
        System.out.println("  COND: ");
        cond.printAST();
        System.out.println("  IFSTMTS: ");
        ifStmts.printAST();
        if (elseStmts != null) {
            System.out.println("  ELSESTMTS: ");
            elseStmts.printAST();
        }
    }
}

class Condition extends AstNode {

    AstNode left;
    OpRel op;
    AstNode right;

    public Condition(AstNode left, Object op, AstNode right) {
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

    public Condition(AstNode left, AstNode right) {
        this.left = left;
        this.right = right;
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
    Object value;

    public Expr(Object value, Type type) {
        this.type = type;
        this.value = value;
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

    private static Float makeOperation(Expr e1, Object OP, Expr e2) {
        switch (OP.toString()) {
            // PLUS
            case "18":
                return Float.parseFloat(e1.value.toString()) + Float.parseFloat(e2.value.toString());
            // MINUS
            case "19":
                return Float.parseFloat(e1.value.toString()) - Float.parseFloat(e2.value.toString());
            // MULT
            case "20":
                return Float.parseFloat(e1.value.toString()) * Float.parseFloat(e2.value.toString());
            // DIV
            case "21":
                if (isCero(e2.value))
                    throw new IllegalArgumentException("Error: Division by zero");
                return Float.parseFloat(e1.value.toString()) / Float.parseFloat(e2.value.toString());
            // MOD
            case "22":
                if (isCero(e2.value))
                    throw new IllegalArgumentException("Error: Division by zero");
                return Float.parseFloat(e1.value.toString()) % Float.parseFloat(e2.value.toString());

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
        this.id = id;
    }

    public void printAST() {
        System.out.println("Read");
        System.out.println("  id: " + id);
    }
}

class Write extends AstNode {
    AstNode expr;

    public Write(AstNode expr) {
        this.expr = expr;
    }

    public void printAST() {
        System.out.println("Write");
        System.out.println("  expr: ");
        expr.printAST();
    }

}
