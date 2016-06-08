package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mygdx.schoolRPG.tools.AnimationSequence;

import java.io.*;

/**
 * Created by Kraft on 16.07.2015.
 */
public class ParticleProperties {
    public float h=0, floorHeight=0, r = 1;
    public Texture tex1=null, tex2=null;
    public TextureRegion texReg1=null, texReg2=null;
    public AnimationSequence animSeq1=null, animSeq2=null;
    public float minYSpeed=0, maxYSpeed=0, minXSpeed=0, maxXSpeed=0, minZSpeed=0, maxZSpeed=0;
    public float XStep=1, YStep=1, ZStep=1;
    public float alphaStep=0, scalingStep=1;
    public int bounces=0;
    public boolean floor = false, bouncing = false, front = false;
    public int speed =0, frames = 0;
    String path = "";

    public ParticleProperties() {
    }

    public ParticleProperties(AssetManager assets, ParticleProperties pp) {
        h=0;
        floorHeight=0;
        r = pp.r;
        minXSpeed = pp.minXSpeed;
        maxXSpeed=pp.maxXSpeed;
        minYSpeed=pp.minYSpeed;
        maxYSpeed=pp.maxYSpeed;
        minZSpeed=pp.minZSpeed;
        maxZSpeed=pp.maxZSpeed;
        XStep=pp.XStep;
        YStep=pp.YStep;
        ZStep=pp.ZStep;
        alphaStep=pp.alphaStep;
        scalingStep=pp.scalingStep;
        bounces=pp.bounces;
        floor = pp.floor;
        bouncing = pp.bouncing;
        front = pp.front;
        speed = pp.speed;
        frames = pp.frames;
        animSeq1 = new AnimationSequence(assets, pp.path + "/1.png", speed, true, frames);
        try {
            animSeq2 = new AnimationSequence(assets, pp.path + "/2.png", speed, true, frames);
        } catch (GdxRuntimeException e) {

        }
    }

    public ParticleProperties(String path) {

        h=0;
        floorHeight=0;
        r = 4;
        this.path = path;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path + "/stats.txt"));
            String line = in.readLine();
            r = Integer.parseInt(line);
            line = in.readLine();
            minXSpeed = Float.parseFloat(line);
            line = in.readLine();
            maxXSpeed=Float.parseFloat(line);
            line = in.readLine();
            minYSpeed=Float.parseFloat(line);
            line = in.readLine();
            maxYSpeed=Float.parseFloat(line);
            line = in.readLine();
            minZSpeed=Float.parseFloat(line);
            line = in.readLine();
            maxZSpeed=Float.parseFloat(line);
            line = in.readLine();
            XStep=Float.parseFloat(line);
            line = in.readLine();
            YStep=Float.parseFloat(line);
            line = in.readLine();
            ZStep=Float.parseFloat(line);
            line = in.readLine();
            alphaStep=Float.parseFloat(line);
            line = in.readLine();
            scalingStep=Float.parseFloat(line);
            line = in.readLine();
            bounces=Integer.parseInt(line);
            line = in.readLine();
            floor = Boolean.parseBoolean(line);
            line = in.readLine();
            bouncing = Boolean.parseBoolean(line);
            line = in.readLine();
            front = Boolean.parseBoolean(line);
            line = in.readLine();
            speed = Integer.parseInt(line);
            line = in.readLine();
            frames = Integer.parseInt(line);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void initialiseResources(AssetManager assets, String path) {
        animSeq1 = new AnimationSequence(assets, path + "/1.png", speed, true, frames);
        try {
            animSeq2 = new AnimationSequence(assets, path + "/2.png", speed, true, frames);
        } catch (GdxRuntimeException e) {

        }
    }
}
