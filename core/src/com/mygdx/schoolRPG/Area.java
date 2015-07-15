package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.schoolRPG.tools.AnimationSequence;
import com.mygdx.schoolRPG.tools.CharacterMaker;
import com.mygdx.schoolRPG.tools.GlobalSequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by user on 06.08.2014.
 */
public class Area {
    int TILE_HEIGHT = 16, TILE_WIDTH = 32, FLOOR_HEIGHT = 6;
    float zoom = 2.0f;
    boolean platformMode = false;
    //int id, nextId;
    String name;
    ArrayList<ArrayList<ArrayList<Integer>>> blocks;
    ArrayList<Entity> objects;
    //ArrayList<HittableEntity> scenery;
    int width, height;
    Player player;
    int playerTileX=0, playerTileY=0, playerTileDynX=0, playerTileDynY=0;
    double cameraX=0, cameraY=0;
    Rectangle camera;
    public boolean initialised = false, loaded = false;
    AssetManager assets;
    FPSLogger fps;
    int playerWidth, playerHeight;
    BlockMultiTile black, white;
    float playerFloor = -1;
    int lastSpawnTileX, lastSpawnTileY;
    float lastSpawnPos;

    public Area(AssetManager assets, World world, Pixmap map, boolean platformMode, int tileWidth, int tileHeight) {
        this.assets = assets;
        fps = new FPSLogger();
        this.platformMode = platformMode;
        TILE_HEIGHT = tileHeight;
        TILE_WIDTH = tileWidth;
        if (platformMode) {
            playerWidth = 8;
            playerHeight = 15;
        } else {
            //playerWidth = 16;
            //playerHeight = 12;
            playerWidth = 16;
            playerHeight = 5;
            playerFloor = 10;
        }

        //this.id = id;
        //nextId = id;
        blocks = new ArrayList<ArrayList<ArrayList<Integer>>>();
        objects = new ArrayList<Entity>();
        //scenery = new ArrayList<HittableEntity>();
        camera = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (map != null) {
            width = map.getWidth();
            height = map.getHeight();
            blocks.add(new ArrayList<ArrayList<Integer>>());
            blocks.add(new ArrayList<ArrayList<Integer>>());
            blocks.add(new ArrayList<ArrayList<Integer>>());
            for (int i = 0; i < map.getWidth(); ++i) {
                blocks.get(0).add(new ArrayList<Integer>());
                blocks.get(1).add(new ArrayList<Integer>());
                blocks.get(2).add(new ArrayList<Integer>());
                for (int t=0; t<map.getHeight(); ++t) {
                    //blocks.get(i).add(map.getPixel(i, t));
                    Color curPixelColor = new Color(map.getPixel(i, t));
                    if (curPixelColor.a == 1.0f) {
                        if (curPixelColor.equals(Color.GREEN)) {
                            playerTileY = t;
                            playerTileX = i;
                            blocks.get(0).get(i).add(1);
                            blocks.get(1).get(i).add(0);
                            blocks.get(2).get(i).add(1);
                        } else if (curPixelColor.equals(Color.RED)) {
                            objects.add(new HittableEntity(assets, world.folderPath+"/sprites/red.png", i*TILE_WIDTH, (t * TILE_HEIGHT - FLOOR_HEIGHT), TILE_WIDTH, TILE_HEIGHT, FLOOR_HEIGHT/4+1, true));
                            blocks.get(0).get(i).add(1);
                            blocks.get(1).get(i).add(0);
                            blocks.get(2).get(i).add(1);
                        } else if (curPixelColor.equals(Color.BLUE)) {
                            objects.add(new HittableEntity(assets, world.folderPath+"/sprites/blue.png", i*TILE_WIDTH+TILE_WIDTH/2-2.5f, (t* TILE_HEIGHT-TILE_HEIGHT/3+2), 5, 4, -FLOOR_HEIGHT, false));
                            blocks.get(0).get(i).add(1);
                            blocks.get(1).get(i).add(0);
                            blocks.get(2).get(i).add(1);
                        } else if (curPixelColor.equals(Color.YELLOW)) {
                            objects.add(new HittableEntity(assets, world.folderPath+"/sprites/yellow.png", i*TILE_WIDTH+TILE_WIDTH/4, (t* TILE_HEIGHT-FLOOR_HEIGHT), TILE_WIDTH/2, TILE_HEIGHT/1.6f, -FLOOR_HEIGHT/2+1, true));
                            blocks.get(0).get(i).add(1);
                            blocks.get(1).get(i).add(0);
                            blocks.get(2).get(i).add(1);
                        } else if (curPixelColor.equals(Color.WHITE)) {
                            blocks.get(0).get(i).add(1);
                            blocks.get(1).get(i).add(0);
                            blocks.get(2).get(i).add(1);
                        } else if (curPixelColor.equals(Color.BLACK)) {
                            blocks.get(0).get(i).add(1);
                            blocks.get(1).get(i).add(1);
                            blocks.get(2).get(i).add(2);
                        } else if (curPixelColor.equals(Color.MAGENTA)) {
                            float offX = 0, offY = 0;
                            if (i == 0) {
                                offX = -TILE_WIDTH;
                            } else if (i == map.getWidth()-1) {
                                offX = TILE_WIDTH;
                            }
                            if (t == 0) {
                                offY = -TILE_HEIGHT;
                            } else if (t == map.getHeight()-1) {
                                offY = TILE_HEIGHT;
                            }
                            //objects.add(new HittableEntity(assets, "arrow.png", i * TILE_WIDTH+offX, (t * TILE_HEIGHT - FLOOR_HEIGHT)+offY, TILE_WIDTH, TILE_HEIGHT, (FLOOR_HEIGHT / 2), false));
                            blocks.get(0).get(i).add(1);
                            blocks.get(1).get(i).add(0);
                            blocks.get(2).get(i).add(1);
                        } else {
                            blocks.get(0).get(i).add(0);
                            blocks.get(1).get(i).add(0);
                            blocks.get(2).get(i).add(0);
                        }
                    } else {
                        blocks.get(0).get(i).add(-1);
                        blocks.get(1).get(i).add(-1);
                        blocks.get(2).get(i).add(0);
                    }
                }
            }
        }
    }

