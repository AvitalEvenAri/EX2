import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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




        public static boolean isFormula(String text) {
            System.out.println("Checking formula: " + text);

            // Check if the formula starts with "="
            if (text == null || text.isEmpty() || !text.startsWith("=")) {
                System.out.println("Error: Formula must start with '='");
                return false;
            }

            // Remove "="
            String withoutEquals = text.substring(1).trim();
            System.out.println("Without '=': " + withoutEquals);

            // Check if the parentheses are balanced
            if (!areParenthesesBalanced(withoutEquals)) {
                System.out.println("Error: Parentheses are not balanced.");
                return false;
            }

            // Handle implicit multiplication (e.g., `)((` becomes `)*(`)
            withoutEquals = insertImplicitMultiplication(withoutEquals);
            System.out.println("After implicit multiplication: " + withoutEquals);

            // Parse the formula recursively
            return parseFormula(withoutEquals);
        }

        private static boolean parseFormula(String text) {
            System.out.println("Parsing formula: " + text);

            // Base case: Check if it's a number
            if (isNumber(text)) {
                System.out.println("It's a valid number.");
                return true;
            }

            // Base case: Check if it's a cell
            if (isCell(text)) {
                System.out.println("It's a valid cell.");
                return true;
            }

            // Handle parentheses first
            int openParenIndex = text.lastIndexOf('(');
            while (openParenIndex != -1) {
                int closeParenIndex = findMatchingParenthesis(text, openParenIndex);
                if (closeParenIndex == -1) {
                    System.out.println("Error: Parentheses are not balanced.");
                    return false;
                }

                String inner = text.substring(openParenIndex + 1, closeParenIndex).trim();
                System.out.println("Checking inner parentheses: " + inner);
                if (!parseFormula(inner)) {
                    return false;
                }

                // Replace the evaluated parentheses with a placeholder
                text = text.substring(0, openParenIndex) + "0" + text.substring(closeParenIndex + 1);
                openParenIndex = text.lastIndexOf('(');
            }

            // Find the lowest priority operator (+, -, *, /) outside parentheses
            int operatorIndex = findLowestPriorityOperator(text);
            if (operatorIndex != -1) {
                String left = text.substring(0, operatorIndex).trim();
                String right = text.substring(operatorIndex + 1).trim();
                char operator = text.charAt(operatorIndex);
                System.out.println("Operator: " + operator + ", Left: " + left + ", Right: " + right);

                // Recursively check left and right parts
                return parseFormula(left) && parseFormula(right);
            }

            System.out.println("Error: Not a valid formula.");
            return false;
        }

        private static boolean areParenthesesBalanced(String text) {
            int balance = 0;
            for (char c : text.toCharArray()) {
                if (c == '(') balance++;
                if (c == ')') balance--;
                if (balance < 0) return false; // More closing than opening
            }
            return balance == 0;
        }

        private static int findMatchingParenthesis(String text, int openIndex) {
            int balance = 1;
            for (int i = openIndex + 1; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c == '(') balance++;
                if (c == ')') balance--;
                if (balance == 0) return i;
            }
            return -1; // No matching closing parenthesis found
        }

        private static String insertImplicitMultiplication(String text) {
            // Add * between closing and opening parentheses
            return text.replaceAll("\\)\\(", ")*(");
        }

        private static int findLowestPriorityOperator(String text) {
            int level = 0;
            int lowestIndex = -1;
            int lowestPriority = Integer.MAX_VALUE;

            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);

                if (c == '(') {
                    level++;
                } else if (c == ')') {
                    level--;
                } else if (level == 0) { // Only check operators outside parentheses
                    int priority = getOperatorPriority(c);
                    if (priority < lowestPriority) {
                        lowestPriority = priority;
                        lowestIndex = i;
                    }
                }
            }

            return lowestIndex;
        }

        private static int getOperatorPriority(char c) {
            if (c == '+' || c == '-') return 1;
            if (c == '*' || c == '/') return 2;
            return Integer.MAX_VALUE; // Not an operator
        }

        private static boolean isNumber(String text) {
            try {
                Double.parseDouble(text);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        private static boolean isCell(String text) {
            return text.matches("[a-zA-Z][0-9]+");
        }

        public static void main(String[] args) {
            // Test cases
            System.out.println(isFormula("=(1+2)*3"));       // true
            System.out.println(isFormula("=3+(9*8)+2"));     // true
            System.out.println(isFormula("=(1+2)((3))-1"));  // true
            System.out.println(isFormula("=(A1+B2)*C3"));    // true
            System.out.println(isFormula("=(2+"));           // false
            System.out.println(isFormula("=3+(9*8"));        // false
            System.out.println(isFormula("=A1+B2"));         // true
            System.out.println(isFormula("=123"));           // true
            System.out.println(isFormula("=a1"));            // true
            System.out.println(isFormula("=1+2"));           // true
        }
    }





























