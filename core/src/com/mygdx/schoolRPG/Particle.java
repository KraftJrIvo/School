package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.schoolRPG.Entity;
import com.mygdx.schoolRPG.menus.MainMenu;
import com.mygdx.schoolRPG.tools.AnimationSequence;

import javax.xml.soap.Text;
import java.util.ArrayList;

/**
 * Created by Kraft on 16.07.2015.
 */
public class Particle extends Entity {

    public ParticleProperties pp;
    float direction;
    float speed;
    float impulse;
    int curStateBounces;
    boolean platformMode;
    public int curStateId;
    long curLoop = -1;
    long curStateStart = 0;
    float XSpeed = 0;
    float YSpeed = 0;
    int curStateRemainingSpawns;
    long lastSpawned;
    boolean important = false;
    public String text = "";
    BitmapFont font;
    Color fontColor;
    float textW;
    Texture simpleTexture;

    public Particle(AssetManager assets, ParticleProperties pp, ParticleProperties.ParticleSpawnProperties spawnProperties, boolean platformMode, float spawnX, float spawnY, float spawnZ) {
        super(assets, (Texture)null, spawnX + spawnProperties.spawnX, spawnY + spawnProperties.spawnY, pp.h, pp.floorHeight, 0);
        z = spawnZ + spawnProperties.spawnZ;
        this.pp = pp;
        curStateId = 0;
        if (this.pp.statesTexes.get(curStateId) != null) tex = this.pp.statesTexes.get(curStateId);
        else if (this.pp.statesAnims.get(curStateId) != null) anim = this.pp.statesAnims.get(curStateId);
        direction = spawnProperties.spawnDir + (float)Math.random() * spawnProperties.dirSpread - spawnProperties.dirSpread/2.0f;
        speed = spawnProperties.spawnSpeed + (float)Math.random() * spawnProperties.speedSpread;
        impulse = spawnProperties.spawnImpulse + ((float)Math.random() * 2.0f - 1.0f) * spawnProperties.impulseSpread;
        curStateBounces = 0;
        centered = true;
        r = pp.r;
        this.platformMode = platformMode;
        if (platformMode) z = 0;
        this.h = y;
        changeState(0);
        if (spawnProperties.spawnSound != null) spawnProperties.spawnSound.play(pp.menu.soundVolume/100.0f);
    }

