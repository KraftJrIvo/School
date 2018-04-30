package com.mygdx.schoolRPG.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by user on 15.07.2014.
 * joy!
 */

public class JoyStick {
    float joyX, joyY, stickWidth;
    boolean joyTouched = false;
    public Rectangle joyRect;
    Texture joy, joyBase;
    Color stickColor;
    int t = -1;

    public JoyStick(Rectangle rect, Texture baseTexture, Texture joyTexture, Color stickColor, float stickWidth) {
        this.joyRect = rect;
        this.joy = joyTexture;
        this.joyBase = baseTexture;
        joyX = joyRect.getX() + joyRect.getWidth()/2;
        joyY = joyRect.getY() + joyRect.getHeight()/2;
        this.stickColor = stickColor;
        this.stickWidth = stickWidth;
    }

    public void checkTouch() {

        if (t == -1 || !Gdx.input.isTouched(t)) {
            t = -1;
            for (int i =0; i<20; ++i) {
                if (Gdx.input.isTouched(i) && distToJoy(i) < (joyRect.width/2-20)) {
                    t = i;
                    break;
                }
            }
        }

        if (t>=0) {
            if (distToJoy(t) < joyRect.width/2) {
                joyTouched = true;
            }
        } else {
            joyTouched = false;
        }

        if (joyTouched) {
            if (distToJoy(t) > (joyRect.width/2)) {
                joyX = (float)(joyRect.x+joyRect.width/2 - (((joyRect.width/2-joyRect.width/8)/distToJoy(t)))*(joyRect.x+joyRect.width/2 - Gdx.input.getX(t)));
                joyY = (float)(joyRect.y+joyRect.height/2 - (((joyRect.width/2-joyRect.width/8)/distToJoy(t)))*(joyRect.y+joyRect.height/2 - Gdx.graphics.getHeight() + Gdx.input.getY(t)));
            } else {
                joyX = Gdx.input.getX(t);
                joyY = Gdx.graphics.getHeight() - Gdx.input.getY(t);
            }
        } else {
            joyX = joyRect.getX() + joyRect.getWidth()/2 + (joyX -joyRect.x-joyRect.width/2)/2;
            joyY = joyRect.getY() + joyRect.getHeight()/2 + (joyY -joyRect.y-joyRect.height/2)/2;
        }
    }

    public double joyOffsetX() {
        if (joyTouched) {
            return (joyX - joyRect.x-joyRect.width/2)/(joyRect.width/2);
        } else {
            return 0;
        }
    }

    public double joyOffsetY() {
        if (joyTouched) {
            return (joyY - joyRect.y-joyRect.height/2)/(joyRect.height/2);
        } else {
            return 0;
        }
    }


    public double distToJoy(int touchId) {
        return Math.hypot((double)(joyRect.x+joyRect.width/2 - Gdx.input.getX(touchId)), (double)(joyRect.y+joyRect.height/2 - Gdx.graphics.getHeight() + Gdx.input.getY(touchId)));
    }

    public void draw(SpriteBatch batch, ShapeRenderer renderer, float alpha) {
        batch.setColor(1,1,1,alpha);
        batch.begin();
        float halfWidth = joyRect.width / 2;
        batch.draw(joyBase, joyRect.x,
                joyRect.y, joyRect.width, joyRect.height);
        batch.end();
        Gdx.gl.glLineWidth(stickWidth);
        renderer.setColor(0, 0, 0, alpha);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        float halfHeight = joyRect.height / 2;
        if (alpha == 1.0f) {
            renderer.line(joyRect.x + halfWidth, joyRect.y + halfHeight, joyX, joyY);
        }
        renderer.end();
        batch.begin();
        batch.draw(joy, joyX-joyRect.width / 4, joyY-joyRect.width / 4, halfWidth + (float)joyOffsetX()/halfWidth, halfHeight+(float)joyOffsetX()/halfHeight);
        batch.end();
        batch.setColor(1,1,1,1);
    }

}
