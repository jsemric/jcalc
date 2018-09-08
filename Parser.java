// test cases - some java approach
// Occam's razor design
// server

import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;

public class Parser {

    public static class SyntaxError extends Exception {
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
            Lexer lexer = new Lexer();
            PrecedenceParser parser = new PrecedenceParser();
            Interpreter interpreter = new Interpreter();

            // parser.verbose = true;
            Scanner s;
            boolean fromFile = false;

            if (args.length >= 1) {
                fromFile = true;
                s = new Scanner(new File(args[0]));
            }
            else {
                s = new Scanner(System.in);
                String str = "(a+b-2*a)+((a+x)*22)";
                // s = new Scanner(str);
            }

            while (s.hasNext()) {
                try {
                    ArrayList<Token> tokens = lexer.parse_line(s.nextLine());
                    System.out.println(tokens);
                    String dest_name = parse(tokens);
                    System.out.println(dest_name);
                    ArrayList<Instr> ins_list = parser.parse(tokens, dest_name);
                    interpreter.interpret(ins_list);
                }
                catch (Lexer.LexerError e) {
                    System.out.println("lexer error");   
                }
                catch (SyntaxError e) {
                    System.out.println(e.toString());      
                } finally {
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