public class Cell {
    private String cell_info;

    public Cell(String cell_info) {
        this.cell_info = cell_info;
    }

    public Cell() {
    }

    public String getCell_info() {
        return cell_info;
    }

    public void setCell_info(String cell_info) {
        this.cell_info = cell_info;
    }


    // This function checks if the input string is a valid formula
    public boolean isForm(String text) {
        if (text == null || text.isEmpty() || !text.startsWith("=")) {
            return false; // The formula must start with "=" and not be empty
        }

        // Remove the "=" and clean up spaces
        String formula = text.substring(1).trim();

        // Check if parentheses are balanced
        if (!areParenthesesBalanced(formula)) {
            return false; // Return false if parentheses are not balanced
        }

        // Try to analyze the formula
        try {
            return parseFormula(formula); // Start parsing the formula
        } catch (IllegalArgumentException e) {
            return false; // If there's an error, the formula is invalid
        }
    }

    // This function analyzes the formula recursively
    private boolean parseFormula(String formula) {
        formula = formula.trim(); // Remove extra spaces

        // If the formula is just a number
        if (isNumber(formula)) {
            return true; // Numbers are valid formulas
        }

        // If the formula is a valid cell (e.g., A1)
        if (isValidCell(formula)) {
            return true; // Cells like A1 are valid formulas
        }

        // If the formula is wrapped in parentheses
        if (formula.startsWith("(") && formula.endsWith(")")) {
            if (isMatchingParenthesis(formula, 0, formula.length() - 1)) {
                // Check the content inside the parentheses
                return parseFormula(formula.substring(1, formula.length() - 1));
            }
        }

        // Find the operator with the lowest priority (like + or -)
        int operatorIndex = findLowestPriorityOperator(formula);
        if (operatorIndex != -1) {
            // Split the formula into left and right parts
            String left = formula.substring(0, operatorIndex).trim();
            String right = formula.substring(operatorIndex + 1).trim();
            // Check both parts of the formula
            return parseFormula(left) && parseFormula(right);
        }

        return false; // If no pattern matches, it's not a valid formula
    }

    // This function finds the operator with the lowest priority
    public int findLowestPriorityOperator(String text) {
        int level = 0; // Tracks the depth of parentheses
        int lowestIndex = -1; // Where the operator is found
        int lowestPriority = Integer.MAX_VALUE; // The current lowest priority

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '(') {
                level++; // Inside parentheses
            } else if (c == ')') {
                level--; // Outside parentheses
            } else if (level == 0) { // Only check outside parentheses
                int priority = getOperatorPriority(c);
                if (priority <= lowestPriority) {
                    lowestPriority = priority; // Update priority
                    lowestIndex = i; // Update index
                }
            }
        }

        return lowestIndex; // Return where the operator is
    }

    // This function assigns priorities to operators
    private int getOperatorPriority(char c) {
        switch (c) {
            case '+':
            case '-':
                return 1; // + and - have lower priority
            case '*':
            case '/':
                return 2; // * and / have higher priority
            default:
                return Integer.MAX_VALUE; // Not an operator
        }
    }

    // This function checks if the text is a valid cell (e.g., A1)
    public boolean isValidCell(String text) {
        if (text.length() < 2) return false; // Cells must be at least two characters

        // The first character must be a letter
        char column = Character.toUpperCase(text.charAt(0));
        if (column < 'A' || column > 'Z') return false;

        // The rest must be a number
        try {
            int row = Integer.parseInt(text.substring(1));
            return row >= 0 && row < 100; // Rows must be between 0 and 99
        } catch (NumberFormatException e) {
            return false; // Not a valid number
        }
    }

    // This function checks if parentheses are balanced
    private boolean areParenthesesBalanced(String text) {
        int balance = 0; // Keeps track of open and close parentheses

        for (char c : text.toCharArray()) {
            if (c == '(') {
                balance++; // Open parenthesis
            } else if (c == ')') {
                balance--; // Close parenthesis
                if (balance < 0) return false; // Too many closing parentheses
            }
        }

        return balance == 0; // Return true if balanced
    }

    // This function checks if the text is a valid number
    public boolean isNumber(String text) {
        try {
            Double.parseDouble(text); // Try to convert to a number
            return true; // It's a valid number
        } catch (NumberFormatException e) {
            return false; // Not a valid number
        }
    }

    // This function checks if parentheses match
    private boolean isMatchingParenthesis(String expr, int open, int close) {
        int depth = 0; // Tracks parentheses depth

        for (int i = open; i <= close; i++) {
            if (expr.charAt(i) == '(') {
                depth++; // Open parenthesis
            } else if (expr.charAt(i) == ')') {
                depth--; // Close parenthesis
                if (depth == 0 && i != close) return false; // Mismatched
            }
        }

        return depth == 0; // True if parentheses match
    }


    public boolean isText(String text) {
        // Null or empty strings are not considered text
        if (text == null || text.isEmpty()) {
            return false;
        }

        // Check if the string is a number
        if (isNumber(text)) {
            return false;
        }

        // Check if the string is a formula
        if (isForm(text)) {
            return false;
        }

        // If it's neither a number nor a formula, it's valid text
        return true;
    }


    public double eval(String form) {
        // Step 1: Check if the formula is valid using isForm
        if (!isForm(form)) {
            throw new IllegalArgumentException("Invalid formula: " + form);
        }

        // Step 2: Remove "=" and trim whitespace
        String formula = form.substring(1).trim();

        // Step 3: Base case: Check if it's a number
        if (isNumber(formula)) {
            return Double.parseDouble(formula);
        }

        // Step 4: Base case: Check if it's a valid cell reference
        if (isValidCell(formula)) {
            throw new IllegalArgumentException("Cell references are not supported in this implementation");
        }

        // Step 5: Handle parentheses
        if (formula.startsWith("(") && formula.endsWith(")")) {
            if (isMatchingParenthesis(formula, 0, formula.length() - 1)) {
                // Evaluate the inner content
                return eval("=" + formula.substring(1, formula.length() - 1));
            }
        }

        // Step 6: Use the existing function to find the lowest priority operator
        int operatorIndex = findLowestPriorityOperator(formula);
        if (operatorIndex != -1) {
            // Split the formula into left and right parts
            String left = formula.substring(0, operatorIndex).trim();
            String right = formula.substring(operatorIndex + 1).trim();
            char operator = formula.charAt(operatorIndex);

            // Recursively evaluate both parts and perform the operation
            return calculate(eval("=" + left), eval("=" + right), operator);
        }

        // If no valid operator found, throw an error
        throw new IllegalArgumentException("Invalid formula");
    }

    private double calculate(double left, double right, char operator) {
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








}
















































