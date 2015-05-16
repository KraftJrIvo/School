package com.mygdx.schoolRPG;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;


/**
 * Created by Kraft on 27.12.2014.
 */
public class Entity implements Comparable {

    float x, y, h;
    Texture tex, texDef;
    TextureRegion texR;
    String texPath;
    boolean initialised = false;
    AssetManager assets;
    int type;
    float floorHeight;

    public Entity(AssetManager assets, String texPath, float x, float y, float h, float floorHeight) {
        this.texPath = texPath;
        this.x = x;
        this.y = y;
        this.assets = assets;
        type = 0;
        this.h = h;
        this.floorHeight = floorHeight;
        //hitBox = new Rectangle(0,0,0,0);
    }

    public Entity(AssetManager assets, TextureRegion tex, float x, float y, float h, float floorHeight) {
        //this.texPath = texPath;
        this.texR = tex;
        this.x = x;
        this.y = y;
        this.h = h;
        this.assets = assets;
        initialised = true;
        this.floorHeight = floorHeight;
        //hitBox = new Rectangle(0,0,0,0);
    }

    public Entity(AssetManager assets, Texture tex, float x, float y, float h, float floorHeight) {
        //this.texPath = texPath;
        this.tex = tex;
        this.x = x;
        this.y = y;
        this.h = h;
        this.assets = assets;
        initialised = true;
        this.floorHeight = floorHeight;
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

    public void draw(SpriteBatch batch, float offsetX, float offsetY, int tileWidth, int tileHeight) {
        initialiseIfNeeded();
        h = y;
        if (tex != null) {
            batch.draw(tex, offsetX+x, offsetY - y-floorHeight, tex.getWidth(), tex.getHeight());
        } else if (texR != null) {
            batch.draw(texR, offsetX+x, offsetY - y-floorHeight, texR.getRegionWidth(), texR.getRegionHeight());
        }
    }

    Rectangle getRect() {
        if (tex != null) return new Rectangle(x, y, tex.getWidth(), tex.getHeight());
        return new Rectangle(x, y, texR.getRegionWidth(), texR.getRegionHeight());
    }


    Rectangle getTexRect() {
        if (tex != null) return new Rectangle(x, y, tex.getWidth(), tex.getHeight());
        if (texR!=null) return new Rectangle(x, y, texR.getRegionWidth(), texR.getRegionHeight());
        return null;
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

    public void fall() {}
    public void platformFall() {}

    @Override
    public int compareTo(Object o) {
        if (o.getClass() == Entity.class || o.getClass() == HittableEntity.class || o.getClass() == Player.class) {
            Entity e = (Entity)o;
            //int compareY = (int)(e.y+e.getTexRect().height+e.floorHeight);
            //return (int)(compareY-this.y-this.h);
            //return (int)(this.y+getTexRect().height+floorHeight-compareY);
            return (int)(h - (e.h));
        }
        return 0;
    }
}
