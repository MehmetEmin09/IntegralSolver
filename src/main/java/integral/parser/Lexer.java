package integral.parser;

import java.util.ArrayList;
import java.util.List;

public final class Lexer {

    private Lexer() {}

    public static List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (isDigit(c)) {
                int start = i;
                while (i < input.length() && isDigit(input.charAt(i))) {
                    i++;
                }
                tokens.add(new Token(TokenType.NUMBER, input.substring(start, i), start));
                continue;
            }

            if (isLetter(c)) {
                int start = i;
                while (i < input.length() && isLetter(input.charAt(i))) {
                    i++;
                }
                String word = input.substring(start, i);
                TokenType type = switch (word) {
                    case "x" -> TokenType.X;
                    case "pi" -> TokenType.PI;
                    case "e" -> TokenType.E;
                    default -> throw new ParseException("Unknown identifier '" + word + "'", start);
                };
                tokens.add(new Token(type, word, start));
                continue;
            }

            TokenType type = switch (c) {
                case '+' -> TokenType.PLUS;
                case '-' -> TokenType.MINUS;
                case '*' -> TokenType.STAR;
                case '/' -> TokenType.SLASH;
                case '^' -> TokenType.CARET;
                case '(' -> TokenType.LPAREN;
                case ')' -> TokenType.RPAREN;
                default -> throw new ParseException("Unexpected character '" + c + "'", i);
            };
            tokens.add(new Token(type, String.valueOf(c), i));
            i++;
        }

        tokens.add(new Token(TokenType.EOF, "", input.length()));
        return tokens;
    }

    // ASCII only, so Unicode digits and letters are rejected
    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
}