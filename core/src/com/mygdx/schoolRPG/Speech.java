package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.schoolRPG.menus.GameMenu;
import com.mygdx.schoolRPG.menus.Menu;

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
    boolean flagVal;
    int flagCharId = -1;
    NPC flagTarget;
    Menu menu;
    NPC target;
    int currentPhrase;
    boolean finished;
    long time;
    long curTime;
    long millsPerChar = 50;
    float screenRatioX, screenRatioY;
    Dialog dialog;
    int oldCharCount = 0, charCount = 0;
    ArrayList<NPC> npcs;
    int nextLineChars = 0;
    String itemToGive = "";
    int itemsToGiveCount = 0;
    int itemsToGiveTo = 0;
    Player player;

    public Speech(Dialog dialog, String speaker, ArrayList<String> phrases, AssetManager assets, String texPath, int charId, int flagCharId, int flagId, boolean flagVal, ArrayList<NPC> npcs, Player player, Menu menu) {
        this.menu = menu;
        this.npcs = npcs;
        this.dialog = dialog;
        this.speaker = speaker;
        this.phrases = phrases;
        this.flagCharId = flagCharId;
        this.flagVal = flagVal;
        this.player = player;
        progress = new ArrayList<Boolean>();
        for (int i =0; i < phrases.size(); ++i) {
            progress.add(false);
        }
        texture = assets.get(texPath, Texture.class);

        overlay = assets.get("dialog_overlay2.png", Texture.class);
        font = new BitmapFont(Gdx.files.internal("palatino24.fnt"), Gdx.files.internal("palatino24.png"), false);
        this.flagId = flagId;
        if (charId == 0) {
            target = player;
        }
        if (npcs != null) {
            for (int i =0; i < npcs.size(); ++i) {
                if (npcs.get(i).charId == charId) {
                    target = npcs.get(i);
                    //break;
                }
                if (npcs.get(i).charId == flagCharId) {
                    flagTarget = npcs.get(i);
                    //break;
                }
            }
        }
        currentPhrase = 0;
        time = 0;
        curTime = 0;
        for (int i = 0; i < phrases.size(); ++i) {
            String phrase = phrases.get(i);
            if (phrase.charAt(0) == ']' && phrase.charAt(1) == ';' && phrase.charAt(2) == '[') {
                boolean numbersStarted = false;
                boolean receiverStarted = false;
                String numba = "";
                String numba2 = "";
                for (int j = 3; j < phrase.length(); ++j) {
                    if (phrase.charAt(j) == ';') {
                        if (numbersStarted) {
                            receiverStarted = true;
                            j++;
                        } else {
                            numbersStarted = true;
                            j++;
                        }
                    }
                    if (numbersStarted) {
                        if (receiverStarted) {
                            numba2 += phrase.charAt(j);
                        } else {
                            numba += phrase.charAt(j);
                        }
                    } else {
                        itemToGive += phrase.charAt(j);
                    }
                }
                itemsToGiveCount = Integer.parseInt(numba);
                itemsToGiveTo = Integer.parseInt(numba2);
                phrases.remove(phrase);
            }
        }
    }

    public void draw(SpriteBatch batch, boolean paused) {
        float screenRatioX = Gdx.graphics.getWidth()/1280.0f;
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;
        float textX = Gdx.graphics.getWidth()/screenRatioX/2 - overlay.getWidth() /2 + 10;
        float textY = Gdx.graphics.getHeight()/screenRatioY/8 + overlay.getHeight() - 12;
        //System.out.println(screenRatioX + " " + screenRatioY);
        batch.draw(texture, Gdx.graphics.getWidth()/screenRatioX/2 - texture.getWidth(), 0, texture.getWidth() * 2, texture.getHeight() * 2);
        batch.draw(overlay, Gdx.graphics.getWidth()/screenRatioX/2 - overlay.getWidth() /2, Gdx.graphics.getHeight()/screenRatioY/8);
        if (target != null) {
            font.setColor(target.charColor);
        }
        font.draw(batch, speaker, textX, textY);
        font.setColor(Color.WHITE);
        if (!paused && (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER))) {
            boolean ok = true;
            for (int i =0; i < phrases.size(); ++i) {
                if (!progress.get(i)) {
                    ok = false;
                    break;
                }
            }
            if (ok)  {

                if (flagId != -1) {
                    flagTarget.flags.set(flagId, flagVal);
                    dialog.changedFlagsNames.add(flagTarget.flagNames.get(flagId));
                    dialog.changedFlagsVals.add(flagVal);
                }
                finished = true;
                target.removeItems(itemToGive, itemsToGiveCount);
                World world = ((GameMenu)dialog.parent).worlds.get(((GameMenu)dialog.parent).curWorld);
                for (int i = 0; i < itemsToGiveCount; ++i) {
                    if (itemsToGiveTo == 0) {
                        player.takeItem(new Item(world.assets, world.folderPath, itemToGive));
                    } else {
                        npcs.get(itemsToGiveTo-1).takeItem(new Item(world.assets, world.folderPath, itemToGive));
                    }
                }

            } else {
                if (progress.get(currentPhrase)) {
                    time = 0;
                    currentPhrase++;
                } else {
                    progress.set(currentPhrase, true);
                }
            }
            nextLineChars = 0;
        }
        boolean roundBracketsStarted = false; //()
        boolean squareBracketsStarted = false;//[]
        boolean curlyBracketsStarted = false;//{}
        boolean shakingTextStarted = false;//@@
        int rainbowTextStarted = -1;//##
        int nextLineStarted = 0;
        for (int i= 0; i < phrases.size(); ++i) {
            if (!progress.get(i)) {
                nextLineStarted++;
            }
            if (progress.get(i) || nextLineStarted == 1) {
                float phraseOffset = 0;
                int to;
                if (nextLineStarted == 1) {
                    to = nextLineChars;
                } else {
                    to = phrases.get(i).length();
                }
                for (int j= 0; j < to; ++j) {
                    char ch = phrases.get(i).charAt(j);
                    String curChar = ""+ch;
                    switch (ch) {
                        case '(':
                            roundBracketsStarted = true;
                            break;
                        case '[':
                            squareBracketsStarted = true;
                            break;
                        case '{':
                            curlyBracketsStarted = true;
                            break;
                        case '@':
                            curChar = "";
                            shakingTextStarted = !shakingTextStarted;
                            break;
                        case '#':
                            curChar = "";
                            if (rainbowTextStarted == -1) {
                                rainbowTextStarted = j+1;
                            } else {
                                rainbowTextStarted = -1;
                            }
                            break;
                    }
                    if (roundBracketsStarted) {
                        font.setColor(new Color(0.251f, 0.878f, 0.816f, 1.0f));
                    } else if (squareBracketsStarted) {
                        font.setColor(new Color(0.502f, 0.941f, 0.502f, 1.0f));
                    } else if (curlyBracketsStarted) {
                        font.setColor(new Color(0.941f, 0.502f, 0.502f, 1.0f));
                    } else if (rainbowTextStarted != -1) {
                        switch ((j - rainbowTextStarted) % 7) {
                            case 0:
                                font.setColor(Color.RED);
                                break;
                            case 1:
                                font.setColor(new Color(1.000f, 0.549f, 0.000f, 1.0f));
                                break;
                            case 2:
                                font.setColor(Color.YELLOW);
                                break;
                            case 3:
                                font.setColor(Color.GREEN);
                                break;
                            case 4:
                                font.setColor(Color.CYAN);
                                break;
                            case 5:
                                font.setColor(new Color(0.255f, 0.412f, 0.882f, 1.0f));
                                break;
                            case 6:
                                font.setColor(new Color(0.600f, 0.196f, 0.800f, 1.0f));
                                break;
                        }
                    } else {
                        font.setColor(Color.WHITE);
                    }
                    float randomXoffset = 0;
                    float randomYoffset = 0;
                    if (shakingTextStarted) {
                        randomXoffset = (float)Math.random() * 2 - 1.0f;
                        randomYoffset = (float)Math.random() * 2 - 1.0f;
                    }
                    font.draw(batch, curChar, textX + phraseOffset + randomXoffset, textY - 32 * (i+1) - 10 + randomYoffset);
                    phraseOffset += font.getBounds(curChar).width;
                    switch (ch) {
                        case ')':
                            roundBracketsStarted = false;
                            break;
                        case ']':
                            squareBracketsStarted = false;
                            break;
                        case '}':
                            curlyBracketsStarted = false;
                            break;
                    }
                }
                //font.draw(batch, phrases.get(i), textX, textY - 32 * (i+1) - 10);
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
        oldCharCount = charCount;
        charCount = (int)Math.floor(curTime / millsPerChar);

        if (charCount != oldCharCount && charCount > 0 && phrases.get(currentPhrase).charAt(charCount - 1) != ' ') {
            float volume = 1.0f;
            if (roundBracketsStarted || squareBracketsStarted || curlyBracketsStarted) {
                volume = 0.5f;
            }
            target.speechSound.play(volume * menu.soundVolume/100.0f);
        }
        int lineChars = phrases.get(currentPhrase).substring(0, Math.min(charCount, phrases.get(currentPhrase).length()-1)).length();
        if (!progress.get(currentPhrase) && lineChars > 0) {
            nextLineChars = lineChars;
        }
        if (charCount == phrases.get(currentPhrase).length()) {
            progress.set(currentPhrase, true);
            nextLineChars = 0;
        }
        //font.draw(batch, phrases.get(currentPhrase).substring(0, Math.min(charCount, phrases.get(currentPhrase).length()-1)), textX, textY - 32 * (currentPhrase+1) - 10);
        //batch.setColor(Color.WHITE);
    }
}
