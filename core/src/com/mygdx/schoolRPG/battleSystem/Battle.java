package com.mygdx.schoolRPG.battleSystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.schoolRPG.*;
import com.mygdx.schoolRPG.battleSystem.ui.FriendlyUnitsDrawGroup;
import com.mygdx.schoolRPG.battleSystem.ui.UnitsDrawGroup;
import com.mygdx.schoolRPG.tools.CircularSelector;
import com.mygdx.schoolRPG.tools.MenuListSelector;

import javax.xml.soap.Text;
import java.security.Key;
import java.util.ArrayList;

import static com.mygdx.schoolRPG.Area.SCREEN_HEIGHT;
import static com.mygdx.schoolRPG.Area.SCREEN_WIDTH;

public class Battle {

    Background bg;
    Sound ambient;
    long ambientId;
    String ambientPath;
    ArrayList<Unit> units;
    ArrayList<Unit> playersUnits;
    UnitsDrawGroup enemyGroup, friendlyGroup;
    BitmapFont palatino24;
    Texture divider;
    Transition trans;
    Texture shade;
    Texture statsBarEdge, statsBar;
    Unit curUnit;
    ArrayList<Float> HUDOffsetsX, HUDOffsetsY;
    public boolean finished = false;
    public boolean finishedBattle = false;
    public boolean playerWon = false;
    public boolean loaded = false;
    public boolean initialized = false;
    MenuListSelector turnOptionsList, unitMovesList;
    ArrayList<Unit> turnOrder;
    boolean showTurnOptions = false, showMovesList = false;
    float turnOptionsY = 0, movesListY = -1000.0f;
    public ArrayList<Item> drops;
    CircularSelector dropsView;
    MenuListSelector endOptionsList;

    ArrayList<Float> HUDshakes, HUDspeedsX, HUDspeedsY;
    ArrayList<Float> tempHps, tempAps, tempXps;
    ArrayList<Integer> tempLvls, tempNextLvlXps, tempPrevLvlXps;
    long finishedTime;

    ArrayList<Integer> expPools;

    public ObjectCell triggerCell;

    public Battle(World w, ArrayList<Unit> units, ArrayList<Unit> playersUnits, String ambientPath) {
        this.units = units;
        this.playersUnits = playersUnits;
        this.ambientPath = ambientPath;
        trans = new Transition(w.assets.get(w.worldDir.path() + "/bg/battle_trans_top.png", Texture.class), w.assets.get(w.worldDir.path() + "/bg/battle_trans_bottom.png", Texture.class));
        palatino24 = new BitmapFont(Gdx.files.internal("palatino24.fnt"), Gdx.files.internal("palatino24.png"), false);
        HUDshakes = new ArrayList<Float>();
        HUDspeedsX = new ArrayList<Float>();
        HUDspeedsY = new ArrayList<Float>();
        tempHps = new ArrayList<Float>();
        tempAps = new ArrayList<Float>();
        tempXps = new ArrayList<Float>();
        expPools = new ArrayList<Integer>();
        tempLvls = new ArrayList<Integer>();
        tempNextLvlXps = new ArrayList<Integer>();
        tempPrevLvlXps = new ArrayList<Integer>();
        for (int i = 0; i < playersUnits.size(); ++i)
        {
            HUDshakes.add(0f);
            HUDspeedsX.add(0f);
            HUDspeedsY.add(0f);
            tempHps.add((float)playersUnits.get(i).stats.hp);
            tempAps.add((float)playersUnits.get(i).stats.ap);
            tempXps.add((float)playersUnits.get(i).stats.exp);
            tempLvls.add(playersUnits.get(i).stats.level);
            tempNextLvlXps.add(playersUnits.get(i).stats.nextLvlExp);
            tempPrevLvlXps.add(playersUnits.get(i).stats.prevLvlExp);
            expPools.add(0);
        }

    }

