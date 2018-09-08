import java.util.ArrayList;
import java.util.HashMap;

public class PrecedenceParser {
    private ArrayList<Token> stack;
    public boolean verbose;
    private int cnt;

    public PrecedenceParser() {
        verbose = false;
        cnt = 0;
    }

    private String get_next_exp_name() {
        cnt += 1;
        return "_e" + String.valueOf(cnt);
    }

    private String get_last_exp_name() {
        return "_e" + String.valueOf(cnt);
    }
    
    private static char prec_table[][] = new char[][]{
        //0    1    2    3    4    5    6    7
        //+    -    *    /    (    )    e    $
        {'>', '>', '<', '<', '<', '>', '<', '>'}, // + 0
        {'>', '>', '<', '<', '<', '>', '<', '>'}, // - 1
        {'>', '>', '>', '>', '<', '>', '<', '>'}, // * 2
        {'>', '>', '>', '>', '<', '>', '<', '>'}, // / 3
        {'<', '<', '<', '<', '<', '=', '<', 'X'}, // ( 4
        {'>', '>', '>', '>', 'X', '>', 'X', '>'}, // ) 5 
        {'>', '>', '>', '>', 'X', '>', 'X', '>'}, // e 6
        {'<', '<', '<', '<', '<', 'X', '<', 'F'}, // $ 7
    };

    private static HashMap<Token.Type, String> token_symbols = 
        new HashMap<Token.Type, String>()
        {{
            put(Token.Type.PLUS, "+"); put(Token.Type.MINUS, "-");
            put(Token.Type.MUL, "*");  put(Token.Type.DIV, "/");
            put(Token.Type.LBRAC, "(");put(Token.Type.RBRAC, ")");
            put(Token.Type.INT, "i");  put(Token.Type.VAR, "i");
            put(Token.Type.EXP, "E"); put(Token.Type.HANDLE, "<");
            put(Token.Type.TOP, "$");
        }};

    private static HashMap<Token.Type,Integer> op_map = 
        new HashMap<Token.Type,Integer>()
        {{
            put(Token.Type.PLUS, 0); put(Token.Type.MINUS, 1);
            put(Token.Type.MUL, 2);  put(Token.Type.DIV, 3);
            put(Token.Type.LBRAC, 4);put(Token.Type.RBRAC, 5);
            put(Token.Type.INT, 6);  put(Token.Type.VAR, 6);
            put(Token.Type.TOP, 7);
        }};

    private static boolean isBinOp(Token token) {
        Token.Type type = token.type;
        return type == Token.Type.PLUS || type == Token.Type.MUL ||
            type == Token.Type.MINUS || type == Token.Type.DIV;
    }

    private static boolean isValue(Token token) {
        Token.Type type = token.type;
        return type == Token.Type.VAR || type == Token.Type.EXP || 
            type == Token.Type.INT;
    }
    
    private void check_token(Token token) throws Parser.SyntaxError {
        if (!op_map.containsKey(token.type)) {
            throw new Parser.SyntaxError("invalid token " + token);
        }
    }

    private char get_prec(Token last, Token next) {
        int x = op_map.get(last.type);
        int y = op_map.get(next.type);
        // System.out.println(x + " " + y);
        return prec_table[y][x];
    }

    private ArrayList<Token> reduce() {
        ArrayList<Token> tokens = new ArrayList<Token>();
        int last_idx = stack.size() - 1;
        while (!stack.isEmpty() && stack.get(last_idx).type != Token.Type.HANDLE) {
            tokens.add(0, stack.get(last_idx));
            stack.remove(last_idx);
            last_idx--;
        }

        // remove prec handle
        if (!stack.isEmpty()) {
            stack.remove(last_idx);
        }
        return tokens;
    }

    // return the position of the last terminal symbol (token)
    private int get_last_terminal() {
        int i = stack.size() - 1;
        while (i > 0 && stack.get(i).type == Token.Type.EXP) {
            i--;
        }
        return i;
    }

    // add precedence handle
    private void add_handle() {
        int i = get_last_terminal();
        stack.add(i + 1, Token.make_token(Token.Type.HANDLE));
    }

