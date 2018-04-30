package com.mygdx.schoolRPG.menus;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.schoolRPG.tools.Button;
import com.mygdx.schoolRPG.tools.CircularSelector;
import com.mygdx.schoolRPG.tools.MenuListSelector;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IVO on 15.07.2014.
 * main menu
 */

public class MainMenu extends Menu {
    public int ID = 0;

    float PLAYSIZE;

    public int curMenu = 0;
    Texture backGround, title, cursor, overlay;
    float overlayAngle = 0;

    boolean gameSelect = false;
    MenuListSelector gameSelector;
    public ArrayList<String> worldsNames;
    public ArrayList<String> worldsPaths;
    public ArrayList<Integer> worldsIds;
    public ArrayList<Integer> worldsStates;

    MenuListSelector selector;

    ApplicationAdapter adapter;

    public MainMenu(int id, boolean android, ApplicationAdapter adapter) {
        super(id, android);
        ID = 0;
        this.adapter = adapter;
        PLAYSIZE = Gdx.graphics.getHeight()/2.5f;
    }

    @Override
    public void changeSetting(int id) {
        gameSelect = false;
    }

    public void invalidate() {
        super.invalidate();

        if (gameSelect && Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL)) {
            int index = gameSelector.getSelectedIndex();
            if (index != 0 && index != gameSelector.titles.size()-1) {
                int worldId = worldsIds.get(index);
                if (worldsIds.get(index-1) == worldId) {
                    int gameNum = 1;
                    for (int i = index + 1; i < worldsIds.size(); ++i) {
                        if (worldsIds.get(i) != worldId) {
                            break;
                        }
                        gameNum++;
                    }
                    FileHandle file = Gdx.files.local(worldsPaths.get(worldId) + "/saves/state" + (gameNum-1));
                    file.delete();
                    FileHandle savesDir = Gdx.files.internal(worldsPaths.get(worldId) + "/saves");
                    for (FileHandle entry: savesDir.list()) {

                        int saveNum = Integer.parseInt(entry.name().substring(5, entry.name().length()));
                        if (saveNum >= gameNum) {
                            FileHandle nfile = Gdx.files.internal(worldsPaths.get(worldId) + "/saves/state" + (saveNum-1));
                            entry.file().renameTo(nfile.file());
                        }
                    }
                }
                int id = gameSelector.getSelectedIndex();
                refreshSaves();
                gameSelector.setSelectedIndex(id);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || (Gdx.input.justTouched() && Gdx.input.getX() < 500)) {
            if (gameSelect) {
                int index = gameSelector.getSelectedIndex();
                if (index == gameSelector.titles.size() - 1) {
                    gameSelect = false;
                } else {
                    int worldId = worldsIds.get(index);
                    if (index == 0 || worldsIds.get(index-1) != worldId) {
                        int gameNum = 1;
                        for (int i = index + 1; i < worldsIds.size(); ++i) {
                            if (worldsIds.get(i) != worldId) {
                                break;
                            }
                            gameNum++;
                        }
                        worldsIds.add(index + gameNum, worldId);
                        worldsStates.add(index + gameNum, gameNum - 1);
                        gameSelector.titles.add(index + gameNum, worldsNames.get(worldId) + " - Continue Game " + gameNum);
                    }
                    nextMenuMessage = worldsPaths.get(worldId);
                    nextMenuSetting = worldsStates.get(index);
                    nextMenu = 1;
                }
            } else {
                int index = selector.getSelectedIndex();
                if (index == 0) {
                    gameSelect = true;
                    refreshSaves();
                } else if (index == 1) {
                    optionsOpen = true;
                }
                else if (index == 2) {
                }
                else if (index == 3) {
                    Gdx.app.exit();
                }
            }
        }
    }

    @Override
    public void updateLanguage() {
        super.updateLanguage();
        ArrayList<String> list = new ArrayList<String>();
        if (currentLanguage == 0) {
            list.add("Play");
            list.add("Options");
            list.add("Info");
            list.add("Exit");
        } else {
            list.add("Играть");
            list.add("Опции");
            list.add("Инфо");
            list.add("Выйти");
        }
        if (selector != null) {
            selector.titles = list;
        } else {
            selector = new MenuListSelector(list, assets, "cursor.png", mainFont, Gdx.graphics.getHeight(), 0, 0, true, this);
        }
    }

