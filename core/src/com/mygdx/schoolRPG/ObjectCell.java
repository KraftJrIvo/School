package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.XmlReader;
import com.mygdx.schoolRPG.menus.GameMenu;
import com.mygdx.schoolRPG.tools.*;
import javafx.util.Pair;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Kraft on 11.01.2016.
 */
enum ObjectType {
    NONE,
    PLAYER,
    SOLID,
    NONSOLID,
    OBSTACLE,
    PARTICLE,
    CHECKPOINT,
    LIQUID,
    NPC
}

public class ObjectCell {
    ObjectType type;
    float h;
    int id;
    Entity entity;
    float x, y;
    float width, height;
    float entityX, entityY;
    int cellOffsetX, cellOffsetY;
    boolean transfer;
    public boolean hIsY;
    private long startTime;
    private long lastSpawned;

    public int radius;
    public boolean isObject = false;
    public boolean isContainer = false;
    public int statesCount = 0;
    public int currentState = -1;
    public int offsetX = 0;
    public int offsetY = 0;
    public ArrayList<Boolean> statesSwitchables;
    public ArrayList<String> statesConditionVars;
    public ArrayList<String> statesTexTypes;
    public ArrayList<String> statesTex;
    public ArrayList<String> statesCharsTex;
    public ArrayList<ArrayList<TextureRegion>> statesHeads;
    public ArrayList<ArrayList<TextureRegion>> statesBodies;
    public ArrayList<ArrayList<TextureRegion>> statesHeadWears;
    public ArrayList<ArrayList<TextureRegion>> statesBodyWears;
    public ArrayList<ArrayList<Integer>> statesHeadWearsDirs;
    public ArrayList<ArrayList<Integer>> statesBodyWearsDirs;
    public ArrayList<ArrayList<Integer>> statesHeadXOffsets;
    public ArrayList<ArrayList<Integer>> statesHeadYOffsets;
    public ArrayList<ArrayList<Integer>> statesBodyXOffsets;
    public ArrayList<ArrayList<Integer>> statesBodyYOffsets;
    public ArrayList<String> statesVars;
    public ArrayList<ParticleProperties.ParticleSpawnProperties> statesJumpAsPrts;
    public ArrayList<String> statesTeleportWorlds;
    public ArrayList<String> statesTeleportRooms;
    public ArrayList<Coords> statesTeleportCoords;
    public ArrayList<String> statesDialogs;
    public ArrayList<String> statesVarVals;
    ArrayList<ArrayList<ParticleProperties.ParticleSpawnProperties>> statesparticleSpawns;
    public ArrayList<Integer> statesSwitchTimers;
    public ArrayList<Boolean> statesCollidables;
    public ArrayList<Integer> statesGotos;
    public ArrayList<Integer> statesProximityGotos;
    public ArrayList<Integer> statesProximityRadii;
    public ArrayList<Item> items;
    public ArrayList<Integer> statesIntervals;
    public ArrayList<Boolean> statesHidePlayer;
    public ArrayList<Boolean> statesDrawPlayer;
    public ArrayList<Integer> statesFPS;
    public ArrayList<Boolean> statesLooping;
    public ArrayList<Integer> statesFramesCount;
    public ArrayList<Sound> statesSwitchSounds;
    public ArrayList<Sound> statesSoundLoops;
    public ArrayList<Integer> statesLoopsVolumes;
    int zPath = 0;
    ArrayList<String> names;
    int charId;
    CharacterMaker characterMaker;
    boolean soundsAreStopped = false;
    long loopId = -1;
    String areaName;
    public boolean objectChecked = false;
    public int objectId = -1;
    ConditionParser parser;
    int forceNextState = -1;
    boolean jumpedThisState = false;
    boolean isJumping = false;
    boolean shouldJump = false;
    int jumpState = -1;
    public Particle jumpPrt = null;

    public ObjectCell(float width, float height, Entity entity, ObjectType type, int id, boolean hIsY, ArrayList<Item> items, Area area) {
        this.type = type;
        this.id = id;
        this.entity = entity;
        this.width = width;
        this.height = height;
        entityX = entity.x % width;
        entityY = entity.h % height;
        transfer = false;
        cellOffsetX = 0;
        cellOffsetY = 0;
        this.hIsY = hIsY;
        this.items = items;
        areaName = area.name;
    }

    public boolean isTransfer() {
        return transfer;
    }

    public void reset() {
        x+=cellOffsetX;
        y+=cellOffsetY;
        entityX = entity.x - (x-1) * width;
        if (hIsY) {
            entityY = entity.h - (y-1) * height;
        } else {
            entityY = entity.y - (y-1) * height;
        }
        transfer = false;
        cellOffsetX = 0;
        cellOffsetY = 0;
    }

