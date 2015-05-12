package com.mygdx.schoolRPG.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

/**
 * Created by Kraft on 30.01.2015.
 */
public class MultiTile {
    String filePath;
    Texture tileSet;
    int xCount, yCount, width, height;
    ArrayList<ArrayList<TextureRegion>> tiles;
    boolean initialised = false;

    public MultiTile(String path, AssetManager assets, int xCount, int yCount) {
        this.xCount = xCount;
        this.yCount = yCount;
        filePath = path;
        assets.load(filePath, Texture.class);
    }

    public MultiTile(Texture tex, int xCount, int yCount) {
        this.xCount = xCount;
        this.yCount = yCount;
        tileSet = tex;
        width = (int)(tileSet.getWidth()/xCount);
        height = (int)(tileSet.getHeight()/yCount);
        tiles = new ArrayList<ArrayList<TextureRegion>>();
        for (int i=0; i<height; ++i) {
            tiles.add(new ArrayList<TextureRegion>());
            for (int t=0; t < width; ++t) {
                tiles.get(i).add(new TextureRegion(tileSet, width*t, height*i, width, height));
            }
        }
        initialised = true;
    }

    public void initialiseIfNeeded(AssetManager assets) {
        if (!initialised) {
            if (tileSet == null) tileSet = assets.get(filePath);
            width = (int)(tileSet.getWidth()/xCount);
            height = (int)(tileSet.getHeight()/yCount);
            tiles = new ArrayList<ArrayList<TextureRegion>>();
            for (int i=0; i<height; ++i) {
                tiles.add(new ArrayList<TextureRegion>());
                for (int t=0; t < width; ++t) {
                    tiles.get(i).add(new TextureRegion(tileSet, width*t, height*i, width, height));
                }
            }
            initialised = true;
        }
    }

    public TextureRegion getTile(int x, int y, boolean xInverted, boolean yInverted) {
        if (!initialised) {
            return null;
        }
        tiles.get(y).get(x).flip(xInverted, yInverted);
        TextureRegion temp = new TextureRegion(tiles.get(y).get(x));
        tiles.get(y).get(x).flip(xInverted, yInverted);
        return temp;

    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
