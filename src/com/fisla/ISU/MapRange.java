package com.fisla.ISU;

public class MapRange {
    public static double mapRange(double value, double startFirst, double endFirst, double startSecond, double endSecond) {
        return startSecond + ((value - startFirst)*(endSecond - startSecond))/(endFirst - startFirst);
    }
}
