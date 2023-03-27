package com.fisla.ISU;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.io.File;

public class Player {

    private static BufferedImage player;

    private static Hitbox hitbox;

    private static BufferedImage[] movement = new BufferedImage[10];
    private static int movementImage;

    private static double x;
    private static double y;

    private static double centerX;
    private static double centerY;

    private static double dimensionX = 16;
    private static double dimensionY = 16;

    private static final double SCALE = 16.0;

    private static boolean startingAnimationComplete = false;

    private static double leftSideRatio;
    private static double upSideRatio;

    private static boolean keepPositionOnPlane = false;

    private static boolean isFiring = false;
    private static boolean invWeaponEquipped = false;
    private static Inventory inventory = new Inventory(2);

    private static int fireTick;

    // Constructor
    public Player(String dir) {

        hitbox = new Hitbox((int)x, (int)y, (int)dimensionX, (int)dimensionY);

        x = 284;
        y = -60;

        // Pre-load player images
        try {
            player = ImageIO.read(new File(dir + "knightright.png"));

            movement[0] = ImageIO.read(new File(dir + "knightup.png"));
            movement[1] = ImageIO.read(new File(dir + "knightleft.png"));
            movement[2] = ImageIO.read(new File(dir + "knightdown.png"));
            movement[3] = ImageIO.read(new File(dir + "knightright.png"));

            movement[4] = ImageIO.read(new File(dir + "knightupwalk1.png"));
            movement[5] = ImageIO.read(new File(dir + "knightleftwalk.png"));
            movement[6] = ImageIO.read(new File(dir + "knightdownwalk1.png"));
            movement[7] = ImageIO.read(new File(dir + "knightrightwalk.png"));

            movement[8] = ImageIO.read(new File(dir + "knightdownwalk2.png"));
            movement[9] = ImageIO.read(new File(dir + "knightupwalk2.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    // Desc: Updates important variables like the player's position, hitbox, and center
    // Param: n/a
    // Return: void
    public static void update() {

        findCenter();

        if (keepPositionOnPlane) {
            x = Map.getTopLeftPixelX() + (Map.getWidth() * leftSideRatio);
            y = Map.getTopLeftPixelY() + (Map.getHeight() * upSideRatio);
        }

        if (Player.getInventory().getInventory().size() == 0) {
            invWeaponEquipped = false;
        } else {
            invWeaponEquipped = true;
        }

        Weapon invWeapon = null;

        if (invWeaponEquipped) invWeapon = Player.getInventory().getInventory().get(0);
        if (invWeapon == null && ThrowingAxe.getThrowingAxes().size() > 0) {
            invWeapon = ThrowingAxe.getThrowingAxes().get(0);
            invWeapon.update();
        }

        if (isFiring && invWeaponEquipped) invWeapon.update();

        dimensionX = Map.getScaleFactor() * SCALE;
        dimensionY = Map.getScaleFactor() * SCALE;

        hitbox.updatePlayerHitbox();

        Enemies.checkDistance();
    }
    // Desc: Adds a weapon to the player's inventory
    // Param: n/a
    // Return: void
    public static void addWeapon(String weapon) {
        try {
            if (weapon.equals("throwing_axe")) inventory.add(new ThrowingAxe());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Desc: Fires the player's current weapon
    // Param: n/a
    // Return: void
    public static void fire() {

        Enemies.getStatCounter().put("axe_thrown", Enemies.getStatCounter().get("axe_thrown") + 1);

        findCenter();

        Weapon invWeapon;

        if (invWeaponEquipped) {
            invWeapon = Player.getInventory().getInventory().get(0);
            invWeapon.setTheta(Util.getTheta(centerX, centerY, Mouse.getFirePoint().x, Mouse.getFirePoint().y));
            invWeapon.fire();
        }

        fireTick = Start.getTick();
        isFiring = true;

    }
    // Desc: The starting animation
    // Param: n/a
    // Return: void
    public static void startAnimation() {
        if (y < 296) {
            Start.setPlayerAnimate(true);
            y += 4;
        }
        if (y == 296) Start.setPlayerAnimate(false);
    }
    // Desc: Inilializes the player's position on the plane
    // Param: n/a
    // Return: void
    public static void stayInPlace() {

        leftSideRatio = ((x - Map.getTopLeftPixelX()) / Map.getScaleFactor()) / 608.0;
        upSideRatio = ((y - Map.getTopLeftPixelY()) / Map.getScaleFactor()) / 608.0;

        Start.setBlockInput(true);

    }
    // Desc: Finds the center of the player
    // Param: n/a
    // Return: void
    public static void findCenter() {

        centerX = x + (dimensionX * 0.5);
        centerY = y + (dimensionY * 0.5);

    }

    // Desc: Creates walking animation for a w key press
    // Param: n/a
    // Return: void
    public static void wKeyPress() {
        if (Start.getTick() % 5 == 0) {
            if (Player.getMovementImage() == 4) {
                Player.setMovementImage(9);
            } else {
                Player.setMovementImage(4);
            }
        }
    }

    // Desc: Creates walking animation for an a key press
    // Param: n/a
    // Return: void
    public static void aKeyPress() {
        if (Start.getTick() % 5 == 0) {
            if (Player.getMovementImage() == 5) {
                Player.setMovementImage(1);
            } else {
                Player.setMovementImage(5);
            }
        }
    }
    // Desc: Creates walking animation for a s key press
    // Param: n/a
    // Return: void
    public static void sKeyPress() {
        if (Start.getTick() % 5 == 0) {
            if (Player.getMovementImage() == 8) {
                Player.setMovementImage(6);
            } else {
                Player.setMovementImage(8);
            }
        }
    }
    // Desc: Creates a walking animation for a d key press
    // Param: n/a
    // Return: void
    public static void dKeyPress() {
        if (Start.getTick() % 5 == 0) {
            if (Player.getMovementImage() == 7) {
                Player.setMovementImage(3);
            } else {
                Player.setMovementImage(7);
            }
        }
    }

    // GETTERS AND SETTERS
    public static Image getImage() {
        return player;
    }
    public static double getX() {
        return x;
    }
    public static double getY(){
        return y;
    }
    public static double getCenterX() {
        return centerX;
    }
    public static double getCenterY() {
        return centerY;
    }
    public static double getDimensionX() {
        return dimensionX;
    }
    public static double getDimensionY() {
        return dimensionY;
    }
    public static boolean getStartingAnimationComplete() {
        return startingAnimationComplete;
    }
    public static int getMovementImage() {
        return movementImage;
    }
    public static BufferedImage[] getMovementImages() {
        return movement;
    }
    public static void setMovementImage(int set) {
        player = movement[set];
        movementImage = set;
    }
    public static void setStartingAnimationComplete(boolean set) {
        startingAnimationComplete = set;
    }
    public static void setKeepPositionOnPlane(boolean set) {
        Player.keepPositionOnPlane = set;
    }
    public static void setIsFiring(boolean set){
        isFiring = set;
    }
    public static void setX(double set) {
        x = set;
    }
    public static void setY(double set) {
        y = set;
    }
    public static boolean getIsFiring() {
        return isFiring;
    }
    public static boolean getKeepPositionOnPlane() {
        return keepPositionOnPlane;
    }
    public static Inventory getInventory() {
        return inventory;
    }
    public static Hitbox getHitbox() {
        return hitbox;
    }
    public static boolean getInvWeaponEquipped() {
        return invWeaponEquipped;
    }
    public static int getFireTick() {
        return fireTick;
    }
    public static void skipDialogue() {
        Dialogue.inrCurrentText();
    }
}