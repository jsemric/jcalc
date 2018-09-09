import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;

public class Parser {
    private Lexer lexer;
    private PrecedenceParser parser;
    private Interpreter interpreter;

    public Parser() {
        lexer = new Lexer();
        parser = new PrecedenceParser();
        interpreter = new Interpreter();
    }

    public void parseLine(String line) throws JcalcError {
        ArrayList<Token> tokens = lexer.parse_line(line);
        System.out.println(tokens);
        String dest_name = parse(tokens);
        System.out.println(dest_name);
        ArrayList<Instr> ins_list = parser.parse(tokens, dest_name);
        interpreter.interpret(ins_list);
    }

    public static class JcalcError extends Exception {
        public JcalcError(String m) {
            super(m);
        }
    }

    public static class SyntaxError extends JcalcError {
        public SyntaxError(String m) {
            super(m);
        }
    }

    public static String parse(ArrayList<Token> tokens) {
        String dest = null;
        if (tokens.size() >= 2 && tokens.get(1).type == Token.Type.ASSIGN) {
            if (tokens.get(0).type != Token.Type.VAR) {
                // error
            }
            dest = new String(tokens.get(0).data);
            // remove first two tokens: <VAR> <ASSIGN>
            tokens.remove(0);
            tokens.remove(0);
        }
        return dest;
    }

    public static void main(String args[]) {
        System.out.println("Running Parser");

        try {
            Parser parser = new Parser();

            // parser.verbose = true;
            Scanner s;
            boolean fromFile = false;

            if (args.length >= 1) {
                fromFile = true;
                s = new Scanner(new File(args[0]));
            }
            else {
                s = new Scanner(System.in);
            }

            while (s.hasNext()) {
                try {
                    parser.parseLine(s.nextLine());
                }
                catch (JcalcError e) {
                    System.out.println(e.toString()); 
                } finally {
                    // break while loop if input is a file
                    if (fromFile) {
                        break;
                    }
                }
            }
        } 
        catch (FileNotFoundException e) {
            System.out.println("file not found");
        }
        

        System.out.println("Parser Finished");
    }
}