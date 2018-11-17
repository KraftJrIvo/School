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
import com.mygdx.schoolRPG.Background;
import com.mygdx.schoolRPG.Item;
import com.mygdx.schoolRPG.Particle;
import com.mygdx.schoolRPG.World;
import com.mygdx.schoolRPG.battleSystem.ui.FriendlyUnitsDrawGroup;
import com.mygdx.schoolRPG.battleSystem.ui.UnitsDrawGroup;
import com.mygdx.schoolRPG.tools.MenuListSelector;

import java.security.Key;
import java.util.ArrayList;

import static com.mygdx.schoolRPG.Area.SCREEN_HEIGHT;
import static com.mygdx.schoolRPG.Area.SCREEN_WIDTH;

public class Battle {

    Background bg;
    Sound ambient;
    String ambientPath;
    ArrayList<Unit> units;
    ArrayList<Unit> playersUnits;
    UnitsDrawGroup enemyGroup, friendlyGroup;
    BitmapFont palatino24;
    Texture divider;
    Transition trans;
    Texture shade;
    Texture statsBarEdge, statsBarHp, statsBarAp, statsBarXp;
    Unit curUnit;
    ArrayList<Float> unitsStatsDrawOffsets;
    public boolean finished = false;
    public boolean loaded = false;
    public boolean initialized = false;
    MenuListSelector turnOptionsList, unitMovesList;
    ArrayList<Unit> turnOrder;
    boolean showTurnOptions = false, showMovesList = false;
    float turnOptionsY = 0, movesListY = -1000.0f;

    public Battle(World w, ArrayList<Unit> units, ArrayList<Unit> playersUnits, String ambientPath) {
        this.units = units;
        this.playersUnits = playersUnits;
        this.ambientPath = ambientPath;
        trans = new Transition(w.assets.get(w.worldDir.path() + "/bg/battle_trans_top.png", Texture.class), w.assets.get(w.worldDir.path() + "/bg/battle_trans_bottom.png", Texture.class));
        palatino24 = new BitmapFont(Gdx.files.internal("palatino24.fnt"), Gdx.files.internal("palatino24.png"), false);
    }

