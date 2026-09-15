package ch.tbz.calculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    Calculator calc = new Calculator();

    @Test
    void add() {
        assertEquals(5.0, calc.add(2, 3));

    }

    @Test
    void subtract() {
        assertEquals(5.0, calc.subtract(10, 5));
    }

    @Test
    void multiply() {
        assertEquals(5.0, calc.multiply(1, 5));
    }

    @Test
    void divide() {
        assertEquals(5.0, calc.divide(10, 2));
    }

    @Test
    void testDivideByZero() {
        assertEquals(Double.POSITIVE_INFINITY, calc.divide(1, 0));
    }
}