package com.mygdx.schoolRPG.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by user on 16.07.2014.
 */
public class LoadingScreen {

    Texture loading;

    public LoadingScreen() {
        loading = new Texture(Gdx.files.internal("loading.png"));
    }

    public void draw(SpriteBatch batch) {
        float screenRatioX = Gdx.graphics.getWidth()/1280.0f;
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(loading, Gdx.graphics.getWidth()/screenRatioX/2 - loading.getWidth()/2, Gdx.graphics.getHeight()/screenRatioY/2 - loading.getHeight()/2);
        batch.end();
    }

}
