import java.util.HashMap;
import java.util.ArrayList;

public class Interpreter {
    private HashMap<String, Float> varmap;

    public Interpreter() {
        varmap = new HashMap<String, Float>();
    }

    public void interpret(ArrayList<Instr> ins_list) {
        System.out.println(ins_list);
    }
}