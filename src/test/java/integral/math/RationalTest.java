package integral.math;

import org.junit.jupiter.api.Test;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class RationalTest {

    @Test
    void reducesAutomatically() {
        Rational r = Rational.of(2, 4);
        assertEquals(BigInteger.ONE, r.numerator());
        assertEquals(BigInteger.TWO, r.denominator());
    }

    @Test
    void denominatorIsAlwaysPositive() {
        Rational r = Rational.of(1, -3);
        assertEquals(BigInteger.valueOf(-1), r.numerator());
        assertEquals(BigInteger.valueOf(3), r.denominator());
    }

    @Test
    void add() {
        assertEquals(Rational.of(1, 2), Rational.of(1, 3).add(Rational.of(1, 6)));
    }

    @Test
    void subtract() {
        assertEquals(Rational.of(1, 6), Rational.of(1, 2).subtract(Rational.of(1, 3)));
    }

    @Test
    void multiply() {
        assertEquals(Rational.of(1, 2), Rational.of(2, 3).multiply(Rational.of(3, 4)));
    }

    @Test
    void divide() {
        assertEquals(Rational.of(8, 9), Rational.of(2, 3).divide(Rational.of(3, 4)));
    }

    @Test
    void divisionByZeroThrows() {
        assertThrows(ArithmeticException.class, () -> Rational.of(1, 0));
        assertThrows(ArithmeticException.class, () -> Rational.of(1, 2).divide(Rational.ZERO));
    }

    @Test
    void toStringFormat() {
        assertEquals("1/3", Rational.of(1, 3).toString());
        assertEquals("-1/3", Rational.of(1, -3).toString());
        assertEquals("5", Rational.of(10, 2).toString());
        assertEquals("0", Rational.of(0, 7).toString());
    }

    @Test
    void equalityUsesReducedForm() {
        assertEquals(Rational.of(1, 2), Rational.of(2, 4));
        assertEquals(Rational.of(1, 2).hashCode(), Rational.of(2, 4).hashCode());
    }
}