package com.mygdx.schoolRPG.menus;

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

import java.util.ArrayList;

/**
 * Created by user on 16.07.2014.
 */
public class OldGameMenu extends Menu {

    public int ID = 4;
    Texture backGround;
    JoyStick leftGameJoy, rightGameJoy;
    Texture joyBase, joy, player, weapon;
    TextureRegion playerReg, wepReg;
    Rectangle leftJoyRect, rightJoyRect, gameRect;
    int halfScreenHeight = Gdx.graphics.getHeight() / 2;
    int halfScreenWidth = Gdx.graphics.getWidth() / 2;
    float JOYSIZE, JOYBASESIZE;
    float PLAYER_SPEED = 5.0f, PLAYER_MAXSPEED = 0.5f;
    float charX, charY, charSpeedX=0, charSpeedY=0;
    float targAngle = 0, curCharAngle = 0, curWepAngle;
    int curWorld = 0;
    ArrayList<World> worlds;
    //boolean just = false;

    public OldGameMenu(int id, boolean android) {
        super(id, android);
        allowPause = true;
        JOYSIZE = Gdx.graphics.getHeight()/6.25f;
        JOYBASESIZE = Gdx.graphics.getHeight()/5.5f;
        if (android) {
            leftJoyRect = new Rectangle(Gdx.graphics.getWidth() / 7 - JOYBASESIZE / 2, Gdx.graphics.getHeight() / 5 - JOYBASESIZE / 2, JOYBASESIZE, JOYBASESIZE);
            rightJoyRect = new Rectangle(Gdx.graphics.getWidth() * 6 / 7 - JOYBASESIZE / 2, Gdx.graphics.getHeight() / 5 - JOYBASESIZE / 2, JOYBASESIZE, JOYBASESIZE);
        }
        gameRect = new Rectangle(Gdx.graphics.getWidth()/12, Gdx.graphics.getHeight()/7, Gdx.graphics.getWidth()*10/12, Gdx.graphics.getHeight()*5/7);
        charX = gameRect.getX() + gameRect.getWidth()/2;
        charY = gameRect.getY() + gameRect.getHeight()/2;
        worlds = new ArrayList<World>();
    }

    @Override
    public void load(AssetManager assets) {
        super.load(assets);
        //assets.load(ID+"/bg.png", Texture.class);
        assets.load("gbg.png", Texture.class);
        assets.load("wep.png", Texture.class);
        assets.load("charUp.png", Texture.class);
        if (android) {
            assets.load("joy.png", Texture.class);
            assets.load("joybase.png", Texture.class);
        }
    }

    public void changeWorld(int n) {
        curWorld = n;
        worlds.get(n).load(assets);
    }

    @Override
    public void initialiseResources() {
        if (!initialised) {
            super.initialiseResources();
            //backGround = assets.get(ID + "/bg.png", Texture.class);
            backGround = assets.get("gbg.png", Texture.class);
            if (android) {
                joyBase = (assets.get("joybase.png"));//new Texture(Gdx.files.internal("joybase.png"));
                joy = (assets.get("joy.png"));//new Texture(Gdx.files.internal("joy.png"));
                leftGameJoy = new JoyStick(leftJoyRect, joyBase, joy, Color.BLACK, Gdx.graphics.getWidth()/20f);
                rightGameJoy = new JoyStick(rightJoyRect, joyBase, joy, Color.BLACK, Gdx.graphics.getWidth()/20f);
            }
            player = (assets.get("charUp.png"));//new Texture(Gdx.files.internal("joy.png"));
            weapon = (assets.get("wep.png"));//new Texture(Gdx.files.internal("joy.png"));
            initialised = true;
            playerReg = new TextureRegion(player);
            wepReg = new TextureRegion(weapon);
        }
        /*if (!worlds.get(curWorld).initialised && assets.update()) {

        }*/
    }


    public void invalidate() {
        super.invalidate();
        if (android) {
            leftGameJoy.checkTouch();
            rightGameJoy.checkTouch();
        }
        rotateChar();
        moveChar();
    }

