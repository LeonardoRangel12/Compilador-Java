public enum OpRel {
    LE,
    GT,
    LEQ,
    GTQ,
    EQ,
    NEQ;

    static String getOp(OpRel op){
        if (op == EQ) {
            return "==";
        }
        if (op == NEQ) {
            return "!=";
        }
        if (op == LE) {
            return "<";
        }
        if (op == GT) {
            return ">";
        }
        if (op == LEQ) {
            return "<=";
        }
        if (op == GTQ) {
            return ">=";
        }
        return "";
    }
}