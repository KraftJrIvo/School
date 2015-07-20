package com.mygdx.schoolRPG;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.schoolRPG.tools.AnimationSequence;

/**
 * Created by Kraft on 16.07.2015.
 */
public class ParticleProperties {
    public float spawnX=0, spawnY=0, spawnZ=0, h=0, floorHeight=0, r = 1;
    public Texture tex1=null, tex2=null;
    public TextureRegion texReg1=null, texReg2=null;
    public AnimationSequence animSeq1=null, animSeq2=null;
    public float minYSpeed=0, maxYSpeed=0, minXSpeed=0, maxXSpeed=0, minZSpeed=0, maxZSpeed=0;
    public float XStep=1, YStep=1, ZStep=1;
    public float alphaStep=0, scalingStep=1;
    public int bounces=0;
    public boolean floor = false;
    public ParticleProperties() {
    }
}
