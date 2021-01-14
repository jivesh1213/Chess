package board;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    public static final String[] ALGEBRIC_NOTATION = initializeAlgebricNotation();
    public static final Map<String, Integer> POSITION_TO_COORDINATE = initializePositionToCoordinateMap();

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

    public static int getCoordinateAtPosition(final String position) {
        return POSITION_TO_COORDINATE.get(position);
    }
    
    public static String getPositionAtCoordinate(final int coordinate){
        return ALGEBRIC_NOTATION[coordinate];
    }

    private static String[] initializeAlgebricNotation() {
        return new String[]{
            "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
            "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
            "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
            "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
            "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
            "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
            "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
            "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"
        };
    }

    private static Map<String, Integer> initializePositionToCoordinateMap() {
        final Map<String, Integer> positionToCoordinate = new HashMap<>();
        for (int i = 0; i < 64; i++) {
            positionToCoordinate.put(ALGEBRIC_NOTATION[i], i);
        }
        
        return Collections.unmodifiableMap(positionToCoordinate);
    }
}
