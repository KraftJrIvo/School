package com.mygdx.schoolRPG;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.schoolRPG.tools.AnimationSequence;

import java.util.ArrayList;


/**
 * Created by Kraft on 27.12.2014.
 */
public class Entity implements Comparable {

    public float x, y, z=0, h, r = 0;
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
    public float alpha = 1.0f;
    float scale = 1.0f;
    boolean centered = false, falling = false;
    float fallY = 0;
    int angle = 0;
    boolean inWater = false, inGoo = false;
    Texture active = null;
    public Item containingItem = null;
    int spawnArea = -1;
    float offY = 0;
    public boolean collidable = true;

    public ArrayList<TextureRegion> heads = null;
    public ArrayList<TextureRegion> headWears = null;
    public ArrayList<Integer> headsOffX = null;
    public ArrayList<Integer> headsOffY = null;
    public ArrayList<TextureRegion> bodies = null;
    public ArrayList<TextureRegion> bodyWears = null;
    public ArrayList<Integer> bodiesOffX = null;
    public ArrayList<Integer> bodiesOffY = null;
    public Texture charTex = null;
    public TextureRegion charTexR = null;
    public AnimationSequence charAnim = null;
    public boolean drawChar = false;
    public boolean draw = true;

    void setFloor(boolean b) {
        floor = b;
        if (!floor) {
            h = y;
        } else {
            h = -999999;
        }
    }

    void setItem(Item item) {
        containingItem = item;
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
        offY = h;
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
        offY = h;
        //if (angle == 2 && tex.getRegionHeight()%2!=0) this.y-=1;
        //hitBox = new Rectangle(0,0,0,0);
    }

    public Entity(AssetManager assets, Texture tex, float x, float y, float h, float floorHeight, int angle) {
        if (tex != null) this.texPath = ((FileTextureData)tex.getTextureData()).getFileHandle().path();
        this.tex = tex;
        this.x = x;
        this.y = y;
        this.assets = assets;
        initialised = true;
        this.floorHeight = floorHeight;
        this.angle = angle;
        this.h = y;
        offY = h;
        //if (angle == 2 && tex.getHeight()%2!=0) this.y-=1;
        //hitBox = new Rectangle(0,0,0,0);
    }

