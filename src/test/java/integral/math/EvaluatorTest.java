package integral.math;

import integral.parser.Parser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluatorTest {

    private static double eval(String input, double x) {
        return Evaluator.evaluate(Parser.parse(input), x);
    }

    @Test
    void basicArithmetic() {
        assertEquals(7.0, eval("2*x+1", 3), 1e-12);
        assertEquals(9.0, eval("x^2", 3), 1e-12);
        assertEquals(1.0 / 3.0, eval("1/3", 0), 1e-12);
        assertEquals(-4.0, eval("-x^2", 2), 1e-12);
    }

    @Test
    void constantsAndFunctions() {
        assertEquals(Math.PI, eval("pi", 0), 1e-12);
        assertEquals(Math.E, eval("e^x", 1), 1e-12);
        assertEquals(Math.sin(1.3), eval("sin(x)", 1.3), 1e-12);
        assertEquals(2.0, eval("sqrt(x)", 4), 1e-12);
        assertEquals(3.0, eval("|x|", -3), 1e-12);
        assertEquals(1.0 / Math.cos(0.5), eval("sec(x)", 0.5), 1e-12);
    }

    @Test
    void undefinedPointsGiveNaN() {
        assertTrue(Double.isNaN(eval("ln(x)", -1)));
        assertTrue(Double.isNaN(eval("1/x", 0)));
        assertTrue(Double.isNaN(eval("sqrt(x)", -4)));
        assertTrue(Double.isNaN(eval("asin(x)", 2)));
    }
}