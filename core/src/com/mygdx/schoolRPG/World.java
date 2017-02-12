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
    ArrayList<Boolean> flags;
    ArrayList<String> flagNames;
    ArrayList<Texture> sprites;
    ArrayList<BlockMultiTile> tiles;
    ArrayList<AnimationSequence> animations;
    ArrayList<String> names;
    ArrayList<String> newNames;
    ArrayList<Integer> tileTypes;
    ArrayList<Integer> tileIndices;
    ArrayList<String> stepSounds;
    ArrayList<String> areasAmbients;
    ArrayList<ParticleProperties> particles;
    int spritesCount = 0, tilesetsCount = 0;
    Texture bg;
    AssetManager assets;
    int firtsAreaWidth;
    int firtsAreaHeight;
    ShapeRenderer shapeRenderer;
    int animsLoaded = 0;
    GameMenu menu;
    Sound currentSound;
    long currentSoundId = -1;
    String currentSoundPath;
    boolean startedAmbient = false;

    public World(GameMenu menu, String folderPath, int size, int startingAreaX, int startingAreaY, int startingAreaZ) {
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
    }

    public World(GameMenu menu, String worldPath) {
        this.menu = menu;
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

        this.assets = assets;
        stepSounds = new ArrayList<String>();
        areasAmbients = new ArrayList<String>();
        assets.load("item.png", Texture.class);
        assets.load("active.png", Texture.class);
        assets.load("blank.png", Texture.class);
        assets.load("blank2.png", Texture.class);
        assets.load("shadow.png", Texture.class);
        assets.load("prt_shadow.png", Texture.class);
        assets.load("inventory_overlay1.png", Texture.class);

        assets.load(folderPath + "/bg.png", Texture.class);
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
                while (fis.available() > 1) {

                    buff = new byte[areaWidth*areaHeight*7];
                    fis.read(buff);
                    if (curCoordX != 246) {
                        areas.add(new Area(curCoordX, curCoordY, curCoordZ, areaWidth/firtsAreaWidth, areaHeight/firtsAreaHeight, buff, areaWidth, areaHeight, tileWidth, tileHeight, platformMode, this));
                        areasAmbients.add(null);
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
            int numAmbients = Integer.parseInt(in.readLine());
            for (int i = 0; i < numAmbients; ++i) {
                int x = Integer.parseInt(in.readLine());
                int y = Integer.parseInt(in.readLine());
                int z = Integer.parseInt(in.readLine());
                int n = areaIds.get(x).get(y).get(z);
                areasAmbients.set(n, in.readLine());
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
        bg = assets.get(folderPath+"/bg.png", Texture.class);
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
                    animations.add(new AnimationSequence(assets, entry.path(), 12, true, 4));
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
        }
        int n = areaIds.get(curAreaX).get(curAreaY).get(curAreaZ);
        areas.get(n).isCurrent = true;
        if (areasAmbients.get(n) != null) {
            currentSoundPath = areasAmbients.get(n);
            currentSound = assets.get(folderPath + "/sounds/" + areasAmbients.get(n), Sound.class);
            currentSoundId = currentSound.loop(menu.musicVolume/100.0f);
            startedAmbient = true;
        }
    }

    private void checkSolidsPosition() {
        Area a = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ));
        for (int i = 0; i < a.worldObjectsHandler.solids.size(); ++i) {
            HittableEntity solid = a.worldObjectsHandler.solids.get(i);
            if (solid.movable) {
                int inRoomXCoord = (int)Math.floor(solid.x/a.TILE_WIDTH/firtsAreaWidth);
                int inRoomYCoord = (int)(Math.floor(a.height/firtsAreaHeight-(solid.y+solid.hitBox.getHeight()/2)/a.TILE_HEIGHT/firtsAreaHeight));
                int offX = 0, offY = 0, offZ = 0;
                boolean horizontal = false;
                if (solid.hitBox.x < 5) {
                    offX = -1;
                    offY = inRoomYCoord;
                    horizontal = true;
                } else if (solid.hitBox.x > a.TILE_WIDTH*(a.width)-a.player.hitBox.getWidth()-5) {
                    offX = inRoomXCoord + 1;
                    offY = inRoomYCoord;
                    horizontal = true;
                } else if (solid.hitBox.y+solid.hitBox.getHeight()/2 < -solid.hitBox.getHeight()/2) {
                    offX = inRoomXCoord;
                    offY = inRoomYCoord;
                } else if (solid.hitBox.y > a.TILE_HEIGHT*(a.height-1)) {
                    offX = inRoomXCoord;
                    offY = -1;
                } else {
                    continue;
                }
                ObjectCell cell = a.worldObjectsHandler.removeSolid(i);
                Area toArea = areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY).get(curAreaZ+offZ));
                float pos = 0;
                if (horizontal) {
                    pos = solid.hitBox.y + ((toArea.y+toArea.h) - (a.y+a.h)) * firtsAreaHeight * a.TILE_HEIGHT;
                    if (offX > 0) {
                        solid.hitBox.x = toArea.TILE_WIDTH;
                    } else {
                        solid.hitBox.x = (toArea.width - 2) * toArea.TILE_WIDTH ;
                    }
                    solid.hitBox.y = pos;
                } else {
                    pos = solid.hitBox.x - (areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY).get(curAreaZ)).x - a.x)*firtsAreaWidth*a.TILE_WIDTH;//a.player.hitBox.y;// - inRoomYCoord;
                    if (offY > 0) {
                        solid.hitBox.y = (toArea.height - 2) * toArea.TILE_HEIGHT - solid.hitBox.height;
                    } else {
                        solid.hitBox.y = toArea.TILE_HEIGHT;
                    }
                    solid.hitBox.x = pos;
                }
                toArea.worldObjectsHandler.addSolid(solid, cell.currentState, cell.items);
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
            int c = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).chekZPath();
            if (c > 0) {
                changeArea(false, inRoomXCoord, inRoomYCoord, c);
            } else if (c < 0) {
                changeArea(false, inRoomXCoord, inRoomYCoord, c);
            }
            a.worldObjectsHandler.activateActiveObject(menu, folderPath, assets, this);
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
            Area oldArea = areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ));
            oldArea.isCurrent = false;
            oldArea.worldObjectsHandler.invalidateObjects(folderPath, assets, this);
            int n = areaIds.get(oldAreaX+offX).get(oldAreaY+offY).get(oldAreaZ+offZ);
            Area curArea = areas.get(n);
            curArea.isCurrent = true;
            if (areasAmbients.get(n)!= null && (currentSoundPath == null || !currentSoundPath.equals(areasAmbients.get(n)))) {
                currentSound = assets.get(folderPath + "/sounds/" + areasAmbients.get(n), Sound.class);
                currentSoundId = currentSound.loop(menu.musicVolume/100.0f);
                startedAmbient = true;
            } else if (currentSound != null) {
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
                player.characterMaker.setDirection(player.characterMaker.getDirection(player.charId), player.charId);
                player.movingConfiguration.updateMoving(Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.UP, Input.Keys.DOWN, Input.Keys.SHIFT_LEFT, -1, Input.Keys.E);
                player.invalidatePose(true, true);
                player.inventory = oldArea.player.inventory;
                player.headWear = oldArea.player.headWear;
                player.bodyWear = oldArea.player.bodyWear;
                player.objectInHands = oldArea.player.objectInHands;
            }
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

    public void checkDialog(Area area) {
        if (menu.currentLanguage != area.worldObjectsHandler.currentDialog.language) {
            area.worldObjectsHandler.currentDialog.reload(area.worldObjectsHandler.getActiveDialogPath(menu, folderPath), menu.currentLanguage, area.worldObjectsHandler.currentDialog.getCurrentSpeechId());
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
                area.worldObjectsHandler.currentInventory.droppedItem = null;
            }
        }

    }

    public void draw(SpriteBatch batch) {
        if (currentSound != null) {
            currentSound.setVolume(currentSoundId, menu.musicVolume/100.0f);
        }
        Area curArea = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ));
        if ((!menu.paused && curArea.worldObjectsHandler.currentDialog == null) || curArea.playerHidden) {
            checkAreaObjects(curArea);
        } else if (curArea.worldObjectsHandler.currentDialog != null) {
            checkDialog(curArea);
        } //else if (curArea.worldObjectsHandler.currentInventory != null) {
        curArea.worldObjectsHandler.invalidateObjects(folderPath, assets, this);
        synchronizeFlags();
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
            Area area1, area2;
            Area oldArea = areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ));
            if (oldAreaX + areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ)).w-1 < curAreaX || oldAreaY > curAreaY + curArea.h-1) {
                area1 = oldArea;
                area2 = curArea;
            } else {
                area2 = oldArea;
                area1 = curArea;
            }
            boolean freeHorCamera1 = (area1.width * area1.TILE_WIDTH * area1.zoom < area1.camera.getWidth());
            boolean freeVerCamera1 = (area1.height * area1.TILE_HEIGHT * area1.zoom < area1.camera.getHeight());
            boolean freeHorCamera2 = (area2.width * area2.TILE_WIDTH * area2.zoom < area2.camera.getWidth());
            boolean freeVerCamera2 = (area2.height * area2.TILE_HEIGHT * area2.zoom < area2.camera.getHeight());
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
                    oldArea.cameraY = curArea.cameraY - (area1.y+area1.h-area2.y-area2.h)*firtsAreaHeight*area1.TILE_HEIGHT;
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
                    oldArea.cameraX = curArea.cameraX + (area1.x-area2.x)*firtsAreaWidth*area1.TILE_WIDTH;
                    if (freeVerCamera1 || freeVerCamera2) {
                        //area2.cameraY = 12;
                        batch.setColor(new Color(1.0f, 1.0f, 1.0f, 2.0f - areaTransitionY * 2.0f));
                        area1.draw(batch, this, 0, 0, true, true, false);
                        batch.setColor(new Color(1.0f, 1.0f, 1.0f, areaTransitionY * 2.0f));
                        area2.draw(batch, this, 0, -6, false, false, false);
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
                        oldArea.cameraX = curArea.cameraX - (area1.x-area2.x)*firtsAreaWidth*area1.TILE_WIDTH;
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
