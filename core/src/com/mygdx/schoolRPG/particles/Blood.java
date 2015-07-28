package com.mygdx.schoolRPG.particles;

import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.schoolRPG.ParticleProperties;
import com.mygdx.schoolRPG.tools.AnimationSequence;

/**
 * Created by Kraft on 26.07.2015.
 */
public class Blood extends ParticleProperties {
    public Blood(AssetManager assets, float spawnX, float spawnY, float spawnZ) {
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
        h=0;
        floorHeight=0;
        animSeq1 = new AnimationSequence(assets, "particles/blood/1.png", 10, true);
        r = 4;
        minXSpeed=-3.0f;
        maxXSpeed=3.0f;
        minYSpeed=-3.0f;
        maxYSpeed=3.0f;
        minZSpeed=1.0f;
        maxZSpeed=3.0f;
        XStep=0.1f;
        YStep=0.1f;
        ZStep=0.1f;
        alphaStep=0.01f;
        scalingStep=0.01f;
        bounces=9999;
        floor = false;
        bouncing = false;
        front = true;
    }
}
