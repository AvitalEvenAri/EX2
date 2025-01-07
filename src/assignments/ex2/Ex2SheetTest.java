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

        sheet.set(0, 0, "5");
        sheet.set(1, 1, "10");
        assertEquals("5", sheet.value(0, 0));
        assertEquals("10", sheet.value(1, 1));
    }

    @Test
    public void testSetAndGetFormula() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "=5+10");
        assertEquals("15.00", sheet.value(0, 0));
    }

    @Test
    public void testCellReference() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "5");
        sheet.set(1, 1, "=A0+10");
        assertEquals("15.00", sheet.value(1, 1));
    }

    @Test
    public void testComplexFormula() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "=5+10*2");
        assertEquals("25.00", sheet.value(0, 0));
    }

    @Test
    public void testDepthComputation() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "10");
        sheet.set(1, 1, "=A0+5");
        sheet.set(2, 2, "=B1*2");

        int[][] depths = sheet.depth();

        assertEquals(0, depths[0][0]);
        assertEquals(1, depths[1][1]);
        assertEquals(2, depths[2][2]);
    }

    @Test
    public void testCircularDependency() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "=B1");
        sheet.set(1, 1, "=A0");

        int[][] depths = sheet.depth();

        assertEquals(-1, depths[0][0]);
        assertEquals(-1, depths[1][1]);
    }

    @Test
    public void testSetUpdatesCorrectly() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "5");
        assertEquals("5", sheet.get(0, 0).getData());

        sheet.set(1, 1, "=A0");
        assertEquals("=A0", sheet.get(1, 1).getData());

        sheet.set(2, 2, "=B1+10");
        assertEquals("=B1+10", sheet.get(2, 2).getData());

        int[][] depths = sheet.depth();
        assertEquals(0, depths[0][0]);
        assertEquals(1, depths[1][1]);
        assertEquals(2, depths[2][2]);
    }

    @Test
    public void testDivisionByZero() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "=10/0");
        assertEquals("#ERROR", sheet.value(0, 0));
    }

    @Test
    public void testEmptyCell() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        assertEquals("", sheet.value(0, 0));
    }

    @Test
    public void testInvalidCellReference() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "=A0");
        assertEquals("#CYCLE", sheet.value(0, 0));

        sheet.set(1, 1, "=Z100");
        assertEquals("#ERROR", sheet.value(1, 1));
    }

    @Test
    public void testNegativeNumbers() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "10");
        System.out.println(sheet.get(0,0));
        sheet.set(1, 1, "=-5");
        System.out.println(sheet.get(1,1));
        System.out.println("1,1" + sheet.get(1,1));
        assertEquals("-5.00", sheet.value(1, 1));

        sheet.set(2, 2, "=A0+B1");
        assertEquals("5.00", sheet.value(2, 2));
    }

    @Test
    public void testWhitespaceHandling() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "= 5  +  10 ");
        assertEquals("15.00", sheet.value(0, 0));
    }

    @Test
    public void testCoordinateToCellAddress() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        String expectedAddress = "A0";
        String actualAddress = "" + (char) ('A' + 0) + (0);
        assertEquals(expectedAddress, actualAddress);

        expectedAddress = "B1";
        actualAddress = "" + (char) ('A' + 1) + (1);
        assertEquals(expectedAddress, actualAddress);

        expectedAddress = "E4";
        actualAddress = "" + (char) ('A' + 4) + (4);
        assertEquals(expectedAddress, actualAddress);
    }

    @Test
    public void testCellEntryValidity() {
        CellEntry entry = new CellEntry("B2");
        assertTrue(entry.isValid());
        assertEquals(1, entry.getX());
        assertEquals(2, entry.getY());

        entry = new CellEntry("A0");
        assertTrue(entry.isValid());
        assertEquals(0, entry.getX());
        assertEquals(0, entry.getY());

        entry = new CellEntry("Z98");
        assertTrue(entry.isValid());
        assertEquals(25, entry.getX());
        assertEquals(98, entry.getY());
    }

    @Test
    public void testNestedParenthesesFormula1() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "=(5+10)*2");
        System.out.println("sheet.get1" +  sheet.get(0,0));
        System.out.println("sheet.get0" +  sheet.get(0,0));
        assertEquals("30.00", sheet.value(0, 0));
    }

    @Test
    public void testNestedParenthesesFormula2() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "=((5+10)*2)/5");
        System.out.println("sheet.get1" +  sheet.get(0,0));
        assertEquals("6.00", sheet.value(0, 0));
    }

    @Test
    public void testFormulaError() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "=5++10");
        assertEquals("#ERROR", sheet.value(0, 0));

        sheet.set(1, 1, "=()");
        assertEquals("#ERROR", sheet.value(1, 1));

        sheet.set(2, 2, "=5+");
        assertEquals("#ERROR", sheet.value(2, 2));
    }

    @Test
    public void testLargeDepth() {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);

        sheet.set(0, 0, "1");
        sheet.set(1, 1, "=A0+1");
        sheet.set(2, 2, "=B1+1");
        sheet.set(3, 3, "=C2+1");
        sheet.set(4, 4, "=D3+1");

        int[][] depths = sheet.depth();

        assertEquals(0, depths[0][0]);
        assertEquals(1, depths[1][1]);
        assertEquals(2, depths[2][2]);
        assertEquals(3, depths[3][3]);
        assertEquals(4, depths[4][4]);
    }

    @Test
    public void testSaveAndLoad() throws IOException {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "5");
        sheet.set(1, 1, "=A0+10");
        sheet.set(2, 2, "=B1*2");

        String fileName = "testSheet.csv";
        sheet.save(fileName);

        Ex2Sheet loadedSheet = new Ex2Sheet(5, 5);
        loadedSheet.load(fileName);

        assertEquals("5", loadedSheet.value(0, 0));
        assertEquals("15.00", loadedSheet.value(1, 1));
        assertEquals("30.00", loadedSheet.value(2, 2));

        new File(fileName).delete();
    }

    @Test
    public void testMixedTypes() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "Hello");
        sheet.set(1, 1, "10");
        sheet.set(2, 2, "=A0+B1");

        assertEquals("Hello", sheet.value(0, 0));
        assertEquals("10", sheet.value(1, 1));
        assertEquals("#ERROR", sheet.value(2, 2));
    }

    @Test
    public void testSelfReference() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "=A0");
        int[][] depths = sheet.depth();
        assertEquals(-1, depths[0][0]);
        assertEquals("#CYCLE", sheet.value(0, 0));
    }

    @Test
    public void testComplexReferenceChain() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "10");
        sheet.set(1, 1, "=A0*2");
        sheet.set(2, 2, "=B1/5");
        sheet.set(3, 3, "=C2+7");

        assertEquals("10", sheet.value(0, 0));
        assertEquals("20.00", sheet.value(1, 1));
        assertEquals("4.00", sheet.value(2, 2));
        assertEquals("11.00", sheet.value(3, 3));
    }

    @Test
    public void testNonNumericFormula() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "=Hello+10");
        assertEquals("#ERROR", sheet.value(0, 0));
    }

    @Test
    public void testCyclicDependencyThreeCells() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "=B1");
        sheet.set(1, 1, "=C2");
        sheet.set(2, 2, "=A0");

        int[][] depths = sheet.depth();

        assertEquals(-1, depths[0][0]);
        assertEquals(-1, depths[1][1]);
        assertEquals(-1, depths[2][2]);

        assertEquals("#CYCLE", sheet.value(0, 0));
        assertEquals("#CYCLE", sheet.value(1, 1));
        assertEquals("#CYCLE", sheet.value(2, 2));
    }

    @Test
    public void testConstructorInitialization() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Cell cell = sheet.get(i, j);
                assertNotNull(cell);
                assertEquals("", cell.getData());
            }
        }
    }

    @Test
    public void testConvertToIndex() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        assertEquals("A0", sheet.convertToIndex(0, 0));
        assertEquals("B1", sheet.convertToIndex(1, 1));
        assertEquals("E4", sheet.convertToIndex(4, 4));
    }

    @Test
    public void testGetWithStringAddress() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "10");
        assertEquals("10", sheet.get("A0").getData());

        sheet.set(4, 4, "=A0+5");
        assertEquals("=A0+5", sheet.get("E4").getData());
    }

    @Test
    public void testValueAfterUpdates() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "5");
        sheet.set(1, 1, "=A0+5");
        assertEquals("10.00", sheet.value(1, 1));

        sheet.set(0, 0, "10");
        assertEquals("15.00", sheet.value(1, 1));
    }

    @Test
    public void testCyclicDependencyDetection() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "=B1");
        sheet.set(1, 1, "=A0");

        assertEquals("#CYCLE", sheet.value(0, 0));
        assertEquals("#CYCLE", sheet.value(1, 1));
    }

    @Test
    public void testSetTextCell() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "Hello");
        assertEquals("Hello", sheet.value(0, 0));
    }

    @Test
    public void testSetValidFormulaCell() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "10");
        sheet.set(1, 1, "=A0+5");
        assertEquals("15.00", sheet.value(1, 1));
    }

    @Test
    public void testDependentCellUpdate() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        sheet.set(0, 0, "5");
        sheet.set(1, 1, "=A0*2");

        assertEquals("10.00", sheet.value(1, 1));

        sheet.set(0, 0, "7");
        assertEquals("14.00", sheet.value(1, 1));
    }

    @Test
    public void testCellClick() {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);

        sheet.set(0, 0, "Hello");

        Cell cell = sheet.get(0, 0);
        assertNotNull(cell);
        assertEquals("Hello", cell.getData());

        String index = sheet.convertToIndex(0, 0);
        assertEquals("A0", index);

        System.out.println("Clicked cell: " + cell.toString());
    }
}