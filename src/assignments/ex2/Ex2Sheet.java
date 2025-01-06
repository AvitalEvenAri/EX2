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
        eval();
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public boolean isIn(int x, int y) {
        return x >= 0 && x < width() && y >= 0 && y < height();
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
        table[x][y].setData(s);
        eval();
    }

    @Override
    public Cell get(int x, int y) {
        if (!isIn(x, y)) {
            return null;
        }
        return table[x][y];
    }

    @Override
    public Cell get(String entry) {
        try {
            int[] coords = parseAddress(entry);
            return get(coords[0], coords[1]);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String value(int x, int y) {
        if (!isIn(x, y)) {
            return Ex2Utils.EMPTY_CELL;
        }
        SCell cell = table[x][y];
        return cell.getComputedValue() != null ? cell.getComputedValue() : cell.getData();
    }

//    @Override
//    public String eval(int x, int y) {
//        if (!isIn(x, y)) {
//            throw new IllegalArgumentException("Coordinates out of bounds: (" + x + ", " + y + ")");
//        }
//
//        SCell cell = table[x][y];
//        String data = cell.getData();
//
//        if (data == null || !data.startsWith("=")) {
//            return data;
//        }
//
//        try {
//            String formula = data.substring(1).trim();
//            String[] tokens = formula.split("(?<=[-+*/()])|(?=[-+*/()])");
//            double result = evaluateFormula(tokens, new HashSet<>());
//            return String.format("%.2f", result);
//        } catch (Exception e) {
//            return "#ERROR";
//        }
//    }



    @Override
    public String eval(int x, int y) {
        if (!isIn(x, y)) {
            throw new IllegalArgumentException("Coordinates out of bounds: (" + x + ", " + y + ")");
        }

        SCell cell = table[x][y];
        String data = cell.getData();

        if (data == null || !data.startsWith("=")) {
            return data;
        }

        try {
            String formula = data.substring(1).trim();

            // טיפול במספר שלילי בתחילת הנוסחה
            if (formula.startsWith("-") && formula.length() > 1) {
                try {
                    // בודק אם זה מספר שלילי
                    double value = Double.parseDouble(formula);
                    return String.format("%.1f", value);
                } catch (NumberFormatException e) {
                    // אם לא מספר שלילי, ממשיכים כרגיל
                }
            }

            String[] tokens = formula.split("(?<=[-+*/()])|(?=[-+*/()])");
            double result = evaluateFormula(tokens, new HashSet<>());
            return String.format("%.2f", result);
        } catch (Exception e) {
            return "#ERROR";
        }
    }

    public void eval() {
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
            int row = Integer.parseInt(address.substring(1)) - 1;
            int col = columnChar - 'A';

            if (row < 0 || row >= height() || col < 0 || col >= width()) {
                throw new IllegalArgumentException("Address out of bounds: " + address);
            }

            return new int[]{col, row};
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row in address: " + address);
        }
    }

    private int getMaxDepth(int[][] depths) {
        int max = 0;
        for (int[] row : depths) {
            for (int depth : row) {
                max = Math.max(max, depth);
            }
        }
        return max;
    }

//    private double evaluateFormula(String[] tokens, Set<String> visited) {
//        Stack<Double> values = new Stack<>();
//        Stack<Character> operators = new Stack<>();
//
//        for (String token : tokens) {
//            token = token.trim();
//            if (token.isEmpty()) continue;
//
//            if ("+-*/".contains(token)) {
//                while (!operators.isEmpty() && hasPrecedence(token.charAt(0), operators.peek())) {
//                    double b = values.pop();
//                    double a = values.pop();
//                    char op = operators.pop();
//                    values.push(applyOperator(a, b, op));
//                }
//                operators.push(token.charAt(0));
//            } else if (isValidCellAddress(token)) {
//                int[] coords = parseAddress(token);
//                String cellValue = eval(coords[0], coords[1]);
//                if (cellValue.equals("#ERROR") || cellValue.equals("#CYCLE")) {
//                    throw new IllegalArgumentException("Invalid cell reference");
//                }
//                values.push(Double.parseDouble(cellValue));
//            } else {
//                values.push(Double.parseDouble(token));
//            }
//        }
//
//        while (!operators.isEmpty()) {
//            double b = values.pop();
//            double a = values.pop();
//            char op = operators.pop();
//            values.push(applyOperator(a, b, op));
//        }
//
//        return values.pop();
//    }
//
    private boolean hasPrecedence(char op1, char op2) {
        return !(op1 == '*' || op1 == '/') || (op2 != '+' && op2 != '-');
    }

//    private double evaluateFormula(String[] tokens, Set<String> visited) {
//        Stack<Double> values = new Stack<>();
//        Stack<Character> operators = new Stack<>();
//
//        for (int i = 0; i < tokens.length; i++) {
//            String token = tokens[i].trim();
//            if (token.isEmpty()) continue;
//
//            // טיפול במספר שלילי אחרי אופרטור
//            if (token.equals("-") && i + 1 < tokens.length) {
//                String nextToken = tokens[i + 1].trim();
//                try {
//                    double value = Double.parseDouble(nextToken);
//                    values.push(-value);
//                    i++; // דילוג על המספר שכבר טיפלנו בו
//                    continue;
//                } catch (NumberFormatException e) {
//                    // אם זה לא מספר, נתייחס למינוס כאופרטור רגיל
//                }
//            }
//
//            if ("+-*/".contains(token)) {
//                while (!operators.isEmpty() && hasPrecedence(token.charAt(0), operators.peek())) {
//                    double b = values.pop();
//                    double a = values.pop();
//                    char op = operators.pop();
//                    values.push(applyOperator(a, b, op));
//                }
//                operators.push(token.charAt(0));
//            } else if (isValidCellAddress(token)) {
//                int[] coords = parseAddress(token);
//                String cellValue = eval(coords[0], coords[1]);
//                if (cellValue.equals("#ERROR") || cellValue.equals("#CYCLE")) {
//                    throw new IllegalArgumentException("Invalid cell reference");
//                }
//                values.push(Double.parseDouble(cellValue));
//            } else {
//                values.push(Double.parseDouble(token));
//            }
//        }
//
//        while (!operators.isEmpty()) {
//            double b = values.pop();
//            double a = values.pop();
//            char op = operators.pop();
//            values.push(applyOperator(a, b, op));
//        }
//
//        return values.pop();
//    }
//
//
//
    private double applyOperator(double a, double b, char operator) {
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0) throw new ArithmeticException("Division by zero");
                yield a / b;
            }
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }

    private double evaluateFormula(String[] tokens, Set<String> visited) {
        tokens = Arrays.stream(tokens)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        // אם יש רק טוקן אחד, זה יכול להיות מספר או הפניה לתא
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

        // מחפשים את האופרטור בעדיפות הנמוכה ביותר (מחוץ לסוגריים)
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

        // אם מצאנו אופרטור, נחלק את הביטוי ונחשב כל צד בנפרד
        if (lastOpIndex != -1) {
            String[] leftTokens = Arrays.copyOfRange(tokens, 0, lastOpIndex);
            String[] rightTokens = Arrays.copyOfRange(tokens, lastOpIndex + 1, tokens.length);

            double leftValue = evaluateFormula(leftTokens, visited);
            double rightValue = evaluateFormula(rightTokens, visited);

            char operator = tokens[lastOpIndex].charAt(0);
            return applyOperator(leftValue, rightValue, operator);
        }

        // אם אין אופרטור ויש סוגריים, נסיר אותם ונחשב את הביטוי בפנים
        if (tokens[0].equals("(") && tokens[tokens.length - 1].equals(")")) {
            return evaluateFormula(Arrays.copyOfRange(tokens, 1, tokens.length - 1), visited);
        }

        throw new IllegalArgumentException("Invalid expression");
    }

    private String convertToIndex(int x, int y) {
        char column = (char) ('A' + x); // מתרגם את x לאות
        int row = y + 1; // מתרגם את y למספר (שורות מתחילות מ-1)
        return column + "" + row;
    }


