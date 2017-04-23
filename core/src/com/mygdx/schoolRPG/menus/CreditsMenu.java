package com.mygdx.schoolRPG.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.schoolRPG.tools.Button;

/**
 * Created by user on 16.07.2014.
 */
public class CreditsMenu extends Menu {

    Texture backGround;
    Texture back;
    Button backButton;
    float BACKSIZE;

    public CreditsMenu(int id, boolean android) {
        super(id, android);
        ID = 3;
        BACKSIZE = Gdx.graphics.getWidth()/3.5f;
    }

    @Override
    public void load(AssetManager assets) {
        super.load(assets);
        //assets.load("back.png", Texture.class);
    }

    @Override
    public void initialiseResources() {
        if (!initialised) {
            super.initialiseResources();
            //backGround = assets.get(ID + "/bg.png", Texture.class);
            backGround = assets.get("bg.png", Texture.class);
            //back = assets.get("back.png", Texture.class);
            backButton = new Button(new Rectangle(Gdx.graphics.getWidth()/2 - BACKSIZE/2, Gdx.graphics.getHeight()/3 - BACKSIZE/2, BACKSIZE, BACKSIZE), back);
            initialised = true;
        }
    }


    public void invalidate() {
        super.invalidate();
        //System.out.println(nextMenu);
        if (backButton.checkTouch()) {
            //System.out.println(nextMenu);
            nextMenu = 0;
            //System.out.println(nextMenu);
        }
    }

    @Override
    public void draw(SpriteBatch batch, ShapeRenderer renderer) {
        batch.begin();
        batch.draw(backGround, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        backButton.draw(batch);
        super.draw(batch, renderer);
    }

}