    public void load(World w) {
        w.assets.load(w.worldDir.path() + "/bg/battle_bg.png", Texture.class);
        w.assets.load(w.worldDir.path() + "/bg/battle_divider.png", Texture.class);
        w.assets.load(w.worldDir.path() + "/sounds/" + ambientPath, Sound.class);
        ArrayList<String> uniqueUnitNames = new ArrayList<String>();
        HUDOffsetsX = new ArrayList<Float>();
        HUDOffsetsY = new ArrayList<Float>();
        turnOrder = new ArrayList<Unit>();
        for (int i = 0; i < playersUnits.size(); ++i) {
            if (!uniqueUnitNames.contains(playersUnits.get(i).name)) {
                uniqueUnitNames.add(playersUnits.get(i).nickname.get(0));
                playersUnits.get(i).load(w);
            }
            turnOrder.add(playersUnits.get(i));
            HUDOffsetsX.add(0.0f);
            HUDOffsetsY.add(0.0f);
        }
        for (int i = 0; i < units.size(); ++i) {
            if (!uniqueUnitNames.contains(units.get(i).name)) {
                uniqueUnitNames.add(units.get(i).nickname.get(0));
                units.get(i).load(w);
            }
            turnOrder.add(units.get(i));
        }
        loaded = true;
    }

    public void initializeResources(World w) {
        if (!initialized && w.assets.update()) {
            for (int i = 0; i < units.size(); ++i) {
                units.get(i).initializeResources(w);
            }
            for (int i = 0; i < playersUnits.size(); ++i) {
                playersUnits.get(i).initializeResources(w);
            }
            this.bg = new Background();
            this.bg.addLayer(w.assets.get(w.worldDir.path() + "/bg/battle_bg.png", Texture.class), 0, 0,0 ,0);
            divider = w.assets.get(w.worldDir.path() + "/bg/battle_divider.png", Texture.class);
            ambient = w.assets.get(w.worldDir.path() + "/sounds/" + ambientPath, Sound.class);
            ambientId = ambient.loop(w.menu.musicVolume / 100.0f);
            initialized = true;
            enemyGroup = new UnitsDrawGroup(this, w, units, 32, 0.9f);
            friendlyGroup = new UnitsDrawGroup(this, w, playersUnits, 32, 0.9f);
            trans.changeDirection(true);
            shade = w.assets.get("p.png", Texture.class);
            statsBarEdge = w.assets.get("stats_bar_edge.png", Texture.class);
            statsBar = w.assets.get("stats_bar.png", Texture.class);
            ArrayList<ArrayList<String>> turnOptions = new ArrayList<ArrayList<String>>();
            turnOptions.add(new ArrayList<String>());
            turnOptions.add(new ArrayList<String>());
            turnOptions.get(0).add("Attack!");
            turnOptions.get(1).add("Атака!");
            turnOptions.get(0).add("Use item");
            turnOptions.get(1).add("Использовать предмет");
            turnOptionsList = new MenuListSelector(turnOptions, w.assets, "cursor.png", palatino24, turnOptions.get(0).size() * 32, 110, 0, false, w.menu);
        }
    }

