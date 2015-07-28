package com.mygdx.schoolRPG.particles;

import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.schoolRPG.ParticleProperties;
import com.mygdx.schoolRPG.tools.AnimationSequence;

/**
 * Created by Kraft on 26.07.2015.
 */
public class Skull extends ParticleProperties {
    public Skull(AssetManager assets, float spawnX, float spawnY, float spawnZ) {
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
        h=0;
        floorHeight=0;
        tex1 = assets.get("particles/skull/1.png");
        r = 4;
        minXSpeed=-2.0f;
        maxXSpeed=2.0f;
        minYSpeed=7.0f;
        maxYSpeed=7.0f;
        minZSpeed=3.0f;
        maxZSpeed=3.0f;
        XStep=0.1f;
        YStep=0.2f;
        ZStep=0.1f;
        alphaStep=0;
        scalingStep=0.03f;
        bounces=0;
        floor = false;
        front = true;
        bouncing = false;
    }
}
