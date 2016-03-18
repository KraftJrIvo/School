package com.mygdx.schoolRPG;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.schoolRPG.tools.AnimationSequence;


/**
 * Created by Kraft on 27.12.2014.
 */
public class Entity implements Comparable {

    float x, y, z=0, h, r = 0;
    float zSpeed = 0;
    Texture tex, texDef;
    TextureRegion texR;
    AnimationSequence anim;
    String texPath;
    boolean initialised = false;
    AssetManager assets;
    int type;
    float floorHeight;
    boolean floor = false;
    float alpha = 1.0f;
    float scale = 1.0f;
    boolean centered = false, falling = false;
    float fallY = 0;
    int angle = 0;
    boolean inWater = false, inGoo = false;

    void setFloor(boolean b) {
        floor = b;
        if (!floor) {
            h = y;
        } else {
            h = -999999;
        }
    }

    public Entity(AssetManager assets, String texPath, float x, float y, float h, float floorHeight, int angle) {
        this.texPath = texPath;
        this.x = x;
        this.y = y;
        this.assets = assets;
        type = 0;
        this.floorHeight = floorHeight;
        this.angle = angle;
        this.h = y;
        //hitBox = new Rectangle(0,0,0,0);
    }

    public Entity(AssetManager assets, TextureRegion tex, float x, float y, float h, float floorHeight, int angle) {
        //this.texPath = texPath;
        this.texR = tex;
        this.x = x;
        this.y = y;
        this.assets = assets;
        initialised = true;
        this.floorHeight = floorHeight;
        this.angle = angle;
        this.h = y;
        //if (angle == 2 && tex.getRegionHeight()%2!=0) this.y-=1;
        //hitBox = new Rectangle(0,0,0,0);
    }

    public Entity(AssetManager assets, Texture tex, float x, float y, float h, float floorHeight, int angle) {
        //this.texPath = texPath;
        this.tex = tex;
        this.x = x;
        this.y = y;
        this.assets = assets;
        initialised = true;
        this.floorHeight = floorHeight;
        this.angle = angle;
        this.h = y;
        //if (angle == 2 && tex.getHeight()%2!=0) this.y-=1;
        //hitBox = new Rectangle(0,0,0,0);
    }

    public Entity(AssetManager assets, AnimationSequence anim, float x, float y, float h, float floorHeight, int angle) {
        //this.texPath = texPath;
        this.anim = anim;
        this.x = x;
        this.y = y;
        this.assets = assets;
        initialised = true;
        this.floorHeight = floorHeight;
        this.angle = angle;
        this.h = y;
        //if (angle == 2 && anim.getFirstFrame().getRegionHeight()%2!=0) this.y-=1;
        //hitBox = new Rectangle(0,0,0,0);
    }

    public void initialiseIfNeeded() {
        if (initialised) return;
        if (texPath != null) {
            tex = assets.get(texPath);
        } /*else {
            texDef = assets.get("crate.png");
        }*/
        initialised = true;
    }

    public void dropShadow(SpriteBatch batch, float offsetX, float offsetY, int tileWidth, int tileHeight, Texture shadow) {
        initialiseIfNeeded();
        if (shadow != null) {
            batch.draw(shadow, offsetX + x - shadow.getWidth() / 2, offsetY - h +getRect().height , shadow.getWidth(), shadow.getHeight());
        }
    }

    public void draw(SpriteBatch batch, float offsetX, float offsetY, int tileWidth, int tileHeight, boolean platformMode) {
        initialiseIfNeeded();
        float baseAlpha = batch.getColor().a;
        batch.setColor(new Color(1, 1, 1, baseAlpha * alpha));
        float anglee = 0;
        if (angle == 1) {
            anglee -= 90;
        } else if (angle == 2) {
            anglee -= 180;
        } else if (angle == 3) {
            anglee -= 270;
        }
        if (anim != null) {
            anglee -= 90;
            float yy;
            if (floor) yy = offsetY - y-floorHeight + z + anim.getFirstFrame().getRegionHeight()*scale/2 - anim.getFirstFrame().getRegionHeight();
            else yy = offsetY - y-floorHeight + z;
            float xx = offsetX+x;
            if (centered) xx = offsetX+x - anim.getFirstFrame().getRegionWidth()*scale/2;

            batch.draw(anim.getCurrentFrame(false), xx, yy, anim.getFirstFrame().getRegionWidth() /2, anim.getFirstFrame().getRegionHeight()/2,
                    anim.getFirstFrame().getRegionWidth()*scale, anim.getFirstFrame().getRegionHeight()*scale, 1.0f, 1.0f, anglee, false);
        } else if (tex != null) {
            float yy;
            if (floor) yy = offsetY - y-floorHeight + z - tex.getHeight()*scale/2;
            else yy = offsetY - y-floorHeight + z;
            float xx = offsetX+x;
            if (centered) xx = offsetX+ x - tex.getWidth()*scale/2;
            //batch.draw(tex, xx, yy, tex.getWidth()*scale, tex.getHeight()*scale);
            batch.draw(tex, xx, yy, tex.getWidth()/2, 0, tex.getWidth()*scale, tex.getHeight()*scale, 1.0f, 1.0f, anglee, 0, 0, tex.getWidth(), tex.getHeight(), false, false);
        } else if (texR != null) {
            float yy;
            if (floor) yy = offsetY - y-floorHeight + z - texR.getRegionHeight()*scale/2;
            else yy = offsetY - y-floorHeight + z;
            float xx = offsetX+x;
            if (centered) xx = offsetX+x - texR.getRegionWidth()*scale/2;
            batch.draw(texR, xx, yy, texR.getRegionWidth()/2, 0,
                    texR.getRegionWidth()*scale, texR.getRegionHeight()*scale, 1.0f, 1.0f, anglee, false);
        }
        batch.setColor(new Color(1, 1, 1, baseAlpha));
    }

    Rectangle getRect() {
        if (anim != null) return new Rectangle(x, y, anim.getFirstFrame().getRegionWidth(), anim.getFirstFrame().getRegionHeight());
        else if (tex != null) return new Rectangle(x, y, tex.getWidth(), tex.getHeight());
        return new Rectangle(x, y, texR.getRegionWidth(), texR.getRegionHeight());
    }


    Rectangle getTexRect() {
        if (anim != null) return new Rectangle(x, y, anim.getFirstFrame().getRegionWidth(), anim.getFirstFrame().getRegionHeight());
        if (tex != null) return new Rectangle(x, y, tex.getWidth(), tex.getHeight());
        if (texR!=null) return new Rectangle(x, y, texR.getRegionWidth(), texR.getRegionHeight());
        return new Rectangle(x, y, 10, 10);
    }

    void setRect(Rectangle rect) {

    }

    /*public void load(AssetManager assets) {
        assets.load("crate.png", Texture.class);
    }

    public void initialise(AssetManager assets) {

    }*/

    /*public Rectangle pushOutSolidObjects(HittableEntity he, Area area, float oldX, float oldY) {
        return he.getRect();
    }*/


    public void fall() {

    }

    public void platformFall() {}

    public float getPreviousY() {
        return y;
    }

    @Override
    public int compareTo(Object o) {
        Entity e = (Entity)o;
        if (h < e.h) return -1;
        if (h > e.h) return 1;
        return 0;
    }
}
