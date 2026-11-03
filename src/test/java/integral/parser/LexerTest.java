package integral.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static integral.parser.TokenType.*;
import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    private static List<TokenType> types(String input) {
        return Lexer.tokenize(input).stream().map(Token::type).toList();
    }

    @Test
    void emptyInputGivesOnlyEof() {
        assertEquals(List.of(EOF), types(""));
        assertEquals(List.of(EOF), types("   "));
    }

    @Test
    void simpleExpression() {
        assertEquals(
                List.of(NUMBER, STAR, X, CARET, NUMBER, PLUS, NUMBER, EOF),
                types("2*x^2+1"));
    }

    @Test
    void allOperatorsAndParentheses() {
        assertEquals(
                List.of(LPAREN, X, PLUS, NUMBER, RPAREN, MINUS, NUMBER, SLASH, NUMBER, EOF),
                types("(x+1)-2/3"));
    }

    @Test
    void multiDigitNumbers() {
        List<Token> tokens = Lexer.tokenize("123*45");
        assertEquals("123", tokens.get(0).text());
        assertEquals("45", tokens.get(2).text());
    }

    @Test
    void whitespaceIsIgnored() {
        assertEquals(types("2*x^2+1"), types("  2 * x ^ 2 + 1 "));
    }

    @Test
    void constants() {
        assertEquals(List.of(PI, STAR, E, EOF), types("pi*e"));
    }

    @Test
    void positionsAreRecorded() {
        List<Token> tokens = Lexer.tokenize("2 * x");
        assertEquals(0, tokens.get(0).position());
        assertEquals(2, tokens.get(1).position());
        assertEquals(4, tokens.get(2).position());
        assertEquals(5, tokens.get(3).position()); // EOF
    }

    @Test
    void implicitMultiplicationIsTokenizedWithoutErrorHere() {
        // "2x" is rejected later by the parser, not by the lexer
        assertEquals(List.of(NUMBER, X, EOF), types("2x"));
    }

    @Test
    void unexpectedCharacterThrows() {
        ParseException ex = assertThrows(ParseException.class, () -> Lexer.tokenize("2*x$"));
        assertEquals(3, ex.position());
    }

    @Test
    void unknownIdentifierThrows() {
        ParseException ex = assertThrows(ParseException.class, () -> Lexer.tokenize("2*y"));
        assertEquals(2, ex.position());
    }

    @Test
    void decimalPointIsRejected() {
        assertThrows(ParseException.class, () -> Lexer.tokenize("1.5"));
    }

    @Test
    void functionsAndBars() {
        assertEquals(List.of(FUNC, LPAREN, BAR, X, BAR, RPAREN, EOF), types("ln(|x|)"));
        assertEquals("asin", Lexer.tokenize("arcsin").get(0).text());
    }
}