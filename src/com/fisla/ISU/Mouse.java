package com.fisla.ISU;

import java.awt.*;

public class Mouse {

    private static Image cursor;
    private static Point point;

    private static Point firePoint;

    public static void update() {
        setPoint(Start.getFrame().getMousePosition());
    }
    public static Image getImage() {
        return cursor;
    }
    public static void setImage(String dir) {
        cursor = Toolkit.getDefaultToolkit().getImage(dir);
        Start.getFrame().setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(0, 0), "custom cursor"));
    }
    public static Point getPoint() {
        return point;
    }
    public static void setPoint(Point p) {
        point = p;
    }
    public static Point getFirePoint() {
        return firePoint;
    }
    public static void setFirePoint(Point p) {
        firePoint = p;
    }
}