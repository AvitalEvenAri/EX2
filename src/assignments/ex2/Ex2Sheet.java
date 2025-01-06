package assignments.ex2;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Ex2Sheet implements Sheet {
    private SCell[][] table;

    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                table[i][j] = new SCell("");
            }
        }
        System.out.println("Initialized Ex2Sheet with dimensions: " + x + "x" + y);
        eval();
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public boolean isIn(int x, int y) {
        boolean inBounds = x >= 0 && x < width() && y >= 0 && y < height();
        System.out.println("isIn(" + x + ", " + y + ") = " + inBounds);
        return inBounds;
    }

    @Override
    public int width() {
        return table.length;
    }

    @Override
    public int height() {
        return table[0].length;
    }

    @Override
    public void set(int x, int y, String s) {
        if (!isIn(x, y)) {
            throw new IllegalArgumentException("Coordinates out of bounds: (" + x + ", " + y + ")");
        }
        System.out.println("Setting cell (" + x + ", " + y + ") to value: " + s);
        table[x][y].setData(s);
        eval();
    }

    @Override
    public Cell get(int x, int y) {
        if (!isIn(x, y)) {
            System.out.println("get(" + x + ", " + y + ") = null (out of bounds)");
            return null;
        }
        String address = convertToIndex(x, y);
        SCell cell = table[x][y];
        cell.setCellEntry(new CellEntry(address));  // Update the CellEntry
        System.out.println("get(" + x + ", " + y + ") = Cell at address: " + address);
        return cell;
    }

    @Override
    public Cell get(String entry) {
        try {
            int[] coords = parseAddress(entry);
            System.out.println("get(\"" + entry + "\") = Coordinates: " + Arrays.toString(coords));
            return get(coords[0], coords[1]);
        } catch (IllegalArgumentException e) {
            System.out.println("get(\"" + entry + "\") failed: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String value(int x, int y) {
        if (!isIn(x, y)) {
            System.out.println("value(" + x + ", " + y + ") = EMPTY_CELL (out of bounds)");
            return Ex2Utils.EMPTY_CELL;
        }
        SCell cell = table[x][y];
        String computedValue = cell.getComputedValue();
        String value = computedValue != null ? computedValue : cell.getData();
        System.out.println("value(" + x + ", " + y + ") = " + value);
        return value;
    }

    @Override
    public String eval(int x, int y) {
        if (!isIn(x, y)) {
            throw new IllegalArgumentException("Coordinates out of bounds: (" + x + ", " + y + ")");
        }

        SCell cell = table[x][y];
        String data = cell.getData();
        System.out.println("Evaluating cell (" + x + ", " + y + ") with data: " + data);

        if (data == null || !data.startsWith("=")) {
            System.out.println("eval(" + x + ", " + y + ") = " + data + " (not a formula)");
            return data;
        }

        try {
            String formula = data.substring(1).trim();
            String[] tokens = formula.split("(?<=[-+*/()])|(?=[-+*/()])");
            double result = evaluateFormula(tokens, new HashSet<>());
            String formattedResult = String.format("%.2f", result);
            System.out.println("eval(" + x + ", " + y + ") = " + formattedResult);
            return formattedResult;
        } catch (Exception e) {
            System.out.println("eval(" + x + ", " + y + ") = #ERROR (" + e.getMessage() + ")");
            return "#ERROR";
        }
    }


    private int getMaxDepth(int[][] depths) {
        int max = 0;
        for (int[] row : depths) {
            for (int depth : row) {
                if (depth != -1) { // מתעלמים ממעגלים (ציון -1)
                    max = Math.max(max, depth);
                }
            }
        }
        System.out.println("Max depth found: " + max);
        return max;
    }
    private double evaluateFormula(String[] tokens, Set<String> visited) {
        // ניקוי טוקנים ריקים
        tokens = Arrays.stream(tokens)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        // אם יש רק טוקן אחד, זה יכול להיות מספר או תא
        if (tokens.length == 1) {
            String token = tokens[0];
            if (isValidCellAddress(token)) {
                int[] coords = parseAddress(token);
                String cellValue = eval(coords[0], coords[1]);
                return Double.parseDouble(cellValue);
            } else {
                return Double.parseDouble(token);
            }
        }

        // מחפש את האופרטור בעל העדיפות הנמוכה ביותר מחוץ לסוגריים
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
                    lastOpIndex = i;
                }
            }
        }

        // אם מצאנו אופרטור, מחלקים את הביטוי לחלקים
        if (lastOpIndex != -1) {
            String[] leftTokens = Arrays.copyOfRange(tokens, 0, lastOpIndex);
            String[] rightTokens = Arrays.copyOfRange(tokens, lastOpIndex + 1, tokens.length);

            double leftValue = evaluateFormula(leftTokens, visited);
            double rightValue = evaluateFormula(rightTokens, visited);

            char operator = tokens[lastOpIndex].charAt(0);
            return applyOperator(leftValue, rightValue, operator);
        }

        // אם יש סוגריים חיצוניים, מסיר אותם ומחשב את הביטוי הפנימי
        if (tokens[0].equals("(") && tokens[tokens.length - 1].equals(")")) {
            return evaluateFormula(Arrays.copyOfRange(tokens, 1, tokens.length - 1), visited);
        }

        throw new IllegalArgumentException("Invalid formula: " + Arrays.toString(tokens));
    }
    private double applyOperator(double a, double b, char operator) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }


    public void eval() {
        System.out.println("Evaluating entire sheet...");
        int[][] depths = depth();

        for (int d = 0; d <= getMaxDepth(depths); d++) {
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    if (depths[i][j] == d) {
                        SCell cell = table[i][j];
                        String data = cell.getData();
                        if (data != null && data.startsWith("=")) {
                            try {
                                String value = eval(i, j);
                                cell.setComputedValue(value);
                            } catch (Exception e) {
                                cell.setComputedValue("#ERROR");
                            }
                        } else {
                            cell.setComputedValue(data);
                        }
                    } else if (depths[i][j] == -1) {
                        table[i][j].setComputedValue("#CYCLE");
                    }
                }
            }
        }
        System.out.println("Sheet evaluation complete.");
    }

    @Override
    public int[][] depth() {
        int[][] depths = new int[width()][height()];
        boolean[][] visited = new boolean[width()][height()];

        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                depths[x][y] = computeDepth(x, y, depths, visited);
            }
        }
        return depths;
    }

    private int computeDepth(int x, int y, int[][] depths, boolean[][] visited) {
        if (!isIn(x, y)) {
            return -1;
        }

        SCell cell = table[x][y];
        String data = cell.getData();

        if (data == null || data.isEmpty() || !data.startsWith("=")) {
            return 0;
        }

        if (visited[x][y]) {
            return -1;
        }

        visited[x][y] = true;
        String formula = data.substring(1).trim();
        String[] tokens = formula.split("(?<=[-+*/()])|(?=[-+*/()])");
        int maxDepth = 0;

        for (String token : tokens) {
            if (isValidCellAddress(token)) {
                try {
                    int[] coords = parseAddress(token);
                    int depDepth = computeDepth(coords[0], coords[1], depths, visited);
                    if (depDepth == -1) {
                        return -1;
                    }
                    maxDepth = Math.max(maxDepth, depDepth);
                } catch (IllegalArgumentException e) {
                    return -1;
                }
            }
        }

        visited[x][y] = false;
        return maxDepth + 1;
    }

    private boolean isValidCellAddress(String address) {
        try {
            parseAddress(address);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private int[] parseAddress(String address) {
        if (address == null || address.length() < 2) {
            throw new IllegalArgumentException("Invalid cell address: " + address);
        }

        char columnChar = Character.toUpperCase(address.charAt(0));
        if (columnChar < 'A' || columnChar > 'Z') {
            throw new IllegalArgumentException("Invalid column in address: " + address);
        }

        try {
            int row = Integer.parseInt(address.substring(1));
            int col = columnChar - 'A';

            if (row < 0 || row >= height() || col < 0 || col >= width()) {
                throw new IllegalArgumentException("Address out of bounds: " + address);
            }

            System.out.println("parseAddress(\"" + address + "\") = [" + col + ", " + row + "]");
            return new int[]{col, row};
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row in address: " + address);
        }
    }
    public String convertToIndex(int x, int y) {
        if (!isIn(x, y)) {
            System.out.println("convertToIndex: Coordinates out of bounds: (" + x + ", " + y + ")");
            throw new IllegalArgumentException("Coordinates out of bounds: (" + x + ", " + y + ")");
        }
        // כעת x מתייחס לשורה ו-y מתייחס לעמודה
        char column = (char) ('A' + y); // ממיר את y לעמודה (אות באנגלית)
        int row = x; // x הוא מספר השורה
        String result = column + String.valueOf(row); // משרשר את השורה והעמודה ליצירת שם התא
        System.out.println("convertToIndex(" + x + ", " + y + ") = " + result); // debug
        return result;
    }

//    public String convertToIndex(int x, int y) {
//        char column = (char) ('A' + x); // Translate x to column letter
//        String index = column + String.valueOf(y); // Append y directly
//        System.out.println("convertToIndex(" + x + ", " + y + ") = " + index);
//        return index;
//    }

    @Override
    public void save(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("I2CS ArielU: SpreadSheet (Ex2) assignment\n");
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    String data = table[i][j].getData();
                    if (data != null && !data.isEmpty()) {
                        writer.write(i + "," + j + "," + data + "\n");
                    }
                }
            }
        }
    }

    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine(); // Skip header line

            // Reset all cells
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    table[i][j] = new SCell("");
                }
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length >= 3) {
                    int x = Integer.parseInt(parts[0].trim());
                    int y = Integer.parseInt(parts[1].trim());
                    String data = parts[2].trim();
                    if (isIn(x, y)) {
                        set(x, y, data);
                    }
                }
            }
        }
    }
}
