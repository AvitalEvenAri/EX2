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
        if (columnChar < 'A' || columnChar > 'J') { // עמודות A-J
            throw new IllegalArgumentException("Invalid column in address: " + address);
        }
        int column = columnChar - 'A';
        int row;
        try {
            row = Integer.parseInt(address.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row in address: " + address);
        }
        if (row < 0 || row >= 100) { // שורות 0-99
            throw new IllegalArgumentException("Row out of bounds: " + address);
        }
        return new int[]{row, column};
    }





    // Get a cell by its address (e.g., "A1")
    public Cell getCell(String address) {
        int[] indices = parseAddress(address);
        return cells[indices[0]][indices[1]];
    }

    public void setCell(String address, Cell cell) {
        // Validate if the address is valid
        if (!isValidCellAddress(address)) {
            throw new IllegalArgumentException("Invalid cell address: " + address);
        }

        // Validate if the cell content is valid
        String cellInfo = cell.getCell_info();
        if (!cell.isNumber(cellInfo) && !cell.isText(cellInfo)) {
            if (cell.isForm(cellInfo)) {
                // Check if the formula is valid
                String formula = cellInfo.substring(1).trim();
                if (!cell.parseFormula(formula)) { // Assuming parseFormula checks formula validity
                    throw new IllegalArgumentException("Invalid formula: " + cellInfo);
                }
            } else {
                throw new IllegalArgumentException("Invalid cell content: " + cellInfo);
            }
        }

        // Insert the cell after validations
        int[] indices = parseAddress(address);
        cells[indices[0]][indices[1]] = cell;
    }


    public int computeDepth(String address, Set<String> visited) {
        // בדיקה אם כתובת התא חוקית
        if (!isValidCellAddress(address)) {
            throw new IllegalArgumentException("Invalid cell address: " + address);
        }

        // בדיקה אם יש לולאה
        if (visited.contains(address)) {
            return -1; // במקרה של לולאה
        }

        visited.add(address); // סימון התא כ"מבוקר"

        Cell cell = getCell(address);
        String cellInfo = cell.getCell_info();

        // בדיקה אם התא ריק או מספר/טקסט
        if (!cell.isForm(cellInfo)) {
            visited.remove(address);
            return 0; // תא שאינו פורמולה, העומק שלו 0
        }

        String formula = cellInfo.substring(1).trim();
        int maxDepth = 0;

        // טיפול בפורמולה
        int operatorIndex = cell.findLowestPriorityOperator(formula);
        if (operatorIndex != -1) {
            String left = formula.substring(0, operatorIndex).trim();
            String right = formula.substring(operatorIndex + 1).trim();

            int leftDepth = cell.isValidCell(left) ? computeDepth(left, visited) : 0;
            int rightDepth = cell.isValidCell(right) ? computeDepth(right, visited) : 0;

            if (leftDepth == -1 || rightDepth == -1) {
                visited.remove(address);
                return -1; // זוהתה לולאה
            }

            maxDepth = Math.max(leftDepth, rightDepth);
        } else if (cell.isValidCell(formula)) {
            maxDepth = computeDepth(formula, visited);
            if (maxDepth == -1) {
                visited.remove(address);
                return -1; // זוהתה לולאה
            }
        }

        visited.remove(address);
        return maxDepth + 1; // הוספת עומק התא הנוכחי
    }






    public int[][] computeAllDepths() {
        int[][] depths = new int[rows][cols]; // Create a 2D array to store the depths of all cells

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String address = getAddress(i, j); // Get the address of the current cell
                Set<String> visited = new HashSet<>(); // Create a new visited set for each cell
                depths[i][j] = computeDepth(address, visited); // Compute the depth for the cell
            }
        }

        return depths; // Return the 2D array with all computed depths
    }


    // Get the address of a cell given its row and column indices
    public String getAddress(int row, int col) {
        char column = (char) ('A' + col);
        return column + String.valueOf(row);
    }

    public boolean isValidCellAddress(String address) {
        try {
            int[] indices = parseAddress(address);
            return indices[0] >= 0 && indices[0] < rows && indices[1] >= 0 && indices[1] < cols;
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
    public String eval(int x, int y) {
        // Validate the coordinates
        if (x < 0 || x >= rows || y < 0 || y >= cols) {
            throw new IllegalArgumentException("Invalid cell coordinates: (" + x + ", " + y + ")");
        }

        // Retrieve the cell
        Cell cell = cells[x][y];
        String cellInfo = cell.getCell_info();

        // If the cell is empty, return an empty string
        if (cellInfo == null || cellInfo.isEmpty()) {
            return "";
        }

        // If the cell contains a number, return it as a string
        if (cell.isNumber(cellInfo)) {
            return cellInfo;
        }

        // If the cell contains text, return it as is
        if (cell.isText(cellInfo)) {
            return cellInfo;
        }

        // If the cell contains a formula
        if (cell.isForm(cellInfo)) {
            // Check for cyclic dependencies
            String address = getAddress(x, y);
            Set<String> visited = new HashSet<>();
            if (computeDepth(address, visited) == -1) {
                throw new IllegalArgumentException("Cyclic dependency detected in formula: " + cellInfo);
            }

            // Evaluate the formula
            try {
                return evaluateFormula(cellInfo, new HashSet<>());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid formula in cell: " + cellInfo);
            }
        }

        // If the content doesn't match any valid type, throw an exception
        throw new IllegalArgumentException("Invalid cell content: " + cellInfo);
    }

    private String evaluateFormula(String formula, Set<String> visited) {
        if (!formula.startsWith("=")) {
            throw new IllegalArgumentException("Invalid formula format: " + formula);
        }

        // Remove the '=' and trim
        String expr = formula.substring(1).trim();

        // If it's just a number, return it
        if (new Cell().isNumber(expr)) {
            double value = Double.parseDouble(expr);
            return formatNumber(value);
        }

        // If it's a cell reference, evaluate that cell
        if (new Cell().isValidCell(expr)) {
            if (visited.contains(expr)) {
                throw new IllegalArgumentException("Circular reference detected");
            }
            visited.add(expr);
            int[] indices = parseAddress(expr);
            return eval(indices[0], indices[1]);
        }

        // Handle parentheses
        if (expr.startsWith("(") && expr.endsWith(")")) {
            String innerExpr = expr.substring(1, expr.length() - 1).trim();
            return evaluateFormula("=" + innerExpr, visited);
        }

        // Find the operator with lowest precedence
        Cell cell = new Cell();
        int operatorIndex = cell.findLowestPriorityOperator(expr);
        if (operatorIndex != -1) {
            String leftPart = expr.substring(0, operatorIndex).trim();
            String rightPart = expr.substring(operatorIndex + 1).trim();
            char operator = expr.charAt(operatorIndex);

            // Recursively evaluate left and right parts
            double leftValue = parseValue(leftPart, visited);
            double rightValue = parseValue(rightPart, visited);

            // Perform the operation
            double result = performOperation(leftValue, rightValue, operator);
            return formatNumber(result);
        }

        throw new IllegalArgumentException("Invalid formula expression: " + expr);
    }
    private double parseValue(String expr, Set<String> visited) {
        if (new Cell().isNumber(expr)) {
            return Double.parseDouble(expr);
        }
        if (new Cell().isValidCell(expr)) {
            if (visited.contains(expr)) {
                throw new IllegalArgumentException("Circular reference detected");
            }
            visited.add(expr);
            int[] indices = parseAddress(expr);
            String result = eval(indices[0], indices[1]);
            return Double.parseDouble(result);
        }
        // Handle nested expressions
        return Double.parseDouble(evaluateFormula("=" + expr, visited));
    }
    private double performOperation(double left, double right, char operator) {
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

    private String formatNumber(double number) {
        // Format the number to remove trailing zeros and unnecessary decimal point
        String formatted = String.format("%.1f", number);
        return formatted.endsWith(".0") ? formatted : String.format("%.1f", number);
    }
    public String[][] evalAll() {
        String[][] result = new String[rows][cols];

        // Evaluate each cell in the spreadsheet
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                try {
                    // Check for circular dependencies before evaluation
                    String address = getAddress(i, j);
                    Set<String> visited = new HashSet<>();
                    if (computeDepth(address, visited) == -1) {
                        result[i][j] = "ERR_CYCL";
                    } else {
                        result[i][j] = eval(i, j);
                    }
                } catch (IllegalArgumentException e) {
                    // Check if the error is due to circular reference
                    if (e.getMessage().contains("Circular reference")) {
                        result[i][j] = "ERR_CYCL";
                    } else {
                        result[i][j] = "#ERROR";
                    }
                }
            }
        }

        return result;
    }
    }