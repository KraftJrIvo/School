package com.mygdx.schoolRPG.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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
    AnimationSequence legs_walk_front, legs_walk_side;
    ArrayList<GlobalSequence> bodies, heads;
    Texture legs_stand_front, legs_stand_side, arms_front, arms_side;
    TextureRegion arms_front_reversed, arms_side_reversed, legs_stand_side_reversed;
    public CharacterDirectionChecker cdc;
    int legsHeight = 10;
    int bodyHeight = 16;
    int armsLevel = 6;
    int armsOffset = 4;
    int armsOffsetSide = 5;
    int legsWidth, bodyWidth, armFrontWidth, armSideWidth, headWidth;
    float bobbing = 0;

    public CharacterMaker(AssetManager assets) {
        FileHandle curDir = Gdx.files.internal("chars/bodies");
        for (FileHandle entry: curDir.list()) {
            if (entry.file().getName().equals("Thumbs.db") || entry.file().isDirectory()) {
                continue;
            }
            assets.load(entry.path(), Texture.class);
        }
        curDir = Gdx.files.internal("chars/heads");
        for (FileHandle entry: curDir.list()) {
            if (entry.file().getName().equals("Thumbs.db") || entry.file().isDirectory()) {
                continue;
            }
            assets.load(entry.path(), Texture.class);
        }
        assets.load("chars/legs/stand_front.png", Texture.class);
        assets.load("chars/legs/stand_side.png", Texture.class);
        assets.load("chars/legs/walk_front.png", Texture.class);
        assets.load("chars/legs/walk_side.png", Texture.class);
        assets.load("chars/arms/stay_front.png", Texture.class);
        assets.load("chars/arms/stay_side.png", Texture.class);
        cdc = new CharacterDirectionChecker();
    }

    public void initialiseResources(AssetManager assets) {
        bodies = new ArrayList<GlobalSequence>();
        FileHandle curDir = Gdx.files.internal("chars/bodies");
        for (FileHandle entry: curDir.list()) {
            if (entry.file().getName().equals("Thumbs.db") || entry.file().isDirectory()) {
                continue;
            }
            bodies.add(new GlobalSequence(assets, entry.path()));
            //assets.load(entry.path(), Texture.class);
        }
        heads = new ArrayList<GlobalSequence>();
        curDir = Gdx.files.internal("chars/heads");
        for (FileHandle entry: curDir.list()) {
            if (entry.file().getName().equals("Thumbs.db") || entry.file().isDirectory()) {
                continue;
            }
            heads.add(new GlobalSequence(assets, entry.path()));
        }
        legs_stand_front = assets.get("chars/legs/stand_front.png");
        legs_stand_side = assets.get("chars/legs/stand_side.png");
        arms_front = assets.get("chars/arms/stay_front.png");
        arms_side = assets.get("chars/arms/stay_side.png");
        legs_walk_front = new AnimationSequence(assets, "chars/legs/walk_front.png", 15, true);
        legs_walk_side = new AnimationSequence(assets, "chars/legs/walk_side.png", 20, true);
        legs_stand_side_reversed = new TextureRegion(legs_stand_side);
        legs_stand_side_reversed.flip(true, false);
        arms_side_reversed = new TextureRegion(arms_side);
        arms_side_reversed.flip(true, false);
        arms_front_reversed = new TextureRegion(arms_front);
        arms_front_reversed.flip(true, false);
    }

    public void draw(SpriteBatch batch, int id, float x, float y, float speedX, float speedY) {
        cdc.update();
        legsWidth = legs_stand_front.getWidth();
        bodyWidth = bodies.get(id).getWidth()/3;
        armFrontWidth = arms_front.getWidth();
        armSideWidth = arms_side.getWidth();
        headWidth = heads.get(id).getWidth()/3;
        if (cdc.lookDir == CharacterDirectionChecker.LookDirection.up) {
            drawLegs(batch, x-legsWidth/2, y, speedX, speedY);
            drawHead(batch, id, x-headWidth/2, y+legsHeight+bodyHeight+bobbing);
            drawArms(batch, x, y+legsHeight+armsLevel+bobbing);
            drawBody(batch, id, x-bodyWidth/2, y+legsHeight+bobbing);
        } else {
            drawLegs(batch, x-legsWidth/2, y, speedX, speedY);
            drawBody(batch, id, x-bodyWidth/2, y+legsHeight+bobbing);
            drawHead(batch, id, x-headWidth/2, y+legsHeight+bodyHeight+bobbing);
            drawArms(batch, x, y+legsHeight+armsLevel+bobbing);
        }
    }

    private void drawLegs(SpriteBatch batch, float x, float y, float speedX, float speedY) {
        if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.left) {
            if (cdc.stand) {
                batch.draw(legs_stand_side_reversed, x, y);
                bobbing = 0;
            } else {
                legs_walk_side.draw(batch, x, y, (int)(20*speedX), true);
                bobbing = (legs_walk_side.currentFrame/legs_walk_side.gs.getLength())*5;
                if (legs_walk_side.currentFrame != 0) {
                    bobbing = (legs_walk_side.currentFrame/legs_walk_side.gs.getLength())*5;
                }
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.left_back) {
            if (cdc.stand) {
                batch.draw(legs_stand_side_reversed, x, y);
                bobbing = 0;
            } else {
                legs_walk_side.drawReversed(batch, x, y, (int)(20*speedX), false);
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.right) {
            if (cdc.stand) {
                batch.draw(legs_stand_side, x, y);
                bobbing = 0;
            } else {
                legs_walk_side.draw(batch, x, y, (int)(20*speedX), false);
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.right_back) {
            if (cdc.stand) {
                batch.draw(legs_stand_side, x, y);
                bobbing = 0;
            } else {
                legs_walk_side.drawReversed(batch, x, y, (int)(20*speedX), true);
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.up) {
            if (cdc.stand) {
                batch.draw(legs_stand_front, x, y);
                bobbing = 0;
            } else {
                legs_walk_front.draw(batch, x, y, (int)(15*speedY), false);
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.up_back) {
            if (cdc.stand) {
                batch.draw(legs_stand_front, x, y);
                bobbing = 0;
            } else {
                legs_walk_front.drawReversed(batch, x, y, (int)(15*speedY), false);
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.down) {
            if (cdc.stand) {
                batch.draw(legs_stand_front, x, y);
                bobbing = 0;
            } else {
                legs_walk_front.draw(batch, x, y, (int)(15*speedY), false);
            }
        } else if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.down_back) {
            if (cdc.stand) {
                batch.draw(legs_stand_front, x, y);
                bobbing = 0;
            } else {
                legs_walk_front.drawReversed(batch, x, y, (int)(15*speedY), false);
            }
        }
    }

    private void drawBody(SpriteBatch batch, int id, float x, float y) {
        //y+=bobbing;
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
        //y+=bobbing;
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

    private void drawArms(SpriteBatch batch, float x, float y) {
        //y+=bobbing;
        if (cdc.lookDir == CharacterDirectionChecker.LookDirection.left) {
            batch.draw(arms_side_reversed, x, y);
        } else if (cdc.lookDir == CharacterDirectionChecker.LookDirection.right) {
            batch.draw(arms_side, x-armSideWidth, y);
        } else if (cdc.lookDir == CharacterDirectionChecker.LookDirection.up) {
            batch.draw(arms_front, x-armFrontWidth-5, y);
            batch.draw(arms_front_reversed, x+5, y);
        } else if (cdc.lookDir == CharacterDirectionChecker.LookDirection.down) {
            batch.draw(arms_front, x-armFrontWidth-5, y);
            batch.draw(arms_front_reversed, x+5, y);
        }
    }
}