    @Override
    public void draw(SpriteBatch batch, ShapeRenderer renderer) {
        float screenRatioX = Gdx.graphics.getWidth()/1280.0f;
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;
        batch.begin();
        batch.draw(backGround, 0, 0, Gdx.graphics.getWidth()/screenRatioX, Gdx.graphics.getHeight()/screenRatioY);
        float centerX = Gdx.graphics.getWidth()/screenRatioX/2;
        float centerY = Gdx.graphics.getHeight()/screenRatioY/2;
        batch.draw(new TextureRegion(overlay), centerX - overlay.getWidth()/2, centerY - overlay.getHeight()/2, 750.0f, 750.0f, 1500, 1500, 1, 1, overlayAngle, true);
        overlayAngle += 0.01f;
        if (!gameSelect) {
            batch.draw(title, Gdx.graphics.getWidth()/screenRatioX/2 - title.getWidth()/2, Gdx.graphics.getHeight()/screenRatioY - title.getHeight() * 1.5f);
            selector.draw(batch, optionsOpen);
        } else {
            gameSelector.draw(batch, optionsOpen);
        }
        batch.end();
        super.draw(batch, renderer);
    }

    @Override
    public void load(AssetManager assets) {
        super.load(assets);
        //assets.load("play.png", Texture.class);
        assets.load("bg.png", Texture.class);
        assets.load("bg_new.png", Texture.class);
        assets.load("bg_overlay.png", Texture.class);
        assets.load("title.png", Texture.class);
        assets.load("cursor.png", Texture.class);
        //assets.load("options.png", Texture.class);
        //assets.load("credits.png", Texture.class);
    }

    private void refreshSaves() {
        worldsStates = new ArrayList<Integer>();
        worldsIds = new ArrayList<Integer>();
        worldsNames = new ArrayList<String>();
        worldsPaths = new ArrayList<String>();
        ArrayList<String> list = new ArrayList<String>();
        FileHandle worldsDir = Gdx.files.internal("worlds");
        for (FileHandle entry: worldsDir.list()) {
            if (entry.isDirectory()) {
                FileHandle tlwFile = Gdx.files.internal(entry.path() + "/world1.tlw");
                if (tlwFile.exists()) {
                    String name = "";
                    InputStream fis = null;
                    try {
                        fis = tlwFile.read();//.open(tlwFile.file());//new FileInputStream(tlwFile.file());
                        int namesCount;
                        namesCount = fis.read();
                        for (int i =0; i < namesCount; ++i) {
                            int nameSize = fis.read();
                            byte[] buff = new byte[nameSize];
                            fis.read(buff);
                        }
                        int size = fis.read();
                        byte[] buff = new byte[size];
                        fis.read(buff);
                        name = new String(buff);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (currentLanguage == 0) {
                        list.add(name + " - Start New Game");
                    } else {
                        list.add(name + " - Начать Новую Игру");
                    }
                    worldsIds.add(worldsPaths.size());
                    worldsNames.add(name);
                    worldsPaths.add(entry.path());
                    worldsStates.add(-1);
                    FileHandle statesDir = Gdx.files.internal(entry.path() + "/saves");
                    if (statesDir.exists()) {
                        int stateNumber = 1;
                        for (FileHandle state: statesDir.list()) {
                            if (currentLanguage == 0) {
                                list.add(name + " - Continue Game " + stateNumber);
                            } else {
                                list.add(name + " - Продолжить Игру " + stateNumber);
                            }
                            worldsIds.add(worldsPaths.size()-1);
                            worldsStates.add(stateNumber-1);
                            stateNumber++;
                        }
                    }
                }
            }
        }
        if (currentLanguage == 0) {
            list.add("Back");
        } else {
            list.add("Назад");
        }
        gameSelector = new MenuListSelector(list, assets, "cursor.png", mainFont, Gdx.graphics.getHeight(), 0, 0, true, this);
    }

    @Override
    public void initialiseResources() {
        if (!initialised) {
            super.initialiseResources();
            backGround = (assets.get("bg_new.png"));
            updateLanguage();
            title = assets.get("title.png", Texture.class);
            overlay = assets.get("bg_overlay.png", Texture.class);
            refreshSaves();

            initialised = true;
        }
    }

}
