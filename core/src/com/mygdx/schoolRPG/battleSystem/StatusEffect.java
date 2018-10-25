package com.mygdx.schoolRPG.battleSystem;

import java.util.ArrayList;

public class StatusEffect {
    String name;
    int life;
    ArrayList<DamageType> damageTypes;
    ArrayList<Integer> baseDamages;
    boolean positive;

    public StatusEffect(String name, ArrayList<DamageType> damageTypes, ArrayList<Integer> baseDamages) {
        this.name = name;
        this.damageTypes = damageTypes;
        this.baseDamages = baseDamages;
        positive = (baseDamages.size() == 0);
    }
}
