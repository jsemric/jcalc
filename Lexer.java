import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Lexer {
    private Pattern token_patterns;

    public class LexerError extends Parser.JcalcError {
        public LexerError() {
            super("lexer error");
        }
    }

    public Lexer() {
        StringBuffer token_patterns_buff = new StringBuffer();
        for (Token.Type tt : Token.Type.values()) {
            String str = String.format("|(?<%s>%s)", tt.name(), tt.pattern);
            token_patterns_buff.append(str);
        }

        String tmp = new String(token_patterns_buff.substring(1));
        token_patterns = Pattern.compile(tmp);
        // debug
        // System.out.println(token_patterns_buff);
    }

    public ArrayList<Token> parse_line(String input) throws LexerError {
        ArrayList<Token> tokens = new ArrayList<Token>();
        input = input.replace("\n", "");
        Matcher matcher = token_patterns.matcher(input);

        while (matcher.find()) {
            for (Token.Type tt : Token.Type.values()) {
                String tmp = matcher.group(tt.name());
                if(tmp != null) {
                    if (tt == Token.Type.SENT) {
                        throw new LexerError();
                    }
                    else if (tt == Token.Type.WSPACE) {
                        
                    } else {
                        tokens.add(new Token(tt, tmp));
                    }
                    break;
                }
            }
        }

        return tokens;
    }
}