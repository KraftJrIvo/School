package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.Rectangle;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Kraft on 25.08.2015.
 */
public class LiquidSurface extends Entity{

    private float x, y;
    private int width;
    private float resistance, damping, spread, targetPosition;
    private ArrayList<Float> positions, velocities, accelerations, leftDeltas, rightDeltas;
    Texture tex;
    Rectangle rect;
    boolean full = false;
    LiquidType type;

    enum LiquidType { NONE, WATER, GOO};

    public LiquidSurface(AssetManager assets, float x, float y, int width, int tileWidth, LiquidType type, boolean full) {
        super(assets, (Texture)null, x, y, y, 0, 0);
        this.x = x;
        this.y = y;
        this.width = tileWidth*width;
        this.type = type;
        if (!full) {
            tex = assets.get("blank.png");
            targetPosition = tileWidth-6;
            if (type == LiquidType.WATER) {
                this.resistance = 0.025f;
                this.damping = 0.035f;
                this.spread = 0.05f;
            } else if (type == LiquidType.GOO) {
                this.resistance = 0.15f;
                this.damping = 0.16f;
                this.spread = 0.2f;
            } else {
                this.resistance = 0.025f;
                this.damping = 0.035f;
                this.spread = 0.5f;
            }
            if (damping < resistance) this.resistance = damping;
            positions = new ArrayList<Float>();
            velocities = new ArrayList<Float>();
            accelerations = new ArrayList<Float>();
            leftDeltas = new ArrayList<Float>();
            rightDeltas = new ArrayList<Float>();
            for (int i = 0; i < this.width; i++) {
                positions.add(0f);
                velocities.add(0f);
                accelerations.add(0f);
                leftDeltas.add(0f);
                rightDeltas.add(0f);
            }
            rect = new Rectangle(x, y, this.width, targetPosition);
        } else {
            tex = assets.get("blank2.png");
            targetPosition = tileWidth;
            rect = new Rectangle(x, y, this.width, targetPosition);
            this.full = full;
        }
    }

    public Rectangle getRect() {
        return rect;
    }

    public void splash(float x, float h) {
        if (full) return;
        int i = Math.round(x - this.x);
        if (i < width) {
            velocities.set(i, h);
        }
    }

    public void invalidate() {
        if (full) return;
        for (int i = 0; i < width; i++) {
            accelerations.set(i, -resistance * positions.get(i) - damping * velocities.get(i));
            positions.set(i, positions.get(i) + velocities.get(i));
            velocities.set(i, velocities.get(i) + accelerations.get(i));
        }
        for (int t = 0; t < 8; t++) {
            for (int i = 0; i < width; i++) {
                if (i > 0) {
                    leftDeltas.set(i, spread * (positions.get(i) - positions.get(i - 1)));
                    velocities.set(i - 1, velocities.get(i - 1) + leftDeltas.get(i));
                }
                if (i < width - 1) {
                    rightDeltas.set(i, spread * (positions.get(i) - positions.get(i + 1)));
                    velocities.set(i + 1, velocities.get(i + 1) + rightDeltas.get(i));
                }
            }
            for (int i = 0; i < width; i++) {
                if (i > 0) positions.set(i - 1, positions.get(i - 1) + leftDeltas.get(i));
                if (i < width-1) positions.set(i + 1, positions.get(i + 1) + rightDeltas.get(i));
            }
        }
    }

    public void draw(SpriteBatch batch, float xOffset, float yOffset) {
        //shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        //batch.setColor(0, 0, 1, 0.6f);
        if (type == LiquidType.WATER) {
            batch.setColor(0, 0, 1, 0.6f);
        } else if (type == LiquidType.GOO) {
            batch.setColor(0, 1, 0, 0.7f);
        } else {
            batch.setColor(0, 0, 1, 0.6f);
        }
        if (full) {
            //batch.draw(tex, (int)x + xOffset, (int)yOffset - y + 4, width, targetPosition);
            batch.draw(tex, 250 + x - 2, /*Gdx.graphics.getHeight()*/ 720 - y - 243, width + 4, targetPosition);
            //batch.draw(tex, xOffset + x, yOffset - y * 2, width + 4, targetPosition);
        } else {
            for (int i = 0; i < width; i++) {

                batch.draw(tex, 250 + i + x, /*Gdx.graphics.getHeight()*/720 - y - 243, 1, Math.round(Math.max(targetPosition + positions.get(i), 1)));
            }
        }
        batch.setColor(1, 1, 1, 1);
        //shapeRenderer.end();

    }
}
