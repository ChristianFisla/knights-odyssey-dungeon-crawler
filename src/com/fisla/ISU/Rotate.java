package com.fisla.ISU;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

public class Rotate implements ImageObserver {

    public static double getSizeConstant(double angle) {
        return 0.207107 * Math.cos(Math.toRadians(4 * angle) + Math.PI) + 1.207107;
    }

    public static BufferedImage rotateImage(BufferedImage buffImage, double radian) {

        double sin = Math.abs(Math.sin(radian));
        double cos = Math.abs(Math.cos(radian));

        int width = buffImage.getWidth();
        int height = buffImage.getHeight();

        int nWidth = (int) Math.floor((double) width * cos + (double) height * sin);
        int nHeight = (int) Math.floor((double) height * cos + (double) width * sin);

        BufferedImage rotatedImage = new BufferedImage(nWidth, nHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = rotatedImage.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        graphics.translate((nWidth - width) / 2, (nHeight - height) / 2);
        // rotation around the center point
        graphics.rotate(radian, (double) (width / 2), (double) (height / 2));
        graphics.drawImage(buffImage, 0, 0, null);
        graphics.dispose();

        return rotatedImage;
    }

    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
// TODO Auto-generated method stub
        return false;
    }
}