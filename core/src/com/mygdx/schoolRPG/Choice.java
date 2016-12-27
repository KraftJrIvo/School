package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.schoolRPG.tools.MenuListSelector;

import java.util.ArrayList;

/**
 * Created by Kraft on 10.06.2016.
 */
public class Choice extends Speech {
    MenuListSelector selector;
    int transitionId;
    String question;

    public Choice(String speaker, ArrayList<String> phrases, AssetManager assets, String texPath, int charId, ArrayList<NPC> npcs) {
        super(speaker, phrases, assets, texPath, charId, -1, -1, false, npcs);
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;
        System.out.println();
        question = phrases.get(0);
        phrases.remove(0);
        selector = new MenuListSelector(phrases, assets, "cursor.png", font, overlay.getHeight() - 20, 350, 175, false);
        transitionId = -1;
    }

    @Override
    public void draw(SpriteBatch batch, boolean paused) {
        float screenRatioX = Gdx.graphics.getWidth()/1280.0f;
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;
        batch.draw(texture, Gdx.graphics.getWidth()/screenRatioX/2 - texture.getWidth(), 0, texture.getWidth() * 2, texture.getHeight() * 2);
        batch.draw(overlay, Gdx.graphics.getWidth()/screenRatioX/2 - overlay.getWidth() /2, Gdx.graphics.getHeight()/screenRatioY/8);
        float textX = Gdx.graphics.getWidth()/screenRatioX/2 - overlay.getWidth() /2 + 10;
        float textY = Gdx.graphics.getHeight()/screenRatioY/8 + overlay.getHeight() - 12;
        if (target != null) {
            font.setColor(target.charColor);
        }
        font.draw(batch, speaker, textX, textY);
        font.setColor(Color.WHITE);
        font.draw(batch, question, textX, textY - 42);
        selector.draw(batch, paused);
        if (!paused && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            transitionId = selector.getSelectedIndex();
            finished = true;
        }
    }
}
