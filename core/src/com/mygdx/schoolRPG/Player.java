package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.schoolRPG.tools.*;

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
    int maxJumpTicks = 10, maxAdditionalJumpTicks = 10, additionalJumps;
    int jumpTicks, additionalJumpTicks, pushCount = 0, additionalJumpsCount = 1;
    boolean jumping = false, setToJump = false;
    boolean lastRight, lastDown, startedAdditionalJump;
    CharacterMaker characterMaker;
    float diffX, diffY;
    boolean pushUp = false, pushDown = false, pushLeft = false, pushRight = false;
    AnimationSequence chargo;
    boolean controlsBlocked = false;


    public Player(AssetManager assets, String baseName, float x, float y, float width, float height, float floorHeight, boolean movable, CharacterMaker characterMaker) {
        super(assets, (String)null, x, y, width, height, floorHeight, movable, 0);
        //wh = 10;
        type = 2;
        spritesPath = baseName;
        jumpTicks = maxJumpTicks;
        if (spritesPath != null) {
            poses = new PlayerMultiTile(spritesPath, assets);
            chargo = new AnimationSequence(assets, spritesPath.substring(0, spritesPath.length()-4)+"go.png", 20, true);
        }
        this.characterMaker = characterMaker;
    }

    public void move(JoyStick leftGameJoy) {
        speedX = (int)((float) leftGameJoy.joyOffsetX() / 16 * 10);
        speedY = (int)((float) leftGameJoy.joyOffsetY() / 16 * 10);
        x += (float)speedX/10.0f;
        y += (float)speedY/10.0f;
    }

    public void blockControls() {

        controlsBlocked = true;
    }

    public void move() {
        //if (!falling) {
            /*if (!canUp && speedY > 0) speedY = 0;
            if (!canDown && speedY < 0) speedY = 0;
            if (!canLeft && speedX < 0) speedX = 0;
            if (!canRight && speedX > 0) speedX = 0;*/
            if (Gdx.input.isKeyPressed(Input.Keys.A) && !falling) {
                speedX -= 1;
                if (Gdx.input.isKeyPressed(Input.Keys.D)) pushCount = -2;
                else if (!pushLeft) pushCount = -2;
            }
            else if (speedX < 0) {
                if (speedX == -1){
                    speedX = 0;
                } else {
                    speedX += 2;
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) && !falling) {
                speedX += 1;
                if (Gdx.input.isKeyPressed(Input.Keys.A)) pushCount = -2;
                else if (!pushRight) pushCount = -2;
            }
            else if (speedX > 0) {
                if (speedX == 1){
                    speedX = 0;
                } else {
                    speedX -= 2;
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W) && !falling) {
                speedY += 1;
                if (Gdx.input.isKeyPressed(Input.Keys.S)) pushCount = -2;
                else if (!pushUp) pushCount = -2;
            }
            else if (speedY > 0) speedY -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.S) && !falling) {
                speedY -= 1;
                if (Gdx.input.isKeyPressed(Input.Keys.W)) pushCount = -2;
                else if (!pushDown) pushCount = -2;
            }
            else if (speedY < 0) speedY += 1;
        //}
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
        float textX = hitBox.x;
        float textY = hitBox.y;
        hitBox.x += (float)speedX/10.0f;
        hitBox.y -= (float)speedY/10.0f;
        if (hitBox.x-textX > 0) lastRight = true;
        else if (hitBox.x-textX < 0) lastRight = false;
        if (hitBox.y-textY > 0) lastDown = true;
        else if (hitBox.y-textY < 0) lastDown = false;
    }


    public void platformMove() {
        type = 2;
        if (!controlsBlocked && (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))) speedX -= 2;
        else if (speedX < 0) speedX += 2;
        if (!controlsBlocked && (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))) speedX += 2f;
        else if (speedX > 0) speedX -= 2;
        if (!inWater && !inGoo) {
            if (Math.abs(speedX) > 20) {
                if (speedX < 0) speedX = -20;
                else  speedX = 20;
            }
        } else if (inGoo) {
            if (Math.abs(speedX) > 8) {
                if (speedX < 0) speedX = -8;
                else  speedX = 8;
            }
        } else {
            if (Math.abs(speedX) > 16) {
                if (speedX < 0) speedX = -16;
                else  speedX = 16;
            }
        }

        if (inWater || inGoo) {
            jumping = false;
        }

        int jumpStep = 35;
        if (inGoo) jumpStep = 14;

        if (!controlsBlocked && Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            if (!jumping) {
                jumpTicks = maxJumpTicks;
                jumping = true;
                //System.out.println(pSpeed);
                if (pSpeed > 13) {
                    if (additionalJumpsCount > 0) {
                        additionalJumps = additionalJumpsCount-1;
                    }
                } else {
                    additionalJumps = additionalJumpsCount;
                }
                additionalJumpTicks = maxAdditionalJumpTicks;
                canUp = true;
            }
        } else if (!controlsBlocked && Gdx.input.isKeyPressed(Input.Keys.Z)) {
            /*if (jumping && startedDoubleJump) {
                //startedDoubleJump = true;
                doubleJumpTicks = 0;
                startedDoubleJump = false;
            }*/
            if (!canUp) {
                jumpTicks = 0;
                if (additionalJumpTicks != maxAdditionalJumpTicks) {
                    additionalJumpTicks = 0;
                }
            }
            if (additionalJumpTicks > 0 && startedAdditionalJump) {
                pSpeed = -jumpStep;
                additionalJumpTicks--;
            }
            if (jumpTicks > 0) {
                pSpeed = -jumpStep;
                jumpTicks--;
            }
        } else {
            jumpTicks = 0;
            if (startedAdditionalJump && additionalJumpTicks < maxAdditionalJumpTicks) {
                additionalJumps--;
                if (additionalJumps <= 0) {
                    startedAdditionalJump = false;
                    additionalJumpTicks = 0;
                } else {
                    //additionalJumps--;
                    additionalJumpTicks = maxAdditionalJumpTicks;
                }
            } else if (additionalJumpTicks != 0) {
                if (additionalJumps > 0) {
                    startedAdditionalJump = true;
                    canUp = true;
                }

            }
        }

        if (pSpeed == 0 && jumpTicks == 0) {
            jumping = false;
            startedAdditionalJump = false;
            additionalJumpTicks = 0;
        }

        if (!controlsBlocked) {
            oldX = hitBox.x;
            oldY = hitBox.y;
            hitBox.x += (float)speedX/10.0f;
        }
        //hitBox.y -= (float)speedY/10.0f;
        if (controlsBlocked && (!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.LEFT)) && !Gdx.input.isKeyPressed(Input.Keys.D) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            controlsBlocked = false;
        }
    }

    @Override
    public void platformFall() {
        oldY = hitBox.y;
        super.platformFall();
        canDown = true;
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

    /*Rectangle getTexRect() {
        return new Rectangle(hitBox.x, hitBox.y, hitBox.width, 0);
        //return null;
    }*/

    public void invalidatePose(boolean stand, boolean notPush) {
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

        characterMaker.invalidateBobbing();
        if (characterMaker.directionsCheck()) {
            pushCount = -2;
        }
        characterMaker.push = (pushCount > 1 && !notPush);

        if (Math.abs(diffX) > 0.5f) {
            pushCount = 0;
        }
        if (Math.abs(diffY) > 0.5f) {
            pushCount = 0;
        }

    }

    @Override
    public void draw(SpriteBatch batch, float offsetX, float offsetY, int tileWidth, int tileHeight, boolean platformMode) {
        super.initialiseIfNeeded();
        poses.initialiseIfNeeded(assets);
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

    public void draw(SpriteBatch batch, float offsetX, float offsetY, int tileWidth, int tileHeight) {
        super.initialiseIfNeeded();
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

        characterMaker.draw(batch, 0, offsetX + graphicX + hitBox.width / 2, offsetY - graphicY - hitBox.height + floorHeight - z, Math.abs((int)Math.floor(diffX*10))/10, Math.abs((int)Math.floor(diffY)));
        /*if (characterMaker.cdc.lookDir == CharacterDirectionChecker.LookDirection.up || characterMaker.cdc.lookDir == CharacterDirectionChecker.LookDirection.down) {
            if (hitBox.width == 8) {
                if (Math.abs(speedX) < 5 && (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D))) {
                    if (Gdx.input.isKeyPressed(Input.Keys.A)) hitBox.x += 2;
                    else if (Gdx.input.isKeyPressed(Input.Keys.D)) hitBox.x -= 8;
                } else {
                    hitBox.x -= 4;
                }
                if (lastRight) {
                    speedX = 4;
                } else {
                    speedX = -4;
                }
                hitBox.width = 16;
                graphicX = hitBox.x;
                //h = hitBox.y-hitBox.height-3;
                pusher = true;
            } else {
                pusher = false;
            }
            if (hitBox.height == 9) {
                hitBox.height = 5;
                hitBox.y += 2;
                graphicY = hitBox.y;
            }
        } else {
            if (hitBox.width == 16) {
                hitBox.x += 4;
                //floorHeight -= 2;
                hitBox.width = 8;
                //h = hitBox.y-hitBox.height-1;
                graphicX = hitBox.x;
            }
            if (hitBox.height == 5) {
                hitBox.height = 9;
                if (Math.abs(speedY) < 3 && (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.S))) {
                    if (Gdx.input.isKeyPressed(Input.Keys.W)) hitBox.y += 2;
                    else if (Gdx.input.isKeyPressed(Input.Keys.S)) hitBox.y -= 4;
                } else {
                    hitBox.y -= 2;
                }
                if (lastDown) {
                    speedY = -3;
                } else {
                    speedY = 3;
                }
                graphicY = hitBox.y;
                pusher = true;
            } else {
                pusher = false;
            }
        }*/
        //System.out.println(pusher);
    }
}
