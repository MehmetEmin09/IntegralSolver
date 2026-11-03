package integral.parser;

import integral.ast.Node.*;
import integral.ast.Node;
import integral.math.Rational;

import java.math.BigInteger;
import java.util.List;

import static integral.ast.Node.*;
import static integral.parser.TokenType.*;
import integral.ast.FuncKind;

public final class Parser {

    private final List<Token> tokens;
    private int index = 0;

    private Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public static Node parse(String input) {
        Parser parser = new Parser(Lexer.tokenize(input));
        Node result = parser.parseExpression();
        Token next = parser.peek();
        if (next.type() != EOF) {
            throw new ParseException("Unexpected token '" + next.text() + "'", next.position());
        }
        return result;
    }

    // expression := term (('+' | '-') term)*
    private Node parseExpression() {
        Node left = parseTerm();
        while (peek().type() == PLUS || peek().type() == MINUS) {
            Token op = next();
            Node right = parseTerm();
            left = (op.type() == PLUS) ? new Add(left, right) : new Add(left, negate(right));
        }
        return left;
    }

    // term := unary (('*' | '/') unary)*
    private Node parseTerm() {
        Node left = parseUnary();
        while (true) {
            Token t = peek();
            if (t.type() == STAR) {
                next();
                left = new Mul(left, parseUnary());
            } else if (t.type() == SLASH) {
                next();
                left = divide(left, parseUnary(), t);
            } else if (startsPrimary(t.type())) {
                throw new ParseException(
                        "Missing '*' before '" + t.text() + "' (implicit multiplication is not supported)",
                        t.position());
            } else {
                return left;
            }
        }
    }

    // unary := '-' unary | power
    private Node parseUnary() {
        if (peek().type() == MINUS) {
            next();
            return negate(parseUnary());
        }
        return parsePower();
    }

    // power := primary ('^' exponent)?   (right-associative)
    private Node parsePower() {
        Node base = parsePrimary();
        if (peek().type() == CARET) {
            next();
            return new Pow(base, parseExponent());
        }
        return base;
    }

    // exponent := '-' exponent | power
    private Node parseExponent() {
        if (peek().type() == MINUS) {
            next();
            return negate(parseExponent());
        }
        return parsePower();
    }

    // primary := NUMBER | x | pi | e | '(' expression ')'
    private Node parsePrimary() {
        Token t = next();
        return switch (t.type()) {
            case NUMBER -> new Num(Rational.of(new BigInteger(t.text()), BigInteger.ONE));
            case X -> new Var();
            case PI -> new Const(ConstKind.PI);
            case E -> new Const(ConstKind.E);
            case FUNC -> {
                expect(LPAREN, "'(' after function name '" + t.text() + "'");
                Node arg = parseExpression();
                expect(RPAREN, "')'");
                yield t.text().equals("sqrt")
                        ? new Pow(arg, num(1, 2))
                        : new Func(FuncKind.fromName(t.text()), arg);
            }
            case BAR -> {
                Node inner = parseExpression();
                expect(BAR, "closing '|'");
                yield new Func(FuncKind.ABS, inner);
            }
            case LPAREN -> {
                Node inner = parseExpression();
                expect(RPAREN, "')'");
                yield inner;
            }
            case EOF -> throw new ParseException("Unexpected end of input", t.position());
            default -> throw new ParseException("Unexpected token '" + t.text() + "'", t.position());
        };
    }

    // Negating a number of literal folds into the number; anything else becomes Neg.
    private static Node negate(Node node) {
        if (node instanceof Num n) {
            return new Num(n.value().multiply(Rational.of(-1, 1)));
        }
        return new Neg(node);
    }

    private static Node divide(Node left, Node right, Token slash) {
        if (right instanceof Num n) {
            if (n.value().numerator().signum() == 0) {
                throw new ParseException("Division by zero", slash.position());
            }
            Rational inverse = Rational.ONE.divide(n.value());
            if (left instanceof Num l) {
                return new Num(l.value().multiply(inverse));
            }
            return new Mul(left, new Num(inverse));
        }
        return new Mul(left, new Pow(right, num(-1)));
    }

    private static boolean startsPrimary(TokenType type) {
        return type == NUMBER || type == X || type == PI || type == E
                || type == FUNC || type == LPAREN;
    }

    private Token peek() {
        return tokens.get(index);
    }

    // Never moves past EOF
    private Token next() {
        Token t = tokens.get(index);
        if (t.type() != EOF) {
            index++;
        }
        return t;
    }

    private void expect(TokenType type, String description) {
        Token t = peek();
        if (t.type() != type) {
            throw new ParseException("Expected " + description, t.position());
        }
        next();
    }
}