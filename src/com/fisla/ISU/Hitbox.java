package com.fisla.ISU;

import java.awt.*;

public class Hitbox {

    private Rectangle hitbox;

    private double x;
    private double y;
    private double width;
    private double height;

    // Constructor
    public Hitbox(int topLeftX, int topLeftY, int width, int height) {
        this.x = topLeftX;
        this.y = topLeftY;

        this.width = width;
        this.height = height;

        hitbox = new Rectangle(topLeftX, topLeftY, width, height);
    }

    // Desc: updates the position of the hitbox for the player
    // Param: n/a
    // Return: void
    public void updatePlayerHitbox() {
        this.x = Player.getX();
        this.y = Player.getY();

        this.width = Player.getDimensionX();
        this.height = Player.getDimensionY();

        hitbox = new Rectangle((int)(x  + MapRange.mapRange(Player.getDimensionX(), 15.51, 40.15, 6, 0)), (int)(y + MapRange.mapRange(Player.getDimensionX(), 15.51, 40.15, 4, -4)), (int)width, (int)height);
    }

    // Desc: Updates the position of the hitbox for the enemy
    // Param: n/a
    // Return: void
    protected void updateEnemyHitbox(Enemies enemy) {
        this.x = enemy.getX();
        this.y = enemy.getY();

        this.width = enemy.getScale();
        this.height = enemy.getScale();

        hitbox = new Rectangle((int)x, (int)y, (int)width, (int)height);
    }

    // Desc: Updates the position of the hitbox for the weapon
    // Param: n/a
    // Return: void
    public void updateWeaponHitbox(Weapon weapon) {
        this.x = weapon.getX();
        this.y = weapon.getY();

        this.width = Player.getDimensionX() * 0.75;
        this.height = Player.getDimensionY() * 0.75;

        hitbox = new Rectangle((int)x, (int)y, (int)width, (int)height);
    }
    // Desc: Updates the position of the hitbox for the coin
    // Param: n/a
    // Return: void
    public void updateCoinHitBox(Coin coin) {
        this.x = coin.getX();
        this.y = coin.getY();

        this.width = Player.getDimensionX() * 0.5;
        this.height = Player.getDimensionY() * 0.5;

        hitbox = new Rectangle((int)x, (int)y, (int)width, (int)height);
    }

    // GETTERS AND SETTERS
    public boolean collision(Hitbox h) {
        return this.hitbox.intersects(h.hitbox);
    }
    public Rectangle getRectangle() {
        return hitbox;
    }

}
