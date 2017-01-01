package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.schoolRPG.tools.AnimationSequence;
import com.mygdx.schoolRPG.tools.CharacterMaker;
import com.mygdx.schoolRPG.tools.JoyStick;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Kraft on 09.06.2016.
 */
public class NPC extends HittableEntity {
    int speedX=0, speedY=0;
    float oldX, oldY;
    String spritesPath;
    //Texture front, back, left, right;
    PlayerMultiTile poses;
    TextureRegion curPose;
    int maxJumpTicks = 10, maxAdditionalJumpTicks = 10, additionalJumps;
    int jumpTicks, additionalJumpTicks, pushCount = 0, additionalJumpsCount = 1;
    boolean jumping = false, setToJump = false;
    boolean lastRight, lastDown, startedAdditionalJump;
    CharacterMaker characterMaker;
    float diffX, diffY;
    boolean pushUp = false, pushDown = false, pushLeft = false, pushRight = false;
    AnimationSequence chargo;
    boolean controlsBlocked = false;
    int charId = 0;
    String name = "";
    public ArrayList<Boolean> flags;
    public ArrayList<String> flagNames;
    public int flagsCount = 0;
    public Color charColor;
    boolean isRunning = false;

    public NPC(AssetManager assets, String baseName, float x, float y, float width, float height, float floorHeight, boolean movable, CharacterMaker characterMaker, int charId, String charPath) {
        super(assets, (String)null, x, y, width, height, floorHeight, movable, 0);
        //wh = 10;
        this.charId = charId;
        type = 2;
        spritesPath = baseName;
        jumpTicks = maxJumpTicks;
        if (spritesPath != null) {
            poses = new PlayerMultiTile(spritesPath, assets);
            chargo = new AnimationSequence(assets, spritesPath.substring(0, spritesPath.length()-4)+"go.png", 20, true, 5);
        }
        this.characterMaker = characterMaker;
        flags = new ArrayList<Boolean>();
        flagNames = new ArrayList<String>();
        if (charPath != null) {
            try {
                float r, g, b;
                BufferedReader in = new BufferedReader(new FileReader(charPath + "/stats"));
                String line = in.readLine();
                name = line;
                line = in.readLine();
                flagsCount = Integer.parseInt(line);
                for (int j =0; j < flagsCount; ++j) {
                    line = in.readLine();
                    flagNames.add(line);
                    line = in.readLine();
                    if (line.equals("0")) {
                        flags.add(false);
                    } else {
                        flags.add(true);
                    }
                }
                line = in.readLine();
                r = Float.parseFloat(line);
                line = in.readLine();
                g = Float.parseFloat(line);
                line = in.readLine();
                b = Float.parseFloat(line);
                charColor = new Color(r, g, b, 1.0f);
                in.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    /*Rectangle getTexRect() {
        return new Rectangle(hitBox.x, hitBox.y, hitBox.width, 0);
        //return null;
    }*/

    public void invalidatePose(boolean stand, boolean notPush) {
        if (characterMaker != null) {
            characterMaker.go = (Math.abs(speedX) > 5 || Math.abs(speedY) > 5);
        }

        if (!stand) {
            diffX = hitBox.x - oldX;
            diffY = hitBox.y - oldY;
            diffY += 0.25f*((1/diffY)*diffY);
            /*if (Math.abs(diffX) > 0.15f && Math.abs(diffX) < 0.3f) {
                diffX = 0.5f*((1/diffX)*diffX);
            } else if (Math.abs(diffX) < 0.25f) {
                diffX = 0;
            }*/
            if (Math.abs(diffY) < 0.11f) {
                diffY = 0;
            }
        } else {
            diffX = 0;
            diffY = 0;
        }
        if (characterMaker != null) {
            characterMaker.invalidateBobbing(charId, speedX, speedY);
            if (characterMaker.directionsCheck(charId)) {
                pushCount = -2;
            }
            characterMaker.pushes.set(charId, (pushCount > 1 && !notPush));

            if (Math.abs(diffX) > 0.5f) {
                pushCount = 0;
            }
            if (Math.abs(diffY) > 0.5f) {
                pushCount = 0;
            }
        }

    }

    @Override
    public void draw(SpriteBatch batch, float offsetX, float offsetY, int tileWidth, int tileHeight, boolean platformMode, boolean active, int activeX, int activeY) {
        super.initialiseIfNeeded();

        if (active) {
            this.active = assets.get("active.png");
            int height = 0;
            if (anim != null) {
                height = anim.getFirstFrame().getRegionHeight();
            } else if (tex != null) {
                height = tex.getHeight();
            } else if (texR != null) {
                height = texR.getRegionHeight();
            }
            batch.draw(this.active, offsetX + x + activeX, offsetY - y + 3 + height + activeY);
        }

        if (poses != null) poses.initialiseIfNeeded(assets);
        x = hitBox.x;
        y = hitBox.y;
        //h = y-hitBox.height;
        if (!floor) {
            //if (hitBox.width == 16)h = hitBox.y-hitBox.height-12;
            //else h = hitBox.y-hitBox.height-8;
        } else {
            h = 999999;
        }

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
            if (Gdx.input.isKeyPressed(Input.Keys.UP) && !jumping) {
                curPose = poses.getTile(PlayerMultiTile.PlayerPose.BACK);
                setToJump = false;
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && !jumping)  {
                curPose = poses.getTile(PlayerMultiTile.PlayerPose.FRONT);
                setToJump = false;
            } else {
                graphicY = y;
            }
            int animDraw = 0;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                if (jumping) {
                    curPose = poses.getTile(PlayerMultiTile.PlayerPose.JUMP_LEFT);
                } else {
                    if (speedX < -5) {
                        curPose = null;
                        animDraw = 1;
                    }
                    else curPose = poses.getTile(PlayerMultiTile.PlayerPose.LEFT);
                    setToJump = false;
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                if (jumping) {
                    curPose = poses.getTile(PlayerMultiTile.PlayerPose.JUMP_RIGHT);
                } else {
                    if (speedX > 5) {
                        curPose = null;
                        animDraw = 2;
                    }
                    else curPose = poses.getTile(PlayerMultiTile.PlayerPose.RIGHT);
                    setToJump = false;
                }
            } else {
                graphicX = x;
            }
            if (animDraw == 0 && (curPose == null || (setToJump && !jumping))) {
                curPose = poses.getTile(PlayerMultiTile.PlayerPose.FRONT);
            }
            if (poses != null && curPose != null) {
                batch.draw(curPose, offsetX + graphicX , offsetY - graphicY + floorHeight - z + 3, curPose.getRegionWidth(), curPose.getRegionHeight());
            } else if (animDraw == 1) {
                TextureRegion tmp = new TextureRegion(chargo.getCurrentFrame(false));
                tmp.flip(true, false);
                batch.draw(tmp, offsetX + graphicX - 3, offsetY - graphicY + floorHeight - z + 3, tmp.getRegionWidth(), tmp.getRegionHeight());
            } else if (animDraw == 2) {
                batch.draw(chargo.getCurrentFrame(false), offsetX + graphicX - 2, offsetY - graphicY + floorHeight - z + 3, chargo.getFirstFrame().getRegionWidth(), chargo.getFirstFrame().getRegionHeight());
            }
        }
    }

    public void draw(SpriteBatch batch, float offsetX, float offsetY, boolean active) {
        super.initialiseIfNeeded();

        if (active) {
            this.active = assets.get("active.png");
            int height = 50;
            int width = 4;
            if (characterMaker.sprites.get(charId) != null) {
                height = characterMaker.sprites.get(charId).getHeight() + 3;
                //width = characterMaker.sprites.get(charId).getWidth();
            }
            batch.draw(this.active, offsetX + x + this.active.getWidth()/2, offsetY - y + height);
        }

        x = hitBox.x;
        y = hitBox.y;
        //if (hitBox.width == 16)h = hitBox.y-8;
        if (!floor) {
            h = hitBox.y-hitBox.height;//-6;
        } else {
            h = 999999;
        }

        if (Math.abs(hitBox.x-oldX)>0.3f || Math.abs(speedX) > 2) {
            graphicX = x;
        }
        if ((Math.abs(hitBox.y-oldY)>0.5f || Math.abs(speedY) > 1)) {
            graphicY = y;
        }
        //System.out.println(speedX + " " + speedY);
        //System.out.println(hitBox.x + " " + hitBox.y);

        characterMaker.draw(batch, charId, offsetX + graphicX + hitBox.width / 2, offsetY - graphicY - hitBox.height + floorHeight - z, Math.abs((int)Math.floor(diffX*10))/10, Math.abs((int)Math.floor(diffY)));

    }
}
