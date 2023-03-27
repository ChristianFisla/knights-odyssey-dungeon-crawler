package com.fisla.ISU;

import java.util.*;

public class Inventory {

    private ArrayList<Weapon> inventory = new ArrayList<>();

    private int size;
    private int selected;

    public Inventory(int size) {
        this.size = size;
    }
    public void add(Weapon weapon) {
        inventory.add(weapon);
    }
    public void remove(String weapon) {
        inventory.remove(weapon);
    }
    public ArrayList<Weapon> getInventory() {
        return inventory;
    }
}
