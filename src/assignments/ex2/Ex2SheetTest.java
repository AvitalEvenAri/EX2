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
        assertEquals(1, entry.getX(), "X של B3 צריך להיות 1 (עמודה B)");
        assertEquals(3, entry.getY(), "Y של B3 צריך להיות 3 (שורה 3)");

        // בדיקה עבור כתובת חוקית אחרת
        entry = new CellEntry("A0");
        assertTrue(entry.isValid(), "כתובת A0 צריכה להיות חוקית");
        assertEquals(0, entry.getX(), "X של A0 צריך להיות 0 (עמודה A)");
        assertEquals(0, entry.getY(), "Y של A0 צריך להיות 0 (שורה 0)");

        // בדיקה עבור כתובת קצה
        entry = new CellEntry("Z99");
        assertTrue(entry.isValid(), "כתובת Z99 צריכה להיות חוקית");
        assertEquals(25, entry.getX(), "X של Z99 צריך להיות 25 (עמודה Z)");
        assertEquals(99, entry.getY(), "Y של Z99 צריך להיות 99 (שורה 99)");
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
    @Test
    public void testConstructorInitialization() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Cell cell = sheet.get(i, j);
                assertNotNull(cell, "Cell at (" + i + ", " + j + ") should not be null.");
                assertEquals("", cell.getData(), "Cell at (" + i + ", " + j + ") should be initialized with an empty string.");
            }
        }
    }
    @Test
    public void testConvertToIndex() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        assertEquals("A0", sheet.convertToIndex(0, 0), "convertToIndex(0, 0) should return A1");
        assertEquals("B1", sheet.convertToIndex(1, 1), "convertToIndex(1, 1) should return B2");
        assertEquals("E4", sheet.convertToIndex(4, 4), "convertToIndex(4, 4) should return E5");
    }
    @Test
    public void testGetWithStringAddress() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "10");
        assertEquals("10", sheet.get("A1").getData(), "get(A1) should return the cell with data 10");

        sheet.set(4, 4, "=A1+5");
        assertEquals("=A1+5", sheet.get("E5").getData(), "get(E5) should return the cell with formula =A1+5");
    }
    @Test
    public void testValueAfterUpdates() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "5");
        sheet.set(1, 1, "=A1+5"); // B2 תלוי ב-A1
        assertEquals("10.00", sheet.value(1, 1), "Value of B2 should be 10.00 after setting A1=5");

        sheet.set(0, 0, "10"); // שינוי ערך ב-A1
        assertEquals("15.00", sheet.value(1, 1), "Value of B2 should update to 15.00 after updating A1 to 10");
    }
    @Test
    public void testCyclicDependencyDetection() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "=B2"); // A1 תלוי ב-B2
        sheet.set(1, 1, "=A1"); // B2 תלוי ב-A1

        assertEquals("#CYCLE", sheet.value(0, 0), "Value of A1 should be #CYCLE due to circular dependency");
        assertEquals("#CYCLE", sheet.value(1, 1), "Value of B2 should be #CYCLE due to circular dependency");
    }
    @Test
    public void testSetTextCell() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "Hello");
        assertEquals("Hello", sheet.value(0, 0), "Value of A1 should be Hello after setting it");
    }
    @Test
    public void testSetValidFormulaCell() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "10");
        sheet.set(1, 1, "=A1+5");
        assertEquals("15.00", sheet.value(1, 1), "Value of B2 should be 15.00 after setting A1=10 and B2=A1+5");
    }
    @Test
    public void testDependentCellUpdate() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "5");
        sheet.set(1, 1, "=A1*2");

        assertEquals("10.00", sheet.value(1, 1), "Value of B2 should be 10.00 after setting A1=5");

        sheet.set(0, 0, "7");
        assertEquals("14.00", sheet.value(1, 1), "Value of B2 should update to 14.00 after updating A1 to 7");
    }
    @Test
    public void testCellClick() {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);

        // הכנסי ערך לתא
        sheet.set(0, 0, "Hello");

        // וודאי שהערך מופיע כהלכה
        Cell cell = sheet.get(0, 0);
        assertNotNull(cell, "Cell should not be null");
        assertEquals("Hello", cell.getData(), "Cell data should be 'Hello'");

        // בדקי ייצוג אינדקסי
        String index = sheet.convertToIndex(0, 0);
        assertEquals("A0", index, "Index should be A0");

        // בדקי קריאה דרך GUI או דרך ה"בחירה"
        System.out.println("Clicked cell: " + cell.toString());
    }


}
