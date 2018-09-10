import java.util.HashMap;
import java.util.ArrayList;

public class Interpreter {

    public static class RuntimeError extends Parser.JcalcError {
        public RuntimeError(String m) {
            super(m);
        }
    }

    private HashMap<String, Float> varmap;

    public Interpreter() {
        varmap = new HashMap<String, Float>();
    }

    private float getVal(String varname) throws RuntimeError {
        checkVar(varname);
        return varmap.get(varname);
    }

    private void checkVar(String varname)  throws RuntimeError {
        if (!varmap.containsKey(varname)) {
            throw new RuntimeError("variable not found: " + varname);
        }
    }

    private void movOp(Instr ins) throws RuntimeError {
        if (ins.a == null) {
            throw new RuntimeError("no-null instruction parameter");
        }
        float val;
        if (ins.a.type == Token.Type.NUM) {
            val = Float.parseFloat(ins.a.data);
        } else {
            val = getVal(ins.a.data);
        }
        varmap.put(ins.dest.data, val);
    }

    private void printOp(Instr ins) throws RuntimeError {
        float a = getVal(ins.a.data);
        if (a - (int) a == 0) {
            System.out.println((int) a);
        } else {
            System.out.println(a);
        }
    }

    private void mathOp(Instr ins)  throws RuntimeError {
        float a = getVal(ins.a.data);
        float b = 0;
        if (ins.type != Instr.Type.UNMIN) {
            b = getVal(ins.b.data);    
        }

        float res;
        switch (ins.type) {
            case ADD:
                res = a + b;
                break;
            case SUB:
                res = a - b;
                break;
            case MUL:
                res = a * b;
                break;
            case DIV:
                res = a / b;
                break;
            case UNMIN:
                res = -a;
                break;
            default:
                throw new RuntimeError("unknown math operation");
        }
        varmap.put(ins.dest.data, res);
    }

    // execution of instructions MOV, ADD, SUB, DIV, MUL, UNMIN, PRINT
    public void interpret(ArrayList<Instr> ins_list) throws RuntimeError {
        // System.out.println(ins_list);
        for (Instr ins : ins_list) {
            switch (ins.type) {
                case MOV:
                    movOp(ins);
                    break;
                case ADD:
                case SUB:
                case DIV:
                case MUL:
                case UNMIN:
                    mathOp(ins);
                    break;
                case PRINT:
                    printOp(ins);
                    break;
                default:
                    throw new RuntimeError("unknown instruction");
            }
        }
        // remove all _e* variables
    }
}