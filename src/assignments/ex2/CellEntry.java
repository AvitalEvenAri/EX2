package assignments.ex2;

// Represents a 2D cell index in a spreadsheet
public class CellEntry implements Index2D {
    private final String index; // The raw index representation (e.g., "B3")

    /**
     * Constructor for CellEntry.
     * @param index The index in string format (e.g., "B3").
     */
    public CellEntry(String index) {
        this.index = index;
    }

    /**
     * Checks if the index is valid.
     * A valid index has:
     * - A letter in the first character (A-Z or a-z).
     * - A number between 0 and 99 in the remaining characters.
     */
    @Override
    public boolean isValid() {
        if (index == null || index.length() < 2) {
            return false; // Must be at least 2 characters
        }

        char column = Character.toUpperCase(index.charAt(0)); // Extract the first character
        if (column < 'A' || column > 'Z') {
            return false; // The first character must be a letter
        }

        try {
            int row = Integer.parseInt(index.substring(1)); // Parse the numeric part
            return row >= 0 && row <= 99; // Row must be within range
        } catch (NumberFormatException e) {
            return false; // Numeric part is invalid
        }
    }

    /**
     * Returns the column index as a number (0-based).
     * For example, "A" -> 0, "B" -> 1, ..., "Z" -> 25.
     */
    @Override
    public int getX() {
        if (!isValid()) {
            return Ex2Utils.ERR; // Return error constant if invalid
        }

        char column = Character.toUpperCase(index.charAt(0)); // Convert to uppercase
        return column - 'A'; // Convert letter to 0-based index
    }

    /**
     * Returns the row index as a number.
     * For example, "B3" -> 3.
     */
    @Override
    public int getY() {
        if (!isValid()) {
            return Ex2Utils.ERR; // Return error constant if invalid
        }

        try {
            return Integer.parseInt(index.substring(1)); // Parse the numeric part
        } catch (NumberFormatException e) {
            return Ex2Utils.ERR; // Return error if parsing fails
        }
    }

    /**
     * Returns the index in the spreadsheet format.
     * For example, "B3".
     */
    @Override
    public String toString() {
        return index; // Return the raw index
    }
}
