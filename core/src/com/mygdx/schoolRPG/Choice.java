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
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;
        System.out.println();
        selector = new MenuListSelector(phrases, assets, "cursor.png", font, overlay.getHeight() - 20, 350, 190, false);
        transitionId = -1;
    }

    @Override
    public void draw(SpriteBatch batch, boolean paused) {
        float screenRatioX = Gdx.graphics.getWidth()/1280.0f;
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;
        batch.draw(texture, Gdx.graphics.getWidth()/screenRatioX/2 - texture.getWidth(), 0, texture.getWidth() * 2, texture.getHeight() * 2);
        batch.draw(overlay, Gdx.graphics.getWidth()/screenRatioX/2 - overlay.getWidth() /2, Gdx.graphics.getHeight()/screenRatioY/8);
        selector.draw(batch, paused);
        if (!paused && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            transitionId = selector.getSelectedIndex();
            finished = true;
        }
    }
}
