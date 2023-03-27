package com.fisla.ISU;

import java.awt.*;

public class Healthbar {

    private int totalHealth;
    private int currentHealth;

    private Rectangle totalHealthRectangle;
    private Rectangle currentHealthRectangle;
    private Rectangle outline;

    private boolean show = true;

    public Healthbar(int totalHealth) {
        this.totalHealth = totalHealth;
    }

    // Desc: Updates the position of the healthbar
    // Param: The enemy that the healthbar is attached to
    // Return: void
    public void update(Enemies enemy) {
        this.currentHealth = enemy.getHealth();

        this.totalHealthRectangle = new Rectangle((int)enemy.getX(), (int)(enemy.getY() - enemy.getScale() / 3), (int)enemy.getScale(), (int)(enemy.getScale() * 0.2));
        this.currentHealthRectangle = new Rectangle((int)enemy.getX(), (int)(enemy.getY() - enemy.getScale() / 3), (int)(enemy.getScale() * ((double)currentHealth / (double)totalHealth)), (int)(enemy.getScale() * 0.2));
        this.outline = new Rectangle((int)(enemy.getX() - enemy.getScale() / 12), (int)(enemy.getY() - enemy.getScale() / 3 - enemy.getScale() / 12), (int)(enemy.getScale() + enemy.getScale() / 6), (int)((enemy.getScale() * 0.2) + enemy.getScale() / 6));
    }

    // Desc: Draws the healthbar
    // Param: Graphics g
    // Return: void
    public void draw(Graphics g) {

        if (show && totalHealthRectangle != null && currentHealthRectangle != null && outline != null) {
            g.setColor(new Color(217, 53, 80));
            g.fillRect(totalHealthRectangle.x, totalHealthRectangle.y, totalHealthRectangle.width, totalHealthRectangle.height);
            g.setColor(new Color(23, 182, 126));
            g.fillRect(currentHealthRectangle.x, currentHealthRectangle.y, currentHealthRectangle.width, currentHealthRectangle.height);
            g.setColor(Color.BLACK);
        }
    }

    // GETTERS AND SETTERS
    public void setShow(boolean set) {
        show = set;
    }
}