package com.mygdx.schoolRPG.menus;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.schoolRPG.World;
import com.mygdx.schoolRPG.tools.JoyStick;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by user on 16.07.2014.
 */
public class GameMenu extends Menu {

    public int ID = 1;
    JoyStick leftGameJoy, rightGameJoy;
    Texture joyBase, joy;
    Rectangle leftJoyRect, rightJoyRect;
    int halfScreenHeight = Gdx.graphics.getHeight() / 2;
    int halfScreenWidth = Gdx.graphics.getWidth() / 2;
    float JOYSIZE, JOYBASESIZE;
    public int curWorld = 0;
    public ArrayList<World> worlds;
    World transferringWorld;

    public GameMenu(int id, boolean android) {
        super(id, android);
        allowPause = true;
        JOYSIZE = Gdx.graphics.getHeight()/6.25f;
        JOYBASESIZE = Gdx.graphics.getHeight()/5.5f;
        if (android) {
            leftJoyRect = new Rectangle(Gdx.graphics.getWidth() / 7 - JOYBASESIZE / 2, Gdx.graphics.getHeight() / 5 - JOYBASESIZE / 2, JOYBASESIZE, JOYBASESIZE);
            rightJoyRect = new Rectangle(Gdx.graphics.getWidth() * 6 / 7 - JOYBASESIZE / 2, Gdx.graphics.getHeight() / 5 - JOYBASESIZE / 2, JOYBASESIZE, JOYBASESIZE);
        }
        worlds = new ArrayList<World>();

        //worlds.add(new World(this, "worlds/ultimatetest1", -1));
    }

    @Override
    public void changeSetting(int id) {
        int found = -1;
        for (int i =0; i < worlds.size(); ++i) {
            if (worlds.get(i).worldDir.path().equals(nextMenuMessage)) {
                found = i;
            }
        }
        if (found == -1) {
            worlds.add(new World(this, nextMenuMessage, id));
            curWorld = worlds.size()-1;
            initialised = false;
        } else {
            curWorld = found;
            worlds.get(curWorld).save = id;
            worlds.get(curWorld).loadState();
        }
    }

    @Override
    public void load(AssetManager assets) {
        super.load(assets);
        if (android) {
            assets.load("joy.png", Texture.class);
            assets.load("joybase.png", Texture.class);
        }
        assets.load("dialog_overlay1.png", Texture.class);
        assets.load("dialog_overlay2.png", Texture.class);
        worlds.get(curWorld).load(assets);
        //changeWorld(0);
    }

    @Override
    public void initialiseResources() {
        if (!initialised) {
            super.initialiseResources();
            //worlds.get(curWorld).initialiseResources(assets);
            if (android) {
                joyBase = (assets.get("joybase.png"));//new Texture(Gdx.files.internal("joybase.png"));
                joy = (assets.get("joy.png"));//new Texture(Gdx.files.internal("joy.png"));
                leftGameJoy = new JoyStick(leftJoyRect, joyBase, joy, Color.BLACK, Gdx.graphics.getWidth()/20f);
                rightGameJoy = new JoyStick(rightJoyRect, joyBase, joy, Color.BLACK, Gdx.graphics.getWidth()/20f);
            }
            if (worlds.get(curWorld).initialised) {
                if (transferringWorld != null) {
                    worlds.get(curWorld).transferFromWorld(transferringWorld);
                }
                initialised = true;
            } else if (assets.update() && worlds.get(curWorld).loaded) {
                worlds.get(curWorld).initialiseResources(assets);
            } else {
                worlds.get(curWorld).load(assets);
            }
            //initialised = true;
        }
    }

    /*@Override
    public void updateLanguage() {

    }*/
    private void copyFile(String dir1, String dir2, String file1, String file2) {
        Path copy_from_1 = Paths.get(dir1, file1);
        File f = new File(copy_from_1.toString());
        if (!f.exists()) return;
        Path copy_to_1 = Paths.get(dir2, file2);
        try {
            Files.copy(copy_from_1, copy_to_1, REPLACE_EXISTING, COPY_ATTRIBUTES, NOFOLLOW_LINKS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeWorld(World w) {
        World newWorld = new World(this, "worlds/" + w.nextWorldDir, curWorld);
        initialised = false;
        for (int i =0; i < w.uniqueItemNamesForTransfer.size(); ++i) {
            copyFile(w.worldDir + "/items", "worlds/" + w.nextWorldDir + "/items", w.uniqueItemNamesForTransfer.get(i) + ".xml", w.uniqueItemNamesForTransfer.get(i) + ".xml");
            copyFile(w.worldDir + "/items/icons", "worlds/" + w.nextWorldDir + "/items/icons", w.uniqueItemNamesForTransfer.get(i) + ".png", w.uniqueItemNamesForTransfer.get(i) + ".png");
            copyFile(w.worldDir + "/items/big_icons", "worlds/" + w.nextWorldDir + "/items/big_icons", w.uniqueItemNamesForTransfer.get(i) + ".png", w.uniqueItemNamesForTransfer.get(i) + ".png");
            copyFile(w.worldDir + "/items/sides", "worlds/" + w.nextWorldDir + "/items/sides", w.uniqueItemNamesForTransfer.get(i) + ".png", w.uniqueItemNamesForTransfer.get(i) + ".png");
        }
        transferringWorld = w;
        worlds.set(worlds.indexOf(w), newWorld);

    }

    public void invalidate() {
        super.invalidate();
        if (android) {
            leftGameJoy.checkTouch();
            rightGameJoy.checkTouch();
        }
        if (!worlds.get(curWorld).initialised) {
            initialised = false;
        }
        else {
            worlds.get(curWorld).invalidate();
            if (worlds.get(curWorld).worldChange) {
                changeWorld(worlds.get(curWorld));
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch, ShapeRenderer renderer) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        //if (worlds.get(curWorld).initialised) {
            worlds.get(curWorld).draw(batch);
        //}
        batch.end();
        if (android) {
            leftGameJoy.draw(batch, renderer, 1.0f);
            rightGameJoy.draw(batch, renderer, 1.0f);
        }
        super.draw(batch, renderer);

    }

    public void stopSounds() {
        if (worlds.get(curWorld).currentSound != null) {
            worlds.get(curWorld).currentSound.stop();
        }
    }
}


