package com.mygdx.schoolRPG.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

/**
 * Created by Kraft on 30.08.2016.
 */
public class CircularSelector {
    ArrayList<String> titles;
    ArrayList<Texture> sprites;
    ArrayList<Float> ys;
    Array<Integer> ids;
    BitmapFont font;
    int selectedIndex = 0;
    float scale;
    float titlesGap;
    float centerX;
    float centerY;
    float currentAngle, targetAngle, prevAngle;
    boolean isAtTarget;
    float angleStep, width, height;
    String curTitle;
    float fontAlpha;


    public CircularSelector(ArrayList<String> titles, ArrayList<Texture> sprites, BitmapFont font, float centerX, float centerY, float width, float height, float scale) {
        this.titles = titles;
        this.sprites = sprites;
        this.font = font;
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width/2.0f;
        this.height = height/2.0f;
        this.scale = scale;
        currentAngle = 0;
        targetAngle = 0;
        prevAngle = 0;
        ys = new ArrayList<Float>();
        ids = new Array<Integer>();
        for (int i = 0; i < sprites.size(); ++i) {
            ys.add(0.0f);
            ids.add(0);
        }
        curTitle = titles.get(selectedIndex);
        fontAlpha = 1.0f;
    }

    public void addItem(Texture sprite, String title) {
        sprites.add(sprite);
        titles.add(title);
        currentAngle = 0;
        targetAngle = 0;
        prevAngle = 0;
        selectedIndex = 0;
        ys.add(0.0f);
        ids.add(0);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void draw(SpriteBatch batch, boolean paused) {
        float screenRatioX = Gdx.graphics.getWidth()/1280.0f;
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;
        isAtTarget = (Math.abs(Math.sin(targetAngle) - Math.sin(currentAngle)) < 0.01f) && (Math.abs(Math.cos(targetAngle) - Math.cos(currentAngle)) < 0.01f);
        if (isAtTarget) {
            currentAngle = targetAngle;
        }
        if (!paused && isAtTarget && sprites.size() > 1) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                prevAngle = currentAngle;
                targetAngle += 2.0f * (float)Math.PI / sprites.size();
                isAtTarget = false;
                selectedIndex--;
                if (selectedIndex < 0) {
                    selectedIndex = sprites.size()-1;
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                prevAngle = currentAngle;
                targetAngle -= 2.0f * (float)Math.PI / sprites.size();
                isAtTarget = false;
                selectedIndex++;
                if (selectedIndex >= sprites.size()) {
                    selectedIndex = 0;
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            addItem(sprites.get(0), "hah");
        }


        if (!isAtTarget) {
            float top = Math.max(Math.abs(prevAngle - currentAngle), 0.01f);
            float bot = Math.max(Math.abs(prevAngle - targetAngle), 0.01f);
            angleStep = (1.01f - top/bot)/sprites.size()*1.5f;
            if (currentAngle < targetAngle) {
                currentAngle += angleStep;
            } else {
                currentAngle -= angleStep;
            }
            if (top/bot < 0.5f) {
                fontAlpha = 1.0f - top/bot * 2.0f;
            } else {
                fontAlpha = top/bot * 2.0f;
                curTitle = titles.get(selectedIndex);
            }
        }


        for (int i = 0; i < ys.size(); ++i) {
            float angle = -(float)Math.PI/2 + i * (2.0f*(float)Math.PI/sprites.size()) + currentAngle;
            float y = centerY + height * (float)Math.sin(angle) - scale * sprites.get(i).getHeight()/2.0f;
            ys.set(i, y);
            ids.set(i, i);
        }
        for (int i = 0; i < ys.size(); ++i) {
            for (int j = ys.size() - 2; j >= 0; --j) {
                if (ys.get(j) < ys.get(j + 1)) {
                    float tmp1 = ys.get(j);
                    ys.set(j, ys.get(j+1));
                    ys.set(j + 1, tmp1);
                    int tmp2 = ids.get(j);
                    ids.set(j, ids.get(j+1));
                    ids.set(j + 1, tmp2);
                }
            }
        }

        for (int i = 0; i < ys.size(); ++i) {
            float angle = -(float)Math.PI/2 + ids.get(i) * (2.0f*(float)Math.PI/sprites.size()) + currentAngle;
            float x = centerX + width * (float)Math.cos(angle) - scale * sprites.get(ids.get(i)).getWidth()/2.0f;
            float alpha = Math.max(-((float)Math.sin(angle) - 1.0f)/2.0f, 0.1f);
            batch.setColor(new Color(1.0f,1.0f,1.0f,alpha));
            batch.draw(sprites.get(ids.get(i)), x, ys.get(i), sprites.get(ids.get(i)).getWidth()*scale, sprites.get(ids.get(i)).getHeight()*scale);
            batch.setColor(Color.WHITE);
        }
        font.setColor(new Color(1.0f,1.0f,1.0f,fontAlpha));
        font.draw(batch, titles.get(selectedIndex), centerX - font.getBounds(titles.get(selectedIndex)).width/2.0f, centerY - sprites.get(ids.get(0)).getHeight()/1.5f*scale);
        font.setColor(Color.WHITE);
    }
}
