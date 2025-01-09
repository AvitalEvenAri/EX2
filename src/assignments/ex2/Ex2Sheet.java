package assignments.ex2;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Ex2Sheet implements Sheet {
    private SCell[][] table; // 2D array to store cells

    // Constructor with dimensions
    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y]; // Initialize the 2D array of cells
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL); // Initialize empty cells
            }
        }
        System.out.println("Initialized Ex2Sheet with dimensions: " + x + "x" + y);
        eval(); // Evaluate the sheet
    }

    // Default constructor
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT); // Initialize with default dimensions
    }

    @Override
    public boolean isIn(int x, int y) {
        boolean inBounds = x >= 0 && x < width() && y >= 0 && y < height(); // Check if coordinates are within bounds
        System.out.println("isIn(" + x + ", " + y + ") = " + inBounds);
        return inBounds; // Return the result
    }

    @Override
    public int width() {
        return table.length; // Return the width of the sheet
    }

    @Override
    public int height() {
        return table[0].length; // Return the height of the sheet
    }

    @Override
    public void set(int x, int y, String s) {
        if (!isIn(x, y)) {
            throw new IllegalArgumentException(String.valueOf(Ex2Utils.ERR_FORM_FORMAT)); // Throw error if out of bounds
        }
        System.out.println("Setting cell (" + x + ", " + y + ") to value: " + s);
        table[x][y].setData(s); // Set the data for the cell
        eval(); // Evaluate the sheet
    }

    @Override
    public Cell get(int x, int y) {
        if (!isIn(x, y)) {
            System.out.println("get(" + x + ", " + y + ") = null (out of bounds)");
            return null; // Return null if out of bounds
        }
        String address = convertToIndex(x, y); // Convert coordinates to address
        SCell cell = table[x][y]; // Get the cell
        cell.setCellEntry(new CellEntry(address)); // Set the cell entry
        System.out.println("get(" + x + ", " + y + ") = Cell at address: " + address);
        return cell; // Return the cell
    }

    @Override
    public Cell get(String entry) {
        try {
            int[] coords = parseAddress(entry); // Parse the address to get coordinates
            System.out.println("get(\"" + entry + "\") = Coordinates: " + Arrays.toString(coords));
            return get(coords[0], coords[1]); // Get the cell at the coordinates
        } catch (IllegalArgumentException e) {
            System.out.println("get(\"" + entry + "\") failed: " + e.getMessage());
            return null; // Return null if parsing fails
        }
    }

    @Override
    public String value(int x, int y) {
        if (!isIn(x, y)) {
            System.out.println("value(" + x + ", " + y + ") = EMPTY_CELL (out of bounds)");
            return Ex2Utils.EMPTY_CELL; // Return empty cell if out of bounds
        }
        SCell cell = table[x][y]; // Get the cell
        String computedValue = cell.getComputedValue(); // Get the computed value
        String value = computedValue != null ? computedValue : cell.getData(); // Get the value

        // **Ensure numbers are returned as double:**
        if (isNumber(value) && !value.startsWith("=")) {
            double number = Double.parseDouble(value); // Convert to double
            value = String.format("%.2f", number); // Format as double
        }

        System.out.println("value(" + x + ", " + y + ") = " + value);
        return value; // Return the value
    }


    @Override
    public String eval(int x, int y) {
        if (!isIn(x, y)) {
            throw new IllegalArgumentException(String.valueOf(Ex2Utils.ERR_FORM_FORMAT)); // Throw error if out of bounds
        }

        SCell cell = table[x][y]; // Get the cell
        String data = cell.getData(); // Get the data
        System.out.println("Evaluating cell (" + x + ", " + y + ") with data: " + data);

        // If it's not a formula, treat it as a simple value:
        if (data == null || !data.startsWith("=")) {
            if (isNumber(data)) {
                double number = Double.parseDouble(data); // Convert to double
                return String.format("%.2f", number); // Format as double
            }
            return data; // Return data if not a number
        }

        try {
            String formula = data.substring(1).trim(); // Extract the formula
            String[] tokens = formula.split("(?<=[-+*/()])|(?=[-+*/()])"); // Split the formula into tokens
            double result = evaluateFormula(tokens, new HashSet<>()); // Evaluate the formula
            String formattedResult = String.format("%.2f", result); // Format the result
            System.out.println("eval(" + x + ", " + y + ") = " + formattedResult);
            return formattedResult; // Return the result
        } catch (Exception e) {
            System.out.println("eval(" + x + ", " + y + ") = " + Ex2Utils.ERR_FORM + " (" + e.getMessage() + ")");
            return Ex2Utils.ERR_FORM; // Return error if evaluation fails
        }
    }

    // Get the maximum depth in the depths array
    private int getMaxDepth(int[][] depths) {
        int max = 0;
        for (int[] row : depths) {
            for (int depth : row) {
                if (depth != Ex2Utils.ERR_CYCLE_FORM) {
                    max = Math.max(max, depth); // Update the maximum depth
                }
            }
        }
        System.out.println("Max depth found: " + max);
        return max; // Return the maximum depth
    }

    // Evaluate the formula
    private double evaluateFormula(String[] tokens, Set<String> visited) {
        tokens = Arrays.stream(tokens)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new); // Clean up the tokens

        if (tokens.length > 1 && tokens[0].equals("-") && isNumber(tokens[1])) {
            tokens[1] = "-" + tokens[1]; // Handle negative numbers
            tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
        }

        if (tokens.length == 1) {
            String token = tokens[0];
            double value;
            if (isValidCellAddress(token)) {
                int[] coords = parseAddress(token); // Parse the address
                String cellValue = eval(coords[0], coords[1]); // Evaluate the cell
                if (cellValue.equals(Ex2Utils.ERR_FORM) || cellValue.equals(Ex2Utils.ERR_CYCLE)) {
                    throw new IllegalArgumentException(cellValue); // Throw error if evaluation fails
                }
                value = Double.parseDouble(cellValue); // Parse the value
            } else {
                value = Double.parseDouble(token); // Parse the value
            }
            return value; // Return the value
        }

        int lastOpIndex = -1;
        int minPriority = Integer.MAX_VALUE;
        int parenthesesCount = 0;

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            if (token.equals("(")) {
                parenthesesCount++;
                continue;
            }
            if (token.equals(")")) {
                parenthesesCount--;
                continue;
            }

            if (parenthesesCount == 0 && "+-*/".contains(token)) {
                int priority = token.equals("+") || token.equals("-") ? 1 : 2;
                if (priority <= minPriority) {
                    minPriority = priority;
                    lastOpIndex = i; // Update the last operator index
                }
            }
        }

        if (lastOpIndex != -1) {
            String[] leftTokens = Arrays.copyOfRange(tokens, 0, lastOpIndex); // Get the left tokens
            String[] rightTokens = Arrays.copyOfRange(tokens, lastOpIndex + 1, tokens.length); // Get the right tokens

            double leftValue = evaluateFormula(leftTokens, visited); // Evaluate the left tokens
            double rightValue = evaluateFormula(rightTokens, visited); // Evaluate the right tokens

            char operator = tokens[lastOpIndex].charAt(0); // Get the operator
            return applyOperator(leftValue, rightValue, operator); // Apply the operator
        }

        if (tokens[0].equals("(") && tokens[tokens.length - 1].equals(")")) {
            return evaluateFormula(Arrays.copyOfRange(tokens, 1, tokens.length - 1), visited); // Evaluate the inner expression
        }

        throw new IllegalArgumentException(Ex2Utils.ERR_FORM); // Throw error if evaluation fails
    }

    // Apply the operator to the values
    private double applyOperator(double a, double b, char operator) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException(Ex2Utils.ERR_FORM); // Handle division by zero
                return a / b;
            default:
                throw new IllegalArgumentException(Ex2Utils.ERR_FORM); // Throw error for unsupported operator
        }
    }

    // Check if the text is a number
    private boolean isNumber(String text) {
        try {
            Double.parseDouble(text); // Try to parse the text as a number
            return true;
        } catch (NumberFormatException e) {
            return false; // Return false if parsing fails
        }
    }

    // Evaluate the entire sheet
    public void eval() {
        System.out.println("Evaluating entire sheet...");
        int[][] depths = depth(); // Get the depths of all cells

        for (int d = 0; d <= getMaxDepth(depths); d++) {
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    if (depths[i][j] == d) {
                        SCell cell = table[i][j];
                        String data = cell.getData();
                        if (data != null && data.startsWith("=")) {
                            try {
                                String value = eval(i, j); // Evaluate the cell
                                cell.setComputedValue(value); // Set the computed value
                            } catch (Exception e) {
                                cell.setComputedValue(Ex2Utils.ERR_FORM); // Set error if evaluation fails
                            }
                        } else {
                            cell.setComputedValue(data); // Set the data as the computed value
                        }
                    } else if (depths[i][j] == Ex2Utils.ERR_CYCLE_FORM) {
                        table[i][j].setComputedValue(Ex2Utils.ERR_CYCLE); // Set cycle error
                    }
                }
            }
        }
        System.out.println("Sheet evaluation complete.");
    }
    @Override
    public int[][] depth() {
        int[][] depths = new int[width()][height()]; // Initialize the depths array
        boolean[][] visited = new boolean[width()][height()]; // Initialize the visited array

        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                depths[x][y] = computeDepth(x, y, depths, visited); // Compute the depth of each cell
            }
        }
        return depths; // Return the depths array
    }

    private int computeDepth(int x, int y, int[][] depths, boolean[][] visited) {
        if (!isIn(x, y)) {
            return Ex2Utils.ERR_FORM_FORMAT; // Return error if out of bounds
        }

        SCell cell = table[x][y]; // Get the cell
        String data = cell.getData(); // Get the data

        if (data == null || data.isEmpty() || !data.startsWith("=")) {
            return 0; // Return 0 if not a formula
        }

        // Extract the formula without the "="
        String formula = data.substring(1).trim();

        // Check for direct self-reference (e.g., A1 references itself as "=A1")
        if (formula.equals(convertToIndex(x, y))) {
            System.out.println("Circular reference detected in cell: " + convertToIndex(x, y));
            return Ex2Utils.ERR_CYCLE_FORM; // Return cycle error
        }

        if (visited[x][y]) {
            System.out.println("Circular dependency detected for cell: " + convertToIndex(x, y));
            return Ex2Utils.ERR_CYCLE_FORM; // Return cycle error if already visited
        }

        visited[x][y] = true; // Mark the cell as visited
        String[] tokens = formula.split("(?<=[-+*/()])|(?=[-+*/()])"); // Split the formula into tokens
        int maxDepth = 0;

        for (String token : tokens) {
            if (isValidCellAddress(token)) {
                try {
                    int[] coords = parseAddress(token); // Parse the address

                    // Compute depth for referenced cells
                    int depDepth = computeDepth(coords[0], coords[1], depths, visited);
                    if (depDepth == Ex2Utils.ERR_CYCLE_FORM) {
                        return Ex2Utils.ERR_CYCLE_FORM; // Return cycle error if detected
                    }
                    maxDepth = Math.max(maxDepth, depDepth); // Update the maximum depth
                } catch (IllegalArgumentException e) {
                    return Ex2Utils.ERR_FORM_FORMAT; // Return error if parsing fails
                }
            }
        }

        visited[x][y] = false; // Unmark the cell as visited
        return maxDepth + 1; // Return the depth
    }



    private boolean isValidCellAddress(String address) {
        try {
            parseAddress(address); // Try to parse the address
            return true;
        } catch (IllegalArgumentException e) {
            return false; // Return false if parsing fails
        }
    }

    private int[] parseAddress(String address) {
        if (address == null || address.length() < 2) {
            throw new IllegalArgumentException(String.valueOf(Ex2Utils.ERR_FORM_FORMAT)); // Throw error if invalid
        }

        char columnChar = Character.toUpperCase(address.charAt(0)); // Get the column character
        if (columnChar < 'A' || columnChar > 'Z') {
            throw new IllegalArgumentException(String.valueOf(Ex2Utils.ERR_FORM_FORMAT)); // Throw error if out of range
        }

        try {
            int row = Integer.parseInt(address.substring(1)); // Parse the row
            int col = columnChar - 'A'; // Convert column to index

            if (row < 0 || row >= height() || col < 0 || col >= width()) {
                throw new IllegalArgumentException(String.valueOf(Ex2Utils.ERR_FORM_FORMAT)); // Throw error if out of bounds
            }

            System.out.println("parseAddress(\"" + address + "\") = [" + col + ", " + row + "]");
            return new int[]{col, row}; // Return the coordinates
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.valueOf(Ex2Utils.ERR_FORM_FORMAT)); // Throw error if parsing fails
        }
    }

    public String convertToIndex(int x, int y) {
        if (!isIn(x, y)) {
            System.out.println("convertToIndex: Coordinates out of bounds: (" + x + ", " + y + ")");
            throw new IllegalArgumentException(String.valueOf(Ex2Utils.ERR_FORM_FORMAT)); // Throw error if out of bounds
        }
        char column = (char) ('A' + y); // Convert column index to character
        int row = x; // Get the row
        String result = column + String.valueOf(row); // Combine column and row
        System.out.println("convertToIndex(" + x + ", " + y + ") = " + result);
        return result; // Return the result
    }

    @Override
    public void save(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("I2CS ArielU: SpreadSheet (Ex2) assignment\n"); // Write the header
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    String data = table[i][j].getData(); // Get the data
                    if (data != null && !data.isEmpty()) {
                        writer.write(i + "," + j + "," + data + "\n"); // Write the data
                    }
                }
            }
        }
    }

    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine(); // Read the header

            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    table[i][j] = new SCell(Ex2Utils.EMPTY_CELL); // Initialize empty cells
                }
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3); // Split the line into parts
                if (parts.length >= 3) {
                    int x = Integer.parseInt(parts[0].trim()); // Parse the x coordinate
                    int y = Integer.parseInt(parts[1].trim()); // Parse the y coordinate
                    String data = parts[2].trim(); // Get the data
                    if (isIn(x, y)) {
                        set(x, y, data); // Set the data
                    }
                }
            }
        }
    }
    }