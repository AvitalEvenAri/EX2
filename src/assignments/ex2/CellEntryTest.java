package assignments.ex2;

import static org.junit.jupiter.api.Assertions.*; // Assertions for JUnit 5
import org.junit.jupiter.api.Test; // Test annotation for JUnit 5

public class CellEntryTest {

    @Test
    public void testValidIndex() {
        CellEntry cell = new CellEntry("B3"); // Create a CellEntry with index "B3"
        assertTrue(cell.isValid(), "B3 should be a valid index"); // Check if the index is valid
        assertEquals(1, cell.getX(), "Column B should map to 1"); // Check if the column maps to 1
        assertEquals(3, cell.getY(), "Row 3 should map to 3"); // Check if the row maps to 3
    }

    @Test
    public void testInvalidIndexEmpty() {
        CellEntry cell = new CellEntry(""); // Create a CellEntry with an empty index
        assertFalse(cell.isValid(), "Empty index should be invalid"); // Check if the index is invalid
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X"); // Check if X returns ERR
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y"); // Check if Y returns ERR
    }

    @Test
    public void testInvalidIndexNull() {
        CellEntry cell = new CellEntry(null); // Create a CellEntry with a null index
        assertFalse(cell.isValid(), "Null index should be invalid"); // Check if the index is invalid
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X"); // Check if X returns ERR
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y"); // Check if Y returns ERR
    }

    @Test
    public void testInvalidIndexWrongColumn() {
        CellEntry cell = new CellEntry("12"); // Create a CellEntry with index "12"
        assertFalse(cell.isValid(), "Index with no column should be invalid"); // Check if the index is invalid
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X"); // Check if X returns ERR
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y"); // Check if Y returns ERR
    }

    @Test
    public void testInvalidIndexOutOfRangeRow() {
        CellEntry cell = new CellEntry("A100"); // Create a CellEntry with index "A100"
        assertFalse(cell.isValid(), "Row greater than 99 should be invalid"); // Check if the index is invalid
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X"); // Check if X returns ERR
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y"); // Check if Y returns ERR
    }

    @Test
    public void testValidIndexCaseInsensitive() {
        CellEntry cell = new CellEntry("c12"); // Create a CellEntry with index "c12"
        assertTrue(cell.isValid(), "Index with lowercase column should be valid"); // Check if the index is valid
        assertEquals(2, cell.getX(), "Column C should map to 2"); // Check if the column maps to 2
        assertEquals(12, cell.getY(), "Row 12 should map to 12"); // Check if the row maps to 12
    }

    @Test
    public void testValidIndexEdgeCase() {
        CellEntry cell = new CellEntry("Z99"); // Create a CellEntry with index "Z99"
        assertTrue(cell.isValid(), "Z99 should be a valid index"); // Check if the index is valid
        assertEquals(25, cell.getX(), "Column Z should map to 25"); // Check if the column maps to 25
        assertEquals(99, cell.getY(), "Row 99 should map to 99"); // Check if the row maps to 99
    }

    @Test
    public void testToString() {
        CellEntry cell = new CellEntry("A5"); // Create a CellEntry with index "A5"
        assertEquals("A5", cell.toString(), "toString should return the original index"); // Check if toString returns the original index
    }

    @Test
    public void testValidIndexSingleRowZero() {
        CellEntry cell = new CellEntry("A0"); // Create a CellEntry with index "A0"
        assertTrue(cell.isValid(), "A0 should be a valid index"); // Check if the index is valid
        assertEquals(0, cell.getX(), "Column A should map to 0"); // Check if the column maps to 0
        assertEquals(0, cell.getY(), "Row 0 should map to 0"); // Check if the row maps to 0
    }

    @Test
    public void testValidIndexMaximumRow() {
        CellEntry cell = new CellEntry("Z99"); // Create a CellEntry with index "Z99"
        assertTrue(cell.isValid(), "Z99 should be a valid index"); // Check if the index is valid
        assertEquals(25, cell.getX(), "Column Z should map to 25"); // Check if the column maps to 25
        assertEquals(99, cell.getY(), "Row 99 should map to 99"); // Check if the row maps to 99
    }

    @Test
    public void testInvalidIndexNoRow() {
        CellEntry cell = new CellEntry("A"); // Create a CellEntry with index "A"
        assertFalse(cell.isValid(), "A should be an invalid index (missing row)"); // Check if the index is invalid
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X"); // Check if X returns ERR
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y"); // Check if Y returns ERR
    }

    @Test
    public void testInvalidIndexMultipleLetters() {
        CellEntry cell = new CellEntry("AA1"); // Create a CellEntry with index "AA1"
        assertFalse(cell.isValid(), "AA1 should be an invalid index (multiple letters)"); // Check if the index is invalid
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X"); // Check if X returns ERR
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y"); // Check if Y returns ERR
    }

    @Test
    public void testInvalidIndexNegativeRow() {
        CellEntry cell = new CellEntry("A-1"); // Create a CellEntry with index "A-1"
        assertFalse(cell.isValid(), "A-1 should be an invalid index (negative row)"); // Check if the index is invalid
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X"); // Check if X returns ERR
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y"); // Check if Y returns ERR
    }

    @Test
    public void testInvalidIndexExtraCharacters() {
        CellEntry cell = new CellEntry("A1!"); // Create a CellEntry with index "A1!"
        assertFalse(cell.isValid(), "A1! should be an invalid index (extra characters)"); // Check if the index is invalid
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X"); // Check if X returns ERR
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y"); // Check if Y returns ERR
    }

    @Test
    public void testInvalidIndexEmptyColumn() {
        CellEntry cell = new CellEntry("1"); // Create a CellEntry with index "1"
        assertFalse(cell.isValid(), "1 should be an invalid index (missing column)"); // Check if the index is invalid
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X"); // Check if X returns ERR
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y"); // Check if Y returns ERR
    }

    @Test
    public void testInvalidIndexOutOfBoundsColumn() {
        CellEntry cell = new CellEntry("AA1"); // Create a CellEntry with index "AA1"
        assertFalse(cell.isValid(), "AA1 should be an invalid index (column out of bounds)"); // Check if the index is invalid
        assertEquals(Ex2Utils.ERR, cell.getX(), "Invalid index should return ERR for X"); // Check if X returns ERR
        assertEquals(Ex2Utils.ERR, cell.getY(), "Invalid index should return ERR for Y"); // Check if Y returns ERR
    }

    @Test
    public void testToStringInvalid() {
        CellEntry cell = new CellEntry("1A"); // Create a CellEntry with index "1A"
        assertFalse(cell.isValid(), "1A should be invalid"); // Check if the index
    }
}