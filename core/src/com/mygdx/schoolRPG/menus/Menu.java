package com.mygdx.schoolRPG.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.schoolRPG.tools.Button;
import com.mygdx.schoolRPG.tools.MenuListSelector;

import java.util.ArrayList;

/**
 * Created by user on 16.07.2014.
 */
public class Menu {

    public int nextMenu, nextMenuSetting;
    public boolean allowPause = false;
    public int ID = 0;
    public boolean initialised = false;
    public boolean paused = false, optionsOpen = false;
    Texture pause, resume, exit, options;
    AssetManager assets;
    boolean android;
    BitmapFont mainFont;
    public boolean drawPause = true;
    public boolean unpausable = true;
    MenuListSelector pauseSelector;

    public Menu(int id, boolean android) {
        this.android = android;
        nextMenu = id;
        nextMenuSetting = 0;
    }

    public void changeSetting(int id) {
    }

    public void load(AssetManager assets) {
        mainFont = new BitmapFont(Gdx.files.internal("font1.fnt"), Gdx.files.internal("font1.png"), false);
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
            if (!assets.isLoaded("p.png", Texture.class)) {
                assets.load("p.png", Texture.class);
            }
        }
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
            ArrayList<String> list= new ArrayList<String>();
            list.add("Continue");
            list.add("Options");
            list.add("Exit to main menu");
            pauseSelector = new MenuListSelector(list, assets, "cursor.png", mainFont, Gdx.graphics.getHeight(), 0, 0, true);
        }
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

    private void unpause() {
        if (unpausable) {
            paused = false;
        } else {
            drawPause = !drawPause;
        }
    }

    public void draw(SpriteBatch batch, ShapeRenderer renderer) {
        //System.out.println(paused);
        if (!paused) {
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
