import java.util.ArrayList;
import java.util.List;

import jflex.base.Pair;

abstract class AstNode {
    // static int varCont = 0;
    static int structureCont = 0;
    static int tempCont = 0;
    static int num = 0;
    static SymbolTable symbolTable = new SymbolTable();
    static List<Expr> tempSymbolTable = new ArrayList<Expr>();
    static List<String> tempEqSymbolTable = new ArrayList<String>();

    public String printIntermediateCode() {
        return "WIP";
    };

    public void printAST() {
    }

    public Type getType() {
        return null;
    }

    public AstNode getValue() {
        return null;
    }
}

class Program extends AstNode {
    List<AstNode> stmts;

    public Program(List<AstNode> stmts) {
        this.stmts = stmts;
        System.out.println(printIntermediateCode());
    }

    public String printIntermediateCode() {
        String program = "";
        // program = program + "BEGIN\n";
        // Prints the intermediate code for the declarations
        // program = program + symbolTable.printIntermediateCode();
        for (AstNode stmt : stmts) {
            program = program + stmt.printIntermediateCode();
        }
        // program = program + "END";
        return program;

    }

    public void printAST() {
        System.out.println("\n\nProgram");
        System.out.println("  decls: ");
        // Print the symbolTable TODO
        System.out.println("  stmts: ");
        for (AstNode stmt : stmts) {
            stmt.printAST();
        }
    }

}

class Scope extends AstNode {
    List<AstNode> stmts;

    public Scope(List<AstNode> stmts) {
        this.stmts = stmts;
    }

    public String printIntermediateCode() {
        String scope = "";

        for (AstNode stmt : stmts) {
            scope = scope + stmt.printIntermediateCode();
        }
        return scope + "\n";
    }

    public void printAST() {
        System.out.println("Scope");

        System.out.println("  stmts: ");
        for (AstNode stmt : stmts) {
            stmt.printAST();
        }
    }

}

class SetValue extends AstNode {
    String id;
    Expr expr;

    public SetValue(String id, Expr expr) {
        this.id = id;
        this.expr = expr;
    }

    public String printIntermediateCode() {
        String response = "";
        if (expr != null) {
            response = expr.printSetValueIntermediateCode();
        }
        return response;
    }
}

class Assign extends SetValue {

    public Assign(String id, Expr expr) {
        super(id, expr);
        Decl decl = symbolTable.getVar(id);
        // Check if the variable is declared
        if (decl == null) {
            throw new IllegalArgumentException("Error: Variable " + id + " not declared");
        }
        // Check if the variable and the expression are of the same type
        // if (!decl.getType().equals(expr.getType()) &&
        // !(decl.getType().equals(Type.FLOAT) && expr.getType().equals(Type.INT)) &&
        // !(decl.getType().equals(Type.INT) && expr.getType().equals(Type.FLOAT))) {
        // throw new IllegalArgumentException("Error: Variable " + id + " and expression
        // are not of the same type");
        // }
    }

    public void printAST() {
        System.out.println("Assign");
        System.out.println("  id: " + id);
        System.out.println("  expr: ");
        expr.printAST();
    }
}

class Decl extends SetValue {
    private Type type;

    public Decl(Type type, String id) {
        super(id, null);
        this.type = type;
        symbolTable.setVar(this);
    }

    public Decl(Type type, String id, Expr expr) {
        super(id, expr);
        this.type = type;
        symbolTable.setVar(this);
    }

