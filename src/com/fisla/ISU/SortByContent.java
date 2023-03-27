package com.fisla.ISU;

import java.util.Comparator;

public class SortByContent implements Comparator<Chest> {

    @Override
    public int compare(Chest o1, Chest o2) {
        return o1.getContent().toLowerCase().compareTo(o2.getContent().toLowerCase());
    }
}
