public class Cell {
    private String cell_info;

    public Cell(String cell_info){
         this.cell_info = cell_info;
     }

    public String getCell_info() {
        return cell_info;
    }

    public void setCell_info(String cell_info) {
         this.cell_info = cell_info;
    }


    public static boolean isForm(String text) {
        // The formula must start with "="
        if (text == null || text.isEmpty() || !text.startsWith("=")) {
            return false; // Return false if the formula does not start with "="
        }

        // Remove "=" and trim whitespace
        String formula = text.substring(1).trim();

        // Check if parentheses are balanced
        if (!areParenthesesBalanced(formula)) {
            return false; // Return false if parentheses are not balanced
        }

        // Handle the formula recursively
        try {
            return parseFormula(formula);
        } catch (IllegalArgumentException e) {
            return false; // Return false if an invalid formula is encountered
        }
    }

    private static boolean parseFormula(String formula) {
        formula = formula.trim();

        // Base case: Check if it's a number
        if (isNumber(formula)) {
            return true; // It's a valid number
        }

        // Base case: Check if it's a cell reference
        if (isValidCell(formula)) {
            return true; // It's a valid cell reference
        }

        // Handle parentheses around the formula
        if (formula.startsWith("(") && formula.endsWith(")")) {
            // Check if parentheses are matching
            if (isMatchingParenthesis(formula, 0, formula.length() - 1)) {
                // Recursively check the content inside the parentheses
                return parseFormula(formula.substring(1, formula.length() - 1));
            }
        }

        // Find the lowest priority operator (outside parentheses)
        int operatorIndex = findLowestPriorityOperator(formula);
        if (operatorIndex != -1) {
            // Split the formula into left and right parts
            String left = formula.substring(0, operatorIndex).trim();
            String right = formula.substring(operatorIndex + 1).trim();
            return parseFormula(left) && parseFormula(right); // Check both parts recursively
        }

        // Check for implicit multiplication (e.g., (1+2)(3+4))
        int implicitIndex = findImplicitMultiplication(formula);
        if (implicitIndex != -1) {
            String left = formula.substring(0, implicitIndex).trim();
            String right = formula.substring(implicitIndex).trim();
            return parseFormula(left) && parseFormula(right); // Check both parts recursively
        }

        return false; // Return false if no valid pattern is matched
    }

    private static int findLowestPriorityOperator(String text) {
        int level = 0;
        int lowestIndex = -1;
        int lowestPriority = Integer.MAX_VALUE;

        // Look for the lowest priority operator outside parentheses
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '(') {
                level++; // Enter parentheses
            } else if (c == ')') {
                level--; // Exit parentheses
            } else if (level == 0) { // Only check operators outside parentheses
                int priority = getOperatorPriority(c);
                if (priority <= lowestPriority) {
                    lowestPriority = priority;
                    lowestIndex = i;
                }
            }
        }

        return lowestIndex; // Return the index of the operator
    }

    private static int getOperatorPriority(char c) {
        // Assign priorities to operators
        switch (c) {
            case '+':
            case '-':
                return 1; // Lower priority
            case '*':
            case '/':
                return 2; // Higher priority
            default:
                return Integer.MAX_VALUE; // Not an operator
        }
    }

    private static int findImplicitMultiplication(String expr) {
        int depth = 0;

        // Look for cases like (1+2)(3+4)
        for (int i = 0; i < expr.length() - 1; i++) {
            char current = expr.charAt(i);
            char next = expr.charAt(i + 1);

            if (current == '(') depth++;
            else if (current == ')') {
                depth--;
                // Check if implicit multiplication exists
                if (depth == 0 && (next == '(' || Character.isLetterOrDigit(next))) {
                    return i + 1; // Return the index where implicit multiplication occurs
                }
            }
        }

        return -1; // Return -1 if no implicit multiplication is found
    }

    private static boolean isValidCell(String text) {
        // A valid cell must start with a letter and end with a number
        if (text.length() < 2) return false;

        // Check the first character is a letter
        char column = Character.toUpperCase(text.charAt(0));
        if (column < 'A' || column > 'Z') return false;

        // Check the rest is a valid number
        try {
            int row = Integer.parseInt(text.substring(1));
            return row >= 0 && row < 100; // Valid cell row range: 0-99
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean areParenthesesBalanced(String text) {
        int balance = 0;

        // Check if parentheses are balanced
        for (char c : text.toCharArray()) {
            if (c == '(') balance++;
            else if (c == ')') {
                balance--;
                if (balance < 0) return false; // Too many closing parentheses
            }
        }

        return balance == 0; // Return true if balanced
    }

    private static boolean isNumber(String text) {
        // Check if the text is a valid number
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isMatchingParenthesis(String expr, int open, int close) {
        int depth = 0;

        // Check if parentheses match
        for (int i = open; i <= close; i++) {
            if (expr.charAt(i) == '(') depth++;
            else if (expr.charAt(i) == ')') {
                depth--;
                if (depth == 0 && i != close) return false; // Mismatched parentheses
            }
        }

        return depth == 0; // Return true if matching
    }



    }

















































