package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.schoolRPG.tools.CharacterMaker;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by user on 06.08.2014.
 */
public class World {
//LOL
    ArrayList<Area> areas;
    ArrayList<ArrayList<ArrayList<Integer>>> areaIds;
    String folderPath, tlwPath;
    ArrayList<Pixmap> maps;
    File tlw;
    FileInputStream fis;
    public boolean initialised = false, loaded = false, areasCreated = false;
    FileHandle worldDir = null;
    int curAreaX = 0, curAreaY = 0, curAreaZ = 0, oldAreaX = 0, oldAreaY = 0, oldAreaZ = 0;
    ArrayList<Integer> crds;
    //Area loadArea;
    float areaTransitionX = 0, areaTransitionY = 0;
    String name;
    boolean platformMode = false;
    int width, height, areaWidth, areaHeight, tileWidth, tileHeight, playerWidth, playerHeight;
    CharacterMaker characterMaker;
    ArrayList<Texture> sprites;
    ArrayList<BlockMultiTile> tiles;
    int spritesCount = 0;
    Texture bg;
    AssetManager assets;


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
        //loadArea = new Area(0, null);
    }

    public World(String worldPath) {
        areas = new ArrayList<Area>();
        //maps = new ArrayList<Pixmap>();
        folderPath = worldPath;
        tlw = new File(worldPath+"/world.tlw");

    }

    public void load(AssetManager assets) {
        characterMaker = new CharacterMaker(assets);
        characterMaker.cdc.setLookingForward(true);

        this.assets = assets;

        assets.load("particles/test/1.png", Texture.class);
        assets.load("particles/test/2.png", Texture.class);
        assets.load("particles/shadow.png", Texture.class);

        assets.load(folderPath + "/bg.png", Texture.class);

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
                areaWidth = fis.read();
                areaHeight = fis.read();
                tileWidth = fis.read();
                tileHeight = fis.read();

                int curCoordX = fis.read();
                int curCoordY = fis.read();
                int curCoordZ = fis.read();
                //int endChecker = 0;
                curAreaX = curCoordX;
                curAreaY = curCoordY;
                curAreaZ = curCoordZ;
                while (fis.available() > 1) {

                    buff = new byte[areaWidth*areaHeight*4];
                    fis.read(buff);
                    areas.add(new Area(this, buff, areaWidth, areaHeight, tileWidth, tileHeight, platformMode));
                    areaIds.get(curCoordX).get(curCoordY).set(curCoordZ, areas.size() - 1);
                    //fis.skip(areaWidth*areaHeight*4);
                    if (fis.available() > 0) {
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
                spritesCount++;
            }
            worldDir = Gdx.files.internal(folderPath+"/tiles");
            for (FileHandle entry: worldDir.list()) {
                if (entry.file().getName().equals("Thumbs.db") || entry.file().isDirectory()){
                    continue;
                }
                assets.load(entry.path(), Texture.class);
            }
        }
        loaded = true;
    }

    public void initialiseResources(AssetManager assets) {
        characterMaker.initialiseResources(assets);
        bg = assets.get(folderPath+"/bg.png", Texture.class);

        if (!initialised && !areasCreated) {
            if (tlw == null) {
                for (FileHandle entry: worldDir.list()) {
                    if (entry.file().getName().equals("Thumbs.db") || entry.file().isDirectory() || entry.file().getName().equals("bg.png")){
                        continue;
                    }
                    maps.add(assets.get(entry.path(), Pixmap.class));
                    if (worldDir.name().substring(0, 2).equals("p_")) {
                        areas.add(new Area(assets, this, maps.get(maps.size()-1), true, 16, 16));
                    } else {
                        areas.add(new Area(assets, this, maps.get(maps.size()-1), false, 32, 16));
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
                    sprites.add(assets.get(entry.path(), Texture.class));
                }
                tiles = new ArrayList<BlockMultiTile>();
                worldDir = Gdx.files.internal(folderPath+"/tiles");
                for (FileHandle entry: worldDir.list()) {
                    if (entry.file().getName().equals("Thumbs.db") || entry.file().isDirectory()){
                        continue;
                    }
                    tiles.add(new BlockMultiTile(assets.get(entry.path(), Texture.class)));
                }
            }
        }
        //System.out.println(areas.size() + " " + areaIds.size());
        if (areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).initialised) {
            initialised = true;
        } else if (assets.update() && areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).loaded) {
            areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).initialiseResources(assets, this, characterMaker);
        } else {
            areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).load(assets, this);
        }
    }

    private void checkPlayerPosition() {
       if (areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player.x < -areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player.hitBox.getWidth()) {
           changeArea(-1, 0);
       } else if (areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player.x > areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).TILE_WIDTH*(areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).width)-5) {
           changeArea(1, 0);
       } else if (areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player.y+areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player.hitBox.getHeight() < -areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player.hitBox.getHeight()/2) {
           changeArea(0, 1);
       } else if (areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player.y > areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).TILE_HEIGHT*(areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).height-1)) {
           changeArea(0, -1);
       }
    }

    private void changeArea(int offX, int offY) {
        if (curAreaX+offX >= 0 && curAreaX+offX <= areaIds.size()-1 && curAreaY+offY >= 0 && curAreaY+offY <= areaIds.get(0).size()-1 && areaIds.get(curAreaX + offX).get(curAreaY + offY).get(curAreaZ) != -1) {
            //areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY)).player.x = areas.get(areaIds.get(curAreaX).get(curAreaY)).player.x - areas.get(areaIds.get(curAreaX).get(curAreaY)).TILE_WIDTH*offX;
            //areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY)).player.y = areas.get(areaIds.get(curAreaX).get(curAreaY)).player.y - areas.get(areaIds.get(curAreaX).get(curAreaY)).TILE_HEIGHT*offY;
            //areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY)).resetCamera();

            areas.get(areaIds.get(curAreaX+offX).get(curAreaY + offY).get(curAreaZ)).playerTileX = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).playerTileX-(areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).width-1)*offX;
            areas.get(areaIds.get(curAreaX + offX).get(curAreaY+offY).get(curAreaZ)).playerTileY = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).playerTileY+(areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).height-1)*offY;

            float pos = 0;
            int tileX, tileY;
            int speed=0;
            if (offY == 0) {
                pos = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player.hitBox.y;
                if (offX > 0) {
                    tileX = -1;
                } else {
                    tileX = 1;
                }
                tileY = 0;
                speed = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player.speedX;
                areaTransitionX = 1.0f;
            } else {
                pos = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player.hitBox.x;
                if (offY > 0) {
                    tileY = -1;
                } else {
                    tileY = 1;
                }
                tileX = 0;
                speed = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player.speedY;
                areaTransitionY = 1.0f;
            }

            areas.get(areaIds.get(curAreaX+offX).get(curAreaY + offY).get(curAreaZ)).respawnPlayer(worldDir.path(), assets, tileX, tileY, pos, speed, characterMaker);
            if (offY != 0) {

            } else {

            }
            //areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY)).resetCamera();

            oldAreaX = curAreaX;
            oldAreaY = curAreaY;
            curAreaX+=offX;
            curAreaY+=offY;
            initialised = false;
            areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).player.invalidatePose(true, true);
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
        if (areaTransitionX == 0 && areaTransitionY == 0) {
            if (areas.size() > areaIds.get(curAreaX).get(curAreaY).get(curAreaZ) && areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)) != null) {
                areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).draw(batch, this, 0, 0, true, true, characterMaker);
                checkPlayerPosition();
            }
        } else {
            Area area1, area2;
            if (oldAreaX < curAreaX || oldAreaY > curAreaY) {
                area1 = areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(curAreaZ));
                area2 = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ));
            } else {
                area2 = areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(curAreaZ));
                area1 = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ));
            }
            if (oldAreaY == curAreaY) {
                areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(curAreaZ)).cameraY = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).cameraY;
                if (oldAreaX < curAreaX) {
                    area2.draw(batch, this, Gdx.graphics.getWidth()/area2.zoom*(areaTransitionX), 0, true, true, characterMaker);
                    area1.draw(batch, this, Gdx.graphics.getWidth()/area1.zoom*(-1.0f+areaTransitionX)-1, 0, false, false, characterMaker);
                } else {
                    area2.draw(batch, this, Gdx.graphics.getWidth()/area2.zoom*(1.0f-areaTransitionX)+1, 0, false, true, characterMaker);
                    area1.draw(batch, this, Gdx.graphics.getWidth()/area1.zoom*(-areaTransitionX), 0, true, false, characterMaker);
                }
            } else {
                areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(curAreaZ)).cameraX = areas.get(areaIds.get(curAreaX).get(curAreaY).get(curAreaZ)).cameraX;
                int off = 2;
                float add = area1.FLOOR_HEIGHT+2;
                int off2 = 4;
                if (platformMode) {
                    off = 1;
                    add = 0;
                    off2 = 0;
                }
                if (oldAreaY < curAreaY) {
                    area1.draw(batch, this, 0, (Gdx.graphics.getHeight()+off*area1.TILE_HEIGHT+add)/area1.zoom*(areaTransitionY) /*+ area1.FLOOR_HEIGHT+2*/, true, true, characterMaker);
                    area2.draw(batch, this, 0, (Gdx.graphics.getHeight()+off*area1.TILE_HEIGHT+add)/area2.zoom*(-1.0f+areaTransitionY)-off2, false, false, characterMaker);
                } else {
                    area1.draw(batch, this, 0, (Gdx.graphics.getHeight()+off*area1.TILE_HEIGHT+add)/area1.zoom*(1.0f-areaTransitionY)+off2/* + area1.FLOOR_HEIGHT+2*/, false, true, characterMaker);
                    area2.draw(batch, this, 0, (Gdx.graphics.getHeight()+off*area1.TILE_HEIGHT+add)/area2.zoom*(-areaTransitionY), true, false, characterMaker);
                }
            }
            areaTransitionX /= 1.1f;
            areaTransitionY /= 1.1f;
            if (Math.abs(areaTransitionX) < 0.001f) {
                areaTransitionX = 0;
                if (areaTransitionY == 0) {
                    areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ)).removeParticles();
                }
            }
            if (Math.abs(areaTransitionY) < 0.001f) {
                areaTransitionY = 0;
                if (areaTransitionX == 0) {
                    areas.get(areaIds.get(oldAreaX).get(oldAreaY).get(oldAreaZ)).removeParticles();
                }
            }
            //System.out.println(areaTransitionX);
        }
    }

}
