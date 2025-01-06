package assignments.ex2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class test_cell {
    @Test
    void testValidFormulas() {
        Cell1 cell = new Cell1("foo");

        // Positive tests
        assertTrue(cell.isForm("=(1+2)"));
        assertTrue(cell.isForm("=(a0)*2"));
        assertTrue(cell.isForm("=(((3)))"));
        assertTrue(cell.isForm("=(3)*(2)"));
        assertTrue(cell.isForm("=(1+2)*3"));
        assertTrue(cell.isForm("=(1+2)*3"));
        assertTrue(cell.isForm("=3+(9*8)+2"));
        assertTrue(cell.isForm("=(1+((2*3)-(4/5)))"));
        assertTrue(cell.isForm("=((1+2)*(3+4))"));
        assertTrue(cell.isForm("=(A1+b2)*C3"));
        assertTrue(cell.isForm("=123"));
        assertTrue(cell.isForm("=a1"));
        assertTrue(cell.isForm("=0"));
        assertTrue(cell.isForm("=-1.23"));
        assertTrue(cell.isForm("=A1"));
        assertTrue(cell.isForm("=a99"));
        assertTrue(cell.isForm("=A1+B2"));
        assertTrue(cell.isForm("=1+2"));
        assertTrue(cell.isForm("=1-2"));
        assertTrue(cell.isForm("=(1+2)"));
        assertTrue(cell.isForm("=(1+2)*(3+4)"));
        assertTrue(cell.isForm("=((1+2)*3)*(4/2)"));
        assertTrue(cell.isForm("=(1)*(2)*(3)"));
        assertTrue(cell.isForm("=(A1)*(B2)"));
        assertTrue(cell.isForm("=((A1+B2)*C3)*(D4)"));
        assertTrue(cell.isForm("=A1+B2*C3"));
        assertTrue(cell.isForm("=(A1+B2)*(C3+D4)"));
        assertTrue(cell.isForm("=A1+A2+A3+A4"));
        assertTrue(cell.isForm("=(A1)*(B2)+C3"));
        assertTrue(cell.isForm("=(A1+(B2*C3))/D4"));
        assertTrue(cell.isForm("=A1+B2/3*C4-5"));
        assertTrue(cell.isForm("=123456789"));
        assertTrue(cell.isForm("=-123456.789"));
        assertTrue(cell.isForm("=(123456.789)"));
        assertTrue(cell.isForm("=(-123.45)*(678.90)"));
        assertTrue(cell.isForm("=(1+2)*(3-4)/(5+6)"));
        assertTrue(cell.isForm("=(1-2+3*4/5)"));
        assertTrue(cell.isForm("=(1+2)*(3/4)-(5*6)"));
        assertTrue(cell.isForm("=((1*2)+(3/4))-5"));
        assertTrue(cell.isForm("=1+2*3-4/5"));
        assertTrue(cell.isForm("=(A1+(B2*C3-(D4/E5)))"));
        assertTrue(cell.isForm("=(A1+(B2*(C3-(D4/E5))))"));
        assertTrue(cell.isForm("=(((A1+B2)*(C3/D4))-E5)"));
        assertTrue(cell.isForm("=((A1+B2)*(C3+D4))/(E5)"));
        assertTrue(cell.isNumber("5"));
        assertTrue(cell.isNumber("500000000000000000"));
        assertTrue(cell.isNumber("-22"));
        assertTrue(cell.isNumber("2999.9292"));
        assertTrue(cell.isNumber("-234.233"));
        assertTrue(cell.isNumber("0"));
        assertTrue(cell.isNumber("+10"));
        assertTrue(cell.isNumber("+10.344"));
        assertTrue(cell.isText("nffbfr"));
        assertTrue(cell.isText("AvItAl"));
        assertTrue(cell.isText("avital325"));
        assertTrue(cell.isText("AA1"));
        assertTrue(cell.isText("ZA"));
        assertTrue(cell.isText("Hello World"));



    }

    @Test
    void testInvalidFormulas() {
        Cell1 cell = new Cell1("foo");

        // Negative tests
        assertFalse(cell.isForm(""));
        assertFalse(cell.isForm("=(2+"));
        assertFalse(cell.isForm("=3+(9*8"));
        assertFalse(cell.isForm(""));
        assertFalse(cell.isForm("="));
        assertFalse(cell.isForm("=()"));
        assertFalse(cell.isForm("=)1+2("));
        assertFalse(cell.isForm("=1+2)"));
        assertFalse(cell.isForm("=(1+2"));
        assertFalse(cell.isForm("=(A1+)"));
        assertFalse(cell.isForm("=A1+B2)"));
        assertFalse(cell.isForm("=1+(2*3"));
        assertFalse(cell.isForm("=1+*2"));
        assertFalse(cell.isForm("=(1+2)+"));
        assertFalse(cell.isForm("=A"));
        assertFalse(cell.isForm("=1A"));
        assertFalse(cell.isForm("=AA1"));
        assertFalse(cell.isForm("=A-1"));
        assertFalse(cell.isForm("=A1.2"));
        assertFalse(cell.isForm("=Z100"));
        assertFalse(cell.isNumber(""));
        assertFalse(cell.isNumber("BGVGV"));
        assertFalse(cell.isNumber("A1"));
        assertFalse(cell.isNumber("=A1"));
        assertFalse(cell.isNumber("=5"));
        assertFalse(cell.isNumber("=-8775"));
        assertFalse(cell.isText("=A1"));
        assertFalse(cell.isText("=A1*3"));
        assertFalse(cell.isText("=(3)*(2)"));
        assertFalse(cell.isText("1000.222"));
        assertFalse(cell.isText("-1000.222"));
        assertFalse(cell.isText(""));
        assertFalse(cell.isText(null));


        assert cell.eval("=1+2") == 3;
        assert cell.eval("=(1+2)*3") == 9;
        assert cell.eval("=(1+2)*(3-1)") == 6;
        assert cell.eval("=10/2+3") == 8;
        assert cell.eval("=(10/(2+3))") == 2;
        assert cell.eval("=((1+2)*2)-1") == 5;
        assert cell.eval("=1") == 1;
        assert cell.eval("=(3)") == 3;
        assert cell.eval("=((2+2)*3)*2*2+1") == 49;
        assertThrows(IllegalArgumentException.class, () -> cell.eval("=(1+2)(3+4)")); // Implicit multiplication not supporte

        // Valid cells
        assertTrue(cell.isValidCell("A1"));
        assertTrue(cell.isValidCell("J99"));

        // Invalid cells
        assertFalse(cell.isValidCell("Z100")); // Row out of bounds
        assertFalse(cell.isValidCell("A-1")); // Negative row
        assertFalse(cell.isValidCell("AA1")); // Invalid column
        assertFalse(cell.isValidCell(null));  // Null input
        assertFalse(cell.isValidCell(""));    // Empty input
        // Valid formulas
        assertTrue(cell.isForm("=A1+1"));
        assertTrue(cell.isForm("=(3+5)*2"));
        assertTrue(cell.isForm("=A1/B2"));

        // Invalid formulas
        assertFalse(cell.isForm(null));        // Null input
        assertFalse(cell.isForm(""));          // Empty input
        assertFalse(cell.isForm("A1+1"));      // Missing '=' prefix
        assertFalse(cell.isForm("=Z100+1"));   // Invalid cell reference
        assertFalse(cell.isForm("=(A1+"));     // Unbalanced parentheses
        assertFalse(cell.isForm("=A1+*B2"));   // Invalid operator sequence
    }
}












