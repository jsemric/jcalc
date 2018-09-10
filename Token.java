public class Token {
    public static enum Type {
        NUM("[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?"),
        // NUM("-?[0-9]+"),
        PLUS("\\+"), MINUS("-"), MUL("\\*"), DIV("/"),
        WSPACE("\\s+"), LBRAC("\\("), RBRAC("\\)"), ASSIGN("="),
        VAR("[A-Za-z_][A-Za-z_0-9]*"),
        SENT(".+"), // sentinel
        // not used by lexer
        EXP("EXP"), TOP("TOP"), HANDLE("HANDLE"), EMPTY("EMPTY");

        public final String pattern;
        private Type(String pattern) {
            this.pattern = pattern;
        }
    }

    public Type type;
    public String data;

    public Token(Type type, String data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("(%s %s)", type.name(), data);
    }

    static public Token make_token(Token.Type ttype) {
        return new Token(ttype, "");
    }
}