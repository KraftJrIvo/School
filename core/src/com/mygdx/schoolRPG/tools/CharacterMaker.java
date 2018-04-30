package com.mygdx.schoolRPG.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.schoolRPG.MovingConfiguration;
import com.mygdx.schoolRPG.menus.GameMenu;

import java.security.Key;
import java.util.ArrayList;

/**
 * Created by Kraft on 10.03.2010.
 */
public class CharacterMaker {
    enum BodyPartRotation{
        front(0), back(1), side(2);

        private int value;
        private BodyPartRotation(int value){
            this.value = value;
        }
    }
    AnimationSequence legs_walk_front, legs_walk_side, legs_run_side, timer1, timer2;
    public ArrayList<GlobalSequence> bodies, heads, bodyWears, headWears;
    public ArrayList<Texture> sprites;
    Texture legs_stand_front, legs_stand_side, arms_front, arms_side, arms_push_side, arms_push_front, arms_push_back, arms_push_side_back;
    TextureRegion arms_front_reversed, arms_side_reversed, legs_stand_side_reversed, arms_push_side_reversed, arms_push_front_reversed, arms_push_back_reversed, arms_push_side_back_reversed;
    //public CharacterDirectionChecker cdc;
    public ArrayList<CharacterDirectionChecker> cdcs;
    public ArrayList<Boolean> pushes;
    public ArrayList<Sound> sounds;
    //public boolean push = true;
    public boolean go = false;
    int legsHeight = 10;
    int bodyHeight = 16;
    int armsLevel = 6;
    int armsOffset = 4;
    int armsOffsetSide = 5;
    int legsWidth, bodyWidth, armFrontWidth, armSideWidth, headWidth;
    //float bobbing = 0;
    ArrayList<Float> bobbings;
    ArrayList<Float> stepCounters;
    int charsCount = 0;
    GameMenu menu;

    public CharacterMaker(AssetManager assets, String worldPath, GameMenu menu) {
        FileHandle curDir = Gdx.files.internal(worldPath + "/chars");
        for (FileHandle entry: curDir.list()) {
            //if (entry.file().isDirectory()) {
                FileHandle spriteFile = Gdx.files.internal(entry.path() + "/sprite.png");
                if (spriteFile.exists()) {
                    assets.load(entry.path() + "/sprite.png", Texture.class);
                } else {
                    assets.load(entry.path() + "/head.png", Texture.class);
                    FileHandle bodyFile = Gdx.files.internal(entry.path() + "/body.png");
                    if (bodyFile.exists()) {
                        assets.load(entry.path() + "/body.png", Texture.class);
                    }
                }
                assets.load(entry.path() + "/speech.wav", Sound.class);
                charsCount++;
                FileHandle curDir1 = Gdx.files.internal(entry.path() + "/graphics");
                if (curDir1.exists()) {
                    for (FileHandle entry1: curDir1.list()) {
                        assets.load(entry1.path(), Texture.class);
                    }
                }
            //}
        }
        this.menu = menu;
        assets.load("char/body_male.png", Texture.class);
        assets.load("char/body_female.png", Texture.class);
        assets.load("char/stand_front.png", Texture.class);
        assets.load("char/stand_side.png", Texture.class);
        assets.load("char/walk_front.png", Texture.class);
        assets.load("char/walk_side.png", Texture.class);
        assets.load("char/run_side.png", Texture.class);
        assets.load("char/stay_front.png", Texture.class);
        assets.load("char/stay_side.png", Texture.class);
        assets.load("char/push_side.png", Texture.class);
        assets.load("char/push_front.png", Texture.class);
        assets.load("char/push_back.png", Texture.class);
        assets.load("char/push_side_back.png", Texture.class);
        cdcs = new ArrayList<CharacterDirectionChecker>();
        cdcs.add(new CharacterDirectionChecker(true));
        bobbings = new ArrayList<Float>();
        bobbings.add(new Float(0));
        stepCounters = new ArrayList<Float>();
        stepCounters.add(new Float(0));
        pushes = new ArrayList<Boolean>();
        pushes.add(new Boolean(false));
    }

