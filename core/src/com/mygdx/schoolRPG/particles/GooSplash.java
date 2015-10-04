package com.mygdx.schoolRPG.particles;

import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.schoolRPG.ParticleProperties;
import com.mygdx.schoolRPG.tools.AnimationSequence;

/**
 * Created by Kraft on 16.07.2015.
 */
public class GooSplash extends ParticleProperties {
    public GooSplash(AssetManager assets, float spawnX, float spawnY, float spawnZ) {
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
        h=0;
        floorHeight=0;
        animSeq1 = new AnimationSequence(assets, "particles/goo/1.png", 20, true);
        tex2 = assets.get("particles/goo/2.png");
        r = 0;
        minXSpeed=-2.0f;
        maxXSpeed=2.0f;
        minYSpeed=1.0f;
        maxYSpeed=2.0f;
        minZSpeed=1.0f;
        maxZSpeed=2.0f;
        XStep=0.1f;
        YStep=0.1f;
        ZStep=0.1f;
        alphaStep=0.1f;
        scalingStep=0;
        bounces=1;
        floor = true;
        bouncing = true;
    }
}
