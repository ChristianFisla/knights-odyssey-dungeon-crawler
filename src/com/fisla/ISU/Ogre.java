package com.fisla.ISU;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class Ogre extends Enemies {

    // Constructor
    public Ogre(int topLeftX, int topLeftY, double speed, int wanderFrequency, int aggressiveness, String type) {
        super(topLeftX, topLeftY, 16, 16);

        wanderFrequencyMod = wanderFrequency;
        agressivenessMod = aggressiveness;

        x = topLeftX;
        y = topLeftY;
        SPEED = speed;

        if (type.equalsIgnoreCase("normal")) {
            setImage("assets/characters/enemies/ogre.png");
            totalHealth = 500;
            health = totalHealth;
        }

        healthbar = new Healthbar(health);
    }

    // Desc: Handles chase patterns and movement
    // Param: n/a
    // Return: void
    public void update() {

        if (!chasing) {
            // On tick, the enemy has a 1 in wanderFrequency chance of wandering
            if (Start.getTick() % wanderFrequencyMod == 0) {
                wanderStartPauseTick = Start.getTick();
                wander();
                image = Rotate.rotateImage(ORIGINAL_IMAGE, -1 * theta);
                SPEED = 0.5;
            }
        } else {
            chase();
            if (Start.getTick() % agressivenessMod == 0) chaseStartTick = Start.getTick();

            image = Rotate.rotateImage(ORIGINAL_IMAGE, Math.toRadians(-1 * theta));

            if (Start.getTick() < chaseStartTick + 40) {
                SPEED = 2;
            } else {
                SPEED = 0.5;
            }
        }

        scale = 18 * Map.getScaleFactor();

        x = Map.getTopLeftPixelX() + (Map.getWidth() * leftSideRatio);
        y = Map.getTopLeftPixelY() + (Map.getHeight() * upSideRatio);

        if (chasing){
            calculateMovementIncrement();
        }

        // Calculates the new position of the enemy
        if (Start.getTick() > wanderStartPauseTick + 30 && !chasing) {
            calculateMovementIncrement();
        }

        // Updates the position of the enemy hitbox and healthbar
        updateHitBox(this);
        updateHealthbar(this);

        // Checks if the enemy is in range of the player and shows the healthbar if it is
        if (inRange || (health != totalHealth)) {
            healthbar.setShow(true);
        } else {
            healthbar.setShow(false);
        }
    }
    // Desc: Sets the ratios of the enemy's position based on the map size
    // Param: n/a
    // Return: void
    public void setRatios() {
        leftSideRatio = ((x - Map.getTopLeftPixelX()) / Map.getScaleFactor()) / 608.0;
        upSideRatio = ((y - Map.getTopLeftPixelY()) / Map.getScaleFactor()) / 608.0;
    }
    // Desc: Calculates the increment of the enemy's position
    // Param: n/a
    // Return: void
    public void calculateMovementIncrement() {

        double speed = SPEED * Map.getScaleFactor();

        x += (speed * Math.cos(theta));
        y += -1 * (speed * Math.sin(theta));

    }
    // Desc: Shifts the enemy based on map movement
    // Param: offset, and which vector to add it to
    // Return: void
    public void shift(int offSet, String xOrY) {
        if (xOrY.equalsIgnoreCase("x")) {
            x += offSet;
        } else if (xOrY.equalsIgnoreCase("y")) {
            y += offSet;
        }
    }
    // Desc: when the enemy is hit, it takes damage
    // Param: the weapon that hit it
    // Return: void
    public void hit(Weapon weapon) {
        if (weapon instanceof ThrowingAxe) {
            if (!firstHitAxe) {
                health -= weapon.getDamage();

                Enemies.getStatCounter().put("damage_dealt", Enemies.getStatCounter().get("damage_dealt") + weapon.getDamage());

                if (health <= 0) die();
            } else {

                health -= (weapon.getDamage() * 0.07);

                Enemies.getStatCounter().put("damage_dealt", Enemies.getStatCounter().get("damage_dealt") + (int)(weapon.getDamage() * 0.07));

                if (health <= 0) die();
            }
        }
    }
    public void draw(Graphics g) {
        g.drawImage(image, (int)x, (int)y, (int)scale, (int)scale, null);
    }
    public void drawHealthbar(Graphics g) {
        healthbar.draw(g);
    }
    public void wander() {
        theta = Math.toRadians((Math.random() * 365));
    }
    public void chase() {
        Player.findCenter();
        theta = Util.getTheta(x + scale / 2, y + scale / 2, Player.getCenterX() + 10 * Map.getScaleFactor(), Player.getCenterY() + 10 * Map.getScaleFactor());
    }
    public void die() {
        ogre.remove(this);
        statCounter.put("ogre", statCounter.get("ogre") + 1);
    }
    public void checkCollisions() {
        if (hitbox.collision(Player.getHitbox())) {
            Start.setIsDead(true);
        }
    }
    public void setImage(String dir) {
        try {
            image = ImageIO.read(new File(dir));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ORIGINAL_IMAGE = image;
    }

    // GETTERS AND SETTERS
    public double getScale() {
        return scale;
    }
    public Hitbox getHitbox() {
        return hitbox;
    }
    public void setFirstHitAxe(boolean set) {
        firstHitAxe = set;
    }
    public void setInRange(boolean set) {
        inRange = set;
    }
    public void setChasing(boolean set) {
        chasing = set;
    }
    public int getHealth() {
        return health;
    }
}