    public void initialiseResources(AssetManager assets, String worldPath) {
        bodies = new ArrayList<GlobalSequence>();
        bodyWears = new ArrayList<GlobalSequence>();
        heads = new ArrayList<GlobalSequence>();
        headWears = new ArrayList<GlobalSequence>();
        sprites = new ArrayList<Texture>();
        sounds = new ArrayList<Sound>();
        for (int i =0; i < charsCount; ++i) {
            if (assets.isLoaded(worldPath + "/chars/" + i + "/sprite.png")) {
                sprites.add(assets.get(worldPath + "/chars/" + i + "/sprite.png", Texture.class));
                bodies.add(null);
                heads.add(null);
            } else {
                heads.add(new GlobalSequence(assets, worldPath + "/chars/" + i + "/head.png", 3));
                sprites.add(null);
                if (assets.isLoaded(worldPath + "/chars/" + i + "/body.png")) {
                    bodies.add(new GlobalSequence(assets, worldPath + "/chars/" + i + "/body.png", 3));
                } else {
                    bodies.add(new GlobalSequence(assets,"char/body_male.png", 3));
                }
            }
            headWears.add(null);
            bodyWears.add(null);
            sounds.add(null);
        }
        for (int i =0; i < heads.size()-1; ++i) {
            cdcs.add(new CharacterDirectionChecker(false));
            bobbings.add(new Float(0));
            stepCounters.add(new Float(0));
            pushes.add(new Boolean(false));
        }
        legs_stand_front = assets.get("char/stand_front.png");
        legs_stand_side = assets.get("char/stand_side.png");
        arms_front = assets.get("char/stay_front.png");
        arms_side = assets.get("char/stay_side.png");
        arms_push_side = assets.get("char/push_side.png");
        arms_push_front = assets.get("char/push_front.png");
        arms_push_back = assets.get("char/push_back.png");
        arms_push_side_back = assets.get("char/push_side_back.png");
        arms_push_side_reversed = new TextureRegion(arms_push_side);
        arms_push_side_reversed.flip(true, false);
        arms_push_front_reversed = new TextureRegion(arms_push_front);
        arms_push_front_reversed.flip(true, false);
        arms_push_back_reversed = new TextureRegion(arms_push_back);
        arms_push_back_reversed.flip(true, false);
        arms_push_side_back_reversed = new TextureRegion(arms_push_side_back);
        arms_push_side_back_reversed.flip(true, false);
        legs_walk_front = new AnimationSequence(assets, "char/walk_front.png", 1, true, 8);
        legs_walk_side = new AnimationSequence(assets, "char/walk_side.png", 20, true, 8);
        legs_run_side = new AnimationSequence(assets, "char/run_side.png", 20, true, 7);
        legs_stand_side_reversed = new TextureRegion(legs_stand_side);
        legs_stand_side_reversed.flip(true, false);
        arms_side_reversed = new TextureRegion(arms_side);
        arms_side_reversed.flip(true, false);
        arms_front_reversed = new TextureRegion(arms_front);
        arms_front_reversed.flip(true, false);
        timer1 = new AnimationSequence(assets, "char/walk_side.png", 20, true, 8);
        timer2 = new AnimationSequence(assets, "char/walk_front.png", 10, true, 8);
    }

    public boolean directionsCheck(int id, MovingConfiguration mc) {

        return cdcs.get(id).update(mc);
    }

    public void setWears(int charId, GlobalSequence headWear, GlobalSequence bodyWear) {
        headWears.set(charId, headWear);
        bodyWears.set(charId, bodyWear);
    }

