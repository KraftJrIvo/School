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
    float screenRatioX, screenRatioY;

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
        //font = new BitmapFont(Gdx.files.internal("palatino12.fnt"), Gdx.files.internal("palatino12.png"), false);
        font = new BitmapFont(Gdx.files.internal("palatino24.fnt"), Gdx.files.internal("palatino24.png"), false);
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

    public void draw(SpriteBatch batch, boolean paused) {
        float screenRatioX = Gdx.graphics.getWidth()/1280.0f;
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;
        float textX = Gdx.graphics.getWidth()/screenRatioX/2 - overlay.getWidth() /2 + 10;
        float textY = Gdx.graphics.getHeight()/screenRatioY/8 + overlay.getHeight() - 12;
        //System.out.println(screenRatioX + " " + screenRatioY);
        batch.draw(texture, Gdx.graphics.getWidth()/screenRatioX/2 - texture.getWidth(), 0, texture.getWidth() * 2, texture.getHeight() * 2);
        batch.draw(overlay, Gdx.graphics.getWidth()/screenRatioX/2 - overlay.getWidth() /2, Gdx.graphics.getHeight()/screenRatioY/8);
        font.setColor(Color.WHITE);
        font.draw(batch, speaker, textX, textY);
        if (!paused && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
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
                font.draw(batch, phrases.get(i), textX, textY - 32 * (i+1) - 10);
            } else {
                break;
            }
        }
        if (paused) {
            time = System.currentTimeMillis() - curTime;
        } else if (!progress.get(currentPhrase)) {
            if (time == 0) {
                time = System.currentTimeMillis();
            }
            curTime = System.currentTimeMillis() - time;
        }
        int charCount = (int)Math.floor(curTime / millsPerChar);
        if (charCount == phrases.get(currentPhrase).length()) {
            progress.set(currentPhrase, true);
        }
        font.draw(batch, phrases.get(currentPhrase).substring(0, charCount), textX, textY - 32 * (currentPhrase+1) - 10);
        //batch.setColor(Color.WHITE);
    }
}
