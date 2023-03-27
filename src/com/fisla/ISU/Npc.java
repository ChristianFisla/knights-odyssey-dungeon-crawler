package com.fisla.ISU;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Npc extends Clickable {

    private BufferedImage image;
    private double x;
    private double y;

    private boolean inRange = false;
    private boolean hovering = false;

    private static double scale = 16;

    private double leftSideRatio;
    private double upSideRatio;

    private static int dialogueIndex = 0;
    private int END_OF_DIALOGUE;

    private boolean isTalking = false;
    private boolean finishedDialogue = false;

    private int mouseX = -5000;
    private int mouseY = -5000;

    // Constructor
    public Npc(double xPos, double yPos, int endOfDialogue, String dir) {
        setImage(dir);

        this.END_OF_DIALOGUE = endOfDialogue;

        this.x = xPos;
        this.y = yPos;

        leftSideRatio = x / Map.getWidth();
        upSideRatio = y / Map.getHeight();
    }

    // Desc: Updates the position of the NPC and checks if the player is in range
    // Param: n/a
    // Return: void
    public void update() {

        if (dialogueIndex == END_OF_DIALOGUE) {
            endInteraction();
            dialogueIndex = 0;
        }

        scale = 16 * Map.getScaleFactor();

        // Resizes the NPC based on the scale factor
        x = Map.getTopLeftPixelX() + (Map.getWidth() * leftSideRatio);
        y = Map.getTopLeftPixelY() + (Map.getHeight() * upSideRatio);

        if (Mouse.getPoint() != null) {
            mouseX = Mouse.getPoint().x;
            mouseY = Mouse.getPoint().y;
        }

        // Checks if the player is in range of the NPC and if the player is hovering over the NPC
        if (mouseX >= x && mouseX <= (x + scale) && mouseY >= (y + 30) && mouseY <= (y + scale + 30)) {
            hover();
            this.hovering = true;
        } else {
            setImage("assets/characters/NPC/npc1.png");
            this.hovering = false;
        }
    }

    // Desc: Changes the state of the NPC when the player is in range
    // Param: n/a
    // Return: void
    @Override
    public void hover() {

        double distance = Util.calculateDistanceBetweenPoints(x + scale / 2.0, y + scale / 2.0, Player.getCenterX(), Player.getCenterY());

        // Checks if the player is in range of the NPC
        if (distance < 75 * Map.getScaleFactor()) {
            if (!finishedDialogue && !isTalking) changeState();
            inRange = true;
        } else {
            inRange = false;
        }
    }

    // Desc: When clicked, the NPC will change state and start talking
    // Param: n/a
    // Return: void
    @Override
    public void clicked() {

        // Set variables
        Start.setZoomingIn(true);
        isTalking = true;

        Player.setKeepPositionOnPlane(true);
        Player.stayInPlace();

        int xOffset = (int)-(x + (scale / 2) - Player.getX());
        int yOffset = (int)-(y + (scale / 2) - Player.getY());

        Map.changeTopLeftPixelX(xOffset);
        Map.changeTopLeftPixelY(yOffset);

        Weapon weapon = null;
        boolean weaponIsDefined = false;

        if (Player.getInvWeaponEquipped()) weapon = Player.getInventory().getInventory().get(0);
        if (weapon == null && ThrowingAxe.getThrowingAxes().size() > 0) weapon = ThrowingAxe.getThrowingAxes().get(0);
        if (weapon != null) weaponIsDefined = true;

        if (weaponIsDefined) if (Player.getInvWeaponEquipped() || weapon.getThrowingAxesInstance().size() > 0) weapon.shiftEntity(xOffset, "x");
        if (weaponIsDefined) if (Player.getInvWeaponEquipped() || weapon.getThrowingAxesInstance().size() > 0) weapon.shiftEntity(yOffset, "y");

        sayDialogue();
    }

    // Desc: Performs all end of interaction tasks
    // Param: n/a
    // Return: void
    public void endInteraction() {

        Dialogue.setAnimate(false);

        Start.setZoomingIn(false);

        isTalking = false;
        finishedDialogue = true;
        dialogueIndex = 0;

        resetMapOnPlayer();

        Player.setX(296);
        Player.setY(296);

        Player.setKeepPositionOnPlane(false);

    }

    // Desc: Puts the map back on the player
    // Param: n/a
    // Return: void
    public void resetMapOnPlayer() {

        int xOffset = (int)-(Player.getX() - x);
        int yOffset = (int)-(Player.getY() - x);

        Map.changeTopLeftPixelX(xOffset);
        Map.changeTopLeftPixelY(yOffset);

        Weapon weapon = null;
        boolean weaponIsDefined = false;

        if (Player.getInvWeaponEquipped()) weapon = Player.getInventory().getInventory().get(0);
        if (weapon == null && ThrowingAxe.getThrowingAxes().size() > 0) weapon = ThrowingAxe.getThrowingAxes().get(0);
        if (weapon != null) weaponIsDefined = true;

        if (weaponIsDefined) if (Player.getInvWeaponEquipped() || weapon.getThrowingAxesInstance().size() > 0) weapon.shiftEntity(xOffset, "x");
        if (weaponIsDefined) if (Player.getInvWeaponEquipped() || weapon.getThrowingAxesInstance().size() > 0) weapon.shiftEntity(yOffset, "y");

    }

    @Override
    public void changeState() {
        setImage("assets/characters/NPC/npc1White.png");
    }
    public void sayDialogue() {

        Dialogue.getStartingTickAnimation();
        Dialogue.setAnimate(true);

    }
    public static void skipDialogue() {
        dialogueIndex++;
    }
    // Desc: Checks for mouse clicks
    // Param: n/a
    // Return: void
    public void checkClicks() {

        if (!Start.getBlockInput()) {
            // Checks if the player is in range of the NPC and if the player is hovering over the NPC
            if (mouseX >= x && mouseX <= (x + scale) && mouseY >= (y + 30) && mouseY <= (y + scale + 30) && inRange && !finishedDialogue) {
                clicked();
            }
        }
    }
    // GETTERS AND SETTERS
    public Image getImage() {
        return image;
    }
    public void setImage(String dir) {
        try {
            image = ImageIO.read(new File(dir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public boolean getIsTalking() {
        return isTalking;
    }
    public void setIsTalking(boolean set) {
        isTalking = set;
    }
    public double getLeftSideRatio() {
        return leftSideRatio;
    }
    public double getUpSideRatio() {
        return upSideRatio;
    }
    public double getScale() {
        return scale;
    }
    public boolean getHovering() {
        return hovering;
    }
    public boolean getFinishedDialogue() {
        return finishedDialogue;
    }
}