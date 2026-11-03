package integral.ast;

import java.math.BigInteger;

import static integral.ast.Node.*;

public final class Printer {

    private static final int ADD = 1;
    private static final int MUL = 2;
    private static final int NEG = 3;
    private static final int POW = 4;
    private static final int ATOM = 5;

    // Negative or fractional numbers: lowest precedence, so they get parentheses
    // whenever they are nested inside any operator, but print plainly at top level.
    private static final int SIGNED_OR_FRACTION = 0;

    private Printer() {}

    public static String print(Node node) {
        return print(node, 0);
    }

    // parentPrecedence: how strongly the surrounding operator binds.
    // If this node's own precedence is lower, it is wrapped in parentheses.
    private static String print(Node node, int parentPrecedence) {
        String text = switch (node) {
            case Num n -> n.value().toString();
            case Var v -> "x";
            case Const c -> switch (c.kind()) {
                case PI -> "pi";
                case E -> "e";
            };
            // Left-associative: the right child needs a strictly higher level
            case Add a -> print(a.left(), ADD) + "+" + print(a.right(), ADD + 1);
            case Mul m -> print(m.left(), MUL) + "*" + print(m.right(), MUL + 1);
            // Base must be an atom; exponent may itself be a Pow (right-associative)
            case Pow p -> print(p.base(), ATOM) + "^" + print(p.exponent(), POW);
            case Neg ng -> "-" + print(ng.operand(), NEG);
            case Func f -> f.kind() == FuncKind.ABS
                    ? "|" + print(f.arg(), 0) + "|"
                    : f.kind().text() + "(" + print(f.arg(), 0) + ")";
        };

        if (precedence(node) < parentPrecedence) {
            return "(" + text + ")";
        }
        return text;
    }

    private static int precedence(Node node) {
        return switch (node) {
            case Num n -> isSignedOrFraction(n) ? SIGNED_OR_FRACTION : ATOM;
            case Var v -> ATOM;
            case Const c -> ATOM;
            case Add a -> ADD;
            case Mul m -> MUL;
            case Neg ng -> NEG;
            case Pow p -> POW;
            case Func f -> ATOM;
        };
    }

    private static boolean isSignedOrFraction(Num n) {
        return n.value().numerator().signum() < 0
                || !n.value().denominator().equals(BigInteger.ONE);
    }
}