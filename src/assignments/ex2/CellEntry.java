package assignments.ex2;

public class CellEntry implements Index2D {
    private String index; // Cell address (e.g., "B3")

    // Constructor for CellEntry
    public CellEntry(String index) {
        if (index != null && index.endsWith("=")) {
            this.index = index.substring(0, index.length() - 1); // Remove trailing '=' if present
        } else {
            this.index = index; // Set index
        }
        System.out.println("CellEntry created with index: " + this.index); // Debugging
    }


    @Override
    public boolean isValid() {
        if (index == null || index.length() < 2) {
            System.out.println("isValid: false (index is null or too short)"); // Debugging
            return false; // Invalid if null or too short
        }
        char column = Character.toUpperCase(index.charAt(0)); // Get column character
        if (column < 'A' || column > 'Z') {
            System.out.println("isValid: false (column out of range: " + column + ")"); // Debugging
            return false; // Invalid if column out of range
        }

        try {
            String numericPart = index.substring(1).trim(); // Get numeric part
            int row = Integer.parseInt(numericPart); // Parse row number
            boolean valid = row >= 0 && row <= 99; // Check if row is within range
            System.out.println("isValid: " + valid + " (row: " + row + ")"); // Debugging
            return valid; // Return validity
        } catch (Exception e) {
            System.out.println("isValid: false (exception while parsing row)"); // Debugging
            return false; // Invalid if exception occurs
        }
    }

    @Override
    public int getX() {
        if (!isValid()) {
            System.out.println("getX: invalid index, returning ERR"); // Debugging
            return Ex2Utils.ERR; // Return error if invalid
        }
        char column = Character.toUpperCase(index.charAt(0)); // Get column character
        int x = column - 'A'; // Convert column to index
        System.out.println("getX: " + x); // Debugging
        return x; // Return column index
    }

    @Override
    public int getY() {
        if (!isValid()) {
            System.out.println("getY: invalid index, returning ERR"); // Debugging
            return Ex2Utils.ERR; // Return error if invalid
        }
        try {
            String numericPart = index.substring(1).trim(); // Get numeric part
            int y = Integer.parseInt(numericPart); // Parse row number
            System.out.println("getY: " + y); // Debugging
            return y; // Return row index
        } catch (NumberFormatException e) {
            System.out.println("getY: exception while parsing row, returning ERR"); // Debugging
            return Ex2Utils.ERR; // Return error if exception occurs
        }
    }

    @Override
    public String toString() {
        if (this.getY() < 0 || this.getY() >= Ex2Utils.ABC.length) {
            return "null="; // Return null if row index is out of range
        }
        // Ex2Utils.ABC[this.getY()] + this.getX()
        return Ex2Utils.ABC[this.getX()] + this.getY(); // Return cell address
    }
}