    public Entity(AssetManager assets, AnimationSequence anim, float x, float y, float h, float floorHeight, int angle) {
        this.texPath = anim.path;
        this.anim = anim;
        this.x = x;
        this.y = y;
        this.assets = assets;
        initialised = true;
        this.floorHeight = floorHeight;
        this.angle = angle;
        this.h = y;
        offY = h;
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

    protected TextureRegion maybeDrawChar(SpriteBatch batch, float x, float y) {
        if (!drawChar) return null;
        TextureRegion tex2;
        int id;
        if (charAnim != null) {
            id = charAnim.getCurrentFrameId();
            tex2 = charAnim.getCurrentFrame(false);
        } else {
            id = 0;
            tex2 = new TextureRegion(charTex);
            tex2 = new TextureRegion(charTex);
            tex2 = new TextureRegion(charTex);
        }
        TextureRegion head = heads.get(id);
        TextureRegion headWear = headWears.get(id);
        int headOffX = headsOffX.get(id);
        int headOffY = headsOffY.get(id);
        TextureRegion body = bodies.get(id);
        TextureRegion bodyWear = bodyWears.get(id);
        int bodyOffX = bodiesOffX.get(id);
        int bodyOffY = bodiesOffY.get(id);
        if (head != null) {
            batch.draw(body, x + bodyOffX, y + bodyOffY);
            if (bodyWear != null) {
                batch.draw(bodyWear, x + bodyOffX, y + bodyOffY);
            }
            batch.draw(head, x + headOffX, y + headOffY);
            if (headWear != null) {
                batch.draw(headWear, x + headOffX, y + headOffY);
            }
        }
        //batch.draw(tex2, x, y);
        return tex2;
    }

    public void draw(SpriteBatch batch, float offsetX, float offsetY, int tileWidth, int tileHeight, boolean platformMode, boolean active, int activeX, int activeY) {
        if (floor) {
            h = -999999;
        }
        initialiseIfNeeded();
        if (active) {
            if (containingItem != null) {
                this.active = containingItem.icon;
            } else {
                this.active = assets.get("active.png");
            }
            int height = 0;
            int width = 0;
            if (anim != null) {
                height = anim.getFirstFrame().getRegionHeight();
                width = anim.getFirstFrame().getRegionWidth();
            } else if (tex != null) {
                height = tex.getHeight();
                width = tex.getWidth();
            } else if (texR != null) {
                height = texR.getRegionHeight();
                width = texR.getRegionWidth();
            }
            if (containingItem != null) {
                batch.setColor(1, 1, 1, 0.5f);
            }
            batch.draw(this.active, offsetX + x - this.active.getWidth()/2 + width/2 + activeX, offsetY - y + 3 + height + activeY);
            batch.setColor(Color.WHITE);
        }
        if (!draw) return;
        float baseR = batch.getColor().r;
        float baseG = batch.getColor().g;
        float baseB = batch.getColor().b;
        float baseAlpha = batch.getColor().a;
        batch.setColor(new Color(baseR, baseG, baseB, baseAlpha * alpha));
        float anglee = 0;
        if (angle == 1) {
            anglee -= 90;
        } else if (angle == 2) {
            anglee -= 180;
        } else if (angle == 3) {
            anglee -= 270;
        }

        if (anim != null) {
            TextureRegion texx = anim.getCurrentFrame(false);
            float yy;
            if (floor) yy = offsetY - y-floorHeight + z - texx.getRegionHeight()*scale/2;
            else yy = offsetY - y-floorHeight + z;
            float xx = offsetX+x;
            if (centered) xx = offsetX+ x - texx.getRegionWidth()*scale/2;
            batch.draw(texx, xx, yy);
            TextureRegion tex2 = maybeDrawChar(batch, xx, yy);
            if (tex2 != null) {
                if (floor) yy -= tex2.getRegionHeight()*scale/2 - texx.getRegionHeight()*scale/2;
                if (centered) xx -= tex2.getRegionWidth()*scale/2-texx.getRegionWidth()*scale/2;
                batch.draw(tex2, xx, yy);
            }
        } else if (tex != null) {
            float yy;
            if (floor) yy = offsetY - y-floorHeight + z - tex.getHeight()*scale/2;
            else yy = offsetY - y-floorHeight + z;
            float xx = offsetX+x;
            if (centered) xx = offsetX+ x - tex.getWidth()*scale/2;
            //batch.draw(tex, xx, yy, tex.getWidth()*scale, tex.getHeight()*scale);
            batch.draw(tex, xx, yy, tex.getWidth()/2, 0, tex.getWidth()*scale, tex.getHeight()*scale, 1.0f, 1.0f, anglee, 0, 0, tex.getWidth(), tex.getHeight(), false, false);
            TextureRegion tex2 = maybeDrawChar(batch, xx, yy);
            if (tex2 != null) {
                if (floor) yy -= tex2.getRegionHeight()*scale/2 - tex.getHeight()*scale/2;
                if (centered) xx -= tex2.getRegionWidth()*scale/2-tex.getWidth()*scale/2;
                batch.draw(tex2, xx, yy);
            }
        } else if (texR != null) {
            float yy = offsetY - y-floorHeight + z + offY;
            float xx = offsetX+x;
            if (centered) xx = offsetX+x - texR.getRegionWidth()*scale/2;
            batch.draw(texR, xx, yy, texR.getRegionWidth()/2, 0,
                    texR.getRegionWidth()*scale, texR.getRegionHeight()*scale, 1.0f, 1.0f, anglee-90, false);
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
