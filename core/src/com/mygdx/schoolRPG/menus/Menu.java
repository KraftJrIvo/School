package com.mygdx.schoolRPG.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.schoolRPG.tools.MenuListSelector;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
/**
 * Created by user on 16.07.2014.
 */
public class Menu {

    public int nextMenu, nextMenuSetting;
    public String nextMenuMessage = "";
    public boolean allowPause = false;
    public int ID = 0;
    public boolean initialised = false;
    public boolean paused = false, optionsOpen = false, dialogSkipping = false;
    Texture pause, resume, exit, options;
    AssetManager assets;
    boolean android;
    public BitmapFont mainFont;
    public boolean drawPause = true;
    public boolean unpausable = true;
    MenuListSelector pauseSelector, optionsSelector;
    int changeSpeed = 0;
    public boolean fullScreen;

    public int musicVolume, soundVolume;
    public int currentLanguage;
    ArrayList<String> languages;

    Sound click2;
    Sound click3;

    public Menu(int id, boolean android) {
        this.android = android;
        nextMenu = id;
        nextMenuSetting = 0;
    }

    public void changeSetting(int id) {
    }

    public void load(AssetManager assets) {
        assets.load("p.png", Texture.class);
        assets.load("menu_click_1.wav", Sound.class);
        assets.load("menu_click_2.wav", Sound.class);
        assets.load("menu_click_3.wav", Sound.class);
        mainFont = new BitmapFont(Gdx.files.internal("palatino24.fnt"), Gdx.files.internal("palatino24.png"), false);
        this.assets = assets;
        if (allowPause) {
            if (android) {
                if (!assets.isLoaded("pause.png", Texture.class)) {
                    assets.load("pause.png", Texture.class);
                }
            }
            if (!assets.isLoaded("play.png", Texture.class)) {
                assets.load("play.png", Texture.class);
            }
        }
        musicVolume = 50;
        soundVolume = 50;
        currentLanguage = 0;
        languages = new ArrayList<String>();
        languages.add("English");
        languages.add("Русский");
    }

    public void initialiseResources() {
        if (allowPause) {
            float BUTTONSIZE = Gdx.graphics.getWidth() / 4.17f;
            if (android && allowPause) {
                pause = assets.get("pause.png", Texture.class);
            }
            resume = assets.get("play.png", Texture.class);
            exit = assets.get("play.png", Texture.class);
            options = assets.get("play.png", Texture.class);
            ArrayList<String> list = new ArrayList<String>();

            pauseSelector = new MenuListSelector(list, assets, "cursor.png", mainFont, Gdx.graphics.getHeight(), 0, 0, true, this);
        }
        ArrayList<String> list2 = new ArrayList<String>();
        /*list2.add("Music Volume: " + musicVolume);
        list2.add("Sound Volume: " + soundVolume);
        list2.add("Language: " + languages.get(currentLanguage));
        list2.add("Back");*/
        optionsSelector = new MenuListSelector(list2, assets, "cursor.png", mainFont, Gdx.graphics.getHeight(), 0, 0, true, this);
        click2 = assets.get("menu_click_1.wav");
        click3 = assets.get("menu_click_3.wav");
        updateLanguage();
    }

    public void invalidate() {
        //System.out.println("inv");
        /*if (allowPause) {
            if (
                    Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                drawPause = true;
                paused = true;
            }
        }*/

    }

    public void updateLanguage() {
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String> list2 = new ArrayList<String>();
        if (currentLanguage == 0) {
            list.add("Music Volume: " + musicVolume);
            list.add("Sound Volume: " + soundVolume);
            list.add("Language: " + languages.get(currentLanguage));
            if (fullScreen) {
                list.add("Fullscreen: On");
            } else {
                list.add("Fullscreen: Off");
            }
            list.add("Reset to defaults");
            list.add("Back");
            list2.add("Continue");
            list2.add("Options");
            list2.add("Exit to main menu");
        } else if (currentLanguage == 1) {
            list.add("Громкость Музыки: " + musicVolume);
            list.add("Громкость 3вука: " + soundVolume);
            list.add("Язык: " + languages.get(currentLanguage));
            if (fullScreen) {
                list.add("Полный экран: Да");
            } else {
                list.add("Полный экран: Нет");
            }
            list.add("Вернуть настройки по умолчанию");
            list.add("Назад");
            list2.add("Продолжить");
            list2.add("Опции");
            list2.add("Выйти в Главное Меню");
        } else {
            return;
        }
        optionsSelector.titles = list;
        if (allowPause) pauseSelector.titles = list2;
    }

