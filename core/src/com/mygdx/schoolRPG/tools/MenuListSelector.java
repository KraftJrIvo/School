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
    int height;
    float xOffset;
    float yOffset;
    boolean centerAlign;
    float titlesGap;
    float centerX;
    float centerY;

    public MenuListSelector(ArrayList<String> titles, AssetManager assets, String cursor, BitmapFont font, int height, float xOffset, float yOffset, boolean centerAlign) {
        this.centerAlign = centerAlign;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.titles = titles;
        this.cursor = assets.get(cursor, Texture.class);
        this.font = font;
        this.height = height;
        titlesGap = font.getLineHeight();// * 2;
        centerX = Gdx.graphics.getWidth()/2;
        centerY = height/2;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void draw(SpriteBatch batch) {
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

        for (int i = 0; i < titles.size(); ++i) {
            if (centerAlign) {
                font.draw(batch, titles.get(i), xOffset + centerX - font.getBounds( titles.get(i)).width/2, yOffset + centerY - titlesGap * titles.size() / 2 + titlesGap * (titles.size() - i - 1));
            } else {
                font.draw(batch, titles.get(i), xOffset + centerX, yOffset + centerY - titlesGap * titles.size() / 2 + titlesGap * (titles.size() - i - 1));
            }
        }
        if (centerAlign) {
            batch.draw(new TextureRegion(cursor), xOffset + centerX - font.getBounds( titles.get(selectedIndex)).width/2 - 20, yOffset + centerY - titlesGap * titles.size() / 2 + titlesGap * (titles.size() - selectedIndex - 1) - cursor.getWidth(), 5.5f, 5.5f, 11, 11, 3, 3, cursorAngle, true);
        } else {
            batch.draw(new TextureRegion(cursor), xOffset + centerX - 20, yOffset + centerY - titlesGap * titles.size() / 2 + titlesGap * (titles.size() - selectedIndex - 1) - cursor.getWidth(), 5.5f, 5.5f, 11, 11, 3, 3, cursorAngle, true);
        }
        cursorAngle += 0.5f;
    }
}
