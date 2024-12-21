import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        create_cells_data(10, 5);
    }


    public static void create_cells_data(int len_row, int len_cels){
        List<String> list = new ArrayList<>();
        char row;
        for(int i = 65; i < len_row + 65; i ++){
            row = (char) i;
            for(int x = 0; x < len_cels; x++){
                list.add(String.valueOf(row) + String.valueOf(x));
            }
        }
        System.out.println(list);
    }
}