package assignments.ex2;

public class SCell implements Cell {
    private String line;           // המחרוזת המקורית
    private String computedValue;  // הערך המחושב
    private int type;              // סוג התא (מספר, טקסט, נוסחה וכו')
    private int order;             // סדר החישוב עבור נוסחאות

    public SCell(String s) {
        setData(s);
    }

    @Override
    public int getOrder() {
        if (type == Ex2Utils.TEXT || type == Ex2Utils.NUMBER) {
            return 0;
        }
        if (type == Ex2Utils.ERR_FORM_FORMAT || type == Ex2Utils.ERR_CYCLE_FORM) {
            return -1;
        }
        return order;
    }

    @Override
    public String toString() {
        return computedValue != null ? computedValue : line;
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

    public String getComputedValue() {
        return computedValue;
    }

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

    public boolean isForm(String text) {
        if (text == null || text.isEmpty() || !text.startsWith("=")) {
            return false;
        }
        String formula = text.substring(1).trim();
        return isNumber(formula) || isValidCell(formula) || parseFormula(formula);
    }

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
        return switch (c) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            default -> Integer.MAX_VALUE;
        };
    }

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

    public boolean isNumber(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isText(String text) {
        return text != null && !text.isEmpty() && !isNumber(text) && !text.startsWith("=");
    }
}