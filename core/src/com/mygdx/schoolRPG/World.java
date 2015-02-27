package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by user on 06.08.2014.
 */
public class World {

    ArrayList<Area> areas;
    ArrayList<ArrayList<Integer>> areaIds;
    String folderPath, tlwPath;
    ArrayList<Pixmap> maps;
    File tlw;
    FileInputStream fis;
    public boolean initialised = false, loaded = false, areasCreated = false;
    FileHandle worldDir;
    int curAreaX = 0, curAreaY = 0, oldAreaX = 0, oldAreaY = 0;
    ArrayList<Integer> crds;
    //Area loadArea;
    float areaTransitionX = 0, areaTransitionY = 0;
    String name;
    boolean platformMode = false;
    int width, height, areaWidth, areaHeight, tileWidth, tileHeight, playerWidth, playerHeight;


    public World(String folderPath, int w, int h, int startingAreaX, int startingAreaY) {
        areas = new ArrayList<Area>();
        this.folderPath = folderPath;
        maps = new ArrayList<Pixmap>();
        areaIds = new ArrayList<ArrayList<Integer>>(w);
        for (int i = 0; i < w; i++) {
            areaIds.add(new ArrayList<Integer>(h));
            for (int t = 0; t < h; t++) {
                areaIds.get(i).add(-1);
            }
        }
        curAreaX = startingAreaX;
        curAreaY = startingAreaY;
        //loadArea = new Area(0, null);
    }

