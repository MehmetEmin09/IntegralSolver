package integral.ast;

import integral.math.Rational;
import org.junit.jupiter.api.Test;

import static integral.ast.Node.*;
import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    @Test
    void sameStructureIsEqual() {
        Node a = new Add(new Mul(num(2), new Pow(x(), num(2))), num(1));
        Node b = new Add(new Mul(num(2), new Pow(x(), num(2))), num(1));
        assertEquals(a, b);
    }

    @Test
    void differentStructureIsNotEqual() {
        Node a = new Add(x(), num(1));
        Node b = new Add(num(1), x());
        assertNotEquals(a, b);
    }

    @Test
    void numHoldsReducedRational() {
        assertEquals(num(1, 2), num(2, 4));
    }

    @Test
    void constantsAreComparedByKind() {
        assertEquals(new Const(ConstKind.PI), new Const(ConstKind.PI));
        assertNotEquals(new Const(ConstKind.PI), new Const(ConstKind.E));
    }

    @Test
    void patternMatchingWorks() {
        Node n = new Neg(x());
        String kind = switch (n) {
            case Num num -> "num";
            case Var v -> "var";
            case Const c -> "const";
            case Add a -> "add";
            case Mul m -> "mul";
            case Pow p -> "pow";
            case Neg ng -> "neg";
        };
        assertEquals("neg", kind);
    }
}