    public void drawHUD(SpriteBatch batch, World w) {
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;
        float h =  Gdx.graphics.getHeight()/screenRatioY;
        float screenRatioX = Gdx.graphics.getWidth()/1280.0f;
        float ww = Gdx.graphics.getWidth()/screenRatioX;
        for (int i = 0; i < playersUnits.size(); ++i) {
            Unit unit = playersUnits.get(i);
            float shakeX = (float)Math.random() * HUDshakes.get(i);
            float shakeY = (float)Math.random() * HUDshakes.get(i);
            if (finishedBattle && System.currentTimeMillis() > finishedTime + 500 * i) {
                float coeff = ((System.currentTimeMillis() - finishedTime - 500 * i) / 50.0f);
                float maxSpeedX = Math.max(0, 50.0f - coeff);
                float centerX = ww/2.0f - 130;
                if (HUDOffsetsX.get(i) < centerX) HUDspeedsX.set(i, HUDspeedsX.get(i) + 1.0f);
                if (HUDOffsetsX.get(i) > centerX) HUDspeedsX.set(i, HUDspeedsX.get(i) - 1.0f);
                if (Math.abs(HUDspeedsX.get(i)) > maxSpeedX) HUDspeedsX.set(i, maxSpeedX * (HUDspeedsX.get(i)/Math.abs(HUDspeedsX.get(i))));
                HUDOffsetsX.set(i, HUDOffsetsX.get(i) + HUDspeedsX.get(i));
                if (maxSpeedX == 0){
                    if (HUDOffsetsX.get(i) < centerX) HUDOffsetsX.set(i, HUDOffsetsX.get(i) + Math.abs(HUDOffsetsX.get(i) - centerX)/10.0f);
                    if (HUDOffsetsX.get(i) > centerX) HUDOffsetsX.set(i, HUDOffsetsX.get(i) - Math.abs(HUDOffsetsX.get(i) - centerX)/10.0f);
                    if (HUDOffsetsX.get(i) - centerX < Math.abs(HUDOffsetsX.get(i) - centerX)/10.0f) HUDOffsetsX.set(i, centerX);
                }
                float maxSpeedY = Math.max(0, (50.0f - ((System.currentTimeMillis() - finishedTime - 500 * i) / 50.0f)));
                float centerY = -80;
                if (HUDOffsetsY.get(i) < centerY) HUDspeedsY.set(i, HUDspeedsY.get(i) + 1.0f);
                if (HUDOffsetsY.get(i) > centerY) HUDspeedsY.set(i, HUDspeedsY.get(i) - 1.0f);
                if (Math.abs(HUDspeedsY.get(i)) > maxSpeedY) HUDspeedsY.set(i, maxSpeedY * (HUDspeedsY.get(i)/Math.abs(HUDspeedsY.get(i))));
                HUDOffsetsY.set(i, HUDOffsetsY.get(i) + HUDspeedsY.get(i));
                if (maxSpeedY == 0){
                    if (HUDOffsetsY.get(i) < centerY) HUDOffsetsY.set(i, HUDOffsetsY.get(i) + Math.abs(HUDOffsetsY.get(i) - centerY)/10.0f);
                    if (HUDOffsetsY.get(i) > centerY) HUDOffsetsY.set(i, HUDOffsetsY.get(i) - Math.abs(HUDOffsetsY.get(i) - centerY)/10.0f);
                    if (HUDOffsetsY.get(i) - centerY < Math.abs(HUDOffsetsY.get(i) - centerY)/10.0f) HUDOffsetsY.set(i, centerY);
                }
                if (playerWon && expPools.get(i) > 0 && Math.abs(HUDOffsetsX.get(i) - centerX) < 1 && Math.abs(HUDOffsetsY.get(i) - centerY) < 1) {
                    notifyStatChange(unit, 2, expPools.get(i));
                    expPools.set(i, 0);
                }
                if (i == 0) {
                    float alph = coeff / 50.0f;
                    if (playerWon) {
                        if (w.menu.currentLanguage == 0) {
                            palatino24.setColor(new Color(0, 0, 0, alph));
                            palatino24.draw(batch, "You won!", ww/2.0f - palatino24.getBounds("You won!").width/2.0f + 2, h + centerY + 30 + 2);
                            palatino24.setColor(new Color(1.0f, 1.0f, 1.0f, alph));
                            palatino24.draw(batch, "You won!", ww/2.0f - palatino24.getBounds("You won!").width/2.0f, h + centerY + 30);
                        } else {
                            palatino24.setColor(new Color(0, 0, 0, alph));
                            palatino24.draw(batch, "Победа!", ww/2.0f - palatino24.getBounds("Победа!").width/2.0f + 2, h + centerY + 30 + 2);
                            palatino24.setColor(new Color(1.0f, 1.0f, 1.0f, alph));
                            palatino24.draw(batch, "Победа!", ww/2.0f - palatino24.getBounds("Победа!").width/2.0f, h + centerY + 30);
                        }
                    } else {
                        if (w.menu.currentLanguage == 0) {
                            palatino24.setColor(new Color(0, 0, 0, alph));
                            palatino24.draw(batch, "You lost!", ww/2.0f - palatino24.getBounds("You lost!").width/2.0f + 2, h + centerY + 30 + 2);
                            palatino24.setColor(new Color(1.0f, 1.0f, 1.0f, alph));
                            palatino24.draw(batch, "You lost!", ww/2.0f - palatino24.getBounds("You lost!").width/2.0f, h + centerY + 30);
                        } else {
                            palatino24.setColor(new Color(0, 0, 0, alph));
                            palatino24.draw(batch, "Поражение!", ww/2.0f - palatino24.getBounds("Поражение!").width/2.0f + 2, h + centerY + 30 + 2);
                            palatino24.setColor(new Color(1.0f, 1.0f, 1.0f, alph));
                            palatino24.draw(batch, "Поражение!", ww/2.0f - palatino24.getBounds("Поражение!").width/2.0f, h + centerY + 30);
                        }
                    }
                }
            }
            batch.setColor(new Color(0, 0, 0, 0.4f));
            batch.draw(shade, shakeX + HUDOffsetsX.get(i) + 32, shakeY + HUDOffsetsY.get(i) + h - (32 + 64 * i), 200, -50);
            palatino24.setColor(new Color(0, 0, 0, 1.0f));
            palatino24.draw(batch, tempLvls.get(i) + " | " + unit.nickname.get(w.menu.currentLanguage), shakeX + HUDOffsetsX.get(i) + 40 + 2, shakeY + HUDOffsetsY.get(i) + h - (40 + 64 * i + 2));
            palatino24.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
            palatino24.draw(batch, tempLvls.get(i) + " | " + unit.nickname.get(w.menu.currentLanguage), shakeX + HUDOffsetsX.get(i) + 40, shakeY + HUDOffsetsY.get(i) + h - (40 + 64 * i));
            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
            batch.draw(statsBarEdge, shakeX + HUDOffsetsX.get(i) + 34, shakeY + HUDOffsetsY.get(i) + h - (80 + 64 * i), 18, 18);
            batch.setColor(new Color(0.6f, 0.1f, 0.6f, 1.0f));
            batch.draw(statsBar, shakeX + HUDOffsetsX.get(i) + 34 + 14, shakeY + HUDOffsetsY.get(i) + h - (71 - 3 + 64 * i), 188 * ((tempXps.get(i) - tempPrevLvlXps.get(i))/(float)(tempNextLvlXps.get(i) - tempPrevLvlXps.get(i))), 6);
            batch.setColor(new Color(0.9f, 0.7f, 0.8f, 1.0f));
            batch.draw(statsBar, shakeX + HUDOffsetsX.get(i) + 34 + 14 + 188 * (Math.min(unit.stats.exp - unit.stats.prevLvlExp, tempXps.get(i) - tempPrevLvlXps.get(i)) /(float)(tempNextLvlXps.get(i) - tempPrevLvlXps.get(i))), shakeY + HUDOffsetsY.get(i) + h - (71 - 3 + 64 * i), 188 * (Math.abs(Math.min(unit.stats.exp, tempNextLvlXps.get(i)) - tempXps.get(i))/((float)tempNextLvlXps.get(i) - tempPrevLvlXps.get(i))), 6);
            batch.setColor(new Color(1.0f, 1.0f, 0.1f, 1.0f));
            batch.draw(statsBar, shakeX + HUDOffsetsX.get(i) + 34 + 14, shakeY + HUDOffsetsY.get(i) + h - (71 + 9 + 64 * i), 188 * (tempAps.get(i)/(float)unit.stats.maxAp), 6);
            batch.setColor(new Color(1.0f, 1.0f, 0.7f, 1.0f));
            batch.draw(statsBar, shakeX + HUDOffsetsX.get(i) + 34 + 14 + 188 * (Math.min(unit.stats.ap, tempAps.get(i))/(float)unit.stats.maxAp), shakeY + HUDOffsetsY.get(i) + h - (71 + 9 + 64 * i), 188 * (Math.abs(unit.stats.ap - tempAps.get(i))/(float)unit.stats.maxAp), 6);
            batch.setColor(new Color(1.0f, 0.1f, 0.1f, 1.0f));
            batch.draw(statsBar, shakeX + HUDOffsetsX.get(i) + 34 + 8, shakeY + HUDOffsetsY.get(i) + h - (71 + 3 + 64 * i), 200 * (tempHps.get(i)/(float)unit.stats.maxHp), 6);
            batch.setColor(new Color(1.0f, 0.7f, 0.7f, 1.0f));
            batch.draw(statsBar, shakeX + HUDOffsetsX.get(i) + 34 + 8 + 200 * (Math.min(unit.stats.hp, tempHps.get(i))/(float)unit.stats.maxHp), shakeY + HUDOffsetsY.get(i) + h - (71 + 3 + 64 * i), 200 * (Math.abs(unit.stats.hp - tempHps.get(i))/(float)unit.stats.maxHp), 6);
            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
            batch.draw(statsBarEdge, shakeX + HUDOffsetsX.get(i) + 34 + 188 + 27, shakeY + HUDOffsetsY.get(i) + h - (80 + 64 * i), -18, 18);
            if (!finishedBattle) {
                if (unit == curUnit) {
                    HUDOffsetsX.set(i, HUDOffsetsX.get(i) + (20.0f - HUDOffsetsX.get(i))/2.0f);
                } else {
                    HUDOffsetsX.set(i, HUDOffsetsX.get(i)/2.0f);
                }
            }
            HUDshakes.set(i, HUDshakes.get(i) - 0.5f);
            if (HUDshakes.get(i) < 0) HUDshakes.set(i, 0f);
            float valHp = Math.max(Math.abs(tempHps.get(i) - unit.stats.hp)/15, 0.1f);
            if (tempHps.get(i) > unit.stats.hp) tempHps.set(i, tempHps.get(i) - valHp);
            if (tempHps.get(i) < unit.stats.hp) tempHps.set(i, tempHps.get(i) + valHp);
            if (Math.abs(tempHps.get(i) - unit.stats.hp) < valHp) tempHps.set(i, (float)unit.stats.hp);
            float valAp = Math.max(Math.abs(tempAps.get(i) - unit.stats.ap)/15, 0.1f);
            if (tempAps.get(i) > unit.stats.ap) tempAps.set(i, tempAps.get(i) - valAp);
            if (tempAps.get(i) < unit.stats.ap) tempAps.set(i, tempAps.get(i) + valAp);
            if (Math.abs(tempAps.get(i) - unit.stats.ap) < valAp) tempAps.set(i, (float)unit.stats.ap);
            float valXp = Math.max(Math.abs(tempXps.get(i) - unit.stats.exp)/30, 0.2f);
            if (tempXps.get(i) > unit.stats.exp) tempXps.set(i, tempXps.get(i) - valXp);
            if (tempXps.get(i) < unit.stats.exp) tempXps.set(i, tempXps.get(i) + valXp);
            if (Math.abs(tempXps.get(i) - unit.stats.exp) < valXp) tempXps.set(i, (float)unit.stats.exp);
            if (tempXps.get(i) > tempNextLvlXps.get(i)) {
                tempPrevLvlXps.set(i, unit.stats.getLevelXp(tempLvls.get(i)));
                tempNextLvlXps.set(i, unit.stats.getLevelXp(tempLvls.get(i) + 1));
                tempLvls.set(i, tempLvls.get(i) + 1);
            }
        }
    }

