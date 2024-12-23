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


        // Main function to check if the text is a valid formula
        public static boolean isFormula(String text) {
            if (text == null || text.isEmpty() || !text.startsWith("=")) {
                return false;
            }
            String withoutEquals = insertImplicitMultiplication(text.substring(1));
            if (!areParenthesesBalanced(withoutEquals)) {
                return false;
            }
            if (isNumber(withoutEquals)) {
                return true;
            }
            if (isCell(withoutEquals)) {
                return true;
            }
            int operatorIndex = findLowestPriorityOperator(withoutEquals);
            if (operatorIndex != -1) {
                String left = withoutEquals.substring(0, operatorIndex);
                String right = withoutEquals.substring(operatorIndex + 1);
                return isFormula("=" + left) && isFormula("=" + right);
            }
            return false;
        }

    private static boolean areParenthesesBalanced(String text) {
        int balance = 0;
        for (char c : text.toCharArray()) {
            if (c == '(') balance++;
            if (c == ')') balance--;
            if (balance < 0) return false;
        }
        return balance == 0;
    }

    private static String insertImplicitMultiplication(String text) {
        return text.replaceAll("\\)\\(", ")*(");
    }

    private static int findLowestPriorityOperator(String text) {
        int level = 0;
        for (int i = text.length() - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == '(') level++;
            else if (c == ')') level--;
            else if (level == 0 && (c == '+' || c == '-')) {
                return i;
            }
        }
        for (int i = text.length() - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (level == 0 && (c == '*' || c == '/')) {
                return i;
            }
        }
        return -1;
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
        System.out.println(areParenthesesBalanced("(1+2)*3")); // true
        System.out.println(areParenthesesBalanced("3+(9*8)+2")); // true
        System.out.println(areParenthesesBalanced("(1+2)((3))-1")); // true
        System.out.println(insertImplicitMultiplication("(1+2)(3)")); // (1+2)*(3)
        System.out.println(insertImplicitMultiplication("((1+2)(3))")); // ((1+2)*(3))
        System.out.println(insertImplicitMultiplication("(1+2)*3")); // (1+2)*3
        System.out.println(findLowestPriorityOperator("(1+2)*3")); // 3 (index of *)
        System.out.println(findLowestPriorityOperator("3+(9*8)+2")); // 1 (index of +)
        System.out.println(findLowestPriorityOperator("(1+2)((3))-1")); // 10 (index of -)
    }
    }