    public String getName() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public void printAST() {
        System.out.println("Decl");
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
        structureCont = tempCont;
        String response = "L" + (structureCont) + ": ";
        structureCont++;
        response = response + "if " + cond.printIntermediateCode() + " GOTO L" + (structureCont) + "\n";
        structureCont++;
        tempCont = structureCont;
        response = response + stmts.printIntermediateCode();
        structureCont -= 2;
        response = response + "GOTO L" + (structureCont) + "\n";
        structureCont++;
        response = response + "L" + (structureCont) + ": \n";
        structureCont--;
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
        structureCont = tempCont;
        String response = "L" + (structureCont) + ": ";
        structureCont++;
        response = response + "if " + cond.printIntermediateCode() + " GOTO L" + (structureCont) + "\n";
        structureCont++;
        tempCont = structureCont;
        response = response + ifStmts.printIntermediateCode();
        structureCont--;
        response = response + "GOTO L" + (structureCont) + "\n";
        response = response + "L" + (structureCont) + ": \n";
        structureCont--;
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
        if (!left.getType().equals(right.getType())
                && !(left.getType().equals(Type.FLOAT) && right.getType().equals(Type.INT))) {
            throw new IllegalArgumentException(
                    "Error: Expressions " + left.getValue() + " and " + right.getValue() + " are not of the same type");
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

    private AstNode left;
    private OpArit op;
    private AstNode right = null;

    public Expr(Atom value) {
        this.left = value;
    }

    public Expr(Expr e1, OpArit OP, Expr e2) {
        this.left = e1;

        this.op = OP;

        this.right = e2;
    }

    public AstNode getLeft() {
        return left;
    }

    public AstNode getRight() {
        return right;
    }

    public String printSetValueIntermediateCode() {
        String response = "";
        String currentSymbol = "t" + (num);
        num++;
        tempSymbolTable.add(this);
        tempEqSymbolTable.add(currentSymbol);
        if (left instanceof Expr)
            response = response + ((Expr) left).printSetValueIntermediateCode();

        if (right != null && right instanceof Expr)
            response = response + ((Expr) right).printSetValueIntermediateCode();
        String leftSymbol = "";
        for (int i = 0; i < tempSymbolTable.size(); i++) {
            if (tempSymbolTable.get(i).equals(this.getLeft())) {
                leftSymbol = tempEqSymbolTable.get(i);
                break;
            }
            leftSymbol = left.printIntermediateCode();
        }

        String rightSymbol = "";
        for (int i = 0; i < tempSymbolTable.size(); i++) {
            if (tempSymbolTable.get(i).equals(this.getRight())) {
                rightSymbol = tempEqSymbolTable.get(i);
                break;
            }
            if (right != null)
                rightSymbol = right.printIntermediateCode();
        }
        String opString = "";
        if (op != null) {
            opString = OpArit.getOp(op);
        }
        response = response + currentSymbol + " = " + leftSymbol + " " + opString + " "
                + rightSymbol + "\n";
        return response;
    }

    public Type getType() {
        if (left.getType().equals(Type.FLOAT)) {
            return Type.FLOAT;
        }
        return Type.INT;
    }

    public AstNode getValue() {
        return left.getValue();
    }

    public String printIntermediateCode() {
        String response = "";
        if (right != null) {

            response = response + left.printIntermediateCode();

            response = response + " " + OpArit.getOp(op) + " ";

            response = response + right.printIntermediateCode();

        } else {
            response = response + left.printIntermediateCode();
        }
        return response;
    }

    public void printAST() {
        System.out.println("Expr");
        System.out.println("  left: " + left.toString());
        System.out.println("  right: " + right.toString());
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

class Atom extends AstNode {
    private Type type;
    private Object value;

    public Atom(Object value, Type type) {
        // Check if the types are the same

        this.type = type;
        this.value = value;
    }

    public Type getType() {
        if (type == Type.IDENT) {
            return symbolTable.getVar(value.toString()).getType();
        }
        return type;
    }

    public String printIntermediateCode() {
        if (type == Type.STRING) {
            return "\"" + value.toString() + "\"";
        }
        if (type == Type.IDENT) {
            return symbolTable.getEquivalent(value.toString());
        }

        return value.toString();
    }

    public void printAST() {
        System.out.println("Atom");
        System.out.println("  value: " + value.toString());
    }
}
