package com.mygdx.schoolRPG.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.security.Key;
import java.util.ArrayList;

/**
 * Created by Kraft on 15.03.2015.
 */
public class CharacterMaker {
    enum BodyPartRotation{
        front(0), back(1), side(2);

        private int value;
        private BodyPartRotation(int value){
            this.value = value;
        }
    }
    AnimationSequence legs_walk_front, legs_walk_side, timer1, timer2;
    ArrayList<GlobalSequence> bodies, heads;
    Texture legs_stand_front, legs_stand_side, arms_front, arms_side, arms_push_side, arms_push_front, arms_push_back, arms_push_side_back;
    TextureRegion arms_front_reversed, arms_side_reversed, legs_stand_side_reversed, arms_push_side_reversed, arms_push_front_reversed, arms_push_back_reversed, arms_push_side_back_reversed;
    public CharacterDirectionChecker cdc;
    public boolean push = true;
    public boolean go = false;
    int legsHeight = 10;
    int bodyHeight = 16;
    int armsLevel = 6;
    int armsOffset = 4;
    int armsOffsetSide = 5;
    int legsWidth, bodyWidth, armFrontWidth, armSideWidth, headWidth;
    float bobbing = 0;
    int charsCount = 0;

    public CharacterMaker(AssetManager assets, String worldPath) {
        FileHandle curDir = Gdx.files.internal(worldPath + "/chars");
        for (FileHandle entry: curDir.list()) {
            if (entry.file().isDirectory()) {
                assets.load(entry.path() + "/head.png", Texture.class);
                assets.load(entry.path()  + "/body.png", Texture.class);
                charsCount++;
            }
        }
        assets.load("char/body_male.png", Texture.class);
        assets.load("char/body_female.png", Texture.class);
        assets.load("char/stand_front.png", Texture.class);
        assets.load("char/stand_side.png", Texture.class);
        assets.load("char/walk_front.png", Texture.class);
        assets.load("char/walk_side.png", Texture.class);
        assets.load("char/stay_front.png", Texture.class);
        assets.load("char/stay_side.png", Texture.class);
        assets.load("char/push_side.png", Texture.class);
        assets.load("char/push_front.png", Texture.class);
        assets.load("char/push_back.png", Texture.class);
        assets.load("char/push_side_back.png", Texture.class);
        cdc = new CharacterDirectionChecker();
    }

