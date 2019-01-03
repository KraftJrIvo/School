package com.mygdx.schoolRPG.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.schoolRPG.menus.Menu;

import java.util.ArrayList;

/**
 * Created by Kraft on 09.06.2016.
 */
public class MenuListSelector {
    public ArrayList<ArrayList<String>> titles;
    Texture cursor;
    BitmapFont font;
    int selectedIndex = 0;
    float cursorAngle = 0;
    public float height;
    public float xOffset;
    public float yOffset;
    boolean centerAlign;
    float titlesGap;
    float centerX;
    float centerY;
    public boolean enabled;
    public boolean looping;
    Menu parent;
    Sound click1;

    public MenuListSelector(ArrayList<ArrayList<String>> titles, AssetManager assets, String cursor, BitmapFont font, int height, float xOffset, float yOffset, boolean centerAlign, Menu parent) {
        this.centerAlign = centerAlign;
        this.titles = titles;
        this.cursor = assets.get(cursor, Texture.class);
        this.font = font;
        float screenRatioX = Gdx.graphics.getWidth()/1280.0f;
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;
        this.height = height;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        titlesGap = font.getLineHeight();// * 2;
        enabled = true;
        looping = true;
        this.parent = parent;
        click1 = assets.get("menu_click_2.wav", Sound.class);
    }

    public void setFont(BitmapFont font) {
        this.font = font;
    }

    public void addItem(String item) {
        titles.get(parent.currentLanguage).add(item);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        selectedIndex = index;
    }

    public void draw(SpriteBatch batch, boolean paused) {
        float screenRatioX = Gdx.graphics.getWidth()/1280.0f;
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;
        //centerY = height/screenRatioY;
        centerY = height/2/screenRatioY;
        //System.out.println(centerY);
        centerX = Gdx.graphics.getWidth()/screenRatioX/2;
        if (!paused && enabled) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || (Gdx.input.justTouched() && Gdx.input.getX() > 500 && Gdx.input.getY() < 500)) {
                selectedIndex++;
                click1.play(parent.soundVolume / 100.0f);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || (Gdx.input.justTouched() && Gdx.input.getX() > 500 && Gdx.input.getY() > 500))  {
                selectedIndex--;
                click1.play(parent.soundVolume / 100.0f);
            }
            if (selectedIndex < 0) {
                selectedIndex = titles.get(parent.currentLanguage).size() - 1;
                if (!looping) {
                    enabled = false;
                }
            } else if (selectedIndex > titles.get(parent.currentLanguage).size() - 1) {
                selectedIndex = 0;
                if (!looping) {
                    enabled = false;
                }
            }
            cursorAngle += 0.5f;
        }

        for (int i = 0; i < titles.get(parent.currentLanguage).size(); ++i) {
            if (centerAlign) {
                font.draw(batch, titles.get(parent.currentLanguage).get(i), xOffset + centerX - font.getBounds( titles.get(parent.currentLanguage).get(i)).width/2, yOffset + centerY - titlesGap * titles.get(parent.currentLanguage).size() / 2 + titlesGap * (titles.get(parent.currentLanguage).size() - i - 1));
            } else {
                font.draw(batch, titles.get(parent.currentLanguage).get(i), xOffset, yOffset - titlesGap * titles.get(parent.currentLanguage).size() / 2 + titlesGap * (titles.size() - i - 1));
            }
        }
        if (enabled) {
            if (centerAlign) {
                batch.draw(new TextureRegion(cursor), xOffset + centerX - font.getBounds( titles.get(parent.currentLanguage).get(selectedIndex)).width/2 - 20, yOffset + centerY - titlesGap * titles.get(parent.currentLanguage).size() / 2 + titlesGap * (titles.get(parent.currentLanguage).size() - selectedIndex - 1) - cursor.getWidth(), 5.5f, 5.5f, 11, 11, 3, 3, cursorAngle, true);
                batch.draw(new TextureRegion(cursor), xOffset + centerX + font.getBounds( titles.get(parent.currentLanguage).get(selectedIndex)).width/2 + 10, yOffset + centerY - titlesGap * titles.get(parent.currentLanguage).size() / 2 + titlesGap * (titles.get(parent.currentLanguage).size() - selectedIndex - 1) - cursor.getWidth(), 5.5f, 5.5f, 11, 11, 3, 3, -cursorAngle, true);
            } else {
                batch.draw(new TextureRegion(cursor), xOffset - 20, yOffset - titlesGap * titles.get(parent.currentLanguage).size() / 2 + titlesGap * (titles.get(parent.currentLanguage).size() - (selectedIndex + (Math.max(titles.get(0).size() - 2, 0))) - 1) - cursor.getWidth(), 5.5f, 5.5f, 11, 11, 3, 3, cursorAngle, true);
            }
        }
    }
}
