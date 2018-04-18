package com.mygdx.schoolRPG;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.schoolRPG.Entity;
import com.mygdx.schoolRPG.menus.MainMenu;
import com.mygdx.schoolRPG.tools.AnimationSequence;

import java.util.ArrayList;

/**
 * Created by Kraft on 16.07.2015.
 */
public class Particle extends Entity {

    ParticleProperties pp;
    float direction;
    float speed;
    float impulse;
    int curStateBounces;
    boolean platformMode;
    int curStateId;
    long curLoop = -1;
    long curStateStart = 0;
    float XSpeed = 0;
    float YSpeed = 0;
    int curStateRemainingSpawns;
    long lastSpawned;

    public Particle(AssetManager assets, ParticleProperties pp, ParticleProperties.ParticleSpawnProperties spawnProperties, boolean platformMode, float spawnX, float spawnY, float spawnZ) {
        super(assets, (Texture)null, spawnX + spawnProperties.spawnX, spawnY + spawnProperties.spawnY, pp.h, pp.floorHeight, 0);
        z = spawnZ + spawnProperties.spawnZ;
        this.pp = pp;
        curStateId = 0;
        if (this.pp.statesTexes.get(curStateId) != null) tex = this.pp.statesTexes.get(curStateId);
        else if (this.pp.statesAnims.get(curStateId) != null) anim = this.pp.statesAnims.get(curStateId);
        direction = spawnProperties.spawnDir + (float)Math.random() * spawnProperties.dirSpread - spawnProperties.dirSpread/2.0f;
        speed = spawnProperties.spawnSpeed + (float)Math.random() * spawnProperties.speedSpread;
        impulse = spawnProperties.spawnImpulse + (float)Math.random() * spawnProperties.impulseSpread;
        curStateBounces = 0;
        centered = true;
        r = pp.r;
        this.platformMode = platformMode;
        if (platformMode) z = 0;
        this.h = y;
        changeState(0);
        if (spawnProperties.spawnSound != null) spawnProperties.spawnSound.play(pp.menu.soundVolume/100.0f);
    }

    public void bounce(boolean floor, boolean side) {
        if (floor) {
            curStateBounces++;
            impulse = -impulse * pp.statesBounciness.get(curStateId);
            speed = speed * pp.statesInertia.get(curStateId);
        } else {
            if (side) {
                direction = 3.1415f - direction;
            } else {
                if (platformMode) {
                    curStateBounces++;
                    impulse = -impulse * pp.statesBounciness.get(curStateId);
                    speed = speed * pp.statesInertia.get(curStateId);
                } else {
                    direction = 2 * 3.1415f - direction;
                }
            }
            //speed = speed * pp.statesBounciness.get(curStateId);
        }
        if (pp.statesBounceSounds.get(curStateId) != null) {
            if (floor && Math.abs(impulse) > 1.0f) {
                pp.statesBounceSounds.get(curStateId).play((Math.abs(impulse)/5.0f) * pp.menu.soundVolume/100.0f);
            } else {
                pp.statesBounceSounds.get(curStateId).play((speed/10.0f) * pp.menu.soundVolume/100.0f);
            }
        }
        checkBounceState();
    }

    public ArrayList<ParticleProperties.ParticleSpawnProperties> checkParticleEmission() {
        if (lastSpawned == 0) lastSpawned = System.currentTimeMillis();
        if (curStateRemainingSpawns != 0 && System.currentTimeMillis() - lastSpawned > pp.statesSpawnIntervals.get(curStateId)) {
            lastSpawned = System.currentTimeMillis();
            curStateRemainingSpawns--;
            return pp.statesparticleSpawns.get(curStateId);
        }
        return null;
    }