    public Particle(AssetManager assets, String text, Color fontColor, boolean platformMode, float spawnX, float spawnY, float spawnZ) {
        super(assets, (Texture)null, spawnX, spawnY, 0, 0, 0);
        ParticleProperties pp = new ParticleProperties();
        h=0;
        floorHeight=0;
        pp.r = 2.0f;
        pp.statesTexes = new ArrayList<Texture>();
        pp.statesTexes.add(null);
        pp.statesTexes.add(null);
        pp.statesAnims = new ArrayList<AnimationSequence>();
        pp.statesAnims.add(null);
        pp.statesAnims.add(null);
        pp.statesIds = new ArrayList<Integer>();
        pp.statesIds.add(0);
        pp.statesIds.add(0);
        pp.statesTexNames = new ArrayList<String>();
        pp.statesTexNames.add(null);
        pp.statesTexNames.add(null);
        pp.statesFramesNumbers = new ArrayList<Integer>();
        pp.statesFramesNumbers.add(1);
        pp.statesFramesNumbers.add(1);
        pp.statesWeights = new ArrayList<Float>();
        pp.statesWeights.add(0.1f);
        pp.statesWeights.add(0.1f);
        pp.statesBounciness = new ArrayList<Float>();
        pp.statesBounciness.add(0.9f);
        pp.statesBounciness.add(0.9f);
        pp.statesInertia = new ArrayList<Float>();
        pp.statesInertia.add(0.69f);
        pp.statesInertia.add(0.69f);
        pp.statesAlphaSteps = new ArrayList<Float>();
        pp.statesAlphaSteps.add(0f);
        pp.statesAlphaSteps.add(0.01f);
        pp.statesMovePatterns = new ArrayList<ParticleProperties.MovePattern>();
        pp.statesMovePatterns.add(ParticleProperties.MovePattern.NORMAL);
        pp.statesMovePatterns.add(ParticleProperties.MovePattern.NONE);
        pp.statesFloors = new ArrayList<Boolean>();
        pp.statesFloors.add(false);
        pp.statesFloors.add(false);
        pp.statesCollisionGroups = new ArrayList<Integer>();
        pp.statesCollisionGroups.add(0);
        pp.statesCollisionGroups.add(0);
        pp.statesSpawnIntervals = new ArrayList<Integer>();
        pp.statesSpawnIntervals.add(0);
        pp.statesSpawnsCounts = new ArrayList<Integer>();
        pp.statesSpawnsCounts.add(0);
        pp.statesSpawnsCounts.add(0);
        pp.statesFlySoundLoops = new ArrayList<Sound>();
        pp.statesFlySoundLoops.add(null);
        pp.statesFlySoundLoops.add(null);
        pp.statesBounceSounds = new ArrayList<Sound>();
        pp.statesBounceSounds.add(null);
        pp.statesBounceSounds.add(null);
        pp.statesFlySoundLoopsNames = new ArrayList<String>();
        pp.statesFlySoundLoopsNames.add(null);
        pp.statesFlySoundLoopsNames.add(null);
        pp.statesBounceSoundsNames = new ArrayList<String>();
        pp.statesBounceSoundsNames.add(null);
        pp.statesBounceSoundsNames.add(null);
        pp.statesChangeConditions = new ArrayList<ArrayList<ParticleProperties.StateChangeCondition>>();
        pp.statesChangeConditions.add(new ArrayList<ParticleProperties.StateChangeCondition>());
        pp.statesChangeConditions.add(new ArrayList<ParticleProperties.StateChangeCondition>());
        pp.statesChangeConditions.get(0).add(new ParticleProperties().new StateChangeCondition("BOUNCE-3-1"));
        pp.statesparticleSpawns = new ArrayList<ArrayList<ParticleProperties.ParticleSpawnProperties>>();
        pp.statesparticleSpawns.add(new ArrayList<ParticleProperties.ParticleSpawnProperties>());
        pp.statesparticleSpawns.add(new ArrayList<ParticleProperties.ParticleSpawnProperties>());
        pp.statesparticleSpawns.get(0).add(null);
        ParticleProperties.ParticleSpawnProperties spawnProperties = new ParticleProperties().new ParticleSpawnProperties("", 0,0,0);
        spawnProperties.spawnDir = 3.14159f * 1.5f;
        spawnProperties.dirSpread = 3.14159f / 2.0f;
        z = spawnZ + spawnProperties.spawnZ;
        this.pp = pp;
        curStateId = 0;
        if (this.pp.statesTexes.get(curStateId) != null) tex = this.pp.statesTexes.get(curStateId);
        else if (this.pp.statesAnims.get(curStateId) != null) anim = this.pp.statesAnims.get(curStateId);
        direction = spawnProperties.spawnDir + (float)Math.random() * spawnProperties.dirSpread - spawnProperties.dirSpread/2.0f;
        speed = spawnProperties.spawnSpeed + (float)Math.random() * spawnProperties.speedSpread;
        impulse = spawnProperties.spawnImpulse + ((float)Math.random() * 2.0f - 1.0f) * spawnProperties.impulseSpread;
        curStateBounces = 0;
        centered = true;
        r = pp.r;
        this.platformMode = platformMode;
        if (platformMode) z = 0;
        this.h = y;
        changeState(0);
        this.text = text;
        font = new BitmapFont(Gdx.files.internal("palatino24.fnt"), Gdx.files.internal("palatino24.png"), false);
        this.fontColor = fontColor;
        textW = font.getBounds(text).width + 2;
    }

