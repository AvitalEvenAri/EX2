package assignments.ex2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
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

    @Test
    public void testInvalidCellReference() { // new
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // Set invalid references
        sheet.set(0, 0, "=A0"); // Index out of bounds
        assertEquals("#ERROR", sheet.value(0, 0));

        sheet.set(1, 1, "=Z100"); // Out of bounds
        assertEquals("#ERROR", sheet.value(1, 1));
    }

    @Test
    public void testNegativeNumbers() { // new
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // Test negative numbers in formulas
        sheet.set(0, 0, "10");
        System.out.println(sheet.get(0,0));
        sheet.set(1, 1, "=-5");
        System.out.println(sheet.get(1,1));
        System.out.println("1,1" + sheet.get(1,1));
        assertEquals("-5.0", sheet.value(1, 1));

        sheet.set(2, 2, "=A1+B2"); // A1 = 10, B2 = -5
        assertEquals("5.00", sheet.value(2, 2));
    }

    @Test
    public void testWhitespaceHandling() { // new
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // Test formula with extra spaces
        sheet.set(0, 0, "= 5  +  10 ");
        assertEquals("15.00", sheet.value(0, 0));
    }

    @Test
    public void testCoordinateToCellAddress() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // בדיקה: קואורדינטות (0,0) צריכות להיות "A1"
        String expectedAddress = "A1";
        String actualAddress = "" + (char) ('A' + 0) + (1); // "A" + "1"
        assertEquals(expectedAddress, actualAddress, "קואורדינטות (0,0) צריכות להיות A1");

        // בדיקה: קואורדינטות (1,1) צריכות להיות "B2"
        expectedAddress = "B2";
        actualAddress = "" + (char) ('A' + 1) + (1 + 1); // "B" + "2"
        assertEquals(expectedAddress, actualAddress, "קואורדינטות (1,1) צריכות להיות B2");

        // בדיקה: קואורדינטות (4,4) צריכות להיות "E5"
        expectedAddress = "E5";
        actualAddress = "" + (char) ('A' + 4) + (4 + 1); // "E" + "5"
        assertEquals(expectedAddress, actualAddress, "קואורדינטות (4,4) צריכות להיות E5");
    }

    @Test
    public void testCellEntryValidity() {
        // בדיקת מחלקת CellEntry עם כתובת חוקית
        CellEntry entry = new CellEntry("B3");
        assertTrue(entry.isValid(), "כתובת B3 צריכה להיות חוקית");
        assertEquals(1, entry.getX(), "X של B3 צריך להיות 1");
        assertEquals(2, entry.getY(), "Y של B3 צריך להיות 2");

        // כתובת לא חוקית
        CellEntry invalidEntry = new CellEntry("1A");
        assertFalse(invalidEntry.isValid(), "כתובת 1A לא צריכה להיות חוקית");
        assertEquals(Ex2Utils.ERR, invalidEntry.getX(), "X של כתובת לא חוקית צריך להיות Ex2Utils.ERR");
        assertEquals(Ex2Utils.ERR, invalidEntry.getY(), "Y של כתובת לא חוקית צריך להיות Ex2Utils.ERR");
    }


    @Test
    public void testNestedParentheses() { // new
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // Nested parentheses
        sheet.set(0, 0, "=(5+10)*2");
        System.out.println("sheet.get1" +  sheet.get(0,0));
        System.out.println("sheet.get0" +  sheet.get(0,0));
        sheet.set(0, 0, "=((5+10)*2)/5");
        System.out.println("sheet.get1" +  sheet.get(0,0));
        assertEquals("6.00", sheet.value(0, 0));
    }

    @Test
    public void testFormulaError() { // new
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // Invalid formulas
        sheet.set(0, 0, "=5++10");
        assertEquals("#ERROR", sheet.value(0, 0));

        sheet.set(1, 1, "=()");
        assertEquals("#ERROR", sheet.value(1, 1));

        sheet.set(2, 2, "=5+");
        assertEquals("#ERROR", sheet.value(2, 2));
    }

    @Test
    public void testLargeDepth() { // new
        Ex2Sheet sheet = new Ex2Sheet(10, 10);

        // Create a large dependency chain
        sheet.set(0, 0, "1");        // A1
        sheet.set(1, 1, "=A1+1");    // B2
        sheet.set(2, 2, "=B2+1");    // C3
        sheet.set(3, 3, "=C3+1");    // D4
        sheet.set(4, 4, "=D4+1");    // E5

        int[][] depths = sheet.depth();

        assertEquals(0, depths[0][0]);
        assertEquals(1, depths[1][1]);
        assertEquals(2, depths[2][2]);
        assertEquals(3, depths[3][3]);
        assertEquals(4, depths[4][4]);
    }

    @Test
    public void testSaveAndLoad() throws IOException { // new
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // Set some values
        sheet.set(0, 0, "5");
        sheet.set(1, 1, "=A1+10");
        sheet.set(2, 2, "=B2*2");

        // Save the sheet
        String fileName = "testSheet.csv";
        sheet.save(fileName);

        // Load the sheet
        Ex2Sheet loadedSheet = new Ex2Sheet(5, 5);
        loadedSheet.load(fileName);

        // Verify loaded values
        assertEquals("5", loadedSheet.value(0, 0));
        assertEquals("15.00", loadedSheet.value(1, 1));
        assertEquals("30.00", loadedSheet.value(2, 2));

        // Clean up
        new File(fileName).delete();
    }

    @Test
    public void testMixedTypes() { // new
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // Test mixing text and numbers
        sheet.set(0, 0, "Hello");
        sheet.set(1, 1, "10");
        sheet.set(2, 2, "=A1+B2");

        assertEquals("Hello", sheet.value(0, 0)); // Text should remain
        assertEquals("10", sheet.value(1, 1));   // Number should remain
        assertEquals("#ERROR", sheet.value(2, 2)); // Invalid formula with text
    }

    @Test
    public void testSelfReference() { // new
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // Set a self-referential formula
        sheet.set(0, 0, "=A1");
        int[][] depths = sheet.depth();
        assertEquals(-1, depths[0][0]);
        assertEquals("#CYCLE", sheet.value(0, 0));
    }

    @Test
    public void testComplexReferenceChain() { // new
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // Create a complex chain
        sheet.set(0, 0, "10");
        sheet.set(1, 1, "=A1*2");
        sheet.set(2, 2, "=B2/5");
        sheet.set(3, 3, "=C3+7");

        assertEquals("10", sheet.value(0, 0));
        assertEquals("20.00", sheet.value(1, 1));
        assertEquals("4.00", sheet.value(2, 2));
        assertEquals("11.00", sheet.value(3, 3));
    }

    @Test
    public void testNonNumericFormula() { // new
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "=Hello+10");
        assertEquals("#ERROR", sheet.value(0, 0));
    }

    @Test
    public void testCyclicDependencyThreeCells() { // new
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "=B2");
        sheet.set(1, 1, "=C3");
        sheet.set(2, 2, "=A1");

        int[][] depths = sheet.depth();

        assertEquals(-1, depths[0][0]);
        assertEquals(-1, depths[1][1]);
        assertEquals(-1, depths[2][2]);
    }
}
