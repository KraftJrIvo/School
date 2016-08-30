package com.mygdx.schoolRPG.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

/**
 * Created by Kraft on 30.08.2016.
 */
public class CircularSelector {
    ArrayList<String> titles;
    ArrayList<Texture> sprites;
    BitmapFont font;
    int selectedIndex = 0;
    float scale;
    float titlesGap;
    float centerX;
    float centerY;
    float currentAngle, targetAngle, prevAngle;
    boolean isAtTarget;
    float angleStep;

    public CircularSelector(ArrayList<String> titles, ArrayList<Texture> sprites, BitmapFont font, float centerX, float centerY, float scale) {
        this.titles = titles;
        this.sprites = sprites;
        this.font = font;
        this.centerX = centerX;
        this.centerY = centerY;
        this.scale = scale;
        currentAngle = 0;
        targetAngle = 0;
        prevAngle = 0;
    }

    public void addItem(Texture sprite, String title) {
        sprites.add(sprite);
        titles.add(title);
        currentAngle = 0;
        targetAngle = 0;
        prevAngle = 0;
        selectedIndex = 0;
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
                selectedIndex++;
                if (selectedIndex >= sprites.size()) {
                    selectedIndex = 0;
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                prevAngle = currentAngle;
                targetAngle -= 2.0f * (float)Math.PI / sprites.size();
                isAtTarget = false;
                selectedIndex--;
                if (selectedIndex < 0) {
                    selectedIndex = sprites.size();
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
        }
        for (int i = 0; i < sprites.size(); ++i) {
            float angle = -(float)Math.PI/2 + i * (2.0f*(float)Math.PI/sprites.size()) + currentAngle;
            float x = centerX + ((float)Math.cos(angle) - sprites.get(i).getWidth()/20.0f) * scale;
            float y = centerY + (float)Math.sin(angle) * scale / 2.0f;
            float alpha = Math.max(-((float)Math.sin(angle) - 1.0f)/2.0f, 0.1f);
            batch.setColor(new Color(1.0f,1.0f,1.0f,alpha));
            batch.draw(sprites.get(i), x, y, sprites.get(i).getWidth()*scale/10.0f, sprites.get(i).getHeight()*scale/10.0f);
            batch.setColor(Color.WHITE);
        }
    }
}
