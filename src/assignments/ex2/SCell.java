package assignments.ex2;
// Add your documentation below:

public class SCell implements Cell {
    private String line;
    private int type;
    private int order;   // The computational order of the cell
    private String formule;
    /**
     * Constructor for SCell. Initializes the cell with given data.
     * @param s The data to initialize the cell with.
     */
    public SCell(String s) {
        setData(s);
    }

    @Override
    public int getOrder() {
        // If the cell type is TEXT or NUMBER, the computation order is 0
        if (type == Ex2Utils.TEXT || type == Ex2Utils.NUMBER) {
            return 0;
        }

        // If the formula contains an error, return -1
        if (type == Ex2Utils.ERR_FORM_FORMAT || type == Ex2Utils.ERR_CYCLE_FORM) {
            return -1;
        }

        // For formulas, the order is stored and managed by setOrder
        return order;
    }


    @Override
    public String toString() {
        return getData();
    }


    @Override
    public void setData(String s) {
        // Handle null input explicitly
        if (s == null) {
            line = null; // Store null in the data field
            type = Ex2Utils.ERR_FORM_FORMAT; // Mark as an error
            order = -1; // Invalid data defaults to order -1
            return; // Exit the method early
        }

        // Store the input data in the line variable
        line = s;

        // Check for empty string input
        if (s.isEmpty()) {
            type = Ex2Utils.TEXT; // Mark as text
            order = 0; // Text has an order of 0
            return; // Exit early for empty strings
        }

        // Check if the input is a number
        if (isNumber(s)) {
            type = Ex2Utils.NUMBER; // Mark as a number
            order = 0; // Numbers have an order of 0
        }
        // Check if the input is valid text
        else if (isText(s)) {
            type = Ex2Utils.TEXT; // Mark as text
            order = 0; // Text has an order of 0
        }
        // Check if the input is a formula
        else if (isForm(s)) {
            if (parseFormula(s.substring(1).trim())) {
                type = Ex2Utils.FORM; // Mark as a formula
                order = -1; // Order will be calculated later
                formule = s;
            } else {
                type = Ex2Utils.ERR_FORM_FORMAT; // Mark as an invalid formula
                order = -1; // Invalid formulas default to order -1
            }
        }
        // Mark as an error if none of the above conditions are met
        else {
            type = Ex2Utils.ERR_FORM_FORMAT; // Invalid data type
            order = -1; // Errors default to order -1
        }
    }

    public String getFormule() {
        return formule;
    }

    public void setFormule(String formule) {
        this.formule = formule;
    }

    @Override
    public String getData() {
        // Returns the raw data (string) stored in the cell
        return line;
    }


    @Override
    public int getType() {
        // Returns the type of the cell
        return type;
    }


    @Override
    public void setType(int t) {
        // Updates the cell's type
        type = t;
    }


    @Override
    public void setOrder(int t) {
        if (type == Ex2Utils.FORM) { // Only update the order if the cell is a formula
            this.order = t;
        }
    }




    // Function to check if the input string is a valid formula
    public boolean isForm(String text) {
        if (text == null || text.isEmpty() || !text.startsWith("=")) {
            return false;
        }
        String formula = text.substring(1).trim();
        if (isNumber(formula) || isValidCell(formula) || areParenthesesBalanced(formula)) {
            return true;
        }
        try {
            return parseFormula(formula);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Recursive function to parse formulas
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

    private int getOperatorPriority(char c) {
        switch (c) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return Integer.MAX_VALUE;
        }
    }

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

    private boolean areParenthesesBalanced(String text) {
        int balance = 0;

        for (char c : text.toCharArray()) {
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
                if (balance < 0) return false;
            }
        }

        return balance == 0;
    }

    public boolean isNumber(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isMatchingParenthesis(String expr, int open, int close) {
        int depth = 0;

        for (int i = open; i <= close; i++) {
            if (expr.charAt(i) == '(') {
                depth++;
            } else if (expr.charAt(i) == ')') {
                depth--;
                if (depth == 0 && i != close) return false;
            }
        }

        return depth == 0;
    }

    public boolean isText(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        if (text.startsWith("=")) {
            return false;
        }
        return !isNumber(text);
    }
}

