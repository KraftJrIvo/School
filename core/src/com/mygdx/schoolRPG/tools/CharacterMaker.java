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
    AnimationSequence legs_walk_front, legs_walk_side, legs_run_side, timer1, timer2;
    ArrayList<GlobalSequence> bodies, heads;
    public ArrayList<Texture> sprites;
    Texture legs_stand_front, legs_stand_side, arms_front, arms_side, arms_push_side, arms_push_front, arms_push_back, arms_push_side_back;
    TextureRegion arms_front_reversed, arms_side_reversed, legs_stand_side_reversed, arms_push_side_reversed, arms_push_front_reversed, arms_push_back_reversed, arms_push_side_back_reversed;
    //public CharacterDirectionChecker cdc;
    public ArrayList<CharacterDirectionChecker> cdcs;
    public ArrayList<Boolean> pushes;
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
    int charsCount = 0;

    public CharacterMaker(AssetManager assets, String worldPath) {
        FileHandle curDir = Gdx.files.internal(worldPath + "/chars");
        for (FileHandle entry: curDir.list()) {
            if (entry.file().isDirectory()) {
                FileHandle spriteFile = Gdx.files.internal(entry.path() + "/sprite.png");
                if (spriteFile.exists()) {
                    assets.load(entry.path() + "/sprite.png", Texture.class);
                } else {
                    assets.load(entry.path() + "/head.png", Texture.class);
                    assets.load(entry.path()  + "/body.png", Texture.class);
                }
                charsCount++;
                FileHandle curDir1 = Gdx.files.internal(entry.path() + "/graphics");
                if (curDir1.exists()) {
                    for (FileHandle entry1: curDir1.list()) {
                        assets.load(entry1.path(), Texture.class);
                    }
                }
            }
        }
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
        pushes = new ArrayList<Boolean>();
        pushes.add(new Boolean(false));
    }

    public void initialiseResources(AssetManager assets, String worldPath) {
        bodies = new ArrayList<GlobalSequence>();
        heads = new ArrayList<GlobalSequence>();
        sprites = new ArrayList<Texture>();
        bodies.add(new GlobalSequence(assets,"char/body_male.png", 3));
        bodies.add(new GlobalSequence(assets,"char/body_female.png", 3));
        for (int i =0; i < charsCount; ++i) {
            if (assets.isLoaded(worldPath + "/chars/" + i + "/sprite.png")) {
                sprites.add(assets.get(worldPath + "/chars/" + i + "/sprite.png", Texture.class));
                bodies.add(null);
                heads.add(null);
            } else {
                bodies.add(new GlobalSequence(assets, worldPath + "/chars/" + i + "/body.png", 3));
                heads.add(new GlobalSequence(assets, worldPath + "/chars/" + i + "/head.png", 3));
                sprites.add(null);
            }
        }
        for (int i =0; i < heads.size()-1; ++i) {
            cdcs.add(new CharacterDirectionChecker(false));
            bobbings.add(new Float(0));
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
        legs_walk_front = new AnimationSequence(assets, "char/walk_front.png", 15, true, 8);
        legs_walk_side = new AnimationSequence(assets, "char/walk_side.png", 20, true, 8);
        legs_run_side = new AnimationSequence(assets, "char/run_side.png", 20, true, 7);
        legs_stand_side_reversed = new TextureRegion(legs_stand_side);
        legs_stand_side_reversed.flip(true, false);
        arms_side_reversed = new TextureRegion(arms_side);
        arms_side_reversed.flip(true, false);
        arms_front_reversed = new TextureRegion(arms_front);
        arms_front_reversed.flip(true, false);
        timer1 = new AnimationSequence(assets, "char/walk_side.png", 20, true, 8);
        timer2 = new AnimationSequence(assets, "char/walk_front.png", 15, true, 8);
    }

    public boolean directionsCheck(int id) {
        return cdcs.get(id).update();
    }

    public void draw(SpriteBatch batch, int id, float x, float y, float speedX, float speedY) {
        if (sprites.get(id) == null) {
            legsWidth = legs_stand_front.getWidth();
            bodyWidth = bodies.get(id).getWidth()/3;
            armFrontWidth = arms_front.getWidth();
            armSideWidth = arms_side.getWidth();
            headWidth = heads.get(id).getWidth()/3;
            float bobbing = bobbings.get(id);
            if (cdcs.get(id).lookDir == CharacterDirectionChecker.LookDirection.up) {
                drawLegs(batch, x-legsWidth/2, y, speedX, speedY, id);
                drawHead(batch, id, x-headWidth/2, y+legsHeight+bodyHeight+bobbing);
                drawArm(batch, x, y + legsHeight + armsLevel + bobbing, id);
                drawBody(batch, id, x-bodyWidth/2, y+legsHeight+bobbing);
            } else {
                if (cdcs.get(id).lookDir != CharacterDirectionChecker.LookDirection.down) {
                    drawArm(batch, x, y+legsHeight+armsLevel+bobbing+3, id);
                }
                drawLegs(batch, x-legsWidth/2, y, speedX, speedY, id);
                drawBody(batch, id, x-bodyWidth/2, y+legsHeight+bobbing);
                drawHead(batch, id, x-headWidth / 2, y + legsHeight + bodyHeight + bobbing);
                drawArm(batch, x, y+legsHeight+armsLevel+bobbing, id);
            }
        } else {
            Texture sprite = sprites.get(id);
            batch.draw(sprite, x - sprite.getWidth()/2, y);
        }
    }

    private void drawLegs(SpriteBatch batch, float x, float y, float speedX, float speedY, int id) {
        //System.out.println(speedX);
        CharacterDirectionChecker cdc = cdcs.get(id);
        if (cdc.walkDir == CharacterDirectionChecker.WalkDirection.left) {
            if (cdc.stand) {
                batch.draw(legs_stand_side_reversed, x, y);
            } else if (Gdx.input.isKeyPressed(Input.Keys.UP) && Math.abs(speedX)<=1 && speedY > speedX) {
                legs_walk_front.drawReversed(batch, x, y, (int) (15 * speedY), false);
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && Math.abs(speedX)<=1 && speedY > speedX) {
                legs_walk_front.draw(batch, x, y, (int)(15*speedY), false);
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
            } else if (Gdx.input.isKeyPressed(Input.Keys.UP) && Math.abs(speedX)<=1 && speedY > speedX) {
                legs_walk_front.drawReversed(batch, x, y, (int)(15*speedY), false);
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && Math.abs(speedX)<=1 && speedY > speedX) {
                legs_walk_front.draw(batch, x, y, (int)(15*speedY), false);
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


    private void drawBody(SpriteBatch batch, int id, float x, float y) {
        y+= bobbings.get(id);;
        id+=2;
        CharacterDirectionChecker cdc = cdcs.get(id-2);
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
        y+=bobbings.get(id);
        CharacterDirectionChecker cdc = cdcs.get(id);
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

    private void drawArm(SpriteBatch batch, float x, float y, int id) {
        //y+=bobbing;
        CharacterDirectionChecker cdc = cdcs.get(id);
        boolean push = pushes.get(id);
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