    public World(String tlwFilePath) {
        areas = new ArrayList<Area>();
        areaIds = new ArrayList<ArrayList<Integer>>();
        tlwPath = tlwFilePath;
        //maps = new ArrayList<Pixmap>();
        tlw = new File(tlwFilePath);
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
            areaWidth = fis.read();
            areaHeight = fis.read();
            tileWidth = fis.read();
            tileHeight = fis.read();
            playerWidth = fis.read();
            playerHeight = fis.read();
            for (int i = 0; i < width; i++) {
                areaIds.add(new ArrayList<Integer>(height));
                for (int t = 0; t < height; t++) {
                    areaIds.get(i).add(-1);
                }
            }
            int curCoordX = fis.read();
            int curCoordY = fis.read();
            //int endChecker = 0;
            curAreaX = curCoordX;
            curAreaY = curCoordY;
            while (fis.available() != 1) {

                buff = new byte[areaWidth*areaHeight*3];
                fis.read(buff);
                areas.add(new Area(this, buff, platformMode, tileWidth, tileHeight));
                areaIds.get(curCoordX).set(curCoordY,areas.size()-1);
                /*fInput.skip(width*height*3);
                endChecker = fInput.read();
                if (endChecker == 254) {
                    return false;
                } else {
                    coordX = endChecker;
                }
                //coordX = fInput.read();
                coordY = fInput.read();*/
                curCoordX = fis.read();
                curCoordY = fis.read();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(AssetManager assets) {
        if (tlw == null) {
            worldDir = Gdx.files.internal(folderPath);
            int curX,curY;
            int count = 0;
            for (FileHandle entry: worldDir.list()) {
                if (entry.file().getName().equals("Thumbs.db") || entry.file().isDirectory()){
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
                areaIds.get(curX).set(curY, count);
                count++;
            }
        } else {

        }
        loaded = true;
    }

    public void initialiseResources(AssetManager assets) {
        if (!initialised && !areasCreated) {
            for (FileHandle entry: worldDir.list()) {
                if (entry.file().getName().equals("Thumbs.db") || entry.file().isDirectory()){
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
        }
        //System.out.println(areas.size() + " " + areaIds.size());
        if (areas.get(areaIds.get(curAreaX).get(curAreaY)).initialised) {
            initialised = true;
        } else if (assets.update() && areas.get(areaIds.get(curAreaX).get(curAreaY)).loaded) {
            areas.get(areaIds.get(curAreaX).get(curAreaY)).initialiseResources(assets, worldDir.path());
        } else {
            areas.get(areaIds.get(curAreaX).get(curAreaY)).load(assets, this);
        }
    }

    private void checkPlayerPosition() {
       if (areas.get(areaIds.get(curAreaX).get(curAreaY)).player.x < -areas.get(areaIds.get(curAreaX).get(curAreaY)).player.hitBox.getWidth()) {
           changeArea(-1, 0);
       } else if (areas.get(areaIds.get(curAreaX).get(curAreaY)).player.x > areas.get(areaIds.get(curAreaX).get(curAreaY)).TILE_WIDTH*(areas.get(areaIds.get(curAreaX).get(curAreaY)).width)-5) {
           changeArea(1, 0);
       } else if (areas.get(areaIds.get(curAreaX).get(curAreaY)).player.y+areas.get(areaIds.get(curAreaX).get(curAreaY)).player.hitBox.getHeight() < -areas.get(areaIds.get(curAreaX).get(curAreaY)).player.hitBox.getHeight()/2) {
           changeArea(0, 1);
       } else if (areas.get(areaIds.get(curAreaX).get(curAreaY)).player.y > areas.get(areaIds.get(curAreaX).get(curAreaY)).TILE_HEIGHT*(areas.get(areaIds.get(curAreaX).get(curAreaY)).height-1)) {
           changeArea(0, -1);
       }
    }

    private void changeArea(int offX, int offY) {
        if (curAreaX+offX >= 0 && curAreaX+offX <= areaIds.size()-1 && curAreaY+offY >= 0 && curAreaY+offY <= areaIds.get(0).size()-1 && areaIds.get(curAreaX + offX).get(curAreaY+offY) != -1) {
            //areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY)).player.x = areas.get(areaIds.get(curAreaX).get(curAreaY)).player.x - areas.get(areaIds.get(curAreaX).get(curAreaY)).TILE_WIDTH*offX;
            //areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY)).player.y = areas.get(areaIds.get(curAreaX).get(curAreaY)).player.y - areas.get(areaIds.get(curAreaX).get(curAreaY)).TILE_HEIGHT*offY;
            //areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY)).resetCamera();

            areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY)).playerTileX = areas.get(areaIds.get(curAreaX).get(curAreaY)).playerTileX-(areas.get(areaIds.get(curAreaX).get(curAreaY)).width-1)*offX;
            areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY)).playerTileY = areas.get(areaIds.get(curAreaX).get(curAreaY)).playerTileY+(areas.get(areaIds.get(curAreaX).get(curAreaY)).height-1)*offY;

            float pos = 0;
            int tileX, tileY;
            int speed=0;
            if (offY == 0) {
                pos = areas.get(areaIds.get(curAreaX).get(curAreaY)).player.hitBox.y;
                if (offX > 0) {
                    tileX = -1;
                } else {
                    tileX = 1;
                }
                tileY = 0;
                speed = areas.get(areaIds.get(curAreaX).get(curAreaY)).player.speedX;
                areaTransitionX = 1.0f;
            } else {
                pos = areas.get(areaIds.get(curAreaX).get(curAreaY)).player.hitBox.x;
                if (offY > 0) {
                    tileY = -1;
                } else {
                    tileY = 1;
                }
                tileX = 0;
                speed = areas.get(areaIds.get(curAreaX).get(curAreaY)).player.speedY;
                areaTransitionY = 1.0f;
            }

            areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY)).respawnPlayer(worldDir.path(), tileX, tileY, pos, speed);
            if (offY != 0) {

            } else {

            }
            //areas.get(areaIds.get(curAreaX+offX).get(curAreaY+offY)).resetCamera();

            oldAreaX = curAreaX;
            oldAreaY = curAreaY;
            curAreaX+=offX;
            curAreaY+=offY;
            initialised = false;

        } else {
            areas.get(areaIds.get(curAreaX).get(curAreaY)).respawnPlayer(null, 0, 0, 0, 0);
        }
        /*if (id < areas.size()) {
            curArea = id;
        } else {
            initialised = false;
        }*/
    }

    public void draw(SpriteBatch batch, float offsetX, float offsetY) {
        if (areaTransitionX == 0 && areaTransitionY == 0) {
            if (areas.size() > areaIds.get(curAreaX).get(curAreaY) && areas.get(areaIds.get(curAreaX).get(curAreaY)) != null) {
                areas.get(areaIds.get(curAreaX).get(curAreaY)).draw(batch, 0, 0, true);
                checkPlayerPosition();
            }
        } else {
            Area area1, area2;
            if (oldAreaX < curAreaX || oldAreaY > curAreaY) {
                area1 = areas.get(areaIds.get(oldAreaX).get(oldAreaY));
                area2 = areas.get(areaIds.get(curAreaX).get(curAreaY));
            } else {
                area2 = areas.get(areaIds.get(oldAreaX).get(oldAreaY));
                area1 = areas.get(areaIds.get(curAreaX).get(curAreaY));
            }
            if (oldAreaY == curAreaY) {
                areas.get(areaIds.get(oldAreaX).get(oldAreaY)).cameraY = areas.get(areaIds.get(curAreaX).get(curAreaY)).cameraY;
                if (oldAreaX < curAreaX) {
                    area2.draw(batch, Gdx.graphics.getWidth()/area2.zoom*(areaTransitionX), 0, true);
                    area1.draw(batch, Gdx.graphics.getWidth()/area1.zoom*(-1.0f+areaTransitionX)-1, 0, false);
                } else {
                    area2.draw(batch, Gdx.graphics.getWidth()/area2.zoom*(1.0f-areaTransitionX)+1, 0, false);
                    area1.draw(batch, Gdx.graphics.getWidth()/area1.zoom*(-areaTransitionX), 0, true);
                }
            } else {
                areas.get(areaIds.get(oldAreaX).get(oldAreaY)).cameraX = areas.get(areaIds.get(curAreaX).get(curAreaY)).cameraX;
                if (oldAreaY < curAreaY) {
                    area1.draw(batch, 0, (Gdx.graphics.getHeight()+2*area1.TILE_HEIGHT+area1.FLOOR_HEIGHT+2)/area1.zoom*(areaTransitionY) /*+ area1.FLOOR_HEIGHT+2*/, true);
                    area2.draw(batch, 0, (Gdx.graphics.getHeight()+2*area1.TILE_HEIGHT+area1.FLOOR_HEIGHT+2)/area2.zoom*(-1.0f+areaTransitionY)-4, false);
                } else {
                    area1.draw(batch, 0, (Gdx.graphics.getHeight()+2*area1.TILE_HEIGHT+area1.FLOOR_HEIGHT+2)/area1.zoom*(1.0f-areaTransitionY)+4/* + area1.FLOOR_HEIGHT+2*/, false);
                    area2.draw(batch, 0, (Gdx.graphics.getHeight()+2*area1.TILE_HEIGHT+area1.FLOOR_HEIGHT+2)/area2.zoom*(-areaTransitionY), true);
                }
            }
            areaTransitionX /= 1.1f;
            areaTransitionY /= 1.1f;
            if (Math.abs(areaTransitionX) < 0.001f) areaTransitionX = 0;
            if (Math.abs(areaTransitionY) < 0.001f) areaTransitionY = 0;
            //System.out.println(areaTransitionX);
        }
    }

}
