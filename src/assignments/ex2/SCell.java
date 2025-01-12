package assignments.ex2;

public class SCell implements Cell {
    private String line;           // Original string
    private String computedValue;  // Computed value
    private int type;              // Cell type (number, text, formula, etc.)
    private int order;             // Calculation order for formulas
    private String cellAddress;
    private CellEntry cellEntry;

    // Constructor for SCell
    public SCell(String s) {
        setData(s); // Set the data for the cell
    }

    // Set the CellEntry
    public void setCellEntry(CellEntry entry) {
        this.cellEntry = entry;
    }

    // Get the CellEntry
    public CellEntry getCellEntry() {
        return cellEntry;
    }

    @Override
    public int getOrder() {
        if (type == Ex2Utils.TEXT || type == Ex2Utils.NUMBER) {
            return 0; // Text and number types have order 0
        }
        if (type == Ex2Utils.ERR_FORM_FORMAT || type == Ex2Utils.ERR_CYCLE_FORM) {
            return -1; // Error types have order -1
        }
        return order; // Return the order for formulas
    }

    @Override
    public String toString() {
        // Returns only the cell's data to avoid duplicate cell address display in GUI
        return getData();
    }

    @Override
    public void setData(String s) {
        if (s == null) {
            line = null;
            computedValue = null;
            type = Ex2Utils.ERR_FORM_FORMAT;
            order = -1;
            return;
        }

        line = s;
        computedValue = null;

        if (s.isEmpty()) {
            type = Ex2Utils.TEXT;
            order = 0;
            return;
        }

        if (isNumber(s)) {
            type = Ex2Utils.NUMBER;
            order = 0;
            computedValue = s;
        } else if (isText(s)) {
            type = Ex2Utils.TEXT;
            order = 0;
            computedValue = s;
        } else if (isForm(s)) {
            if (parseFormula(s.substring(1).trim())) {
                type = Ex2Utils.FORM;
                order = -1;
            } else {
                type = Ex2Utils.ERR_FORM_FORMAT;
                order = -1;
            }
        } else {
            type = Ex2Utils.ERR_FORM_FORMAT;
            order = -1;
        }
    }

    // Get the computed value
    public String getComputedValue() {
        return computedValue;
    }

    // Set the computed value
    public void setComputedValue(String value) {
        this.computedValue = value;
    }

    @Override
    public String getData() {
        return line;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        type = t;
    }

    @Override
    public void setOrder(int t) {
        if (type == Ex2Utils.FORM) {
            this.order = t;
        }
    }

    // Check if the text is a formula
    public boolean isForm(String text) {
        if (text == null || text.isEmpty() || !text.startsWith("=")) {
            return false;
        }
        String formula = text.substring(1).trim();
        return isNumber(formula) || isValidCell(formula) || parseFormula(formula);
    }

    // Parse the formula
    public boolean parseFormula(String formula) {
        formula = formula.trim();

        if (isNumber(formula)) {
            return true;
        }

        if (isValidCell(formula)) {
            return true;
        }

        if (formula.startsWith("(") && formula.endsWith(")")) {
            if (isMatchingParenthesis(formula, 0, formula.length() - 1)) {
                return parseFormula(formula.substring(1, formula.length() - 1));
            }
        }

        int operatorIndex = findLowestPriorityOperator(formula);
        if (operatorIndex != -1) {
            String left = formula.substring(0, operatorIndex).trim();
            String right = formula.substring(operatorIndex + 1).trim();
            return parseFormula(left) && parseFormula(right);
        }

        return false;
    }

    // Find the lowest priority operator in the formula
    public int findLowestPriorityOperator(String text) {
        int level = 0;
        int lowestIndex = -1;
        int lowestPriority = Integer.MAX_VALUE;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '(') {
                level++;
            } else if (c == ')') {
                level--;
            } else if (level == 0) {
                int priority = getOperatorPriority(c);
                if (priority <= lowestPriority) {
                    lowestPriority = priority;
                    lowestIndex = i;
                }
            }
        }

        return lowestIndex;
    }

    // Get the priority of the operator
    private int getOperatorPriority(char c) {
        return switch (c) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            default -> Integer.MAX_VALUE;
        };
    }

    // Check if the parentheses in the expression are matching
    private boolean isMatchingParenthesis(String expr, int open, int close) {
        int depth = 0;

        for (int i = open; i <= close; i++) {
            char c = expr.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
                if (depth < 0 || (depth == 0 && i != close)) {
                    return false;
                }
            }
        }

        return depth == 0;
    }

    // Check if the text is a valid cell address
    public boolean isValidCell(String text) {
        if (text == null || text.length() < 2) return false;
        char column = Character.toUpperCase(text.charAt(0));
        if (column < 'A' || column > 'Z') return false;

        try {
            int row = Integer.parseInt(text.substring(1));
            return row >= 0 && row < 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Check if the text is a number
    public boolean isNumber(String text) {
        if (text.startsWith("-")) {
            text = text.substring(1);
        }
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Check if the text is plain text
    public boolean isText(String text) {
        return text != null && !text.isEmpty() && !isNumber(text) && !text.startsWith("=");
    }
}


