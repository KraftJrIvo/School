package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.XmlReader;
import com.mygdx.schoolRPG.menus.GameMenu;
import com.mygdx.schoolRPG.tools.AnimationSequence;
import com.mygdx.schoolRPG.tools.CharacterMaker;
import com.mygdx.schoolRPG.tools.GlobalSequence;
import com.mygdx.schoolRPG.tools.Coords;
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
    public ArrayList<String> statesConditionFlags;
    public ArrayList<Boolean> statesConditionFlagVals;
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
    public ArrayList<String> statesFlags;
    public ArrayList<String> statesTeleportRooms;
    public ArrayList<Coords> statesTeleportCoords;
    public ArrayList<String> statesDialogs;
    public ArrayList<Boolean> statesFlagVals;
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
        entityX = entity.x % width;
        if (hIsY) {
            entityY = entity.h % height;
        } else {
            entityY = entity.y % height;
        }
        transfer = false;
        cellOffsetX = 0;
        cellOffsetY = 0;
    }

    public void invalidate() {
        cellOffsetX = (int)Math.floor(entityX / width);
        cellOffsetY = (int)Math.floor(entityY / height);
        if (cellOffsetX == 0 && cellOffsetY == 0) {
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

    public void invalidateAsObject(String worldDir, AssetManager assets, ArrayList<String> flagNames, ArrayList<Boolean> flags, Area area, int charId, GameMenu menu) {
        if (statesSwitchTimers.get(currentState) != -1 && System.currentTimeMillis() - startTime > statesSwitchTimers.get(currentState)) {
            activate(worldDir, assets, flagNames, flags, area, charId, menu, false, false);
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

    public void activate(String worldDir, AssetManager assets, ArrayList<String> flagNames, ArrayList<Boolean> flags, Area area, int charId, GameMenu menu, boolean playerActivated, boolean proximity) {
        if (!proximity && !statesSwitchables.get(currentState) && (flagNames.contains(statesConditionFlags.get(currentState))
                && statesConditionFlagVals.get(currentState) != flags.get(flagNames.indexOf(statesConditionFlags.get(currentState))))) return;
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
        updateSoundState(menu);
        updateEntityState(assets, worldDir);
        this.charId = charId;
        for (int i = 0; i < flagNames.size(); ++i) {
            if (flagNames.get(i).equals(statesFlags.get(currentState))) {
                if (statesFlagVals.get(currentState) == null) flags.set(i, !flags.get(i));
                else flags.set(i, statesFlagVals.get(currentState));
            }
        }
        updateHiddenPlayer(assets, worldDir);
        area.playerHidden = statesHidePlayer.get(currentState);
        startTime = System.currentTimeMillis();
        lastSpawned = 0;
        if (!statesCollidables.get(currentState)) {
            entity.collidable = false;
        } else {
            entity.collidable = true;
        }
        if (statesTeleportRooms.get(currentState) != null) {
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

    public void checkFlags(String worldDir, AssetManager assets, ArrayList<String> flagNames, ArrayList<Boolean> flags, Area area, GameMenu menu) {
        if ((flagNames.contains(statesConditionFlags.get(currentState)) && statesConditionFlagVals.get(currentState) == flags.get(flagNames.indexOf(statesConditionFlags.get(currentState))))) {
            activate(worldDir, assets, flagNames, flags, area, charId, menu, false, false);
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
        boolean res = (statesparticleSpawns.get(currentState).size() > 0) && (System.currentTimeMillis() - startTime - lastSpawned) > statesIntervals.get(currentState);
        if (res) {
            lastSpawned = System.currentTimeMillis() - startTime;
            return statesparticleSpawns.get(currentState);
        }
        return null;
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

    public void objectCheck(World world, AssetManager assets, int state, CharacterMaker characterMaker, int objectId) {
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
            statesConditionFlags = new ArrayList<String>();
            statesConditionFlagVals = new ArrayList<Boolean>();
            statesTexTypes = new ArrayList<String>();
            statesTex = new ArrayList<String>();
            names = new ArrayList<String>();
            statesFlags = new ArrayList<String>();
            statesDialogs = new ArrayList<String>();
            statesFlagVals = new ArrayList<Boolean>();
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
            statesTeleportRooms = new ArrayList<String>();
            statesTeleportCoords = new ArrayList<Coords>();

            for (int i= 0; i< nList.getLength(); ++i) {
                Node nNode = nList.item(i);
                Element eElement = (Element) nNode;
                statesSwitchables.add(Boolean.parseBoolean(eElement.getAttribute("switchable")));
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
                if (condition.length() > 0 && condition.charAt(0) == '!') {
                    condition = condition.substring(1);
                    statesConditionFlagVals.add(false);
                } else {
                    statesConditionFlagVals.add(true);
                }
                statesConditionFlags.add(condition);
                String tex = eElement.getAttribute("tex");
                statesTex.add(tex);
                String texType = eElement.getAttribute("texType");
                statesTexTypes.add(texType);
                statesFlags.add(eElement.getAttribute("flagName"));
                if (eElement.getAttribute("flagVal").equals("toggle")) {
                    statesFlagVals.add(null);
                } else {
                    statesFlagVals.add(Boolean.parseBoolean(eElement.getAttribute("flagVal")));
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
