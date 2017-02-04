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
import com.mygdx.schoolRPG.tools.GlobalSequence;
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
    public ArrayList<String> changedFlags;
    public int flagsCount = 0;
    public Color charColor;
    boolean isRunning = false;
    public ArrayList<Item> inventory;
    int lastHoldedFlag = -1;
    boolean isControlled;
    MovingConfiguration movingConfiguration;

    public GlobalSequence headWear;
    public GlobalSequence bodyWear;
    public GlobalSequence objectInHands;

    public NPC(AssetManager assets, String baseName, float x, float y, float width, float height, float floorHeight, boolean movable, CharacterMaker characterMaker, int charId, String worldDir) {
        super(assets, (String)null, x, y, width, height, floorHeight, movable, 0);
        movingConfiguration = new MovingConfiguration();
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
        changedFlags = new ArrayList<String>();
        ArrayList<String> itemsNames = new ArrayList<String>();
        ArrayList<Integer> itemsCounts = new ArrayList<Integer>();
        inventory = new ArrayList<Item>();
        String charPath = worldDir + "/chars/" + charId;
        if (baseName == null) {
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
                line = in.readLine();
                int itemsCount = Integer.parseInt(line);
                for (int j =0; j < itemsCount; ++j) {
                    line = in.readLine();
                    itemsNames.add(line);
                    line = in.readLine();
                    itemsCounts.add(Integer.parseInt(line));
                    Item item = new Item(assets, worldDir, itemsNames.get(j));
                    item.stack = itemsCounts.get(j);
                    inventory.add(item);
                }
                in.readLine();
                line = in.readLine();
                if (!line.equals("no")) {
                    for (int i =0; i < itemsCount; ++i) {
                        if (inventory.get(i).fileName.equals(line)) {
                            headWear = inventory.get(i).sides;
                        }
                    }
                }
                in.readLine();
                line = in.readLine();
                if (!line.equals("no")) {
                    for (int i =0; i < itemsCount; ++i) {
                        if (inventory.get(i).fileName.equals(line)) {
                            bodyWear = inventory.get(i).sides;
                        }
                    }
                }
                in.readLine();
                line = in.readLine();
                if (!line.equals("no")) {
                    for (int i =0; i < itemsCount; ++i) {
                        if (inventory.get(i).fileName.equals(line)) {
                            objectInHands = inventory.get(i).sides;
                        }
                    }
                }
                in.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setWears();
        //lastInventorySize = inventory.size();
    }

    public void updateFlagsAfterRemoval(Item droppedItem) {
        int index = flagNames.indexOf(droppedItem.flagName);
        if (index < flags.size() && index >= 0 && flags.get(index)) {
            flags.set(index, false);
            changedFlags.add(flagNames.get(index));
        }
    }

    protected void checkInventory() {
        //changedFlags.clear();
        //if (lastInventorySize == inventory.size()) return;
        for (int i = 0; i < flags.size(); ++i) {
            boolean found = false;
            for (int j = 0; j < inventory.size(); ++j) {
                Item item = inventory.get(j);
                if (item.flagName.equals(flagNames.get(i))) {
                    //int index = flagNames.indexOf(item.flagName);
                    found = true;
                    if (item.sides == objectInHands || item.sides == headWear || item.sides == bodyWear) {
                        flags.set(i, true);
                        changedFlags.add(flagNames.get(i));
                    } else if (flags.get(i)) {
                        flags.set(i, false);
                        changedFlags.add(flagNames.get(i));
                    }
                }
            }
            /*if (!found && flags.get(i)) {
                flags.set(i, false);
                changedFlags.add(flagNames.get(i));
            }*/
        }
    }

    public void setWears() {
        if (characterMaker != null) {
            characterMaker.setWears(charId, headWear, bodyWear);
        }
    }

    public void takeItem(Item item) {
        for (int i = 0; i < inventory.size(); ++i) {
            if (inventory.get(i).stackable && inventory.get(i).fileName.equals(item.fileName) && inventory.get(i).stack < inventory.get(i).maxStack) {
                inventory.get(i).stack++;
                return;
            }
        }
        inventory.add(item);
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


    public void move(boolean allowToMove) {
        if (isControlled) {
            movingConfiguration.updateMoving(Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.UP, Input.Keys.DOWN, Input.Keys.SHIFT_LEFT, -1, Input.Keys.E);
        } else
        {
            //movingConfiguration.updateMoving(Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.UP, Input.Keys.DOWN, Input.Keys.SHIFT_LEFT, -1, Input.Keys.E);
        }

        if (allowToMove && movingConfiguration.sprint > 0) {
            isRunning = true;
        } else {
            isRunning = false;
        }

        if (allowToMove && movingConfiguration.movingLeft > 0 && movingConfiguration.movingRight == 0 && !falling) {
            speedX -= 1;
            if (!pushLeft) pushCount = -2;
        }
        else if (speedX < 0) {
            if (speedX == -1){
                speedX = 0;
            } else {
                speedX += 2;
            }
        }
        if (allowToMove && movingConfiguration.movingLeft == 0 && movingConfiguration.movingRight > 0 && !falling) {
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
        if (allowToMove && movingConfiguration.movingUp > 0 && movingConfiguration.movingDown == 0 && !falling) {
            speedY += 1;
            if (!pushUp) pushCount = -2;
        }
        else if (speedY > 0) speedY -= 1;
        if (allowToMove && movingConfiguration.movingUp == 0 && movingConfiguration.movingDown > 0 && !falling) {
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
            if (characterMaker.directionsCheck(charId, movingConfiguration)) {
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

        checkInventory();

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
            if (movingConfiguration.movingUp > 0 && !jumping) {
                curPose = poses.getTile(PlayerMultiTile.PlayerPose.BACK);
                setToJump = false;
            } else if (movingConfiguration.movingDown > 0 && !jumping)  {
                curPose = poses.getTile(PlayerMultiTile.PlayerPose.FRONT);
                setToJump = false;
            } else {
                graphicY = y;
            }
            int animDraw = 0;
            if (movingConfiguration.movingLeft > 0) {
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
            } else if (movingConfiguration.movingRight > 0) {
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
        checkInventory();
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

        characterMaker.draw(batch, charId, offsetX + graphicX + hitBox.width / 2, offsetY - graphicY - hitBox.height + floorHeight - z, Math.abs((int)Math.floor(diffX*10))/10, Math.abs((int)Math.floor(diffY)), headWear, bodyWear, objectInHands, movingConfiguration);

    }


}