    public void checkBounceState() {
        for (int i = 0; i < pp.statesChangeConditions.get(curStateId).size(); ++i) {
            int param = pp.statesChangeConditions.get(curStateId).get(i).conditionParam;
            if (pp.statesChangeConditions.get(curStateId).get(i).conditionType == ParticleProperties.ChangeConditionType.BOUNCE) {
                if (curStateBounces >= param) {
                    changeState(pp.statesChangeConditions.get(curStateId).get(i).nextStateId);
                }
            } else if (pp.statesChangeConditions.get(curStateId).get(i).conditionType == ParticleProperties.ChangeConditionType.RANDOM_BOUNCE) {
                float r = (float)Math.random() * 100.0f;
                int n = 0;
                for (float j = 0; j < param * pp.statesChangeConditions.get(curStateId).get(i).nextStateId; j += param) {
                    if (r < j) return;
                    n++;
                    if (j >= 100.0f && r >= j) {
                        n = 0;
                    }
                }
                changeState(curStateId + n);
            }
        }
    }

    public void checkTimerState() {
        for (int i = 0; i < pp.statesChangeConditions.get(curStateId).size(); ++i) {
            int param = pp.statesChangeConditions.get(curStateId).get(i).conditionParam;
            if (pp.statesChangeConditions.get(curStateId).get(i).conditionType == ParticleProperties.ChangeConditionType.TIMER) {
                if (System.currentTimeMillis() - curStateStart >= param) {
                    changeState(pp.statesChangeConditions.get(curStateId).get(i).nextStateId);
                }
            } else if (anim != null && pp.statesChangeConditions.get(curStateId).get(i).conditionType == ParticleProperties.ChangeConditionType.RANDOM) {
                float r = (float)Math.random() * 1000.0f;
                if (r < param) {
                    changeState(pp.statesChangeConditions.get(curStateId).get(i).nextStateId);
                }
            } else if (anim != null && pp.statesChangeConditions.get(curStateId).get(i).conditionType == ParticleProperties.ChangeConditionType.LOOPS) {
                if (anim.loops >= param) {
                    changeState(pp.statesChangeConditions.get(curStateId).get(i).nextStateId);
                }
            }
        }
    }

    public void stopSounds() {
        if (pp.statesFlySoundLoops.get(curStateId) != null) {
            pp.statesFlySoundLoops.get(curStateId).stop(curLoop);
            curLoop = -1;
        }
    }

    public void changeState(int id) {
        if (pp.statesFlySoundLoops.get(curStateId) != null && pp.statesFlySoundLoops.get(curStateId) != pp.statesFlySoundLoops.get(id)) {
            pp.statesFlySoundLoops.get(curStateId).stop(curLoop);
            curLoop = -1;
        }
        curStateBounces = 0;
        curStateStart = System.currentTimeMillis();
        curStateId = id;
        if (pp.statesFramesNumbers.get(curStateId) > 1) {
            anim = new AnimationSequence(pp.statesAnims.get(curStateId));
            tex = null;
        } else {
            tex = pp.statesTexes.get(curStateId);
            anim = null;
        }
        if (pp.statesFlySoundLoops.get(curStateId) != null) {
            if (curLoop == -1) {
                curLoop = pp.statesFlySoundLoops.get(curStateId).loop(pp.menu.soundVolume/100.0f);
            }
        } else if (curLoop != -1) {
            pp.statesFlySoundLoops.get(curStateId).stop(curLoop);
            curLoop = -1;
        }
        curStateRemainingSpawns = pp.statesSpawnsCounts.get(curStateId);
        floor = pp.statesFloors.get(curStateId);
        if (floor && pp.statesMovePatterns.get(curStateId) == ParticleProperties.MovePattern.NONE) {
            z = 0;
        }
    }

    public void bounce(Particle p) {
        return;
        /*if (pp.statesCollisionGroups.get(curStateId).equals(p.pp.statesCollisionGroups.get(p.curStateId))) {
            //curStateBounces++;
            float collideDir = (float)Math.atan2(x - p.x, y - p.y);
            float newThisDir = 3.1415f/2.0f + collideDir + (3.1415f * 3.0f/2.0f + collideDir - direction);
            float newThisSpeed = speed * pp.statesBounciness.get(curStateId);
            float newThatDir = 3.1415f/2.0f + collideDir + (3.1415f * 3.0f/2.0f + collideDir - p.direction) + 3.1415f;
            float newThatSpeed = p.speed * p.pp.statesBounciness.get(p.curStateId);
            direction = newThisDir;
            speed = newThisSpeed;
            p.direction = newThatDir;
            p.speed = newThatSpeed;
            if (pp.statesBounceSounds.get(curStateId) != null) {
                pp.statesBounceSounds.get(curStateId).play((speed/10.0f) * pp.menu.soundVolume/100.0f);
            }
            checkBounceState();
        }*/
    }