    public void invalidate() {
        cellOffsetX = (int)Math.floor(entityX / width);
        cellOffsetY = (int)Math.floor(entityY / height);
        if (cellOffsetX == 0 && cellOffsetY == 0) {
            entityX = entity.x % width;
            /*if (hIsY) {
                entityY = entity.h % height;
            } else {
                entityY = entity.y % height;
            }*/
            entityX = entity.x - (x-1) * width;
            if (hIsY) {
                entityY = entity.h - (y-1) * height;
            } else {
                entityY = entity.y - (y-1) * height;
            }
        } else {
            transfer = true;
        }
        h = entity.h;
    }

    public void invalidateAsObject(String worldDir, AssetManager assets, ArrayList<String> varNames, ArrayList<Integer> vars, Area area, int charId, GameMenu menu) {
        if (statesSwitchTimers.get(currentState) != -1 && System.currentTimeMillis() - startTime > statesSwitchTimers.get(currentState)) {
            activate(worldDir, assets, varNames, vars, area, charId, menu, false, false);
        }
    }

    public void updateWears() {
        for (int i = 0; i < statesHeadWearsDirs.size(); ++i) {
            statesHeadWears.set(i, new ArrayList<TextureRegion>());
            statesBodyWears.set(i, new ArrayList<TextureRegion>());
            if (statesHeadWearsDirs.get(i) == null) continue;
            for (int j = 0; j < statesHeadWearsDirs.get(i).size(); ++j) {
                if (statesHeadWearsDirs.get(i).get(j) == null) continue;
                int headDir = statesHeadWearsDirs.get(i).get(j);
                if (headDir >= 0 && characterMaker.headWears.get(charId) != null) {
                    if (headDir < 3) {
                        statesHeadWears.get(i).add(characterMaker.headWears.get(charId).getFrame(headDir));
                    } else if (headDir == 3) {
                        characterMaker.headWears.get(charId).getFrame(2).flip(true, false);
                        statesHeadWears.get(i).add(new TextureRegion(characterMaker.headWears.get(charId).getFrame(2)));
                        characterMaker.headWears.get(charId).getFrame(2).flip(true, false);
                    }
                } else {
                    statesHeadWears.get(i).add(null);
                }
                int bodyDir = statesBodyWearsDirs.get(i).get(j);
                if (bodyDir >= 0 && characterMaker.bodyWears.get(charId) != null) {
                    if (bodyDir < 3) {
                        statesBodyWears.get(i).add(characterMaker.bodyWears.get(charId).getFrame(bodyDir));
                    } else if (bodyDir == 3) {
                        characterMaker.bodyWears.get(charId).getFrame(2).flip(true, false);
                        statesBodyWears.get(i).add(new TextureRegion(characterMaker.bodyWears.get(charId).getFrame(2)));
                        characterMaker.bodyWears.get(charId).getFrame(2).flip(true, false);
                    }
                } else {
                    statesBodyWears.get(i).add(null);
                }
            }
        }


        entity.headWears = statesHeadWears.get(currentState);
        entity.bodyWears = statesBodyWears.get(currentState);
    }

    public void updateEntityState(AssetManager assets, String worldDir) {
        if (statesTexTypes.get(currentState).equals("")) {
            entity.anim = null;
            entity.tex = null;
        } else if (statesTexTypes.get(currentState).equals("sprite")) {
            entity.anim = null;
            entity.tex = assets.get(worldDir + "/" + statesTex.get(currentState) + ".png", Texture.class);
        } else if (statesTexTypes.get(currentState).equals("anim")) {
            entity.tex = null;
            entity.anim = new AnimationSequence(assets, worldDir + "/anim/" + statesTex.get(currentState) + ".png", statesFPS.get(currentState), statesLooping.get(currentState), statesFramesCount.get(currentState));
        }
    }


    public void updateSoundState(GameMenu menu) {
        if (statesSoundLoops.get(currentState) != null) {
            statesSoundLoops.get(currentState).loop((statesLoopsVolumes.get(currentState)/100.0f) * (menu.soundVolume / 100.0f));
            soundsAreStopped = false;
        }
    }

    private ArrayList<Pair<String, Integer>> prepareSpecialVars(Area area) {
        ArrayList<Pair<String, Integer>> specialVarVals = new ArrayList<Pair<String, Integer>>();
        specialVarVals.add(new Pair<String, Integer>("x", (int)entity.x));
        specialVarVals.add(new Pair<String, Integer>("y",(int)entity.y));
        specialVarVals.add(new Pair<String, Integer>("z",(int)entity.z));
        specialVarVals.add(new Pair<String, Integer>("state",currentState));
        specialVarVals.add(new Pair<String, Integer>("px",(int)area.player.x));
        specialVarVals.add(new Pair<String, Integer>("py",(int)area.player.y));
        specialVarVals.add(new Pair<String, Integer>("pz",(int)area.player.z));
        return specialVarVals;
    }

