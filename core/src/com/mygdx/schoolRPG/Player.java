package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.schoolRPG.tools.CharacterMaker;
import com.mygdx.schoolRPG.tools.JoyStick;
import com.mygdx.schoolRPG.tools.MultiTile;

/**
 * Created by Kraft on 27.12.2014.
 */
public class Player extends HittableEntity {

    int speedX=0, speedY=0;
    float oldX, oldY;
    String spritesPath;
    //Texture front, back, left, right;
    PlayerMultiTile poses;
    TextureRegion curPose;
    int jumpTicks = 15;
    boolean jumping = false, setToJump = false;

    public Player(AssetManager assets, String baseName, float x, float y, float width, float height, float floorHeight, boolean movable) {
        super(assets, null, x, y, width, height, floorHeight, movable);
        spritesPath = baseName;
        poses = new PlayerMultiTile(spritesPath, assets);
    }

    public void move(JoyStick leftGameJoy) {
        speedX = (int)((float) leftGameJoy.joyOffsetX() / 16 * 10);
        speedY = (int)((float) leftGameJoy.joyOffsetY() / 16 * 10);
        x += (float)speedX/10.0f;
        y += (float)speedY/10.0f;
    }

    public void move() {
        if (!falling) {
            if (Gdx.input.isKeyPressed(Input.Keys.A)) speedX -= 2;
            else if (speedX < 0) speedX += 2;
            if (Gdx.input.isKeyPressed(Input.Keys.D)) speedX += 2;
            else if (speedX > 0) speedX -= 2;
            if (Gdx.input.isKeyPressed(Input.Keys.W)) speedY += 1;
            else if (speedY > 0) speedY -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.S)) speedY -= 1;
            else if (speedY < 0) speedY += 1;
        }
        if (Math.abs(speedX) > 16) {
            if (speedX < 0) speedX = -16;
            else  speedX = 16;
        }
        if (Math.abs(speedY) > 10) {
            if (speedY < 0) speedY = -10;
            else speedY = 10;
        }
        oldX = hitBox.x;
        oldY = hitBox.y;
        hitBox.x += (float)speedX/10.0f;
        hitBox.y -= (float)speedY/10.0f;

    }


    public void platformMove() {
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) speedX -= 2;
        else if (speedX < 0) speedX += 2;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) speedX += 2f;
        else if (speedX > 0) speedX -= 2;
        if (Math.abs(speedX) > 20) {
            if (speedX < 0) speedX = -20;
            else  speedX = 20;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            if (pSpeed == 0 && !jumping) {
                jumpTicks = 15;
                jumping = true;
            } else if (pSpeed > 0) {
                jumpTicks = 0;
            }
            if (jumpTicks > 0) {
                pSpeed = -25;
                jumpTicks--;
            }
        } else {
            jumpTicks = 0;
        }

        if (pSpeed == 0 && jumpTicks == 0) {
            jumping = false;
        }

        oldX = hitBox.x;
        oldY = hitBox.y;
        hitBox.x += (float)speedX/10.0f;
        //hitBox.y -= (float)speedY/10.0f;

    }

    @Override
    public void platformFall() {
        oldY = hitBox.y;
        super.platformFall();
        speedY = pSpeed;
    }

    @Override
    public void dropShadow(SpriteBatch batch, float offsetX, float offsetY, int tileWidth, int tileHeight, Texture shadow) {
        super.initialiseIfNeeded();
        if (Math.abs(hitBox.x) - Math.abs(oldX) == 0.2f) hitBox.x = oldX;
        if (Math.abs(hitBox.y) - Math.abs(oldY) == 0.1f) hitBox.y = oldY;
        if (shadow != null) {
            batch.draw(shadow, offsetX + hitBox.getX() + hitBox.getWidth() / 2 - shadow.getWidth() / 2, offsetY - hitBox.getY() + shadow.getHeight() / 2 - floorHeight, shadow.getWidth(), shadow.getHeight());
        }
    }

    @Override
    public void draw(SpriteBatch batch, float offsetX, float offsetY, int tileWidth, int tileHeight) {
        super.initialiseIfNeeded();
        poses.initialiseIfNeeded(assets);
        x = hitBox.x;
        y = hitBox.y;
        if (Math.abs(hitBox.x-oldX)>0.3f || Math.abs(speedX) > 2) {
            graphicX = x;
        }
        /*if (Math.abs(hitBox.y - oldY2) < 0.3f) {
            hitBox.y = oldY2;
            graphicY = hitBox.y;
        }*/
        if ((Math.abs(hitBox.y-oldY)>0.5f || Math.abs(speedY) > 1)) {
            graphicY = y;
        }
        if (spritesPath != null) {
            //curPose = poses.getTile(PlayerMultiTile.PlayerPose.FRONT);
            if (jumping) {
                curPose = poses.getTile(PlayerMultiTile.PlayerPose.JUMP);
                setToJump = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W) && !jumping) {
                curPose = poses.getTile(PlayerMultiTile.PlayerPose.BACK);
                setToJump = false;
            } else if (Gdx.input.isKeyPressed(Input.Keys.S) && !jumping)  {
                curPose = poses.getTile(PlayerMultiTile.PlayerPose.FRONT);
                setToJump = false;
            } else {
                graphicY = y;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                if (jumping) {
                    curPose = poses.getTile(PlayerMultiTile.PlayerPose.JUMP_LEFT);
                } else {
                    curPose = poses.getTile(PlayerMultiTile.PlayerPose.LEFT);
                    setToJump = false;
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                if (jumping) {
                    curPose = poses.getTile(PlayerMultiTile.PlayerPose.JUMP_RIGHT);
                } else {
                    curPose = poses.getTile(PlayerMultiTile.PlayerPose.RIGHT);
                    setToJump = false;
                }
            } else {
                graphicX = x;
            }
            if (curPose == null || (setToJump && !jumping)) {
                curPose = poses.getTile(PlayerMultiTile.PlayerPose.FRONT);
            }
            if (poses != null) {
                batch.draw(curPose, offsetX + graphicX , offsetY - graphicY + floorHeight - z, curPose.getRegionWidth(), curPose.getRegionHeight());
            }
        }
    }

    public void draw(SpriteBatch batch, CharacterMaker characterMaker, float offsetX, float offsetY, int tileWidth, int tileHeight) {
        super.initialiseIfNeeded();
        x = hitBox.x;
        y = hitBox.y;
        if (Math.abs(hitBox.x-oldX)>0.3f || Math.abs(speedX) > 2) {
            graphicX = x;
        }
        if ((Math.abs(hitBox.y-oldY)>0.5f || Math.abs(speedY) > 1)) {
            graphicY = y;
        }
        characterMaker.draw(batch, 0, offsetX + graphicX + hitBox.width/2, offsetY - graphicY + floorHeight - z, Math.abs(speedX/10), Math.abs(speedY/10));
    }
}
