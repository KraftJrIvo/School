package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.schoolRPG.menus.GameMenu;
import com.mygdx.schoolRPG.tools.AnimationSequence;
import com.mygdx.schoolRPG.tools.CharacterMaker;
import com.mygdx.schoolRPG.tools.IntCoords;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class World{
    public static final int SCREEN_HEIGHT = 720;
    public static final int SCREEN_WIDTH = 1280;
    ArrayList<Area> areas;
    ArrayList<ArrayList<ArrayList<Integer>>> areaIds;
    String folderPath;
    ArrayList<Pixmap> maps;
    File tlw;
    FileInputStream fis;
    public boolean initialised = false, loaded = false, areasCreated = false;
    public FileHandle worldDir = null;
    int curAreaX = 0, curAreaY = 0, curAreaZ = 0, oldAreaX = 0, oldAreaY = 0, oldAreaZ = 0;
    float areaTransitionX = 0, areaTransitionY = 0, areaTransitionZ = 0;
    String name;
    boolean platformMode = false;
    int width, height, areaWidth, areaHeight, tileWidth, tileHeight;
    CharacterMaker characterMaker;
    ArrayList<Boolean> flags;
    ArrayList<String> flagNames;
    ArrayList<Texture> sprites;
    ArrayList<BlockMultiTile> tiles;
    ArrayList<AnimationSequence> animations;
    ArrayList<String> names;
    ArrayList<String> newNames;
    ArrayList<String> roomNames;
    ArrayList<Integer> tileTypes;
    ArrayList<Integer> tileIndices;
    ArrayList<String> stepSounds;
    ArrayList<String> areasAmbients;
    ArrayList<NPC> npcs;
    ArrayList<Integer> npcsAreas;
    ArrayList<ObjectCell> objects;
    ArrayList<HittableEntity> movables;
    ArrayList<Integer> movablesAreas;
    ArrayList<Entity> itemsOnFloor;
    ArrayList<Integer> itemsOnFloorAreas;
    ArrayList<ParticleProperties> particles;
    int spritesCount = 0, tilesetsCount = 0;
    Texture bg;
    AssetManager assets;
    int firtsAreaWidth;
    int firtsAreaHeight;
    ShapeRenderer shapeRenderer;
    int animsLoaded = 0;
    GameMenu menu;
    public Sound currentSound;
    long currentSoundId = -1;
    String currentSoundPath;
    boolean startedAmbient = false;
    boolean startedChanging = false;
    public int save;
    boolean loadedState = false;
    RoomsMap map;

    public World(GameMenu menu, String folderPath, int size, int startingAreaX, int startingAreaY, int startingAreaZ, int save) {
        this.menu = menu;
        areas = new ArrayList<Area>();
        this.folderPath = folderPath;
        maps = new ArrayList<Pixmap>();
        areaIds = new ArrayList<ArrayList<ArrayList<Integer>>>(size);
        for (int i = 0; i < size; i++) {
            areaIds.add(new ArrayList<ArrayList<Integer>>(size));
            for (int t = 0; t < size; t++) {
                areaIds.get(i).add(new ArrayList<Integer>(size));
                for (int k = 0; k < size; k++) {
                    areaIds.get(i).get(t).add(-1);
                }
            }
        }
        curAreaX = startingAreaX;
        curAreaY = startingAreaY;
        curAreaZ = startingAreaZ;
        this.save = save;
    }

    public World(GameMenu menu, String worldPath, int save) {
        this.menu = menu;
        areas = new ArrayList<Area>();
        folderPath = worldPath;
        tlw = new File(worldPath+"/world1.tlw");
        this.save = save;
    }

    public void updateTiles() {
        /*ArrayList<String> unusedNames = new ArrayList<String>();
        ArrayList<Integer> unusedTypes = new ArrayList<Integer>();
        ArrayList<Integer> unusedIndices = new ArrayList<Integer>();
        for (int i = 0; i < names.size(); ++i) {
            if (!newNames.contains(names.get(i))) {
                unusedNames.add(names.get(i));
                unusedTypes.add(tileTypes.get(i));
                unusedIndices.add(tileIndices.get(i));
            }
        }*/
        ArrayList<String> preNames = new ArrayList<String>(names);
        ArrayList<Integer> preTileIndices = new ArrayList<Integer>(tileIndices);
        ArrayList<Integer> preTileTypes = new ArrayList<Integer>(tileTypes);
        for (int i = 0; i < newNames.size(); ++i) {
            int id = names.indexOf(newNames.get(i));
            if (id != -1) {
                preTileTypes.set(i, tileTypes.get(id));
                preTileIndices.set(i, tileIndices.get(id));
                preNames.set(i, newNames.get(i));
            } else {
                preTileTypes.add(i, -1);
                preTileIndices.add(i, -1);
                preNames.add(i, "");
            }
        }
        /*for (int i = 0; i < unusedNames.size(); ++i) {
            tileTypes.set(newNames.size() + i, unusedTypes.get(i));
            tileIndices.set(newNames.size() + i, unusedIndices.get(i));
            preNames.set(newNames.size() + i, unusedNames.get(i));
        }*/
        /*ArrayList<String> preNames = new ArrayList<String>(names);
        for (int i = 0; i < newNames.size(); ++i) {
            int id = names.indexOf(newNames.get(i));
            if (id >= 0 && id != i) {
                int type = tileTypes.get(i);
                int idx = tileIndices.get(i);
                String name = preNames.get(i);
                tileTypes.set(i, tileTypes.get(id));
                tileIndices.set(i, tileIndices.get(id));
                preNames.set(i, preNames.get(id));
                tileTypes.set(id, type);
                tileIndices.set(id, idx);
                preNames.set(id, name);
            }
        }*/
        tileIndices = preTileIndices;
        tileTypes = preTileTypes;
        names = preNames;
    }

    public static byte [] float2ByteArray (float value)
    {
        return ByteBuffer.allocate(4).putFloat(value).array();
    }

    public static float toFloat(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getFloat();
    }

    private void saveState() {
        File savesDir = new File(worldDir + "/saves");
        if (!savesDir.exists()) {
            savesDir.mkdir();
        }
        File currentSave = new File(worldDir + "/saves/state" + save);
        try {
            currentSave.createNewFile();
            FileOutputStream saveFile = new FileOutputStream(currentSave, false);

            for (int i =0; i < flags.size(); ++i) {
                if (flags.get(i)) {
                    saveFile.write(1);
                } else {
                    saveFile.write(0);
                }
            }
            saveFile.write(curAreaX);
            saveFile.write(curAreaY);
            saveFile.write(curAreaZ);
            Area curArea = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ));
            int activeCheckpoint = -1;
            for (int i =0; i < curArea.worldObjectsHandler.checkPoints.size(); ++i) {
                if (curArea.worldObjectsHandler.checkPoints.get(i).on) {
                    activeCheckpoint = i;
                }
            }
            saveFile.write(activeCheckpoint);
            if (activeCheckpoint == -1) {
                saveFile.write(curArea.lastSpawnTileX);
                saveFile.write(curArea.lastSpawnTileY);
            }
            Player player = curArea.player;
            saveFile.write(float2ByteArray(player.hitBox.x));
            saveFile.write(float2ByteArray(player.hitBox.y));
            if (curArea.playerHidden) {
                saveFile.write(1);
            } else {
                saveFile.write(0);
            }
            if (curArea.worldObjectsHandler.activeObject != null) {
                saveFile.write(curArea.worldObjectsHandler.objects.indexOf(curArea.worldObjectsHandler.activeObject) + 1);
            } else {
                saveFile.write(0);
            }
            saveFile.write(player.inventory.size());
            int headWear = -1;
            int bodyWear = -1;
            int handsObject = -1;
            for (int i =0; i < player.inventory.size(); ++i) {
                saveFile.write(player.inventory.get(i).fileName.length());
                saveFile.write(player.inventory.get(i).fileName.getBytes());
                saveFile.write(player.inventory.get(i).stack);
                if (player.inventory.get(i).sides == player.headWear) {
                    headWear = i;
                } else if (player.inventory.get(i).sides == player.bodyWear) {
                    bodyWear = i;
                } else if (player.inventory.get(i).sides == player.objectInHands) {
                    handsObject = i;
                }
            }
            saveFile.write(headWear + 1);
            saveFile.write(bodyWear + 1);
            saveFile.write(handsObject + 1);
            for (int i =0; i < npcs.size(); ++i) {
                for (int j =0; j < npcs.get(i).flags.size(); ++j) {
                    if (npcs.get(i).flags.get(j)) {
                        saveFile.write(1);
                    } else {
                        saveFile.write(0);
                    }
                }
                saveFile.write(npcsAreas.get(i));
                saveFile.write(float2ByteArray(npcs.get(i).hitBox.x));
                saveFile.write(float2ByteArray(npcs.get(i).hitBox.y));
                saveFile.write(npcs.get(i).inventory.size());
                headWear = -1;
                bodyWear = -1;
                handsObject = -1;
                for (int j =0; j < npcs.get(i).inventory.size(); ++j) {
                    saveFile.write(npcs.get(i).inventory.get(j).fileName.length());
                    saveFile.write(npcs.get(i).inventory.get(j).fileName.getBytes());
                    saveFile.write(npcs.get(i).inventory.get(j).stack);
                    if (npcs.get(i).inventory.get(j).sides == player.headWear) {
                        headWear = j;
                    } else if (npcs.get(i).inventory.get(j).sides == npcs.get(i).bodyWear) {
                        bodyWear = j;
                    } else if (npcs.get(i).inventory.get(j).sides == npcs.get(i).objectInHands) {
                        handsObject = j;
                    }
                }
                saveFile.write(headWear + 1);
                saveFile.write(bodyWear + 1);
                saveFile.write(handsObject + 1);
            }

            for (int i =0; i < movables.size(); ++i) {
                saveFile.write(movablesAreas.get(i));
                saveFile.write(float2ByteArray(movables.get(i).hitBox.x));
                saveFile.write(float2ByteArray(movables.get(i).hitBox.y));
            }

            for (int i =0; i < objects.size(); ++i) {
                saveFile.write(objects.get(i).currentState);
                if (objects.get(i).entity.drawChar) {
                    saveFile.write(1);
                } else {
                    saveFile.write(0);
                }
                if (objects.get(i).items == null) {
                    saveFile.write(0);
                } else {
                    saveFile.write(objects.get(i).items.size());
                    for (int j =0; j < objects.get(i).items.size(); ++j) {
                        saveFile.write(objects.get(i).items.get(j).fileName.length());
                        saveFile.write(objects.get(i).items.get(j).fileName.getBytes());
                        saveFile.write(objects.get(i).items.get(j).stack);
                    }
                }
            }

            saveFile.write(itemsOnFloor.size());
            for (int i =0; i < itemsOnFloor.size(); ++i) {
                saveFile.write(itemsOnFloorAreas.get(i));
                saveFile.write(itemsOnFloor.get(i).containingItem.fileName.length());
                saveFile.write(itemsOnFloor.get(i).containingItem.fileName.getBytes());
                saveFile.write(itemsOnFloor.get(i).containingItem.stack);
                saveFile.write(float2ByteArray(itemsOnFloor.get(i).x));
                saveFile.write(float2ByteArray(itemsOnFloor.get(i).y));
            }

            Dialog dialog = curArea.worldObjectsHandler.currentDialog;
            if (dialog != null) {
                saveFile.write(dialog.fileName.length());
                saveFile.write(dialog.fileName.getBytes());
                saveFile.write(dialog.mainCharId);
                saveFile.write(dialog.charPath.length());
                saveFile.write(dialog.charPath.getBytes());
                saveFile.write(Math.abs(dialog.currentSpeechId));
            } else {
                saveFile.write(0);
            }

            saveFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadState() {
        File currentSave = new File(worldDir + "/saves/state" + save);
        try {
            FileInputStream saveFile = new FileInputStream(currentSave);
            for (int i =0; i < flags.size(); ++i) {
                int flag = saveFile.read();
                if (flag == 1) {
                    flags.set(i, true);
                } else {
                   flags.set(i, false);
                }
            }
            curAreaX = saveFile.read();
            curAreaY = saveFile.read();
            curAreaZ = saveFile.read();
            Area curArea = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ));
            int activeCheckpoint = saveFile.read();
            if (activeCheckpoint < 255) {
                curArea.worldObjectsHandler.saveOnCheckPoint(curArea.worldObjectsHandler.checkPoints.get(activeCheckpoint));
            } else {
                curArea.worldObjectsHandler.resetCheckPoints();
                curArea.lastSpawnTileX = saveFile.read();
                curArea.lastSpawnTileY = saveFile.read();
            }
            byte[] flo = {0,0,0,0};
            saveFile.read(flo);
            Player player = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player;
            float playerX = toFloat(flo);
            saveFile.read(flo);
            float playerY = toFloat(flo);
            int hidden = saveFile.read();
            int activeObjectId = saveFile.read();
            int itemsCount = saveFile.read();
            player.inventory.clear();
            player.movingConfiguration = new MovingConfiguration();
            for (int i =0; i < itemsCount; ++i) {
                int bytesCount = saveFile.read();
                byte[] bitfield = new byte[bytesCount];
                saveFile.read(bitfield);
                String str = new String(bitfield, StandardCharsets.UTF_8);
                Item item = new Item(assets, worldDir.path(), str);
                item.stack = saveFile.read();
                player.inventory.add(item);
            }
            int headWear = saveFile.read();
            if (headWear > 0) {
                player.headWear = player.inventory.get(headWear-1).sides;
            } else {
                player.headWear = null;
            }
            int bodyWear = saveFile.read();
            if (bodyWear > 0) {
                player.bodyWear = player.inventory.get(bodyWear-1).sides;
            } else {
                player.bodyWear = null;
            }
            int handsObject = saveFile.read();
            if (handsObject > 0) {
                player.objectInHands = player.inventory.get(handsObject-1).sides;
            }else {
                player.objectInHands = null;
            }
            for (int i =0; i < npcs.size(); ++i) {
                for (int j =0; j < npcs.get(i).flags.size(); ++j) {
                    int flag = saveFile.read();
                    if (flag == 1) {
                        npcs.get(i).flags.set(j, true);
                    } else {
                        npcs.get(i).flags.set(j, false);
                    }
                }
                npcsAreas.set(i,saveFile.read());
                saveFile.read(flo);
                npcs.get(i).hitBox.x = toFloat(flo);
                saveFile.read(flo);
                npcs.get(i).hitBox.y = toFloat(flo);
                if (npcs.get(i).spawnArea != npcsAreas.get(i)) {
                    //ObjectCell cell = areas.get(npcs.get(i).spawnArea).worldObjectsHandler.removeSolid(areas.get(npcs.get(i).spawnArea).worldObjectsHandler.solids.indexOf(npcs.get(i)));
                    //ObjectCell soc = areas.get(npcsAreas.get(i)).worldObjectsHandler.addSolid(npcs.get(i), this, cell.currentState, cell.items);
                    areas.get(npcs.get(i).spawnArea).worldObjectsHandler.deleteObjectCellsForEntity(npcs.get(i));
                    areas.get(npcs.get(i).spawnArea).worldObjectsHandler.NPCs.remove(npcs.get(i));
                    areas.get(npcsAreas.get(i)).worldObjectsHandler.addNPC(npcs.get(i), this, -1);
                }
                itemsCount = saveFile.read();
                npcs.get(i).inventory.clear();
                for (int j =0; j < itemsCount; ++j) {
                    int bytesCount = saveFile.read();
                    byte[] bitfield = new byte[bytesCount];
                    saveFile.read(bitfield);
                    String str = new String(bitfield, StandardCharsets.UTF_8);
                    Item item = new Item(assets, worldDir.path(), str);
                    item.stack = saveFile.read();
                    npcs.get(i).inventory.add(item);
                }
                headWear = saveFile.read();
                if (headWear > 0) {
                    npcs.get(i).headWear = npcs.get(i).inventory.get(headWear-1).sides;
                } else {
                    npcs.get(i).headWear = null;
                }
                bodyWear = saveFile.read();
                if (bodyWear > 0) {
                    npcs.get(i).bodyWear = npcs.get(i).inventory.get(bodyWear-1).sides;
                } else {
                    npcs.get(i).bodyWear = null;
                }
                handsObject = saveFile.read();
                if (handsObject > 0) {
                    npcs.get(i).objectInHands = npcs.get(i).inventory.get(handsObject-1).sides;
                } else {
                    npcs.get(i).objectInHands = null;
                }
            }

            for (int i =0; i < movables.size(); ++i) {
                movablesAreas.set(i,saveFile.read());
                saveFile.read(flo);
                movables.get(i).hitBox.x = toFloat(flo);
                saveFile.read(flo);
                movables.get(i).hitBox.y = toFloat(flo);
                if (movables.get(i).spawnArea != movablesAreas.get(i)) {
                    ObjectCell cell = areas.get(movables.get(i).spawnArea).worldObjectsHandler.removeSolid(areas.get(movables.get(i).spawnArea).worldObjectsHandler.solids.indexOf(movables.get(i)));
                    ObjectCell cel2 = areas.get(movablesAreas.get(i)).worldObjectsHandler.addSolid(movables.get(i), this, cell.currentState, cell.items);
                    for (int j =0; j < objects.size(); ++j) {
                        if (objects.get(j).entity == movables.get(i)) {
                            objects.set(j, cel2);
                        }
                    }
                }
            }

            for (int i =0; i < objects.size(); ++i) {
                objects.get(i).currentState = saveFile.read();
                int drawChar = saveFile.read();
                if (drawChar == 1) {
                    objects.get(i).entity.drawChar = true;
                } else {
                    objects.get(i).entity.drawChar = false;
                }
                objects.get(i).soundsAreStopped = true;
                if (areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).worldObjectsHandler.objects.contains(objects.get(i))) {
                    //objects.get(i).updateSoundState(menu);
                }
                objects.get(i).updateEntityState(assets, worldDir.path());
                itemsCount = saveFile.read();
                if (objects.get(i).items != null) {
                    objects.get(i).items.clear();
                    for (int j =0; j < itemsCount; ++j) {
                        int bytesCount = saveFile.read();
                        byte[] bitfield = new byte[bytesCount];
                        saveFile.read(bitfield);
                        String str = new String(bitfield, StandardCharsets.UTF_8);
                        Item item = new Item(assets, worldDir.path(), str);
                        item.stack = saveFile.read();
                        objects.get(i).items.add(item);
                    }
                }
            }

            //deleteObjectCellsForEntity(activeItem);
            for (int i =0; i<areas.size(); ++i) {
                for (int j =0; j<areas.get(i).worldObjectsHandler.items.size(); ++j) {
                    areas.get(i).worldObjectsHandler.deleteObjectCellsForEntity(areas.get(i).worldObjectsHandler.items.get(j));
                }
                areas.get(i).worldObjectsHandler.items.clear();
                areas.get(i).worldObjectsHandler.currentDialog = null;
                areas.get(i).worldObjectsHandler.removeParticles();
                areas.get(i).playerHidden = false;
            }
            if (hidden == 1) {
                curArea.playerHidden = true;
            } else {
                curArea.playerHidden = false;
            }
            if (activeObjectId > 0) {
                curArea.worldObjectsHandler.activeObject = curArea.worldObjectsHandler.objects.get(activeObjectId-1);
                curArea.worldObjectsHandler.activeObject.updateHiddenPlayer(assets, worldDir.path());
            } else {
                curArea.worldObjectsHandler.activeObject = null;
            }
            itemsOnFloor.clear();
            itemsOnFloorAreas.clear();
            itemsCount = saveFile.read();
            for (int i =0; i<itemsCount; ++i) {
                int areaId = saveFile.read();
                int bytesCount = saveFile.read();
                byte[] bitfield = new byte[bytesCount];
                saveFile.read(bitfield);
                String str = new String(bitfield, StandardCharsets.UTF_8);
                Item item = new Item(assets, worldDir.path(), str);
                item.stack = saveFile.read();
                saveFile.read(flo);
                float x = toFloat(flo);
                saveFile.read(flo);
                float y = toFloat(flo);
                Entity itemGlow = new Entity(assets, "item.png", x, y, 0 ,0 ,0);
                itemGlow.containingItem = item;
                areas.get(areaId).worldObjectsHandler.addNonSolid(itemGlow, -1);
                itemsOnFloorAreas.add(areaId);
                itemsOnFloor.add(itemGlow);
            }
            curArea.worldObjectsHandler.currentDialog = null;
            int dialogFileNameLength = saveFile.read();
            if (dialogFileNameLength > 0) {
                menu.paused = true;

                byte[] dialogFileNameBytes = new byte[dialogFileNameLength];
                saveFile.read(dialogFileNameBytes);
                String dialogFileName = new String(dialogFileNameBytes, StandardCharsets.UTF_8);
                int dialogMainCharId = saveFile.read();
                int dialogCharPathLength = saveFile.read();
                byte[] dialogCharPathBytes = new byte[dialogCharPathLength];
                saveFile.read(dialogCharPathBytes);
                String dialogCharPath = new String(dialogCharPathBytes, StandardCharsets.UTF_8);

                //int isChoice = saveFile.read();
                int dialogCurrentId = saveFile.read();
                Dialog dialog = new Dialog(dialogFileName, "", dialogMainCharId, false, npcs, curArea.player, assets, dialogCharPath, menu.currentLanguage, menu);
                /*if (isChoice == 0) {
                    dialog.reload("", menu.currentLanguage, dialogCurrentId);
                } else {
                    dialog.reload("", menu.currentLanguage, -dialogCurrentId);
                }*/
                curArea.invalidate(this);
                curArea.worldObjectsHandler.currentDialog = dialog;
            } else {
                menu.paused = false;
            }
            player.hitBox.x = playerX;
            player.x = playerX;
            player.graphicX = playerX;
            player.hitBox.y = playerY;
            player.y = playerY;
            player.graphicY = playerY;
            menu.drawPause = false;
            //menu.unpausable = false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadedState = true;
    }

    public void load(AssetManager assets) {
        npcs = new ArrayList<NPC>();
        npcsAreas = new ArrayList<Integer>();
        objects = new ArrayList<ObjectCell>();
        movables = new ArrayList<HittableEntity>();
        movablesAreas = new ArrayList<Integer>();
        itemsOnFloor = new ArrayList<Entity>();
        itemsOnFloorAreas = new ArrayList<Integer>();
        this.assets = assets;
        stepSounds = new ArrayList<String>();
        areasAmbients = new ArrayList<String>();
        roomNames = new ArrayList<String>();
        assets.load("item.png", Texture.class);
        assets.load("active.png", Texture.class);
        assets.load("blank.png", Texture.class);
        assets.load("blank2.png", Texture.class);
        assets.load("shadow.png", Texture.class);
        assets.load("prt_shadow.png", Texture.class);
        assets.load("inventory_overlay3.png", Texture.class);
        assets.load("inventory_overlay2.png", Texture.class);

        //assets.load(folderPath + "bg/bg.png", Texture.class);
        flags = new ArrayList<Boolean>();
        flagNames = new ArrayList<String>();
        if (tlw == null) {
            worldDir = Gdx.files.internal(folderPath);
            int curX,curY,curZ=0;
            int count = 0;
            for (FileHandle entry: worldDir.list()) {
                if (!entry.file().getName().contains(".png") || entry.file().getName().equals("bg.png")){
                    continue;
                }
                assets.load(entry.path(), Pixmap.class);
                if (entry.file().getName().length() == 7) {
                    curX = Integer.parseInt(entry.file().getName().substring(0,1));
                    curY = Integer.parseInt(entry.file().getName().substring(2,3));
                } else if (entry.file().getName().length() == 8) {
                    if (entry.file().getName().substring(0,1).equals("-")) {
                        curX = -Integer.parseInt(entry.file().getName().substring(1,2));
                        curY = Integer.parseInt(entry.file().getName().substring(3,4));
                    } else {
                        curX = Integer.parseInt(entry.file().getName().substring(0,1));
                        curY = -Integer.parseInt(entry.file().getName().substring(3,4));
                    }
                } else {
                    String str = entry.file().getName();
                    curX = -Integer.parseInt(entry.file().getName().substring(1,2));
                    curY = -Integer.parseInt(entry.file().getName().substring(4,5));
                }
                areaIds.get(curX).get(curY).set(curZ, count);
                count++;
            }
        } else {
            try {

                BufferedReader flagsIn = new BufferedReader(new FileReader(folderPath + "/flags"));
                String line = flagsIn.readLine();
                int flagsCount = Integer.parseInt(line);
                for (int i = 0; i < flagsCount; ++i) {
                    line = flagsIn.readLine();
                    flagNames.add(line);
                    line = flagsIn.readLine();
                    if (line.equals("0")) {
                        flags.add(false);
                    } else {
                        flags.add(true);
                    }
                }

                fis = new FileInputStream(tlw);

                tileIndices = new ArrayList<Integer>();
                tileTypes = new ArrayList<Integer>();
                names = new ArrayList<String>();
                newNames = new ArrayList<String>();
                int namesCount;
                namesCount = fis.read();
                for (int i =0; i < namesCount; ++i) {
                    int nameSize = fis.read();
                    byte[] buff = new byte[nameSize];
                    fis.read(buff);
                    newNames.add(new String(buff));
                }

                int size = fis.read();
                byte[] buff = new byte[size];
                fis.read(buff);
                name = new String(buff);
                int tmp = fis.read();
                if (tmp == 0) platformMode = false;
                else platformMode = true;
                width = fis.read();
                height = fis.read();
                areaIds = new ArrayList<ArrayList<ArrayList<Integer>>>();
                for (int i = 0; i < width; i++) {
                    areaIds.add(new ArrayList<ArrayList<Integer>>());
                    for (int t = 0; t < width; t++) {
                        areaIds.get(i).add(new ArrayList<Integer>());
                        for (int k = 0; k < height; k++) {
                            areaIds.get(i).get(t).add(-1);
                        }
                    }
                }

                tileWidth = fis.read();
                tileHeight = fis.read();
                firtsAreaWidth = fis.read();
                firtsAreaHeight = fis.read();
                areaWidth = fis.read();
                areaHeight = fis.read();
                /*if (!platformMode) {
                    firtsAreaWidth = 7;
                    firtsAreaHeight = 5;
                } else {
                    firtsAreaWidth = 24;
                    firtsAreaHeight = 16;
                }*/
                int curCoordX = fis.read();
                int curCoordY = fis.read();
                int curCoordZ = fis.read();
                //int endChecker = 0;
                curAreaX = curCoordX;
                curAreaY = curCoordY;
                curAreaZ = curCoordZ;

                map = new RoomsMap(this);
                while (fis.available() > 1) {
                    int size1 = fis.read();
                    byte[] buff1 = new byte[size1];
                    fis.read(buff1);
                    String name1 = new String(buff1);

                    int size2 = fis.read();
                    byte[] buff2 = new byte[size2];
                    fis.read(buff2);
                    String name2 = new String(buff2);

                    roomNames.add(name1);
                    areasAmbients.add(name2);

                    buff = new byte[areaWidth*areaHeight*9];
                    fis.read(buff);
                    if (curCoordX != 246) {
                        Area newArea = new Area(curCoordX, curCoordY, curCoordZ, areaWidth/firtsAreaWidth, areaHeight/firtsAreaHeight, buff, areaWidth, areaHeight, tileWidth, tileHeight, platformMode, this);
                        newArea.name = name1;
                        areas.add(newArea);
                        map.addRoom(newArea, curCoordX, curCoordY, curCoordZ, newArea.width / firtsAreaWidth, newArea.height / firtsAreaHeight, name1);
                        if (newArea.containsSpawn) {
                            curAreaX = curCoordX;
                            curAreaY = curCoordY;
                            curAreaZ = curCoordZ;
                        }
                        int areaRoomsHor = (int)Math.ceil(areaWidth/firtsAreaWidth);
                        int areaRoomsVer = (int)Math.ceil(areaHeight/firtsAreaHeight);
                        for (int i = curCoordX; i < curCoordX+areaRoomsHor; ++i) {
                            for (int t = curCoordY; t < curCoordY+areaRoomsVer; ++t) {
                                areaIds.get(i).get(t).set(curCoordZ, areas.size() - 1);
                            }
                        }
                    }
                    //areaIds.get(curCoordX).get(curCoordY).set(curCoordZ, areas.size() - 1);
                    //fis.skip(areaWidth*areaHeight*4);
                    if (fis.available() > 0) {
                        areaWidth = fis.read();
                        areaHeight = fis.read();
                        curCoordX = fis.read();
                        curCoordY = fis.read();
                        curCoordZ = fis.read();
                    } else {
                        break;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (platformMode) {
                assets.load("platform_sounds/jump.wav", Sound.class);
                assets.load("platform_sounds/djump.wav", Sound.class);
                assets.load("platform_sounds/die.wav", Sound.class);
                assets.load("platform_sounds/step.wav", Sound.class);
                assets.load("platform_sounds/land.wav", Sound.class);
            }
            worldDir = Gdx.files.internal(folderPath);

            for (FileHandle entry: worldDir.list()) {
                if (!entry.file().getName().contains(".png")){
                    continue;
                }
                assets.load(entry.path(), Texture.class);
                names.add(entry.name().substring(0, entry.name().length() - 4));
                tileTypes.add(0);
                tileIndices.add(spritesCount);
                spritesCount++;
                stepSounds.add(null);
            }
            worldDir = Gdx.files.internal(folderPath+"/tiles");
            for (FileHandle entry: worldDir.list()) {
                if (!entry.file().getName().contains(".png")){
                    continue;
                }
                assets.load(entry.path(), Texture.class);
                names.add("tiles\\" + entry.name().substring(0, entry.name().length() - 4));
                tileTypes.add(1);
                tileIndices.add(tilesetsCount);
                tilesetsCount++;
                stepSounds.add(null);
            }
            worldDir = Gdx.files.internal(folderPath+"/bg");
            for (FileHandle entry: worldDir.list()) {
                if (!entry.file().getName().contains(".png")){
                    continue;
                }
                assets.load(entry.path(), Texture.class);
            }
            worldDir = Gdx.files.internal(folderPath+"/objects/util");
            for (FileHandle entry: worldDir.list()) {
                if (!entry.file().getName().contains(".png")){
                    continue;
                }
                assets.load(entry.path(), Texture.class);
            }
            worldDir = Gdx.files.internal(folderPath+"/items/icons");
            for (FileHandle entry: worldDir.list()) {
                assets.load(entry.path(), Texture.class);
            }
            worldDir = Gdx.files.internal(folderPath+"/items/big_icons");
            for (FileHandle entry: worldDir.list()) {
                assets.load(entry.path(), Texture.class);
            }
            worldDir = Gdx.files.internal(folderPath+"/items/sides");
            for (FileHandle entry: worldDir.list()) {
                assets.load(entry.path(), Texture.class);
            }
            worldDir = Gdx.files.internal(folderPath+"/sounds");
            for (FileHandle entry: worldDir.list()) {
                if (entry.isDirectory()) {
                    for (FileHandle entry2: entry.list()) {
                        assets.load(entry2.path(), Sound.class);
                    }
                } else {
                    assets.load(entry.path(), Sound.class);
                }
            }
            worldDir = Gdx.files.internal(folderPath+"/anim");
            for (FileHandle entry: worldDir.list()) {
                if (!entry.file().getName().contains(".png")){
                    continue;
                }
                assets.load(entry.path(), Texture.class);
                names.add("anim\\" + entry.name().substring(0, entry.name().length() - 4));
                tileTypes.add(2);
                tileIndices.add(animsLoaded);
                animsLoaded++;
                stepSounds.add(null);
            }
            worldDir = Gdx.files.internal(folderPath);

            updateTiles();
        }
        try {
            BufferedReader in = new BufferedReader(new FileReader(worldDir.path() + "/sound"));
            String line;
            int numTileSteps = Integer.parseInt(in.readLine());
            for (int i = 0; i < numTileSteps; ++i) {
                line = in.readLine();
                int n = names.indexOf(line);
                if (n >= 0) {
                    stepSounds.set(n, in.readLine());
                } else {
                    in.readLine();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        particles = new ArrayList<ParticleProperties>();
        FileHandle particlesDir = Gdx.files.internal(folderPath + "/particles");
        int dirsCount = 0;
        for (FileHandle entry: particlesDir.list()) {
            if (entry.file().isDirectory()) {
                assets.load(folderPath + "/particles/" + dirsCount + "/1.png", Texture.class);
                FileHandle entry2 = Gdx.files.internal(folderPath + "/particles/" + dirsCount + "/2.png");
                if (entry2.exists()) {
                    assets.load(entry2.path(), Texture.class);
                }
                particles.add(new ParticleProperties(folderPath + "/particles/" + dirsCount));
                dirsCount++;
            }
        }
        if (platformMode) {
            FileHandle charDir =  Gdx.files.internal(folderPath + "/chars");
            for (FileHandle entry: charDir.list()) {
                if (entry.isDirectory()) {
                    assets.load(entry.path() + "/char.png", Texture.class);
                    assets.load(entry.path() + "/chargo.png", Texture.class);
                    assets.load(entry.path() + "/speech.wav", Sound.class);
                }
            }
            assets.load(folderPath + "/save1.png", Texture.class);
            assets.load(folderPath + "/save2.png", Texture.class);
        } else {
            characterMaker = new CharacterMaker(assets, folderPath, menu);
            characterMaker.cdcs.get(0).setLookingForward(true);
        }
        /*int minAreaArea = 99999;
        for (int i = 0 ; i < areas.size(); ++i) {
            if (areas.get(i).width * areas.get(i).height < minAreaArea) {
                firtsAreaHeight = areas.get(i).height;
                firtsAreaWidth = areas.get(i).width;
            }
        }*/
        loaded = true;
    }

    public void initialiseResources(AssetManager assets) {
        if (!platformMode) {
            characterMaker.initialiseResources(assets, folderPath);
        }
        for (int i = 0; i < particles.size(); ++i) {
            particles.get(i).initialiseResources(assets, folderPath + "/particles/" + i);
        }
        //bg = assets.get(folderPath+"/bg.png", Texture.class);
        shapeRenderer = new ShapeRenderer();
        if (!initialised && !areasCreated) {
            if (tlw == null) {
                for (FileHandle entry: worldDir.list()) {
                    if (!entry.file().getName().contains(".png") || entry.file().getName().equals("bg.png")){
                        continue;
                    }
                    maps.add(assets.get(entry.path(), Pixmap.class));
                    if (worldDir.name().substring(0, 2).equals("p_")) {
                        //areas.add(new Area(assets, this, maps.get(maps.size()-1), true, 16, 16));
                    } else {
                        //areas.add(new Area(assets, this, maps.get(maps.size()-1), false, 32, 16));
                    }
                }
            /*for (int i=0; i<maps.size(); ++i) {

                //areas.get(i).initialiseResources(assets);
            }*/
                maps.clear();
                areasCreated = true;
            } else {
                sprites = new ArrayList<Texture>();
                worldDir = Gdx.files.internal(folderPath);
                for (FileHandle entry: worldDir.list()) {
                    if (!entry.file().getName().contains(".png")){
                        continue;
                    }
                    for (int i = 0; i < names.size(); ++i) {
                        String s = entry.name().substring(0, entry.name().length() - 4);
                        /*if (entry.name().substring(0, entry.name().length() - 4).equals(names.get(i))) {
                            tileIndices.set(i, sprites.size());
                            tileTypes.set(i, 0);
                        }*/
                    }
                    sprites.add(assets.get(entry.path(), Texture.class));
                }
                tiles = new ArrayList<BlockMultiTile>();
                worldDir = Gdx.files.internal(folderPath+"/tiles");
                for (FileHandle entry: worldDir.list()) {
                    if (!entry.file().getName().contains(".png")){
                        continue;
                    }
                    /*for (int i = 0; i < names.size(); ++i) {
                        if (("tiles\\" + entry.name().substring(0, entry.name().length() - 4)).equals(names.get(i))) {
                            tileIndices.set(i, tiles.size());
                            tileTypes.set(i, 1);
                        }
                    }*/
                    tiles.add(new BlockMultiTile(assets.get(entry.path(), Texture.class)));
                }
                animations = new ArrayList<AnimationSequence>();
                worldDir = Gdx.files.internal(folderPath+"/anim");
                for (FileHandle entry: worldDir.list()) {
                    if (!entry.file().getName().contains(".png")){
                        continue;
                    }
                    /*for (int i = 0; i < names.size(); ++i) {
                        if (("anim\\" + entry.name().substring(0, entry.name().length() - 4)).equals(names.get(i))) {
                            tileIndices.set(i, animations.size());
                            tileTypes.set(i, 2);
                        }
                    }*/
                    animations.add(new AnimationSequence(assets, entry.path(), 12, true));
                }
                worldDir = Gdx.files.internal(folderPath);
            }
        }
        //System.out.println(areas.size() + " " + areaIds.size());
        for (int t = 0; t < areas.size(); ++t) {
            if (areas.get(t) == null) continue;
            areas.get(t).initialiseResources(assets, this, characterMaker);
        }

        if (areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).initialised) {
            initialised = true;
            if (save >= 0) {
                File saveFile = new File(worldDir + "/saves/state" + save);
                if (saveFile.exists()) {
                    loadState();
                } else {
                    createNewSave();
                }
            } else {
                createNewSave();
            }

        }
        int n = areaIds.get(curAreaX).get(curAreaY).get(curAreaZ);
        areas.get(n).isCurrent = true;
        if (areasAmbients.get(n) != null && areasAmbients.get(n).length() > 0) {
            currentSoundPath = areasAmbients.get(n);
            currentSound = assets.get(folderPath + "/sounds/" + areasAmbients.get(n), Sound.class);
            currentSoundId = currentSound.loop(menu.musicVolume/100.0f);
            startedAmbient = true;
        }
        map.connectExits();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(worldDir.path() + "/bg/bg"));
            String line = "";
            int numLayers = Integer.parseInt(in.readLine());
            Background defaultBg = new Background();
            for (int i = 0; i < numLayers; ++i) {
                line = in.readLine();
                String vals[] = line.split(" ");
                defaultBg.addLayer(assets.get(worldDir.path() + "/bg/" + vals[0] + ".png", Texture.class), Float.parseFloat(vals[1]),  Float.parseFloat(vals[2]),  Float.parseFloat(vals[3]),  Float.parseFloat(vals[4]));
            }
            for (int i =0; i < areas.size(); ++i) {
                areas.get(i).bg = defaultBg;
            }
            int numCustomBGGroups = Integer.parseInt(in.readLine());
            for (int z = 0; z < numCustomBGGroups; ++z) {
                int numCustomBGs = Integer.parseInt(in.readLine());
                ArrayList<Area> customAreas = new ArrayList<Area>();
                for (int i = 0; i < numCustomBGs; ++i) {
                    line = in.readLine();
                    customAreas.add(map.getAreaByName(line));
                }
                Background bg = new Background();
                numLayers = Integer.parseInt(in.readLine());
                for (int j = 0; j < numLayers; ++j) {
                    line = in.readLine();
                    String vals[] = line.split(" ");
                    bg.addLayer(assets.get(worldDir.path() + "/bg/" + vals[0] + ".png", Texture.class), Float.parseFloat(vals[1]),  Float.parseFloat(vals[2]),  Float.parseFloat(vals[3]),  Float.parseFloat(vals[4]));
                }
                for (int j = 0; j < customAreas.size(); ++j) {
                    if (customAreas.get(j) != null) {
                        customAreas.get(j).bg = bg;
                    }
                }
            }
            in = new BufferedReader(new FileReader(worldDir.path() + "/looping"));
            int numLoopingAreas = Integer.parseInt(in.readLine());
            for (int i=0; i < numLoopingAreas; ++i) {
                line = in.readLine();
                Area a = map.getAreaByName(line);
                line = in.readLine();
                a.loopingX = line.contains("x");
                a.loopingY = line.contains("y");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println();
    }

    private void createNewSave() {
        File savesDir = new File(worldDir + "/saves");
        save = 0;
        if (savesDir.exists()) {
            FileHandle curDir = Gdx.files.internal(worldDir + "/saves");
            for (FileHandle entry: curDir.list()) {
                save++;
            }
        }
        saveState();
    }

    private void checkSolidsPosition() {
        Area a = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ));
        for (int i = 0; i < a.worldObjectsHandler.solids.size() + a.worldObjectsHandler.NPCs.size(); ++i) {
            HittableEntity solid;
            boolean isNPC = false;
            if (i < a.worldObjectsHandler.solids.size()) {
                solid = a.worldObjectsHandler.solids.get(i);
            } else {
                solid = a.worldObjectsHandler.NPCs.get(i - a.worldObjectsHandler.solids.size());
                isNPC = true;
            }
            if (solid.movable) {
                int inRoomXCoord = (int)Math.floor(solid.x/a.TILE_WIDTH/firtsAreaWidth);
                int inRoomYCoord = (int)(Math.floor(a.height/firtsAreaHeight-(solid.y+solid.hitBox.getHeight()/2)/a.TILE_HEIGHT/firtsAreaHeight));
                int offX = 0, offY = 0, offZ = 0;
                boolean horizontal = false;
                if (solid.hitBox.x < 5) {
                    offX = -1;
                    offY = inRoomYCoord;
                    horizontal = true;
                    if (a.loopingX) {
                        if (platformMode) {
                            solid.x += a.width * a.TILE_WIDTH;
                            solid.graphicX += a.width * a.TILE_WIDTH;
                            solid.hitBox.x += a.width * a.TILE_WIDTH;
                        }
                        return;
                    }
                } else if (solid.hitBox.x > a.TILE_WIDTH*(a.width)-a.player.hitBox.getWidth()-5) {
                    offX = inRoomXCoord + 1;
                    offY = inRoomYCoord;
                    horizontal = true;
                    if (a.loopingX) {
                        if (platformMode) {
                            solid.x -= a.width * a.TILE_WIDTH;
                            solid.graphicX -= a.width * a.TILE_WIDTH;
                            solid.hitBox.x -= a.width * a.TILE_WIDTH;
                        }
                        return;
                    }
                } else if (solid.hitBox.y+solid.hitBox.getHeight()/2 < -solid.hitBox.getHeight()/2) {
                    offX = inRoomXCoord;
                    offY = inRoomYCoord;
                    if (a.loopingY) {
                        if (platformMode) {
                            solid.y += a.height * a.TILE_HEIGHT;
                            solid.graphicY += a.height * a.TILE_HEIGHT;
                            solid.hitBox.y += a.height * a.TILE_HEIGHT;
                        }
                        return;
                    }
                } else if (solid.hitBox.y > a.TILE_HEIGHT*(a.height-1)) {
                    offX = inRoomXCoord;
                    offY = -1;
                    if (a.loopingY) {
                        if (platformMode) {
                            solid.y -= a.height * a.TILE_HEIGHT;
                            solid.graphicY -= a.height * a.TILE_HEIGHT;
                            solid.hitBox.y -= a.height * a.TILE_HEIGHT;
                        }
                        return;
                    }
                } else {
                    continue;
                }
                ObjectCell cell = null;
                if (!isNPC) cell = a.worldObjectsHandler.removeSolid(i);
                else {
                    a.worldObjectsHandler.NPCs.remove(solid);
                }
                int toAreaId = areaIds.get(curAreaX+offX).get(curAreaY+offY).get(curAreaZ+offZ);
                if (toAreaId != -1) {
                    Area toArea = areas.get(toAreaId);
                    float pos = 0;
                    if (horizontal) {
                        pos = solid.hitBox.y + ((toArea.y+toArea.h) - (a.y+a.h)) * firtsAreaHeight * a.TILE_HEIGHT;
                        if (offX > 0) {
                            solid.hitBox.x = toArea.TILE_WIDTH;
                            if (isNPC) ((NPC)solid).characterMaker.setDirection(3, ((NPC)solid).charId);
                        } else {
                            solid.hitBox.x = (toArea.width - 2) * toArea.TILE_WIDTH ;
                            if (isNPC) ((NPC)solid).characterMaker.setDirection(1, ((NPC)solid).charId);
                        }
                        solid.hitBox.y = pos;
                    } else {
                        pos = solid.hitBox.x - (areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY).get(curAreaZ)).x - a.x)*firtsAreaWidth*a.TILE_WIDTH;//a.player.hitBox.y;// - inRoomYCoord;
                        if (offY > 0) {
                            solid.hitBox.y = (toArea.height - 2) * toArea.TILE_HEIGHT - solid.hitBox.height;
                            if (isNPC) ((NPC)solid).characterMaker.setDirection(2, ((NPC)solid).charId);
                        } else {
                            solid.hitBox.y = toArea.TILE_HEIGHT;
                            if (isNPC) ((NPC)solid).characterMaker.setDirection(4, ((NPC)solid).charId);
                        }
                        solid.hitBox.x = pos;
                    }
                    solid.x = solid.hitBox.x;
                    solid.y = solid.hitBox.y;
                    solid.h = solid.hitBox.y;
                    solid.graphicX = solid.x;
                    solid.graphicY = solid.y;
                    if (solid.getClass() == HittableEntity.class) {
                        toArea.worldObjectsHandler.addSolid(solid, this, cell.currentState, cell.items);
                        movablesAreas.set(movables.indexOf(solid), areas.indexOf(toArea));
                    } else {
                        //ObjectCell soc = toArea.worldObjectsHandler.addSolid(solid, this, -1, null);
                        ((NPC)solid).speedX = 0;
                        ((NPC)solid).speedY = 0;
                        a.worldObjectsHandler.deleteObjectCellsForEntity(solid);
                        toArea.worldObjectsHandler.addNPC((NPC)solid, this, -1);
                        npcsAreas.set(npcs.indexOf(solid), areas.indexOf(toArea));
                        ((NPC)solid).curRoom = map.getRoomByName(toArea.name);
                        if (((NPC)solid).currentTaskPath != null && ((NPC)solid).currentTaskPath.size() > 0 && ((NPC)solid).currentTaskPath.get(0).otherExit.room == ((NPC)solid).curRoom) {
                            ((NPC)solid).currentTaskPath.remove(0);
                        }
                    }
                }
            }
        }
    }

    private void checkAreaObjects(Area a) {
        a.worldObjectsHandler.checkNPCs(menu, folderPath, assets);
        a.worldObjectsHandler.checkObjects(folderPath, assets, this);
    }

    private void checkPlayerPosition() {
        Area a = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ));
        if (tlw == null && maps.size() == 0) return;

        int inRoomXCoord = (int)Math.floor(a.player.x/a.TILE_WIDTH/firtsAreaWidth);
        int inRoomYCoord = (int)(Math.floor(a.height/firtsAreaHeight-(a.player.y+a.player.hitBox.getHeight()/2)/a.TILE_HEIGHT/firtsAreaHeight));
        if (a.player.x < 5) {
            changeArea(true, -1, inRoomYCoord, 0);
        } else if (a.player.x > a.TILE_WIDTH*(a.width)-a.player.hitBox.getWidth()-5) {
            changeArea(true, inRoomXCoord + 1, inRoomYCoord, 0);
        } else if (a.player.y+a.player.hitBox.getHeight() < -a.player.hitBox.getHeight()/2) {
            changeArea(false, inRoomXCoord, inRoomYCoord, 0);
        } else if (a.player.y > a.TILE_HEIGHT*(a.height-1)) {
            changeArea(false, inRoomXCoord, -1, 0);
        }
        if (a.player.movingConfiguration.use == 1) {
            a.worldObjectsHandler.activateActiveObject(menu, folderPath, assets, this);
        }
        if (a.player.z > a.cameraY + SCREEN_HEIGHT) {
            if (curAreaZ >= 1 && areaIds.get(curAreaX).get(curAreaY).get(curAreaZ-1) != -1) {
                changeArea(false, inRoomXCoord, inRoomYCoord, -1);
            }
            else a.respawnPlayer(null, assets, 0, 0, 0, 0, null);
        }
        boolean resetCamera = false;
        double cameraDiffX = 0;
        double cameraDiffY = 0;
        if (a.loopingX) {
            if (a.player.hitBox.x + a.player.hitBox.width >= a.width * a.TILE_WIDTH) {
                cameraDiffX = a.player.graphicX + a.player.hitBox.getWidth()/2 - a.cameraX;
                cameraDiffY = a.player.graphicY + a.player.hitBox.getHeight()/2 - a.cameraY;
                a.player.x -= a.width * a.TILE_WIDTH;
                a.player.graphicX -= a.width * a.TILE_WIDTH;
                a.player.hitBox.x -= a.width * a.TILE_WIDTH;
                resetCamera = true;
            } else if (a.player.hitBox.x + a.player.hitBox.width < 0) {
                cameraDiffX = a.player.graphicX + a.player.hitBox.getWidth()/2 - a.cameraX;
                cameraDiffY = a.player.graphicY + a.player.hitBox.getHeight()/2 - a.cameraY;
                a.player.x += a.width * a.TILE_WIDTH;
                a.player.graphicX += a.width * a.TILE_WIDTH;
                a.player.hitBox.x += a.width * a.TILE_WIDTH;
                resetCamera = true;
            }
        }
        if (a.loopingY) {
            if (a.player.hitBox.y + a.player.hitBox.height * 2 >= a.height * a.TILE_HEIGHT) {
                cameraDiffX = a.player.graphicX + a.player.hitBox.getWidth()/2 - a.cameraX;
                cameraDiffY = a.player.graphicY + a.player.hitBox.getHeight()/2 - a.cameraY;
                a.player.y -= a.height * a.TILE_HEIGHT;
                a.player.graphicY -= a.height * a.TILE_HEIGHT;
                a.player.hitBox.y -= a.height * a.TILE_HEIGHT;
                resetCamera = true;
            } else if (a.player.hitBox.y + a.player.hitBox.height * 2 < 0) {
                cameraDiffX = a.player.graphicX + a.player.hitBox.getWidth()/2 - a.cameraX;
                cameraDiffY = a.player.graphicY + a.player.hitBox.getHeight()/2 - a.cameraY;
                a.player.y += a.height * a.TILE_HEIGHT;
                a.player.graphicY += a.height * a.TILE_HEIGHT;
                a.player.hitBox.y += a.height * a.TILE_HEIGHT;
                resetCamera = true;
            }
        }

        if (resetCamera) {
            a.cameraX = a.player.graphicX + a.player.hitBox.getWidth()/2 - cameraDiffX;
            a.cameraY = a.player.graphicY + a.player.hitBox.getHeight()/2 - cameraDiffY;
        }
    }

    public void changeArea(boolean horizontal, int offX, int offY, int offZ) {
        Area a = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ));
        if (curAreaX+offX >= 0 && curAreaX+offX <= areaIds.size()-1 && curAreaY+offY >= 0 && curAreaY+offY <= areaIds.get(0).size()-1 && (areaIds.get(curAreaX + offX).get(curAreaY + offY).get(curAreaZ) != -1 && ((offX != 0 && !a.loopingX) || (offY != 0 && !a.loopingY)))) {

            //areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).resetCheckPoints();
            //areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY)).player.x = areas.get(areaIds.get(curAreaX).get(curAreaY)).player.x - areas.get(areaIds.get(curAreaX).get(curAreaY)).TILE_WIDTH*offX;
            //areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY)).player.y = areas.get(areaIds.get(curAreaX).get(curAreaY)).player.y - areas.get(areaIds.get(curAreaX).get(curAreaY)).TILE_HEIGHT*offY;
            //areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY)).resetCamera();
            if (offX == 0) {
                areas.get(areaIds.get(curAreaX+offX).get(curAreaY + offY).get(curAreaZ)).playerTileX = a.playerTileX - (areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY).get(curAreaZ)).x - a.x)*firtsAreaWidth;
                //areas.get(areaIds.get(curAreaX+offX).get(curAreaY + offY).get(curAreaZ)).playerTileX = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).playerTileX;
                if (offY < 0) {
                    areas.get(areaIds.get(curAreaX + offX).get(curAreaY+offY).get(curAreaZ)).playerTileY = 1;
                } else {
                    areas.get(areaIds.get(curAreaX + offX).get(curAreaY+offY).get(curAreaZ)).playerTileY = areas.get(areaIds.get(curAreaX + offX).get(curAreaY+offY).get(curAreaZ)).height-2;
                }
            } else if (offY == 0) {
                areas.get(areaIds.get(curAreaX+offX).get(curAreaY + offY).get(curAreaZ)).playerTileY = a.playerTileY + ((areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY).get(curAreaZ)).y+areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY).get(curAreaZ)).h) - (a.y+a.h))*firtsAreaHeight;
                //areas.get(areaIds.get(curAreaX+offX).get(curAreaY + offY).get(curAreaZ)).playerTileY = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).playerTileY;
                if (offX > 0) {
                    areas.get(areaIds.get(curAreaX + offX).get(curAreaY+offY).get(curAreaZ)).playerTileX = 0;
                } else {
                    areas.get(areaIds.get(curAreaX + offX).get(curAreaY+offY).get(curAreaZ)).playerTileX = areas.get(areaIds.get(curAreaX + offX).get(curAreaY+offY).get(curAreaZ)).width-1;
                }
            }

            //areas.get(areaIds.get(curAreaX+offX).get(curAreaY + offY).get(curAreaZ)).playerTileX = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).playerTileX-(areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).width-1)*offX;
            //areas.get(areaIds.get(curAreaX + offX).get(curAreaY+offY).get(curAreaZ)).playerTileY = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).playerTileY+(areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).height-1)*offY;

            float pos = 0;
            int tileX=0, tileY=0;
            int speed=0;


            if (offZ == 0) {
                if (horizontal) {
                    pos = a.player.y + ((areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY).get(curAreaZ)).y+areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY).get(curAreaZ)).h) - (a.y+a.h))*firtsAreaHeight*a.TILE_HEIGHT;//a.player.hitBox.y;// - inRoomYCoord;
                    if (offX > 0) {
                        tileX = -1;
                    } else {
                        tileX = 1;
                    }
                    tileY = 0;
                    speed = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player.speedX;
                    areaTransitionX = 1.0f;
                } else {
                    pos = a.player.x - (areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY).get(curAreaZ)).x - a.x)*firtsAreaWidth*a.TILE_WIDTH;//a.player.hitBox.y;// - inRoomYCoord;
                    //pos = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player.hitBox.x;
                    if (offY > 0) {
                        tileY = -1;
                    } else {
                        tileY = 1;
                    }
                    tileX = 0;
                    speed = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player.speedY;
                    areaTransitionY = 1.0f;
                }
            } else {
                tileX = (int)a.player.x - (areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY).get(curAreaZ+offZ)).x - a.x)*firtsAreaWidth*a.TILE_WIDTH;
                tileY = (int)a.player.y + ((areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY).get(curAreaZ+offZ)).y+areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY).get(curAreaZ+offZ)).h) - (a.y+a.h))*firtsAreaHeight*a.TILE_HEIGHT;
                areaTransitionZ = 1.0f;
            }

            //areas.get(areaIds.get(curAreaX+offX).get(curAreaY + offY).get(curAreaZ + offZ)).worldObjectsHandler.setPlayer(areas.get(areaIds.get(curAreaX+offX).get(curAreaY + offY).get(curAreaZ + offZ)).player);
            if (offZ == 0) {
                areas.get(areaIds.get(curAreaX+offX).get(curAreaY + offY).get(curAreaZ + offZ)).respawnPlayer(worldDir.path(), assets, tileX, tileY, pos, speed, characterMaker);
            }
            else areas.get(areaIds.get(curAreaX+offX).get(curAreaY + offY).get(curAreaZ + offZ)).respawnPlayerZ(worldDir.path(), assets, tileX, tileY, characterMaker);



            oldAreaX = curAreaX;
            oldAreaY = curAreaY;
            oldAreaZ = curAreaZ;
            Area oldArea = areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ));
            oldArea.isCurrent = false;
            oldArea.worldObjectsHandler.invalidateObjects(folderPath, assets, this);
            int n = areaIds.get(oldAreaX+offX).get(oldAreaY+offY).get(oldAreaZ+offZ);
            Area curArea = areas.get(n);
            //curArea.invalidate(this);
            curArea.worldObjectsHandler.invalidateObjectCells();
            curArea.isCurrent = true;
            if (areasAmbients.get(n)!= null && areasAmbients.get(n).length() > 0 && (currentSoundPath == null || !currentSoundPath.equals(areasAmbients.get(n)))) {
                currentSound = assets.get(folderPath + "/sounds/" + areasAmbients.get(n), Sound.class);
                currentSoundId = currentSound.loop(menu.musicVolume/100.0f);
                startedAmbient = true;
            } else if (currentSound != null && (currentSoundPath == null || !currentSoundPath.equals(areasAmbients.get(n)))) {
                currentSound.stop();
                currentSound = null;
                currentSoundPath = null;
                //startedAmbient = false;
            }
            curAreaX = curArea.x;
            curAreaY = curArea.y;
            curAreaZ = curArea.z;
            //initialised = false;
            if (areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player != null) {
                Player player = curArea.player;
                if (player.characterMaker != null) {
                    player.characterMaker.setDirection(player.characterMaker.getDirection(player.charId), player.charId);
                }
                player.movingConfiguration.updateMoving(Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.UP, Input.Keys.DOWN, Input.Keys.SHIFT_LEFT, -1, Input.Keys.E);
                player.invalidatePose(true, true);
                player.inventory = oldArea.player.inventory;
                player.headWear = oldArea.player.headWear;
                player.bodyWear = oldArea.player.bodyWear;
                player.objectInHands = oldArea.player.objectInHands;
            }
            startedChanging = false;
        } else {
            Area curarea = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ));
            if ((curarea.loopingX && offX != 0) || (curarea.loopingY && offY != 0)){
                return;
            }
            if( (offX != 0 && !curarea.loopingX && (curAreaX+offX < 0 || curAreaX+offX > areaIds.size()-1 || areaIds.get(curAreaX + offX).get(curAreaY).get(curAreaZ) == -1)) || (offY != 0 && !curarea.loopingY && (curAreaY+offY < 0 || curAreaY+offY > areaIds.get(0).size()-1 || areaIds.get(curAreaX).get(curAreaY + offY).get(curAreaZ) == -1) && !curarea.loopingY)) {
                curarea.respawnPlayer(null, assets, 0, 0, 0, 0, characterMaker);
            }
        }
        /*if (id < areas.size()) {
            curArea = id;
        } else {
            initialised = false;
        }*/
        //saveState();
    }

    public void invalidate() {
        if (areaTransitionX == 0 && areaTransitionY == 0) {
            areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).invalidate(this);
        }
    }

    public Sound getSound(int id) {
        if (id < 0 || stepSounds.get(id) == null) {
            return assets.get(folderPath + "/sounds/default.wav", Sound.class);
        }
        if (stepSounds.get(id).contains(".")) {
            return assets.get(folderPath + "/sounds/" + stepSounds.get(id), Sound.class);
        }
        int r = (int)Math.floor(Math.random() * 3);
        return assets.get(folderPath + "/sounds/" + stepSounds.get(id) + "/" + r + ".wav", Sound.class);

    }

    public void synchronizeFlags() {
        for (int t = 0; t < areas.size(); ++t) {
            if (areas.get(t) == null) continue;
            Area a = areas.get(t);

            for (int i = 0; i < a.worldObjectsHandler.NPCs.size(); ++i) {
                for (int j = 0; j < a.worldObjectsHandler.NPCs.get(i).flags.size(); ++j) {
                    for (int z = 0; z < flagNames.size(); ++z) {
                        if (a.worldObjectsHandler.NPCs.get(i).flagNames.get(j).equals(flagNames.get(z))) {
                            a.worldObjectsHandler.NPCs.get(i).flags.set(j, flags.get(z));
                        }
                    }
                }
            }

        }
    }

    public void handleNPCTasks() {
        for (int t = 0; t < areas.size(); ++t) {
            if (t == areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)) continue;
            Area a = areas.get(t);
            ArrayList<NPC> npcs = a.worldObjectsHandler.NPCs;
            for (int i = 0; i < npcs.size(); ++i) {
                NPC npc = npcs.get(i);
                Exit pos = npc.handleOffscreenTasks();
                if (pos != null) {
                    if (pos.room != npc.curRoom) {
                        if (pos.direction == ExitDirection.EAST) {
                            npc.characterMaker.setDirection(1, npc.charId);
                            npc.dir = 1;
                        } else if (pos.direction == ExitDirection.SOUTH) {
                            npc.characterMaker.setDirection(2, npc.charId);
                            npc.dir = 2;
                        } else if (pos.direction == ExitDirection.WEST) {
                            npc.characterMaker.setDirection(3, npc.charId);
                            npc.dir = 3;
                        } else if (pos.direction == ExitDirection.NORTH) {
                            npc.characterMaker.setDirection(4, npc.charId);
                            npc.dir = 4;
                        }
                        npc.currentExitPath = null;
                        Area toArea = map.getAreaByName(pos.room.name);
                        a.worldObjectsHandler.deleteObjectCellsForEntity(npc);
                        npcs.remove(npc);
                        npc.x = pos.x - pos.offsetX * a.TILE_WIDTH;
                        npc.y = pos.y - pos.offsetY * a.TILE_HEIGHT/2 - 10;
                        if (pos.offsetX == 0) npc.x += 10;
                        else npc.y += 10;
                        npc.hitBox.x = npc.x;
                        npc.hitBox.y = npc.y;
                        toArea.worldObjectsHandler.addNPC(npc, this, -1);
                        npcsAreas.set(npc.charId, areas.indexOf(toArea));
                        npc.curRoom = map.getRoomByName(toArea.name);
                    }
                }
            }
        }
    }

    public void checkDialog(Area area) {
        if (menu.currentLanguage != area.worldObjectsHandler.currentDialog.language) {
            area.worldObjectsHandler.currentDialog.reload(menu.currentLanguage, area.worldObjectsHandler.currentDialog.currentSpeechId);
        }
    }

    public void checkInventory(Area area) {
        if (!menu.paused && area.worldObjectsHandler.currentInventory == null && Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            area.worldObjectsHandler.openInventory(menu, folderPath, assets, this);
        } else if (area.worldObjectsHandler.currentInventory != null) {
            if (menu.currentLanguage != area.worldObjectsHandler.currentInventory.language) {
                area.worldObjectsHandler.currentInventory.reload(menu.currentLanguage);
            } else if (area.worldObjectsHandler.currentInventory.droppedItem != null) {
                Entity itemGlow = new Entity(assets, "item.png", area.player.x, area.player.y, 0 ,0 ,0);
                //itemGlow.floor = true;
                itemGlow.containingItem = area.worldObjectsHandler.currentInventory.droppedItem;
                area.worldObjectsHandler.addNonSolid(itemGlow, -1);
                itemsOnFloor.add(itemGlow);
                itemsOnFloorAreas.add(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ));
                area.worldObjectsHandler.currentInventory.droppedItem = null;
            }
        }

    }

    public void draw(SpriteBatch batch) {
        if (currentSound != null) {
            currentSound.setVolume(currentSoundId, menu.musicVolume/100.0f);
        }
        Area curArea = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ));
        if (curArea.worldObjectsHandler.currentInventory == null) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.F6)) {
                saveState();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F9)) {
                loadState();
            }
        }
        if ((!menu.paused && curArea.worldObjectsHandler.currentDialog == null) || curArea.playerHidden) {
            checkAreaObjects(curArea);
        } else if (curArea.worldObjectsHandler.currentDialog != null) {
            checkDialog(curArea);
        } //else if (curArea.worldObjectsHandler.currentInventory != null) {
        curArea.worldObjectsHandler.invalidateObjects(folderPath, assets, this);
        synchronizeFlags();
        if (!menu.paused) handleNPCTasks();
        checkInventory(curArea);
        //}
        if (areaTransitionX == 0 && areaTransitionY == 0 && areaTransitionZ == 0) {
            if (areas.size() > areaIds.get(curAreaX).get(curAreaY).get(curAreaZ) && curArea != null) {
                curArea.draw(batch, this, 0, 0, true, true, true);
                for (int i = 0; i <= curArea.worldObjectsHandler.NPCs.size(); ++i) {
                    NPC npc;
                    if (i == curArea.worldObjectsHandler.NPCs.size()) {
                        npc = curArea.player;
                    } else {
                        npc = curArea.worldObjectsHandler.NPCs.get(i);
                    }
                    if (npc.changedFlags.size() > 0) {
                        for (int j = 0; j < npc.changedFlags.size(); ++j) {
                            if (flagNames.contains(npc.changedFlags.get(j))) {
                                flags.set(flagNames.indexOf(npc.changedFlags.get(j)), npc.flags.get(npc.flagNames.indexOf(npc.changedFlags.get(j))));
                            }
                        }
                        npc.changedFlags.clear();
                    }
                }
                if (!menu.paused && curArea.worldObjectsHandler.currentDialog == null) {
                    checkPlayerPosition();
                    checkSolidsPosition();
                }
            }
            if (curArea.worldObjectsHandler.currentDialog != null) {
                curArea.worldObjectsHandler.currentDialog.draw(batch, menu.drawPause);
                if (curArea.worldObjectsHandler.currentDialog.finished) {
                    ArrayList<String> changedFlagsNames = curArea.worldObjectsHandler.currentDialog.changedFlagsNames;
                    for (int i = 0; i < changedFlagsNames.size(); ++i) {
                        if (flagNames.contains(changedFlagsNames.get(i))) {
                            flags.set(flagNames.indexOf(changedFlagsNames.get(i)), curArea.worldObjectsHandler.currentDialog.changedFlagsVals.get(i));
                        }
                    }
                    curArea.worldObjectsHandler.currentDialog = null;
                    menu.dialogSkipping = false;
                    menu.drawPause = true;
                    menu.paused = false;
                    menu.unpausable = true;
                }
            }
            if (curArea.worldObjectsHandler.currentInventory != null) {
                curArea.worldObjectsHandler.currentInventory.draw(batch, menu.drawPause);
                if (curArea.worldObjectsHandler.currentInventory.closed) {
                    if (curArea.worldObjectsHandler.currentInventory.containerMode) {
                        curArea.worldObjectsHandler.activeObject.activate(folderPath, assets, flagNames, flags, curArea, 0, menu, true);
                    }
                    curArea.worldObjectsHandler.currentInventory = null;
                    menu.drawPause = true;
                    menu.paused = false;
                    menu.unpausable = true;
                }
            }
        } else {
            if (!startedChanging) {
                //curArea.draw(batch, this, 0, 0, true, true, true);
                curArea.invalidate(this);
                startedChanging = true;
            }

            Area area1, area2;
            Area oldArea = areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ));
            if (oldAreaX + areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ)).w-1 < curAreaX || oldAreaY > curAreaY + curArea.h-1) {
                area1 = oldArea;
                area2 = curArea;
            } else {
                area2 = oldArea;
                area1 = curArea;
            }
            boolean freeHorCamera1 = !platformMode || (area1.width * area1.TILE_WIDTH * area1.zoom < area1.camera.getWidth());
            boolean freeVerCamera1 = !platformMode || (area1.height * area1.TILE_HEIGHT * area1.zoom < area1.camera.getHeight());
            boolean freeHorCamera2 = !platformMode || (area2.width * area2.TILE_WIDTH * area2.zoom < area2.camera.getWidth());
            boolean freeVerCamera2 = !platformMode || (area2.height * area2.TILE_HEIGHT * area2.zoom < area2.camera.getHeight());
            if (areaTransitionZ != 0) {
                if (areaTransitionZ > 0.2f) {
                    oldArea.draw(batch, this, 0, 0, true, true, true);
                    batch.setColor(0, 0, 0, 1.25f * ((1.0f - areaTransitionZ)));
                    batch.draw((Texture) assets.get("blank.png"), 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                    batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                    areaTransitionZ /= 1.1f;
                } else {
                    curArea.draw(batch, this, 0, 0, true, true, true);
                    batch.setColor(0, 0, 0, areaTransitionZ*5);
                    batch.draw((Texture)assets.get("blank.png"), 0 ,0 ,SCREEN_WIDTH, SCREEN_HEIGHT );
                    batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                    areaTransitionZ /= 1.1f;
                }
                if (Math.abs(areaTransitionZ) < 0.01f && areaTransitionZ != 0) {
                    areaTransitionZ = 0;
                    oldArea.removeParticles();
                    oldArea.resetCheckPoints();
                }
            } else if (areaTransitionX != 0) {
                if (oldAreaX < curAreaX) {
                    oldArea.cameraY = curArea.cameraY + (area1.y+area1.h-area2.y-area2.h)*firtsAreaHeight*area1.TILE_HEIGHT;
                    //float freeOff = 0;
                    //if (freeHorCamera1) freeOff = /*area1.zoom **/ area1.camera.getWidth()/2/area1.zoom;
                    if (freeHorCamera1 || freeHorCamera2) {
                        if (platformMode) {
                            area2.draw(batch, this, area1.TILE_WIDTH*(areaTransitionX), 0, true, true, true);
                            area1.draw(batch, this, -area1.TILE_WIDTH*(1.0f-areaTransitionX), 0, false, false, true);
                        } else {
                            area1.cameraX = area1.width * area1.TILE_WIDTH - area2.cameraX + 13;
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, areaTransitionX * 2.0f));
                            area1.draw(batch, this, -1, 0, false, true, false);
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 2.0f - areaTransitionX * 2.0f));
                            area2.draw(batch, this, 0, 0, true, false, false);
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                        }
                    } else {
                        area2.draw(batch, this, SCREEN_WIDTH /area2.zoom*(areaTransitionX), 0, true, true, true);
                        area1.draw(batch, this, SCREEN_WIDTH/area1.zoom*(-1.0f+areaTransitionX)/*-1*/, 0, false, false, true);
                    }
                } else {
                    oldArea.cameraY = curArea.cameraY - (area1.y+area1.h-area2.y-area2.h)*firtsAreaHeight*area1.TILE_HEIGHT;
                    if (freeHorCamera1 || freeHorCamera2) {
                        if (platformMode) {
                            area2.draw(batch, this, area1.TILE_WIDTH*(1.0f-areaTransitionX), 0, false, true, true);
                            area1.draw(batch, this, -area1.TILE_WIDTH*(areaTransitionX), 0, true, false, true);
                        } else {
                            area2.cameraX = 12;
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, areaTransitionX * 2.0f));
                            area2.draw(batch, this, 0, 0, false, true, false);
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 2.0f - areaTransitionX * 2.0f));
                            area1.draw(batch, this, -4, 0, true, false, false);
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                        }
                    } else {
                        area2.draw(batch, this, SCREEN_WIDTH/area2.zoom*(1.0f-areaTransitionX)/*+1*//* - (area2.w-1)*firtsAreaWidth*area2.TILE_WIDTH*/, 0, false, true, true);
                        area1.draw(batch, this, SCREEN_WIDTH/area1.zoom*(-areaTransitionX), 0, true, false, true);
                    }
                }
            } else {

                int off = 2;
                float add = area1.FLOOR_HEIGHT+2;
                int off2 = 4;
                if (platformMode) {
                    off = 1;
                    add = 0;
                    off2 = 0;
                }
                if (oldAreaY < curAreaY) {
                    oldArea.cameraX = curArea.cameraX + (area1.x-area2.x)*firtsAreaWidth*area1.TILE_WIDTH;
                    if (freeVerCamera1 || freeVerCamera2 ) {
                        //area2.cameraY = 12;
                        if (platformMode) {

                        }else {
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 2.0f - areaTransitionY * 2.0f));
                            area1.draw(batch, this, 0, 0, true, true, false);
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, areaTransitionY * 2.0f));
                            area2.draw(batch, this, 0, -6, false, false, false);
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                        }
                    } else {
                        area1.draw(batch, this, 0, (SCREEN_HEIGHT +off*area1.TILE_HEIGHT+add)/area1.zoom*(areaTransitionY) /*+ area1.FLOOR_HEIGHT+2*/, true, true, true);
                        area2.draw(batch, this, 0, (SCREEN_HEIGHT+off*area1.TILE_HEIGHT+add)/area2.zoom*(1.0f - areaTransitionY)-off2, false, false, true);
                    }
                } else {
                    if (freeVerCamera1 || freeVerCamera2) {
                        if (platformMode) {

                        }else {
                            area1.cameraY = area1.height * area1.TILE_HEIGHT - area2.cameraY - 5;
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, areaTransitionY * 2.0f));
                            area1.draw(batch, this, 0, 3, false, true, false);
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 2.0f - areaTransitionY * 2.0f));
                            area2.draw(batch, this, 0, 0, true, false, false);
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                        }
                    } else {
                        oldArea.cameraX = curArea.cameraX - (area1.x-area2.x)*firtsAreaWidth*area1.TILE_WIDTH;
                        area1.draw(batch, this, /*(area1.x-area2.x)*firtsAreaWidth*area1.TILE_WIDTH*/0, (SCREEN_HEIGHT+off*area1.TILE_HEIGHT+add)/area1.zoom*(1.0f-areaTransitionY)+off2/* + area1.FLOOR_HEIGHT+2*/, false, true, true);
                        area2.draw(batch, this, 0, (SCREEN_HEIGHT+off*area1.TILE_HEIGHT+add)/area2.zoom*(-areaTransitionY), true, false, true);
                    }
                }
            }
            if (!platformMode) {
                areaTransitionY -= 0.05f;
                areaTransitionX -= 0.05f;
            } else {
                areaTransitionY /= 1.2f;
                areaTransitionX /= 1.2f;
            }

            if (areaTransitionX < 0.001f && areaTransitionX != 0) {
                areaTransitionX = 0;
                areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ)).removeParticles();
                areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ)).resetCheckPoints();
            }
            if (areaTransitionY < 0.001f && areaTransitionY != 0) {
                areaTransitionY = 0;
                areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ)).removeParticles();
                areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ)).resetCheckPoints();
            }
            //System.out.println(areaTransitionX);
        }
    }

}
