package com.mygdx.schoolRPG.particles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.schoolRPG.Particle;
import com.mygdx.schoolRPG.ParticleProperties;
import com.mygdx.schoolRPG.tools.AnimationSequence;

/**
 * Created by Kraft on 16.07.2015.
 */
public class TestParticle extends ParticleProperties {
    public TestParticle(AssetManager assets, float spawnX, float spawnY, float spawnZ) {
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
        h=0;
        floorHeight=0;
        animSeq1 = new AnimationSequence(assets, "particles/test/1.png", 0, true);
        animSeq2 = new AnimationSequence(assets, "particles/test/2.png", 0, true);
        r = 4;
        minXSpeed=-1.0f;
        maxXSpeed=1.0f;
        minYSpeed=-1.0f;
        maxYSpeed=1.0f;
        minZSpeed=0.5f;
        maxZSpeed=2.0f;
        XStep=0.1f;
        YStep=0.1f;
        ZStep=0.1f;
        alphaStep=0.008f;//.01f;
        scalingStep=0;//.01f;
        bounces=5;
        floor = true;
        bouncing = true;
    }
}
