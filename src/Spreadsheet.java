import java.util.HashSet;
import java.util.Set;

public class Spreadsheet {
    private final int rows;
    private final int cols;
    private final Cell[][] cells;

    // Constructor for the spreadsheet
    public Spreadsheet(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new Cell(""); // Initialize empty cells
            }
        }
    }

    public int[] parseAddress(String address) {
        if (address == null || address.length() < 2) {
            throw new IllegalArgumentException("Invalid cell address: " + address);
        }
        char columnChar = Character.toUpperCase(address.charAt(0));
        if (columnChar < 'A' || columnChar > 'J') { // עמודות חוקיות בין A ל-J
            throw new IllegalArgumentException("Invalid column in address: " + address);
        }
        int column = columnChar - 'A';
        int row;
        try {
            row = Integer.parseInt(address.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row in address: " + address);
        }
        if (row < 0 || row >= rows || column < 0 || column >= cols) {
            throw new IllegalArgumentException("Address out of bounds: " + address);
        }
        return new int[]{row, column};
    }





    // Get a cell by its address (e.g., "A1")
    public Cell getCell(String address) {
        int[] indices = parseAddress(address);
        return cells[indices[0]][indices[1]];
    }

    // Set a cell at a specific address
    public void setCell(String address, Cell cell) {
        int[] indices = parseAddress(address);
        cells[indices[0]][indices[1]] = cell;
    }

    public int computeDepth(String address, Set<String> visited) {
        if (visited.contains(address)) {
            return -1; // מחזור זוהה, מחזירים -1
        }

        visited.add(address);

        Cell cell = getCell(address);
        String cellInfo = cell.getCell_info();

        if (!cell.isForm(cellInfo)) {
            visited.remove(address);
            return 0; // תא לא פורמולרי, עומק 0
        }

        String formula = cellInfo.substring(1).trim();
        int maxDepth = 0;

        int operatorIndex = cell.findLowestPriorityOperator(formula);
        if (operatorIndex != -1) {
            String left = formula.substring(0, operatorIndex).trim();
            String right = formula.substring(operatorIndex + 1).trim();

            maxDepth = Math.max(
                    cell.isValidCell(left) ? computeDepth(left, visited) : 0,
                    cell.isValidCell(right) ? computeDepth(right, visited) : 0
            );

            if (maxDepth == -1) {
                return -1; // מחזור נמצא, מחזירים -1
            }
        } else if (cell.isValidCell(formula)) {
            maxDepth = computeDepth(formula, visited);
            if (maxDepth == -1) {
                return -1; // מחזור נמצא, מחזירים -1
            }
        }

        visited.remove(address);
        return maxDepth + 1; // מוסיפים עומק לתא הנוכחי
    }




    // Compute the depth of all cells in the spreadsheet
    public int[][] computeAllDepths() {
        int[][] depths = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Set<String> visited = new HashSet<>();
                String address = getAddress(i, j);
                depths[i][j] = computeDepth(address, visited);
            }
        }
        return depths;
    }

    // Get the address of a cell given its row and column indices
    public String getAddress(int row, int col) {
        char column = (char) ('A' + col);
        return column + String.valueOf(row);
    }

    // Check if a string is a valid cell address
    private boolean isValidCellAddress(String address) {
        try {
            parseAddress(address);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Get the number of rows in the spreadsheet
    public int height() {
        return rows;
    }

    // Get the number of columns in the spreadsheet
    public int width() {
        return cols;
    }

    // Extract the column index (X) from a cell address
    public int xCell(String address) {
        return parseAddress(address)[1];
    }

    // Extract the row index (Y) from a cell address
    public int yCell(String address) {
        return parseAddress(address)[0];
    }
}