    @Override
    public void draw(SpriteBatch batch, ShapeRenderer renderer) {
        Gdx.gl.glClearColor(0.6f, 0.6f, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(backGround, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(playerReg, charX - JOYSIZE/4, charY - JOYSIZE/4, JOYSIZE/4, JOYSIZE/4, JOYSIZE/2, JOYSIZE/2, 1, 1, curCharAngle/*+30*/);
        batch.draw(wepReg, charX-JOYSIZE/4, charY+JOYSIZE/5, JOYSIZE/4, -JOYSIZE/5, JOYSIZE/2, JOYSIZE/2, 1, 1, curWepAngle);
        batch.end();
        if (android) {
            leftGameJoy.draw(batch, renderer, 1.0f);
            rightGameJoy.draw(batch, renderer, 1.0f);
        }
        super.draw(batch, renderer);
    }

    private void rotateChar() {
        float prevAngle = targAngle;
        if (android) {
            if (rightGameJoy.joyOffsetX() != 0 && rightGameJoy.joyOffsetY() != 0) {
                if (rightGameJoy.joyOffsetX() < 0) {
                    targAngle = (float) (Math.toDegrees(Math.atan(rightGameJoy.joyOffsetY() / rightGameJoy.joyOffsetX())) + 90);
                } else {
                    targAngle = (float) (Math.toDegrees(Math.atan(rightGameJoy.joyOffsetY() / rightGameJoy.joyOffsetX())) - 90);
                }
            }
            //System.out.println(targAngle +" "+ curAngle);

        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                targAngle = 90;
                curCharAngle = 90;
                curWepAngle = 90;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                targAngle = -90;
                curCharAngle = -90;
                curWepAngle = -90;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP))  {
                targAngle = 0;
                curCharAngle = 0;
                curWepAngle = 0;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                targAngle = 180;
                curCharAngle = 180;
                curWepAngle = 180;
            }
        }

        if (prevAngle > 90 && targAngle < -90) {
            curCharAngle -= 360;//-curAngle-(180-curAngle);
            curWepAngle -= 360;//-curAngle-(180-curAngle);
        } else if (prevAngle < -90 && targAngle > 90) {
            curCharAngle += 360;//= -curAngle+(180+curAngle);
            curWepAngle += 360;//= -curAngle+(180+curAngle);
        }
        if (curWepAngle < targAngle) {
            curWepAngle += (targAngle - curWepAngle) / 5;
            curCharAngle += (targAngle - curCharAngle) / 13;
        } else if (curWepAngle > targAngle) {
            curWepAngle -= (curWepAngle - targAngle) / 5;
            curCharAngle -= (curCharAngle - targAngle) / 13;
        }
    }

    private void moveChar() {
        if (android) {
            charSpeedX = (float) leftGameJoy.joyOffsetX() / 16;
            charSpeedY = (float) leftGameJoy.joyOffsetY() / 16;
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.A)) charSpeedX -= 0.5f;
            else if (charSpeedX < 0) charSpeedX += 0.5f;
            if (Gdx.input.isKeyPressed(Input.Keys.D)) charSpeedX += 0.5f;
            else if (charSpeedX > 0) charSpeedX -= 0.5f;
            if (Gdx.input.isKeyPressed(Input.Keys.W)) charSpeedY += 0.5f;
            else if (charSpeedY > 0) charSpeedY -= 0.5f;
            if (Gdx.input.isKeyPressed(Input.Keys.S)) charSpeedY -= 0.5f;
            else if (charSpeedY < 0) charSpeedY += 0.5f;
            if (Math.abs(charSpeedX) > 4) charSpeedX = (Math.abs(charSpeedX)/charSpeedX)*4;
            if (Math.abs(charSpeedY) > 4) charSpeedY = (Math.abs(charSpeedY)/charSpeedY)*4;
        }
        charX += charSpeedX;
        charY += charSpeedY;
        if (charX < gameRect.getX()) charX = gameRect.getX();
        if (charY < gameRect.getY()) charY = gameRect.getY();
        if (charX > gameRect.getX() + gameRect.getWidth()) charX = (gameRect.getX() + gameRect.getWidth());
        if (charY > gameRect.getY() + gameRect.getHeight()) charY = (gameRect.getY() + gameRect.getHeight());
    }

}
//changes