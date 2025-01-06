package assignments.ex2;

public class CellEntry implements Index2D {
    private final String index; // נתיב התא (למשל "B3")

    public CellEntry(String index) {
        this.index = index;
    }

    @Override
    public boolean isValid() {
        if (index == null || index.length() < 2) {
            return false; // שם התא לא חוקי
        }
        // בדיקת האות הראשונה
        char column = Character.toUpperCase(index.charAt(0));
        if (column < 'A' || column > 'Z') {
            return false; // האות לא חוקית
        }

        try {
            // הסרת "=" רק אם הוא בתחילת המחרוזת
            String numericPart = index.startsWith("=") ? index.substring(1) : index;
            numericPart = numericPart.substring(1); // חילוץ המספר מהחלק השני
            int row = Integer.parseInt(numericPart); // המרה למספר
            return row >= 0 && row <= 99; // בדיקת טווח
        } catch (Exception e) {
            return false; // במקרה של חריגה
        }
    }

    @Override
    public int getX() {
        if (!isValid()) {
            return Ex2Utils.ERR;
        }
        char column = Character.toUpperCase(index.charAt(0));
        return column - 'A';
    }

//    @Override
//    public int getY() {
//        if (!isValid()) {
//            return Ex2Utils.ERR;
//        }
//        try {
//            String numericPart = index.replace("=", "").substring(1);
//            return Integer.parseInt(numericPart);
//        } catch (NumberFormatException e) {
//            return Ex2Utils.ERR;
//        }
//    }
@Override
public int getY() {
    if (!isValid()) {
        return Ex2Utils.ERR;
    }
    try {
        // הסרת סימן "=" וניקוי רווחים
        String numericPart = index.replace("=", "").trim();
        if (numericPart.length() <= 1) {
            return Ex2Utils.ERR; // אם אין מספיק תווים
        }
        numericPart = numericPart.substring(1).trim(); // חילוץ המספר וניקוי רווחים נוספים
            Integer result = Integer.parseInt(numericPart)-1;
        return result;
    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
        return Ex2Utils.ERR; // שגיאה בניתוח המספר
    }
}

    @Override
    public String toString() {
        if (!isValid()) {
            return "null=";
        }
        char col = Character.toUpperCase(index.charAt(0));
        String numericPart = index.substring(1); // מחזיר את המספר אחרי האות
        return col + numericPart;
    }



}