package com.mygdx.schoolRPG;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.schoolRPG.tools.AnimationSequence;

import java.util.ArrayList;

public class Background {
    ArrayList<Texture> layers;
    ArrayList<AnimationSequence> animLayers;
    ArrayList<Float> layersMovementX;
    ArrayList<Float> layersMovementY;
    ArrayList<Float> layersParallaxX;
    ArrayList<Float> layersParallaxY;
    ArrayList<Float> layersOffsetsX;
    ArrayList<Float> layersOffsetsY;

    Background() {
        layers = new ArrayList<Texture>();
        animLayers = new ArrayList<AnimationSequence>();
        layersMovementX = new ArrayList<Float>();
        layersMovementY = new ArrayList<Float>();
        layersParallaxX = new ArrayList<Float>();
        layersParallaxY = new ArrayList<Float>();
        layersOffsetsX = new ArrayList<Float>();
        layersOffsetsY = new ArrayList<Float>();
    }

    public void addLayer(Texture texture, float mX, float mY, float pX, float pY) {
        layers.add(texture);
        animLayers.add(null);
        layersMovementX.add(mX);
        layersMovementY.add(mY);
        layersParallaxX.add(pX);
        layersParallaxY.add(pY);
        layersOffsetsX.add(0f);
        layersOffsetsY.add(0f);
    }

    public void addLayer(AnimationSequence anim, float mX, float mY, float pX, float pY) {
        layers.add(null);
        animLayers.add(anim);
        layersMovementX.add(mX);
        layersMovementY.add(mY);
        layersParallaxX.add(pX);
        layersParallaxY.add(pY);
        layersOffsetsX.add(0f);
        layersOffsetsY.add(0f);
    }

    public void move(float mX, float mY) {
        if (Math.abs(mX) < 0.1f) mX = 0;
        if (Math.abs(mY) < 0.1f) mY = 0;
        for (int i = 0; i < layers.size(); ++i) {
            layersOffsetsX.set(i, layersOffsetsX.get(i) - mX * layersParallaxX.get(i) + layersMovementX.get(i));
            layersOffsetsY.set(i, layersOffsetsY.get(i) + mY * layersParallaxY.get(i) + layersMovementY.get(i));
            while (layersOffsetsX.get(i) > layers.get(i).getWidth()) layersOffsetsX.set(i, layersOffsetsX.get(i) -  layers.get(i).getWidth());
            while (layersOffsetsY.get(i) > layers.get(i).getHeight()) layersOffsetsY.set(i, layersOffsetsY.get(i) -  layers.get(i).getHeight());
            while (layersOffsetsX.get(i) < -layers.get(i).getWidth()) layersOffsetsX.set(i, layersOffsetsX.get(i) +  layers.get(i).getWidth());
            while (layersOffsetsY.get(i) < -layers.get(i).getHeight()) layersOffsetsY.set(i, layersOffsetsY.get(i) +  layers.get(i).getHeight());
        }
    }

    public void draw(SpriteBatch batch, int screenSizeX, int screenSizeY) {
        for (int i = 0; i < layers.size(); ++i) {
            int xLoops = screenSizeX / layers.get(i).getWidth() + 3;
            int yLoops = screenSizeY / layers.get(i).getHeight() + 3;
            for (int z = 0; z < xLoops; ++z) {
                for (int zz = 0; zz < yLoops; ++zz) {
                    if (animLayers.get(i) == null) {
                        batch.draw(layers.get(i), layersOffsetsX.get(i) + layers.get(i).getWidth() * (z-1), layersOffsetsY.get(i) + layers.get(i).getHeight() * (zz - 1));
                    } else {
                        batch.draw(animLayers.get(i).getCurrentFrame(false), layersOffsetsX.get(i) + layers.get(i).getWidth() * (z-1), layersOffsetsY.get(i) + layers.get(i).getHeight() * (zz - 1));
                    }
                }
            }
        }
    }
}
