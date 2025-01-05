package assignments.ex2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

public class Ex2SheetTest {

    @Test
    public void testSetAndGetValue() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // Set values and verify
        sheet.set(0, 0, "5");
        sheet.set(1, 1, "10");
        assertEquals("5", sheet.value(0, 0));
        assertEquals("10", sheet.value(1, 1));
    }


    @Test
    public void testSetAndGetFormula() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // Set a formula and verify
        sheet.set(0, 0, "=5+10");
        assertEquals("15.00", sheet.value(0, 0));
    }

    @Test
    public void testCellReference() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // Set referenced cells and formulas
        sheet.set(0, 0, "5");
        sheet.set(1, 1, "=A1+10"); // A1 is (0,0)
        assertEquals("15.00", sheet.value(1, 1));
    }

    @Test
    public void testComplexFormula() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // Set complex formula
        sheet.set(0, 0, "=5+10*2");
        assertEquals("25.00", sheet.value(0, 0));
    }

    @Test
    public void testDepthComputation() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "10");       // A1
        sheet.set(1, 1, "=A1+5");    // B2
        sheet.set(2, 2, "=B2*2");    // C3

        int[][] depths = sheet.depth();

        assertEquals(0, depths[0][0]); // תא ללא תלות
        assertEquals(1, depths[1][1]); // תלוי ב-A1
        assertEquals(2, depths[2][2]); // תלוי ב-B2 שתלוי ב-A1
    }


    @Test
    public void testCircularDependency() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "=B2"); // A1 תלוי ב-B2
        sheet.set(1, 1, "=A1"); // B2 תלוי ב-A1

        int[][] depths = sheet.depth();

        assertEquals(-1, depths[0][0]); // מעגל תלות
        assertEquals(-1, depths[1][1]); // מעגל תלות
    }

    @Test
    public void testSetUpdatesCorrectly() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "5"); // A1
        assertEquals("5", sheet.get(0, 0).getData(), "A1 should be set to 5");

        sheet.set(1, 1, "=A1"); // B2 depends on A1
        assertEquals("=A1", sheet.get(1, 1).getData(), "B2 should be set to =A1");

        sheet.set(2, 2, "=B2+10"); // C3 depends on B2
        assertEquals("=B2+10", sheet.get(2, 2).getData(), "C3 should be set to =B2+10");

        int[][] depths = sheet.depth();
        assertEquals(0, depths[0][0], "A1 depth should be 0");
        assertEquals(1, depths[1][1], "B2 depth should be 1");
        assertEquals(2, depths[2][2], "C3 depth should be 2");
    }

    @Test
    public void testDivisionByZero() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // Set a division by zero
        sheet.set(0, 0, "=10/0");
        assertEquals("#ERROR", sheet.value(0, 0));
    }


    @Test
    public void testEmptyCell() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // Ensure empty cell behavior
        assertEquals("", sheet.value(0, 0));
    }
}
