package com.mygdx.schoolRPG;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;


/**
 * Created by Kraft on 27.12.2014.
 */
public class Entity {

    float x, y;
    Texture tex, texDef;
    String texPath;
    boolean initialised = false;
    AssetManager assets;

    public Entity(AssetManager assets, String texPath, float x, float y) {
        this.texPath = texPath;
        this.x = x;
        this.y = y;
        this.assets = assets;
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
            batch.draw(shadow, offsetX + x - shadow.getWidth() / 2, offsetY - y - shadow.getHeight() / 2, shadow.getWidth(), shadow.getHeight());
        }
    }

    public void draw(SpriteBatch batch, float offsetX, float offsetY, int tileWidth, int tileHeight) {
        initialiseIfNeeded();
        if (tex != null) {
            batch.draw(tex, offsetX + x, offsetY - y, tex.getWidth(), tex.getHeight());
        }
    }

    Rectangle getRect() {
        return null;
    }

    void setRect(Rectangle rect) {

    }

    /*public void load(AssetManager assets) {
        assets.load("crate.png", Texture.class);
    }

    public void initialise(AssetManager assets) {

    }*/

    public Rectangle invalidate(Rectangle rect, Area area, float oldX, float oldY, boolean objectIsMovable, boolean objectIsPlayer) {
        return rect;
    }
}
