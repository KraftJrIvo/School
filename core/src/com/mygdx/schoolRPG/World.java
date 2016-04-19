package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.schoolRPG.tools.AnimationSequence;
import com.mygdx.schoolRPG.tools.CharacterMaker;
import java.io.*;
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
    FileHandle worldDir = null;
    int curAreaX = 0, curAreaY = 0, curAreaZ = 0, oldAreaX = 0, oldAreaY = 0, oldAreaZ = 0;
    float areaTransitionX = 0, areaTransitionY = 0, areaTransitionZ = 0;
    String name;
    boolean platformMode = false;
    int width, height, areaWidth, areaHeight, tileWidth, tileHeight;
    CharacterMaker characterMaker;
    ArrayList<Texture> sprites;
    ArrayList<BlockMultiTile> tiles;
    ArrayList<AnimationSequence> animations;
    ArrayList<String> names;
    ArrayList<String> newNames;
    ArrayList<Integer> tileTypes;
    ArrayList<Integer> tileIndices;
    int spritesCount = 0, tilesetsCount = 0;
    Texture bg;
    AssetManager assets;
    int firtsAreaWidth;
    int firtsAreaHeight;
    ShapeRenderer shapeRenderer;
    int animsLoaded = 0;


    public World(String folderPath, int size, int startingAreaX, int startingAreaY, int startingAreaZ) {
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
    }

    public World(String worldPath) {
        areas = new ArrayList<Area>();
        folderPath = worldPath;
        tlw = new File(worldPath+"/world.tlw");

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
            preTileTypes.set(i, tileTypes.get(names.indexOf(newNames.get(i))));
            preTileIndices.set(i, tileIndices.get(names.indexOf(newNames.get(i))));
            preNames.set(i, newNames.get(i));
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

    public void load(AssetManager assets) {
        characterMaker = new CharacterMaker(assets);
        characterMaker.cdc.setLookingForward(true);

        this.assets = assets;

        assets.load("particles/test/1.png", Texture.class);
        assets.load("particles/skull/1.png", Texture.class);
        assets.load("particles/body/1.png", Texture.class);
        assets.load("particles/bone/1.png", Texture.class);
        assets.load("particles/blood/1.png", Texture.class);
        assets.load("particles/test/2.png", Texture.class);
        assets.load("particles/shadow.png", Texture.class);
        assets.load("particles/water/1.png", Texture.class);
        assets.load("particles/water/2.png", Texture.class);
        assets.load("particles/goo/1.png", Texture.class);
        assets.load("particles/goo/2.png", Texture.class);

        assets.load("blank.png", Texture.class);
        assets.load("blank2.png", Texture.class);

        assets.load(folderPath + "/bg.png", Texture.class);

        assets.load("worlds/platform_new/sprites/chargo.png", Texture.class);
        assets.load("worlds/platform_new/sprites/save1.png", Texture.class);
        assets.load("worlds/platform_new/sprites/save2.png", Texture.class);
        //if (platformMode) {

        //}

        if (tlw == null) {
            worldDir = Gdx.files.internal(folderPath);
            int curX,curY,curZ=0;
            int count = 0;
            for (FileHandle entry: worldDir.list()) {
                if (entry.file().getName().equals("Thumbs.db") || entry.file().isDirectory() || entry.file().getName().equals("bg.png")){
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
                areaWidth = fis.read();
                areaHeight = fis.read();
                firtsAreaWidth = 7;//Math.min(areaWidth, areaHeight);
                firtsAreaHeight = 5;//Math.min(areaWidth, areaHeight);

                int curCoordX = fis.read();
                int curCoordY = fis.read();
                int curCoordZ = fis.read();
                //int endChecker = 0;
                curAreaX = curCoordX;
                curAreaY = curCoordY;
                curAreaZ = curCoordZ;
                while (fis.available() > 1) {

                    buff = new byte[areaWidth*areaHeight*7];
                    fis.read(buff);
                    if (curCoordX != 246) {
                        areas.add(new Area(curCoordX, curCoordY, curCoordZ, areaWidth/firtsAreaWidth, areaHeight/firtsAreaHeight, buff, areaWidth, areaHeight, tileWidth, tileHeight, platformMode));
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
            worldDir = Gdx.files.internal(folderPath);

            for (FileHandle entry: worldDir.list()) {
                if (entry.file().getName().equals("Thumbs.db") || entry.file().isDirectory() || entry.file().getName().equals("world.tlw")){
                    continue;
                }
                assets.load(entry.path(), Texture.class);
                names.add(entry.name().substring(0, entry.name().length() - 4));
                tileTypes.add(0);
                tileIndices.add(spritesCount);
                spritesCount++;
            }
            worldDir = Gdx.files.internal(folderPath+"/tiles");
            for (FileHandle entry: worldDir.list()) {
                if (entry.file().getName().equals("Thumbs.db") || entry.file().isDirectory()){
                    continue;
                }
                assets.load(entry.path(), Texture.class);
                names.add("tiles\\" + entry.name().substring(0, entry.name().length() - 4));
                tileTypes.add(1);
                tileIndices.add(tilesetsCount);
                tilesetsCount++;
            }
            worldDir = Gdx.files.internal(folderPath+"/anim");
            for (FileHandle entry: worldDir.list()) {
                if (entry.file().getName().equals("Thumbs.db") || entry.file().isDirectory()){
                    continue;
                }
                assets.load(entry.path(), Texture.class);
                names.add("anim\\" + entry.name().substring(0, entry.name().length() - 4));
                tileTypes.add(2);
                tileIndices.add(animsLoaded);
                animsLoaded++;
            }
            worldDir = Gdx.files.internal(folderPath);
            updateTiles();
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
        characterMaker.initialiseResources(assets);
        bg = assets.get(folderPath+"/bg.png", Texture.class);
        shapeRenderer = new ShapeRenderer();
        if (!initialised && !areasCreated) {
            if (tlw == null) {
                for (FileHandle entry: worldDir.list()) {
                    if (entry.file().getName().equals("Thumbs.db") || entry.file().isDirectory() || entry.file().getName().equals("bg.png")){
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
                    if (entry.file().getName().equals("Thumbs.db") || entry.file().isDirectory() || entry.file().getName().equals("world.tlw")){
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
                    if (entry.file().getName().equals("Thumbs.db") || entry.file().isDirectory()){
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
                    if (entry.file().getName().equals("Thumbs.db") || entry.file().isDirectory()){
                        continue;
                    }
                    /*for (int i = 0; i < names.size(); ++i) {
                        if (("anim\\" + entry.name().substring(0, entry.name().length() - 4)).equals(names.get(i))) {
                            tileIndices.set(i, animations.size());
                            tileTypes.set(i, 2);
                        }
                    }*/
                    animations.add(new AnimationSequence(assets, entry.path(), 12, true, 4));
                }
                worldDir = Gdx.files.internal(folderPath);
            }
        }
        //System.out.println(areas.size() + " " + areaIds.size());
        if (areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).initialised) {
            initialised = true;
        } else {
            areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).initialiseResources(assets, this, characterMaker);
        }
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            int c = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).chekZPath();
            if (c > 0) {
                changeArea(false, inRoomXCoord, inRoomYCoord, c);
            } else if (c < 0) {
                changeArea(false, inRoomXCoord, inRoomYCoord, c);
            }
        }
        if (a.player.z > a.cameraY + SCREEN_HEIGHT) {
            if (curAreaZ >= 1 && areaIds.get(curAreaX).get(curAreaY).get(curAreaZ-1) != -1) {
                changeArea(false, inRoomXCoord, inRoomYCoord, -1);
            }
            else a.respawnPlayer(null, assets, 0, 0, 0, 0, null);
        }
    }

    private void changeArea(boolean horizontal, int offX, int offY, int offZ) {
        if (curAreaX+offX >= 0 && curAreaX+offX <= areaIds.size()-1 && curAreaY+offY >= 0 && curAreaY+offY <= areaIds.get(0).size()-1 && areaIds.get(curAreaX + offX).get(curAreaY + offY).get(curAreaZ) != -1) {

            //areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).resetCheckPoints();
            //areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY)).player.x = areas.get(areaIds.get(curAreaX).get(curAreaY)).player.x - areas.get(areaIds.get(curAreaX).get(curAreaY)).TILE_WIDTH*offX;
            //areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY)).player.y = areas.get(areaIds.get(curAreaX).get(curAreaY)).player.y - areas.get(areaIds.get(curAreaX).get(curAreaY)).TILE_HEIGHT*offY;
            //areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY)).resetCamera();
            Area a = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ));
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
            curAreaX = areas.get(areaIds.get(oldAreaX+offX).get(oldAreaY+offY).get(oldAreaZ+offZ)).x;
            curAreaY = areas.get(areaIds.get(oldAreaX+offX).get(oldAreaY+offY).get(oldAreaZ+offZ)).y;
            curAreaZ = areas.get(areaIds.get(oldAreaX+offX).get(oldAreaY+offY).get(oldAreaZ+offZ)).z;
            initialised = false;
            if (areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player != null) areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player.invalidatePose(true, true);
        } else {
            areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).respawnPlayer(null, assets, 0, 0, 0, 0, characterMaker);
        }
        /*if (id < areas.size()) {
            curArea = id;
        } else {
            initialised = false;
        }*/
    }

    public void invalidate() {
        if (areaTransitionX == 0 && areaTransitionY == 0) {
            areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).invalidate();
        }
    }

    public void draw(SpriteBatch batch) {
        if (areaTransitionX == 0 && areaTransitionY == 0 && areaTransitionZ == 0) {
            if (areas.size() > areaIds.get(curAreaX).get(curAreaY).get(curAreaZ) && areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)) != null) {
                areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).draw(batch, this, 0, 0, true, true, true);
                checkPlayerPosition();
            }
        } else {
            Area area1, area2;
            if (oldAreaX + areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ)).w-1 < curAreaX || oldAreaY > curAreaY + areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).h-1) {
                area1 = areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ));
                area2 = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ));
            } else {
                area2 = areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ));
                area1 = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ));
            }
            boolean freeHorCamera1 = (area1.width * area1.TILE_WIDTH * area1.zoom < area1.camera.getWidth());
            boolean freeVerCamera1 = (area1.height * area1.TILE_HEIGHT * area1.zoom < area1.camera.getHeight());
            boolean freeHorCamera2 = (area2.width * area2.TILE_WIDTH * area2.zoom < area2.camera.getWidth());
            boolean freeVerCamera2 = (area2.height * area2.TILE_HEIGHT * area2.zoom < area2.camera.getHeight());
            if (areaTransitionZ != 0) {
                if (areaTransitionZ > 0.2f) {
                    areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ)).draw(batch, this, 0, 0, true, true, true);
                    batch.setColor(0, 0, 0, 1.25f * ((1.0f - areaTransitionZ)));
                    batch.draw((Texture) assets.get("blank.png"), 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                    batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                    areaTransitionZ /= 1.1f;
                } else {
                    areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).draw(batch, this, 0, 0, true, true, true);
                    batch.setColor(0, 0, 0, areaTransitionZ*5);
                    batch.draw((Texture)assets.get("blank.png"), 0 ,0 ,SCREEN_WIDTH, SCREEN_HEIGHT );
                    batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                    areaTransitionZ /= 1.1f;
                }
                if (Math.abs(areaTransitionZ) < 0.01f && areaTransitionZ != 0) {
                    areaTransitionZ = 0;
                    areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ)).removeParticles();
                    areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ)).resetCheckPoints();
                }
            } else if (areaTransitionX != 0) {
                if (oldAreaX < curAreaX) {
                    areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ)).cameraY = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).cameraY + (area1.y+area1.h-area2.y-area2.h)*firtsAreaHeight*area1.TILE_HEIGHT;
                    //float freeOff = 0;
                    //if (freeHorCamera1) freeOff = /*area1.zoom **/ area1.camera.getWidth()/2/area1.zoom;
                    if (freeHorCamera1 || freeHorCamera2) {
                        area1.cameraX = area1.width * area1.TILE_WIDTH - area2.cameraX + 13;
                        batch.setColor(new Color(1.0f, 1.0f, 1.0f, areaTransitionX * 2.0f));
                        area1.draw(batch, this, -1, 0, false, true, false);
                        batch.setColor(new Color(1.0f, 1.0f, 1.0f, 2.0f - areaTransitionX * 2.0f));
                        area2.draw(batch, this, 0, 0, true, false, false);
                        batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                    } else {
                        area2.draw(batch, this, SCREEN_WIDTH /area2.zoom*(areaTransitionX), 0, true, true, true);
                        area1.draw(batch, this, SCREEN_WIDTH/area1.zoom*(-1.0f+areaTransitionX)-1, 0, false, false, true);
                    }
                } else {
                    areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ)).cameraY = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).cameraY - (area1.y+area1.h-area2.y-area2.h)*firtsAreaHeight*area1.TILE_HEIGHT;
                    if (freeHorCamera1 || freeHorCamera2) {
                        area2.cameraX = 12;
                        batch.setColor(new Color(1.0f, 1.0f, 1.0f, areaTransitionX * 2.0f));
                        area2.draw(batch, this, 0, 0, false, true, false);
                        batch.setColor(new Color(1.0f, 1.0f, 1.0f, 2.0f - areaTransitionX * 2.0f));
                        area1.draw(batch, this, -4, 0, true, false, false);
                        batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                    } else {
                        area2.draw(batch, this, SCREEN_WIDTH/area2.zoom*(1.0f-areaTransitionX)+1/* - (area2.w-1)*firtsAreaWidth*area2.TILE_WIDTH*/, 0, false, true, true);
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
                    areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ)).cameraX = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).cameraX + (area1.x-area2.x)*firtsAreaWidth*area1.TILE_WIDTH;
                    if (freeVerCamera1 || freeVerCamera2) {
                        //area2.cameraY = 12;
                        batch.setColor(new Color(1.0f, 1.0f, 1.0f, 2.0f - areaTransitionY * 2.0f));
                        area1.draw(batch, this, 0, 0, false, true, false);
                        batch.setColor(new Color(1.0f, 1.0f, 1.0f, areaTransitionY * 2.0f));
                        area2.draw(batch, this, 0, -6, true, false, false);
                        batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                    } else {
                        area1.draw(batch, this, 0, (SCREEN_HEIGHT +off*area1.TILE_HEIGHT+add)/area1.zoom*(areaTransitionY) /*+ area1.FLOOR_HEIGHT+2*/, true, true, true);
                        area2.draw(batch, this, 0, (SCREEN_HEIGHT+off*area1.TILE_HEIGHT+add)/area2.zoom*(1.0f - areaTransitionY)-off2, false, false, true);
                    }
                } else {
                    if (freeVerCamera1 || freeVerCamera2) {
                        area1.cameraY = area1.height * area1.TILE_HEIGHT - area2.cameraY - 5;
                        batch.setColor(new Color(1.0f, 1.0f, 1.0f, areaTransitionY * 2.0f));
                        area1.draw(batch, this, 0, 3, false, true, false);
                        batch.setColor(new Color(1.0f, 1.0f, 1.0f, 2.0f - areaTransitionY * 2.0f));
                        area2.draw(batch, this, 0, 0, true, false, false);
                        batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                    } else {
                        areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ)).cameraX = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).cameraX - (area1.x-area2.x)*firtsAreaWidth*area1.TILE_WIDTH;
                        area1.draw(batch, this, /*(area1.x-area2.x)*firtsAreaWidth*area1.TILE_WIDTH*/0, (SCREEN_HEIGHT+off*area1.TILE_HEIGHT+add)/area1.zoom*(1.0f-areaTransitionY)+off2/* + area1.FLOOR_HEIGHT+2*/, false, true, true);
                        area2.draw(batch, this, 0, (SCREEN_HEIGHT+off*area1.TILE_HEIGHT+add)/area2.zoom*(-areaTransitionY), true, false, true);
                    }
                }
            }
            if (freeHorCamera1 || freeHorCamera2 || freeVerCamera1 || freeVerCamera2) {
                areaTransitionY -= 0.05f;
                areaTransitionX -= 0.05f;
            } else {
                areaTransitionY /= 1.1f;
                areaTransitionX /= 1.1f;
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