    public void initialiseResources(AssetManager assets, String worldPath) {
        bodies = new ArrayList<GlobalSequence>();
        heads = new ArrayList<GlobalSequence>();
        bodies.add(new GlobalSequence(assets,"char/body_male.png", 3));
        bodies.add(new GlobalSequence(assets,"char/body_female.png", 3));
        for (int i =0; i < charsCount; ++i) {
            bodies.add(new GlobalSequence(assets, worldPath + "/chars/" + i + "/body.png", 3));
            heads.add(new GlobalSequence(assets, worldPath + "/chars/" + i + "/head.png", 3));
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
        legs_walk_front = new AnimationSequence(assets, "char/walk_front.png", 15, true, 8);
        legs_walk_side = new AnimationSequence(assets, "char/walk_side.png", 20, true, 8);
        legs_stand_side_reversed = new TextureRegion(legs_stand_side);
        legs_stand_side_reversed.flip(true, false);
        arms_side_reversed = new TextureRegion(arms_side);
        arms_side_reversed.flip(true, false);
        arms_front_reversed = new TextureRegion(arms_front);
        arms_front_reversed.flip(true, false);
        timer1 = new AnimationSequence(assets, "char/walk_side.png", 20, true, 8);
        timer2 = new AnimationSequence(assets, "char/walk_front.png", 15, true, 8);
    }

    public boolean directionsCheck() {
        return cdc.update();
    }

    public void draw(SpriteBatch batch, int id, float x, float y, float speedX, float speedY) {

        legsWidth = legs_stand_front.getWidth();
        bodyWidth = bodies.get(id).getWidth()/3;
        armFrontWidth = arms_front.getWidth();
        armSideWidth = arms_side.getWidth();
        headWidth = heads.get(id).getWidth()/3;
        if (cdc.lookDir == CharacterDirectionChecker.LookDirection.up) {
            drawLegs(batch, x-legsWidth/2, y, speedX, speedY);
            drawHead(batch, id, x-headWidth/2, y+legsHeight+bodyHeight+bobbing);
            drawArm(batch, x, y + legsHeight + armsLevel + bobbing);
            drawBody(batch, id, x-bodyWidth/2, y+legsHeight+bobbing);
        } else {
            if (cdc.lookDir != CharacterDirectionChecker.LookDirection.down) {
                drawArm(batch, x, y+legsHeight+armsLevel+bobbing+3);
            }
            drawLegs(batch, x-legsWidth/2, y, speedX, speedY);
            drawBody(batch, id, x-bodyWidth/2, y+legsHeight+bobbing);
            drawHead(batch, id, x-headWidth / 2, y + legsHeight + bodyHeight + bobbing);
            drawArm(batch, x, y+legsHeight+armsLevel+bobbing);
        }
    }

    private void drawLegs(SpriteBatch batch, float x, float y, float speedX, float speedY) {
        //System.out.println(speedX);
        if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.left) {
            if (cdc.stand) {
                batch.draw(legs_stand_side_reversed, x, y);
            } else if (Gdx.input.isKeyPressed(Input.Keys.UP) && Math.abs(speedX)<=1 && speedY > speedX) {
                legs_walk_front.drawReversed(batch, x, y, (int) (15 * speedY), false);
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && Math.abs(speedX)<=1 && speedY > speedX) {
                legs_walk_front.draw(batch, x, y, (int)(15*speedY), false);
            } else {
                legs_walk_side.draw(batch, x, y, (int)(20*speedX), true);
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.left_back) {
            if (cdc.stand) {
                batch.draw(legs_stand_side_reversed, x, y);
            } else {
                legs_walk_side.drawReversed(batch, x, y, (int)(20*speedX), false);
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.right) {
            if (cdc.stand) {
                batch.draw(legs_stand_side, x, y);
            } else if (Gdx.input.isKeyPressed(Input.Keys.UP) && Math.abs(speedX)<=1 && speedY > speedX) {
                legs_walk_front.drawReversed(batch, x, y, (int)(15*speedY), false);
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && Math.abs(speedX)<=1 && speedY > speedX) {
                legs_walk_front.draw(batch, x, y, (int)(15*speedY), false);
            }  else {
                legs_walk_side.draw(batch, x, y, (int)(20*speedX), false);
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.right_back) {
            if (cdc.stand) {
                batch.draw(legs_stand_side, x, y);
            } else {
                legs_walk_side.drawReversed(batch, x, y, (int)(20*speedX), true);
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.up) {
            if (cdc.stand) {
                batch.draw(legs_stand_front, x, y);
            } else {
                legs_walk_front.draw(batch, x, y, (int)(15*speedY), false);
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.up_back) {
            if (cdc.stand) {
                batch.draw(legs_stand_front, x, y);
            } else {
                legs_walk_front.drawReversed(batch, x, y, (int)(15*speedY), false);
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.down) {
            if (cdc.stand) {
                batch.draw(legs_stand_front, x, y);
            } else {
                legs_walk_front.draw(batch, x, y, (int)(15*speedY), false);
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.down_back) {
            if (cdc.stand) {
                batch.draw(legs_stand_front, x, y);
            } else {
                legs_walk_front.drawReversed(batch, x, y, (int)(15*speedY), false);
            }
        }
    }

    public void invalidateBobbing() {
        if (/*cdc.stand || push*/go) {
            timer1.getCurrentFrame(false);
            bobbing = (float)(Math.sin((double)((float)timer1.currentFrame/(float)timer1.gs.getLength())*3)-1.0);
        } else {
            timer2.getCurrentFrame(false);
            bobbing = (float)(Math.sin((double)((float)timer2.currentFrame/(float)timer2.gs.getLength())*3)/2.0f-1.0f);
        }
    }


    private void drawBody(SpriteBatch batch, int id, float x, float y) {
        y+=bobbing;
        if (cdc.lookDir == CharacterDirectionChecker.LookDirection.left) {
            bodies.get(id).getFrame(BodyPartRotation.side.value).flip(true, false);
            batch.draw(bodies.get(id).getFrame(BodyPartRotation.side.value), x, y);
            bodies.get(id).getFrame(BodyPartRotation.side.value).flip(true, false);
        } else if (cdc.lookDir == CharacterDirectionChecker.LookDirection.right) {
            batch.draw(bodies.get(id).getFrame(BodyPartRotation.side.value), x, y);
        } else if (cdc.lookDir == CharacterDirectionChecker.LookDirection.up) {
            batch.draw(bodies.get(id).getFrame(BodyPartRotation.back.value), x, y);
        } else if (cdc.lookDir == CharacterDirectionChecker.LookDirection.down) {
            batch.draw(bodies.get(id).getFrame(BodyPartRotation.front.value), x, y);
        }
    }

    private void drawHead(SpriteBatch batch, int id, float x, float y) {
        y+=bobbing;
        if (cdc.lookDir == CharacterDirectionChecker.LookDirection.left) {
            heads.get(id).getFrame(BodyPartRotation.side.value).flip(true, false);
            batch.draw(heads.get(id).getFrame(BodyPartRotation.side.value), x, y);
            heads.get(id).getFrame(BodyPartRotation.side.value).flip(true, false);
        } else if (cdc.lookDir == CharacterDirectionChecker.LookDirection.right) {
            batch.draw(heads.get(id).getFrame(BodyPartRotation.side.value), x, y);
        } else if (cdc.lookDir == CharacterDirectionChecker.LookDirection.up) {
            batch.draw(heads.get(id).getFrame(BodyPartRotation.back.value), x, y);
        } else if (cdc.lookDir == CharacterDirectionChecker.LookDirection.down) {
            batch.draw(heads.get(id).getFrame(BodyPartRotation.front.value), x, y);
        }
    }

    private void drawArm(SpriteBatch batch, float x, float y) {
        //y+=bobbing;
        if (cdc.lookDir == CharacterDirectionChecker.LookDirection.left) {
            if (push && !Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.left) {
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
            if (push && !Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.right) {
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
                if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.up) {
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
                if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.down || cdc.walkDir == CharacterDirectionChecker.WalkDirection.up_back) {
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
