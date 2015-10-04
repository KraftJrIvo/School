package com.mygdx.schoolRPG.particles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.schoolRPG.ParticleProperties;
import com.mygdx.schoolRPG.tools.AnimationSequence;

/**
 * Created by Kraft on 16.07.2015.
 */
public class WaterSplash extends ParticleProperties {
    public WaterSplash(AssetManager assets, float spawnX, float spawnY, float spawnZ) {
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
        h=0;
        floorHeight=0;
        tex1 = assets.get("particles/water/1.png");
        tex2 = assets.get("particles/water/2.png");
        r = 3;
        minXSpeed=-2.0f;
        maxXSpeed=2.0f;
        minYSpeed=2.0f;
        maxYSpeed=3.0f;
        minZSpeed=2.0f;
        maxZSpeed=3.0f;
        XStep=0.1f;
        YStep=0.1f;
        ZStep=0.1f;
        alphaStep=0.4f;
        scalingStep=0;
        bounces=1;
        floor = true;
        bouncing = true;
    }
}
