package com.mygdx.schoolRPG;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

/**
 * Created by Kraft on 27.07.2015.
 */
public class CheckPoint extends Entity {

    boolean on = false;
    Texture offTex, onTex;

    public CheckPoint(AssetManager assets, String spritePath, float x, float y, float h, float floorHeight, int angle) {
        super(assets, (Texture)assets.get(spritePath+"/save1.png"), x, y, h, floorHeight, angle);
        offTex = assets.get(spritePath+"/save1.png");
        onTex = assets.get(spritePath+"/save2.png");
    }

    public void turnOn(ArrayList<CheckPoint> array) {
        for (int i = 0; i < array.size(); ++i) {
            array.get(i).turnOff();
        }
        on = true;
        tex = onTex;
    }

    public void turnOff() {
        on = false;
        tex = offTex;
    }

    public boolean collide(Rectangle rect) {
        Rectangle thisRect = new Rectangle(x, y, offTex.getWidth(), offTex.getHeight());
        return thisRect.overlaps(rect);
    }
}
