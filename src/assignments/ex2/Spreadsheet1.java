package assignments.ex2;

import java.util.HashSet;
import java.util.Set;

public class Spreadsheet1 {
    private final int rows; // Number of rows in the spreadsheet
    private final int cols; // Number of columns in the spreadsheet
    private final Cell1[][] cells; // 2D array to store cells

    // Constructor for the spreadsheet
    public Spreadsheet1(int rows, int cols) {
        this.rows = rows; // Initialize rows
        this.cols = cols; // Initialize columns
        this.cells = new Cell1[rows][cols]; // Initialize the 2D array of cells

        // Initialize all cells as empty
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new Cell1(""); // Initialize empty cells
            }
        }
    }

    // Get the cell at the specified coordinates
    public Cell1 get(int x, int y) {
        String address = getAddress(x, y); // Convert coordinates to address
        return getCell(address); // Retrieve the cell by address
    }

    // Set the cell at the specified coordinates
    public void set(int x, int y, Cell1 c) {
        String address = getAddress(x, y); // Convert coordinates to address
        setCell(address, c); // Set the cell by address
    }

    // Get the number of columns
    public int width() {
        return cols;
    }

    // Get the number of rows
    public int height() {
        return rows;
    }

    // Get the column index from a cell address
    public int xCell(String c) {
        return parseAddress(c)[1]; // Column index
    }

    // Get the row index from a cell address
    public int yCell(String c) {
        return parseAddress(c)[0]; // Row index
    }

    // Converts a cell address (e.g., "A1") into row and column indices
    public int[] parseAddress(String address) {
        if (address == null || address.length() < 2) {
            throw new IllegalArgumentException("Invalid cell address: " + address);
        }
        char columnChar = Character.toUpperCase(address.charAt(0)); // Get the column character
        if (columnChar < 'A' || columnChar > 'J') { // Columns A-J
            throw new IllegalArgumentException("Invalid column in address: " + address);
        }
        int column = columnChar - 'A'; // Convert column character to index
        int row;
        try {
            row = Integer.parseInt(address.substring(1)); // Convert row part to integer
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row in address: " + address);
        }
        if (row < 0 || row >= 100) { // Rows 0-99
            throw new IllegalArgumentException("Row out of bounds: " + address);
        }
        return new int[]{row, column}; // Return row and column indices
    }

    // Retrieves a cell by its address
    public Cell1 getCell(String address) {
        int[] indices = parseAddress(address); // Parse the address to get indices
        return cells[indices[0]][indices[1]]; // Return the cell at the specified indices
    }

    // Sets the content of a cell after validating its address and content
    public void setCell(String address, Cell1 cell) {
        if (!isValidCellAddress(address)) {
            throw new IllegalArgumentException("Invalid cell address: " + address);
        }

        String cellInfo = cell.getCell_info(); // Get the cell content
        if (!cell.isNumber(cellInfo) && !cell.isText(cellInfo)) {
            if (cell.isForm(cellInfo)) {
                String formula = cellInfo.substring(1).trim(); // Extract the formula
                if (!cell.parseFormula(formula)) {
                    throw new IllegalArgumentException("Invalid formula: " + cellInfo);
                }
            } else {
                throw new IllegalArgumentException("Invalid cell content: " + cellInfo);
            }
        }

        int[] indices = parseAddress(address); // Parse the address to get indices
        cells[indices[0]][indices[1]] = cell; // Set the cell at the specified indices
    }

    // Computes the depth of a specific cell recursively
    public int computeDepth(String address, Set<String> visited) {
        if (!isValidCellAddress(address)) {
            throw new IllegalArgumentException("Invalid cell address: " + address);
        }

        if (visited.contains(address)) {
            return -1; // Circular dependency detected
        }

        visited.add(address); // Mark the cell as visited

        Cell1 cell = getCell(address); // Get the cell
        String cellInfo = cell.getCell_info(); // Get the cell content

        if (!cell.isForm(cellInfo)) {
            visited.remove(address);
            return 0; // Non-formula cells have depth 0
        }

        String formula = cellInfo.substring(1).trim(); // Extract the formula
        int maxDepth = 0;

        int operatorIndex = cell.findLowestPriorityOperator(formula); // Find the operator in the formula
        if (operatorIndex != -1) {
            String left = formula.substring(0, operatorIndex).trim(); // Left part of the formula
            String right = formula.substring(operatorIndex + 1).trim(); // Right part of the formula

            int leftDepth = cell.isValidCell(left) ? computeDepth(left, visited) : 0; // Compute depth of left part
            int rightDepth = cell.isValidCell(right) ? computeDepth(right, visited) : 0; // Compute depth of right part

            if (leftDepth == -1 || rightDepth == -1) {
                visited.remove(address);
                return -1; // Circular dependency detected
            }

            maxDepth = Math.max(leftDepth, rightDepth); // Get the maximum depth
        } else if (cell.isValidCell(formula)) {
            maxDepth = computeDepth(formula, visited); // Compute depth of the formula
            if (maxDepth == -1) {
                visited.remove(address);
                return -1; // Circular dependency detected
            }
        }

        visited.remove(address);
        return maxDepth + 1; // Increment depth for the current cell
    }

    // Computes the depth of all cells in the spreadsheet
    public int[][] depth() {
        int[][] depths = new int[rows][cols]; // Initialize the depths array

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String address = getAddress(i, j); // Get the address of the cell
                Set<String> visited = new HashSet<>(); // Initialize the visited set
                depths[i][j] = computeDepth(address, visited); // Compute the depth of the cell
            }
        }

        return depths; // Return the depths array
    }

    // Converts row and column indices to a cell address (e.g., "A1")
    public String getAddress(int row, int col) {
        char column = (char) ('A' + col); // Convert column index to character
        return String.valueOf(column) + row; // Return the cell address
    }

    // Validates if a cell address is within the spreadsheet bounds
    public boolean isValidCellAddress(String address) {
        try {
            int[] indices = parseAddress(address); // Parse the address to get indices
            return indices[0] >= 0 && indices[0] < rows && indices[1] >= 0 && indices[1] < cols; // Check if indices are within bounds
        } catch (IllegalArgumentException e) {
            return false; // Invalid address
        }
    }

    // Evaluates the value of a specific cell
    public String eval(int x, int y) {
        if (x < 0 || x >= rows || y < 0 || y >= cols) {
            throw new IllegalArgumentException("Invalid cell coordinates: (" + x + ", " + y + ")");
        }

        Cell1 cell = cells[x][y]; // Get the cell
        String cellInfo = cell.getCell_info(); // Get the cell content

        if (cellInfo == null || cellInfo.isEmpty()) {
            return "";
        }

        if (cell.isNumber(cellInfo)) {
            return cellInfo; // Return the number
        }

        if (cell.isText(cellInfo)) {
            return cellInfo; // Return the text
        }

        if (cell.isForm(cellInfo)) {
            String address = getAddress(x, y); // Get the address of the cell
            Set<String> visited = new HashSet<>(); // Initialize the visited set
            if (computeDepth(address, visited) == -1) {
                throw new IllegalArgumentException("Cyclic dependency detected in formula: " + cellInfo);
            }

            return evaluateFormula(cellInfo, new HashSet<>()); // Evaluate the formula
        }

        throw new IllegalArgumentException("Invalid cell content: " + cellInfo);
    }

    // Evaluates all cells in the spreadsheet and returns the results
    public String[][] evalAll() {
        String[][] result = new String[rows][cols]; // Initialize the result array

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                try {
                    String address = getAddress(i, j); // Get the address of the cell
                    Set<String> visited = new HashSet<>(); // Initialize the visited set
                    if (computeDepth(address, visited) == -1) {
                        result[i][j] = "ERR_CYCL"; // Circular dependency detected
                    } else {
                        result[i][j] = eval(i, j); // Evaluate the cell
                    }
                } catch (IllegalArgumentException e) {
                    if (e.getMessage().contains("Circular reference")) {
                        result[i][j] = "ERR_CYCL"; // Circular reference error
                    } else {
                        result[i][j] = "#ERROR"; // General error
                    }
                }
            }
        }

        return result; // Return the evaluation results
    }

    // Evaluates a formula and returns the result
    public String evaluateFormula(String formula, Set<String> visited) {
        if (!formula.startsWith("=")) {
            throw new IllegalArgumentException("Invalid formula format: " + formula);
        }

        String expr = formula.substring(1).trim(); // Remove the '=' and trim spaces

        if (new Cell1().isNumber(expr)) {
            double value = Double.parseDouble(expr); // Parse the number
            return formatNumber(value); // Format and return the number
        }

        if (new Cell1().isValidCell(expr)) {
            if (visited.contains(expr)) {
                throw new IllegalArgumentException("Circular reference detected");
            }
            visited.add(expr); // Mark the cell as visited
            int[] indices = parseAddress(expr); // Parse the address to get indices
            return eval(indices[0], indices[1]); // Evaluate the cell
        }

        if (expr.startsWith("(") && expr.endsWith(")")) {
            String innerExpr = expr.substring(1, expr.length() - 1).trim(); // Extract the inner expression
            return evaluateFormula("=" + innerExpr, visited); // Evaluate the inner expression
        }

        Cell1 cell = new Cell1();
        int operatorIndex = cell.findLowestPriorityOperator(expr); // Find the operator in the expression
        if (operatorIndex != -1) {
            String leftPart = expr.substring(0, operatorIndex).trim(); // Left part of the expression
            String rightPart = expr.substring(operatorIndex + 1).trim(); // Right part of the expression
            char operator = expr.charAt(operatorIndex); // Operator

            double leftValue = parseValue(leftPart, visited); // Parse the left value
            double rightValue = parseValue(rightPart, visited); // Parse the right value

            double result = performOperation(leftValue, rightValue, operator); // Perform the operation
            return formatNumber(result); // Format and return the result
        }

        throw new IllegalArgumentException("Invalid formula expression: " + expr);
    }

    // Parses and evaluates a value from a formula expression
    public double parseValue(String expr, Set<String> visited) {
        if (new Cell1().isNumber(expr)) {
            return Double.parseDouble(expr); // Parse and return the number
        }
        if (new Cell1().isValidCell(expr)) {
            if (visited.contains(expr)) {
                throw new IllegalArgumentException("Circular reference detected");
            }
            visited.add(expr); // Mark the cell as visited
            int[] indices = parseAddress(expr); // Parse the address to get indices
            String result = eval(indices[0], indices[1]); // Evaluate the cell
            return Double.parseDouble(result); // Parse and return the result
        }
        return Double.parseDouble(evaluateFormula("=" + expr, visited)); // Evaluate the formula and return the result
    }

    // Performs arithmetic operations based on the operator
    public double performOperation(double left, double right, char operator) {
        switch (operator) {
            case '+': return left + right;
            case '-': return left - right;
            case '*': return left * right;
            case '/':
                if (right == 0) throw new ArithmeticException("Division by zero");
                return left / right;
            default:
                throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }

    // Formats a double value to remove unnecessary trailing zeros
    public String formatNumber(double number) {
        String formatted = String.format("%.1f", number);
        return formatted.endsWith(".0") ? formatted : String.format("%.1f", number);
    }
}