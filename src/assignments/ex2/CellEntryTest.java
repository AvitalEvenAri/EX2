package assignments.ex2;

import static org.junit.jupiter.api.Assertions.*; // Assertions for JUnit 5
import org.junit.jupiter.api.Test; // Test annotation for JUnit 5

public class CellEntryTest {

    @Test
    public void testValidIndex() {
        CellEntry cell = new CellEntry("B3");
        assertTrue(cell.isValid(), "B3 should be a valid index");
        assertEquals(1, cell.getX(), "Column B should map to 1");
        assertEquals(3, cell.getY(), "Row 3 should map to 3");
    }

    @Test
    public void testInvalidIndexEmpty() {
        CellEntry cell = new CellEntry("");
        assertFalse(cell.isValid(), "Empty index should be invalid");
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X");
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y");
    }

    @Test
    public void testInvalidIndexNull() {
        CellEntry cell = new CellEntry(null);
        assertFalse(cell.isValid(), "Null index should be invalid");
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X");
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y");
    }

    @Test
    public void testInvalidIndexWrongColumn() {
        CellEntry cell = new CellEntry("12");
        assertFalse(cell.isValid(), "Index with no column should be invalid");
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X");
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y");
    }

    @Test
    public void testInvalidIndexOutOfRangeRow() {
        CellEntry cell = new CellEntry("A100");
        assertFalse(cell.isValid(), "Row greater than 99 should be invalid");
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X");
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y");
    }

    @Test
    public void testValidIndexCaseInsensitive() {
        CellEntry cell = new CellEntry("c12");
        assertTrue(cell.isValid(), "Index with lowercase column should be valid");
        assertEquals(2, cell.getX(), "Column C should map to 2");
        assertEquals(12, cell.getY(), "Row 12 should map to 12");
    }

    @Test
    public void testValidIndexEdgeCase() {
        CellEntry cell = new CellEntry("Z99");
        assertTrue(cell.isValid(), "Z99 should be a valid index");
        assertEquals(25, cell.getX(), "Column Z should map to 25");
        assertEquals(99, cell.getY(), "Row 99 should map to 99");
    }

    @Test
    public void testToString() {
        CellEntry cell = new CellEntry("A5");
        assertEquals("A5", cell.toString(), "toString should return the original index");
    }
    @Test
    public void testValidIndexSingleRowZero() {
        CellEntry cell = new CellEntry("A0");
        assertTrue(cell.isValid(), "A0 should be a valid index");
        assertEquals(0, cell.getX(), "Column A should map to 0");
        assertEquals(0, cell.getY(), "Row 0 should map to 0");
    }

    @Test
    public void testValidIndexMaximumRow() {
        CellEntry cell = new CellEntry("Z99");
        assertTrue(cell.isValid(), "Z99 should be a valid index");
        assertEquals(25, cell.getX(), "Column Z should map to 25");
        assertEquals(99, cell.getY(), "Row 99 should map to 99");
    }

    @Test
    public void testInvalidIndexNoRow() {
        CellEntry cell = new CellEntry("A");
        assertFalse(cell.isValid(), "A should be an invalid index (missing row)");
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X");
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y");
    }

    @Test
    public void testInvalidIndexMultipleLetters() {
        CellEntry cell = new CellEntry("AA1");
        assertFalse(cell.isValid(), "AA1 should be an invalid index (multiple letters)");
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X");
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y");
    }

    @Test
    public void testInvalidIndexNegativeRow() {
        CellEntry cell = new CellEntry("A-1");
        assertFalse(cell.isValid(), "A-1 should be an invalid index (negative row)");
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X");
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y");
    }

    @Test
    public void testInvalidIndexExtraCharacters() {
        CellEntry cell = new CellEntry("A1!");
        assertFalse(cell.isValid(), "A1! should be an invalid index (extra characters)");
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X");
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y");
    }

    @Test
    public void testInvalidIndexEmptyColumn() {
        CellEntry cell = new CellEntry("1");
        assertFalse(cell.isValid(), "1 should be an invalid index (missing column)");
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X");
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y");
    }

    @Test
    public void testInvalidIndexOutOfBoundsColumn() {
        CellEntry cell = new CellEntry("AA1");
        assertFalse(cell.isValid(), "AA1 should be an invalid index (column out of bounds)");
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X");
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y");
    }

}
