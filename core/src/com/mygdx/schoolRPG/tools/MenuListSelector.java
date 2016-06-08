package com.mygdx.schoolRPG.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

    public MenuListSelector(ArrayList<String> titles, Texture cursor, BitmapFont font) {
        this.titles = titles;
        this.cursor = cursor;
        this.font = font;
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

        float titlesGap = font.getLineHeight() * 2;
        float centerX = Gdx.graphics.getWidth()/2;
        float centerY = Gdx.graphics.getHeight()/2;
        for (int i = 0; i < titles.size(); ++i) {
            font.draw(batch, titles.get(i), centerX - font.getBounds( titles.get(i)).width/2, centerY - titlesGap * titles.size() / 2 + titlesGap * (titles.size() - i - 1));
        }
        batch.draw(new TextureRegion(cursor), centerX - font.getBounds( titles.get(selectedIndex)).width/2 - 32, centerY - titlesGap * titles.size() / 2 + titlesGap * (titles.size() - selectedIndex - 1) - 4 * cursor.getWidth()/2, 5.5f, 5.5f, 11, 11, 5, 5, cursorAngle, true);
        cursorAngle += 0.1f;
    }
}
