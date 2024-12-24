import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class test_cell {
    @Test
    void testValidFormulas() {
        // Positive tests
        assertTrue(Cell.isForm("=(1+2)*3"));
        assertTrue(Cell.isForm("=3+(9*8)+2"));
        assertTrue(Cell.isForm("=(1+((2*3)-(4/5)))"));
        assertTrue(Cell.isForm("=((1+2)(3+4))"));
        assertTrue(Cell.isForm("=(A1+B2)*C3"));
        assertTrue(Cell.isForm("=123"));
        assertTrue(Cell.isForm("=a1"));
        assertTrue(Cell.isForm("=0"));
        assertTrue(Cell.isForm("=-1.23"));
        assertTrue(Cell.isForm("=A1"));
        assertTrue(Cell.isForm("=a99"));
        assertTrue(Cell.isForm("=A1+B2"));
        assertTrue(Cell.isForm("=1+2"));
        assertTrue(Cell.isForm("=1-2"));
        assertTrue(Cell.isForm("=(1+2)"));
        assertTrue(Cell.isForm("=(1+2)*(3+4)"));
        assertTrue(Cell.isForm("=(1+2)(3+4)"));
        assertTrue(Cell.isForm("=((1+2)*3)(4/2)"));
        assertTrue(Cell.isForm("=(1)(2)(3)"));
        assertTrue(Cell.isForm("=(A1)(B2)"));
        assertTrue(Cell.isForm("=((A1+B2)*C3)(D4)"));
        assertTrue(Cell.isForm("=A1+B2*C3"));
        assertTrue(Cell.isForm("=(A1+B2)*(C3+D4)"));
        assertTrue(Cell.isForm("=A1+A2+A3+A4"));
        assertTrue(Cell.isForm("=(A1)(B2)+C3"));
        assertTrue(Cell.isForm("=(A1+(B2*C3))/D4"));
        assertTrue(Cell.isForm("=A1+B2/3*C4-5"));
        assertTrue(Cell.isForm("=123456789"));
        assertTrue(Cell.isForm("=-123456.789"));
        assertTrue(Cell.isForm("=(123456.789)"));
        assertTrue(Cell.isForm("=(-123.45)*(678.90)"));
        assertTrue(Cell.isForm("=(1+2)*(3-4)/(5+6)"));
        assertTrue(Cell.isForm("=(1-2+3*4/5)"));
        assertTrue(Cell.isForm("=(1+2)*(3/4)-(5*6)"));
        assertTrue(Cell.isForm("=((1*2)+(3/4))-5"));
        assertTrue(Cell.isForm("=1+2*3-4/5"));
        assertTrue(Cell.isForm("=(A1+(B2*C3-(D4/E5)))"));
        assertTrue(Cell.isForm("=(A1+(B2*(C3-(D4/E5))))"));
        assertTrue(Cell.isForm("=(((A1+B2)*(C3/D4))-E5)"));
        assertTrue(Cell.isForm("=(A1)(B2)(C3)"));
        assertTrue(Cell.isForm("=((A1+B2)*(C3+D4))/(E5)"));
    }

    @Test
    void testInvalidFormulas() {
        // Negative tests
        assertFalse(Cell.isForm("=(2+"));
        assertFalse(Cell.isForm("=3+(9*8"));
        assertFalse(Cell.isForm(""));
        assertFalse(Cell.isForm("="));
        assertFalse(Cell.isForm("=()"));
        assertFalse(Cell.isForm("=)1+2("));
        assertFalse(Cell.isForm("=1+2)"));
        assertFalse(Cell.isForm("=(1+2"));
        assertFalse(Cell.isForm("=(A1+)"));
        assertFalse(Cell.isForm("=A1+B2)"));
        assertFalse(Cell.isForm("=1+(2*3"));
        assertFalse(Cell.isForm("=1+*2"));
        assertFalse(Cell.isForm("=(1+2)+"));
        assertFalse(Cell.isForm("=A"));
        assertFalse(Cell.isForm("=1A"));
        assertFalse(Cell.isForm("=AA1"));
        assertFalse(Cell.isForm("=A-1"));
        assertFalse(Cell.isForm("=A1.2"));
        assertFalse(Cell.isForm("=Z100"));
    }
}