    private boolean checkSpecialVar(Area area, String name, int val) {
        ArrayList<Pair<String, Integer>> specialVarVals = prepareSpecialVars(area);
        for (int i = 0; i < specialVarVals.size(); ++i) {
            Pair<String, Integer> specialVarVal = specialVarVals.get(i);
            if (specialVarVal.getKey().equals(name)) {
                if (name.equals("x")) {
                    entity.x = val;
                    entityX = entity.x - (x-1) * width;
                    return true;
                } else if (name.equals("y")) {
                    entity.y = val;
                    if (hIsY) {
                        entity.h = val;
                        entityY = entity.h - (y-1) * height;
                    } else {
                        entityY = entity.y - (y-1) * height;
                    }
                    return true;
                } else if (name.equals("z")) {
                    entity.z = val;
                    return true;
                } else if (name.equals("state")) {
                    forceNextState = val;
                    return true;
                }
            }
        }
        return false;
    }

    public void activate(String worldDir, AssetManager assets, ArrayList<String> varNames, ArrayList<Integer> vars, Area area, int charId, GameMenu menu, boolean playerActivated, boolean proximity) {
        if (!proximity && !statesSwitchables.get(currentState) && varNames.contains(statesConditionVars.get(currentState))) {
            if (!parser.parseCondition(statesConditionVars.get(currentState), prepareSpecialVars(area))) {
                return;
            }
        }
        if (statesSwitchSounds.get(currentState) != null && !menu.paused && playerActivated) {
            statesSwitchSounds.get(currentState).play(menu.soundVolume / 100.0f);
        }
        if (statesSoundLoops.get(currentState) != null) {
            statesSoundLoops.get(currentState).stop();
            loopId = -1;
        }
        if (statesDialogs.get(currentState) != null && !statesDialogs.get(currentState).equals("")) {
            menu.drawPause = false;
            menu.paused = true;
            menu.unpausable = false;
            area.worldObjectsHandler.currentDialog = new Dialog(worldDir + "/chars/0/dialog", statesDialogs.get(currentState) + ".xml", 0, false, area.worldObjectsHandler.NPCs, area.player, assets, worldDir + "/chars", menu.currentLanguage, area.world.menu);
        }
        boolean found = false;
        if (statesVars.get(currentState).length() > 0) {
            String[] multiVar = statesVars.get(currentState).split(",");
            String[] multiVal = null;
            if (statesVarVals.get(currentState) != null) {
                multiVal = statesVarVals.get(currentState).split(",");
            }
            for (int i = 0; i < multiVar.length; ++i) {
                String curVar = multiVar[i];
                int newVarVal;
                if (statesVarVals.get(currentState) == null) { //in case it's TOGGLE (i know)
                    if (varNames.contains(curVar)) {
                        int index = varNames.indexOf(curVar);
                        if (vars.get(index) == 0) newVarVal = 1;
                        else newVarVal = 0;
                        vars.set(index, newVarVal);
                        area.world.changedVarNames.add(varNames.get(index));
                    } else {
                        newVarVal = 1;
                        if (!checkSpecialVar(area, curVar, newVarVal)) {
                            varNames.add(curVar);
                            vars.add(newVarVal);
                            area.world.changedVarNames.add(curVar);
                        }
                    }
                } else {
                    newVarVal = parser.evalVal(multiVal[i], prepareSpecialVars(area));
                    if (varNames.contains(curVar)) {
                        int index = varNames.indexOf(curVar);
                        vars.set(index, newVarVal);
                        area.world.changedVarNames.add(varNames.get(index));
                    } else if (!checkSpecialVar(area, curVar, newVarVal)) {
                        varNames.add(curVar);
                        vars.add(newVarVal);
                        area.world.changedVarNames.add(curVar);
                    }
                }
            }
        }
        if (statesJumpAsPrts.get(currentState) != null) {
            shouldJump = true;
            jumpState = currentState;
        }
        if (!proximity) {
            if (statesGotos.get(currentState) == -1) {
                currentState++;
            } else {
                currentState = statesGotos.get(currentState);
            }
        } else {
            currentState = statesProximityGotos.get(currentState);
        }
        currentState = currentState % statesCount;
        if (forceNextState != -1) {
            currentState = forceNextState;
            forceNextState = -1;
        }
        jumpedThisState = false;
        jumpPrt = null;
        updateSoundState(menu);
        updateEntityState(assets, worldDir);
        this.charId = charId;
        updateHiddenPlayer(assets, worldDir);
        area.playerHidden = statesHidePlayer.get(currentState);
        startTime = System.currentTimeMillis();
        lastSpawned = 0;
        if (!statesCollidables.get(currentState)) {
            entity.collidable = false;
        } else {
            entity.collidable = true;
        }
        if (statesTeleportWorlds.get(currentState) != null) {
            if (statesTeleportRooms.get(currentState) != null) {
                area.world.changeWorld(statesTeleportWorlds.get(currentState), statesTeleportRooms.get(currentState), (int)statesTeleportCoords.get(currentState).x, (int)statesTeleportCoords.get(currentState).y);
            } else {
                area.world.changeWorld(statesTeleportWorlds.get(currentState), "", -1, -1);
            }
        }
        else if (statesTeleportRooms.get(currentState) != null) {
            Area toArea = area.world.map.getAreaByName(statesTeleportRooms.get(currentState));
            area.world.changeArea(false, toArea.x - area.world.curAreaX, toArea.y - area.world.curAreaY, toArea.z - area.world.curAreaZ, statesTeleportCoords.get(currentState));
        }
    }

