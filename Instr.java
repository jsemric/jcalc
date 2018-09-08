import java.util.HashMap;

public class Instr {
    public static enum Type {
        MOV, ADD, SUB, DIV, MUL, UNMIN, PRINT
    }

    public static HashMap<Token.Type,Type> token_ins_map = 
        new HashMap<Token.Type,Type>()
        {{
            put(Token.Type.PLUS, Type.ADD); put(Token.Type.MINUS, Type.SUB);
            put(Token.Type.MUL, Type.MUL);  put(Token.Type.DIV, Type.DIV);
        }};

    public Token a, b;
    public Token dest; // destination of the result
    public Type type;

    // dest = a (type) b
    // dest = (type) a
    public Instr(Token dest, Type type) {
        a = Token.make_token(Token.Type.EMPTY);
        b = Token.make_token(Token.Type.EMPTY);
        this.dest = dest;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("(%s %s %s %s)", type.name(), dest.data,
            a.type.name(), b.type.name());
    }

}