    public Particle(AssetManager assets, Texture tex, boolean platformMode, float spawnX, float spawnY, float spawnZ) {
        super(assets, (Texture)null, spawnX, spawnY, 0, 0, 0);
        ParticleProperties pp = new ParticleProperties();
        h=0;
        floorHeight=0;
        pp.r = 2.0f;
        pp.statesTexes = new ArrayList<Texture>();
        pp.statesTexes.add(null);
        pp.statesTexes.add(null);
        pp.statesAnims = new ArrayList<AnimationSequence>();
        pp.statesAnims.add(null);
        pp.statesAnims.add(null);
        pp.statesIds = new ArrayList<Integer>();
        pp.statesIds.add(0);
        pp.statesIds.add(0);
        pp.statesTexNames = new ArrayList<String>();
        pp.statesTexNames.add(null);
        pp.statesTexNames.add(null);
        pp.statesFramesNumbers = new ArrayList<Integer>();
        pp.statesFramesNumbers.add(1);
        pp.statesFramesNumbers.add(1);
        pp.statesWeights = new ArrayList<Float>();
        pp.statesWeights.add(0.1f);
        pp.statesWeights.add(0.1f);
        pp.statesBounciness = new ArrayList<Float>();
        pp.statesBounciness.add(0.9f);
        pp.statesBounciness.add(0.9f);
        pp.statesInertia = new ArrayList<Float>();
        pp.statesInertia.add(0.69f);
        pp.statesInertia.add(0.69f);
        pp.statesAlphaSteps = new ArrayList<Float>();
        pp.statesAlphaSteps.add(0f);
        pp.statesAlphaSteps.add(0f);
        pp.statesMovePatterns = new ArrayList<ParticleProperties.MovePattern>();
        pp.statesMovePatterns.add(ParticleProperties.MovePattern.NORMAL);
        pp.statesMovePatterns.add(ParticleProperties.MovePattern.NONE);
        pp.statesFloors = new ArrayList<Boolean>();
        pp.statesFloors.add(false);
        pp.statesFloors.add(false);
        pp.statesCollisionGroups = new ArrayList<Integer>();
        pp.statesCollisionGroups.add(0);
        pp.statesCollisionGroups.add(0);
        pp.statesSpawnIntervals = new ArrayList<Integer>();
        pp.statesSpawnIntervals.add(0);
        pp.statesSpawnsCounts = new ArrayList<Integer>();
        pp.statesSpawnsCounts.add(0);
        pp.statesSpawnsCounts.add(0);
        pp.statesFlySoundLoops = new ArrayList<Sound>();
        pp.statesFlySoundLoops.add(null);
        pp.statesFlySoundLoops.add(null);
        pp.statesBounceSounds = new ArrayList<Sound>();
        pp.statesBounceSounds.add(null);
        pp.statesBounceSounds.add(null);
        pp.statesFlySoundLoopsNames = new ArrayList<String>();
        pp.statesFlySoundLoopsNames.add(null);
        pp.statesFlySoundLoopsNames.add(null);
        pp.statesBounceSoundsNames = new ArrayList<String>();
        pp.statesBounceSoundsNames.add(null);
        pp.statesBounceSoundsNames.add(null);
        pp.statesChangeConditions = new ArrayList<ArrayList<ParticleProperties.StateChangeCondition>>();
        pp.statesChangeConditions.add(new ArrayList<ParticleProperties.StateChangeCondition>());
        pp.statesChangeConditions.add(new ArrayList<ParticleProperties.StateChangeCondition>());
        pp.statesChangeConditions.get(0).add(new ParticleProperties().new StateChangeCondition("BOUNCE-1-1"));
        pp.statesparticleSpawns = new ArrayList<ArrayList<ParticleProperties.ParticleSpawnProperties>>();
        pp.statesparticleSpawns.add(new ArrayList<ParticleProperties.ParticleSpawnProperties>());
        pp.statesparticleSpawns.add(new ArrayList<ParticleProperties.ParticleSpawnProperties>());
        pp.statesparticleSpawns.get(0).add(null);
        ParticleProperties.ParticleSpawnProperties spawnProperties = new ParticleProperties().new ParticleSpawnProperties("", 0,0,0);
        spawnProperties.spawnDir = 3.14159f * 1.5f;
        spawnProperties.dirSpread = 3.14159f / 2.0f;
        z = spawnZ + spawnProperties.spawnZ;
        this.pp = pp;
        curStateId = 0;
        if (this.pp.statesTexes.get(curStateId) != null) tex = this.pp.statesTexes.get(curStateId);
        else if (this.pp.statesAnims.get(curStateId) != null) anim = this.pp.statesAnims.get(curStateId);
        direction = spawnProperties.spawnDir + (float)Math.random() * spawnProperties.dirSpread - spawnProperties.dirSpread/2.0f;
        speed = spawnProperties.spawnSpeed + (float)Math.random() * spawnProperties.speedSpread;
        impulse = spawnProperties.spawnImpulse + ((float)Math.random() * 2.0f - 1.0f) * spawnProperties.impulseSpread;
        curStateBounces = 0;
        centered = true;
        r = pp.r;
        this.platformMode = platformMode;
        if (platformMode) z = 0;
        this.h = y;
        changeState(0);
        this.text = text;
        font = new BitmapFont(Gdx.files.internal("palatino24.fnt"), Gdx.files.internal("palatino24.png"), false);
        textW = font.getBounds(text).width + 2;
        this.simpleTexture = tex;
    }

