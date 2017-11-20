package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.schoolRPG.menus.GameMenu;
import com.mygdx.schoolRPG.tools.CharacterMaker;

import java.util.ArrayList;
import java.util.Collections;

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
    ArrayList<ArrayList<ArrayList<Integer>>> blocks;
    WorldObjectsHandler worldObjectsHandler;
    ObjectAdder adder;
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

    float lastSpawnPos;
    Texture shadow;
    boolean saved = false;

    BitmapFont signFont;


    public Area(int x, int y, int z, int w, int h, byte[] map, int width, int height , int tileWidth, int tileHeight, boolean platformMode, World world) {
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

        for (int i = 0; i < 8; ++i) blocks.add(new ArrayList<ArrayList<Integer>>());

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
                        if ((int)map[c] == 255 || (int)map[c] == -1) {
                            blocks.get(k).get(t).add(-1);
                        } else {
                            blocks.get(k).get(t).add((int)map[c]);
                            if (k == 0) {
                                blocks.get(2).get(t).set(i, 1);
                            } else {
                                blocks.get(2).get(t).set(i, 2);
                            }
                        }
                        c++;
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
                            }
                        }

                        int type = (int)map[c];
                        blocks.get(k).get(t).add(type);
                        if (k == 4) {
                            if (type == -1) {
                                blocks.get(2).get(t).set(i, 0);
                            } else if (type == 0) {
                                playerTileX = t;
                                playerTileY = i;
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
                        }
                        c++;
                    }
                }
            }
        }

        worldObjectsHandler = new WorldObjectsHandler(this, blocks, world.flagNames, world.flags);
        adder = new ObjectAdder(worldObjectsHandler);
    }

    public void initialiseResources(AssetManager assets, World world, CharacterMaker characterMaker) {
        this.assets = world.assets;
        worldPath = world.worldDir.path();
        if (!initialised) {

            adder.initialiseWorldObjects(assets, world, characterMaker);

            if (player == null) {
                if (platformMode) {
                    player = new Player(assets, world.worldDir+"/chars/0/char.png", (playerTileX*TILE_WIDTH), ((playerTileY)*TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true, characterMaker, world);
                } else {
                    player = new Player(assets, null, (playerTileX * TILE_WIDTH), ((playerTileY) * TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true, characterMaker, world);
                }
                lastSpawnPos = 0;
            }
            cameraX = player.graphicX + player.hitBox.getWidth()/2;
            cameraY = player.graphicY + player.hitBox.getHeight()/2;
            //worldObjectsHandler.setPlayer(player);

        }
        shadow = assets.get("prt_shadow.png");
        if (assets.isLoaded(worldPath+"/sign.png")) {
            worldObjectsHandler.signOverlay = assets.get(worldPath+"/sign_overlay.png");
            signFont = new BitmapFont(Gdx.files.internal(worldPath + "/sign_font.fnt"), Gdx.files.internal(worldPath + "/sign_font.png"), false);
        }

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
        if (TILE_WIDTH*(width)*zoom +20 > camera.getWidth()) {
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
        if (TILE_HEIGHT*(height-off)*zoom +20 > camera.getHeight()) {
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
            batch.draw(world.bg, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
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


        worldObjectsHandler.draw(world.menu, batch, world, offsetX, offsetY, drawPlayer, alpha);
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
