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

/**
 * Created by user on 16.07.2014.
 */
public class Menu {

    public int nextMenu, nextMenuSetting;
    public boolean allowPause = false;
    public int ID = 0;
    public boolean initialised = false;
    public boolean paused = false, optionsOpen = false;
    Button pauseButton, resumeButton, exitButton, optionsButton;
    Texture pause, resume, exit, options;
    AssetManager assets;
    boolean android;
    BitmapFont mainFont;
    public boolean drawPause = true;
    public boolean unpausable = true;

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
                pauseButton = new Button(new Rectangle(Gdx.graphics.getWidth() - BUTTONSIZE / 3,
                        Gdx.graphics.getHeight() - BUTTONSIZE / 3, BUTTONSIZE / 3, BUTTONSIZE / 3), pause);
            }
            resume = assets.get("play.png", Texture.class);
            exit = assets.get("play.png", Texture.class);
            options = assets.get("play.png", Texture.class);
            resumeButton = new Button(new Rectangle(Gdx.graphics.getWidth() / 2 - BUTTONSIZE / 2,
                    (Gdx.graphics.getHeight() * 4) / 5 - BUTTONSIZE / 2, BUTTONSIZE, BUTTONSIZE), resume);
            exitButton = new Button(new Rectangle(Gdx.graphics.getWidth() / 2 - BUTTONSIZE / 2,
                    (Gdx.graphics.getHeight() * 3) / 5 - BUTTONSIZE / 2, BUTTONSIZE, BUTTONSIZE), resume);
            optionsButton = new Button(new Rectangle(Gdx.graphics.getWidth() / 2 - BUTTONSIZE / 2,
                    (Gdx.graphics.getHeight() * 2) / 5 - BUTTONSIZE / 2, BUTTONSIZE, BUTTONSIZE), resume);
        }
    }

    public void invalidate() {
        //System.out.println("inv");
        if (allowPause) {
            if (android && pauseButton.checkTouch() ||
                    Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                drawPause = true;
                paused = true;
            }
        }
    }

    public void draw(SpriteBatch batch, ShapeRenderer renderer) {
        //System.out.println(paused);
        if (!paused) {
            invalidate();
            if (android && allowPause) {
                pauseButton.draw(batch);
            }
        } else if (drawPause) {
            batch.setColor(1,1,1,0.5f);
            batch.begin();
            batch.draw(assets.get("p.png", Texture.class), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();
            batch.setColor(1,1,1,1);
            if ((android && (pauseButton.checkTouch())) || resumeButton.checkTouch() ||
                    Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                if (unpausable) {
                    paused = false;
                } else {
                    drawPause = !drawPause;
                }
            }
            if (exitButton.checkTouch()) nextMenu = 0;
            if (optionsButton.checkTouch()) optionsOpen = true;
            //System.out.println(nextMenu);
            if (android) {
                pauseButton.draw(batch);
            }
            resumeButton.draw(batch);
            exitButton.draw(batch);
            optionsButton.draw(batch);
        }
        //batch.end();
    }
}
