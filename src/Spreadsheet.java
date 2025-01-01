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

        // Initialize all cells as empty
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new Cell(""); // Initialize empty cells
            }
        }
    }
    public Cell get(int x, int y) {
        String address = getAddress(x, y);
        return getCell(address);
    }

    public void set(int x, int y, Cell c) {
        String address = getAddress(x, y);
        setCell(address, c);
    }

    public int width() {
        return cols;
    }

    public int height() {
        return rows;
    }

    public int xCell(String c) {
        return parseAddress(c)[1]; // Column index
    }

    public int yCell(String c) {
        return parseAddress(c)[0]; // Row index
    }

    // Converts a cell address (e.g., "A1") into row and column indices
    public int[] parseAddress(String address) {
        if (address == null || address.length() < 2) {
            throw new IllegalArgumentException("Invalid cell address: " + address);
        }
        char columnChar = Character.toUpperCase(address.charAt(0));
        if (columnChar < 'A' || columnChar > 'J') { // Columns A-J
            throw new IllegalArgumentException("Invalid column in address: " + address);
        }
        int column = columnChar - 'A';
        int row;
        try {
            row = Integer.parseInt(address.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row in address: " + address);
        }
        if (row < 0 || row >= 100) { // Rows 0-99
            throw new IllegalArgumentException("Row out of bounds: " + address);
        }
        return new int[]{row, column};
    }

    // Retrieves a cell by its address
    public Cell getCell(String address) {
        int[] indices = parseAddress(address);
        return cells[indices[0]][indices[1]];
    }

    // Sets the content of a cell after validating its address and content
    public void setCell(String address, Cell cell) {
        if (!isValidCellAddress(address)) {
            throw new IllegalArgumentException("Invalid cell address: " + address);
        }

        String cellInfo = cell.getCell_info();
        if (!cell.isNumber(cellInfo) && !cell.isText(cellInfo)) {
            if (cell.isForm(cellInfo)) {
                String formula = cellInfo.substring(1).trim();
                if (!cell.parseFormula(formula)) {
                    throw new IllegalArgumentException("Invalid formula: " + cellInfo);
                }
            } else {
                throw new IllegalArgumentException("Invalid cell content: " + cellInfo);
            }
        }

        int[] indices = parseAddress(address);
        cells[indices[0]][indices[1]] = cell;
    }

    // Computes the depth of a specific cell recursively
    public int computeDepth(String address, Set<String> visited) {
        if (!isValidCellAddress(address)) {
            throw new IllegalArgumentException("Invalid cell address: " + address);
        }

        if (visited.contains(address)) {
            return -1; // Circular dependency detected
        }

        visited.add(address);

        Cell cell = getCell(address);
        String cellInfo = cell.getCell_info();

        if (!cell.isForm(cellInfo)) {
            visited.remove(address);
            return 0; // Non-formula cells have depth 0
        }

        String formula = cellInfo.substring(1).trim();
        int maxDepth = 0;

        int operatorIndex = cell.findLowestPriorityOperator(formula);
        if (operatorIndex != -1) {
            String left = formula.substring(0, operatorIndex).trim();
            String right = formula.substring(operatorIndex + 1).trim();

            int leftDepth = cell.isValidCell(left) ? computeDepth(left, visited) : 0;
            int rightDepth = cell.isValidCell(right) ? computeDepth(right, visited) : 0;

            if (leftDepth == -1 || rightDepth == -1) {
                visited.remove(address);
                return -1; // Circular dependency detected
            }

            maxDepth = Math.max(leftDepth, rightDepth);
        } else if (cell.isValidCell(formula)) {
            maxDepth = computeDepth(formula, visited);
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
        int[][] depths = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String address = getAddress(i, j);
                Set<String> visited = new HashSet<>();
                depths[i][j] = computeDepth(address, visited);
            }
        }

        return depths;
    }

// Converts row and column indices to a cell address (e.g., "A1")
    public String getAddress(int row, int col) {
        char column = (char) ('A' + col);
        return String.valueOf(column) + row;
    }


    // Validates if a cell address is within the spreadsheet bounds
    public boolean isValidCellAddress(String address) {
        try {
            int[] indices = parseAddress(address);
            return indices[0] >= 0 && indices[0] < rows && indices[1] >= 0 && indices[1] < cols;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Evaluates the value of a specific cell
    public String eval(int x, int y) {
        if (x < 0 || x >= rows || y < 0 || y >= cols) {
            throw new IllegalArgumentException("Invalid cell coordinates: (" + x + ", " + y + ")");
        }

        Cell cell = cells[x][y];
        String cellInfo = cell.getCell_info();

        if (cellInfo == null || cellInfo.isEmpty()) {
            return "";
        }

        if (cell.isNumber(cellInfo)) {
            return cellInfo;
        }

        if (cell.isText(cellInfo)) {
            return cellInfo;
        }

        if (cell.isForm(cellInfo)) {
            String address = getAddress(x, y);
            Set<String> visited = new HashSet<>();
            if (computeDepth(address, visited) == -1) {
                throw new IllegalArgumentException("Cyclic dependency detected in formula: " + cellInfo);
            }

            return evaluateFormula(cellInfo, new HashSet<>());
        }

        throw new IllegalArgumentException("Invalid cell content: " + cellInfo);
    }

    // Evaluates all cells in the spreadsheet and returns the results
    public String[][] evalAll() {
        String[][] result = new String[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                try {
                    String address = getAddress(i, j);
                    Set<String> visited = new HashSet<>();
                    if (computeDepth(address, visited) == -1) {
                        result[i][j] = "ERR_CYCL";
                    } else {
                        result[i][j] = eval(i, j);
                    }
                } catch (IllegalArgumentException e) {
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

    // Evaluates a formula and returns the result
    private String evaluateFormula(String formula, Set<String> visited) {
        if (!formula.startsWith("=")) {
            throw new IllegalArgumentException("Invalid formula format: " + formula);
        }

        String expr = formula.substring(1).trim();

        if (new Cell().isNumber(expr)) {
            double value = Double.parseDouble(expr);
            return formatNumber(value);
        }

        if (new Cell().isValidCell(expr)) {
            if (visited.contains(expr)) {
                throw new IllegalArgumentException("Circular reference detected");
            }
            visited.add(expr);
            int[] indices = parseAddress(expr);
            return eval(indices[0], indices[1]);
        }

        if (expr.startsWith("(") && expr.endsWith(")")) {
            String innerExpr = expr.substring(1, expr.length() - 1).trim();
            return evaluateFormula("=" + innerExpr, visited);
        }

        Cell cell = new Cell();
        int operatorIndex = cell.findLowestPriorityOperator(expr);
        if (operatorIndex != -1) {
            String leftPart = expr.substring(0, operatorIndex).trim();
            String rightPart = expr.substring(operatorIndex + 1).trim();
            char operator = expr.charAt(operatorIndex);

            double leftValue = parseValue(leftPart, visited);
            double rightValue = parseValue(rightPart, visited);

            double result = performOperation(leftValue, rightValue, operator);
            return formatNumber(result);
        }

        throw new IllegalArgumentException("Invalid formula expression: " + expr);
    }

    // Parses and evaluates a value from a formula expression
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
        return Double.parseDouble(evaluateFormula("=" + expr, visited));
    }

    // Performs arithmetic operations based on the operator
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

    // Formats a double value to remove unnecessary trailing zeros
    private String formatNumber(double number) {
        String formatted = String.format("%.1f", number);
        return formatted.endsWith(".0") ? formatted : String.format("%.1f", number);
    }

}
