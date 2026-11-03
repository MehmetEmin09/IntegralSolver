package integral.ast;

import org.junit.jupiter.api.Test;

import static integral.ast.Node.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PrinterTest {

    @Test
    void atoms() {
        assertEquals("x", Printer.print(x()));
        assertEquals("2", Printer.print(num(2)));
        assertEquals("1/3", Printer.print(num(1, 3)));
        assertEquals("pi", Printer.print(new Const(ConstKind.PI)));
        assertEquals("e", Printer.print(new Const(ConstKind.E)));
    }

    @Test
    void simpleOperations() {
        assertEquals("x+1", Printer.print(new Add(x(), num(1))));
        assertEquals("2*x", Printer.print(new Mul(num(2), x())));
        assertEquals("x^2", Printer.print(new Pow(x(), num(2))));
        assertEquals("-x", Printer.print(new Neg(x())));
    }

    @Test
    void lowerPrecedenceInsideMulGetsParentheses() {
        assertEquals("(x+1)*2", Printer.print(new Mul(new Add(x(), num(1)), num(2))));
    }

    @Test
    void higherPrecedenceInsideAddNeedsNoParentheses() {
        assertEquals("2*x^2+1",
                Printer.print(new Add(new Mul(num(2), new Pow(x(), num(2))), num(1))));
    }

    @Test
    void powerBaseGetsParentheses() {
        assertEquals("(x+1)^2", Printer.print(new Pow(new Add(x(), num(1)), num(2))));
        assertEquals("(2*x)^2", Printer.print(new Pow(new Mul(num(2), x()), num(2))));
        assertEquals("(-x)^2", Printer.print(new Pow(new Neg(x()), num(2))));
        assertEquals("(x^2)^3", Printer.print(new Pow(new Pow(x(), num(2)), num(3))));
    }

    @Test
    void negativeOfPowerNeedsNoParentheses() {
        assertEquals("-x^2", Printer.print(new Neg(new Pow(x(), num(2)))));
    }

    @Test
    void negativeInsideAddAndMul() {
        assertEquals("x+-1", Printer.print(new Add(x(), new Neg(num(1)))));
        assertEquals("2*-x", Printer.print(new Mul(num(2), new Neg(x()))));
    }

    @Test
    void negativeAndFractionalNumbersGetParenthesesAsPowerBase() {
        assertEquals("(-2)^x", Printer.print(new Pow(num(-2), x())));
        assertEquals("(1/2)^x", Printer.print(new Pow(num(1, 2), x())));
    }

    @Test
    void fractionInsideMulGetsParentheses() {
        assertEquals("(1/3)*x", Printer.print(new Mul(num(1, 3), x())));
    }

    @Test
    void addIsPrintedWithoutExtraParenthesesWhenNested() {
        assertEquals("x+1+2", Printer.print(new Add(new Add(x(), num(1)), num(2))));
        assertEquals("x+(1+2)", Printer.print(new Add(x(), new Add(num(1), num(2)))));
    }
}