//    private double evaluateFormula(String[] tokens, Set<String> visited) {
//        // מנקה רווחים ומסיר טוקנים ריקים
//        tokens = Arrays.stream(tokens)
//                .map(String::trim)
//                .filter(s -> !s.isEmpty())
//                .toArray(String[]::new);
//
//        Stack<Double> values = new Stack<>();
//        Stack<Character> operators = new Stack<>();
//
//        for (int i = 0; i < tokens.length; i++) {
//            String token = tokens[i];
//
//            // טיפול במספרים שליליים
//            if (token.equals("-") && i + 1 < tokens.length) {
//                String nextToken = tokens[i + 1];
//                try {
//                    double value = Double.parseDouble(nextToken);
//                    values.push(-value);
//                    i++;  // דילוג על המספר שכבר טיפלנו בו
//                    continue;
//                } catch (NumberFormatException e) {
//                    // אם זה לא מספר, נתייחס למינוס כאופרטור רגיל
//                }
//            }
//
//            if ("+-*/".contains(token)) {
//                while (!operators.isEmpty() && hasPrecedence(token.charAt(0), operators.peek())) {
//                    double b = values.pop();
//                    double a = values.pop();
//                    char op = operators.pop();
//                    values.push(applyOperator(a, b, op));
//                }
//                operators.push(token.charAt(0));
//            } else if (token.equals("(")) {
//                operators.push('(');
//            } else if (token.equals(")")) {
//                while (!operators.isEmpty() && operators.peek() != '(') {
//                    double b = values.pop();
//                    double a = values.pop();
//                    char op = operators.pop();
//                    values.push(applyOperator(a, b, op));
//                }
//                if (!operators.isEmpty()) {
//                    operators.pop();  // מסיר את הסוגר הפותח
//                }
//            } else if (isValidCellAddress(token)) {
//                int[] coords = parseAddress(token);
//                String cellValue = eval(coords[0], coords[1]);
//                if (cellValue.equals("#ERROR") || cellValue.equals("#CYCLE")) {
//                    throw new IllegalArgumentException("Invalid cell reference");
//                }
//                values.push(Double.parseDouble(cellValue));
//            } else {
//                values.push(Double.parseDouble(token));
//            }
//        }
//
//        while (!operators.isEmpty()) {
//            double b = values.pop();
//            double a = values.pop();
//            char op = operators.pop();
//            values.push(applyOperator(a, b, op));
//        }
//
//        return values.pop();
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