    public boolean checkProximity(float x, float y) {
        if (statesProximityGotos.get(currentState) != -1) {
            float px = x;
            float py = y;
            float ox = entity.x + offsetX;
            float oy = entity.y+ offsetY;
            float dist = (float)Math.sqrt((px - ox) * (px - ox) + (py - oy) * (py - oy));
            if (statesProximityRadii.get(currentState) < 0) {
                return dist >= -statesProximityRadii.get(currentState);
            }
            return dist < statesProximityRadii.get(currentState);

        }
        return false;
    }

    public void checkVars(String worldDir, AssetManager assets, ArrayList<String> varNames, ArrayList<Integer> vars, Area area, GameMenu menu) {
        if (statesConditionVars.get(currentState).length() > 0 && parser.parseCondition(statesConditionVars.get(currentState), prepareSpecialVars(area))) {
            do {
                activate(worldDir, assets, varNames, vars, area, charId, menu, false, false);
                if (statesCount == 1) break;
            } while (parser.parseCondition(statesConditionVars.get(currentState), prepareSpecialVars(area)));
        }
        if (statesSoundLoops.get(currentState) != null) {
            if (menu.paused || !area.isCurrent) {
                statesSoundLoops.get(currentState).stop();
                soundsAreStopped = true;
                loopId = -1;
            } else {
                if (soundsAreStopped) {
                    statesSoundLoops.get(currentState).loop((statesLoopsVolumes.get(currentState)/100.0f) * (menu.soundVolume / 100.0f));
                } else {
                    statesSoundLoops.get(currentState).setVolume(0, (statesLoopsVolumes.get(currentState)/100.0f) * menu.soundVolume / 100.0f);
                }
                soundsAreStopped = false;
            }
            if (area.stopAllSounds) {
                statesSoundLoops.get(currentState).stop();
            }
        }
    }

    public void updateHiddenPlayer(AssetManager assets, String worldDir) {
        if (statesHidePlayer.get(currentState) && statesDrawPlayer.get(currentState)) {
            entity.heads = statesHeads.get(currentState);
            entity.bodies = statesBodies.get(currentState);
            updateWears();
            entity.headsOffX = statesHeadXOffsets.get(currentState);
            entity.headsOffY = statesHeadYOffsets.get(currentState);
            entity.bodiesOffX = statesBodyXOffsets.get(currentState);
            entity.bodiesOffY = statesBodyYOffsets.get(currentState);
            if (statesTexTypes.get(currentState).equals("anim")) {
                entity.charAnim = new AnimationSequence(assets, assets.get(worldDir + "/objects/util/" + statesCharsTex.get(currentState) + ".png", Texture.class), statesFPS.get(currentState), statesLooping.get(currentState), statesFramesCount.get(currentState));
            } else {
                entity.charTex = assets.get( worldDir + "/objects/util/" + statesCharsTex.get(currentState) + ".png", Texture.class);
            }
            entity.drawChar = true;
        } else if (entity.heads != null) {
            entity.drawChar = false;
            entity.heads = null;
            entity.bodies = null;
            entity.headWears = null;
            entity.bodyWears = null;
            entity.headsOffX = null;
            entity.headsOffY = null;
            entity.bodiesOffX = null;
            entity.bodiesOffY = null;
            entity.charAnim = null;
            entity.charTex = null;
        }
    }

    public boolean isSwitchable() {
        return statesSwitchables.get(currentState);
    }