    public void load(World w) {
        w.assets.load(w.worldDir.path() + "/bg/battle_bg.png", Texture.class);
        w.assets.load(w.worldDir.path() + "/bg/battle_divider.png", Texture.class);
        w.assets.load(w.worldDir.path() + "/sounds/" + ambientPath, Sound.class);
        ArrayList<String> uniqueUnitNames = new ArrayList<String>();
        unitsStatsDrawOffsets = new ArrayList<Float>();
        turnOrder = new ArrayList<Unit>();
        for (int i = 0; i < playersUnits.size(); ++i) {
            if (!uniqueUnitNames.contains(playersUnits.get(i).name)) {
                uniqueUnitNames.add(playersUnits.get(i).nickname.get(0));
                playersUnits.get(i).load(w);
            }
            turnOrder.add(playersUnits.get(i));
            unitsStatsDrawOffsets.add(0.0f);
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
            ambient.loop(w.menu.musicVolume / 100.0f);
            initialized = true;
            enemyGroup = new UnitsDrawGroup(w, units, 32, 0.9f);
            friendlyGroup = new UnitsDrawGroup(w, playersUnits, 32, 0.9f);
            trans.changeDirection(true);
            shade = w.assets.get("p.png", Texture.class);
            statsBarEdge = w.assets.get("stats_bar_edge.png", Texture.class);
            statsBarHp = w.assets.get("stats_bar_hp.png", Texture.class);
            statsBarAp = w.assets.get("stats_bar_ap.png", Texture.class);
            statsBarXp = w.assets.get("stats_bar_xp.png", Texture.class);
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

    public void drawHUD(SpriteBatch batch) {

    }

    public void draw(World w, SpriteBatch batch) {
        if (initialized && trans.closed) {

            if (curUnit == null) {
                curUnit = turnOrder.get(0);
                turnOrder.remove(0);
                turnOrder.add(curUnit);
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
                        friendlyGroup.setCurSkill(possibleToUseSkills.get(r2), curUnit, possibleToHitUnits.get(r3));
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
                        if (!enemyGroup.showSelector) {
                            enemyGroup.showSelector = true;
                        } else {
                            enemyGroup.showSelector = false;
                            showTurnOptions = false;
                            showMovesList = false;
                            enemyGroup.setCurSkill(curUnit.skills.get(unitMovesList.getSelectedIndex()), curUnit, enemyGroup.getSelectedUnit());
                            friendlyGroup.setCurSkill(curUnit.skills.get(unitMovesList.getSelectedIndex()), curUnit, null);
                        }
                    }
                }
            }

            bg.draw(batch, SCREEN_WIDTH / 2, SCREEN_HEIGHT, 0, 0);
            //bg.draw(batch, SCREEN_WIDTH / 2, SCREEN_HEIGHT, SCREEN_WIDTH / 2, 0);
            friendlyGroup.draw(w, batch, SCREEN_WIDTH / 4.0f, SCREEN_HEIGHT / 2.0f, curUnit);
            enemyGroup.draw(w, batch, 3.0f * SCREEN_WIDTH / 4.0f, SCREEN_HEIGHT / 2.0f, curUnit);
            float screenRatioX = Gdx.graphics.getWidth()/1280.0f;
            float screenRatioY = Gdx.graphics.getHeight()/720.0f;
            batch.draw(divider, 0, 0, Gdx.graphics.getWidth()/screenRatioX, Gdx.graphics.getHeight()/screenRatioY);

            float h =  Gdx.graphics.getHeight()/screenRatioY;

            for (int i = 0; i < playersUnits.size(); ++i) {
                Unit unit = playersUnits.get(i);
                batch.setColor(new Color(0, 0, 0, 0.4f));
                batch.draw(shade, unitsStatsDrawOffsets.get(i) + 32, h - (32 + 64 * i), 200, -50);
                palatino24.setColor(new Color(0, 0, 0, 1.0f));
                palatino24.draw(batch, unit.nickname.get(w.menu.currentLanguage), unitsStatsDrawOffsets.get(i) + 40 + 2, h - (40 + 64 * i + 2));
                palatino24.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                palatino24.draw(batch, unit.nickname.get(w.menu.currentLanguage), unitsStatsDrawOffsets.get(i) + 40, h - (40 + 64 * i));
                batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                batch.draw(statsBarEdge, unitsStatsDrawOffsets.get(i) + 34, h - (80 + 64 * i), 18, 18);
                batch.draw(statsBarXp, unitsStatsDrawOffsets.get(i) + 34 + 14, h - (71 - 3 + 64 * i), 188 * (unit.stats.exp/unit.stats.nextLvlExp), 6);
                batch.draw(statsBarAp, unitsStatsDrawOffsets.get(i) + 34 + 14, h - (71 + 9 + 64 * i), 188 * (unit.stats.ap/unit.stats.maxAp), 6);
                batch.draw(statsBarHp, unitsStatsDrawOffsets.get(i) + 34 + 8, h - (71 + 3 + 64 * i), 200 * (unit.stats.ap/unit.stats.maxAp), 6);
                //Sprite sprite = new Sprite(statsBarEdge);
                //sprite.flip(true, false);
                batch.draw(statsBarEdge, unitsStatsDrawOffsets.get(i) + 34 + 188 + 27, h - (80 + 64 * i), -18, 18);
                if (unit == curUnit) {
                    unitsStatsDrawOffsets.set(i, unitsStatsDrawOffsets.get(i) + (20.0f - unitsStatsDrawOffsets.get(i))/2.0f);
                } else {
                    unitsStatsDrawOffsets.set(i, unitsStatsDrawOffsets.get(i)/2.0f);
                }
            }

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
        }
        trans.draw(batch);
    }
}
