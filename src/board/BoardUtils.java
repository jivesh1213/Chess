package board;

public class BoardUtils {
    
    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHTH_COLUMN = initColumn(7);
    
    public static final boolean[] FIRST_ROW = initRow(0);
    public static final boolean[] SECOND_ROW = initRow(8);
    public static final boolean[] THIRD_ROW = initRow(16);
    public static final boolean[] FOURTH_ROW = initRow(24);
    public static final boolean[] FIFTH_ROW = initRow(32);
    public static final boolean[] SIXTH_ROW = initRow(40);
    public static final boolean[] SEVENTH_ROW = initRow(48);
    public static final boolean[] EIGHTH_ROW = initRow(56);
    
    private BoardUtils(){
        throw new RuntimeException("You cannot instantiate me");
    }
    
    private static boolean[] initRow(int i){
        final boolean[] row = new boolean[64];
        do{
            row[i] = true;
            i++;
        }while(i % 8 != 0);
        
        return row;
    }
    
    private static boolean[] initColumn(int j) {
        final boolean[] column = new boolean[64];
        do{
            column[j] = true;
            j += 8;
        }while(j < 64);
        return column;
    }
    
    public static boolean isValidTileCoordinate(final int coordinate){
        return coordinate >= 0 && coordinate < 64;
    }

}
