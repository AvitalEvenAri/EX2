package assignments.ex2;

/**
 * Represents a cell in a spreadsheet.
 * This class provides functionality to validate formulas, evaluate expressions,
 * and determine whether text is valid for a cell.
 */
public class Cell1 {
    private String cell_info;

    /**
     * Constructor to initialize a cell with information.
     * @param cell_info the information to set for the cell.
     */
    public Cell1(String cell_info) {
        this.cell_info = cell_info;
    }

    /**
     * Default constructor for an empty cell.
     */
    public Cell1() {
    }

    /**
     * Retrieves the cell information.
     * @return the cell information.
     */
    public String getCell_info() {
        return cell_info;
    }

    /**
     * Sets the cell information.
     * @param cell_info the information to set for the cell.
     */
    public void setCell_info(String cell_info) {
        this.cell_info = cell_info;
    }

    /**
     * Checks if the input string is a valid formula.
     * @param text the string to validate.
     * @return true if the string is a valid formula, false otherwise.
     */
    public boolean isForm(String text) {
        if (text == null || text.isEmpty() || !text.startsWith("=")) {
            return false; // The formula must start with "=" and not be empty
        }

        String formula = text.substring(1).trim(); // Remove the "=" and clean up spaces

        if (isNumber(formula)) return true; // Formulas like "=5" are valid
        if (isValidCell(formula)) return true; // Formulas like "=A1" are valid
        if (!areParenthesesBalanced(formula)) return false; // Parentheses must be balanced

        try {
            return parseFormula(formula); // Start parsing the formula
        } catch (IllegalArgumentException e) {
            return false; // Invalid formula
        }
    }

    /**
     * Parses and validates a formula recursively.
     * @param formula the formula to parse.
     * @return true if the formula is valid, false otherwise.
     */
    public boolean parseFormula(String formula) {
        formula = formula.trim();

        if (isNumber(formula)) return true; // Numbers are valid
        if (isValidCell(formula)) return true; // Cell references are valid

        if (formula.startsWith("(") && formula.endsWith(")")) {
            if (isMatchingParenthesis(formula, 0, formula.length() - 1)) {
                return parseFormula(formula.substring(1, formula.length() - 1)); // Validate inner formula
            }
        }

        int operatorIndex = findLowestPriorityOperator(formula);
        if (operatorIndex != -1) {
            String left = formula.substring(0, operatorIndex).trim();
            String right = formula.substring(operatorIndex + 1).trim();
            return parseFormula(left) && parseFormula(right);
        }

        return false; // No valid pattern matched
    }

    /**
     * Finds the operator with the lowest priority in a formula.
     * @param text the formula to analyze.
     * @return the index of the operator with the lowest priority.
     */
    public int findLowestPriorityOperator(String text) {
        int level = 0;
        int lowestIndex = -1;
        int lowestPriority = Integer.MAX_VALUE;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '(') level++;
            else if (c == ')') level--;
            else if (level == 0) { // Only consider operators outside parentheses
                int priority = getOperatorPriority(c);
                if (priority <= lowestPriority) {
                    lowestPriority = priority;
                    lowestIndex = i;
                }
            }
        }

        return lowestIndex;
    }

    /**
     * Assigns priority to operators.
     * @param c the operator character.
     * @return the priority value.
     */
    public int getOperatorPriority(char c) {
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

    /**
     * Checks if a string is a valid cell reference (e.g., "A1").
     * @param text the string to validate.
     * @return true if valid, false otherwise.
     */
    public boolean isValidCell(String text) {
        if (text == null || text.length() < 2) return false;
        char column = Character.toUpperCase(text.charAt(0));
        if (column < 'A' || column > 'Z') return false;

        try {
            int row = Integer.parseInt(text.substring(1));
            return row >= 0 && row < 100; // Row range between 0 and 99
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if parentheses are balanced in a string.
     * @param text the string to check.
     * @return true if balanced, false otherwise.
     */
    public boolean areParenthesesBalanced(String text) {
        int balance = 0;

        for (char c : text.toCharArray()) {
            if (c == '(') balance++;
            else if (c == ')') {
                balance--;
                if (balance < 0) return false;
            }
        }

        return balance == 0;
    }

    /**
     * Checks if a string represents a valid number.
     * @param text the string to check.
     * @return true if it's a number, false otherwise.
     */
    public boolean isNumber(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates and evaluates a formula.
     * @param form the formula to evaluate.
     * @return the result of the evaluation.
     */
    public double eval(String form) {
        if (!isForm(form)) throw new IllegalArgumentException("Invalid formula: " + form);

        String formula = form.substring(1).trim();

        if (isNumber(formula)) return Double.parseDouble(formula);
        if (isValidCell(formula)) throw new IllegalArgumentException("Cell references not supported");

        if (formula.startsWith("(") && formula.endsWith(")")) {
            if (isMatchingParenthesis(formula, 0, formula.length() - 1)) {
                return eval("=" + formula.substring(1, formula.length() - 1));
            }
        }

        int operatorIndex = findLowestPriorityOperator(formula);
        if (operatorIndex != -1) {
            String left = formula.substring(0, operatorIndex).trim();
            String right = formula.substring(operatorIndex + 1).trim();
            char operator = formula.charAt(operatorIndex);

            return calculate(eval("=" + left), eval("=" + right), operator);
        }

        throw new IllegalArgumentException("Invalid formula");
    }

    /**
     * Performs a calculation based on the operator and two operands.
     * @param left the left operand.
     * @param right the right operand.
     * @param operator the operator.
     * @return the result of the calculation.
     */
    public double calculate(double left, double right, char operator) {
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

    /**
     * Checks if parentheses match correctly.
     * @param expr the string to check.
     * @param open the index of the opening parenthesis.
     * @param close the index of the closing parenthesis.
     * @return true if parentheses match, false otherwise.
     */
    private boolean isMatchingParenthesis(String expr, int open, int close) {
        int depth = 0;

        for (int i = open; i <= close; i++) {
            if (expr.charAt(i) == '(') depth++;
            else if (expr.charAt(i) == ')') {
                depth--;
                if (depth == 0 && i != close) return false;
            }
        }

        return depth == 0;
    }

    /**
     * Checks if a string is valid text for a cell.
     * @param text the string to validate.
     * @return true if valid text, false otherwise.
     */
    public boolean isText(String text) {
        if (text == null || text.isEmpty()) return false;
        if (text.startsWith("=")) return false;
        return !isNumber(text);
    }
}
