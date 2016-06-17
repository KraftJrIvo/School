package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.schoolRPG.tools.MenuListSelector;

import java.util.ArrayList;

/**
 * Created by Kraft on 10.06.2016.
 */
public class Choice extends Speech {
    MenuListSelector selector;
    int transitionId;

    public Choice(String question, ArrayList<String> phrases, AssetManager assets, String texPath) {
        super(question, phrases, assets, texPath, -1, -1, -1, null);
        selector = new MenuListSelector(phrases, assets, "cursor.png", font, Gdx.graphics.getHeight(), -225, -155, false);
        transitionId = -1;
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, Gdx.graphics.getWidth()/3, texture.getHeight()/2);
        batch.draw(overlay, Gdx.graphics.getWidth()/2 - overlay.getWidth()/2.75f, overlay.getHeight()/1.25f, overlay.getWidth()/2, overlay.getHeight()/2);
        selector.draw(batch);
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            transitionId = selector.getSelectedIndex();
            finished = true;
        }
    }
}
