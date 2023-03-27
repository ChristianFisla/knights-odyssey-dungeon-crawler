package com.fisla.ISU;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class Map {

    // Image and details about the maps scale
    private static int level = 0;
    private static BufferedImage map;
    private static double scaleFactor = 1;
    private static double scaleFactorDesired = 2.5;
    private static int zoomPhase = 0;

    // Width and height of the map
    private static double width = 608;
    private static double height = 608;
    private static double focalX = 0;
    private static double focalY = 0;

    private static final int WIDTH_CONSTANT = 608;
    private static final int HEIGHT_CONSTANT = 608;

    private static double topLeftPixelX = 0;
    private static double topLeftPixelY = 0;

    private static double leftSideRatio;
    private static double upSideRatio;

    private static boolean startingAnimationComplete = false;

    private static String zoomType = null;

    // Denotes which way the map is shifting
    private static boolean mapShiftLeft;
    private static boolean mapShiftRight;
    private static boolean mapShiftUp;
    private static boolean mapShiftDown;

    // Denotes which way the map is blocked from moving
    private static boolean mapShiftLeftCollision = true;
    private static boolean mapShiftRightCollision = true;
    private static boolean mapShiftUpCollision = true;
    private static boolean mapShiftDownCollision = true;

    // All the boundaries on the map
    private static ArrayList<BoundBlock> bounds = new ArrayList<>();

    // All the interactable objects on the map
    private static ArrayList<Npc> npc = new ArrayList<>();
    private static ArrayList<Chest> chest = new ArrayList<>();

    // The values of zoom when holding down q or e
    private static double[] zoomCycle = {0.01, 0.01, 0.02, 0.02, 0.02, 0.03, 0.03, 0.04, 0.05};

    // Constructor
    public Map(String dir, int level) {
        Map.level = level;
        setMap(dir);

        if (level == 1) {

            // Outer boundaries
            bounds.add(new BoundBlock(0, -80, 608, 24));
            bounds.add(new BoundBlock(-80, 0, 16, 608));
            bounds.add(new BoundBlock(0, 585, 608,670));
            bounds.add(new BoundBlock(592, 0, 670, 608));

            // Leftside bound blocks
            bounds.add(new BoundBlock(64, 169, 112, 304));
            bounds.add(new BoundBlock(112, 228, 160, 368));

            bounds.add(new BoundBlock(224, 17, 288, 87));
            bounds.add(new BoundBlock(257, 80, 288, 128));
            bounds.add(new BoundBlock(80, 48, 288, 87));
            bounds.add(new BoundBlock(144, 48, 288, 112));

            bounds.add(new BoundBlock(368, 16, 480, 80));
            bounds.add(new BoundBlock(448, 16, 480, 128));

            bounds.add(new BoundBlock(328, 154, 495, 208));
            bounds.add(new BoundBlock(224, 168, 384, 240));
            bounds.add(new BoundBlock(304, 168, 384, 272));
            bounds.add(new BoundBlock(328, 248, 582, 320));

            bounds.add(new BoundBlock(48, 454, 192, 528));
            bounds.add(new BoundBlock(96, 438, 144, 496));
            bounds.add(new BoundBlock(112, 502, 160, 544));

            bounds.add(new BoundBlock(240, 374, 352, 448));
            bounds.add(new BoundBlock(304, 408, 368, 480));
            bounds.add(new BoundBlock(320, 454, 368, 512));

            bounds.add(new BoundBlock(497, 390, 544, 528));
            bounds.add(new BoundBlock(400, 424, 544, 496));
            bounds.add(new BoundBlock(480, 454, 582, 528));
            bounds.add(new BoundBlock(544, 454, 582, 582));

            // NPC Bound
            bounds.add(new BoundBlock(320, 90, 336, 106));

            // Add NPC
            npc.add(new Npc(320, 90, 10,"assets/characters/NPC/npc1.png"));

            // Add Chests
            chest.add(new Chest(320, 30, "assets/items/objects/chest/chest_0.png", "throwing_axe"));

            chest.add(new Chest(40, 30, "assets/items/objects/chest/chest_0.png", "coin"));
            chest.add(new Chest(70, 312, "assets/items/objects/chest/chest_0.png", "coin"));
            chest.add(new Chest(270, 460, "assets/items/objects/chest/chest_0.png", "coin"));

            chest.add(new Chest(85, 550, "assets/items/objects/chest/chest_0.png", "coin"));
            chest.add(new Chest(533, 216, "assets/items/objects/chest/chest_0.png", "coin"));
            chest.add(new Chest(566, 335, "assets/items/objects/chest/chest_0.png", "coin"));
            chest.add(new Chest(397, 500, "assets/items/objects/chest/chest_0.png", "coin"));
            chest.add(new Chest(282, 260, "assets/items/objects/chest/chest_0.png", "coin"));

        }
    }
    // Desc: Updates the boundaries when moved
    // Param: n/a
    // Return: void
    public static void updateBounds() {
        int count = 0;

        for (BoundBlock bound : bounds) {
            if (bound.update(Start.getPlayer())) count++;
        }

        if (count == 0) {
            mapShiftLeftCollision = true;
            mapShiftRightCollision = true;
            mapShiftUpCollision = true;
            mapShiftDownCollision = true;
        }

    }
    // Desc: Updates the perspective and top left pixel that needs to be drawn when zoomed or moved
    // Param: n/a
    // Return: void
    public static void updateMapPerspective() {

        leftSideRatio = Math.abs(focalX - topLeftPixelX) / width;
        upSideRatio = Math.abs(focalY - topLeftPixelY) / height;

        width = (WIDTH_CONSTANT * scaleFactor);
        height = (HEIGHT_CONSTANT * scaleFactor);

        topLeftPixelX = (focalX - (width * leftSideRatio));
        topLeftPixelY = (focalY - (height * upSideRatio));

    }
    public static void zoomIn() {
        Start.setZoomingIn(true);
    }
    public static void zoomOut() {
        Start.setZoomingOut(true);
    }
    // Desc: Updates the map when zooming in or out
    // Param: n/a
    // Return: void
    public static void zoomOnTick() {

        // Zoom in regular
        if (!Player.getKeepPositionOnPlane()) {
            if (Start.getZoomingIn() && !Start.getZoomAnimate() && !Start.getPlayerAnimate()) {
                if (scaleFactor <= 2.5) increaseScale(zoomCycle[zoomPhase]);
                if (zoomPhase != zoomCycle.length - 1) zoomPhase++;
                focalX = 304;
                focalY = 304;
                updateMapPerspective();
            } else if (Start.getZoomingOut() && !Start.getZoomAnimate() && !Start.getPlayerAnimate()) {
                if (scaleFactor >= 1.004) decreaseScale(zoomCycle[zoomPhase]);
                if (zoomPhase != zoomCycle.length - 1) zoomPhase++;
                focalX = 304;
                focalY = 304;
                updateMapPerspective();
            } else {
                zoomPhase = 0;
            }

            // Zoom in keep position
        } else if (Player.getKeepPositionOnPlane() || Start.getMonologue()) {

            if (Start.getZoomingIn() && !Start.getZoomAnimate() && !Start.getPlayerAnimate()) {
                if (scaleFactor <= 4) increaseScale(zoomCycle[zoomPhase]);
                if (zoomPhase != zoomCycle.length - 1) zoomPhase++;
                focalX = 304;
                focalY = 304;
                updateMapPerspective();
            } else if (Start.getZoomingOut() && !Start.getZoomAnimate() && !Start.getPlayerAnimate()) {
                if (scaleFactor >= 1.004) decreaseScale(zoomCycle[zoomPhase]);
                if (zoomPhase != zoomCycle.length - 1) zoomPhase++;
                focalX = 304;
                focalY = 304;
                updateMapPerspective();
            } else {
                zoomPhase = 0;
            }

        }
    }
    // Desc: Zooms in on a point
    // Param: n/a
    // Return: void
    public static void startingZoom(Graphics g) {

        g.drawImage(map, 0, 0, (int)(608 * scaleFactor), (int)(608 * scaleFactor), null);

        focalX = 605;
        focalY = 10;

        createZoomAnimation((int)focalX, (int)focalY, 2.5, "starting");
    }
    // Desc: Zooms in on a point
    // Param: n/a
    // Return: void
    public static void cutsceneZoomOnTick() {
        if (scaleFactor <= scaleFactorDesired) {
            zoomAnimation((int)focalX, (int)focalY);
        } else {
            Start.setZoomAnimate(false);
            if (zoomType.equalsIgnoreCase("starting")) startingAnimationComplete = true;
            Player.update();
        }
    }
    // Desc: Creates a zoom animation at a given point and desired scale factor
    // Param: n/a
    // Return: void
    public static void createZoomAnimation(int focalX, int focalY, double scaleFactorDesired, String type) {

        Map.focalX = focalX;
        Map.focalY = focalY;

        Map.scaleFactorDesired = scaleFactorDesired;

        if (type.equalsIgnoreCase("starting")) {
            Start.setZoomAnimate(true);
            zoomType = "starting";
        } else if (type.equalsIgnoreCase("interaction")) {
            Start.setZoomAnimate(true);
            zoomType = "interaction";
        }

    }
    // Desc: Adjust the map based on the scale and perspective
    // Param: n/a
    // Return: void
    public static void zoomAnimation(int focalX, int focalY) {

        leftSideRatio = focalX / 608.0;
        upSideRatio = focalY / 608.0;

        Map.scaleFactor += zoomCycle[zoomPhase];
        if (zoomPhase != zoomCycle.length - 1) inrZoomPhase();

        width = (int) (WIDTH_CONSTANT * scaleFactor);
        height = (int) (HEIGHT_CONSTANT * scaleFactor);

        topLeftPixelX = focalX - (int) (width * leftSideRatio);
        topLeftPixelY = focalY - (int) (height * upSideRatio);

    }
    // Desc: Updates the map when moving
    // Param: n/a
    // Return: void
    public static void updateMapMovementOnTick() {

        // Check if the player has a weapon that ALSO needs to be shifted
        Weapon weapon = null;
        boolean weaponIsDefined = false;

        if (Player.getInvWeaponEquipped()) weapon = Player.getInventory().getInventory().get(0);
        if (weapon == null && ThrowingAxe.getThrowingAxes().size() > 0) weapon = ThrowingAxe.getThrowingAxes().get(0);
        if (weapon != null) weaponIsDefined = true;

        // Move the map
        if (mapShiftLeft && mapShiftLeftCollision) {
            Map.changeTopLeftPixelX((int)-(2 * Map.getScaleFactor()));
            if (weaponIsDefined) if (Player.getInvWeaponEquipped() || weapon.getThrowingAxesInstance().size() > 0) weapon.shiftEntity((int)-(2 * Map.getScaleFactor()), "x");
            Enemies.shiftAllEntities((int)-(2 * Map.getScaleFactor()), "x");
        }
        if (mapShiftRight && mapShiftRightCollision) {
            Map.changeTopLeftPixelX((int)(2 * Map.getScaleFactor()));
            if (weaponIsDefined) if (Player.getInvWeaponEquipped() || weapon.getThrowingAxesInstance().size() > 0) weapon.shiftEntity((int)(2 * Map.getScaleFactor()), "x");
            Enemies.shiftAllEntities((int)(2 * Map.getScaleFactor()), "x");
        }
        if (mapShiftUp && mapShiftUpCollision) {
            Map.changeTopLeftPixelY((int)-(2 * Map.getScaleFactor()));
            if (weaponIsDefined) if (Player.getInvWeaponEquipped() || weapon.getThrowingAxesInstance().size() > 0) weapon.shiftEntity((int)-(2 * Map.getScaleFactor()), "y");
            Enemies.shiftAllEntities((int)-(2 * Map.getScaleFactor()), "y");
        }
        if (mapShiftDown && mapShiftDownCollision) {
            Map.changeTopLeftPixelY((int)(2 * Map.getScaleFactor()));
            if (weaponIsDefined) if (Player.getInvWeaponEquipped() || weapon.getThrowingAxesInstance().size() > 0) weapon.shiftEntity((int)(2 * Map.getScaleFactor()), "y");
            Enemies.shiftAllEntities((int)(2 * Map.getScaleFactor()), "y");
        }
    }

    // GETTERS AND SETTERS
    public static void setShiftLeft(boolean set) {
        mapShiftLeft = set;
    }
    public static void setShiftRight(boolean set) {
        mapShiftRight = set;
    }
    public static void setShiftUp(boolean set) {
        mapShiftUp = set;
    }
    public static void setShiftDown(boolean set) {
        mapShiftDown = set;
    }
    public static boolean getShiftLeft() {
        return mapShiftLeft;
    }
    public static boolean getShiftRight() {
        return mapShiftRight;
    }
    public static boolean getShiftUp() {
        return mapShiftUp;
    }
    public static boolean getShiftDown() {
        return mapShiftDown;
    }
    public static void setShiftLeftCollision(boolean set) {
        mapShiftLeftCollision = set;
    }
    public static void setShiftRightCollision(boolean set) {
        mapShiftRightCollision = set;
    }
    public static void setShiftUpCollision(boolean set) {
        mapShiftUpCollision = set;
    }
    public static void setShiftDownCollision(boolean set) {
        mapShiftDownCollision = set;
    }
    public static boolean getShiftLeftCollision() {
        return mapShiftLeftCollision;
    }
    public static boolean getShiftRightCollision() {
        return mapShiftRightCollision;
    }
    public static boolean getShiftUpCollision() {
        return mapShiftUpCollision;
    }
    public static boolean getShiftDownCollision() {
        return mapShiftDownCollision;
    }
    public static boolean getStartingAnimationComplete() {
        return startingAnimationComplete;
    }
    public static void increaseScale(double change) {
        scaleFactor += change;
    }
    public static void decreaseScale(double change) {
        scaleFactor -= change;
    }
    public static int getZoomPhase() {
        return zoomPhase;
    }
    public static void setZoomPhase(int set) {
        zoomPhase = set;
    }
    public static void inrZoomPhase() {
        zoomPhase++;
    }
    public static double[] getZoomCycle() {
        return zoomCycle;
    }
    public static double getScaleFactor() {
        return scaleFactor;
    }
    public static void setScaleFactor(double set) {
        scaleFactor = set;
    }
    public static double getTopLeftPixelX() {
        return topLeftPixelX;
    }
    public static double getTopLeftPixelY() {
        return topLeftPixelY;
    }
    public static double getFocalX() {
        return focalX;
    }
    public static void setFocalX(int set) {
        focalX = set;
    }
    public static double getFocalY() {
        return focalY;
    }
    public static void setFocalY(int set) {
        focalY = set;
    }
    public static double getWidth() {
        return width;
    }
    public static double getHeight() {
        return height;
    }
    public static double getScaleFactorDesired() {
        return scaleFactorDesired;
    }
    public static void changeTopLeftPixelX(int change) {
        topLeftPixelX += change;
    }
    public static void changeTopLeftPixelY(int change) {
        topLeftPixelY += change;
    }
    public static void setMap(String dir) {
        try {
            map = ImageIO.read(new File(dir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Image getMap() {
        return map;
    }
    public static ArrayList<Npc> getNpc() {
        return npc;
    }
    public static ArrayList<Chest> getChest() {
        return chest;
    }
    public static ArrayList<BoundBlock> getBounds() {
        return bounds;
    }
}