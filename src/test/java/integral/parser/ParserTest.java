package integral.parser;

import integral.ast.FuncKind;
import integral.ast.Node;
import integral.ast.Printer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static integral.ast.Node.*;
import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private static Node parse(String input) {
        return Parser.parse(input);
    }

    private static void assertErrorAt(String input, int position) {
        ParseException ex = assertThrows(ParseException.class, () -> Parser.parse(input), input);
        assertEquals(position, ex.position(), input);
    }

    @Test
    void atoms() {
        assertEquals(x(), parse("x"));
        assertEquals(num(2), parse("2"));
        assertEquals(new Const(ConstKind.PI), parse("pi"));
        assertEquals(new Const(ConstKind.E), parse("e"));
    }

    @Test
    void precedenceOfMulOverAdd() {
        assertEquals(new Add(num(1), new Mul(num(2), num(3))), parse("1+2*3"));
        assertEquals(new Mul(new Add(num(1), num(2)), num(3)), parse("(1+2)*3"));
    }

    @Test
    void fullExpression() {
        assertEquals(
                new Add(new Mul(num(2), new Pow(x(), num(2))), num(1)),
                parse("2*x^2+1"));
    }

    @Test
    void addAndMulAreLeftAssociative() {
        assertEquals(new Add(new Add(num(1), num(2)), num(3)), parse("1+2+3"));
        assertEquals(new Mul(new Mul(x(), num(2)), num(3)), parse("x*2*3"));
    }

    @Test
    void powerIsRightAssociative() {
        assertEquals(new Pow(num(2), new Pow(num(3), num(2))), parse("2^3^2"));
    }

    @Test
    void subtractionBecomesAddOfNegative() {
        assertEquals(new Add(x(), num(-1)), parse("x-1"));
        assertEquals(new Add(new Add(x(), num(-1)), num(-2)), parse("x-1-2"));
        assertEquals(new Add(x(), new Neg(x())), parse("x-x"));
    }

    @Test
    void unaryMinus() {
        assertEquals(num(-2), parse("-2"));
        assertEquals(new Neg(x()), parse("-x"));
        assertEquals(new Neg(new Pow(x(), num(2))), parse("-x^2"));
        assertEquals(new Neg(new Pow(num(2), num(2))), parse("-2^2"));
        assertEquals(new Mul(new Neg(x()), num(2)), parse("-x*2"));
        assertEquals(new Mul(num(2), new Neg(x())), parse("2*-x"));
        assertEquals(new Neg(new Neg(x())), parse("--x"));
    }

    @Test
    void negativeExponent() {
        assertEquals(new Pow(x(), num(-1)), parse("x^-1"));
        assertEquals(new Pow(x(), num(-1)), parse("x^(-1)"));
    }

    @Test
    void divisionByNumberBecomesRational() {
        assertEquals(num(1, 3), parse("1/3"));
        assertEquals(num(1, 2), parse("2/4"));
        assertEquals(num(2), parse("6/3"));
        assertEquals(new Mul(x(), num(1, 3)), parse("x/3"));
        assertEquals(new Mul(num(2, 3), x()), parse("2/3*x"));
        assertEquals(new Mul(x(), num(2)), parse("x/(1/2)"));
    }

    @Test
    void divisionByExpressionBecomesNegativePower() {
        assertEquals(new Mul(num(1), new Pow(x(), num(-1))), parse("1/x"));
        assertEquals(
                new Mul(num(1), new Pow(new Add(x(), num(1)), num(-1))),
                parse("1/(x+1)"));
    }

    @Test
    void whitespaceIsIgnored() {
        assertEquals(parse("2*x^2+1"), parse("  2 * x ^ 2 + 1 "));
    }

    @Test
    void functions() {
        assertEquals(new Func(FuncKind.SIN, x()), parse("sin(x)"));
        assertEquals(new Func(FuncKind.ASIN, x()), parse("arcsin(x)"));
        assertEquals(new Func(FuncKind.LN, new Add(x(), num(1))), parse("ln(x+1)"));
        assertEquals(new Pow(x(), num(1, 2)), parse("sqrt(x)"));
        assertEquals(new Pow(new Func(FuncKind.SIN, x()), num(2)), parse("sin(x)^2"));
    }

    @Test
    void absoluteValueBars() {
        assertEquals(new Func(FuncKind.ABS, x()), parse("|x|"));
        assertEquals(new Func(FuncKind.ABS, new Add(x(), num(1))), parse("|x+1|"));
        assertEquals(
                new Mul(new Func(FuncKind.ABS, x()), new Func(FuncKind.ABS, new Add(x(), num(1)))),
                parse("|x|*|x+1|"));
        assertEquals(
                new Func(FuncKind.ABS, new Add(new Func(FuncKind.ABS, x()), num(1))),
                parse("||x|+1|"));
        assertEquals(new Func(FuncKind.LN, new Func(FuncKind.ABS, x())), parse("ln(|x|)"));
    }

    @Test
    void errorPositions() {
        assertErrorAt("2x", 1);            // missing '*'
        assertErrorAt("2 x", 2);
        assertErrorAt("(x+1)(x-1)", 5);   // missing '*'
        assertErrorAt("", 0);             // unexpected end
        assertErrorAt("x+", 2);
        assertErrorAt("x^", 2);
        assertErrorAt("(x+1", 4);         // missing ')'
        assertErrorAt("x+1)", 3);         // extra ')'
        assertErrorAt("*x", 0);
        assertErrorAt("()", 1);
        assertErrorAt("x/0", 1);          // division by zero
    }

    @Test
    void functionAndBarErrors() {
        assertErrorAt("sin x", 4);      // missing '('
        assertErrorAt("sin(x", 5);      // missing ')'
        assertErrorAt("2sin(x)", 1);    // missing '*'
        assertErrorAt("abs(x)", 0);     // abs is written |x|
        assertErrorAt("|x", 2);         // unclosed bar
        assertErrorAt("x|", 1);         // stray bar
        assertErrorAt("|x||x|", 3);     // ambiguous, needs '*'
    }

    @Test
    void roundTripWithPrinter() {
        List<String> inputs = List.of(
                "2*x^2+1", "x+1+2", "x+(1+2)", "(x+1)*2", "(x+1)^2",
                "-x^2", "(-x)^2", "-(x+1)", "2^3^2", "(2^3)^2",
                "1/3", "(1/3)*x", "2*-x", "x^(-1)", "x+(-1)", "(-2)^x",
                "sin(x)^2", "|x|", "||x|+1|", "|x|*|x+1|", "ln(|x|)", "2*sin(x)");
        for (String input : inputs) {
            assertEquals(input, Printer.print(parse(input)), input);
        }
    }
}