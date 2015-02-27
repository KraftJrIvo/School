package com.mygdx.schoolRPG.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by user on 15.07.2014.
 */
public class Button {

    Texture img;
    Rectangle rect;
    boolean wasPressed = false, released = false;

    public Button(Rectangle rect, Texture img) {
        this.rect = rect;
        this.img = img;
    }

    private boolean containsMouse() {
        return rect.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
    }

    public boolean checkTouch() {
        //System.out.println(wasPressed +" "+ containsMouse() +" "+ !Gdx.input.isTouched());
        if (wasPressed && containsMouse() && !Gdx.input.isTouched()) {
            wasPressed = false;
            return true;
        }
        if (Gdx.input.isTouched() && containsMouse()) {
            wasPressed = true;
        }
        if (!containsMouse() || !Gdx.input.isTouched()) {
                wasPressed = false;
        }
        return false;
    }

    public void draw(SpriteBatch batch) {
        batch.begin();
        //checkTouch();
        if (wasPressed) {
            //batch.setColor(0.06f, 0.93f, 0.06f, 1);
            batch.draw(img, rect.x + Gdx.graphics.getWidth()/40, rect.y + Gdx.graphics.getWidth()/40, rect.width - Gdx.graphics.getWidth()/20, rect.height - Gdx.graphics.getWidth()/20);
        } else {
            //batch.setColor(0.93f, 0.49f, 0.06f, 1);
            batch.draw(img, rect.x, rect.y, rect.width, rect.height);
        }
        batch.setColor(1, 1, 1, 1);
        batch.end();
    }

}
