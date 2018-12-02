package com.mygdx.schoolRPG.battleSystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Transition {

    Texture top;
    Texture bottom;
    boolean outwards = false;
    float speed = 0;
    float curY = 0;
    boolean closed = false;

    public Transition(Texture top, Texture bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    public void changeDirection(boolean outwards) {
        this.outwards = outwards;
    }

    public void draw(SpriteBatch batch) {
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;
        float h =  Gdx.graphics.getHeight()/screenRatioY;
        if (curY < 0) return;
        batch.draw(top, 0, h - curY, Gdx.graphics.getWidth(), h);
        batch.draw(bottom, 0, curY - h, Gdx.graphics.getWidth(), h);
        if (outwards && closed) {
            speed -= 0.5f;
        } else {
            speed += 0.5f;
        }
        curY += speed;
        if (curY > h) {
            curY = h;
            speed = -speed * 0.5f;
            if (Math.abs(speed) < 0.2f) {
                closed = true;
            }
        }
    }

    public void reset() {
        outwards = false;
        speed = 0;
        curY = 0;
        closed = false;
    }
}
