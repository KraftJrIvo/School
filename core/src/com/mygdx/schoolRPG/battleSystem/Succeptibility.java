package com.mygdx.schoolRPG.battleSystem;

import com.mygdx.schoolRPG.World;

public class Succeptibility {
    DamageType damageType;
    int percent;
    boolean positive;

    public Succeptibility(World w, DamageType damageType, int percent) {
        this.damageType = damageType;
        this.percent = percent;
        positive = (percent < 100);
    }
}
