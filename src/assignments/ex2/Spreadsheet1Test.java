package assignments.ex2;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class Spreadsheet1Test {

    @Test
    void testParseAddress() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Valid addresses
        int[] result = spreadsheet.parseAddress("A0"); // Parse address "A0"
        assertArrayEquals(new int[]{0, 0}, result); // Check if the result is [0, 0]

        result = spreadsheet.parseAddress("B1"); // Parse address "B1"
        assertArrayEquals(new int[]{1, 1}, result); // Check if the result is [1, 1]

        result = spreadsheet.parseAddress("J9"); // Parse address "J9"
        assertArrayEquals(new int[]{9, 9}, result); // Check if the result is [9, 9]

        // Invalid addresses
        assertThrows(IllegalArgumentException.class, () -> spreadsheet.parseAddress("1")); // Missing column
        assertThrows(IllegalArgumentException.class, () -> spreadsheet.parseAddress("AA1")); // Invalid column
        assertThrows(IllegalArgumentException.class, () -> spreadsheet.parseAddress("A-1")); // Negative row
        assertThrows(IllegalArgumentException.class, () -> spreadsheet.parseAddress("Z10")); // Out of bounds
    }

    @Test
    void testSetAndGet() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Setting and getting cells
        Cell1 cellA1 = new Cell1("=B1"); // Create a new cell with formula "=B1"
        spreadsheet.setCell("A0", cellA1); // Set cell at address "A0"

        Cell1 retrievedCell = spreadsheet.getCell("A0"); // Get cell at address "A0"
        assertEquals("=B1", retrievedCell.getCell_info()); // Check if the cell info is "=B1"

        Cell1 cellB1 = new Cell1("5"); // Create a new cell with value "5"
        spreadsheet.setCell("B1", cellB1); // Set cell at address "B1"

        retrievedCell = spreadsheet.getCell("B1"); // Get cell at address "B1"
        assertEquals("5", retrievedCell.getCell_info()); // Check if the cell info is "5"
    }

    @Test
    void testComputeDepthForNumbersAndText() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Numbers and text should have depth 0
        spreadsheet.setCell("A0", new Cell1("5")); // Set cell at address "A0" with value "5"
        assertEquals(0, spreadsheet.computeDepth("A0", new HashSet<>())); // Check if the depth is 0

        spreadsheet.setCell("B0", new Cell1("Hello")); // Set cell at address "B0" with value "Hello"
        assertEquals(0, spreadsheet.computeDepth("B0", new HashSet<>())); // Check if the depth is 0
    }

    @Test
    void testComputeDepthForSimpleFormulas() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Simple formulas should have depth 1
        spreadsheet.setCell("A0", new Cell1("=5")); // Set cell at address "A0" with formula "=5"
        assertEquals(1, spreadsheet.computeDepth("A0", new HashSet<>())); // Check if the depth is 1

        spreadsheet.setCell("B0", new Cell1("=3+5")); // Set cell at address "B0" with formula "=3+5"
        assertEquals(1, spreadsheet.computeDepth("B0", new HashSet<>())); // Check if the depth is 1
    }

    @Test
    void testComputeDepthWithDependencies() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        spreadsheet.setCell("A0", new Cell1("5")); // Set cell at address "A0" with value "5"
        spreadsheet.setCell("B0", new Cell1("=A0+1")); // Set cell at address "B0" with formula "=A0+1"
        spreadsheet.setCell("C0", new Cell1("=B0+1")); // Set cell at address "C0" with formula "=B0+1"

        assertEquals(0, spreadsheet.computeDepth("A0", new HashSet<>())); // Check if the depth of A0 is 0
        assertEquals(1, spreadsheet.computeDepth("B0", new HashSet<>())); // Check if the depth of B0 is 1
        assertEquals(2, spreadsheet.computeDepth("C0", new HashSet<>())); // Check if the depth of C0 is 2
    }

    @Test
    void testComputeDepthWithCycle() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        spreadsheet.setCell("A0", new Cell1("=B0+1")); // Set cell at address "A0" with formula "=B0+1"
        spreadsheet.setCell("B0", new Cell1("=A0+1")); // Set cell at address "B0" with formula "=A0+1"

        assertEquals(-1, spreadsheet.computeDepth("A0", new HashSet<>())); // Check if the depth of A0 is -1 (cycle)
        assertEquals(-1, spreadsheet.computeDepth("B0", new HashSet<>())); // Check if the depth of B0 is -1 (cycle)
    }

    @Test
    void testComputeAllDepths() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(3, 3); // Create a new spreadsheet with dimensions 3x3

        spreadsheet.setCell("A0", new Cell1("5")); // Set cell at address "A0" with value "5"
        spreadsheet.setCell("B0", new Cell1("=A0+1")); // Set cell at address "B0" with formula "=A0+1"
        spreadsheet.setCell("C0", new Cell1("=B0+1")); // Set cell at address "C0" with formula "=B0+1"

        int[][] depths = spreadsheet.depth(); // Compute the depths of all cells

        assertEquals(0, depths[0][0]); // Check if the depth of A0 is 0
        assertEquals(1, depths[0][1]); // Check if the depth of B0 is 1
        assertEquals(2, depths[0][2]); // Check if the depth of C0 is 2
    }

    @Test
    void testInvalidCellReferences() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Valid cell reference
        spreadsheet.setCell("C0", new Cell1("=A0+1")); // Set cell at address "C0" with formula "=A0+1"
        spreadsheet.setCell("A0", new Cell1("5")); // Set cell at address "A0" with value "5"
        assertDoesNotThrow(() -> spreadsheet.computeDepth("C0", new HashSet<>())); // Check if no exception is thrown
    }

    @Test
    void testComplexFormulas() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Formula with nested and mixed references
        spreadsheet.setCell("A0", new Cell1("5")); // Set cell at address "A0" with value "5"
        spreadsheet.setCell("B0", new Cell1("=A0*2+3")); // Set cell at address "B0" with formula "=A0*2+3"
        spreadsheet.setCell("C0", new Cell1("=B0/(A0+1)")); // Set cell at address "C0" with formula "=B0/(A0+1)"

        assertEquals(0, spreadsheet.computeDepth("A0", new HashSet<>())); // Check if the depth of A0 is 0
        assertEquals(1, spreadsheet.computeDepth("B0", new HashSet<>())); // Check if the depth of B0 is 1
        assertEquals(2, spreadsheet.computeDepth("C0", new HashSet<>())); // Check if the depth of C0 is 2
    }

    @Test
    void testCyclicDependencies() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        spreadsheet.setCell("A0", new Cell1("=B0+C0")); // Set cell at address "A0" with formula "=B0+C0"
        spreadsheet.setCell("B0", new Cell1("=C0+A0")); // Set cell at address "B0" with formula "=C0+A0"
        spreadsheet.setCell("C0", new Cell1("=A0+B0")); // Set cell at address "C0" with formula "=A0+B0"

        assertEquals(-1, spreadsheet.computeDepth("A0", new HashSet<>())); // Check if the depth of A0 is -1 (cycle)
        assertEquals(-1, spreadsheet.computeDepth("B0", new HashSet<>())); // Check if the depth of B0 is -1 (cycle)
        assertEquals(-1, spreadsheet.computeDepth("C0", new HashSet<>())); // Check if the depth of C0 is -1 (cycle)
    }

    @Test
    void testInvalidCellAddressDetection() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        assertFalse(spreadsheet.isValidCellAddress("Z100")); // Check if "Z100" is an invalid cell address
        assertFalse(spreadsheet.isValidCellAddress("A-1")); // Check if "A-1" is an invalid cell address
    }

    @Test
    void testParseAddressWithInvalidReferences() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        assertThrows(IllegalArgumentException.class, () -> spreadsheet.parseAddress("Z100")); // Check if parsing "Z100" throws an exception
        assertThrows(IllegalArgumentException.class, () -> spreadsheet.parseAddress("A-1")); // Check if parsing "A-1" throws an exception
    }

    @Test
    void testEvalEmptyCell() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Empty cell
        assertEquals("", spreadsheet.eval(0, 0)); // Check if the value of an empty cell is ""
    }

    @Test
    void testEvalNumberCell() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Number cell
        spreadsheet.setCell("A0", new Cell1("123")); // Set cell at address "A0" with value "123"
        assertEquals("123", spreadsheet.eval(0, 0)); // Check if the value of the cell is "123"
    }

    @Test
    void testEvalTextCell() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Text cell
        spreadsheet.setCell("A0", new Cell1("Hello")); // Set cell at address "A0" with value "Hello"
        assertEquals("Hello", spreadsheet.eval(0, 0)); // Check if the value of the cell is "Hello"
    }

    @Test
    void testEvalSimpleFormula() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Simple formula
        spreadsheet.setCell("A0", new Cell1("=3+5")); // Set cell at address "A0" with formula "=3+5"
        assertEquals("8.0", spreadsheet.eval(0, 0)); // Check if the evaluated result is "8.0"
    }

    @Test
    void testEvalNestedFormula() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Nested formula
        spreadsheet.setCell("A0", new Cell1("=3+5")); // Set cell at address "A0" with formula "=3+5"
        spreadsheet.setCell("B0", new Cell1("=(A0)*2")); // Set cell at address "B0" with formula "=(A0)*2"
        assertEquals("8.0", spreadsheet.eval(0, 0)); // Check if the evaluated result of A0 is "8.0"
        assertEquals("16.0", spreadsheet.eval(0, 1)); // Check if the evaluated result of B0 is "16.0"
    }

    @Test
    void testEvalCyclicFormula() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Cyclic formula
        spreadsheet.setCell("A0", new Cell1("=B0+1")); // Set cell at address "A0" with formula "=B0+1"
        spreadsheet.setCell("B0", new Cell1("=A0+1")); // Set cell at address "B0" with formula "=A0+1"

        Exception exception = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 0)); // Check if evaluating A0 throws an exception
        assertTrue(exception.getMessage().contains("Cyclic dependency detected")); // Check if the exception message contains "Cyclic dependency detected"
    }

    @Test
    void testEvalInvalidFormula() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Add your test logic here
    }

    @Test
    void testEvalFormulaWithDependencies() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Formula with valid dependencies
        spreadsheet.setCell("A0", new Cell1("5")); // Set cell at address "A0" with value "5"
        spreadsheet.setCell("B0", new Cell1("=A0+10")); // Set cell at address "B0" with formula "=A0+10"

        assertEquals("5", spreadsheet.eval(0, 0)); // Check if the value of A0 is "5"
        assertEquals("15.0", spreadsheet.eval(0, 1)); // Check if the evaluated result of B0 is "15.0"
    }

    @Test
    void testEvalOutOfBoundsCell() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Out of bounds
        Exception exception = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(10, 10)); // Check if evaluating out of bounds throws an exception
        assertTrue(exception.getMessage().contains("Invalid cell coordinates")); // Check if the exception message contains "Invalid cell coordinates"
    }

    @Test
    void testEvalFormulaReferencingInvalidCell() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Add your test logic here
    }

    @Test
    void testSetCellInvalidAddress() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Invalid address
        assertThrows(IllegalArgumentException.class, () -> spreadsheet.setCell("AA1", new Cell1("5"))); // Check if setting cell at invalid address throws an exception
    }

    @Test
    void testEvalWithFormula() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        spreadsheet.setCell("A0", new Cell1("5")); // Set cell at address "A0" with value "5"
        spreadsheet.setCell("B0", new Cell1("=(A0+10)")); // Set cell at address "B0" with formula "=(A0+10)"
        spreadsheet.setCell("C0", new Cell1("=(B0*2)")); // Set cell at address "C0" with formula "=(B0*2)"

        assertEquals("5", spreadsheet.eval(0, 0));   // Check if the value of A0 is "5"
        assertEquals("15.0", spreadsheet.eval(0, 1)); // Check if the evaluated result of B0 is "15.0"
        assertEquals("30.0", spreadsheet.eval(0, 2)); // Check if the evaluated result of C0 is "30.0"
    }

    @Test
    void testCircularDependency() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        spreadsheet.setCell("A0", new Cell1("=B0+1")); // Set cell at address "A0" with formula "=B0+1"
        spreadsheet.setCell("B0", new Cell1("=C0+1")); // Set cell at address "B0" with formula "=C0+1"
        spreadsheet.setCell("C0", new Cell1("=A0+1")); // Set cell at address "C0" with formula "=A0+1"

        assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 0)); // Check if evaluating A0 throws an exception
    }

    @Test
    void testComplexNestedFormulas() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        spreadsheet.setCell("A0", new Cell1("5")); // Set cell at address "A0" with value "5"
        spreadsheet.setCell("B0", new Cell1("=(A0+10)")); // Set cell at address "B0" with formula "=(A0+10)"
        spreadsheet.setCell("C0", new Cell1("=((B0*2)+A0)")); // Set cell at address "C0" with formula "=((B0*2)+A0)"

        assertEquals("5", spreadsheet.eval(0, 0));   // Check if the value of A0 is "5"
        assertEquals("15.0", spreadsheet.eval(0, 1)); // Check if the evaluated result of B0 is "15.0"
        assertEquals("35.0", spreadsheet.eval(0, 2)); // Check if the evaluated result of C0 is "35.0"
    }

    @Test
    void testFormulaWithMultipleOperators() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        spreadsheet.setCell("A0", new Cell1("=2+3*4-5/2")); // Set cell at address "A0" with formula "=2+3*4-5/2"

        assertEquals("11.5", spreadsheet.eval(0, 0)); // Check if the evaluated result is "11.5"
    }

    @Test
    void testSimpleCyclicDependency() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Create a simple cycle: A0 -> B0 -> A0
        spreadsheet.setCell("A0", new Cell1("=B0+1")); // Set cell at address "A0" with formula "=B0+1"
        spreadsheet.setCell("B0", new Cell1("=A0+1")); // Set cell at address "B0" with formula "=A0+1"

        // A0 and B0 should both detect a cyclic dependency
        Exception exceptionA = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 0)); // Check if evaluating A0 throws an exception
        assertTrue(exceptionA.getMessage().contains("Cyclic dependency detected")); // Check if the exception message contains "Cyclic dependency detected"

        Exception exceptionB = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 1)); // Check if evaluating B0 throws an exception
        assertTrue(exceptionB.getMessage().contains("Cyclic dependency detected")); // Check if the exception message contains "Cyclic dependency detected"
    }

    @Test
    void testComplexCyclicDependency() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Create a complex cycle: A0 -> B0 -> C0 -> A0
        spreadsheet.setCell("A0", new Cell1("=B0+1")); // Set cell at address "A0" with formula "=B0+1"
        spreadsheet.setCell("B0", new Cell1("=C0+1")); // Set cell at address "B0" with formula "=C0+1"
        spreadsheet.setCell("C0", new Cell1("=A0+1")); // Set cell at address "C0" with formula "=A0+1"

        // All cells involved in the cycle should detect a cyclic dependency
        Exception exceptionA = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 0)); // Check if evaluating A0 throws an exception
        assertTrue(exceptionA.getMessage().contains("Cyclic dependency detected")); // Check if the exception message contains "Cyclic dependency detected"

        Exception exceptionB = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 1)); // Check if evaluating B0 throws an exception
        assertTrue(exceptionB.getMessage().contains("Cyclic dependency detected")); // Check if the exception message contains "Cyclic dependency detected"

        Exception exceptionC = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 2)); // Check if evaluating C0 throws an exception
        assertTrue(exceptionC.getMessage().contains("Cyclic dependency detected")); // Check if the exception message contains "Cyclic dependency detected"
    }

    @Test
    void testSelfReferencingCell() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Create a self-referencing cell: A0 -> A0
        spreadsheet.setCell("A0", new Cell1("=A0+1")); // Set cell at address "A0" with formula "=A0+1"

        // A0 should detect a cyclic dependency
        Exception exception = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 0)); // Check if evaluating A0 throws an exception
        assertTrue(exception.getMessage().contains("Cyclic dependency detected")); // Check if the exception message contains "Cyclic dependency detected"
    }

    @Test
    void testCyclicDependencyWithIntermediateCells() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Create a cycle with intermediate cells: A0 -> B0 -> C0 -> D0 -> A0
        spreadsheet.setCell("A0", new Cell1("=B0+1")); // Set cell at address "A0" with formula "=B0+1"
        spreadsheet.setCell("B0", new Cell1("=C0+1")); // Set cell at address "B0" with formula "=C0+1"
        spreadsheet.setCell("C0", new Cell1("=D0+1")); // Set cell at address "C0" with formula "=D0+1"
        spreadsheet.setCell("D0", new Cell1("=A0+1")); // Set cell at address "D0" with formula "=A0+1"

        // All cells involved in the cycle should detect a cyclic dependency
        Exception exceptionA = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 0)); // Check if evaluating A0 throws an exception
        assertTrue(exceptionA.getMessage().contains("Cyclic dependency detected")); // Check if the exception message contains "Cyclic dependency detected"

        Exception exceptionB = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 1)); // Check if evaluating B0 throws an exception
        assertTrue(exceptionB.getMessage().contains("Cyclic dependency detected")); // Check if the exception message contains "Cyclic dependency detected"

        Exception exceptionC = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 2)); // Check if evaluating C0 throws an exception
        assertTrue(exceptionC.getMessage().contains("Cyclic dependency detected")); // Check if the exception message contains "Cyclic dependency detected"

        Exception exceptionD = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 3)); // Check if evaluating D0 throws an exception
        assertTrue(exceptionD.getMessage().contains("Cyclic dependency detected")); // Check if the exception message contains "Cyclic dependency detected"
    }

    @Test
    void testNonCyclicDependencies() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10); // Create a new spreadsheet with dimensions 10x10

        // Create a dependency chain without a cycle: A0 -> B0 -> C0
        spreadsheet.setCell("A0", new Cell1("=B0+1")); // Set cell at address "A0" with formula "=B0+1"
        spreadsheet.setCell("B0", new Cell1("=C0+1")); // Set cell at address "B0" with formula "=C0+1"
        spreadsheet.setCell("C0", new Cell1("5")); // Set cell at address "C0" with value "5"

        // All cells should compute correctly without a cyclic dependency
        assertEquals("5", spreadsheet.eval(0, 2));  // Check if the value of C0 is "5"
        assertEquals("6.0", spreadsheet.eval(0, 1)); // Check if the evaluated result of B0 is "6.0"
        assertEquals("7.0", spreadsheet.eval(0, 0)); // Check if the evaluated result of A0 is "7.0"
    }
    }