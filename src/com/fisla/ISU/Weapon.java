package com.fisla.ISU;

import java.awt.*;
import java.util.ArrayList;

public abstract class Weapon {

    public abstract Image getImage();
    public abstract void setTheta(double set);
    public abstract void fire();
    public abstract void update();
    public abstract boolean getPopping();
    public abstract double getSpeed();
    public abstract void setSpeed(int set);
    public abstract double getSpeedFactor();
    public abstract int getCurrentFrame();
    public abstract ArrayList<ThrowingAxe> getThrowingAxesInstance();
    public abstract int getDamage();
    public abstract int getRotateSpeed();
    public abstract void setSpeedFactor(double set);
    public abstract double getX();
    public abstract double getY();
    public abstract void setX(double set);
    public abstract void setY(double set);
    public abstract void setInBound(boolean set);
    public abstract boolean getInBound();
    public abstract double getTheta();
    public abstract Hitbox getHitbox();
    public abstract Image[] getAnimationFrames();
    public abstract void shiftEntity(int offSet, String xOrY);
}
