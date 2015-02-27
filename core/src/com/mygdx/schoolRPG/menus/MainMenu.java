package com.mygdx.schoolRPG.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.schoolRPG.tools.Button;

/**
 * Created by IVO on 15.07.2014.
 * main menu
 */

public class MainMenu extends Menu {
    public int ID = 0;

    float PLAYSIZE;

    public int curMenu = 0;
    Texture backGround;
    Texture play, options, credits;

    int halfScreenHeight = Gdx.graphics.getHeight() / 2;
    int halfScreenWidth = Gdx.graphics.getWidth() / 2;
    Button playButton, optionsButton, creditsButton;

    public MainMenu(int id, boolean android) {
        super(id, android);
        ID = 0;

        PLAYSIZE = Gdx.graphics.getHeight()/2.5f;

    }

    public void invalidate() {
        super.invalidate();

        if (playButton.checkTouch()) {
            nextMenuSetting = 0;
            nextMenu = 1;
        }
        /*if (optionsButton.checkTouch()) {
            nextMenu = 2;
        }*/
        if (creditsButton.checkTouch()) {
            nextMenuSetting = 1;
            nextMenu = 1;
        }

        if (optionsButton.checkTouch()) {
            nextMenuSetting = 2;
            nextMenu = 1;
        }
    }

    @Override
    public void draw(SpriteBatch batch, ShapeRenderer renderer) {

        batch.begin();


        batch.draw(backGround, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        playButton.draw(batch);
        optionsButton.draw(batch);
        creditsButton.draw(batch);

        super.draw(batch, renderer);
    }

    @Override
    public void load(AssetManager assets) {
        super.load(assets);
        assets.load("play.png", Texture.class);
        assets.load("bg.png", Texture.class);
        assets.load("options.png", Texture.class);
        assets.load("credits.png", Texture.class);

    }

    @Override
    public void initialiseResources() {
        if (!initialised) {
            super.initialiseResources();
            backGround = (assets.get("bg.png"));

            play = (assets.get("play.png"));
            credits = (assets.get("credits.png"));
            options = (assets.get("options.png"));

            playButton = new Button(new Rectangle(Gdx.graphics.getWidth()/3-PLAYSIZE/2, Gdx.graphics.getHeight()/2 - PLAYSIZE/2, PLAYSIZE, PLAYSIZE), play);
            optionsButton = new Button(new Rectangle(Gdx.graphics.getWidth()*2/3-PLAYSIZE/3, Gdx.graphics.getHeight()/2 - PLAYSIZE/3 + PLAYSIZE/3, PLAYSIZE/1.5f, PLAYSIZE/1.5f), options);
            creditsButton = new Button(new Rectangle(Gdx.graphics.getWidth()*2/3-PLAYSIZE/3, Gdx.graphics.getHeight()/2 - PLAYSIZE/3 - PLAYSIZE/3, PLAYSIZE/1.5f, PLAYSIZE/1.5f), credits);
            initialised = true;
        }
    }

}
