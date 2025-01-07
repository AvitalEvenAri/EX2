package assignments.ex2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

public class Ex2SheetTest {

    @Test
    public void testSetAndGetValue() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5); // Create a new sheet with dimensions 5x5

        sheet.set(0, 0, "5"); // Set value "5" at cell (0, 0)
        sheet.set(1, 1, "10"); // Set value "10" at cell (1, 1)
        assertEquals("5", sheet.value(0, 0)); // Check if the value at cell (0, 0) is "5"
        assertEquals("10", sheet.value(1, 1)); // Check if the value at cell (1, 1) is "10"
    }

    @Test
    public void testSetAndGetFormula() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5); // Create a new sheet with dimensions 5x5

        sheet.set(0, 0, "=5+10"); // Set formula "=5+10" at cell (0, 0)
        assertEquals("15.00", sheet.value(0, 0)); // Check if the value at cell (0, 0) is "15.00"
    }

    @Test
    public void testCellReference() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5); // Create a new sheet with dimensions 5x5

        sheet.set(0, 0, "5"); // Set value "5" at cell (0, 0)
        sheet.set(1, 1, "=A0+10"); // Set formula "=A0+10" at cell (1, 1)
        assertEquals("15.00", sheet.value(1, 1)); // Check if the value at cell (1, 1) is "15.00"
    }

    @Test
    public void testComplexFormula() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5); // Create a new sheet with dimensions 5x5

        sheet.set(0, 0, "=5+10*2"); // Set formula "=5+10*2" at cell (0, 0)
        assertEquals("25.00", sheet.value(0, 0)); // Check if the value at cell (0, 0) is "25.00"
    }

    @Test
    public void testDepthComputation() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5); // Create a new sheet with dimensions 5x5

        sheet.set(0, 0, "10"); // Set value "10" at cell (0, 0)
        sheet.set(1, 1, "=A0+5"); // Set formula "=A0+5" at cell (1, 1)
        sheet.set(2, 2, "=B1*2"); // Set formula "=B1*2" at cell (2, 2)

        int[][] depths = sheet.depth(); // Compute the depths of all cells

        assertEquals(0, depths[0][0]); // Check if the depth of cell (0, 0) is 0
        assertEquals(1, depths[1][1]); // Check if the depth of cell (1, 1) is 1
        assertEquals(2, depths[2][2]); // Check if the depth of cell (2, 2) is 2
    }

    @Test
    public void testCircularDependency() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5); // Create a new sheet with dimensions 5x5

        sheet.set(0, 0, "=B1"); // Set formula "=B1" at cell (0, 0)
        sheet.set(1, 1, "=A0"); // Set formula "=A0" at cell (1, 1)

        int[][] depths = sheet.depth(); // Compute the depths of all cells

        assertEquals(Ex2Utils.ERR_CYCLE_FORM, depths[0][0]); // Check if the depth of cell (0, 0) is ERR_CYCLE_FORM
        assertEquals(Ex2Utils.ERR_CYCLE_FORM, depths[1][1]); // Check if the depth of cell (1, 1) is ERR_CYCLE_FORM

        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 0)); // Check if the value at cell (0, 0) is ERR_CYCLE
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(1, 1)); // Check if the value at cell (1, 1) is ERR_CYCLE
    }

    @Test
    public void testDivisionByZero() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5); // Create a new sheet with dimensions 5x5

        sheet.set(0, 0, "=10/0"); // Set formula "=10/0" at cell (0, 0)
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(0, 0)); // Check if the value at cell (0, 0) is ERR_FORM
    }

    @Test
    public void testInvalidCellReference() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5); // Create a new sheet with dimensions 5x5

        sheet.set(1, 1, "=Z100"); // Set formula "=Z100" at cell (1, 1)
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(1, 1)); // Check if the value at cell (1, 1) is ERR_FORM
    }

    @Test
    public void testNegativeNumbers() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5); // Create a new sheet with dimensions 5x5

        sheet.set(0, 0, "10"); // Set value "10" at cell (0, 0)
        sheet.set(1, 1, "=-5"); // Set formula "=-5" at cell (1, 1)
        assertEquals("-5.00", sheet.value(1, 1)); // Check if the value at cell (1, 1) is "-5.00"

        sheet.set(2, 2, "=A0+B1"); // Set formula "=A0+B1" at cell (2, 2)
        assertEquals("5.00", sheet.value(2, 2)); // Check if the value at cell (2, 2) is "5.00"
    }

    @Test
    public void testFormulaError() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5); // Create a new sheet with dimensions 5x5

        sheet.set(0, 0, "=5++10"); // Set formula "=5++10" at cell (0, 0)
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(0, 0)); // Check if the value at cell (0, 0) is ERR_FORM

        sheet.set(1, 1, "=()"); // Set formula "=()" at cell (1, 1)
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(1, 1)); // Check if the value at cell (1, 1) is ERR_FORM

        sheet.set(2, 2, "=5+"); // Set formula "=5+" at cell (2, 2)
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(2, 2)); // Check if the value at cell (2, 2) is ERR_FORM
    }

    @Test
    public void testSelfReference() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5); // Create a new sheet with dimensions 5x5

        sheet.set(0, 0, "=A0"); // Set formula "=A0" at cell (0, 0)
        int[][] depths = sheet.depth(); // Compute the depths of all cells
        assertEquals(Ex2Utils.ERR_CYCLE_FORM, depths[0][0]); // Check if the depth of cell (0, 0) is ERR_CYCLE_FORM
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 0)); // Check if the value at cell (0, 0) is ERR_CYCLE
    }

    @Test
    public void testCyclicDependencyThreeCells() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5); // Create a new sheet with dimensions 5x5

        sheet.set(0, 0, "=B1"); // Set formula "=B1" at cell (0, 0)
        sheet.set(1, 1, "=C2"); // Set formula "=C2" at cell (1, 1)
        sheet.set(2, 2, "=A0"); // Set formula "=A0" at cell (2, 2)

        int[][] depths = sheet.depth(); // Compute the depths of all cells

        assertEquals(Ex2Utils.ERR_CYCLE_FORM, depths[0][0]); // Check if the depth of cell (0, 0) is ERR_CYCLE_FORM
        assertEquals(Ex2Utils.ERR_CYCLE_FORM, depths[1][1]); // Check if the depth of cell (1, 1) is ERR_CYCLE_FORM
        assertEquals(Ex2Utils.ERR_CYCLE_FORM, depths[2][2]); // Check if the depth of cell (2, 2) is ERR_CYCLE_FORM

        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 0)); // Check if the value at cell (0, 0) is ERR_CYCLE
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(1, 1)); // Check if the value at cell (1, 1) is ERR_CYCLE
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(2, 2)); // Check if the value at cell (2, 2) is ERR_CYCLE
    }

    @Test
    public void testNonNumericFormula() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5); // Create a new sheet with dimensions 5x5

        sheet.set(0, 0, "=Hello+10"); // Set formula "=Hello+10" at cell (0, 0)
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(0, 0)); // Check if the value at cell (0, 0) is ERR_FORM
    }

    @Test
    public void testSaveAndLoad() throws IOException {
        Ex2Sheet sheet = new Ex2Sheet(5, 5); // Create a new sheet with dimensions 5x5

        sheet.set(0, 0, "5"); // Set value "5" at cell (0, 0)
        sheet.set(1, 1, "=A0+10"); // Set formula "=A0+10" at cell (1, 1)
        sheet.set(2, 2, "=B1*2"); // Set formula "=B1*2" at cell (2, 2)

        String fileName = "testSheet.csv"; // Define the file name
        sheet.save(fileName); // Save the sheet to a file

        Ex2Sheet loadedSheet = new Ex2Sheet(5, 5); // Create a new sheet with dimensions 5x5
        loadedSheet.load(fileName); // Load the sheet from the file

        assertEquals("5", loadedSheet.value(0, 0)); // Check if the value at cell (0, 0) is "5"
        assertEquals("15.00", loadedSheet.value(1, 1)); // Check if the value at cell (1, 1) is "15.00"
        assertEquals("30.00", loadedSheet.value(2, 2)); // Check if the value at cell (2, 2) is "30.00"

        new File(fileName).delete(); // Delete the file
    }
    }