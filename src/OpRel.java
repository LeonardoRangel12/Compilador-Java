public enum OpRel {
    LE,
    GT,
    LEQ,
    GTQ,
    EQ,
    NEQ;

    static String getOp(OpRel op){
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
}