    public void addExpToPool(Unit unit, int amount) {
        if (playersUnits.contains(unit))
            return;

        for (int i = 0; i < expPools.size(); ++i)
            expPools.set(i, expPools.get(i) + amount);
    }

    //0-hp, 1-ap, 2-exp
    public void notifyStatChange(Unit unit, int stat, int amount) {
        if (stat == 0) {
            unit.changeHp(amount);
        } else if (stat == 1) {
            unit.changeAp(amount);
        } else if (stat == 2) {
            unit.changeXp(amount);
        }
        if (!playersUnits.contains(unit))
            return;
        HUDshakes.set(playersUnits.indexOf(unit), 3.0f);
    }

    public void useSkill(UnitsDrawGroup group, Skill skill) {
        if (!group.showSelector) {
            group.showSelector = true;
            group.skillPositive = skill.positive;
        } else {
            group.showSelector = false;
            showTurnOptions = false;
            showMovesList = false;
            group.setCurSkill(skill, curUnit, group.getSelectedUnit());
            if (group.equals(enemyGroup)) {
                friendlyGroup.setCurSkill(curUnit.skills.get(unitMovesList.getSelectedIndex()), curUnit, null);
            }
        }
    }

    public void stopSound() {
        ambient.stop();
    }

    private void checkFinished() {
        playerWon = true;
        for (int i = 0; i < units.size(); ++i) {
            if (!units.get(i).stats.dead) {
                playerWon = false;
                break;
            }
        }
        boolean someoneAliveInFriendlyGroup = false;
        if (!playerWon) {
            for (int i = 0; i < playersUnits.size(); ++i) {
                if (!playersUnits.get(i).stats.dead) {
                    someoneAliveInFriendlyGroup = true;
                    break;
                }
            }
        }
        finishedBattle = playerWon || !someoneAliveInFriendlyGroup;
        if (finishedBattle) {
            finishedTime = System.currentTimeMillis();
            curUnit = null;
        }
    }

