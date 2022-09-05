package com.jpro.pathfinding;

public class Dimensions {
    public static int maxRow = 25;
    public static int maxCol = 51;

    public static int getMaxRow() {
        return maxRow;
    }

    public static int getMaxCol() {
        return maxCol;
    }

    private static void setMaxRow(int maxRow) {
        Dimensions.maxRow = maxRow;
    }

    private static void setMaxCol(int maxCol) {
        Dimensions.maxCol = maxCol;
    }

    public static void setGrid(int setting) {
        if (setting == 1) {
            setMaxRow(5);
            setMaxCol(11);
        } else if (setting == 2) {
            setMaxRow(11);
            setMaxCol(25);
        } else if (setting == 3) {
            setMaxRow(17);
            setMaxCol(35);
        } else if (setting == 4) {
            setMaxRow(25);
            setMaxCol(51);
        } else if (setting == 5) {
            setMaxRow(35);
            setMaxCol(71);
        }
    }
}
