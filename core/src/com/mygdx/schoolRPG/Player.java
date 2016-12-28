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
public class Player extends NPC {


    public Player(AssetManager assets, String baseName, float x, float y, float width, float height, float floorHeight, boolean movable, CharacterMaker characterMaker) {
        super(assets, baseName, x, y, width, height, floorHeight, movable, characterMaker, 0, null);
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

    public void move(boolean allowToMove) {
        //if (!falling) {
            /*if (!canUp && speedY > 0) speedY = 0;
            if (!canDown && speedY < 0) speedY = 0;
            if (!canLeft && speedX < 0) speedX = 0;
            if (!canRight && speedX > 0) speedX = 0;*/
        if (allowToMove && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            isRunning = true;
        } else {
            isRunning = false;
        }

        if (allowToMove && Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !falling) {
            speedX -= 1;
            //if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) pushCount = -2;
            if (!pushLeft) pushCount = -2;
        }
        else if (speedX < 0) {
            if (speedX == -1){
                speedX = 0;
            } else {
                speedX += 2;
            }
        }
        if (allowToMove && Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT) && !falling) {
            speedX += 1;
            if (!pushRight) pushCount = -2;
        }
        else if (speedX > 0) {
            if (speedX == 1){
                speedX = 0;
            } else {
                speedX -= 2;
            }
        }
        if (allowToMove && Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.DOWN) && !falling) {
            speedY += 1;
            if (!pushUp) pushCount = -2;
        }
        else if (speedY > 0) speedY -= 1;
        if (allowToMove && Gdx.input.isKeyPressed(Input.Keys.DOWN) && !Gdx.input.isKeyPressed(Input.Keys.UP) && !falling) {
            speedY -= 1;
            if (!pushDown) pushCount = -2;
        }
        else if (speedY < 0) speedY += 1;
        //}
        int maxSpeedX, maxSpeedY;
        if (isRunning) {
            maxSpeedX = 32;
            maxSpeedY = 20;
        } else {
            maxSpeedX = 16;
            maxSpeedY = 10;
        }
        if (Math.abs(speedX) > maxSpeedX) {
            if (speedX < 0) speedX = -maxSpeedX;
            else  speedX = maxSpeedX;

        }
        if (Math.abs(speedY) > maxSpeedY) {
            if (speedY < 0) speedY = -maxSpeedY;
            else speedY = maxSpeedY;

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
            if ((speedX > 0 && canRight) || (speedX < 0 && canLeft)) hitBox.x += (float)speedX/10.0f;
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
}
