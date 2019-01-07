package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.schoolRPG.menus.GameMenu;
import com.mygdx.schoolRPG.tools.AnimationSequence;
import com.mygdx.schoolRPG.tools.CharacterMaker;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.FileHandler;

/**
 * Created by user on 06.08.2014.
 */
public class Area {
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;
    int TILE_HEIGHT = 16, TILE_WIDTH = 32, FLOOR_HEIGHT = 6;
    float zoom = 2.0f;
    boolean platformMode = false;
    String name;
    ArrayList<ArrayList<ArrayList<Integer>>> blocks; //10xWxH; 0-layer1;1-layer2;2-layer3;3-layer4;4-id;5-angle(or objId if >= 100);6-x;7-y;8-2;9-h;
    WorldObjectsHandler worldObjectsHandler;
    ObjectAdder adder;
    ObjectLoader loader;
    int width, height, x, y, z, w, h;
    Player player;
    int playerTileX=0, playerTileY=0;
    double cameraX=0, cameraY=0;
    Rectangle camera;
    public boolean initialised = false;
    AssetManager assets;
    int playerWidth, playerHeight;
    BlockMultiTile black, white;
    float playerFloor = -1;
    int lastSpawnTileX, lastSpawnTileY;
    public String worldPath;
    boolean playerHidden = false;
    World world;
    public boolean isCurrent = false;
    public boolean loopingX = false;
    public boolean loopingY = false;
    public Background bg;
    public Weather weather;

    float lastSpawnPos;
    Texture shadow;
    boolean saved = false;

    public boolean stopAllSounds = false;

    BitmapFont signFont;

    public boolean containsSpawn = false;

    public boolean loaded = false;
    public boolean loading = false;
    public String ambient = "";

    public byte[] map;


