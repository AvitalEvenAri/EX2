package assignments.ex2;
import java.io.IOException;
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
    public String value(int x, int y) {
        String ans = Ex2Utils.EMPTY_CELL;
        Cell c = get(x, y);
        if (c != null) {
            ans = c.toString();
        }
        return ans;
    }

    @Override
    public Cell get(int x, int y) {
        return table[x][y];
    }

    @Override
    public Cell get(String cords) {
        Cell ans = null;
        if (cords == null || cords.isEmpty()) {
            return null;
        }
        CellEntry entry = new CellEntry(cords);
        if (!entry.isValid()) {
            return null;
        }
        int x = entry.getX();
        int y = entry.getY();
        if (isIn(x, y)) {
            ans = table[x][y];
        }
        return ans;
    }

    @Override
    public int width() {
        return table.length;
    }

    @Override
    public int height() {
        return table[0].length;
    }

    public void set(int x, int y, String s) {
        if (!isIn(x, y)) {
            throw new IllegalArgumentException("Coordinates out of bounds: (" + x + ", " + y + ")");
        }

        System.out.println("Set cell (" + x + "," + y + ") to value: " + s);

        Cell c = table[x][y];
        c.setData(s);  // עדכון הערך של התא
        System.out.println("Cell (" + x + "," + y + ") data updated to: " + c.getData());
        eval();
    }

    //calculate the formula
    @Override
    public void eval() {
        int[][] depths = depth();  // מחשב את העומקים קודם

        // ממיין את התאים לפי העומק שלהם
        for (int d = 0; d <= getMaxDepth(depths); d++) {
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    if (depths[i][j] == d) {
                        Cell cell = table[i][j];
                        String data = cell.getData();
                        if (data != null && data.startsWith("=")) {
                            try {
                                String value = eval(i, j);
                                cell.setData(value);
                            } catch (Exception e) {
                                cell.setData("#ERROR");
                            }
                        }
                    } else if (depths[i][j] == -1) {
                        table[i][j].setData("#CYCLE");
                    }
                }
            }
        }
    }

    private int getMaxDepth(int[][] depths) {
        int max = 0;
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                if (depths[i][j] > max) {
                    max = depths[i][j];
                }
            }
        }
        return max;
    }

    @Override
    public boolean isIn(int xx, int yy) {
        return xx >= 0 && xx < width() && yy >= 0 && yy < height();
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
        SCell cell = table[x][y];
        int type_of = cell.getType();
        String data = "";

        switch(type_of){
            case Ex2Utils.FORM:
                data = cell.getFormule();
                break;
            default:
                data = cell.getData();
                break;
        }

        System.out.println("Computing depth for cell (" + x + "," + y + "), data: " + data);

        if (data == null || data.isEmpty() || !data.startsWith("=")) {
            depths[x][y] = 0;
            visited[x][y] = false;
            return 0;
        }

        if (visited[x][y]) {
            return -1;  // מעגליות
        }

        visited[x][y] = true;
        String formula = data.substring(1).trim();
        String[] tokens = formula.split("(?<=[-+*/()])|(?=[-+*/()])");
        int maxDepth = 0;

        for (String token : tokens) {
            if (isValidCellAddress(token)) {
                int[] coords = parseAddress(token);
                int depDepth = computeDepth(coords[0], coords[1], depths, visited);
                if (depDepth == -1) {
                    return -1;  // מעגליות
                }
                maxDepth = Math.max(maxDepth, depDepth);
                System.out.println("\nx: " + x + "\ny: " + y + "\nmaxDepth: " + maxDepth);
            }
        }

        visited[x][y] = false;
        depths[x][y] = maxDepth + 1;  // 1+עומק מירבי של תלויות
        return depths[x][y];
    }

    private boolean canBeComputedNow(int x, int y, int[][] depths) {
        Cell cell = table[x][y];
        String data = cell.getData();

        if (data == null || data.isEmpty() || !data.startsWith("=")) {
            return true;
        }

        String formula = data.substring(1).trim();
        String[] tokens = formula.split("(?<=[-+*/()])|(?=[-+*/()])");

        for (String token : tokens) {
            token = token.trim();
            if (isValidCellAddress(token)) {
                int[] coords = parseAddress(token);
                // אם תא תלוי בתא אחר שלא חושב עדיין
                if (depths[coords[0]][coords[1]] == -1) {
                    return false;
                }
            }
        }

        return true;
    }



    private int getMaxDependencyDepth(int x, int y, int[][] depths) {
        Cell cell = table[x][y];
        String data = cell.getData();

        if (data == null || data.isEmpty() || !data.startsWith("=")) {
            return 0; // תא ללא תלותות
        }

        String formula = data.substring(1).trim();
        String[] tokens = formula.split("(?<=[-+*/()])|(?=[-+*/()])");
        int maxDepth = -1;

        for (String token : tokens) {
            token = token.trim();
            if (isValidCellAddress(token)) {
                int[] coords = parseAddress(token);
                maxDepth = Math.max(maxDepth, depths[coords[0]][coords[1]]);
            }
        }

        return maxDepth; // מחזיר את העומק המקסימלי
    }

    // עדכון פונקצית parseAddress
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

            return new int[]{row, col};  // [שורה, עמודה]
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row in address: " + address);
        }
    }
    private boolean isValidCellAddress(String address) {
        try {
            parseAddress(address);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private String getCellAddress(int row, int col) {
        char column = (char) ('A' + col);
        return column + Integer.toString(row);
    }

    @Override
    public void load(String fileName) throws IOException {
        // Add your code here
    }

    @Override
    public void save(String fileName) throws IOException {
        // Add your code here
    }

    @Override
    public String eval(int x, int y) {
        if (!isIn(x, y)) {
            throw new IllegalArgumentException("Coordinates out of bounds: (" + x + ", " + y + ")");
        }

        Cell cell = table[x][y];
        String data = cell.getData();

        System.out.println("Evaluating cell (" + x + "," + y + "): " + data);

        if (data == null || data.isEmpty() || !data.startsWith("=")) {
            return data != null ? data : "";
        }

        try {
            String formula = data.substring(1).trim();
            System.out.println("Formula: " + formula);

            String[] tokens = formula.split("(?<=[-+*/()])|(?=[-+*/()])");
            double result = evaluateFormula(tokens, new HashSet<>());

            System.out.println("Result for cell (" + x + "," + y + "): " + result);

            return String.format("%.2f", result);
        } catch (Exception e) {
            return "#ERROR";
        }
    }



    private double evaluateFormula(String[] tokens, Set<String> visited) {
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (String token : tokens) {
            token = token.trim();

            if (token.isEmpty()) continue;

            if ("+-*/".contains(token)) {
                while (!operators.isEmpty() && hasPrecedence(token.charAt(0), operators.peek())) {
                    double b = values.pop();
                    double a = values.pop();
                    char op = operators.pop();
                    values.push(applyOperator(a, b, op));
                }
                operators.push(token.charAt(0));
            } else if (isValidCellAddress(token)) {
                int[] coords = parseAddress(token);
                double value = Double.parseDouble(eval(coords[0], coords[1]));
                values.push(value);
            } else {
                values.push(Double.parseDouble(token));
            }
        }

        while (!operators.isEmpty()) {
            double b = values.pop();
            double a = values.pop();
            char op = operators.pop();
            values.push(applyOperator(a, b, op));
        }

        return values.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false;
        }
        return true;
    }

    private double applyOperator(double a, double b, char operator) {
        switch (operator) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
            default: throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
}