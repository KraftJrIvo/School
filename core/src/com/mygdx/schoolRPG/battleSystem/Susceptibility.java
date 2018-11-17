package com.mygdx.schoolRPG.battleSystem;

import com.mygdx.schoolRPG.World;

public class Susceptibility {
    DamageType damageType;
    float percent;
    boolean positive;

    public Susceptibility(World w, DamageType damageType, float percent) {
        this.damageType = damageType;
        this.percent = percent;
        positive = (percent < 100);
    }
}