    public void setAlphaStep(int stateId, float alphaStep) {
        pp.statesAlphaSteps.set(stateId, alphaStep);
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
            if (floor && Math.abs(impulse) > 0) {
                pp.statesBounceSounds.get(curStateId).play((Math.abs(impulse)/2.0f) * pp.menu.soundVolume/100.0f);
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
        float deltaTime = Gdx.graphics.getDeltaTime() * 60;
        while (direction > 2 * 3.1415f) direction -= 2 * 3.1415f;
        while (direction < 0) direction += 2 * 3.1415f;
        if (platformMode) {
            if (pp.statesMovePatterns.get(curStateId) == ParticleProperties.MovePattern.NORMAL) {
                XSpeed = (float)Math.cos(direction) * speed * deltaTime;
                YSpeed = -impulse * deltaTime;
                x += XSpeed * deltaTime;
                y += YSpeed * deltaTime;
                impulse -= pp.statesWeights.get(curStateId) * deltaTime;
                //speed -= (Math.abs(Math.cos(direction)) + Math.abs(Math.sin(direction)))* pp.statesInertia.get(curStateId);
                if (speed < 0) speed = 0;
            } else if (pp.statesMovePatterns.get(curStateId) == ParticleProperties.MovePattern.RANDOM) {
                x += Math.random() * speed * deltaTime;
                y += Math.random() * speed * deltaTime;
            }
        } else {
            if (pp.statesMovePatterns.get(curStateId) == ParticleProperties.MovePattern.NORMAL) {
                XSpeed = (float)Math.cos(direction) * speed * deltaTime;
                YSpeed = (float)Math.sin(direction) * speed * 0.7f * deltaTime;
                x += XSpeed * deltaTime;
                y += YSpeed * deltaTime;
                z += impulse * deltaTime;
                if (z <= 0) {
                    z = 0;
                    bounce(true, false);
                }
                impulse -= pp.statesWeights.get(curStateId) * deltaTime;
                //speed -= (Math.abs(Math.cos(direction)) + Math.abs(Math.sin(direction)))* pp.statesInertia.get(curStateId);
                if (speed < 0) speed = 0;
            } else if (pp.statesMovePatterns.get(curStateId) == ParticleProperties.MovePattern.RANDOM) {
                //x += Math.random() * speed - speed/2.0f;
                //y += Math.random() * speed - speed/2.0f;
                direction += Math.random() * speed - speed/2.0f * deltaTime;
                XSpeed = (float)Math.cos(direction) * speed * deltaTime;
                YSpeed = (float)Math.sin(direction) * speed * 0.7f * deltaTime;
                x += XSpeed * deltaTime;
                y += YSpeed * deltaTime;
                z += Math.random() * speed - speed/2.0f * deltaTime;
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

    public void drawBattle(SpriteBatch batch) {
        float yy = y + z;
        if (tex == null && simpleTexture != null) {
            tex = simpleTexture;
        }
        if (text.length() == 0) {
            if (anim != null) {
                yy += anim.getFirstFrame().getRegionHeight();
                if (floor) yy -= anim.getFirstFrame().getRegionHeight();
            } else {
                yy += tex.getHeight();
                if (floor) yy -= tex.getHeight();
            }

            batch.setColor(new Color(1.0f, 1.0f, 1.0f, alpha));
            if (anim != null) {
                batch.draw(anim.getCurrentFrame(false), x - anim.getFirstFrame().getRegionWidth(), yy, anim.getFirstFrame().getRegionWidth() * 2.0f, anim.getFirstFrame().getRegionHeight() * 2.0f);
            } else {
                batch.draw(tex, x - tex.getWidth(), yy, tex.getWidth() * 2.0f, tex.getHeight() * 2.0f);
            }
        } else {
            font.setColor(new Color(0, 0, 0, alpha));
            font.draw(batch, text, x - textW / 2.0f + 2, yy + font.getLineHeight());
            fontColor.a = alpha;
            font.setColor(fontColor);
            font.draw(batch, text, x - textW / 2.0f, yy + 2 + font.getLineHeight());
        }
        batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
    }

    @Override
    public float getPreviousY() {
        return y - impulse;
    }

}