    private Instr apply_rules(ArrayList<Token> tokens, Token dest) 
        throws Parser.SyntaxError
    {
        Instr ret = null;
        int len = tokens.size();
        if (len == 3) {
            Token t0 = tokens.get(0);
            Token t1 = tokens.get(1);
            Token t2 = tokens.get(2);
            if (isBinOp(t1) && isValue(t0) && isValue(t2)) {
                // E [+-*/] E -> E
                ret = new Instr(dest, Instr.token_ins_map.get(t1.type));
                ret.a = t0;
                ret.a = t2;
            } else if (t0.type == Token.Type.LBRAC && isValue(t1) &&
                t2.type == Token.Type.RBRAC)
            {
                // (E) -> E
                ret = new Instr(dest, Instr.Type.MOV);
                ret.a = t1;
            } else {
                throw new Parser.SyntaxError("invalid syntax");
            }
        } else if (len == 2) {
            Token t0 = tokens.get(0);
            Token t1 = tokens.get(1);
            if ((t0.type == Token.Type.MINUS || t0.type == Token.Type.PLUS)
                && isValue(t1))
            {
                if (t0.type == Token.Type.MINUS) {
                    // unary plus
                    ret = new Instr(dest, Instr.Type.UNMIN);
                } else {
                    // unary plus
                    ret = new Instr(dest, Instr.Type.MOV);
                }
                ret.a = t1;
            } else {
                throw new Parser.SyntaxError(
                    "invalid syntax of unary operation");
            }
        } else if (len == 1) {
            // var -> E
            ret = new Instr(dest, Instr.Type.MOV);
            ret.a = tokens.get(0);
        } else {
            throw new Parser.SyntaxError("none or too many operands");
        }
        return ret;
    }

    public void print_stack() {
        if (verbose) {
            String str = new String();
            for (Token i : stack) {
                str += token_symbols.get(i.type);
            }
            System.out.println(str);
        }
    }

    public ArrayList<Instr> parse(ArrayList<Token> tokens, String dest_name)
        throws Parser.SyntaxError
    {
        if (verbose)
            System.out.println(tokens);

        cnt = 0;
        ArrayList<Instr> ins_list = new ArrayList<Instr>();
        stack = new ArrayList<Token>();
        stack.add(Token.make_token(Token.Type.TOP));
        tokens.add(Token.make_token(Token.Type.TOP));
        print_stack();

        while (!tokens.isEmpty()) {
            Token next = tokens.get(0);
            Token last = stack.get(get_last_terminal());
            check_token(next); check_token(last);
            char prec = get_prec(next, last);

            if (verbose)
                System.out.println(prec + " | " + token_symbols.get(next.type));

            if (prec == '>' ) {
                // reduce
                ArrayList<Token> to_execute = reduce();
                Token exp = new Token(Token.Type.EXP, get_next_exp_name());
                stack.add(exp);
                // apply rules
                Instr ins = apply_rules(to_execute, exp);
                ins_list.add(ins);
            } else if (prec == '<' || prec == '=') {
                // shift
                if  (prec != '=') {
                    add_handle();
                }
                tokens.remove(0); // next token
                stack.add(next);
            } else if (prec == 'F') {
                // check stack, $E$ expected
                boolean cond = stack.size() == 2 &&
                    stack.get(0).type == Token.Type.TOP &&
                    stack.get(1).type == Token.Type.EXP;
                if (!cond) {
                    throw new Parser.SyntaxError("invalid final symbol");
                }
                // end while loop
                break;
            } else if (prec == 'X') {
                throw new Parser.SyntaxError("invalid precedence syntax");
            }
            print_stack();
            if (verbose) System.out.println("===========");
        }

        Token exp = new Token(Token.Type.EXP, get_last_exp_name());
        Instr ins;
        if (dest_name != null && !dest_name.equals("")) {
            // store instruction
            ins = new Instr(new Token(Token.Type.VAR, dest_name),
                Instr.Type.MOV);
        } else {
            // just print the result
            ins = new Instr(Token.make_token(Token.Type.EMPTY),
                Instr.Type.PRINT);
        }
        ins.a = exp;
        ins_list.add(ins);

        return ins_list;
    }
}