    public Area(int x, int y, int z, int w, int h, byte[] map, int width, int height , int tileWidth, int tileHeight, boolean platformMode, World world, String name) {
        this.map = map.clone();
        TILE_WIDTH = tileWidth;
        TILE_HEIGHT = tileHeight;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.h = h;
        this.platformMode = platformMode;
        if (platformMode) {
            playerWidth = 8;
            playerHeight = 12;
        } else {
            playerWidth = 16;
            playerHeight = 5;
            playerFloor = 10;
        }
        blocks = new ArrayList<ArrayList<ArrayList<Integer>>>();
        camera = new Rectangle(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        this.width = width;
        this.height = height;
        int c = 0;

        for (int i = 0; i < 10; ++i) blocks.add(new ArrayList<ArrayList<Integer>>());

        if (map != null) {
            for (int i = 0; i < width; ++i) {
                blocks.get(2).add(new ArrayList<Integer>());
                for (int t = 0; t < height; ++t) {
                    blocks.get(2).get(i).add(0);
                }
            }
            for (int k = 0; k < 2; ++k) {
                for (int i = 0; i < height; ++i) {
                    for (int t = 0; t < width; ++t) {
                        if (i == 0)blocks.get(k).add(new ArrayList<Integer>());
                        if (((int)map[c] == 255 || (int)map[c] == -1) && ((int)map[c+1] == 255 || (int)map[c+1] == -1)) {
                            blocks.get(k).get(t).add(-1);
                        } else {
                            int res = (map[c+1] & 0xff) | (short) (map[c] << 8);
                            blocks.get(k).get(t).add(res);
                            if (k == 0) {
                                blocks.get(2).get(t).set(i, 1);
                            } else {
                                blocks.get(2).get(t).set(i, 2);
                            }
                        }
                        c+=2;
                    }
                }
            }
            for (int k = 3; k < 5; ++k) {
                for (int i = 0; i < height; ++i) {

                    for (int t = 0; t < width; ++t) {
                        if (i == 0) {
                            blocks.get(k).add(new ArrayList<Integer>());
                            if (k == 4) {
                                blocks.get(k+1).add(new ArrayList<Integer>());
                                blocks.get(k+2).add(new ArrayList<Integer>());
                                blocks.get(k+3).add(new ArrayList<Integer>());
                                blocks.get(k+4).add(new ArrayList<Integer>());
                                blocks.get(k+5).add(new ArrayList<Integer>());
                            }
                        }
                        int type;
                        if (k == 3) {
                            type = (map[c+1] & 0xff) | (short) (map[c] << 8);
                        } else {
                            type = (int)map[c];
                        }
                        blocks.get(k).get(t).add(type);
                        if (k == 4) {
                            if (type == -1) {
                                blocks.get(2).get(t).set(i, 0);
                            } else if (type == 0) {
                                playerTileX = t;
                                playerTileY = i;
                                containsSpawn = true;
                            } else if (type == 1) {
                                if (blocks.get(0).get(t).get(i) == -1) blocks.get(2).get(t).set(i, 1);
                            } else if (type == 2) {
                                if (blocks.get(1).get(t).get(i) == -1) blocks.get(2).get(t).set(i, 2);
                            }
                            c++;
                            type = (int)map[c];
                            blocks.get(k+1).get(t).add(type);
                            c++;
                            type = (int)map[c];
                            blocks.get(k+2).get(t).add(type);
                            c++;
                            type = (int)map[c];
                            blocks.get(k+3).get(t).add(type);
                            c++;
                            type = (int)map[c];
                            blocks.get(k+4).get(t).add(type);
                            c++;
                            type = (int)map[c];
                            blocks.get(k+5).get(t).add(type);
                        }
                        c++;
                        if (k == 3) c++;
                    }
                }
            }
        }

        worldObjectsHandler = new WorldObjectsHandler(this, blocks, world.varNames, world.vars);
        adder = new ObjectAdder(worldObjectsHandler);
        loader = new ObjectLoader(worldObjectsHandler);
        this.name = name;
    }

    public void load() {
        loading = true;
        if (loaded) return;
        this.assets = world.assets;
        loader.loadObjects(assets, world);
        loaded = true;
    }

    public void initialiseResources(AssetManager assets, World world, CharacterMaker characterMaker) {
        worldPath = world.worldDir.path();
        loader.initializeObjects(assets, world);
        if (!initialised) {
            if (player == null) {
                if (platformMode) {
                    player = new Player(assets, world.worldDir+"/chars/0/char.png", (playerTileX*TILE_WIDTH), ((playerTileY)*TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true, characterMaker, world);
                } else {
                    player = new Player(assets, null, (playerTileX * TILE_WIDTH), ((playerTileY) * TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true, characterMaker, world);
                }
                lastSpawnPos = 0;
            }
            adder.initialiseWorldObjects(assets, world, characterMaker);

            cameraX = player.graphicX + player.hitBox.getWidth()/2;
            cameraY = player.graphicY + player.hitBox.getHeight()/2;
            //worldObjectsHandler.setPlayer(player);

        }
        FileHandle weatherFile = Gdx.files.internal(worldPath + "/weather.xml");
        if (weatherFile.exists()) {
            weather = new Weather(worldPath + "/weather.xml", name, assets, world);
        }
        shadow = assets.get("prt_shadow.png");
        if (assets.isLoaded(worldPath+"/sign.png")) {
            worldObjectsHandler.signOverlay = assets.get(worldPath+"/sign_overlay.png");
            signFont = new BitmapFont(Gdx.files.internal(worldPath + "/sign_font.fnt"), Gdx.files.internal(worldPath + "/sign_font.png"), false);
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(Gdx.files.internal(world.worldDir.path() + "/bg/bg").read()));
            String line = "_";
            boolean defaultBG = true;
            while (line != null) {
                if (line.equals(name)) {
                    defaultBG = false;
                }
                line = in.readLine();
            }
            in = new BufferedReader(new InputStreamReader(Gdx.files.internal(world.worldDir.path() + "/bg/bg").read()));
            int numLayers = Integer.parseInt(in.readLine());
            bg = new Background();

            for (int i = 0; i < numLayers; ++i) {
                line = in.readLine();
                String vals[] = line.split(" ");
                if (defaultBG) {
                    if (vals.length == 5) {
                        bg.addLayer(assets.get(world.worldDir.path() + "/bg/" + vals[0] + ".png", Texture.class), Float.parseFloat(vals[1]),  Float.parseFloat(vals[2]),  Float.parseFloat(vals[3]),  Float.parseFloat(vals[4]));
                    } else {
                        int framesCount = Integer.parseInt(vals[1]);
                        int fps = Integer.parseInt(vals[2]);
                        bg.addLayer(new AnimationSequence(assets, world.worldDir.path() + "/bg/" + vals[0] + ".png",  fps, true, framesCount), Float.parseFloat(vals[1]),  Float.parseFloat(vals[2]),  Float.parseFloat(vals[3]),  Float.parseFloat(vals[4]));
                    }
                }
            }
            if (!defaultBG) {
                int numCustomBGGroups = Integer.parseInt(in.readLine());
                for (int z = 0; z < numCustomBGGroups; ++z) {
                    int numCustomBGs = Integer.parseInt(in.readLine());
                    ArrayList<Area> customAreas = new ArrayList<Area>();
                    boolean thiss = false;
                    for (int i = 0; i < numCustomBGs; ++i) {
                        line = in.readLine();
                        if (line.equals(name)) thiss = true;
                        customAreas.add(world.map.getAreaByName(line));
                    }
                    numLayers = Integer.parseInt(in.readLine());
                    for (int j = 0; j < numLayers; ++j) {
                        line = in.readLine();
                        String vals[] = line.split(" ");
                        if (thiss) {
                            if (vals.length == 5) {
                                bg.addLayer(assets.get(world.worldDir.path() + "/bg/" + vals[0] + ".png", Texture.class), Float.parseFloat(vals[1]),  Float.parseFloat(vals[2]),  Float.parseFloat(vals[3]),  Float.parseFloat(vals[4]));
                            } else {
                                int framesCount = Integer.parseInt(vals[1]);
                                int fps = Integer.parseInt(vals[2]);
                                bg.addLayer(new AnimationSequence(assets, world.worldDir.path() + "/bg/" + vals[0] + ".png",  fps, true, framesCount), Float.parseFloat(vals[1]),  Float.parseFloat(vals[2]),  Float.parseFloat(vals[3]),  Float.parseFloat(vals[4]));
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println();
        initialised = true;
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void moveCamera(int k) {
        if (player == null) return;
        cameraX += (player.graphicX + player.hitBox.getWidth()/2 - cameraX)/k;
        cameraY += (player.graphicY + player.hitBox.getHeight()/2 - cameraY)/k;
        bg.move((float) (player.graphicX + player.hitBox.getWidth()/2 - cameraX)/k, (float)(player.graphicY + player.hitBox.getHeight()/2 - cameraY)/k);
        if (platformMode && TILE_WIDTH*(width)*zoom +20 > camera.getWidth()) {
            if (cameraX - SCREEN_WIDTH/zoom/2 < 1) {
                cameraX = SCREEN_WIDTH/zoom/2+1;
                if (platformMode) {
                    cameraX--;
                }
            } else if (cameraX + SCREEN_WIDTH/zoom/2 > TILE_WIDTH*(width)) {
                cameraX = TILE_WIDTH*(width) - SCREEN_WIDTH/zoom/2;

            }
        }
        float a = TILE_HEIGHT*(height)*zoom;
        float b = camera.getHeight();
        int off = 2;
        if (platformMode) {
            off = 1;
        }
        if (platformMode && TILE_HEIGHT*(height-off)*zoom +20 > camera.getHeight()) {
            if (cameraY - SCREEN_HEIGHT / zoom / 2 + TILE_HEIGHT < 0) {
                cameraY = SCREEN_HEIGHT / zoom / 2 - TILE_HEIGHT;
            } else if (cameraY + SCREEN_HEIGHT / zoom / 2 + FLOOR_HEIGHT + 2 > TILE_HEIGHT * (height - off)) {
                cameraY = TILE_HEIGHT * (height - off) - SCREEN_HEIGHT / zoom / 2 - FLOOR_HEIGHT - 2;
            }
        }
        cameraX = round(cameraX, 1);
        cameraY = round(cameraY, 1);
        /*if (zoom == 3.0f) {
            //cameraX += 40;
            //cameraY -= 25;
        }*/
    }


    public void invalidate(World world) {
        worldObjectsHandler.invalidateObjectCells();
        worldObjectsHandler.invalidatePlayer(world);
        worldObjectsHandler.invalidateNPCs();
        worldObjectsHandler.invalidateSolids();
        worldObjectsHandler.invalidateCollisions(player, player.oldX, player.oldY);
        if (weather != null) {
            weather.invalidateWind(world.menu.paused);
            worldObjectsHandler.invalidateWeather();
        }
   }


    public void respawnPlayerZ(String worldDir, AssetManager assets, int tileX, int tileY, CharacterMaker characterMaker) {
        if (player == null) {
            if (platformMode) {
                player = new Player(assets, worldDir+"/chars/0/char.png", (playerTileX*TILE_WIDTH), ((playerTileY)*TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true, characterMaker, world);
            } else {
                player = new Player(assets, null, (playerTileX*TILE_WIDTH), ((playerTileY)*TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true, characterMaker, world);
            }
        }
        player.hitBox.x = tileX;
        player.hitBox.y = tileY;
        player.speedX = 0;
        player.speedY = 0;
        player.pSpeed = 0;
        player.x = player.hitBox.x;
        player.y = player.hitBox.y;
        player.graphicX = player.hitBox.x;
        player.graphicY = player.hitBox.y;
        player.falling = false;
        player.z = 0;
        player.zSpeed = 0;
        if (worldDir != null) {
            cameraX = player.graphicX + player.hitBox.getWidth()/2 - cameraX;
            cameraY = player.graphicY + player.hitBox.getHeight()/2 - cameraY;
            moveCamera(1);
        }
    }

    public void respawnPlayer(String worldDir, AssetManager assets, int tileX, int tileY, float pos, int speed, CharacterMaker characterMaker) {
        if (player == null) {

            if (platformMode) {
                player = new Player(assets, worldDir+"/chars/0/char.png", (playerTileX*TILE_WIDTH), ((playerTileY)*TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true, characterMaker, world);
            } else {
                player = new Player(assets, null, (playerTileX*TILE_WIDTH), ((playerTileY)*TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true, characterMaker, world);
            }
        }
        if (tileX != 0 || tileY != 0 || pos != 0) {
            lastSpawnTileX = tileX;
            lastSpawnTileY = tileY;
            lastSpawnPos = pos;
        }
        //player = new Player(assets, "char", (playerTileX-1)*TILE_WIDTH+TILE_WIDTH/2-11, (playerTileY)* TILE_HEIGHT-TILE_HEIGHT/2+4, 22, 8, (FLOOR_HEIGHT/2), true);
        if (tileX == 0 && tileY == 0 && pos == 0) {
            respawnPlayer(null, assets, lastSpawnTileX, lastSpawnTileY, lastSpawnPos, 0, characterMaker);
        } else if (pos != 0) {
            if (tileX == -1) {
                player.hitBox.x = 15;
                if (platformMode) {
                    player.hitBox.x = 5;
                    player.speedX = speed;
                }
                if (platformMode) player.curPose = player.poses.getTile(PlayerMultiTile.PlayerPose.RIGHT);
            } else if (tileX == 1) {
                player.hitBox.x = TILE_WIDTH * (width - 1);
                if (platformMode) {
                    player.speedX = speed;
                }
                player.speedY = 0;
                if (platformMode) player.curPose = player.poses.getTile(PlayerMultiTile.PlayerPose.LEFT);
            } else if (tileY == 1) {
                player.hitBox.y = 5;
                if (platformMode) {
                    player.speedY = speed;
                }
                player.speedX = 0;
                if (platformMode) player.curPose = player.poses.getTile(PlayerMultiTile.PlayerPose.FRONT);
            } else if (tileY == -1) {
                player.hitBox.y = TILE_HEIGHT * (height - 1) - 10;
                //player.speedY = speed;
                if (platformMode) {
                    player.speedY = speed;
                }
                player.speedX = 0;
                if (platformMode) player.curPose = player.poses.getTile(PlayerMultiTile.PlayerPose.BACK);
            }
            if (tileY == 0 && tileX != 0) {
                player.hitBox.y = pos;
            } else if (tileY != 0 && tileX == 0) {
                player.hitBox.x = pos;
            }
        } else {
            player.hitBox.x = (lastSpawnTileX*TILE_WIDTH);
            player.hitBox.y = (lastSpawnTileY*TILE_HEIGHT);
            if (platformMode) player.curPose = player.poses.getTile(PlayerMultiTile.PlayerPose.FRONT);
        }
        player.speedX = 0;
        player.speedY = 0;
        player.pSpeed = 0;
        player.x = player.hitBox.x;
        player.y = player.hitBox.y;
        player.graphicX = player.hitBox.x;
        player.graphicY = player.hitBox.y;
        player.falling = false;
        player.z = 0;
        player.zSpeed = 0;
        if (worldDir != null) {
            cameraX = player.graphicX + player.hitBox.getWidth()/2 - cameraX;
            cameraY = player.graphicY + player.hitBox.getHeight()/2 - cameraY;
            moveCamera(1);
        }
    }

    public void resetCheckPoints() {
        worldObjectsHandler.resetCheckPoints();
    }


    public void removeParticles() {
        worldObjectsHandler.removeParticles();
    }


    public void draw(SpriteBatch batch, World world, float offsetX, float offsetY, boolean drawPlayer, boolean drawBG, boolean activeCamera) {
        drawPlayer = drawPlayer && !playerHidden;
        float alpha = batch.getColor().a;
        if (drawBG) {

            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
            //batch.draw(world.bg, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            bg.draw(batch, SCREEN_WIDTH, SCREEN_HEIGHT, 0, 0);
            batch.setColor(new Color(1.0f, 1.0f, 1.0f, alpha));
        }

        Matrix4 transform = new Matrix4();
        transform.scale(zoom, zoom, 1);
        batch.setTransformMatrix(transform);
        Matrix4 translate = new Matrix4();
        float off = 0;
        if (zoom == 2) off = 250;
        else if (zoom == 3) off = 335;
        else if (zoom == 4) off = 375;
        else if (zoom == 5) off = 400;
        translate.setToTranslation(-off, -off / 2, 0);
        batch.setTransformMatrix(transform.mul(translate));

        if (activeCamera) moveCamera(5);

        offsetX += -cameraX + SCREEN_WIDTH /2;
        offsetY += cameraY + SCREEN_HEIGHT /2;

        if (!platformMode) {
            int xLoops;
            int yLoops;
            int thisX = 0;
            int thisY = 0;
            if (loopingX) {
                xLoops = SCREEN_WIDTH / (width * TILE_WIDTH) + 1;
                thisX = Math.round((float)xLoops/(float)2)-1;
            } else {
                xLoops = 1;
            }
            if (loopingY) {
                yLoops  = SCREEN_HEIGHT / (height * TILE_HEIGHT) + 1;
                thisY = Math.round((float)yLoops/(float)2)-1;
            } else {
                yLoops = 1;
            }
            for (int i = 0; i < yLoops; ++i) {
                for (int t = 0; t < xLoops; ++t) {
                    float offSetX = (t - thisX) * width * TILE_WIDTH;
                    float offSetY = (thisY - i) * height * TILE_HEIGHT;
                    worldObjectsHandler.draw(world.menu, batch, world, offsetX + offSetX, offsetY + offSetY, drawPlayer && (t == thisX && i == thisY), alpha);
                }
            }
        } else {
            worldObjectsHandler.draw(world.menu, batch, world, offsetX, offsetY, drawPlayer, alpha);
        }

        transform = new Matrix4();
        batch.setTransformMatrix(transform);
    }



    public int chekZPath() {
        for (int i = 0; i < height; ++i) {
            for (int t = 0; t < width; ++t) {
                if (blocks.get(4).get(t).get(i) == 15) {
                    Rectangle rect = new Rectangle(TILE_WIDTH*t, TILE_HEIGHT*i, TILE_WIDTH, TILE_HEIGHT);
                    if (player.hitBox.overlaps(rect)) {
                        return 1;
                    }
                }
                else if (blocks.get(4).get(t).get(i) == 16) {
                    Rectangle rect = new Rectangle(TILE_WIDTH*t, TILE_HEIGHT*i, TILE_WIDTH, TILE_HEIGHT);
                    if (player.hitBox.overlaps(rect)) {
                        return -1;
                    }
                }
            }
        }
        return 0;
    }
}
