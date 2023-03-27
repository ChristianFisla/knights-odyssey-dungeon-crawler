package com.fisla.ISU;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;

public class ThrowingAxe extends Weapon {

    private static ArrayList<ThrowingAxe> throwingAxes = new ArrayList<>();

    private int damage = 30;
    private int range = 20;

    private double leftSideRatio;
    private double upSideRatio;

    private int rotateSpeed = 1;

    // Boolean values for when the axe is popping out of a chest or not
    private boolean popping = false;
    private boolean finishedPopping = false;

    // The speed of the axe
    private double SPEED;
    private double speedFactor = 4;

    // Direction of the axes trajectory
    private double theta;

    // If the axe is in the air or not
    private boolean inBound = false;
    private boolean stayInPlace = false;

    private double x = Player.getX();
    private double y = Player.getY();

    private double popX;
    private double popY;

    // Which chest the axe came out of
    private Chest popChest;

    private BufferedImage image = ImageIO.read(new File("assets/items/axe/axerotate_0.png"));

    // The animation frams of the spinning motion
    private int currentFrame = 0;
    private BufferedImage[] animationFrames = new BufferedImage[16];

    // Creates the hitbox of the axe
    private Hitbox hitbox = new Hitbox((int)x, (int)y, (int)(Player.getDimensionX() * 0.75), (int)(Player.getDimensionY() * 0.75));

    // Constructs a new object and pre-loads the animation frames
    public ThrowingAxe() throws IOException {
        for (int i = 0; i <= 15; i++) {
            animationFrames[i] = ImageIO.read(new File("assets/items/axe/axerotate_" + i + ".png"));
        }
    }

    // Desc: updates x and y values as well as important values when thrown and popped out of a chest
    // Param: n/a
    // Return: void
    public void update() {

        // If the axe is popping out of a chest
        if (popping) {
            // Keep moving it down
            if (y < popY && !finishedPopping) {
                updateAxeIncrementPop();
            } else {
                // After that, stay in place
                if (!finishedPopping) {
                    leftSideRatio = ((x - Map.getTopLeftPixelX()) / Map.getScaleFactor()) / 608.0;
                    upSideRatio = ((y - Map.getTopLeftPixelY()) / Map.getScaleFactor()) / 608.0;
                }
                finishedPopping = true;
                x = Map.getTopLeftPixelX() + (Map.getWidth() * leftSideRatio);
                y = Map.getTopLeftPixelY() + (Map.getHeight() * upSideRatio);
            }

            // If the axe is being thrown
        } else if (Player.getIsFiring()){
            // Update increments and speed
            updateSpeedFactor();
            updateAxeIcrement();

            incrementThrowAnimation();
        }

        // Check for collisions and update the hitbox
        hitbox.updateWeaponHitbox(this);
        checkCollisions();

    }

    // Desc: determines the x and y vectors given an angle and speed when popping
    // Param: n/a
    // Return: void
    public void updateAxeIncrementPop() {
        SPEED = 4 * Map.getScaleFactor();

        double thetaInRadians = Math.toRadians(theta);

        x += (SPEED * Math.cos(thetaInRadians));
        y += -1 * (SPEED * Math.sin(thetaInRadians));
    }

    // Desc: increments the animation frame
    // Param: n/a
    // Return: void
    public void incrementThrowAnimation() {

        if (currentFrame != 15) {
            currentFrame += rotateSpeed;
        } else {
            currentFrame = 0;
        }

        image = animationFrames[currentFrame];
    }

    // Desc: determines the x and y vectors given an angle and speed when thrown
    // Param: n/a
    // Return: void
    public void updateAxeIcrement() {
        SPEED = speedFactor * Map.getScaleFactor();

        double thetaInRadians = Math.toRadians(theta);

        x += (SPEED * Math.cos(thetaInRadians));
        y += -1 * (SPEED * Math.sin(thetaInRadians));
    }

