package com.mygdx.schoolRPG.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.schoolRPG.tools.Button;
import com.mygdx.schoolRPG.tools.CircularSelector;
import com.mygdx.schoolRPG.tools.MenuListSelector;

import java.util.ArrayList;

/**
 * Created by IVO on 15.07.2014.
 * main menu
 */

public class MainMenu extends Menu {
    public int ID = 0;

    float PLAYSIZE;

    public int curMenu = 0;
    Texture backGround, title, cursor, overlay;
    Texture play, options, credits;
    float overlayAngle = 0;

    //int halfScreenHeight = Gdx.graphics.getHeight() / 2;
    //int halfScreenWidth = Gdx.graphics.getWidth() / 2;
    Button playButton, optionsButton, creditsButton;
    MenuListSelector selector;
    CircularSelector cs;

    public MainMenu(int id, boolean android) {
        super(id, android);
        ID = 0;

        PLAYSIZE = Gdx.graphics.getHeight()/2.5f;

    }

    public void invalidate() {
        super.invalidate();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            int index = selector.getSelectedIndex();
            if (index == 0) {
                nextMenuSetting = 0;
                nextMenu = 1;
            } else if (index == 1) {
                nextMenuSetting = 2;
                nextMenu = 1;
            }
            else if (index == 2) {
                nextMenuSetting = 1;
                nextMenu = 1;
            }
        }
        /*if (playButton.checkTouch()) {
            nextMenuSetting = 0;
            nextMenu = 1;
        }

        if (creditsButton.checkTouch()) {
            nextMenuSetting = 1;
            nextMenu = 1;
        }

        if (optionsButton.checkTouch()) {
            nextMenuSetting = 2;
            nextMenu = 1;
        }*/
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
        batch.draw(title, Gdx.graphics.getWidth()/screenRatioX/2 - title.getWidth()/2, Gdx.graphics.getHeight()/screenRatioY - title.getHeight() * 1.5f);
        selector.draw(batch, false);
        cs.draw(batch, false);
        batch.end();
        //playButton.draw(batch);
        //optionsButton.draw(batch);
        //creditsButton.draw(batch);

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

    @Override
    public void initialiseResources() {
        if (!initialised) {
            super.initialiseResources();
            backGround = (assets.get("bg_new.png"));
            ArrayList<String> list= new ArrayList<String>();
            list.add("Play");
            list.add("Options");
            list.add("Info");

            title = assets.get("title.png", Texture.class);
            overlay = assets.get("bg_overlay.png", Texture.class);
            selector = new MenuListSelector(list, assets, "cursor.png", mainFont, Gdx.graphics.getHeight(), 0, 0, true);

            ArrayList<Texture> sprits = new ArrayList<Texture>();
            for (int i = 0; i < 1; ++i) {
                sprits.add(assets.get("cursor.png", Texture.class));
            }
            cs = new CircularSelector(list, sprits, mainFont, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/5, 100.0f);
            //play = (assets.get("play.png"));
            //credits = (assets.get("credits.png"));
            //options = (assets.get("options.png"));

            //playButton = new Button(new Rectangle(Gdx.graphics.getWidth()/3-PLAYSIZE/2, Gdx.graphics.getHeight()/2 - PLAYSIZE/2, PLAYSIZE, PLAYSIZE), play);
            //optionsButton = new Button(new Rectangle(Gdx.graphics.getWidth()*2/3-PLAYSIZE/3, Gdx.graphics.getHeight()/2 - PLAYSIZE/3 + PLAYSIZE/3, PLAYSIZE/1.5f, PLAYSIZE/1.5f), options);
            //creditsButton = new Button(new Rectangle(Gdx.graphics.getWidth()*2/3-PLAYSIZE/3, Gdx.graphics.getHeight()/2 - PLAYSIZE/3 - PLAYSIZE/3, PLAYSIZE/1.5f, PLAYSIZE/1.5f), credits);
            initialised = true;
        }
    }

}
