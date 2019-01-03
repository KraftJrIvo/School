package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.schoolRPG.menus.GameMenu;
import com.mygdx.schoolRPG.menus.Menu;
import com.mygdx.schoolRPG.tools.ConditionParser;
import com.mygdx.schoolRPG.tools.MenuListSelector;

import java.util.ArrayList;

/**
 * Created by Kraft on 10.06.2016.
 */
public class Choice extends Speech {
    MenuListSelector selector;
    int transitionId;
    String question;
    Menu parent;

    public Choice(Dialog dialog, int speakerId, String speaker, ArrayList<ArrayList<String>> phrases, AssetManager assets, String texPath, int charId, ArrayList<NPC> npcs, Player player, Menu parent, ConditionParser parser) {
        super(dialog, speakerId, speaker, phrases, assets, texPath, charId, -1, "", "", npcs, player, parent, parser);
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;
        //System.out.println();
        question = phrases.get(menu.currentLanguage).get(0);
        for (int i = 0; i < phrases.size(); ++i)
            phrases.get(i).remove(0);
        selector = new MenuListSelector(phrases, assets, "cursor.png", font, overlay.getHeight() - 20, 350, 175, false, parent);
        transitionId = -1;
        this.parent = parent;
        isSpeech = false;
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
        if (!paused && (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || (Gdx.input.justTouched() && Gdx.input.getY() > 300 && Gdx.input.getY() < 600 ))) {
            transitionId = selector.getSelectedIndex();
            finished = true;
        }
    }
}
