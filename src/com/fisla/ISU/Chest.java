package com.fisla.ISU;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

public class Chest extends Clickable {

    private String content;
    private BufferedImage image;
    private double x;
    private double y;
    private double scale = 16;

    private boolean hovering = false;

    private boolean opened = false;
    private boolean inRange = false;

    private double leftSideRatio;
    private double upSideRatio;

    private int mouseX = -5000;
    private int mouseY = -5000;

    // Constructor
    public Chest(double xPos, double yPos, String dir, String content) {
        setImage(dir);

        this.x = xPos;
        this.y = yPos;

        this.content = content;

        leftSideRatio = x / Map.getWidth();
        upSideRatio = y / Map.getHeight();
    }

    // Desc: updates the important variables
    // Param: n/a
    // Return: void
    public void update() {
        scale = 16 * Map.getScaleFactor();

        x = Map.getTopLeftPixelX() + (Map.getWidth() * leftSideRatio);
        y = Map.getTopLeftPixelY() + (Map.getHeight() * upSideRatio);

        if (Mouse.getPoint() != null) {
            mouseX = Mouse.getPoint().x;
            mouseY = Mouse.getPoint().y;
        }

        // Check if the mouse is hovering over the chest, and also if it has been opened
        if (opened) {
            setImage("assets/items/objects/chest/chest_2.png");
            this.hovering = false;
        } else {
            if (mouseX >= x && mouseX <= (x + scale) && mouseY >= (y + 30) && mouseY <= (y + scale + 30)) {
                hover();
                this.hovering = true;
            } else {
                setImage("assets/items/objects/chest/chest_0.png");
                this.hovering = false;
            }
        }
    }

    // Desc: Checks if the chest has been clicked on
    // Param: n/a
    // Return: void
    public void checkClicks() {

        boolean npcIsNotTalking = true;

        for (int i = 0; i < Map.getNpc().size(); i++) {
            if (Map.getNpc().get(i).getIsTalking()) {
                npcIsNotTalking = false;
            }
        }

        // If the chest has been clicked on, and the player is not talking to an NPC, then open the chest
        if (mouseX >= x && mouseX <= (x + scale) && mouseY >= (y + 30) && mouseY <= (y + scale + 30) && inRange && npcIsNotTalking && Map.getNpc().get(0).getFinishedDialogue()) {
            clicked();
        }
    }

    // Desc: When hovering, change the image to the hover image if the chest has not been opened and is in range
    // Param: n/a
    // Return: void
    @Override
    public void hover() {
        double distance = Util.calculateDistanceBetweenPoints(x + scale / 2.0, y + scale / 2.0, Player.getCenterX(), Player.getCenterY());

        if (distance < 75 * Map.getScaleFactor()) {
            inRange = true;
            changeState();
        } else {
            inRange = false;
        }
    }

    // Desc: When clicked, change the image to the clicked image if the chest has not been opened and is in range
    // Param: n/a
    // Return: void
    @Override
    public void clicked() {

        if (!opened) {
            // If the content is an axe, then give the player an axe
            if (content.equals("throwing_axe")) {
                try {
                    ThrowingAxe.getThrowingAxes().add(new ThrowingAxe());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ThrowingAxe axe = ThrowingAxe.getThrowingAxes().get(ThrowingAxe.getThrowingAxes().size() - 1);
                axe.pop(this);
            } else if (content.equals("coin")) {
                try {
                    // If the content is a coin, then give the player a coin
                    // Give anywhere from 1 to 10 coins
                    for (int i = 0; i < Math.round(Math.random() * 10); i++) {
                        Coin.getCoins().add(new Coin());
                        Coin coin = Coin.getCoins().get(Coin.getCoins().size() - 1);
                        coin.pop(this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        this.opened = true;
    }

    // GETTERS AND SETTERS
    @Override
    public void changeState() {
        setImage("assets/items/objects/chest/chest_1.png");
    }
    public Image getImage() {
        return image;
    }
    public void setImage(String dir) {
        try {
            image = ImageIO.read(new File(dir));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getScale() {
        return scale;
    }
    public String getContent() {
        return content;
    }
    public void setOpened(boolean set) {
        this.opened = set;
    }
    public boolean getHovering() {
        return hovering;
    }
}