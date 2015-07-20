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
        animSeq1 = new AnimationSequence(assets, "particles/test/1.png", 5, true);
        animSeq2 = new AnimationSequence(assets, "particles/test/2.png", 5, true);
        r = 4;
        minYSpeed=-1.0f;
        maxYSpeed=1.0f;
        minXSpeed=-1.0f;
        maxXSpeed=1.0f;
        minZSpeed=1.0f;
        maxZSpeed=3.0f;
        XStep=0.1f;
        YStep=0.1f;
        ZStep=0.1f;
        alphaStep=0.01f;
        scalingStep=0.01f;
        bounces=9999;
        floor = true;
    }
}
