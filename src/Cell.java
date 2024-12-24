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
        // The formula must start with "=" to be valid
        if (text == null || text.isEmpty() || !text.startsWith("=")) {
            return false; // Return false if the formula is null, empty, or doesn't start with "="
        }

        // Remove "=" and trim any extra spaces from the beginning or end
        String formula = text.substring(1).trim();

        // Check if the parentheses in the formula are balanced
        if (!areParenthesesBalanced(formula)) {
            return false; // Return false if the parentheses are not balanced
        }

        // Try to analyze the formula recursively
        try {
            return parseFormula(formula); // Start the recursive parsing
        } catch (IllegalArgumentException e) {
            return false; // Return false if an invalid formula is encountered
        }
    }

    private static boolean parseFormula(String formula) {
        formula = formula.trim(); // Remove extra spaces around the formula

        // Check if the formula is a simple number
        if (isNumber(formula)) {
            return true; // A valid number is a valid formula
        }

        // Check if the formula is a valid cell reference (e.g., A1)
        if (isValidCell(formula)) {
            return true; // A valid cell is a valid formula
        }

        // Handle cases where the formula is wrapped in parentheses
        if (formula.startsWith("(") && formula.endsWith(")")) {
            // Ensure that the outer parentheses are matching
            if (isMatchingParenthesis(formula, 0, formula.length() - 1)) {
                // Remove the outer parentheses and parse the inner content recursively
                return parseFormula(formula.substring(1, formula.length() - 1));
            }
        }

        // Look for the lowest priority operator outside parentheses (e.g., + or -)
        int operatorIndex = findLowestPriorityOperator(formula);
        if (operatorIndex != -1) {
            // Split the formula into left and right parts based on the operator
            String left = formula.substring(0, operatorIndex).trim();
            String right = formula.substring(operatorIndex + 1).trim();
            // Recursively parse both parts
            return parseFormula(left) && parseFormula(right);
        }

        // Handle implicit multiplication (e.g., (1+2)(3+4))
        int implicitIndex = findImplicitMultiplication(formula);
        if (implicitIndex != -1) {
            // Split the formula where implicit multiplication occurs
            String left = formula.substring(0, implicitIndex).trim();
            String right = formula.substring(implicitIndex).trim();
            // Recursively parse both parts
            return parseFormula(left) && parseFormula(right);
        }

        return false; // If none of the patterns match, it's not a valid formula
    }

    private static int findLowestPriorityOperator(String text) {
        int level = 0; // Tracks the depth of parentheses
        int lowestIndex = -1; // Index of the operator with the lowest priority
        int lowestPriority = Integer.MAX_VALUE; // The current lowest priority found

        // Loop through all characters in the formula
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '(') {
                level++; // Increase depth when entering parentheses
            } else if (c == ')') {
                level--; // Decrease depth when exiting parentheses
            } else if (level == 0) { // Only check operators outside parentheses
                int priority = getOperatorPriority(c); // Get the priority of the current operator
                if (priority <= lowestPriority) { // Lower priority means higher precedence
                    lowestPriority = priority; // Update the lowest priority found
                    lowestIndex = i; // Update the index of the operator
                }
            }
        }

        return lowestIndex; // Return the index of the operator with the lowest priority
    }

    private static int getOperatorPriority(char c) {
        // Assign priorities to operators
        switch (c) {
            case '+':
            case '-':
                return 1; // Addition and subtraction have the lowest priority
            case '*':
            case '/':
                return 2; // Multiplication and division have higher priority
            default:
                return Integer.MAX_VALUE; // Not an operator
        }
    }

    private static int findImplicitMultiplication(String expr) {
        int depth = 0; // Tracks the depth of parentheses

        // Loop through all characters except the last one
        for (int i = 0; i < expr.length() - 1; i++) {
            char current = expr.charAt(i);
            char next = expr.charAt(i + 1);

            if (current == '(') {
                depth++; // Increase depth when entering parentheses
            } else if (current == ')') {
                depth--; // Decrease depth when exiting parentheses
                // Look for implicit multiplication
                if (depth == 0 && (next == '(' || Character.isLetterOrDigit(next))) {
                    return i + 1; // Return the index where implicit multiplication is found
                }
            }
        }

        return -1; // No implicit multiplication was found
    }

    private static boolean isValidCell(String text) {
        // A cell must be at least two characters long
        if (text.length() < 2) return false;

        // Get the first character and ensure it is a letter (uppercase or lowercase)
        char column = Character.toUpperCase(text.charAt(0));
        if (column < 'A' || column > 'Z') return false; // Column must be between A and Z

        // Try to parse the remaining characters as a number (the row)
        try {
            int row = Integer.parseInt(text.substring(1));
            return row >= 0 && row < 100; // Rows must be between 0 and 99
        } catch (NumberFormatException e) {
            return false; // If the rest is not a number, it's not a valid cell
        }
    }

    private static boolean areParenthesesBalanced(String text) {
        int balance = 0; // Tracks the balance of parentheses

        // Loop through all characters in the formula
        for (char c : text.toCharArray()) {
            if (c == '(') {
                balance++; // Increase balance for an open parenthesis
            } else if (c == ')') {
                balance--; // Decrease balance for a close parenthesis
                if (balance < 0) return false; // Too many closing parentheses
            }
        }

        return balance == 0; // Return true if all parentheses are balanced
    }

    public static boolean isNumber(String text) {
        // Check if the text is a valid number
        try {
            Double.parseDouble(text);
            return true; // The text is a valid number
        } catch (NumberFormatException e) {
            return false; // Not a valid number
        }
    }

    private static boolean isMatchingParenthesis(String expr, int open, int close) {
        int depth = 0; // Tracks the depth of parentheses

        // Check if the parentheses match
        for (int i = open; i <= close; i++) {
            if (expr.charAt(i) == '(') {
                depth++; // Increase depth for an open parenthesis
            } else if (expr.charAt(i) == ')') {
                depth--; // Decrease depth for a close parenthesis
                if (depth == 0 && i != close) return false; // Mismatched parentheses
            }
        }

        return depth == 0; // Return true if the parentheses match
    }
    public static boolean isText(String text) {
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

}
















































