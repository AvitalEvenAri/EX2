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

    private boolean isNumber(String text){
        if(this.isNotEmpty(text)){
            return false;
        }
        if( !((text.charAt(0)=='-') || Character.isDigit(text.charAt(0)))){
            return false;
        }

        char[] char_holder = text.substring(1).toCharArray();
        for (char c : char_holder) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
    private boolean isText(String text) {
        if(this.isNotEmpty(text)){
            return false;
        }
        return true;
    }
    private boolean isNotEmpty(String text){
        if (text == null || text.isEmpty()) {
            return false;
        }
        return true;
    }
    private boolean isForm(String form, List<String> data_holder){
        if(this.isNotEmpty(form)){
            return false;
        }
        //count ( )
        int count_open = 0;
        int count_close = 0;

        for (char c : form.toCharArray()) {
            if (c == '(') {
                count_open++;
            }else if (c == ')'){
                count_close++;
            }
        }
        //delete all ( )
        form.replaceAll("\\(","");
        form.replaceAll("\\)","");

        if (count_close != count_open){
            //error with the open ( and clos )
            return false;
        }

        List<Integer> list = new ArrayList<>();
        int count = 0;
        for(char c : form.toCharArray()){
            if (c == '+' || c == '-' || c == '/' || c == '*') {
                list.add(count);
            }
            count++;
        }

        if (form.length() == list.get(list.size() - 1)+1){
            // the last varibal is - + * /
            return false;
        }else {
            list.add(form.length() -1);
        }

        if(!this.isNumber(form.substring(0, list.get(1)))){
            if(!data_holder.contains(form.substring(0, list.get(1)))){
                return false;
            }
        }
        for(int i = 0; i < list.size() - 1; i++){
            if(!this.isNumber(form.substring(list.get(i), list.get(i+1)))){
                if(!data_holder.contains(form.substring(list.get(i), list.get(i+1)))){
                    return false;
                }
            }
        }
        return true;
    }

}