    public void draw(SpriteBatch batch, int id, float x, float y, float speedX, float speedY, GlobalSequence headWear, GlobalSequence bodyWear, GlobalSequence objectInHands, MovingConfiguration mc) {
        headWears.set(id, headWear);
        bodyWears.set(id, bodyWear);
        if (menu.paused) {
            speedX = 0;
            speedY = 0;
        }
        stepCounters.set(id, stepCounters.get(id) + Math.max(speedX, speedY));
        Sound sound = sounds.get(id);
        if (stepCounters.get(id) >= 25 && sound != null) {
            sound.play(menu.soundVolume/100.0f);
            stepCounters.set(id, new Float(0));
        }

        if (sprites.get(id) == null) {
            legsWidth = legs_stand_front.getWidth();
            bodyWidth = bodies.get(id).getWidth()/3;
            armFrontWidth = arms_front.getWidth();
            armSideWidth = arms_side.getWidth();
            headWidth = heads.get(id).getWidth()/3;
            float bobbing = bobbings.get(id);
            if (cdcs.get(id).lookDir == CharacterDirectionChecker.LookDirection.up) {
                drawArm(batch, x, y + legsHeight + armsLevel + bobbing, id, objectInHands, mc);
                drawLegs(batch, x-legsWidth/2, y, speedX, speedY, id, mc);
                drawHead(batch, id, x-headWidth/2, y+legsHeight+bodyHeight+bobbing, heads.get(id));
                if (headWear != null) {
                    drawHead(batch, id, x-headWidth/2, y+legsHeight+bodyHeight+bobbing, headWear);
                }
                drawBody(batch, id, x-bodyWidth/2, y+legsHeight+bobbing, bodies.get(id));
                if (bodyWear != null) {
                    drawBody(batch, id, x-bodyWidth/2, y+legsHeight+bobbing, bodyWear);
                }
            } else {
                if (cdcs.get(id).lookDir != CharacterDirectionChecker.LookDirection.down) {
                    drawArm(batch, x, y+legsHeight+armsLevel+bobbing+3, id, null, mc);
                }
                drawLegs(batch, x-legsWidth/2, y, speedX, speedY, id, mc);
                drawBody(batch, id, x-bodyWidth/2, y+legsHeight+bobbing, bodies.get(id));
                if (bodyWear != null) {
                    drawBody(batch, id, x-bodyWidth/2, y+legsHeight+bobbing, bodyWear);
                }
                drawHead(batch, id, x-headWidth / 2, y + legsHeight + bodyHeight + bobbing, heads.get(id));
                if (headWear != null) {
                    drawHead(batch, id, x-headWidth / 2, y + legsHeight + bodyHeight + bobbing, headWear);
                }
                drawArm(batch, x, y+legsHeight+armsLevel+bobbing, id, objectInHands, mc);
            }
        } else {
            Texture sprite = sprites.get(id);
            batch.draw(sprite, x - sprite.getWidth()/2, y);
        }
    }

