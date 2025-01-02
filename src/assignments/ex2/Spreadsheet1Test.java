package assignments.ex2;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class Spreadsheet1Test {

    @Test
    void testParseAddress() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

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
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        // Setting and getting cells
        Cell1 cellA1 = new Cell1("=B1");
        spreadsheet.setCell("A0", cellA1);

        Cell1 retrievedCell = spreadsheet.getCell("A0");
        assertEquals("=B1", retrievedCell.getCell_info());

        Cell1 cellB1 = new Cell1("5");
        spreadsheet.setCell("B1", cellB1);

        retrievedCell = spreadsheet.getCell("B1");
        assertEquals("5", retrievedCell.getCell_info());
    }

    @Test
    void testComputeDepthForNumbersAndText() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        // Numbers and text should have depth 0
        spreadsheet.setCell("A0", new Cell1("5"));
        assertEquals(0, spreadsheet.computeDepth("A0", new HashSet<>()));

        spreadsheet.setCell("B0", new Cell1("Hello"));
        assertEquals(0, spreadsheet.computeDepth("B0", new HashSet<>()));
    }

    @Test
    void testComputeDepthForSimpleFormulas() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        // Simple formulas should have depth 1
        spreadsheet.setCell("A0", new Cell1("=5"));
        assertEquals(1, spreadsheet.computeDepth("A0", new HashSet<>()));

        spreadsheet.setCell("B0", new Cell1("=3+5"));
        assertEquals(1, spreadsheet.computeDepth("B0", new HashSet<>()));
    }

    @Test
    void testComputeDepthWithDependencies() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        spreadsheet.setCell("A0", new Cell1("5"));
        spreadsheet.setCell("B0", new Cell1("=A0+1"));
        spreadsheet.setCell("C0", new Cell1("=B0+1"));

        assertEquals(0, spreadsheet.computeDepth("A0", new HashSet<>()));
        assertEquals(1, spreadsheet.computeDepth("B0", new HashSet<>()));
        assertEquals(2, spreadsheet.computeDepth("C0", new HashSet<>()));
    }

    @Test
    void testComputeDepthWithCycle() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        spreadsheet.setCell("A0", new Cell1("=B0+1"));
        spreadsheet.setCell("B0", new Cell1("=A0+1"));

        assertEquals(-1, spreadsheet.computeDepth("A0", new HashSet<>()));
        assertEquals(-1, spreadsheet.computeDepth("B0", new HashSet<>()));
    }

    @Test
    void testComputeAllDepths() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(3, 3);

        spreadsheet.setCell("A0", new Cell1("5"));
        spreadsheet.setCell("B0", new Cell1("=A0+1"));
        spreadsheet.setCell("C0", new Cell1("=B0+1"));

        int[][] depths = spreadsheet.depth();

        assertEquals(0, depths[0][0]); // A0
        assertEquals(1, depths[0][1]); // B0
        assertEquals(2, depths[0][2]); // C0
    }

    @Test
    void testInvalidCellReferences() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);




        // Valid cell reference
        spreadsheet.setCell("C0", new Cell1("=A0+1")); // Valid reference
        spreadsheet.setCell("A0", new Cell1("5"));
        assertDoesNotThrow(() -> spreadsheet.computeDepth("C0", new HashSet<>()));
    }

    @Test
    void testComplexFormulas() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        // Formula with nested and mixed references
        spreadsheet.setCell("A0", new Cell1("5"));
        spreadsheet.setCell("B0", new Cell1("=A0*2+3"));
        spreadsheet.setCell("C0", new Cell1("=B0/(A0+1)"));

        assertEquals(0, spreadsheet.computeDepth("A0", new HashSet<>()));
        assertEquals(1, spreadsheet.computeDepth("B0", new HashSet<>()));
        assertEquals(2, spreadsheet.computeDepth("C0", new HashSet<>()));
    }

    @Test
    void testCyclicDependencies() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        spreadsheet.setCell("A0", new Cell1("=B0+C0"));
        spreadsheet.setCell("B0", new Cell1("=C0+A0"));
        spreadsheet.setCell("C0", new Cell1("=A0+B0"));

        assertEquals(-1, spreadsheet.computeDepth("A0", new HashSet<>()));
        assertEquals(-1, spreadsheet.computeDepth("B0", new HashSet<>()));
        assertEquals(-1, spreadsheet.computeDepth("C0", new HashSet<>()));
    }
    @Test
    void testInvalidCellAddressDetection() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        assertFalse(spreadsheet.isValidCellAddress("Z100"));
        assertFalse(spreadsheet.isValidCellAddress("A-1"));
    }
    @Test
    void testParseAddressWithInvalidReferences() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        assertThrows(IllegalArgumentException.class, () -> spreadsheet.parseAddress("Z100"));
        assertThrows(IllegalArgumentException.class, () -> spreadsheet.parseAddress("A-1"));
    }

    @Test
    void testEvalEmptyCell() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        // Empty cell
        assertEquals("", spreadsheet.eval(0, 0));
    }

    @Test
    void testEvalNumberCell() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        // Number cell
        spreadsheet.setCell("A0", new Cell1("123"));
        assertEquals("123", spreadsheet.eval(0, 0));
    }

    @Test
    void testEvalTextCell() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        // Text cell
        spreadsheet.setCell("A0", new Cell1("Hello"));
        assertEquals("Hello", spreadsheet.eval(0, 0));
    }

    @Test
    void testEvalSimpleFormula() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        // Simple formula
        spreadsheet.setCell("A0", new Cell1("=3+5"));
        assertEquals("8.0", spreadsheet.eval(0, 0)); // Evaluated result
    }

    @Test
    void testEvalNestedFormula() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        // Nested formula
        spreadsheet.setCell("A0", new Cell1("=3+5"));
        spreadsheet.setCell("B0", new Cell1("=(A0)*2"));
        assertEquals("8.0", spreadsheet.eval(0, 0)); // Evaluated result of A0
        assertEquals("16.0", spreadsheet.eval(0, 1)); // Evaluated result of B0
    }

    @Test
    void testEvalCyclicFormula() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        // Cyclic formula
        spreadsheet.setCell("A0", new Cell1("=B0+1"));
        spreadsheet.setCell("B0", new Cell1("=A0+1"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 0));
        assertTrue(exception.getMessage().contains("Cyclic dependency detected"));
    }

    @Test
    void testEvalInvalidFormula() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

    }

    @Test
    void testEvalFormulaWithDependencies() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        // Formula with valid dependencies
        spreadsheet.setCell("A0", new Cell1("5"));
        spreadsheet.setCell("B0", new Cell1("=A0+10"));

        assertEquals("5", spreadsheet.eval(0, 0)); // Value of A0
        assertEquals("15.0", spreadsheet.eval(0, 1)); // Evaluated result of B0
    }

    @Test
    void testEvalOutOfBoundsCell() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        // Out of bounds
        Exception exception = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(10, 10));
        assertTrue(exception.getMessage().contains("Invalid cell coordinates"));
    }

    @Test
    void testEvalFormulaReferencingInvalidCell() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

    }
    @Test
    void testSetCellInvalidAddress() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        // Invalid address
        assertThrows(IllegalArgumentException.class, () -> spreadsheet.setCell("AA1", new Cell1("5")));
    }
    @Test
    void testEvalWithFormula() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        spreadsheet.setCell("A0", new Cell1("5"));
        spreadsheet.setCell("B0", new Cell1("=(A0+10)"));
        spreadsheet.setCell("C0", new Cell1("=(B0*2)"));

        assertEquals("5", spreadsheet.eval(0, 0));   // A0
        assertEquals("15.0", spreadsheet.eval(0, 1)); // B0
        assertEquals("30.0", spreadsheet.eval(0, 2)); // C0
    }

    @Test
    void testCircularDependency() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        spreadsheet.setCell("A0", new Cell1("=B0+1"));
        spreadsheet.setCell("B0", new Cell1("=C0+1"));
        spreadsheet.setCell("C0", new Cell1("=A0+1"));

        assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 0));
    }
    @Test
    void testComplexNestedFormulas() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        spreadsheet.setCell("A0", new Cell1("5"));
        spreadsheet.setCell("B0", new Cell1("=(A0+10)"));
        spreadsheet.setCell("C0", new Cell1("=((B0*2)+A0)"));

        assertEquals("5", spreadsheet.eval(0, 0));   // A0
        assertEquals("15.0", spreadsheet.eval(0, 1)); // B0
        assertEquals("35.0", spreadsheet.eval(0, 2)); // C0
    }

    @Test
    void testFormulaWithMultipleOperators() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        spreadsheet.setCell("A0", new Cell1("=2+3*4-5/2"));

        assertEquals("11.5", spreadsheet.eval(0, 0)); // Evaluated result
    }

    @Test
    void testSimpleCyclicDependency() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        // Create a simple cycle: A0 -> B0 -> A0
        spreadsheet.setCell("A0", new Cell1("=B0+1"));
        spreadsheet.setCell("B0", new Cell1("=A0+1"));

        // A0 and B0 should both detect a cyclic dependency
        Exception exceptionA = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 0));
        assertTrue(exceptionA.getMessage().contains("Cyclic dependency detected"));

        Exception exceptionB = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 1));
        assertTrue(exceptionB.getMessage().contains("Cyclic dependency detected"));
    }

    @Test
    void testComplexCyclicDependency() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        // Create a complex cycle: A0 -> B0 -> C0 -> A0
        spreadsheet.setCell("A0", new Cell1("=B0+1"));
        spreadsheet.setCell("B0", new Cell1("=C0+1"));
        spreadsheet.setCell("C0", new Cell1("=A0+1"));

        // All cells involved in the cycle should detect a cyclic dependency
        Exception exceptionA = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 0));
        assertTrue(exceptionA.getMessage().contains("Cyclic dependency detected"));

        Exception exceptionB = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 1));
        assertTrue(exceptionB.getMessage().contains("Cyclic dependency detected"));

        Exception exceptionC = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 2));
        assertTrue(exceptionC.getMessage().contains("Cyclic dependency detected"));
    }

    @Test
    void testSelfReferencingCell() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        // Create a self-referencing cell: A0 -> A0
        spreadsheet.setCell("A0", new Cell1("=A0+1"));

        // A0 should detect a cyclic dependency
        Exception exception = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 0));
        assertTrue(exception.getMessage().contains("Cyclic dependency detected"));
    }

    @Test
    void testCyclicDependencyWithIntermediateCells() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        // Create a cycle with intermediate cells: A0 -> B0 -> C0 -> D0 -> A0
        spreadsheet.setCell("A0", new Cell1("=B0+1"));
        spreadsheet.setCell("B0", new Cell1("=C0+1"));
        spreadsheet.setCell("C0", new Cell1("=D0+1"));
        spreadsheet.setCell("D0", new Cell1("=A0+1"));

        // All cells involved in the cycle should detect a cyclic dependency
        Exception exceptionA = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 0));
        assertTrue(exceptionA.getMessage().contains("Cyclic dependency detected"));

        Exception exceptionB = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 1));
        assertTrue(exceptionB.getMessage().contains("Cyclic dependency detected"));

        Exception exceptionC = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 2));
        assertTrue(exceptionC.getMessage().contains("Cyclic dependency detected"));

        Exception exceptionD = assertThrows(IllegalArgumentException.class, () -> spreadsheet.eval(0, 3));
        assertTrue(exceptionD.getMessage().contains("Cyclic dependency detected"));
    }

    @Test
    void testNonCyclicDependencies() {
        Spreadsheet1 spreadsheet = new Spreadsheet1(10, 10);

        // Create a dependency chain without a cycle: A0 -> B0 -> C0
        spreadsheet.setCell("A0", new Cell1("=B0+1"));
        spreadsheet.setCell("B0", new Cell1("=C0+1"));
        spreadsheet.setCell("C0", new Cell1("5"));

        // All cells should compute correctly without a cyclic dependency
        assertEquals("5", spreadsheet.eval(0, 2));  // C0
        assertEquals("6.0", spreadsheet.eval(0, 1)); // B0
        assertEquals("7.0", spreadsheet.eval(0, 0)); // A0
    }


}



