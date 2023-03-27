package com.fisla.ISU;

public class Util {

    /*
             (CLICK x2, y2)
               |\
               |  \
               |    \
     Vertical  |      \  Hypotenuse
               |        \
               |          \
               |      Theta \
               _______________  (PLAYER x1, y1)
     */
    public static double getTheta(double x1, double y1, double x2, double y2) {

        y2 -= 20;
        x2 -= 10;

        double radians = Math.atan2(y2 - y1, x2 - x1);

        double degrees = Math.toDegrees(radians);

        if (degrees < 0) {
            degrees += 360;
        }

        degrees = 360 - degrees;

        return degrees;
    }
    public static double calculateDistanceBetweenPoints(double x1, double y1, double x2, double y2) {
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }
}