    private void drawLegs(SpriteBatch batch, float x, float y, float speedX, float speedY, int id, MovingConfiguration mc) {
        //System.out.println(speedX);
        CharacterDirectionChecker cdc = cdcs.get(id);
        if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.left) {
            if (cdc.stand) {
                batch.draw(legs_stand_side_reversed, x, y);
            } else if (mc.movingUp > 0 && Math.abs(speedX)<=1 && speedY > speedX) {
                legs_walk_front.drawReversed(batch, x, y, (int) (10 * speedY), false);
            } else if (mc.movingDown > 0 && Math.abs(speedX)<=1 && speedY > speedX) {
                legs_walk_front.draw(batch, x, y, (int)(10*speedY), false);
            } else {
                if (Math.abs(speedX) > 1.6f) {
                    legs_run_side.draw(batch, x, y, (int)(10*speedX), true);
                } else {
                    legs_walk_side.draw(batch, x, y, (int)(20*speedX), true);
                }
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.left_back) {
            if (cdc.stand) {
                batch.draw(legs_stand_side_reversed, x, y);
            } else if (mc.movingUp > 0 && Math.abs(speedX)<=1 && speedY > speedX) {
                legs_walk_front.drawReversed(batch, x, y, (int) (10 * speedY), false);
            } else if (mc.movingDown > 0 && Math.abs(speedX)<=1 && speedY > speedX) {
                legs_walk_front.draw(batch, x, y, (int)(10*speedY), false);
            } else {
                if (Math.abs(speedX) > 1.6f) {
                    legs_run_side.drawReversed(batch, x, y, (int)(10*speedX), false);
                } else {
                    legs_walk_side.drawReversed(batch, x, y, (int)(20*speedX), false);
                }

            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.right) {
            if (cdc.stand) {
                batch.draw(legs_stand_side, x, y);
            } else if (mc.movingUp > 0 && Math.abs(speedX)<=1 && speedY > speedX) {
                legs_walk_front.drawReversed(batch, x, y, (int)(10*speedY), false);
            } else if (mc.movingDown > 0 && Math.abs(speedX)<=1 && speedY > speedX) {
                legs_walk_front.draw(batch, x, y, (int)(10*speedY), false);
            }  else {
                if (Math.abs(speedX) > 1.6f) {
                    legs_run_side.draw(batch, x, y, (int)(10*speedX), false);
                } else {
                    legs_walk_side.draw(batch, x, y, (int)(20*speedX), false);
                }
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.right_back) {
            if (cdc.stand) {
                batch.draw(legs_stand_side, x, y);
            } else if (mc.movingUp > 0 && Math.abs(speedX)<=1 && speedY > speedX) {
                legs_walk_front.drawReversed(batch, x, y, (int) (10 * speedY), false);
            } else if (mc.movingDown > 0 && Math.abs(speedX)<=1 && speedY > speedX) {
                legs_walk_front.draw(batch, x, y, (int)(10*speedY), false);
            } else {
                if (Math.abs(speedX) > 1.6f) {
                    legs_run_side.drawReversed(batch, x, y, (int)(10*speedX), true);
                } else {
                    legs_walk_side.drawReversed(batch, x, y, (int)(20*speedX), true);
                }

            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.up) {
            if (cdc.stand) {
                batch.draw(legs_stand_front, x, y);
            } else {
                legs_walk_front.draw(batch, x, y, (int)(10*speedY), false);
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.up_back) {
            if (cdc.stand) {
                batch.draw(legs_stand_front, x, y);
            } else {
                legs_walk_front.drawReversed(batch, x, y, (int)(10*speedY), false);
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.down) {
            if (cdc.stand) {
                batch.draw(legs_stand_front, x, y);
            } else {
                legs_walk_front.draw(batch, x, y, (int)(10*speedY), false);
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.down_back) {
            if (cdc.stand) {
                batch.draw(legs_stand_front, x, y);
            } else {
                legs_walk_front.drawReversed(batch, x, y, (int)(10*speedY), false);
            }
        }
    }

    public void setDirection(int dir, int id) {
        CharacterDirectionChecker cdc = cdcs.get(id);
        if (dir == 1) {
            cdc.walkDir = CharacterDirectionChecker.WalkDirection.left;
            cdc.lookDir = CharacterDirectionChecker.LookDirection.left;
        } else if (dir == 2) {
            cdc.walkDir = CharacterDirectionChecker.WalkDirection.up;
            cdc.lookDir = CharacterDirectionChecker.LookDirection.up;
        } else if (dir == 3) {
            cdc.walkDir = CharacterDirectionChecker.WalkDirection.right;
            cdc.lookDir = CharacterDirectionChecker.LookDirection.right;
        } else {
            cdc.walkDir = CharacterDirectionChecker.WalkDirection.down;
            cdc.lookDir = CharacterDirectionChecker.LookDirection.down;
        }
    }

    public int getDirection(int id) {
        CharacterDirectionChecker cdc = cdcs.get(id);
        if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.left) {
            return 1;
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.up) {
            return 2;
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.right) {
            return 3;
        } else {
            return 0;
        }
    }


    public void invalidateBobbing(int id, float speedX, float speedY) {
        CharacterDirectionChecker cdc = cdcs.get(id);
        boolean push = pushes.get(id);
        if (Math.abs(speedX) > 4 || Math.abs(speedY) > 4) {
            timer1.getCurrentFrame(false);
            bobbings.set(id,(float)(Math.sin((double)((float)timer1.currentFrame/(float)timer1.gs.getLength())*3)-1.0));
        } else {
            timer2.getCurrentFrame(false);
            bobbings.set(id, (float)(Math.sin((double)((float)timer2.currentFrame/(float)timer2.gs.getLength())*3)/2.0f-1.0f));
        }
    }


    private void drawBody(SpriteBatch batch, int id, float x, float y, GlobalSequence body) {
        y+= bobbings.get(id);;
        id+=2;
        CharacterDirectionChecker cdc = cdcs.get(id-2);
        if (cdc.lookDir == CharacterDirectionChecker.LookDirection.left) {
            body.getFrame(BodyPartRotation.side.value).flip(true, false);
            batch.draw(body.getFrame(BodyPartRotation.side.value), x, y);
            body.getFrame(BodyPartRotation.side.value).flip(true, false);
        } else if (cdc.lookDir == CharacterDirectionChecker.LookDirection.right) {
            batch.draw(body.getFrame(BodyPartRotation.side.value), x, y);
        } else if (cdc.lookDir == CharacterDirectionChecker.LookDirection.up) {
            batch.draw(body.getFrame(BodyPartRotation.back.value), x, y);
        } else if (cdc.lookDir == CharacterDirectionChecker.LookDirection.down) {
            batch.draw(body.getFrame(BodyPartRotation.front.value), x, y);
        }
    }

    private void drawHead(SpriteBatch batch, int id, float x, float y, GlobalSequence head) {
        y+=bobbings.get(id);
        CharacterDirectionChecker cdc = cdcs.get(id);
        if (cdc.lookDir == CharacterDirectionChecker.LookDirection.left) {
            head.getFrame(BodyPartRotation.side.value).flip(true, false);
            batch.draw(head.getFrame(BodyPartRotation.side.value), x, y);
            head.getFrame(BodyPartRotation.side.value).flip(true, false);
        } else if (cdc.lookDir == CharacterDirectionChecker.LookDirection.right) {
            batch.draw(head.getFrame(BodyPartRotation.side.value), x, y);
        } else if (cdc.lookDir == CharacterDirectionChecker.LookDirection.up) {
            batch.draw(head.getFrame(BodyPartRotation.back.value), x, y);
        } else if (cdc.lookDir == CharacterDirectionChecker.LookDirection.down) {
            batch.draw(head.getFrame(BodyPartRotation.front.value), x, y);
        }
    }

    private void drawArm(SpriteBatch batch, float x, float y, int id, GlobalSequence objectInHands, MovingConfiguration mc) {
        //y+=bobbing;
        y-=1;
        CharacterDirectionChecker cdc = cdcs.get(id);
        boolean push = pushes.get(id) || objectInHands != null;
        if (cdc.lookDir == CharacterDirectionChecker.LookDirection.left) {
            if (push && (mc.movingUp == 0 && mc.movingDown == 0 || objectInHands != null)) {
                if (objectInHands != null) {
                    objectInHands.getFrame(2).flip(true, false);
                    batch.draw(objectInHands.getFrame(2), x - 21, y - 8);
                    objectInHands.getFrame(2).flip(true, false);
                    batch.draw(arms_push_side_reversed, x-8, y);
                } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.left) {
                    batch.draw(arms_push_side_reversed, x-8, y);
                } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.right_back) {
                    batch.draw(arms_push_side_back_reversed, x, y);
                } else {
                    batch.draw(arms_side_reversed, x, y);
                }
            } else {
                batch.draw(arms_side_reversed, x, y);
            }
        } else if (cdc.lookDir == CharacterDirectionChecker.LookDirection.right) {
            if (push && (mc.movingUp == 0 && mc.movingDown == 0 || objectInHands != null)) {
                if (objectInHands != null) {
                    batch.draw(objectInHands.getFrame(2), x - 11, y - 8);
                    batch.draw(arms_push_side, x - armSideWidth, y);
                } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.right) {
                    batch.draw(arms_push_side, x - armSideWidth, y);
                } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.left_back) {
                    batch.draw(arms_push_side_back, x - armSideWidth - 6, y);
                } else {
                    batch.draw(arms_side, x - armSideWidth, y);
                }
            } else {
                batch.draw(arms_side, x - armSideWidth, y);
            }
        } else if (cdc.lookDir == CharacterDirectionChecker.LookDirection.up) {
            if (push) {
                if (objectInHands != null) {
                    batch.draw(objectInHands.getFrame(1), x - 16, y - 6);
                    batch.draw(arms_push_back, x - armFrontWidth - 5, y);
                    batch.draw(arms_push_back_reversed, x + 5, y);
                } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.up) {
                    batch.draw(arms_push_back, x - armFrontWidth - 5, y);
                    batch.draw(arms_push_back_reversed, x + 5, y);
                } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.down_back) {
                    batch.draw(arms_push_front, x - armFrontWidth - 5, y);
                    batch.draw(arms_push_front_reversed, x + 5, y);
                } else {
                    batch.draw(arms_front, x - armFrontWidth - 5, y);
                    batch.draw(arms_front_reversed, x + 5, y);
                }
            } else {
                batch.draw(arms_front, x - armFrontWidth - 5, y);
                batch.draw(arms_front_reversed, x + 5, y);
            }
        } else if (cdc.lookDir == CharacterDirectionChecker.LookDirection.down) {
            if (push) {
                if (objectInHands != null) {
                    batch.draw(arms_push_front, x - armFrontWidth - 5, y);
                    batch.draw(arms_push_front_reversed, x + 5, y);
                    batch.draw(objectInHands.getFrame(0), x - 17, y - 10);
                } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.down || cdc.walkDir == CharacterDirectionChecker.WalkDirection.up_back) {
                    batch.draw(arms_push_front, x - armFrontWidth - 5, y);
                    batch.draw(arms_push_front_reversed, x + 5, y);
                } else {
                    batch.draw(arms_front, x - armFrontWidth - 5, y);
                    batch.draw(arms_front_reversed, x + 5, y);
                }
            } else {
                batch.draw(arms_front, x - armFrontWidth - 5, y);
                batch.draw(arms_front_reversed, x + 5, y);
            }
        }
    }
}
