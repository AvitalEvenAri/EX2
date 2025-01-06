package assignments.ex2;

public class CellEntry implements Index2D {
    private String index; // נתיב התא (למשל "B3")

    public CellEntry(String index) {
        if (index != null && index.endsWith("=")) {
            this.index = index.substring(0, index.length() - 1);
        } else {
            this.index = index;
        }
        System.out.println("CellEntry created with index: " + this.index); // Debugging
    }

    @Override
    public boolean isValid() {
        if (index == null || index.length() < 2) {
            System.out.println("isValid: false (index is null or too short)"); // Debugging
            return false;
        }
        char column = Character.toUpperCase(index.charAt(0));
        if (column < 'A' || column > 'Z') {
            System.out.println("isValid: false (column out of range: " + column + ")"); // Debugging
            return false;
        }

        try {
            String numericPart = index.substring(1).trim();
            int row = Integer.parseInt(numericPart);
            boolean valid = row >= 0 && row <= 99;
            System.out.println("isValid: " + valid + " (row: " + row + ")"); // Debugging
            return valid;
        } catch (Exception e) {
            System.out.println("isValid: false (exception while parsing row)"); // Debugging
            return false;
        }
    }

    @Override
    public int getX() {
        if (!isValid()) {
            System.out.println("getX: invalid index, returning ERR"); // Debugging
            return Ex2Utils.ERR;
        }
        char column = Character.toUpperCase(index.charAt(0));
        int x = column - 'A';
        System.out.println("getX: " + x); // Debugging
        return x;
    }

    @Override
    public int getY() {
        if (!isValid()) {
            System.out.println("getY: invalid index, returning ERR"); // Debugging
            return Ex2Utils.ERR;
        }
        try {
            String numericPart = index.substring(1).trim();
            int y = Integer.parseInt(numericPart);
            System.out.println("getY: " + y); // Debugging
            return y;
        } catch (NumberFormatException e) {
            System.out.println("getY: exception while parsing row, returning ERR"); // Debugging
            return Ex2Utils.ERR;
        }
    }

    @Override
    public String toString() {
        if (this.getY() < 0 || this.getY() >= Ex2Utils.ABC.length) {
            return "null=";
        }
        //Ex2Utils.ABC[this.getY()] + this.getX()
        return Ex2Utils.ABC[this.getX()] + this.getY();
    }

}