    public void windBlow(float windDirection, float windStrength) {
        if (z < 0.2f) return;
        float realDir = windDirection;
        if (Math.abs(windDirection-direction) > Math.abs((windDirection + 6.28)-direction)) realDir = windDirection + 6.28f;
        else realDir = windDirection;
        if (Math.abs(realDir-direction) > Math.abs((windDirection - 6.28)-direction)) realDir = windDirection - 6.28f;
        float vectorX = (float)Math.cos(direction);
        float vectorY = (float)Math.sin(direction);
        float targX = (float)Math.cos(realDir);
        float targY = (float)Math.sin(realDir);
        float step = pp.statesWeights.get(curStateId) * windStrength;
        if (vectorX < targX - step) vectorX += step;
        else if (vectorX > targX + step) vectorX -= step;
        else vectorX = targX;
        if (vectorY < targY - step) vectorY += step;
        else if (vectorY > targY + step) vectorY -= step;
        else vectorY = targY;

        direction = (float)Math.atan2(vectorY, vectorX);
        speed = (float)Math.sqrt(vectorX * vectorX + vectorY * vectorY);
    }

    @Override
    public void fall() {
        while (direction > 2 * 3.1415f) direction -= 2 * 3.1415f;
        while (direction < 0) direction += 2 * 3.1415f;
        if (platformMode) {
            if (pp.statesMovePatterns.get(curStateId) == ParticleProperties.MovePattern.NORMAL) {
                XSpeed = (float)Math.cos(direction) * speed;
                YSpeed = -impulse;
                x += XSpeed;
                y += YSpeed;
                impulse -= pp.statesWeights.get(curStateId);
                //speed -= (Math.abs(Math.cos(direction)) + Math.abs(Math.sin(direction)))* pp.statesInertia.get(curStateId);
                if (speed < 0) speed = 0;
            } else if (pp.statesMovePatterns.get(curStateId) == ParticleProperties.MovePattern.RANDOM) {
                x += Math.random() * speed;
                y += Math.random() * speed;
            }
        } else {
            if (pp.statesMovePatterns.get(curStateId) == ParticleProperties.MovePattern.NORMAL) {
                XSpeed = (float)Math.cos(direction) * speed;
                YSpeed = (float)Math.sin(direction) * speed * 0.7f;
                x += XSpeed;
                y += YSpeed;
                z += impulse;
                if (z <= 0) {
                    z = 0;
                    bounce(true, false);
                }
                impulse -= pp.statesWeights.get(curStateId);
                //speed -= (Math.abs(Math.cos(direction)) + Math.abs(Math.sin(direction)))* pp.statesInertia.get(curStateId);
                if (speed < 0) speed = 0;
            } else if (pp.statesMovePatterns.get(curStateId) == ParticleProperties.MovePattern.RANDOM) {
                //x += Math.random() * speed - speed/2.0f;
                //y += Math.random() * speed - speed/2.0f;
                direction += Math.random() * speed - speed/2.0f;
                XSpeed = (float)Math.cos(direction) * speed;
                YSpeed = (float)Math.sin(direction) * speed * 0.7f;
                x += XSpeed;
                y += YSpeed;
                z += Math.random() * speed - speed/2.0f;
            }
        }
        if (!falling || floor) {
            h = y;
        }
        alpha -= pp.statesAlphaSteps.get(curStateId);
        if (pp.statesFlySoundLoops.get(curStateId) != null) {
            if (curLoop != -1 && pp.menu.paused) {
                pp.statesFlySoundLoops.get(curStateId).stop(curLoop);
                curLoop = -1;
            } else if (curLoop == -1) {
                curLoop = pp.statesFlySoundLoops.get(curStateId).loop(pp.menu.soundVolume/100.0f);
            }
        }
        checkTimerState();
    }

    @Override
    public float getPreviousY() {
        return y - impulse;
    }

}