    public ArrayList<ParticleProperties.ParticleSpawnProperties> checkParticleEmission() {
        ArrayList<ParticleProperties.ParticleSpawnProperties> allPrts = new ArrayList<ParticleProperties.ParticleSpawnProperties>();
        boolean res = (statesparticleSpawns.get(currentState).size() > 0) && (System.currentTimeMillis() - startTime - lastSpawned) > statesIntervals.get(currentState);
        if (res) {
            lastSpawned = System.currentTimeMillis() - startTime;
            allPrts.addAll(statesparticleSpawns.get(currentState));
        }
        if (shouldJump && statesJumpAsPrts.get(jumpState) != null && !jumpedThisState && !isJumping) {
            allPrts.add(statesJumpAsPrts.get(jumpState));
            jumpedThisState = true;
            isJumping = true;
            entity.draw = false;
            shouldJump = false;
        }
        if (isJumping && jumpPrt != null && jumpPrt.floor) {
            jumpPrt.alpha = 0;
            isJumping = false;
            entity.draw = true;
            entity.x = jumpPrt.x;
            entity.y = jumpPrt.y;
            if (entity.tex != null) {
                entity.x -= entity.tex.getWidth()/2.0f;
            } else if (entity.texR != null) {
                entity.x -= entity.texR.getRegionWidth()/2.0f;
            }else if (entity.anim != null) {
                entity.x -= entity.anim.getFirstFrame().getRegionWidth()/2.0f;
            }
            entity.y += entity.floorHeight/2.0f;
            entityX = entity.x - (x-1) * width;
            if (hIsY) {
                entity.h = jumpPrt.y;
                entityY = entity.h - (y-1) * height;
            } else {
                entityY = entity.y - (y-1) * height;
            }

        }
        if (allPrts.isEmpty())
            return null;
        return allPrts;
    }

    private TextureRegion getDir(ArrayList<GlobalSequence> list, String dir) {
        if (charId >= list.size() || list.get(charId) == null) return null;
        TextureRegion textureRegion;
        if (dir.equals("front")) {
            textureRegion = list.get(charId).getFrame(0);
        } else if (dir.equals("back")) {
            textureRegion = list.get(charId).getFrame(1);
        } else if (dir.equals("left")) {
            list.get(charId).getFrame(2).flip(true, false);
            textureRegion = new TextureRegion(list.get(0).getFrame(2));
            list.get(charId).getFrame(2).flip(true, false);
        } else {
            textureRegion = list.get(charId).getFrame(2);
        }
        return textureRegion;
    }

    private int getWearDir(String dir) {
        if (dir.equals("front")) {
            return 0;
        } else if (dir.equals("back")) {
            return 1;
        } else if (dir.equals("left")) {
            return 3;
        } else {
            return 2;
        }
    }

    private int getWearRegionFromDir(String dir, boolean head) {
        if (head) {
            return getWearDir(dir);
        }
        return getWearDir(dir);
    }

    private TextureRegion getRegionFromDir(CharacterMaker characterMaker, String dir, boolean head) {
        TextureRegion textureRegion;
        if (head) {
            textureRegion = getDir(characterMaker.heads, dir);
        } else {
            textureRegion = getDir(characterMaker.bodies, dir);
        }
        return textureRegion;
    }