    public void draw(World w, SpriteBatch batch) {
        float screenRatioX = Gdx.graphics.getWidth()/1280.0f;
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;
        if (initialized && (trans.closed || finishedBattle)) {

            if (!finishedBattle && curUnit == null) {
                while (curUnit == null || curUnit.stats.dead) {
                    curUnit = turnOrder.get(0);
                    turnOrder.remove(0);
                    if (!curUnit.stats.dead) turnOrder.add(curUnit);
                }
                if (playersUnits.contains(curUnit)) {
                    showTurnOptions = true;
                    turnOptionsList.setSelectedIndex(0);
                } else {
                    int r = (int)Math.floor(Math.random() * 2);
                    if (r == 0) {
                        ArrayList<Item> possibleToUseItems = new ArrayList<Item>();
                        for (int i = 0; i < curUnit.inventory.size(); ++i) {
                            if (curUnit.inventory.get(i).consumable) {
                                possibleToUseItems.add(curUnit.inventory.get(i));
                            }
                        }
                        if (possibleToUseItems.size() > 0) {
                            int r2 = (int)Math.floor(Math.random() * possibleToUseItems.size());
                        } else {
                            r = 1;
                        }
                    }
                    if (r == 1) {
                        ArrayList<Skill> possibleToUseSkills = new ArrayList<Skill>();
                        for (int i = 0; i < curUnit.skills.size(); ++i) {
                            if (curUnit.skills.get(i).cooldown == 0 && curUnit.stats.ap >= curUnit.skills.get(i).costAP) {
                                possibleToUseSkills.add(curUnit.skills.get(i));
                            }
                        }
                        int r2 = (int)Math.floor(Math.random() * possibleToUseSkills.size());
                        enemyGroup.setCurSkill(possibleToUseSkills.get(r2), curUnit, null);
                        ArrayList<Unit> possibleToHitUnits = new ArrayList<Unit>();
                        for (int i = 0; i < playersUnits.size(); ++i) {
                            if (!playersUnits.get(i).stats.dead) {
                                possibleToHitUnits.add(playersUnits.get(i));
                            }
                        }
                        int r3 = (int)Math.floor(Math.random() * possibleToHitUnits.size());
                        if (possibleToHitUnits.size() > 0 && possibleToUseSkills.size() > 0) {
                            friendlyGroup.setCurSkill(possibleToUseSkills.get(r2), curUnit, possibleToHitUnits.get(r3));
                        }
                    }
                }
            }

            if (!w.menu.drawPause) {
                if (showTurnOptions && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    ArrayList<ArrayList<String>> moves = new ArrayList<ArrayList<String>>();
                    moves.add(new ArrayList<String>());
                    moves.add(new ArrayList<String>());
                    if (turnOptionsList.getSelectedIndex() == 0) {
                        for (int i = 0; i < curUnit.skills.size(); ++i) {
                            moves.get(0).add(curUnit.skills.get(i).title.get(0));
                            moves.get(1).add(curUnit.skills.get(i).title.get(1));
                        }
                    } else {
                        for (int i = 0; i < curUnit.inventory.size(); ++i) {
                            if (curUnit.inventory.get(i).consumable) {
                                moves.get(0).add(curUnit.inventory.get(i).getName(0));
                                moves.get(1).add(curUnit.inventory.get(i).getName(1));
                            }
                        }
                        if (moves.get(0).size() == 0) {
                            moves.get(0).add("You have no items");
                            moves.get(1).add("У вас нет предметов");
                        }
                    }
                    moves.get(0).add("Back");
                    moves.get(1).add("Назад");
                    unitMovesList = new MenuListSelector(moves, w.assets, "cursor.png", palatino24, 32 * moves.get(0).size(), 110, 0, false, w.menu);
                    showTurnOptions = false;
                    showMovesList = true;
                } else if (showMovesList && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    if (unitMovesList.getSelectedIndex() == unitMovesList.titles.get(0).size() - 1) {
                        showTurnOptions = true;
                        showMovesList = false;
                        enemyGroup.showSelector = false;
                        friendlyGroup.showSelector = false;
                    } else if (turnOptionsList.getSelectedIndex() == 0) {
                        useSkill(enemyGroup, curUnit.skills.get(unitMovesList.getSelectedIndex()));
                    } else if (turnOptionsList.getSelectedIndex() == 1) {
                        int id = turnOptionsList.getSelectedIndex() - 1;
                        int curId = 0;
                        Item item = null;
                        for (int i = 0; i < curUnit.inventory.size(); ++i) {
                            if (curUnit.inventory.get(i).consumable) {
                                if (curId == id) {
                                    item = curUnit.inventory.get(i);
                                }
                                curId++;
                            }
                        }
                        if (item != null) {
                            if (item.effect.positive) {
                                if (friendlyGroup.showSelector) {
                                    item.stack--;
                                    if (item.stack == 0) curUnit.inventory.remove(item);
                                }
                                useSkill(friendlyGroup, item.effect);
                            } else {
                                if (enemyGroup.showSelector) {
                                    item.stack--;
                                    if (item.stack == 0) curUnit.inventory.remove(item);
                                }
                                useSkill(enemyGroup, item.effect);
                            }

                        }
                    }
                }
            }

            bg.draw(batch, SCREEN_WIDTH / 2, SCREEN_HEIGHT, 0, 0);
            //bg.draw(batch, SCREEN_WIDTH / 2, SCREEN_HEIGHT, SCREEN_WIDTH / 2, 0);
            friendlyGroup.draw(w, batch, SCREEN_WIDTH / 4.0f, SCREEN_HEIGHT / 2.0f, curUnit);
            enemyGroup.draw(w, batch, 3.0f * SCREEN_WIDTH / 4.0f, SCREEN_HEIGHT / 2.0f, curUnit);
            batch.draw(divider, 0, 0, Gdx.graphics.getWidth()/screenRatioX, Gdx.graphics.getHeight()/screenRatioY);

            if (!finishedBattle) drawHUD(batch, w);

            batch.setColor(new Color(0, 0, 0, 0.4f));
            batch.draw(shade, 100, turnOptionsY, 300, turnOptionsList.height + 20);
            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
            turnOptionsList.yOffset = turnOptionsY + turnOptionsList.height + 10 + 12 * (Math.max(turnOptionsList.titles.get(0).size() - 2, 0));
            turnOptionsList.draw(batch, !showTurnOptions || showMovesList);
            if (showTurnOptions) {
                turnOptionsY = turnOptionsY + (turnOptionsList.height + 20 - turnOptionsY)/2.0f - ((turnOptionsList.height)/2.0f + 10);
            } else {
                turnOptionsY = turnOptionsY - turnOptionsY/2.0f - (turnOptionsList.height/2.0f + 10);
            }

            if (unitMovesList != null) {
                batch.setColor(new Color(0, 0, 0, 0.4f));
                batch.draw(shade, 100, movesListY, 300, unitMovesList.height + 20);
                batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                unitMovesList.yOffset = movesListY + (unitMovesList.height + 10) + 12 * (Math.max(unitMovesList.titles.get(0).size() - 2, 0));
                unitMovesList.draw(batch, showTurnOptions || enemyGroup.showSelector || friendlyGroup.showSelector);
                if (showMovesList) {
                    movesListY = movesListY + (unitMovesList.height + 20 - movesListY)/2.0f - (unitMovesList.height/2.0f + 10);
                } else {
                    movesListY = movesListY - movesListY/2.0f - (unitMovesList.height/2.0f + 10);
                }
            }

            if (enemyGroup.curSkill == null && friendlyGroup.curSkill == null && !showTurnOptions && !showMovesList) {
                curUnit = null;
            }

            if (!finishedBattle) checkFinished();
            if (finishedBattle && trans.outwards) {
                trans.reset();
                if (playerWon) {
                    drops = new ArrayList<Item>();
                    for (int i = 0; i < units.size(); ++i)
                        for (int j = 0; j < units.get(i).drops.size(); ++j) {
                            Inventory.addItem(drops, units.get(i).drops.get(j));
                            Inventory.addItem(playersUnits.get(0).inventory, units.get(i).drops.get(j));
                        }
                    ArrayList<Texture> sprites = new ArrayList<Texture>();
                    ArrayList<ArrayList<String>> titles = new ArrayList<ArrayList<String>>();
                    titles.add(new ArrayList<String>());
                    titles.add(new ArrayList<String>());
                    for (int i = 0; i < drops.size(); ++i) {
                        sprites.add(drops.get(i).icon);
                        String stackStr = "";
                        if (drops.get(i).stack > 1) stackStr += " (" + drops.get(i).stack + ")";
                        titles.get(0).add("<- " + drops.get(i).getName(0) + stackStr + " ->");
                        titles.get(1).add("<- " + drops.get(i).getName(1) + stackStr + " ->");
                    }

                    dropsView = new CircularSelector(w.assets, titles.get(w.menu.currentLanguage), sprites, palatino24,  Gdx.graphics.getWidth()/screenRatioX/2.0f, 300/screenRatioY, 128, 64, 2, w.menu);
                }
                ArrayList<ArrayList<String>> endOptions = new ArrayList<ArrayList<String>>();
                endOptions.add(new ArrayList<String>());
                endOptions.add(new ArrayList<String>());
                endOptions.get(0).add("End battle");
                endOptions.get(1).add("Закончить бой");
                endOptionsList = new MenuListSelector(endOptions, w.assets, "cursor.png", palatino24, 100, 0, 100/screenRatioY, true, w.menu);
            }
        }
        trans.draw(batch);
        if (finishedBattle) drawHUD(batch, w);
        if (finishedBattle && trans.closed) {
            if (playerWon && drops.size() > 0) {
                if (w.menu.currentLanguage == 0)
                    palatino24.draw(batch, "You receive:", Gdx.graphics.getWidth()/screenRatioX/2.0f - palatino24.getBounds("You receive:").width/2.0f, 400/screenRatioY);
                else
                    palatino24.draw(batch, "Вы получаете:", Gdx.graphics.getWidth()/screenRatioX/2.0f - palatino24.getBounds("Вы получаете:").width/2.0f, 400/screenRatioY);
            } else {
                if (w.menu.currentLanguage == 0)
                    palatino24.draw(batch, "You receive nothing.", Gdx.graphics.getWidth()/screenRatioX/2.0f - palatino24.getBounds("You receive nothing.").width/2.0f, 400/screenRatioY);
                else
                    palatino24.draw(batch, "Вы ничего не получаете.", Gdx.graphics.getWidth()/screenRatioX/2.0f - palatino24.getBounds("Вы ничего не получаете.").width/2.0f, 400/screenRatioY);
            }
            if (playerWon && drops.size() > 0) dropsView.draw(batch, false);
            endOptionsList.draw(batch, false);
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                if (playerWon && triggerCell != null) {
                    triggerCell.currentState = 1;
                    triggerCell.updateEntityState(w.assets, w.worldDir.path());
                }
                finished = true;
                ambient.stop();
            }
        }
        if (ambient != null)
            ambient.setVolume(ambientId, w.menu.musicVolume / 100.0f);
    }
}
