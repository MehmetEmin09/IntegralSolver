package integral.ast;

import integral.math.Rational;

public sealed interface Node permits
        Node.Num, Node.Var, Node.Const,
        Node.Add, Node.Mul, Node.Pow, Node.Neg {

    static Node num(long value) {
        return new Num(Rational.of(value, 1));
    }

    static Node num(long numerator, long denominator) {
        return new Num(Rational.of(numerator, denominator));
    }

    static Node x() {
        return new Var();
    }

    // A rational number such as 2 or 1/3
    record Num(Rational value) implements Node {}

    // The integration variable x
    record Var() implements Node {}

    // Named constants
    enum ConstKind { PI, E }
    record Const(ConstKind kind) implements Node {}

    record Add(Node left, Node right) implements Node {}

    record Mul(Node left, Node right) implements Node {}

    // base ^ exponent
    record Pow(Node base, Node exponent) implements Node {}

    // unary minus
    record Neg(Node operand) implements Node {}
}