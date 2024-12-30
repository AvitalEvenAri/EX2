import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;

public class SpreadsheetTest {

    @Test
    void testParseAddress() {
        Spreadsheet spreadsheet = new Spreadsheet(10, 10);

        // Valid addresses
        int[] result = spreadsheet.parseAddress("A0");
        assertArrayEquals(new int[]{0, 0}, result);

        result = spreadsheet.parseAddress("B1");
        assertArrayEquals(new int[]{1, 1}, result);

        result = spreadsheet.parseAddress("J9");
        assertArrayEquals(new int[]{9, 9}, result);

        // Invalid addresses
        assertThrows(IllegalArgumentException.class, () -> spreadsheet.parseAddress("1")); // Missing column
        assertThrows(IllegalArgumentException.class, () -> spreadsheet.parseAddress("AA1")); // Invalid column
        assertThrows(IllegalArgumentException.class, () -> spreadsheet.parseAddress("A-1")); // Negative row
        assertThrows(IllegalArgumentException.class, () -> spreadsheet.parseAddress("Z10")); // Out of bounds
    }

    @Test
    void testSetAndGet() {
        Spreadsheet spreadsheet = new Spreadsheet(10, 10);

        // Setting and getting cells
        Cell cellA1 = new Cell("=B1");
        spreadsheet.setCell("A0", cellA1);

        Cell retrievedCell = spreadsheet.getCell("A0");
        assertEquals("=B1", retrievedCell.getCell_info());

        Cell cellB1 = new Cell("5");
        spreadsheet.setCell("B1", cellB1);

        retrievedCell = spreadsheet.getCell("B1");
        assertEquals("5", retrievedCell.getCell_info());
    }

    @Test
    void testComputeDepth() {
        Spreadsheet spreadsheet = new Spreadsheet(10, 10);

        // Single cell depth
        spreadsheet.setCell("A0", new Cell("5"));
        assertEquals(0, spreadsheet.computeDepth("A0", new HashSet<>()));

        // Simple formula depth
        spreadsheet.setCell("B0", new Cell("=A0+1"));
        assertEquals(1, spreadsheet.computeDepth("B0", new HashSet<>()));

        // Nested formula depth
        spreadsheet.setCell("C0", new Cell("=B0+1"));
        assertEquals(2, spreadsheet.computeDepth("C0", new HashSet<>()));
    }

    @Test
    public void testComputeDepth_withCycle() {
        Spreadsheet spreadsheet = new Spreadsheet(10, 10);
        spreadsheet.setCell("A0", new Cell("=B0+1"));
        spreadsheet.setCell("B0", new Cell("=A0+1"));

        Set<String> visited = new HashSet<>();
        assertEquals(-1, spreadsheet.computeDepth("A0", visited));
    }


    @Test
    void testComputeAllDepths() {
        Spreadsheet spreadsheet = new Spreadsheet(3, 3);

        spreadsheet.setCell("A0", new Cell("5"));
        spreadsheet.setCell("B0", new Cell("=A0+1"));
        spreadsheet.setCell("C0", new Cell("=B0+1"));

        int[][] depths = spreadsheet.computeAllDepths();

        assertEquals(0, depths[0][0]); // A0
        assertEquals(1, depths[0][1]); // B0
        assertEquals(2, depths[0][2]); // C0
    }

    @Test
    void testXCellAndYCell() {
        Spreadsheet spreadsheet = new Spreadsheet(10, 10);

        // Valid X and Y cell extraction
        assertEquals(0, spreadsheet.xCell("A0"));
        assertEquals(9, spreadsheet.xCell("J0"));

        assertEquals(0, spreadsheet.yCell("A0"));
        assertEquals(5, spreadsheet.yCell("A5"));

        // Invalid addresses
        assertThrows(IllegalArgumentException.class, () -> spreadsheet.xCell("AA0")); // Invalid column
        assertThrows(IllegalArgumentException.class, () -> spreadsheet.yCell("A-1")); // Invalid row
    }
    @Test
    void testInvalidCellReferences() {
        Spreadsheet spreadsheet = new Spreadsheet(10, 10);

        // Invalid references in formulas
        spreadsheet.setCell("A0", new Cell("=Z100+1")); // Out of bounds reference
        assertThrows(IllegalArgumentException.class, () -> spreadsheet.computeDepth("A0", new HashSet<>()));

        spreadsheet.setCell("B0", new Cell("=A-1+1")); // Negative reference
        assertThrows(IllegalArgumentException.class, () -> spreadsheet.computeDepth("B0", new HashSet<>()));
    }

    @Test
    void testEmptyCell() {
        Spreadsheet spreadsheet = new Spreadsheet(10, 10);

        // Empty cell should have depth 0
        spreadsheet.setCell("A0", new Cell(""));
        assertEquals(0, spreadsheet.computeDepth("A0", new HashSet<>()));
    }

    @Test
    void testFormulaWithoutDependencies() {
        Spreadsheet spreadsheet = new Spreadsheet(10, 10);

        // Simple formula without other cell references
        spreadsheet.setCell("A0", new Cell("=3+5"));
        assertEquals(0, spreadsheet.computeDepth("A0", new HashSet<>()));

        spreadsheet.setCell("B0", new Cell("=(3+5)*2"));
        assertEquals(0, spreadsheet.computeDepth("B0", new HashSet<>()));
    }

    @Test
    void testFormulaWithMultipleReferences() {
        Spreadsheet spreadsheet = new Spreadsheet(10, 10);

        // Formula with multiple cell references
        spreadsheet.setCell("A0", new Cell("5"));
        spreadsheet.setCell("B0", new Cell("=A0+2"));
        spreadsheet.setCell("C0", new Cell("=A0+B0"));

        assertEquals(0, spreadsheet.computeDepth("A0", new HashSet<>()));
        assertEquals(1, spreadsheet.computeDepth("B0", new HashSet<>()));
        assertEquals(2, spreadsheet.computeDepth("C0", new HashSet<>()));
    }

    @Test
    void testComplexCyclicReferences() {
        Spreadsheet spreadsheet = new Spreadsheet(10, 10);

        // Complex cyclic references
        spreadsheet.setCell("A0", new Cell("=B0+1"));
        spreadsheet.setCell("B0", new Cell("=C0+1"));
        spreadsheet.setCell("C0", new Cell("=A0+1"));

        assertEquals(-1, spreadsheet.computeDepth("A0", new HashSet<>()));
        assertEquals(-1, spreadsheet.computeDepth("B0", new HashSet<>()));
        assertEquals(-1, spreadsheet.computeDepth("C0", new HashSet<>()));
    }

    @Test
    void testLargeSpreadsheet() {
        Spreadsheet spreadsheet = new Spreadsheet(100, 100);

        // Populate a large spreadsheet with formulas
        for (int i = 0; i < 100; i++) {
            spreadsheet.setCell("A" + i, new Cell(String.valueOf(i)));
            spreadsheet.setCell("B" + i, new Cell("=A" + i + "+1"));
        }

        // Check depth of last cell
        assertEquals(1, spreadsheet.computeDepth("B99", new HashSet<>()));
    }

    @Test
    void testInvalidDepthCalculation() {
        Spreadsheet spreadsheet = new Spreadsheet(10, 10);

        // Reference to invalid cell
        spreadsheet.setCell("A0", new Cell("=B10+1"));
        assertThrows(IllegalArgumentException.class, () -> spreadsheet.computeDepth("A0", new HashSet<>()));
    }

}