    // Desc: updates the speed depending on the scale of the map
    // Param: n/a
    // Return: void
    public void updateSpeedFactor() {

        // The tick the axe was thrown
        int currentTick = Start.getTick();
        int fireTick = Player.getFireTick();

        // Wait 20 ticks before coming back
        if (currentTick == fireTick + 20) this.inBound = true;

        // Ease in ease out
        if (currentTick >= fireTick + 5 && currentTick <= fireTick + 11) {
            this.speedFactor = 3;
        } else if (currentTick >= fireTick + 12 && currentTick <= fireTick + 16) {
            this.speedFactor = 2;
        } else if (currentTick >= fireTick + 17 && currentTick <= fireTick + 24) {
            this.speedFactor = 1;
        } else if (currentTick >= fireTick + 25 && currentTick <= fireTick + 29) {
            this.speedFactor = 2;
        } else if (currentTick >= fireTick + 29 && currentTick <= fireTick + 35) {
            this.speedFactor = 3;
        } else {
            this.speedFactor = 4;
        }

        // When coming back, always aim for the player
        if (this.inBound) this.setTheta(Util.getTheta(x, y, Player.getCenterX() + 10 * Map.getScaleFactor(), Player.getCenterY() + 10 * Map.getScaleFactor()));

    }

    // Desc: checks for collisions with the axe
    // Param: n/a
    // Return: void
    public void checkCollisions() {

        // Loop through all the enemies
        for (int i = 0; i < Enemies.getSpiders().size(); i++) {
            Spider spider = Enemies.getSpiders().get(i);

            // If the axe is colliding with the spider
            if (hitbox.collision(spider.getHitbox())) {
                spider.hit(this);
                spider.setFirstHitAxe(true);
            } else {
                spider.setFirstHitAxe(false);
            }
        }

        for (int i = 0; i < Enemies.getOgres().size(); i++) {
            Ogre ogre = Enemies.getOgres().get(i);

            // If the axe is colliding with the ogre
            if (hitbox.collision(ogre.getHitbox())) {
                ogre.hit(this);
                ogre.setFirstHitAxe(true);
            } else {
                ogre.setFirstHitAxe(false);
            }
        }

        // If the axe is colliding with the player
        if ((Player.getHitbox().collision(this.hitbox) && this.inBound) || (Player.getHitbox().collision(this.hitbox) && this.popping)) {

            // If the axe was popped out of a chest
            if (popping) {
                Player.addWeapon(this.toString());
                ThrowingAxe.getThrowingAxes().remove(this);
                popping = false;

                // Start monologue
                Start.setMonologue(true);
                Dialogue.setCurrentText(0);
                Dialogue.setY(616);
                Dialogue.getStartingTickAnimation();
                Dialogue.setAnimate(true);

                Start.setZoomingIn(true);
            }

            this.inBound = false;
            Player.setIsFiring(false);
        }
    }

    // Desc: Shifts the axe when the map moves
    // Param: the offset and the x or y vector
    // Return: void
    public void shiftEntity(int offSet, String xOrY) {
        if (xOrY.equalsIgnoreCase("x")) {
            x += offSet;
        } else if (xOrY.equalsIgnoreCase("y")) {
            y += offSet;

            if (offSet < 0) {
                popY += offSet;
            }
        }
    }

    // Desc: Sets the initial position of the axe
    // Param: n/a
    // Return: void
    public void fire() {
        x = (int) Player.getX();
        y = (int) Player.getY();
    }
    // Desc: Sets the initial position of the axe when popping out of a chest
    // Param: The chest it came out of
    // Return: void
    public void pop(Chest chest) {
        popping = true;

        calculatePositionOffChest(chest);

        x = chest.getX();
        y = chest.getY();

        theta = Util.getTheta(x, y, popX, popY);
    }
    // Desc: Calculates the position of the axe when popping out of a chest, this is random
    // Param: The chest it came out of
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
    public void setInBound(boolean set) {
        inBound = set;
    }
    public int getDamage() {
        return damage;
    }
    public int getRotateSpeed() {
        return rotateSpeed;
    }
    public boolean getInBound() {
        return inBound;
    }
    public double getSpeed() {
        return SPEED;
    }
    public void setSpeedFactor(double set) {
        speedFactor = set;
    }
    public double getSpeedFactor() {
        return speedFactor;
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
    public int getCurrentFrame() {
        return currentFrame;
    }
    public Image[] getAnimationFrames() {
        return animationFrames;
    }
    public static ArrayList<ThrowingAxe> getThrowingAxes() {
        return throwingAxes;
    }
    public ArrayList<ThrowingAxe> getThrowingAxesInstance() {
        return throwingAxes;
    }
    public String toString() {
        return "throwing_axe";
    }
}