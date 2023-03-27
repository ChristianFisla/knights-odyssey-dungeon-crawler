package com.fisla.ISU;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.TreeMap;

public class Enemies {

    protected BufferedImage ORIGINAL_IMAGE;
    protected BufferedImage image;

    // Direction and scale of the enemy
    protected double scale;
    protected double theta;

    protected double x;
    protected double y;

    // Hitbox
    protected Hitbox hitbox;

    protected double leftSideRatio;
    protected double upSideRatio;

    // The starting ticks of any wander/chase cycle
    protected int wanderStartPauseTick;
    protected int chaseStartTick;

    protected double SPEED;

    // Boolean values to determine if the enemy is chasing or wandering
    protected boolean inRange = false;
    protected boolean chasing = false;

    // How often the enemy will leap towards the player
    protected int agressivenessMod;

    protected int totalHealth;
    protected int health;
    protected Healthbar healthbar;

    protected boolean firstHitAxe = false;

    protected int wanderFrequencyMod;

    // Static ArrayLists for all enemies
    protected static ArrayList<Spider> spider = new ArrayList<>();
//    protected static ArrayList<Spirit> spirit = new ArrayList<>();
    protected static ArrayList<Ogre> ogre = new ArrayList<>();

    // Create stat counter
    protected static TreeMap<String, Integer> statCounter = new TreeMap<>();

    // Constructor
    public Enemies(int topLeftX, int topLeftY, int width, int height) {
        hitbox = new Hitbox(topLeftX, topLeftY, width, height);
    }

    public void updateHitBox(Enemies enemy) {
        hitbox.updateEnemyHitbox(enemy);
    }

    public void updateHealthbar(Enemies enemy) {
        healthbar.update(enemy);
    }

    // Desc: calls update method on all enemies
    // Param: n/a
    // Return: void
    public static void updateAllEntities() {
        for (int i = 0; i < spider.size(); i++) {
            spider.get(i).update();
        }
//        for (int i = 0; i < spirit.size(); i++) {
//            spirit.get(i).update();
//        }
        for (int i = 0; i < ogre.size(); i++) {
            ogre.get(i).update();
        }
    }

    // Desc: updates the rations of each enemy to keep them in place when zooming/moving
    // Param: n/a
    // Return: void
    public static void updateAllEntityRatios() {
        for (int i = 0; i < spider.size(); i++) {
            spider.get(i).setRatios();
        }
//        for (int i = 0; i < spirit.size(); i++) {
//            spirit.get(i).setRatios();
//        }
        for (int i = 0; i < ogre.size(); i++) {
            ogre.get(i).setRatios();
        }
    }

    // Desc: shift all enemies when the player moves
    // Param: the offSet, how much to move by, and whether the offset should be applied to the x or y coordinate
    // Return: void
    public static void shiftAllEntities(int offSet, String xOrY) {
        for (int i = 0; i < spider.size(); i++) {
            spider.get(i).shift(offSet, xOrY);
        }
//        for (int i = 0; i < spirit.size(); i++) {
//            spirit.get(i).shift(offSet, xOrY);
//        }
        for (int i = 0; i < ogre.size(); i++) {
            ogre.get(i).shift(offSet, xOrY);
        }
    }

    // Desc: draws all enemies
    // Param: Graphics g
    // Return: void
    public static void drawAllEnemies(Graphics g) {
        for (int i = 0; i < spider.size(); i++) {
            spider.get(i).draw(g);
        }
//        for (int i = 0; i < spirit.size(); i++) {
//            spirit.get(i).draw(g);
//        }
        for (int i = 0; i < ogre.size(); i++) {
            ogre.get(i).draw(g);
        }
    }

    // Desc: draws all the enemy healthbars
    // Param: Graphics g
    // Return: void
    public static void drawAllHealthbars(Graphics g) {
        for (int i = 0; i < spider.size(); i++) {
            spider.get(i).drawHealthbar(g);
        }
        for (int i = 0; i < ogre.size(); i++) {
            ogre.get(i).drawHealthbar(g);
        }
    }

    // Desc: checks the collision of all enemies with the player
    // Param: n/a
    // Return: a boolean value (yes -> the player has collided, no-> the player has not collided)
    public static boolean checkCollisionsWithPlayer() {
        for (int i = 0; i < spider.size(); i++) {
            spider.get(i).checkCollisions();
        }
        for (int i = 0; i < ogre.size(); i++) {
            ogre.get(i).checkCollisions();
        }
        return false;
    }

    // Desc: checks the distance between the player and all enemies to determine if they should chase or show healthbars or both
    // Param: n/a
    // Return: void
    public static void checkDistance() {

        double distance;

        // Loop through all spiders
        for (int i = 0; i < spider.size(); i++) {
            Spider spiderInstance = spider.get(i);

            distance = Util.calculateDistanceBetweenPoints(Player.getCenterX(), Player.getCenterY(), (spiderInstance.getX() + spiderInstance.getScale() / 2), (spiderInstance.getY() + spiderInstance.getScale() / 2));

            // If the distance is close enough for chasing or healthbars, set the boolean values to true
            if (distance < 85 * Map.getScaleFactor() ) {
                spiderInstance.setInRange(true);
            } else {
                spiderInstance.setInRange(false);
            }
            if (distance < 75 * Map.getScaleFactor()) {
                spiderInstance.setChasing(true);
            } else {
                spiderInstance.setChasing(false);
            }
        }
        // Loop through all ogres
        for (int i = 0; i < ogre.size(); i++) {
            Ogre ogreInstance = ogre.get(i);

            distance = Util.calculateDistanceBetweenPoints(Player.getCenterX(), Player.getCenterY(), (ogreInstance.getX() + ogreInstance.getScale() / 2), (ogreInstance.getY() + ogreInstance.getScale() / 2));

            // If the distance is close enough for chasing or healthbars, set the boolean values to true
            if (distance < 85 * Map.getScaleFactor() ) {
                ogreInstance.setInRange(true);
            } else {
                ogreInstance.setInRange(false);
            }
            if (distance < 75 * Map.getScaleFactor()) {
                ogreInstance.setChasing(true);
            } else {
                ogreInstance.setChasing(false);
            }
        }
    }

    // Desc: spawns a spider
    // Param: the x and y pos, the speed, how often it wanders around, how often it attacks, and the type
    // Return: void
    public static void spawnSpider(int xPos, int yPos, double speed, int wanderFrequency, int agressiveness, String type) {
        spider.add(new Spider(xPos, yPos, speed, wanderFrequency, agressiveness, type));
    }

//    public static void spawnSpirit(int xPos, int yPos, double speed, int wanderFrequency, String type) {
//        spirit.add(new Spirit(xPos, yPos, speed, wanderFrequency, type));
//    }
//
    // Desc: spawns an ogre
    // Param: the x and y pos, the speed, how often it wanders around, how often it attacks, and the type
    // Return: void
    public static void spawnOgre(int xPos, int yPos, double speed, int wanderFrequency, int agressiveness, String type) {
        ogre.add(new Ogre(xPos, yPos, speed, wanderFrequency, agressiveness, type));
    }

    // GETTERS AND SETTERS
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public int getHealth() {
        return 0;
    }
    public static ArrayList<Spider> getSpiders() {
        return spider;
    }
    public static ArrayList<Ogre> getOgres() {
        return ogre;
    }
    public static TreeMap<String, Integer> getStatCounter() {
        return statCounter;
    }
    public double getScale() {
        return 0;
    }
}
