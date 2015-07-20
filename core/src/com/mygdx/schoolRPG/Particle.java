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
    //Texture shadow;

    public Particle(AssetManager assets, ParticleProperties pp) {
        super(assets, (Texture)null, pp.spawnX, pp.spawnY, pp.h, pp.floorHeight);
        this.pp = pp;
        if (pp.animSeq1 != null) {
            anim = pp.animSeq1;
        } else if (pp.tex1 != null) {
            tex = pp.tex1;
        } else {
            texR = pp.texReg1;
        }
        XSpeed = (float)Math.random()*(pp.maxXSpeed-pp.minXSpeed)+pp.minXSpeed;
        YSpeed = (float)Math.random()*(pp.maxYSpeed-pp.minYSpeed)+pp.minYSpeed;
        ZSpeed = (float)Math.random()*(pp.maxZSpeed-pp.minZSpeed)+pp.minZSpeed;
        scale = 1.0f;
        curBounces = pp.bounces;
        fallen = false;
        centered = true;
        r = pp.r;

    }
    
    @Override
    public void fall() {
        if (!fallen) {
            x += XSpeed;
            y += YSpeed;

            if (z < ZSpeed && ZSpeed < 0 && !falling) z = 0;
            else z += ZSpeed;

            //if (Math.abs(XSpeed) < pp.XStep) XSpeed = 0;
            //else if (XSpeed != 0) XSpeed-=pp.XStep*((Math.abs(XSpeed)/XSpeed));

            //if (Math.abs(YSpeed) < pp.YStep) YSpeed = 0;
            //else if (YSpeed != 0) YSpeed-=pp.YStep*(Math.abs(YSpeed)/YSpeed);

            ZSpeed-=pp.ZStep;

            if (falling) curBounces = 0;

            if (z == 0 && !falling) {
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
        if (curBounces == 0 && fallen) {
            alpha -= pp.alphaStep;
            if (alpha < 0) alpha = 0;
            scale += pp.scalingStep;
        }
    }
}
