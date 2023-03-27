package com.fisla.ISU;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;

public class Coin {

    private static ArrayList<Coin> coins = new ArrayList<>();

    private double leftSideRatio;
    private double upSideRatio;

    private boolean popping = false;
    private boolean finishedPopping = false;

    private double x;
    private double y;

    private double popX;
    private double popY;

    private double theta;
    private double SPEED;

    private Chest popChest;

    private BufferedImage image = ImageIO.read(new File("assets/items/objects/coin/coin.png"));

    private Hitbox hitbox = new Hitbox((int)x, (int)y, (int)(Player.getDimensionX() * 0.75), (int)(Player.getDimensionY() * 0.75));

    // Constructor
    public Coin() throws IOException {
    }

    // Desc: updates the important variables
    // Param: n/a
    // Return: void
    public void update() {

        // If the coin is popping, update the position
        if (popping) {
            if (y < popY && !finishedPopping) {
                updateAxeIncrementPop();
            } else {
                // If the coin has reached the final destination, stop popping
                if (!finishedPopping) {
                    leftSideRatio = ((x - Map.getTopLeftPixelX()) / Map.getScaleFactor()) / 608.0;
                    upSideRatio = ((y - Map.getTopLeftPixelY()) / Map.getScaleFactor()) / 608.0;
                }
                finishedPopping = true;
                x = Map.getTopLeftPixelX() + (Map.getWidth() * leftSideRatio);
                y = Map.getTopLeftPixelY() + (Map.getHeight() * upSideRatio);
            }

        }

        hitbox.updateCoinHitBox(this);
        checkCollisions();

    }

    // Desc: Update the position of the coin when it is popping
    // Param: n/a
    // Return: void
    public void updateAxeIncrementPop() {
        SPEED = 4 * Map.getScaleFactor();

        double thetaInRadians = Math.toRadians(theta);

        x += (SPEED * Math.cos(thetaInRadians));
        y += -1 * (SPEED * Math.sin(thetaInRadians));
    }

    // Desc: Check the collisions of the coin
    // Param: n/a
    // Return: void
    public void checkCollisions() {

        if (Player.getHitbox().collision(this.hitbox)) {
            Start.addScore();
            popping = false;
            Coin.getCoins().remove(this);
        }
    }

    // Desc: set the initial variables for the coin to pop
    // Param: Chest that it will pop from
    // Return: void
    public void pop(Chest chest) {
        popping = true;

        calculatePositionOffChest(chest);

        x = chest.getX();
        y = chest.getY();

        theta = Util.getTheta(x, y, popX, popY);
    }
    // Desc: Calculate the position of the coin when it pops and where it needs to go
    // Param: Chest that it will pop from
    // Return: void
    public void calculatePositionOffChest(Chest chest) {
        popX = (int)(chest.getX() + Math.random() * chest.getScale());
        popY = (int)(chest.getY() + chest.getScale());

        popChest = chest;
    }

    // GETTERS AND SETTERS
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public void setX(double set) {
        x = set;
    }
    public void setY(double set) {
        y = set;
    }
    public void setSpeed(int set) {
        SPEED = set;
    }
    public Image getImage() {
        return image;
    }
    public void setTheta(double set) {
        theta = set;
    }
    public double getTheta() {
        return theta;
    }
    public Hitbox getHitbox() {
        return hitbox;
    }
    public void setPopping(boolean set) {
        popping = set;
    }
    public boolean getPopping() {
        return popping;
    }
    public static ArrayList<Coin> getCoins() {
        return coins;
    }
    public String toString() {
        return "coin";
    }
}
