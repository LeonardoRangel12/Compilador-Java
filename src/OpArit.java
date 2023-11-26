public enum OpArit {
    PLUS,
    MINUS,
    MULT,
    DIV,
    MOD;

    static String getOp(OpArit op){
        switch (op) {
            case PLUS:
                return "+";
            case MINUS:
                return "-";
            case MULT:
                return "*";
            case DIV:
                return "/";
            case MOD:
                return "%";
            default:
                return null;
        }
    }
}
