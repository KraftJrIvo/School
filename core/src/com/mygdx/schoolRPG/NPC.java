package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.schoolRPG.tools.*;

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
    Sound speechSound;
    Sound jump, djump, land, step, die;
    World world;
    ArrayList<Task> tasks;
    Task currentTask = null;
    ArrayList<Exit> currentTaskPath;
    ArrayList<IntCoords> currentExitPath;
    RoomNode curRoom;
    RoomNode curExitRoom;
    long lastTransition;
    boolean lastHandledOffscreen = false;
    boolean repeatTasks = false;
    String charPath;
    boolean lastMovedUp = false;
    boolean lastMovedDown = false;
    boolean ignoreNextZero = false;

    public GlobalSequence headWear;
    public GlobalSequence bodyWear;
    public GlobalSequence objectInHands;

    int walkingFrames = 0;

    int dir = 0;

    public NPC(AssetManager assets, String baseName, float x, float y, float width, float height, float floorHeight, boolean movable, CharacterMaker characterMaker, int charId, World world) {
        super(assets, (String)null, x, y, width, height, floorHeight, movable, 0);
        movingConfiguration = new MovingConfiguration();
        //wh = 10;
        this.charId = charId;
        type = 2;
        spritesPath = baseName;
        jumpTicks = maxJumpTicks;
        this.world = world;
        if (spritesPath != null) {
            poses = new PlayerMultiTile(spritesPath, assets);
            chargo = new AnimationSequence(assets, spritesPath.substring(0, spritesPath.length()-4)+"go.png", 20, true, 5);
            jump = assets.get("platform_sounds/jump.wav");
            djump = assets.get("platform_sounds/djump.wav");
            die = assets.get("platform_sounds/die.wav");
            step = assets.get("platform_sounds/step.wav");
            land = assets.get("platform_sounds/land.wav");
        }
        this.characterMaker = characterMaker;
        flags = new ArrayList<Boolean>();
        flagNames = new ArrayList<String>();
        changedFlags = new ArrayList<String>();
        ArrayList<String> itemsNames = new ArrayList<String>();
        ArrayList<Integer> itemsCounts = new ArrayList<Integer>();
        inventory = new ArrayList<Item>();
        charPath = world.worldDir + "/chars/" + charId;
        speechSound = assets.get(charPath + "/speech.wav", Sound.class);
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
                    Item item = new Item(assets, world.worldDir.path(), itemsNames.get(j));
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
        tasks = new ArrayList<Task>();
        currentTaskPath = new ArrayList<Exit>();
        currentExitPath = new ArrayList<IntCoords>();
        parseTasks();

        //lastInventorySize = inventory.size();
    }

    private void parseTasks() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(charPath + "/behavior"));
            int tasksCount = Integer.parseInt(in.readLine());
            for (int i =0; i < tasksCount; ++i) {
                tasks.add(new Task(in, world));
            }
            String str = in.readLine();
            repeatTasks = (str.charAt(0) == 'r');
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTask() {
        if (tasks.size() == 0) {
            currentTask = null;
            return;
        }
        currentTask = tasks.get(0);
        int npcId = world.npcs.indexOf(this);
        Area curArea = world.areas.get(world.npcsAreas.get(npcId));
        curRoom = world.map.getRoomByName(curArea.name);
        IntCoords myCoords = new IntCoords((int)Math.floor(x/curArea.TILE_WIDTH), (int)Math.floor(y/curArea.TILE_HEIGHT));
        IntCoords taskCoords;
        if (currentTask.random) {
            taskCoords = new IntCoords(-1, -1);
        } else if (currentTask.objectName != null) {
            taskCoords = new IntCoords(-1, -1);
        } else {
            taskCoords = currentTask.coords.get(0);
        }
        if (!currentTask.destinationRoomName.equals(curRoom.name)) {
            currentTaskPath = world.map.findPathToRoom(curRoom, myCoords, world.map.getRoomByName(currentTask.destinationRoomName), taskCoords);
        } else {
            currentTask.destinationReached = true;
            lastTransition = System.currentTimeMillis();
        }
    }

    public Exit handleOffscreenTasks() {
        Exit pos = null;
        if (currentTask == null || currentTaskPath == null) {
            if (tasks.size() > 0) {
                updateTask();
                lastTransition = System.currentTimeMillis();
            } else if (repeatTasks) {
                parseTasks();
            }
        }
        if (currentTask == null) {
            movingConfiguration.movingRight = 0;
            movingConfiguration.movingLeft = 0;
            movingConfiguration.movingUp = 0;
            movingConfiguration.movingDown = 0;
            return null;
        }
        if (currentTaskPath == null) {
            updateTask();
            lastTransition = System.currentTimeMillis();
        }
        if (!lastHandledOffscreen) {
            lastTransition = System.currentTimeMillis();
            lastHandledOffscreen = true;
        }
        if (!currentTask.destinationReached) {
            if (System.currentTimeMillis() - lastTransition > 3000) {
                if (currentTaskPath.size() == 0) {
                    currentTask.destinationReached = true;
                } else {
                    lastTransition = System.currentTimeMillis();
                    Exit e = currentTaskPath.get(0).otherExit;
                    pos = e;//new IntCoords(e.x, e.y);
                    currentTaskPath.remove(0);
                    if (currentTaskPath.size() == 0) {
                        currentTask.destinationReached = true;
                    }
                }
            }

        } else {
            if (currentTask.startPointReached) {
                if (System.currentTimeMillis() >= currentTask.startedTime + currentTask.time) {
                    currentTask.finished = true;
                    tasks.remove(currentTask);
                    currentTask = null;
                } else {
                    currentTask.finished = true;
                    tasks.remove(currentTask);
                    currentTask = null;
                }
            } else if (System.currentTimeMillis() - lastTransition > 3000) {
                lastTransition = System.currentTimeMillis();
                int npcId = world.npcs.indexOf(this);
                Area curArea = world.areas.get(world.npcsAreas.get(npcId));
                IntCoords coords = new IntCoords((int)Math.floor(x/curArea.TILE_WIDTH), (int)Math.floor(y/curArea.TILE_HEIGHT));
                pos = new Exit(world.map.getRoomByName(currentTask.destinationRoomName), ExitDirection.DOWN, coords.x, coords.y, 1);
                currentTask.startPointReached = true;
                currentTask.started = true;
                currentTask.startedTime = System.currentTimeMillis();
            }
        }
        //System.out.println(curRoom.name);
        return pos;
    }

    public void handleTasks() {
        if (lastHandledOffscreen) {
            movingConfiguration.movingRight = 0;
            movingConfiguration.movingLeft = 0;
            movingConfiguration.movingUp = 0;
            movingConfiguration.movingDown = 0;
            characterMaker.setDirection(dir, charId);
            speedX = 0;
            speedY = 0;
            if (tasks.size() > 0) {
                updateTask();
                currentExitPath = null;
            }
        }
        lastHandledOffscreen = false;
        if (currentTask == null) {
            if (tasks.size() > 0) {
                updateTask();
            } else {
                return;
            }
        } else if (!currentTask.destinationReached) {
            int npcId = world.npcs.indexOf(this);
            Area curArea = world.areas.get(world.npcsAreas.get(npcId));
            curRoom = world.map.getRoomByName(curArea.name);
            IntCoords myCoords = new IntCoords((int)Math.floor(x/curArea.TILE_WIDTH), (int)Math.floor(y/curArea.TILE_HEIGHT));
            if ((currentExitPath == null || currentExitPath.size() == 0)) {
                if (currentTaskPath != null && currentTaskPath.size() > 0) {
                    PathFinder pathFinder = new PathFinder();
                    Exit exit = currentTaskPath.get(0);
                    IntCoords exitCoords = new IntCoords((int)Math.floor((exit.x)/curArea.TILE_WIDTH), (int)Math.floor((exit.y)/curArea.TILE_HEIGHT));
                    currentExitPath = pathFinder.getAStarPath(curRoom.walkables, myCoords, exitCoords);
                    if (currentExitPath != null && currentExitPath.size() > 0) {
                        IntCoords lastCoords = new IntCoords(currentExitPath.get(0).x, currentExitPath.get(0).y);
                        int offsetX = exit.offsetX;
                        int offsetY = exit.offsetY;
                        lastCoords.x += offsetX;
                        lastCoords.y += offsetY;
                        currentExitPath.add(0, lastCoords);
                    }
                    curExitRoom = curRoom;
                } else if (currentTaskPath != null && currentTaskPath.size() == 0){
                    currentTask.destinationReached = true;
                    if (currentTask.coords.size() == 0) {
                        currentTask.startPointReached = true;
                        currentTask.started = true;
                        currentTask.startedTime = System.currentTimeMillis();
                    } else {
                        PathFinder pathFinder = new PathFinder();
                        currentExitPath = pathFinder.getAStarPath(curRoom.walkables, myCoords, currentTask.coords.get(0));
                    }
                }
            } else {
                walktToExit(new IntCoords((int)x, (int)y));
                /*if (currentExitPath.size() == 0 && currentTaskPath != null && currentTaskPath.size() > 0) {
                    currentTaskPath.remove(0);
                }*/
            }
        } else {
            if (currentTask.startPointReached) {
                if (System.currentTimeMillis() >= currentTask.startedTime + currentTask.time) {
                    currentTask.finished = true;
                    tasks.remove(currentTask);
                    currentTask = null;
                } else {

                }
            } else {
                if (currentExitPath == null) {
                    int npcId = world.npcs.indexOf(this);
                    Area curArea = world.areas.get(world.npcsAreas.get(npcId));
                    IntCoords myCoords = new IntCoords((int)Math.floor(x/curArea.TILE_WIDTH), (int)Math.floor(y/curArea.TILE_HEIGHT));
                    PathFinder pathFinder = new PathFinder();
                    if (currentTask.coords.size() > 0) {
                        currentExitPath = pathFinder.getAStarPath(curRoom.walkables, myCoords, currentTask.coords.get(0));
                    } else {
                        currentTask.startPointReached = true;
                    }
                } else {
                    walkToStartPoint(new IntCoords((int)x, (int)y));
                }
            }
        }
    }

    private void walkTo(IntCoords trueCoords, IntCoords currentTarget, ArrayList currentExitPath) {
        int targetX = currentTarget.x * 32 + 8;
        int targetY = currentTarget.y * 16;
        if (targetX < trueCoords.x - 5) {
            movingConfiguration.movingLeft = 1;
            movingConfiguration.movingRight = 0;
        } else if (targetX > trueCoords.x + 5) {
            movingConfiguration.movingRight = 1;
            movingConfiguration.movingLeft = 0;
        } else {
            movingConfiguration.movingRight = 0;
            movingConfiguration.movingLeft = 0;
        }
        if (targetY < trueCoords.y - 3) {
            movingConfiguration.movingUp = 1;
            movingConfiguration.movingDown = 0;
        } else if (targetY > trueCoords.y + 3) {
            movingConfiguration.movingDown = 1;
            movingConfiguration.movingUp = 0;
        } else {
            movingConfiguration.movingUp = 0;
            movingConfiguration.movingDown = 0;
        }
        if (trueCoords.hypot(new IntCoords(targetX, targetY)) < 10) {
            currentExitPath.remove(currentTarget);
        }
        h = hitBox.y;
    }

    private void walktToExit(IntCoords trueCoords) {
        /*if (!curRoom.name.equals(currentTaskPath.get(0).room.name)) {
            currentExitPath.clear();
        }*/
        if (curExitRoom != null && curExitRoom != curRoom) {
            currentExitPath.clear();
            return;
        }
        if (currentExitPath.size() > 0) {
            IntCoords currentTarget = currentExitPath.get(currentExitPath.size() - 1);
            walkTo(trueCoords, currentTarget, currentExitPath);
            if (currentExitPath.size() == 0) {
                movingConfiguration.movingRight = 0;
                movingConfiguration.movingLeft = 0;
                movingConfiguration.movingUp = 0;
                movingConfiguration.movingDown = 0;
                /*Exit curExit = currentTaskPath.get(0);
                if (curExit.direction == ExitDirection.WEST) {

                }*/
            }
        }
    }

    private void walkToStartPoint(IntCoords trueCoords) {
        if (currentExitPath != null && currentExitPath.size() > 0) {
            IntCoords currentTarget = currentExitPath.get(currentExitPath.size() - 1);
            walkTo(trueCoords, currentTarget, currentExitPath);
            if (currentExitPath.size() == 0) {
                currentTask.startPointReached = true;
                movingConfiguration.movingRight = 0;
                movingConfiguration.movingLeft = 0;
                movingConfiguration.movingUp = 0;
                movingConfiguration.movingDown = 0;
            }
        }
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
            if (!lastMovedUp || speedY != 0 || ignoreNextZero) {
                speedY += 1;
                if (speedY > 0) {
                    ignoreNextZero = false;
                }
            } else {
                pushCount = 3;
            }
            lastMovedUp = true;
            if (!pushUp) pushCount = -2;
        }
        else if (speedY > 0) speedY -= 1;
        if (allowToMove && movingConfiguration.movingUp == 0 && movingConfiguration.movingDown > 0 && !falling) {
            if (!lastMovedDown || speedY != 0 || ignoreNextZero) {
                speedY -= 1;
                if (speedY < 0) {
                    ignoreNextZero = false;
                }
            } else {
                pushCount = 3;
            }
            lastMovedDown = true;
            if (!pushDown) pushCount = -2;
        }
        else if (speedY < 0) speedY += 1;

        if ((speedY > 0 && movingConfiguration.movingDown > 0) || (speedY < 0 && movingConfiguration.movingUp > 0)) {
            ignoreNextZero = true;
        }

        if (movingConfiguration.movingUp == 0) {
            lastMovedUp = false;
        }
        if (movingConfiguration.movingDown == 0) {
            lastMovedDown = false;
        }
        if (movingConfiguration.movingLeft > 0 || movingConfiguration.movingRight > 0) {
            lastMovedUp = false;
            lastMovedDown = false;
        }

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

        if (charId == 0) {
            if (Math.abs(hitBox.x-oldX)>0.3f || Math.abs(speedX) > 2) {
                graphicX = x;
            }
            if ((Math.abs(hitBox.y-oldY)>0.5f || Math.abs(speedY) > 1)) {
                graphicY = y;
            }
        } else {
            graphicX = x;
            graphicY = y;
        }
        if (spritesPath != null) {
            //curPose = poses.getTile(PlayerMultiTile.PlayerPose.FRONT);
            if (jumping) {
                //curPose = poses.getTile(PlayerMultiTile.PlayerPose.JUMP);
                setToJump = true;
            }
            if (movingConfiguration.movingUp > 0 && !jumping) {
                curPose = poses.getTile(PlayerMultiTile.PlayerPose.BACK);
                setToJump = false;
                walkingFrames = 0;
            } else if (movingConfiguration.movingDown > 0 && !jumping)  {
                curPose = poses.getTile(PlayerMultiTile.PlayerPose.FRONT);
                setToJump = false;
                walkingFrames = 0;
            } else {
                graphicY = y;
            }
            int animDraw = 0;
            if (movingConfiguration.movingLeft > 0) {
                if (jumping) {
                    curPose = poses.getTile(PlayerMultiTile.PlayerPose.JUMP_LEFT);
                    walkingFrames = 0;
                } else {
                    if (speedX < -5) {
                        curPose = null;
                        animDraw = 1;
                    }
                    else curPose = poses.getTile(PlayerMultiTile.PlayerPose.LEFT);
                    setToJump = false;
                    walkingFrames++;
                }
            } else if (movingConfiguration.movingRight > 0) {
                if (jumping) {
                    curPose = poses.getTile(PlayerMultiTile.PlayerPose.JUMP_RIGHT);
                    walkingFrames = 0;
                } else {
                    if (speedX > 5) {
                        curPose = null;
                        animDraw = 2;
                    }
                    else curPose = poses.getTile(PlayerMultiTile.PlayerPose.RIGHT);
                    setToJump = false;
                    walkingFrames++;
                }
            } else {
                graphicX = x;
                if (jumping) {
                    curPose = poses.getTile(PlayerMultiTile.PlayerPose.JUMP);
                } else {
                    curPose = poses.getTile(PlayerMultiTile.PlayerPose.FRONT);
                }
                walkingFrames = 0;
            }
            if (Math.abs(pSpeed) < 15 && Math.abs(speedX) > 2 && walkingFrames != 0 && walkingFrames % 10 == 0) {
                step.play(world.menu.soundVolume / 100.0f);
            }
            if (animDraw == 0 && (curPose == null || (setToJump && !jumping))) {
                //curPose = poses.getTile(PlayerMultiTile.PlayerPose.FRONT);
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
