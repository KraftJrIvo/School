package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

/**
 * Created by Kraft on 10.06.2016.
 */
public class Speech {

    String speaker;
    ArrayList<String> phrases;
    ArrayList<Boolean> progress;
    Texture overlay, texture;
    BitmapFont font;
    int flagId = -1;
    int flagCharId = -1;
    NPC flagTarget;

    NPC target;
    int currentPhrase;
    boolean finished;
    long time;
    long curTime;
    long millsPerChar = 50;

    public Speech(String speaker, ArrayList<String> phrases, AssetManager assets, String texPath, int charId, int flagCharId, int flagId, ArrayList<NPC> npcs) {
        this.speaker = speaker;
        this.phrases = phrases;
        this.flagCharId = flagCharId;
        progress = new ArrayList<Boolean>();
        for (int i =0; i < phrases.size(); ++i) {
            progress.add(false);
        }
        texture = assets.get(texPath, Texture.class);
        overlay = assets.get("dialog_overlay2.png", Texture.class);
        font = new BitmapFont(Gdx.files.internal("font2.fnt"), Gdx.files.internal("font2.png"), false);
        this.flagId = flagId;
        if (npcs != null) {
            for (int i =0; i < npcs.size(); ++i) {
                if (npcs.get(i).charId == charId) {
                    target = npcs.get(i);
                    break;
                }
                if (npcs.get(i).charId == flagCharId) {
                    flagTarget = npcs.get(i);
                    break;
                }
            }
        }
        currentPhrase = 0;
        time = 0;
        curTime = 0;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, Gdx.graphics.getWidth()/3, texture.getHeight()/2);
        batch.draw(overlay, Gdx.graphics.getWidth()/2 - overlay.getWidth()/2.75f, overlay.getHeight()/1.25f, overlay.getWidth()/2, overlay.getHeight()/2);
        font.setColor(Color.WHITE);
        font.draw(batch, speaker, Gdx.graphics.getWidth()/2 - overlay.getWidth()/2.75f + 5, overlay.getHeight()/1.25f + overlay.getHeight()/10 * 5 - 3);
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            boolean ok = true;
            for (int i =0; i < phrases.size(); ++i) {
                if (!progress.get(i)) {
                    ok = false;
                    break;
                }
            }
            if (ok)  {

                if (flagId != -1) {
                    flagTarget.flags.set(flagId, true);
                }
                finished = true;
            } else {
                if (progress.get(currentPhrase)) {
                    time = 0;
                    currentPhrase++;
                } else {
                    progress.set(currentPhrase, true);
                }
            }
        }
        if (target != null) {
            font.setColor(target.charColor);
        }
        for (int i= 0; i < phrases.size(); ++i) {
            if (progress.get(i)) {
                font.draw(batch, phrases.get(i), Gdx.graphics.getWidth()/2 - overlay.getWidth()/2.75f + 5, overlay.getHeight()/1.25f + overlay.getHeight()/10 * (4 - i) - 3);
            } else {
                break;
            }
        }
        if (!progress.get(currentPhrase)) {
            if (time == 0) {
                time = System.currentTimeMillis();
            }
            curTime = System.currentTimeMillis() - time;
            int charCount = (int)Math.floor(curTime / millsPerChar);
            font.draw(batch, phrases.get(currentPhrase).substring(0, charCount), Gdx.graphics.getWidth()/2 - overlay.getWidth()/2.75f + 5, overlay.getHeight()/1.25f + overlay.getHeight()/10 * (4 - currentPhrase) - 3);
            if (charCount == phrases.get(currentPhrase).length()) {
                progress.set(currentPhrase, true);
            }
        }

        //batch.setColor(Color.WHITE);
    }
}
