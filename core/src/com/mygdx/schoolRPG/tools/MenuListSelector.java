package com.mygdx.schoolRPG.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

/**
 * Created by Kraft on 09.06.2016.
 */
public class MenuListSelector {
    ArrayList<String> titles;
    Texture cursor;
    BitmapFont font;
    int selectedIndex = 0;
    float cursorAngle = 0;
    float height;
    float xOffset;
    float yOffset;
    boolean centerAlign;
    float titlesGap;
    float centerX;
    float centerY;

    public MenuListSelector(ArrayList<String> titles, AssetManager assets, String cursor, BitmapFont font, int height, float xOffset, float yOffset, boolean centerAlign) {
        this.centerAlign = centerAlign;
        this.titles = titles;
        this.cursor = assets.get(cursor, Texture.class);
        this.font = font;
        float screenRatioX = Gdx.graphics.getWidth()/1280.0f;
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;
        this.height = height/screenRatioY;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        titlesGap = font.getLineHeight();// * 2;
    }

    public void addItem(String item) {
        titles.add(item);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void draw(SpriteBatch batch, boolean paused) {
        float screenRatioX = Gdx.graphics.getWidth()/1280.0f;
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;
        //centerY = height/screenRatioY;
        centerY = height/2;
        //System.out.println(centerY);
        centerX = Gdx.graphics.getWidth()/screenRatioX/2;
        if (!paused) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                selectedIndex++;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP))  {
                selectedIndex--;
            }
            if (selectedIndex < 0) {
                selectedIndex = titles.size()-1;
            } else if (selectedIndex > titles.size() - 1) {
                selectedIndex = 0;
            }
            cursorAngle += 0.5f;
        }

        for (int i = 0; i < titles.size(); ++i) {
            if (centerAlign) {
                font.draw(batch, titles.get(i), xOffset + centerX - font.getBounds( titles.get(i)).width/2, yOffset + centerY - titlesGap * titles.size() / 2 + titlesGap * (titles.size() - i - 1));
            } else {
                font.draw(batch, titles.get(i), xOffset, yOffset - titlesGap * titles.size() / 2 + titlesGap * (titles.size() - i - 1));
            }
        }
        if (centerAlign) {
            batch.draw(new TextureRegion(cursor), xOffset + centerX - font.getBounds( titles.get(selectedIndex)).width/2 - 20, yOffset + centerY - titlesGap * titles.size() / 2 + titlesGap * (titles.size() - selectedIndex - 1) - cursor.getWidth(), 5.5f, 5.5f, 11, 11, 3, 3, cursorAngle, true);
        } else {
            batch.draw(new TextureRegion(cursor), xOffset - 20, yOffset - titlesGap * titles.size() / 2 + titlesGap * (titles.size() - selectedIndex - 1) - cursor.getWidth(), 5.5f, 5.5f, 11, 11, 3, 3, cursorAngle, true);
        }
    }
}
