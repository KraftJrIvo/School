package com.mygdx.schoolRPG;

import com.badlogic.gdx.assets.AssetManager;

/**
 * Created by Kraft on 04.09.2016.
 */
public class Item {
    enum EquipSlot {
        HEAD,
        BODY,
        NONE
    }
    EquipSlot equipSlot;
    float mass;
    float sharpness;
    float range;
    String name;
    String Description;
    boolean stackable;
    int maxStack;

    public Item(AssetManager assets, String worldPath) {

    }
}