    public Area(World world, byte[] map, int width, int height , int tileWidth, int tileHeight, boolean platformMode) {
        TILE_WIDTH = tileWidth;
        TILE_HEIGHT = tileHeight;
        this.platformMode = platformMode;
        if (platformMode) {
            playerWidth = 8;
            playerHeight = 15;
        } else {
            playerWidth = 16;
            playerHeight = 5;
            playerFloor = 10;
        }
        blocks = new ArrayList<ArrayList<ArrayList<Integer>>>();
        objects = new ArrayList<Entity>();
        //scenery = new ArrayList<HittableEntity>();
        camera = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.width = width;
        this.height = height;
        int c = 0;
        blocks.add(new ArrayList<ArrayList<Integer>>());
        blocks.add(new ArrayList<ArrayList<Integer>>());
        blocks.add(new ArrayList<ArrayList<Integer>>());

        blocks.add(new ArrayList<ArrayList<Integer>>());
        blocks.add(new ArrayList<ArrayList<Integer>>());
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
                        if (i == 0)blocks.get(k).add(new ArrayList<Integer>());

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
                        }
                        c++;
                    }
                }
            }
        }


    }

    public void load(AssetManager assets, World world) {



        if (world.worldDir.child("sprites/black.png").exists()) {
            black = new BlockMultiTile(world.folderPath + "/sprites/black.png", assets);
        }
        if (world.worldDir.child("sprites/white.png").exists()) {
            white = new BlockMultiTile(world.folderPath+"/sprites/white.png", assets);
        }
        if (world.tlw == null) {
            if (world.worldDir.child("sprites/red.png").exists()) {
                assets.load(world.folderPath+"/sprites/red.png", Texture.class);
            }
            if (world.worldDir.child("sprites/yellow.png").exists()) {
                assets.load(world.folderPath + "/sprites/yellow.png", Texture.class);
            }
            if (world.worldDir.child("sprites/blue.png").exists()) {
                assets.load(world.folderPath + "/sprites/blue.png", Texture.class);
            }
        } else {

        }

        loaded = true;
    }

    public void initialiseResources(AssetManager assets, World world) {
        if (!initialised) {
            //block = assets.get("block.png");
            if (black != null){
                black.initialiseIfNeeded(assets);
            }
            if (white!=null) {
                white.initialiseIfNeeded(assets);
            }
            if (world.tlw != null) {
                if (this.assets == null) {
                    this.assets = assets;
                }
                for (int i = 0; i < world.tiles.size(); ++i) {
                    world.tiles.get(i).initialiseIfNeeded(assets);
                }
                for (int i = 0; i < height; ++i) {
                    for (int t = 0; t < width; ++t) {
                        int type = blocks.get(4).get(t).get(i);
                        int img = blocks.get(3).get(t).get(i);
                        if (img == -1) continue;
                        if (type == 1) {
                            float y = (i)* TILE_HEIGHT-TILE_HEIGHT/2;
                            if (platformMode) y += FLOOR_HEIGHT/2+1;
                            if(img < world.sprites.size()){
                                objects.add(new Entity(assets, world.sprites.get(img), t*TILE_WIDTH+TILE_WIDTH/2-world.sprites.get(img).getWidth()/2, y, 0, 0));
                            } else {
                                objects.add(new Entity(assets, world.tiles.get(img - world.spritesCount).getSingleTile(), t*TILE_WIDTH+TILE_WIDTH/2-world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth()/2, y, 0, 0));
                            }
                            objects.get(objects.size()-1).setFloor(true);
                        } else if (type == 2) {
                            float y = (i)* TILE_HEIGHT-TILE_HEIGHT/2;
                            if (platformMode) y += FLOOR_HEIGHT/2+1;
                            if(img < world.sprites.size()){
                                objects.add(new Entity(assets, world.sprites.get(img), t*TILE_WIDTH+TILE_WIDTH/2-world.sprites.get(img).getWidth()/2, y, 0, 0));
                            } else {
                                objects.add(new Entity(assets, world.tiles.get(img - world.spritesCount).getSingleTile(), t*TILE_WIDTH+TILE_WIDTH/2-world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth()/2, y, 0, 0));
                            }
                        } else if (type == 3) {
                            float y = (i)* TILE_HEIGHT-TILE_HEIGHT/2;
                            if (platformMode) y += FLOOR_HEIGHT/2+1;
                            if(img < world.sprites.size()){
                                objects.add(new Entity(assets, world.sprites.get(img), t*TILE_WIDTH+TILE_WIDTH/2-world.sprites.get(img).getWidth()/2, y, 0, 0));
                            } else {
                                objects.add(new Entity(assets, world.tiles.get(img - world.spritesCount).getSingleTile(), t*TILE_WIDTH+TILE_WIDTH/2-world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth()/2, y, 0, 0));
                            }
                        } else if (type == 4) {
                            float y = (i * TILE_HEIGHT - TILE_HEIGHT/2);
                            float width = 0;
                            float height = 0;
                            float floorHeight = 0;
                            if (platformMode) {
                                if(img < world.sprites.size()){
                                    width = world.sprites.get(img).getWidth();
                                    height = world.sprites.get(img).getHeight();
                                } else {
                                    width = world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth();
                                    height = world.tiles.get(img - world.spritesCount).getSingleTile().getRegionHeight();
                                }
                                y -= (height-TILE_HEIGHT)-2;
                            } else {
                                if(img < world.sprites.size()){
                                    width = world.sprites.get(img).getWidth();
                                    height = world.sprites.get(img).getWidth()/2;
                                    floorHeight = world.sprites.get(img).getWidth()/4-2;
                                } else {
                                    //?y+=2;
                                    width = world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth();
                                    height = world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth()/2;
                                    floorHeight = world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth()/4-2;
                                }
                            }
                            if(img < world.sprites.size()){
                                objects.add(new HittableEntity(assets, world.sprites.get(img), t*TILE_WIDTH + TILE_WIDTH/2-world.sprites.get(img).getWidth()/2, y, width, height, floorHeight, false));
                            } else {
                                objects.add(new HittableEntity(assets, world.tiles.get(img - world.spritesCount).getSingleTile(), t*TILE_WIDTH + TILE_WIDTH/2-world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth()/2, y,
                                        width, height, floorHeight, false));
                            }
                        } else if (type == 5) {
                            float y = (i * TILE_HEIGHT - TILE_HEIGHT/2);
                            float width = 0;
                            float height = 0;
                            if (platformMode) {
                                if(img < world.sprites.size()){
                                    width = world.sprites.get(img).getWidth();
                                    height = world.sprites.get(img).getHeight();
                                } else {
                                    width = world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth();
                                    height = world.tiles.get(img - world.spritesCount).getSingleTile().getRegionHeight();
                                }
                                y -= (height-TILE_HEIGHT)-2;
                            } else {
                                if(img < world.sprites.size()){
                                    width = world.sprites.get(img).getWidth();
                                    height = world.sprites.get(img).getWidth()/2;
                                } else {
                                    //?y+=2;
                                    width = world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth();
                                    height = world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth()/2;
                                }
                            }
                            if(img < world.sprites.size()){
                                objects.add(new HittableEntity(assets, world.sprites.get(img), t*TILE_WIDTH + TILE_WIDTH/2-world.sprites.get(img).getWidth()/2, y, width, height, world.sprites.get(img).getWidth()/4-2, true));
                            } else {
                                objects.add(new HittableEntity(assets, world.tiles.get(img - world.spritesCount).getSingleTile(), t*TILE_WIDTH + TILE_WIDTH/2-world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth()/2, y,
                                        width, height, TILE_WIDTH/2-world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth()/4-2, true));
                            }
                        }
                    }
                }
            }

            //objects.add(new Entity(assets, assets.get("worlds/tlw_test/yellow.png", Texture.class), 100, 400, 0));

            if (player == null) {
                if (platformMode) {
                    player = new Player(assets, world.worldDir+"/sprites/char.png", (playerTileX*TILE_WIDTH), ((playerTileY)*TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true);
                } else {
                    player = new Player(assets, null, (playerTileX*TILE_WIDTH), ((playerTileY)*TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true);
                }
                lastSpawnTileX = playerTileX;
                lastSpawnTileY = playerTileY;
                lastSpawnPos = 0;
            }
            cameraX = player.graphicX + player.hitBox.getWidth()/2;
            cameraY = player.graphicY + player.hitBox.getHeight()/2;
            objects.add(player);
        }
        initialised = true;
    }

    public void respawnPlayer(String worldDir, AssetManager assets, int tileX, int tileY, float pos, int speed) {
        if (player == null) {
            if (platformMode) {
                player = new Player(assets, worldDir+"/sprites/char.png", (playerTileX*TILE_WIDTH), ((playerTileY)*TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true);
            } else {
                player = new Player(assets, null, (playerTileX*TILE_WIDTH), ((playerTileY)*TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true);
            }
        }
        if (tileX != 0 || tileY != 0 || pos != 0) {
            lastSpawnTileX = tileX;
            lastSpawnTileY = tileY;
            lastSpawnPos = pos;
        }
        //player = new Player(assets, "char", (playerTileX-1)*TILE_WIDTH+TILE_WIDTH/2-11, (playerTileY)* TILE_HEIGHT-TILE_HEIGHT/2+4, 22, 8, (FLOOR_HEIGHT/2), true);
        if (tileX == 0 && tileY == 0 && pos == 0) {
            respawnPlayer(null, assets, lastSpawnTileX, lastSpawnTileY, lastSpawnPos, 0);
        } else if (pos != 0) {
            if (tileX == -1) {
                player.hitBox.x = 5;
                if (platformMode) {
                    player.speedX = speed;
                }
                if (platformMode) player.curPose = player.poses.getTile(PlayerMultiTile.PlayerPose.RIGHT);
            } else if (tileX == 1) {
                player.hitBox.x = TILE_WIDTH * (width - 1) + 5;
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
                player.hitBox.y = TILE_HEIGHT * (height - 2) - 5;
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

    public void invalidateCollisions(HittableEntity he, float oldX, float oldY) {
        boolean isPlayer = false;
        if (he.getClass() == Player.class) isPlayer = true;

        int tileX1 = (int)(he.hitBox.x/(TILE_WIDTH))+1;
        int tileY1 = (int)(he.hitBox.y/(TILE_HEIGHT))+1;
        int tileX2 = (int)((he.hitBox.x + he.hitBox.width)/(TILE_WIDTH))-1;
        int tileY2 = (int)((he.hitBox.y + he.hitBox.height)/(TILE_HEIGHT))-1;
        /*if (tileX1 < 1 || tileY1 < 1 || tileX1 >= blocks.get(2).size()+1 || tileY1 >= blocks.get(0).size()) {
            return;
        }
        if (tileX2 < 1 || tileY2 < 1 || tileX2 >= blocks.get(2).size()+1 || tileY2 >= blocks.get(0).size()) {
            return;
        }*/
        if (platformMode && he.pSpeed >= 0) {
            tileY2++;
        }

        Rectangle oldRect = new Rectangle(he.hitBox);
        for (int z=0; z<objects.size(); ++z) {
            if (objects.get(z).getClass() == Entity.class) continue;
            if (!((HittableEntity)objects.get(z)).falling) {
                if (platformMode) {
                    he.hitBox = ((HittableEntity)objects.get(z)).pushOutSolidObjects(he, this, player.oldX, he.oldY);
                    if (he.hitBox.y < oldRect.y && he.pSpeed > 0) {
                        he.pSpeed=0;
                    } else if (he.hitBox.y > oldRect.y && he.pSpeed < 0) {
                        he.pSpeed=1;
                    }
                } else {
                    he.hitBox = ((HittableEntity)objects.get(z)).pushOutSolidObjects(he, this, player.oldX, player.oldY);
                }
            }
        }
        oldRect = new Rectangle(he.hitBox);
        for (int i = tileY1-1; i <= tileY2+2; ++i) {
            for (int t = tileX1-1; t <= tileX2+1; ++t) {
                if (t < 0 || t >= blocks.get(2).size() || i < 0 || i >= blocks.get(2).get(0).size()) break;
                if (blocks.get(2).get(t).get(i) == 2/* || blocks.get(t).get(i) == 0*/) {
                    HittableEntity tmp = new HittableEntity(assets, (String)null, t*(TILE_WIDTH), i* TILE_HEIGHT -6, TILE_WIDTH, TILE_HEIGHT, 3, false);
                    boolean left = t > 0 && blocks.get(2).get(t - 1).get(i) != 2/* && blocks.get(t-1).get(i) != 0*/;
                    boolean right = t < blocks.get(2).size()-1 && blocks.get(2).get(t + 1).get(i) != 2/* && blocks.get(t+1).get(i) != 0*/;
                    boolean up = i < blocks.get(2).get(i).size()-1 && blocks.get(2).get(t).get(i+1) != 2/* && blocks.get(t).get(i+1) != 0*/;
                    boolean down = i > 0 && blocks.get(2).get(t).get(i-1) != 2/* && blocks.get(t).get(i-1) != 0*/;
                    tmp.setSides(left, right, down, up);
                    if (isPlayer) {
                        he.hitBox = tmp.pushOutSolidObjects(he, this, player.oldX, player.oldY);
                    } else {
                        if (!platformMode) {
                            he.hitBox = tmp.pushOutSolidObjects(he, this, oldX, oldY);
                            he.hitBox = tmp.pushOutSolidObjects(he, this, player.oldX, he.oldY);
                        } else {
                            he.hitBox = tmp.pushOutSolidObjects(he, this, player.oldX, he.oldY);
                            he.hitBox = tmp.pushOutSolidObjects(he, this, he.oldX, he.oldY);
                            he.hitBox = tmp.pushOutSolidObjects(he, this, oldX, oldY);
                        }
                    }
                    if (he.hitBox == player.hitBox && (he.hitBox.x != oldRect.x || he.hitBox.y != oldRect.y)) {
                        //System.out.println(player.speedX);
                        //if (rect == player.hitBox) {
                        if (player.speedX != 0 && he.hitBox.x != oldRect.x && Math.abs(he.hitBox.x - oldRect.x)/(he.hitBox.x - oldRect.x) != Math.abs(player.speedX)/player.speedX) {
                            player.speedX=0;
                        }
                        if (!platformMode) {
                            if (player.speedY != 0 && he.hitBox.y != oldRect.y && (Math.abs(he.hitBox.y - oldRect.y)/(he.hitBox.y - oldRect.y) != -Math.abs(player.speedY)/player.speedY)) {
                                player.speedY=0;
                            }
                        } else {
                            if (he.hitBox.y < oldRect.y && player.pSpeed > 0) {
                                player.pSpeed=0;
                            } else if (he.hitBox.y > oldRect.y && player.pSpeed < 0) {
                                player.pSpeed=1;
                            }
                        }
                        //}
                        return;
                    }
                }
            }
        }
        /*if ((he.hitBox.x == oldX && he.hitBox.y == oldY)) {
            he.hitBox.x = Math.round(he.hitBox.x);
            he.hitBox.y = Math.round(he.hitBox.y);
        }*/
        return;
    }

    public void checkFall(HittableEntity object) {
        boolean fall = true;
        for (int i = 0; i < width; ++i) {
            for (int t = 0; t < height; ++t) {
                if (blocks.get(2).get(t).get(i) == 1) {
                    Rectangle tmp = new Rectangle(t * (TILE_WIDTH), i * TILE_HEIGHT - 6, TILE_WIDTH, TILE_HEIGHT);
                    Rectangle objRect = new Rectangle(object.hitBox.x, object.hitBox.y+1, object.hitBox.width, object.hitBox.height-1);

                    if (tmp.overlaps(objRect)) {
                        fall = false;
                        break;
                    }
                }
            }
            if (!fall) break;
        }
        if (fall) object.falling = true;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void moveCamera(int k) {
        cameraX += (player.graphicX + player.hitBox.getWidth()/2 - cameraX)/k;
        cameraY += (player.graphicY + player.hitBox.getHeight()/2 - cameraY)/k;
        if (TILE_WIDTH*(width)*zoom > camera.getWidth()) {
            if (cameraX - Gdx.graphics.getWidth()/zoom/2 < 1) {
                cameraX = Gdx.graphics.getWidth()/zoom/2+1;
            } else if (cameraX + Gdx.graphics.getWidth()/zoom/2 > TILE_WIDTH*(width)) {
                cameraX = TILE_WIDTH*(width) - Gdx.graphics.getWidth()/zoom/2;
            }
        }
        float a = TILE_HEIGHT*(height)*zoom;
        float b = camera.getHeight();
        int off = 2;
        if (platformMode) {
            off = 1;
        }
        if (TILE_HEIGHT*(height-off)*zoom > camera.getHeight()) {
            if (cameraY - Gdx.graphics.getHeight() / zoom / 2 + TILE_HEIGHT < 0) {
                cameraY = Gdx.graphics.getHeight() / zoom / 2 - TILE_HEIGHT;
            } else if (cameraY + Gdx.graphics.getHeight() / zoom / 2 + FLOOR_HEIGHT + 2 > TILE_HEIGHT * (height - off)) {
                cameraY = TILE_HEIGHT * (height - off) - Gdx.graphics.getHeight() / zoom / 2 - FLOOR_HEIGHT - 2;
            }
        }
        cameraX = round(cameraX, 1);
        cameraY = round(cameraY, 1);
    }

    public void invalidate() {
        //if (player.z < 0.5f) {
            if (!platformMode) {
                player.move();
            } else {
                player.platformMove();
            }
        //}

        for (int i=0; i<objects.size(); ++i) {
            if (objects.get(i).getClass() == Entity.class) continue;
            float old = ((HittableEntity)objects.get(i)).hitBox.y;
            if (!platformMode) {
                objects.get(i).fall();
            } else if (objects.get(i).getClass()!=Player.class) {
                //objects.get(i).platformFall();
                objects.get(i).platformFall();
            }
            if (((HittableEntity)objects.get(i)).movable && objects.get(i).getClass()!=Player.class && !((HittableEntity)objects.get(i)).falling) {
                invalidateCollisions((HittableEntity)objects.get(i), ((HittableEntity)objects.get(i)).oldX, ((HittableEntity)objects.get(i)).oldY);
            }
            if (((HittableEntity)objects.get(i)).hitBox.y < old) {
                ((HittableEntity)objects.get(i)).pSpeed = 0;
            }
        }

        if (!platformMode) {
            player.fall();
        } else {
            //player.platformFall();
            player.platformFall();
        }

        invalidateCollisions(player, player.oldX, player.oldY);
        playerTileX = (int)(player.hitBox.x/(TILE_WIDTH))+1;
        playerTileY = (int)((player.hitBox.y)/(TILE_HEIGHT))+1;


        for (int i=0; i<objects.size(); ++i) {
            //System.out.println(objects.get(i).y);
            //if (objects.get(i).getClass() == Player.class) System.out.println(objects.get(i).y+" p");
            //else System.out.println(objects.get(i).y);
            if (objects.get(i).getClass() == Entity.class) continue;
            if (((HittableEntity)objects.get(i)).movable && objects.get(i).getClass() != Player.class && !((HittableEntity)objects.get(i)).falling) {
                invalidateCollisions((HittableEntity)objects.get(i), ((HittableEntity)objects.get(i)).oldX, ((HittableEntity)objects.get(i)).oldY);
            }
        }
        //System.out.println("---------------------------");

        for (int i=objects.size()-1; i>=0; --i) {
            if (objects.get(i).getClass() == Entity.class) continue;
            if (((HittableEntity)objects.get(i)).movable && (objects.get(i).getClass()!=Player.class || platformMode) && !((HittableEntity)objects.get(i)).falling) {
                invalidateCollisions((HittableEntity)objects.get(i), ((HittableEntity)objects.get(i)).oldX, ((HittableEntity)objects.get(i)).oldY);
            }
            if (!platformMode) {
                checkFall((HittableEntity)objects.get(i));
                if (((HittableEntity)objects.get(i)).z > cameraY + Gdx.graphics.getHeight()) {
                    if (objects.get(i).getClass() != Player.class) {
                        objects.remove(i);
                    } else {
                        respawnPlayer(null, assets, 0, 0, 0, 0);
                    }
                }
            }
        }

        if (player.pusher) {
            Player pushField = new Player(assets, null, player.hitBox.x, player.hitBox.y, player.hitBox.width, player.hitBox.height, 3, false);
            pushField.speedX = -player.speedX*10;
            pushField.speedY = -player.speedY*10;
            for (int i=0; i<objects.size(); ++i) {
                if (objects.get(i).getClass() == Entity.class) continue;
                if (objects.get(i).getClass() == Player.class) {
                    continue;
                }
                pushField.pushOutSolidObjects((HittableEntity)objects.get(i), this, pushField.oldX, pushField.oldY);
                //objects.get(i).canDown = true;
                //objects.get(i).canUp = true;
                //objects.get(i).canLeft = true;
                //objects.get(i).canRight = true;
            }
            invalidateCollisions(pushField, pushField.oldX, pushField.oldY);
        }
   }

    public void draw(SpriteBatch batch, World world, float offsetX, float offsetY, boolean drawPlayer, boolean drawBG, CharacterMaker characterMaker) {

        if (drawBG) {
            batch.draw(world.bg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.PLUS) && zoom < 5) zoom += 1;
        else if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS) && zoom > 1) zoom -= 1;
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

        moveCamera(5);

        offsetX += -cameraX + Gdx.graphics.getWidth()/2;
        offsetY += cameraY + Gdx.graphics.getHeight()/2;


        Collections.sort(objects);
        if (!platformMode) {
            for (int i = -1; i <= height + 1; ++i) {
                for (int t = 0; t < width; ++t) {
                    if (i >= 0 && i < height) {

                        if (blocks.get(0).get(t).get(i) >= 0) {
                            drawLayer(batch, world, 0, offsetX, offsetY, i, t);
                        }

                    }
                    if (i > 0 && i < height+1 && blocks.get(2).get(t).get(i - 1) == 2) {
                        drawLayer(batch, world, 1, offsetX, offsetY, i-1, t);
                    }

                }
                for (int z = 0; z < objects.size(); ++z) {
                    //if (objects.get(z).getClass() == Entity.class) continue;
                    if (objects.get(z).getClass() == Entity.class || ((HittableEntity)objects.get(z)).falling && objects.get(z).getClass() != Player.class) {
                        int objectTileY=0;
                        int objectTileX=0;
                        if (objects.get(z).getClass() != Entity.class) {
                            objectTileY = (int) ((objects.get(z).getRect().y + FLOOR_HEIGHT) / (TILE_HEIGHT));
                            objectTileX = (int) ((objects.get(z).getRect().x + objects.get(z).getRect().getWidth() / 2) / (TILE_WIDTH));
                            if (i == objectTileY + 1) {
                                //System.out.println(blocks.get(objectTileX).get(i - 1) + " " + blocks.get(objectTileX).get(i) + " " + blocks.get(objectTileX).get(i + 1));
                                //System.out.println(blocks.get(objectTileX + 1).get(i - 1) + " " + blocks.get(objectTileX + 1).get(i) + " " + blocks.get(objectTileX + 1).get(i + 1));
                                boolean up = i==2 || blocks.get(2).get(objectTileX).get(i - 1)==2;
                                boolean w_up = i==2 || blocks.get(2).get(objectTileX).get(i - 1)!=0;
                                boolean down = i==height+1 || blocks.get(2).get(objectTileX).get(i+1)==2;
                                boolean w_down = i==height+1 || blocks.get(2).get(objectTileX).get(i+1)!=0;
                                boolean left = objectTileX==0 || blocks.get(2).get(objectTileX - 1).get(i)==2;
                                boolean w_left = objectTileX==0 || blocks.get(2).get(objectTileX - 1).get(i)!=0;
                                boolean right = objectTileX==width-1 || blocks.get(2).get(objectTileX + 1).get(i)==2;
                                boolean w_right = objectTileX==width-1 || blocks.get(2).get(objectTileX + 1).get(i)!=0;
                                objects.get(z).draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT), platformMode);

                                if (white != null) {
                                    if (i > 0 && blocks.get(2).get(objectTileX).get(i) == 2) {
                                        batch.draw(black.getTile(up, down, left, right), offsetX + objectTileX * (TILE_WIDTH), offsetY - (i) * TILE_HEIGHT + 4, black.getWidth(), black.getHeight());
                                    } else if (i > 0 && blocks.get(2).get(objectTileX).get(i) == 1) {
                                        batch.draw(white.getTile(w_up, w_down, w_left, w_right), offsetX + objectTileX * (TILE_WIDTH), offsetY - (i) * TILE_HEIGHT, white.getWidth(), white.getHeight());
                                    }
                                }
                            }
                        }
                    }
                }
                for (int z = 0; z < objects.size(); ++z) {
                    float objectTileY=0;
                    //if (objects.get(z).getClass() == Entity.class) continue;
                    if (objects.get(z).getClass() == Entity.class || !((HittableEntity)objects.get(z)).falling) {
                        if (objects.get(z).getClass() != Entity.class) {
                            objectTileY = (int) ((objects.get(z).getRect().y + FLOOR_HEIGHT) / (TILE_HEIGHT)) + 1;
                        } else {
                            objectTileY = (int) ((objects.get(z).y) / (TILE_HEIGHT))+2;
                        }
                    } else {
                        objectTileY = (int) ((objects.get(z).getRect().y + FLOOR_HEIGHT) / (TILE_HEIGHT));
                    }
                    if (i == objectTileY/* || (objects.get(z).falling && objectTileY < playerTileY && objects.get(z).y > player.y && i == playerTileY)*/ &&  (objects.get(z).getClass() == Entity.class || (!((HittableEntity)objects.get(z)).falling || objects.get(z).getClass() == Player.class))) {
                        if (objects.get(z).getClass() == Player.class) {
                            if (drawPlayer) {
                                //player.draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT));
                                player.draw(batch, characterMaker, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT));
                                //characterMaker.draw(batch, 0, 100, 100);
                            }
                        } else {
                            objects.get(z).draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT), platformMode);
                        }
                    }

                }
                for (int z = 0; z < objects.size(); ++z) {
                    //if (objects.get(z).getClass() == Entity.class) continue;
                    //if (objects.get(z).getClass() == Entity.class || ((HittableEntity)objects.get(z)).falling)
                        //System.out.println(objects.get(z).hitBox.y - objects.get(z).hitBox.height + " " + player.hitBox.y);
                    if (objects.get(z).getClass() != Entity.class && ((HittableEntity)objects.get(z)).falling && objects.get(z).getClass() != Player.class && ((HittableEntity)objects.get(z)).hitBox.y - ((HittableEntity)objects.get(z)).hitBox.height / 2 >= player.hitBox.y) {
                        float objectTileY = 0;
                        if (objects.get(z).getClass() != Entity.class) objectTileY = (int) ((objects.get(z).getRect().y + FLOOR_HEIGHT) / (TILE_HEIGHT) + 1);
                        else objectTileY = (int) ((objects.get(z).y + FLOOR_HEIGHT) / (TILE_HEIGHT) + 1);
                        if (i == objectTileY) {
                            objects.get(z).draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT), platformMode);
                        }

                    }
                }

            }

            //System.out.println(player.hitBox.y);
        } else {
            for (int i = -1; i <= height + 1; ++i) {
                for (int t = 0; t < width; ++t) {
                    if (i >= 0 && i < height) {

                        if (blocks.get(0).get(t).get(i) >= 0) {
                            drawLayer(batch, world, 0, offsetX, offsetY, i, t);
                        }

                    }
                    if (i > 0 && i < height + 1 && blocks.get(2).get(t).get(i - 1) == 2) {
                        drawLayer(batch, world, 1, offsetX, offsetY, i - 1, t);
                    }

                }
            }
            /*for (int i = 0; i < height; ++i) {
                for (int t = 0; t < width; ++t) {
                    //if (i >= 0 && i < height) {
                        if (i > 0 && i < height+1 && blocks.get(2).get(t).get(i - 1) == 2) {
                            int type = blocks.get(1).get(t).get(i);
                            boolean up = i==0 || blocks.get(1).get(t).get(i - 1)==type;
                            boolean down = i==height-1 || blocks.get(1).get(t).get(i+1)==type;
                            boolean left = t==0 || blocks.get(1).get(t - 1).get(i)==type;
                            boolean right = t==width-1 || blocks.get(1).get(t + 1).get(i)==type;
                            try {
                            batch.draw(black.getTile(up, down, left, right), offsetX + t * (TILE_WIDTH), offsetY - (i - 1) * TILE_HEIGHT + 4, black.getWidth(), black.getHeight());
                            } catch (Exception e) {
                                System.out.println();
                            }
                        }
                    //}
                }
            }*/
            for (int z = 0; z < objects.size(); ++z) {
                if (objects.get(z).getClass() == Player.class) {
                    if (drawPlayer) {
                        player.draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT), platformMode);
                    }
                } else {
                    objects.get(z).draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT), platformMode);
                }

            }
        }
        //fps.log();
        //characterMaker.draw(batch, 0, 100, 100);
        transform = new Matrix4();
        batch.setTransformMatrix(transform);
    }

    private void drawLayer(SpriteBatch batch, World world, int layer, float offsetX, float offsetY, int i, int t) {
        int type = blocks.get(layer).get(t).get(i);
        if (type == -1) return;
        boolean up = i==0 || blocks.get(layer).get(t).get(i - 1)==type;
        boolean down = i==height-1 || blocks.get(layer).get(t).get(i+1)==type;
        boolean left = t==0 || blocks.get(layer).get(t - 1).get(i)==type;
        boolean right = t==width-1 || blocks.get(layer).get(t + 1).get(i)==type;
        try {
            //
            //type = blocks.get(0).get(t).get(i)-world.spritesCount;
            if (white != null) {
                float x = offsetX + t * (TILE_WIDTH);
                float y = offsetY - (i) * TILE_HEIGHT;
                if (layer == 0) batch.draw(white.getTile(up, down, left, right), x,y, white.getWidth(), white.getHeight());
                else batch.draw(black.getTile(up, down, left, right), x, y+4, black.getWidth(), black.getHeight());
            } else {
                if (blocks.get(layer).get(t).get(i) >= world.spritesCount) {
                    TextureRegion img = world.tiles.get(blocks.get(layer).get(t).get(i)-world.spritesCount).getTile(up, down, left, right);
                    float x = offsetX + t * (TILE_WIDTH) + TILE_WIDTH/2 - img.getRegionWidth()/2;
                    float y = offsetY - i * TILE_HEIGHT-img.getRegionHeight()+TILE_HEIGHT;
                    float y2 = 0;
                    if (!platformMode) {
                        y2 = offsetY - i * TILE_HEIGHT;
                    } else {
                        y+=FLOOR_HEIGHT/2+1;
                        if (layer == 0) {
                            y-=TILE_HEIGHT-img.getRegionHeight();
                        }
                        y2 = offsetY - i * TILE_HEIGHT+FLOOR_HEIGHT/2+1;
                    }
                    if (layer == 0)batch.draw(img, x, y, img.getRegionWidth(), img.getRegionHeight());
                    else batch.draw(img, x, y2, img.getRegionWidth(), img.getRegionHeight());
                } else {
                    Texture img = world.sprites.get(blocks.get(layer).get(t).get(i));
                    float x = offsetX + t * (TILE_WIDTH) + TILE_WIDTH/2 - img.getWidth()/2;
                    float y = offsetY - i * TILE_HEIGHT-img.getHeight()+TILE_HEIGHT;
                    float y2 = 0;
                    if (!platformMode) {
                        y2 = offsetY - i * TILE_HEIGHT;
                    } else {
                        y+=FLOOR_HEIGHT/2+1;
                        if (layer == 0) {
                            y-=TILE_HEIGHT-img.getHeight();
                        }
                        y2 = offsetY - i * TILE_HEIGHT+FLOOR_HEIGHT/2+1;
                    }
                    if (layer == 0) batch.draw(img, x, y, img.getWidth(), img.getHeight());
                    else  {
                        batch.draw(img, x, y2, img.getWidth(), img.getHeight());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
