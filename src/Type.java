public enum Type {
    INT,
    FLOAT,
    STRING,
    IDENT;

    static Type getType(int type) {
        System.out.println("Type: " + type);
        switch (type) {
            case 0:
                return INT;
            case 1:
                return FLOAT;
            case 2:
                return STRING;
            case 3:
                return IDENT;
            default:
                return null;

        }
        
    }
}
