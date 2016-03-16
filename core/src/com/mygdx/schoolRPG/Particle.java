package com.mygdx.schoolRPG;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.schoolRPG.Entity;
import com.mygdx.schoolRPG.tools.AnimationSequence;

/**
 * Created by Kraft on 16.07.2015.
 */
public class Particle extends Entity {
    
    float XSpeed, YSpeed, ZSpeed;
    int curBounces;
    ParticleProperties pp;
    boolean fallen;
    boolean platformMode;
    //Texture shadow;

    public Particle(AssetManager assets, ParticleProperties pp, boolean platformMode) {
        super(assets, (Texture)null, pp.spawnX, pp.spawnY, pp.h, pp.floorHeight, 0);
        this.pp = pp;
        if (pp.animSeq1 != null) {
            anim = pp.animSeq1;
        } else if (pp.tex1 != null) {
            tex = pp.tex1;
        } else {
            texR = pp.texReg1;
        }
        XSpeed = (float)Math.random()*(pp.maxXSpeed-pp.minXSpeed)+pp.minXSpeed;
        if (!platformMode) YSpeed = (float)Math.random()*(pp.maxYSpeed-pp.minYSpeed)+pp.minYSpeed;
        else YSpeed = (float)Math.random()*(-pp.maxZSpeed+pp.minYSpeed)-pp.minYSpeed;
        ZSpeed = (float)Math.random()*(pp.maxZSpeed-pp.minZSpeed)+pp.minZSpeed;
        scale = 1.0f;
        curBounces = pp.bounces;
        fallen = false;
        centered = true;
        r = pp.r;
        this.platformMode = platformMode;
        if (platformMode) z = 0;
        this.h = y;
    }

    public void bounce(boolean floor, boolean side) {
        if (!pp.bouncing) return;
        curBounces--;
        if (curBounces <= 0) {
            if (!fallen && platformMode) {
                if (side && Math.abs(XSpeed) > Math.abs(YSpeed)) {
                    if (XSpeed > 0) angle = 3;
                    else angle = 1;
                } else {
                    if (YSpeed > 0) angle = 0;
                    else angle = 2;
                }
            }


            fallen = true;
            floor = pp.floor;

            if (pp.animSeq2 != null) {
                anim = pp.animSeq2;
            } else if (pp.tex2 != null) {
                tex = pp.tex2;
                anim = null;
            } else if (pp.texReg2 != null) {
                texR = pp.texReg2;
                tex = null;
                anim = null;
            }
        } else if (floor) {
            YSpeed = pp.minYSpeed;
        } else {
            if (side) {
                XSpeed = -XSpeed;
            } else {
                YSpeed = -YSpeed;
            }
        }
        /*if (YSpeed > pp.maxYSpeed && YSpeed > pp.minYSpeed) YSpeed = pp.maxYSpeed;
        else if (YSpeed < pp.minYSpeed) YSpeed = pp.minYSpeed;*/
    }
    
    @Override
    public void fall() {
        if (!fallen) {
            if (!falling) {
                h = y;
            }
            x += XSpeed;
            if (!platformMode) {
                y += YSpeed;
                if (z < ZSpeed && ZSpeed < 0 && !falling) z = 0;
                else z += ZSpeed;
                ZSpeed-=pp.ZStep;
                if (falling) curBounces = 0;

                if (!platformMode) {
                    if (z == 0 && !falling && pp.bouncing) {
                        curBounces--;
                        if (curBounces <= 0) {
                            fallen = true;
                            floor = pp.floor;
                            if (pp.animSeq2 != null) {
                                anim = pp.animSeq2;
                            } else if (pp.tex2 != null) {
                                tex = pp.tex2;
                            } else if (pp.texReg2 != null) {
                                texR = pp.texReg2;
                            }
                        } else {
                            ZSpeed = (float)Math.random()*(pp.maxZSpeed/2-pp.minZSpeed)+pp.minZSpeed;
                        }
                    }
                }
            } else {
                /*if (y < YSpeed && YSpeed < 0 && !falling) y = 0;
                else*/ y += YSpeed;
                YSpeed+=pp.YStep;

            }

        } else {
            h = y;// + 10;
        }
        if ((curBounces == 0 && fallen && pp.bouncing) || (platformMode && curBounces<=0)) {
            alpha -= pp.alphaStep;
            if (alpha < 0) alpha = 0;
            scale += pp.scalingStep;
        }
    }

    @Override
    public float getPreviousY() {
        return y - YSpeed;
    }

}