    public void objectCheck(World world, AssetManager assets, int state, CharacterMaker characterMaker, int objectId, ConditionParser parser) {
        objectChecked = true;
        if (entity.texPath == null) return;
        //String baseTex = entity.texPath.substring(entity.texPath.lastIndexOf("/") + 1, entity.texPath.length() - 4);;

        FileHandle charDir =  Gdx.files.internal(world.worldDir + "/objects");
        FileHandle objectXML = null;
        for (FileHandle entry: charDir.list()) {
            if (entry.isDirectory()) continue;
            int id = Integer.parseInt(entry.nameWithoutExtension().split("_")[0]);
            if (id == objectId) {
                isObject = true;
                objectXML = entry;
                this.objectId = id;
            }
        }
        if (isObject) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = null;
            try {
                dBuilder = dbFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            org.w3c.dom.Document doc = null;
            try {
                doc = dBuilder.parse(objectXML.read());
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            doc.getDocumentElement().normalize();
            statesCount = Integer.parseInt(doc.getDocumentElement().getAttribute("statesCount"));
            currentState = state;
            if (currentState == -1) {
                currentState = Integer.parseInt(doc.getDocumentElement().getAttribute("startState"));
            }
            radius = Integer.parseInt(doc.getDocumentElement().getAttribute("radius"));
            offsetX = Integer.parseInt(doc.getDocumentElement().getAttribute("offsetX"));
            offsetY = Integer.parseInt(doc.getDocumentElement().getAttribute("offsetY"));
            isContainer = Boolean.parseBoolean(doc.getDocumentElement().getAttribute("container"));
            NodeList nList = doc.getElementsByTagName("state");
            statesSwitchables = new ArrayList<Boolean>();
            statesConditionVars = new ArrayList<String>();
            statesTexTypes = new ArrayList<String>();
            statesTex = new ArrayList<String>();
            names = new ArrayList<String>();
            statesVars = new ArrayList<String>();
            statesJumpAsPrts = new ArrayList<ParticleProperties.ParticleSpawnProperties>();
            statesDialogs = new ArrayList<String>();
            statesVarVals = new ArrayList<String>();
            statesparticleSpawns = new ArrayList<ArrayList<ParticleProperties.ParticleSpawnProperties>>();
            statesIntervals = new ArrayList<Integer>();
            statesHidePlayer = new ArrayList<Boolean>();
            statesFPS = new ArrayList<Integer>();
            statesLooping = new ArrayList<Boolean>();
            statesFramesCount = new ArrayList<Integer>();
            statesCharsTex = new ArrayList<String>();
            statesDrawPlayer = new ArrayList<Boolean>();
            statesHeads = new ArrayList<ArrayList<TextureRegion>>();
            statesBodies = new ArrayList<ArrayList<TextureRegion>>();
            statesHeadWears = new ArrayList<ArrayList<TextureRegion>>();
            statesBodyWears = new ArrayList<ArrayList<TextureRegion>>();
            statesHeadWearsDirs = new ArrayList<ArrayList<Integer>>();
            statesBodyWearsDirs = new ArrayList<ArrayList<Integer>>();
            statesHeadXOffsets = new ArrayList<ArrayList<Integer>>();
            statesHeadYOffsets = new ArrayList<ArrayList<Integer>>();
            statesBodyXOffsets = new ArrayList<ArrayList<Integer>>();
            statesBodyYOffsets = new ArrayList<ArrayList<Integer>>();
            statesSwitchSounds = new ArrayList<Sound>();
            statesSoundLoops = new ArrayList<Sound>();
            statesLoopsVolumes = new ArrayList<Integer>();
            statesSwitchTimers = new ArrayList<Integer>();
            statesCollidables = new ArrayList<Boolean>();
            statesGotos = new ArrayList<Integer>();
            statesProximityGotos = new ArrayList<Integer>();
            statesProximityRadii = new ArrayList<Integer>();
            statesTeleportWorlds = new ArrayList<String>();
            statesTeleportRooms = new ArrayList<String>();
            statesTeleportCoords = new ArrayList<Coords>();

            for (int i= 0; i< nList.getLength(); ++i) {
                Node nNode = nList.item(i);
                Element eElement = (Element) nNode;
                if (!eElement.getAttribute("switchable").equals("")) {
                    statesSwitchables.add(Boolean.parseBoolean(eElement.getAttribute("switchable")));
                } else {
                    if (i == 0) {
                        statesSwitchables.add(false);
                    } else {
                        statesSwitchables.add(statesSwitchables.get(i-1));
                    }
                }
                String condition = eElement.getAttribute("switchCondition");
                if (!eElement.getAttribute("zLayerChange").equals("")) {
                    zPath = Integer.parseInt(eElement.getAttribute("zLayerChange"));
                    RoomNode room = world.map.getRoomByName(areaName);
                    Area area = world.map.getAreaByName(areaName);
                    if (zPath == -1) {
                        room.addExit(new Exit(room, ExitDirection.DOWN, (int)entity.x, (int)entity.y, 1), area, true);
                        //world.map.connectExits();
                    } else if (zPath == 1) {
                        room.addExit(new Exit(room, ExitDirection.UP, (int)entity.x, (int)entity.y, 1), area, true);
                        //world.map.connectExits();
                    }
                }
                statesConditionVars.add(condition);
                String tex = eElement.getAttribute("tex");
                statesTex.add(tex);
                String texType = eElement.getAttribute("texType");
                statesTexTypes.add(texType);
                statesVars.add(eElement.getAttribute("varName"));
                if (eElement.getAttribute("varVal").equals("toggle") || eElement.getAttribute("varVal").equals("")) {
                    statesVarVals.add(null);
                } else {
                    statesVarVals.add(eElement.getAttribute("varVal"));
                }
                if (eElement.getAttribute("emitInterval").equals("")) {
                    statesIntervals.add(-1);
                } else {
                    statesIntervals.add(Integer.parseInt(eElement.getAttribute("emitInterval")));
                }
                statesparticleSpawns.add(new ArrayList<ParticleProperties.ParticleSpawnProperties>());
                NodeList childParticlesSpawns = eElement.getElementsByTagName("particle");
                for (int j = 0; j < childParticlesSpawns.getLength(); ++j) {
                    Element spawnParams = (Element)childParticlesSpawns.item(j);
                    statesparticleSpawns.get(statesparticleSpawns.size()-1).add(new ParticleProperties().new ParticleSpawnProperties(
                            assets,
                            world.worldDir + "/sounds/" + spawnParams.getAttribute("spawnSound"),
                            spawnParams.getAttribute("name"),
                            Integer.parseInt(spawnParams.getAttribute("spawnX")),
                            Integer.parseInt(spawnParams.getAttribute("spawnY")),
                            Integer.parseInt(spawnParams.getAttribute("spawnZ")),
                            Float.parseFloat(spawnParams.getAttribute("spawnDir")),
                            Float.parseFloat(spawnParams.getAttribute("spawnSpeed")),
                            Float.parseFloat(spawnParams.getAttribute("spawnImpulse")),
                            Float.parseFloat(spawnParams.getAttribute("dirSpread")),
                            Float.parseFloat(spawnParams.getAttribute("speedSpread")),
                            Float.parseFloat(spawnParams.getAttribute("impulseSpread"))
                    ));
                }
                String switchSoundPath = eElement.getAttribute("switchSound");
                if (!switchSoundPath.equals("")) {
                    statesSwitchSounds.add(assets.get(world.worldDir + "/sounds/" + switchSoundPath, Sound.class));
                } else {
                    statesSwitchSounds.add(null);
                }
                NodeList jumpAsPrtNode = eElement.getElementsByTagName("jumpAsPrt");
                if (jumpAsPrtNode.getLength() > 0) {
                    Element spawnParams = (Element)jumpAsPrtNode.item(0);
                    statesJumpAsPrts.add(new ParticleProperties().new ParticleSpawnProperties(
                            assets,
                            world.worldDir + "/sounds/" + spawnParams.getAttribute("spawnSound"),
                            spawnParams.getAttribute("name"),
                            Integer.parseInt(spawnParams.getAttribute("spawnX")),
                            Integer.parseInt(spawnParams.getAttribute("spawnY")),
                            Integer.parseInt(spawnParams.getAttribute("spawnZ")),
                            Float.parseFloat(spawnParams.getAttribute("spawnDir")),
                            Float.parseFloat(spawnParams.getAttribute("spawnSpeed")),
                            Float.parseFloat(spawnParams.getAttribute("spawnImpulse")),
                            Float.parseFloat(spawnParams.getAttribute("dirSpread")),
                            Float.parseFloat(spawnParams.getAttribute("speedSpread")),
                            Float.parseFloat(spawnParams.getAttribute("impulseSpread"))
                    ));
                } else {
                    statesJumpAsPrts.add(null);
                }
                if (!eElement.getAttribute("switchTimer").equals("")) {
                    statesSwitchTimers.add(Integer.parseInt(eElement.getAttribute("switchTimer")));
                } else {
                    statesSwitchTimers.add(-1);
                }
                if (!eElement.getAttribute("collidable").equals("")) {
                    statesCollidables.add(Boolean.parseBoolean(eElement.getAttribute("collidable")));
                } else {
                    statesCollidables.add(true);
                }
                if (!eElement.getAttribute("goto").equals("")) {
                    statesGotos.add(Integer.parseInt(eElement.getAttribute("goto")));
                } else {
                    statesGotos.add(-1);
                }
                if (!eElement.getAttribute("proximityGoto").equals("")) {
                    statesProximityGotos.add(Integer.parseInt(eElement.getAttribute("proximityGoto")));
                } else {
                    statesProximityGotos.add(-1);
                }
                if (!eElement.getAttribute("proximityRadius").equals("")) {
                    statesProximityRadii.add(Integer.parseInt(eElement.getAttribute("proximityRadius")));
                } else {
                    statesProximityRadii.add(radius);
                }
                if (!eElement.getAttribute("teleportWorld").equals("")) {
                    statesTeleportWorlds.add(eElement.getAttribute("teleportWorld"));
                } else {
                    statesTeleportWorlds.add(null);
                }
                if (!eElement.getAttribute("teleportRoom").equals("")) {
                    statesTeleportRooms.add(eElement.getAttribute("teleportRoom"));
                    statesTeleportCoords.add(new Coords(Integer.parseInt(eElement.getAttribute("teleportX")), Integer.parseInt(eElement.getAttribute("teleportY"))));
                } else {
                    statesTeleportRooms.add(null);
                    statesTeleportCoords.add(null);
                }
                String soundLoopPath = eElement.getAttribute("loopSound");
                if (!soundLoopPath.equals("")) {
                    statesSoundLoops.add(assets.get(world.worldDir + "/sounds/" + eElement.getAttribute("loopSound"), Sound.class));
                    statesLoopsVolumes.add(Integer.parseInt(eElement.getAttribute("loopVolume")));
                } else {
                    statesSoundLoops.add(null);
                    statesLoopsVolumes.add(0);
                }
                boolean hidePlayer = false;
                if (!eElement.getAttribute("hidePlayer").equals("")) {
                    hidePlayer = Boolean.parseBoolean(eElement.getAttribute("hidePlayer"));
                }
                statesHidePlayer.add(hidePlayer);
                boolean drawHiddenPlayer = false;
                if (hidePlayer) {
                    drawHiddenPlayer = Boolean.parseBoolean(eElement.getAttribute("drawPlayer"));
                }
                statesDrawPlayer.add(drawHiddenPlayer);
                if (hidePlayer && drawHiddenPlayer) {
                    this.characterMaker = characterMaker;
                    statesCharsTex.add(tex + "_char");

                    for (int j =0; j < statesCount; ++j) {
                        statesHeads.add(new ArrayList<TextureRegion>());
                        statesBodies.add(new ArrayList<TextureRegion>());
                        statesHeadWears.add(new ArrayList<TextureRegion>());
                        statesBodyWears.add(new ArrayList<TextureRegion>());
                        statesHeadXOffsets.add(new ArrayList<Integer>());
                        statesHeadYOffsets.add(new ArrayList<Integer>());
                        statesBodyXOffsets.add(new ArrayList<Integer>());
                        statesBodyYOffsets.add(new ArrayList<Integer>());
                        statesHeadWearsDirs.add(new ArrayList<Integer>());
                        statesBodyWearsDirs.add(new ArrayList<Integer>());
                    }

                    if (texType.equals("anim")) {
                        NodeList nList2 = doc.getElementsByTagName("frames");
                        Element eElement3 = (Element) nList2.item(0);
                        if (eElement3.getAttribute("id").equals(""+i)) {
                            NodeList nList3 = doc.getElementsByTagName("frame");
                            for (int j = 0; j < nList3.getLength(); ++j) {
                                Node nNode2 = nList3.item(j);
                                Element eElement2 = (Element) nNode2;
                                getCharDrawInfoFromElement(characterMaker, eElement2, i);
                            }
                        }
                    } else {
                        getCharDrawInfoFromElement(characterMaker, eElement, i);
                    }
                } else {
                    statesCharsTex.add(null);
                    statesHeads.add(null);
                    statesBodies.add(null);
                    statesHeadWears.add(null);
                    statesBodyWears.add(null);
                    statesHeadXOffsets.add(null);
                    statesHeadYOffsets.add(null);
                    statesBodyXOffsets.add(null);
                    statesBodyYOffsets.add(null);
                    statesHeadWearsDirs.add(null);
                    statesBodyWearsDirs.add(null);
                }
                if (statesTexTypes.get(i).equals("anim")) {
                    statesFPS.add(Integer.parseInt(eElement.getAttribute("fps")));
                    statesLooping.add(Boolean.parseBoolean(eElement.getAttribute("looping")));
                    statesFramesCount.add(Integer.parseInt(eElement.getAttribute("framesCount")));
                } else {
                    statesFPS.add(0);
                    statesLooping.add(false);
                    statesFramesCount.add(1);
                }
                statesDialogs.add(eElement.getAttribute("initDialog"));
            }

            this.parser = parser;

            if (isContainer) {
                nList = doc.getElementsByTagName("eng");
                Node nNode = nList.item(0);
                Element eElement = (Element) nNode;
                names.add(eElement.getAttribute("name"));

                nList = doc.getElementsByTagName("rus");
                nNode = nList.item(0);
                eElement = (Element) nNode;
                names.add(eElement.getAttribute("name"));
                if (items == null) {
                    items = new ArrayList<Item>();
                    nList = doc.getElementsByTagName("item");
                    for (int i= 0; i< nList.getLength(); ++i) {
                        nNode = nList.item(i);
                        eElement = (Element) nNode;
                        String name = eElement.getAttribute("name");
                        int stack = Integer.parseInt(eElement.getAttribute("stack"));
                        Item item = new Item(assets, world.folderPath, name);
                        item.stack = stack;
                        items.add(item);
                    }
                }
            }
            if (statesTexTypes.get(currentState) == "sprite") {
                entity.anim = null;
                entity.tex = assets.get(world.worldDir + "/" + statesTex.get(currentState) + ".png", Texture.class);
            } else if (statesTexTypes.get(currentState) == "anim") {
                entity.tex = null;
                entity.anim = new AnimationSequence(assets, world.worldDir + "/anim/" + statesTex.get(currentState) + ".png", statesFPS.get(currentState), statesLooping.get(currentState), statesFramesCount.get(currentState));
            }
            startTime = System.currentTimeMillis();
            lastSpawned = 0;
        }
    }

    private void getCharDrawInfoFromElement(CharacterMaker characterMaker, Element eElement, int i) {
        String headDir = eElement.getAttribute("head");
        String bodyDir = eElement.getAttribute("body");
        statesHeads.get(i).add(getRegionFromDir(characterMaker, headDir, true));
        statesBodies.get(i).add(getRegionFromDir(characterMaker, bodyDir, false));
        statesHeadWearsDirs.get(i).add(getWearRegionFromDir(headDir, true));
        statesBodyWearsDirs.get(i).add(getWearRegionFromDir(bodyDir, false));
        statesHeadXOffsets.get(i).add(Integer.parseInt(eElement.getAttribute("headX")));
        statesHeadYOffsets.get(i).add(Integer.parseInt(eElement.getAttribute("headY")));
        statesBodyXOffsets.get(i).add(Integer.parseInt(eElement.getAttribute("bodyX")));
        statesBodyYOffsets.get(i).add(Integer.parseInt(eElement.getAttribute("bodyY")));
    }
}