    private void unpause() {
        if (unpausable) {
            paused = false;
        } else {
            drawPause = !drawPause;
        }
    }

    private void saveOptions() {
        try {
            PrintWriter writer = new PrintWriter("../../current.cfg", "UTF-8");
            writer.println("fullscreen");
            writer.println("" + fullScreen);
            writer.println("sound");
            writer.println("" + soundVolume);
            writer.println("music");
            writer.println("" + musicVolume);
            writer.println("language");
            writer.println("" + currentLanguage);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void draw(SpriteBatch batch, ShapeRenderer renderer) {
        //System.out.println(paused);
        updateLanguage();
        if (optionsOpen) {
            batch.setColor(1,1,1,0.8f);
            batch.begin();
            batch.draw(assets.get("p.png", Texture.class), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(1,1,1,1);
            optionsSelector.draw(batch, false);
            batch.end();
            int index = optionsSelector.getSelectedIndex();
            int rate = Math.max(1, (10 - (changeSpeed / 10)));
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
                if (index == 0) {
                    musicVolume++;
                    click3.play(soundVolume / 100.0f);
                } else if (index == 1) {
                    soundVolume++;
                    click3.play(soundVolume / 100.0f);
                }
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                if (index == 0) {
                    musicVolume--;
                    click3.play(soundVolume / 100.0f);
                } else if (index == 1) {
                    soundVolume--;
                    click3.play(soundVolume / 100.0f);
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
                changeSpeed++;
                if (index == 0) {
                    if (changeSpeed % rate == 0) {
                        musicVolume++;
                        if (musicVolume < 100) click3.play(soundVolume / 100.0f);
                    }
                } else if (index == 1) {
                    if (changeSpeed % rate == 0) {
                        soundVolume++;
                        if (soundVolume < 100)  click3.play(soundVolume / 100.0f);
                    }
                }
            }else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
                changeSpeed++;
                if (index == 0) {
                    if (changeSpeed % rate == 0) {
                        musicVolume--;
                        if (musicVolume > 0) click3.play(soundVolume / 100.0f);
                    }
                } else if (index == 1) {
                    if (changeSpeed % rate == 0) {
                        soundVolume--;
                        if (soundVolume > 0) click3.play(soundVolume / 100.0f);
                    }
                }
            } else {
                changeSpeed = 0;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                if (index == 4) {
                    fullScreen = true;
                    currentLanguage = 0;
                    soundVolume = 50;
                    musicVolume = 50;
                    updateLanguage();
                } else if (index == 5) {
                    optionsOpen = false;
                    saveOptions();
                    click2.play(soundVolume / 100.0f);
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                if (index == 2) {
                    currentLanguage++;
                    click3.play(soundVolume / 100.0f);
                }
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                if (index == 2) {
                    currentLanguage--;
                    click3.play(soundVolume / 100.0f);
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                if (index == 3) {
                    fullScreen = !fullScreen;
                    click3.play(soundVolume / 100.0f);
                }
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                if (index == 3) {
                    fullScreen = !fullScreen;
                    click3.play(soundVolume / 100.0f);
                }
            }
            if (musicVolume > 100) musicVolume = 100;
            if (musicVolume < 0) musicVolume = 0;
            if (soundVolume > 100) soundVolume = 100;
            if (soundVolume < 0) soundVolume = 0;
            if (currentLanguage >= languages.size()) currentLanguage = 0;
            if (currentLanguage < 0) currentLanguage = languages.size() - 1;
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                optionsOpen = false;
                saveOptions();
                click2.play(soundVolume / 100.0f);
            }

        }
        else if (!paused) {
            invalidate();
            if (android && allowPause) {
                //pauseButton.draw(batch);
            }

        } else if (drawPause) {
            batch.setColor(1,1,1,0.5f);
            batch.begin();
            batch.draw(assets.get("p.png", Texture.class), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            pauseSelector.draw(batch, false);
            batch.end();
            batch.setColor(1,1,1,1);
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                int index = pauseSelector.getSelectedIndex();
                if (index == 0) {
                    unpause();
                } else if (index == 1) {
                    optionsOpen = true;
                } else {
                    nextMenu = 0;
                }
            }

        }
        if ((this.getClass() != GameMenu.class || (paused && !dialogSkipping)) && (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))) {
            click2.play(soundVolume / 100.0f);
        }
        if (allowPause) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                if (paused) {
                    unpause();
                } else {
                    drawPause = true;
                    paused = true;
                }
            }

        }
        //batch.end();
    }
}
