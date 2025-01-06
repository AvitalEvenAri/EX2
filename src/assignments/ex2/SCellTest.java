package assignments.ex2;

import static org.junit.jupiter.api.Assertions.*; // Assertions for JUnit 5
import org.junit.jupiter.api.Test; // Test annotation for JUnit 5
import assignments.ex2.Ex2Utils; // Ex2Utils constants and definitions
import assignments.ex2.SCell; // The SCell implementation

public class SCellTest {

    // Existing Tests
    @Test
    public void testSetDataForNumber() {
        SCell cell = new SCell("");
        cell.setData("42.5");
        assertEquals("42.5", cell.getData());
        assertEquals(Ex2Utils.NUMBER, cell.getType());
        assertEquals(0, cell.getOrder());
    }

    @Test
    public void testSetDataForText() {
        SCell cell = new SCell("");
        cell.setData("Hello World");
        assertEquals("Hello World", cell.getData());
        assertEquals(Ex2Utils.TEXT, cell.getType());
        assertEquals(0, cell.getOrder());
    }

    @Test
    public void testSetDataForValidFormula() {
        SCell cell = new SCell("");
        cell.setData("=5+3");
        assertEquals("=5+3", cell.getData());
        assertEquals(Ex2Utils.FORM, cell.getType());
        assertEquals(-1, cell.getOrder());
    }

    @Test
    public void testSetDataForInvalidFormula() {
        SCell cell = new SCell("");
        cell.setData("=5+"); // Invalid formula
        assertEquals("=5+", cell.getData());
        assertEquals(Ex2Utils.ERR_FORM_FORMAT, cell.getType());
        assertEquals(-1, cell.getOrder());
    }

    @Test
    public void testSetDataForCycleError() {
        SCell cell = new SCell("");
        cell.setType(Ex2Utils.ERR_CYCLE_FORM);
        assertEquals(Ex2Utils.ERR_CYCLE_FORM, cell.getType());
        assertEquals(-1, cell.getOrder());
    }

    @Test
    public void testSetDataForWrongFormulaError() {
        SCell cell = new SCell("");
        cell.setData("=A1+"); // Invalid formula
        assertEquals("=A1+", cell.getData());
        assertEquals(Ex2Utils.ERR_FORM_FORMAT, cell.getType());
        assertEquals(-1, cell.getOrder());
    }

    @Test
    public void testSetOrder() {
        SCell cell = new SCell("=A1+A2");
        cell.setOrder(5);
        assertEquals(5, cell.getOrder());
    }

    @Test
    public void testToString() {
        SCell cell = new SCell("Test String");
        assertEquals("Test String", cell.toString());
    }

    // Additional Tests
    @Test
    public void testSetDataForEmptyString() {
        SCell cell = new SCell("");
        cell.setData("");
        assertEquals("", cell.getData());
        assertEquals(Ex2Utils.TEXT, cell.getType());
        assertEquals(0, cell.getOrder());
    }


    @Test
    public void testSetDataForNull() {
        SCell cell = new SCell("");
        cell.setData(null);
        assertNull(cell.getData()); // Verify the data is null
        assertEquals(Ex2Utils.ERR_FORM_FORMAT, cell.getType()); // Verify the type is an error
        assertEquals(-1, cell.getOrder()); // Verify the order is -1
    }

    @Test
    public void testSetDataForUnbalancedParentheses() {
        SCell cell = new SCell("");
        cell.setData("=(3+5");
        assertEquals("=(3+5", cell.getData());
        assertEquals(Ex2Utils.ERR_FORM_FORMAT, cell.getType());
        assertEquals(-1, cell.getOrder());
    }

    @Test
    public void testSetDataForExtraClosingParenthesis() {
        SCell cell = new SCell("");
        cell.setData("=3+5)");
        assertEquals("=3+5)", cell.getData());
        assertEquals(Ex2Utils.ERR_FORM_FORMAT, cell.getType());
        assertEquals(-1, cell.getOrder());
    }

    @Test
    public void testSetDataForLargeNumber() {
        SCell cell = new SCell("");
        cell.setData(String.valueOf(Double.MAX_VALUE));
        assertEquals(String.valueOf(Double.MAX_VALUE), cell.getData());
        assertEquals(Ex2Utils.NUMBER, cell.getType());
        assertEquals(0, cell.getOrder());
    }

    @Test
    public void testSetDataForNegativeNumber() {
        SCell cell = new SCell("");
        cell.setData("-123.456");
        assertEquals("-123.456", cell.getData());
        assertEquals(Ex2Utils.NUMBER, cell.getType());
        assertEquals(0, cell.getOrder());
    }

    @Test
    public void testSetDataForValidCellReferenceFormula() {
        SCell cell = new SCell("");
        cell.setData("=A1+B2");
        assertEquals("=A1+B2", cell.getData());
        assertEquals(Ex2Utils.FORM, cell.getType());
        assertEquals(-1, cell.getOrder());
    }

    @Test
    public void testSetOrderForNonFormulaCell() {
        SCell cell = new SCell("123");
        cell.setOrder(5);
        assertEquals(0, cell.getOrder());
    }

    @Test
    public void testSetDataForComplexFormula() {
        SCell cell = new SCell("");
        cell.setData("=(3+5)*2");
        assertEquals("=(3+5)*2", cell.getData());
        assertEquals(Ex2Utils.FORM, cell.getType());
        assertEquals(-1, cell.getOrder());
    }
    @Test
    public void testToStringForEmptyAndNonEmptyCells() {
        SCell cell = new SCell("");
        assertEquals("", cell.toString(), "toString should return an empty string for an empty cell");

        cell.setData("Hello");
        assertEquals("Hello", cell.toString(), "toString should return the data for a non-empty cell");

        cell.setComputedValue("42");
        assertEquals("42", cell.toString(), "toString should return the computed value if available");
    }
    @Test
    public void testComputedValueForValidFormula() {
        SCell cell = new SCell("=5+3");
        cell.setComputedValue("8");
        assertEquals("8", cell.toString(), "Computed value should be 8 for the formula =5+3");
    }
    @Test
    public void testTextCellHandling() {
        SCell cell = new SCell("Hello");
        assertEquals("Hello", cell.getData(), "Data should be Hello for a text cell");
        assertEquals(Ex2Utils.TEXT, cell.getType(), "Type should be TEXT for a text cell");
    }
    @Test
    public void testInvalidFormulaHandling() {
        SCell cell = new SCell("=5+");
        assertEquals("=5+", cell.getData(), "Data should remain =5+ for an invalid formula");
        assertEquals(Ex2Utils.ERR_FORM_FORMAT, cell.getType(), "Type should be ERR_FORM_FORMAT for an invalid formula");
    }

}

