package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.schoolRPG.menus.GameMenu;
import com.mygdx.schoolRPG.tools.*;

/**
 * Created by Kraft on 27.12.2014.
 */
public class Player extends NPC {


    public Player(AssetManager assets, String baseName, float x, float y, float width, float height, float floorHeight, boolean movable, CharacterMaker characterMaker, World world) {
        super(assets, baseName, x, y, width, height, floorHeight, movable, characterMaker, 0, world);
        isControlled = true;
    }


    public void blockControls() {
        controlsBlocked = true;
    }

    public void platformMove(GameMenu menu) {
        if (menu.android) {
            movingConfiguration.updateMovingAndroid(menu);
        } else {
            movingConfiguration.updateMoving(Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.UP, Input.Keys.DOWN, Input.Keys.SHIFT_LEFT, Input.Keys.Z, Input.Keys.E);
        }
        type = 2;
        if (!controlsBlocked && movingConfiguration.movingLeft > 0) speedX -= 2;
        else if (speedX < 0) speedX += 2;
        if (!controlsBlocked && movingConfiguration.movingRight > 0) speedX += 2f;
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
        if (inGoo || inWater) jumpStep = 20;

        if (!controlsBlocked && movingConfiguration.jump == 1) {
            if (!jumping) {
                jumpTicks = maxJumpTicks;
                jumping = true;
                //System.out.println(pSpeed);
                if (pSpeed > 13) {
                    if (!inWater && !inGoo) djump.play(world.menu.soundVolume / 100.0f);
                    if (additionalJumpsCount > 0) {
                        additionalJumps = additionalJumpsCount-1;
                    }
                } else {
                    if (!inWater && !inGoo) jump.play(world.menu.soundVolume / 100.0f);
                    if (!inWater && !inGoo) jump.play(world.menu.soundVolume / 100.0f);
                    additionalJumps = additionalJumpsCount;
                }
                additionalJumpTicks = maxAdditionalJumpTicks;
                canUp = true;
            }
        } else if (!controlsBlocked && movingConfiguration.jump > 0) {
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
                if (!inWater && !inGoo && additionalJumpTicks == maxAdditionalJumpTicks) {
                    djump.play(world.menu.soundVolume / 100.0f);
                }
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
            if (jumping){
                land.play(world.menu.soundVolume / 100.0f);
            }
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
        if (controlsBlocked && movingConfiguration.allZeroesPlatform()) {
            controlsBlocked = false;
        }

        if (Math.abs(pSpeed) < 15 && Math.abs(speedX) > 2 && walkingFrames != 0 && walkingFrames % 10 == 0) {
            step.play(world.menu.soundVolume / 100.0f);
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
