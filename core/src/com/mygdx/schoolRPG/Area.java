package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.zip.ZipFile;

/**
 * Created by user on 06.08.2014.
 */
public class Area {
    int TILE_HEIGHT = 16, TILE_WIDTH = 32, FLOOR_HEIGHT = 6;
    float zoom = 2.0f;
    boolean platformMode = false;
    //int id, nextId;
    String name;
    ArrayList<ArrayList<Integer>> blocks;
    ArrayList<HittableEntity> objects;
    Texture block, shadow, dung;
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
            playerWidth = 26;
            playerHeight = 12;
            playerFloor = FLOOR_HEIGHT/4+1;
        }

        //this.id = id;
        //nextId = id;
        blocks = new ArrayList<ArrayList<Integer>>();
        objects = new ArrayList<HittableEntity>();
        camera = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (map != null) {
            width = map.getWidth();
            height = map.getHeight();
            for (int i = 0; i < map.getWidth(); ++i) {
                blocks.add(new ArrayList<Integer>());
                for (int t=0; t<map.getHeight(); ++t) {
                    //blocks.get(i).add(map.getPixel(i, t));
                    Color curPixelColor = new Color(map.getPixel(i, t));
                    if (curPixelColor.a == 1.0f) {
                        if (curPixelColor.equals(Color.GREEN)) {
                            playerTileY = t;
                            playerTileX = i;
                            blocks.get(i).add(1);
                        } else if (curPixelColor.equals(Color.RED)) {
                            objects.add(new HittableEntity(assets, world.folderPath+"/sprites/red.png", i*TILE_WIDTH, (t * TILE_HEIGHT - FLOOR_HEIGHT)-1, TILE_WIDTH, TILE_HEIGHT, FLOOR_HEIGHT/4+1, true));
                            blocks.get(i).add(1);
                        } else if (curPixelColor.equals(Color.BLUE)) {
                            objects.add(new HittableEntity(assets, world.folderPath+"/sprites/blue.png", i*TILE_WIDTH+TILE_WIDTH/2-2.5f, (t * TILE_HEIGHT), 5, 4, -TILE_HEIGHT/2, false));
                            blocks.get(i).add(1);
                        } else if (curPixelColor.equals(Color.YELLOW)) {
                            objects.add(new HittableEntity(assets, world.folderPath+"/sprites/yellow.png", i*TILE_WIDTH+TILE_WIDTH/4, (t* TILE_HEIGHT-FLOOR_HEIGHT), TILE_WIDTH/2, TILE_HEIGHT/1.6f, -FLOOR_HEIGHT/2+1, true));
                            blocks.get(i).add(1);
                        } else if (curPixelColor.equals(Color.WHITE)) {
                            blocks.get(i).add(1);
                        } else if (curPixelColor.equals(Color.BLACK)) {
                            blocks.get(i).add(2);
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
                            blocks.get(i).add(1);
                        } else {
                            blocks.get(i).add(0);
                        }
                    } else {
                        blocks.get(i).add(0);
                    }
                }
            }
        }
    }

    public Area(World world, byte[] map, boolean platformMode, int tileWidth, int tileHeight) {
        TILE_WIDTH = tileWidth;
        TILE_HEIGHT = tileHeight;
        this.platformMode = platformMode;

        /*this.assets = assets;
        //ZipFile zip;
        InputStream is;
        try {
            is = new FileInputStream(file);
            int nameSize = is.read();
            byte[] buff = new byte[nameSize];
            name = new String(buff);
            platformMode = (is.read()>0);
            width = is.read();
            height = is.read();
            TILE_HEIGHT = is.read();
            TILE_WIDTH = is.read();
            playerWidth = is.read();
            playerHeight = is.read();
            blocks = new ArrayList<ArrayList<Integer>>();
            for (int i = 0; i < width; ++i) {
                blocks.add(new ArrayList<Integer>());
                for (int t=0; t<height; ++t) {
                    blocks.get(i).set(t, is.read());
                }
            }
            objects = new ArrayList<HittableEntity>();
            camera = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!platformMode) {
            playerFloor = FLOOR_HEIGHT/4+1;
        }*/

        //this.id = id;
        //nextId = id;
    }

    public void load(AssetManager assets, World world) {
        //this.assets = assets;
        //if(objects.size() > 0) objects.get(0).load(assets);
        //assets.load("block.png", Texture.class);
        //assets.load("block_high.png", Texture.class);
        //assets.load("shadow.png", Texture.class);
        if (world.worldDir.child("sprites/red.png").exists()) {
            assets.load(world.folderPath+"/sprites/red.png", Texture.class);
        }
        if (world.worldDir.child("sprites/yellow.png").exists()) {
            assets.load(world.folderPath + "/sprites/yellow.png", Texture.class);
        }
        if (world.worldDir.child("sprites/blue.png").exists()) {
            assets.load(world.folderPath + "/sprites/blue.png", Texture.class);
        }
            if (world.worldDir.child("sprites/black.png").exists()) {
            black = new BlockMultiTile(world.folderPath + "/sprites/black.png", assets);
        }
        if (world.worldDir.child("sprites/white.png").exists()) {
            white = new BlockMultiTile(world.folderPath+"/sprites/white.png", assets);
        }
        //assets.load("arrow.png", Texture.class);
        //assets.load("char.png", Texture.class);
        //assets.load("char_back.png", Texture.class);
        //assets.load("char_left.png", Texture.class);
        //assets.load("char_right.png", Texture.class);
        loaded = true;
    }

    public void initialiseResources(AssetManager assets, String worldDir) {
        if (!initialised) {
            //block = assets.get("block.png");
            if (black != null){
                black.initialiseIfNeeded(assets);
            }
            if (white!=null) {
                white.initialiseIfNeeded(assets);
            }
            //shadow = assets.get("shadow.png");
            if (player == null) {
                player = new Player(assets, worldDir+"/sprites/char.png", (playerTileX*TILE_WIDTH), ((playerTileY)*TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true);
                lastSpawnTileX = playerTileX;
                lastSpawnTileY = playerTileY;
                lastSpawnPos = 0;
            }
            cameraX = player.graphicX + player.hitBox.getWidth()/2;
            cameraY = player.graphicY + player.hitBox.getHeight()/2;
            /*if(objects.size() > 0) {
                for (int i =0; i<objects.size(); ++i) {
                    objects.get(i).initialise(assets);
                }
            }*/
            objects.add(player);
        }
        initialised = true;
    }

    public void respawnPlayer(String worldDir, int tileX, int tileY, float pos, int speed) {
        if (player == null) {
            player = new Player(assets, worldDir+"/sprites/char.png", (playerTileX) * TILE_WIDTH, (playerTileY) * TILE_HEIGHT, playerWidth, playerHeight, playerFloor, true);
        }
        if (tileX != 0 || tileY != 0 || pos != 0) {
            lastSpawnTileX = tileX;
            lastSpawnTileY = tileY;
            lastSpawnPos = pos;
        }
        //player = new Player(assets, "char", (playerTileX-1)*TILE_WIDTH+TILE_WIDTH/2-11, (playerTileY)* TILE_HEIGHT-TILE_HEIGHT/2+4, 22, 8, (FLOOR_HEIGHT/2), true);
        if (tileX == 0 && tileY == 0 && pos == 0) {
            respawnPlayer(null, lastSpawnTileX, lastSpawnTileY, lastSpawnPos, 0);
        } else if (pos != 0) {
            if (tileX == -1) {
                player.hitBox.x = 5;
                if (platformMode) {
                    player.speedX = speed;
                }
                player.curPose = player.poses.getTile(PlayerMultiTile.PlayerPose.RIGHT);
            } else if (tileX == 1) {
                player.hitBox.x = TILE_WIDTH * (width - 1) + 5;
                if (platformMode) {
                    player.speedX = speed;
                }
                player.speedY = 0;
                player.curPose = player.poses.getTile(PlayerMultiTile.PlayerPose.LEFT);
            } else if (tileY == 1) {
                player.hitBox.y = 5;
                if (platformMode) {
                    player.speedY = speed;
                }
                player.speedX = 0;
                player.curPose = player.poses.getTile(PlayerMultiTile.PlayerPose.FRONT);
            } else if (tileY == -1) {
                player.hitBox.y = TILE_HEIGHT * (height - 2) - 5;
                //player.speedY = speed;
                if (platformMode) {
                    player.speedY = speed;
                }
                player.speedX = 0;
                player.curPose = player.poses.getTile(PlayerMultiTile.PlayerPose.BACK);
            }
            if (tileY == 0 && tileX != 0) {
                player.hitBox.y = pos;
            } else if (tileY != 0 && tileX == 0) {
                player.hitBox.x = pos;
            }
        } else {
            player.hitBox.x = (lastSpawnTileX*TILE_WIDTH);
            player.hitBox.y = (lastSpawnTileY*TILE_HEIGHT);
            player.curPose = player.poses.getTile(PlayerMultiTile.PlayerPose.FRONT);
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

    public void invalidate(HittableEntity he, float oldX, float oldY) {
        boolean isPlayer = false;
        if (he.getClass() == Player.class) isPlayer = true;

        int tileX = (int)(he.hitBox.x/(TILE_WIDTH))+1;
        int tileY = (int)(he.hitBox.y/(TILE_HEIGHT))+1;
        if (tileX < 1 || tileY < 1 || tileX >= blocks.size()+1 || tileY >= blocks.get(0).size()) {
            return;
        }
        if (platformMode && he.pSpeed >= 0) {
            tileY++;
        }
        Rectangle oldRect = new Rectangle(he.hitBox);
        for (int z=0; z<objects.size(); ++z) {
            if (!objects.get(z).falling) {
                if (platformMode) {
                    //he.hitBox = objects.get(z).invalidate(he.hitBox, this, player.oldX, player.oldY, true, isPlayer);
                    he.hitBox = objects.get(z).invalidate(he.hitBox, this, player.oldX, he.oldY, true, isPlayer);
                    //he.hitBox = objects.get(z).invalidate(he.hitBox, this, player.oldX, player.oldY, true, isPlayer);
                    if (he.hitBox.y < oldRect.y && he.pSpeed > 0) {
                        he.pSpeed=0;
                    } else if (he.hitBox.y > oldRect.y && he.pSpeed < 0) {
                        he.pSpeed=1;
                    }
                } else {
                    he.hitBox = objects.get(z).invalidate(he.hitBox, this, player.oldX, player.oldY, true, isPlayer);
                }
            }
        }
        oldRect = new Rectangle(he.hitBox);
        for (int i = tileY-1; i <= tileY+1; ++i) {
            for (int t = tileX-1; t <= tileX+1; ++t) {
                if (t < 0 || t >= blocks.size() || i < 0 || i >= blocks.size()) break;
                if (blocks.get(t).get(i) == 2/* || blocks.get(t).get(i) == 0*/) {
                    HittableEntity tmp = new HittableEntity(assets, null, t*(TILE_WIDTH), i* TILE_HEIGHT -6, TILE_WIDTH, TILE_HEIGHT, 3, false);
                    boolean left = t > 0 && blocks.get(t-1).get(i) != 2/* && blocks.get(t-1).get(i) != 0*/;
                    boolean right = t < blocks.size()-1 && blocks.get(t+1).get(i) != 2/* && blocks.get(t+1).get(i) != 0*/;
                    boolean up = i < blocks.get(i).size()-1 && blocks.get(t).get(i+1) != 2/* && blocks.get(t).get(i+1) != 0*/;
                    boolean down = i > 0 && blocks.get(t).get(i-1) != 2/* && blocks.get(t).get(i-1) != 0*/;
                    tmp.setSides(left, right, down, up);
                    if (isPlayer) {
                        he.hitBox = tmp.invalidate(he.hitBox, this, player.oldX, player.oldY, true, true);
                    } else {
                        if (!platformMode) {
                            he.hitBox = tmp.invalidate(he.hitBox, this, player.oldX, he.oldY, true, false);
                            he.hitBox = tmp.invalidate(he.hitBox, this, oldX, oldY, true, false);
                        } else {
                            he.hitBox = tmp.invalidate(he.hitBox, this, player.oldX, he.oldY, true, false);
                            he.hitBox = tmp.invalidate(he.hitBox, this, he.oldX, he.oldY, true, false);
                            he.hitBox = tmp.invalidate(he.hitBox, this, oldX, oldY, true, false);
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

        return;
    }

    public void checkFall(HittableEntity object) {
        boolean fall = true;
        for (int i = 0; i < width; ++i) {
            for (int t = 0; t < height; ++t) {
                if (blocks.get(t).get(i) == 1) {
                    Rectangle tmp = new Rectangle(t * (TILE_WIDTH), i * TILE_HEIGHT - 6, TILE_WIDTH, TILE_HEIGHT-FLOOR_HEIGHT/2);
                    Rectangle objRect = new Rectangle(object.hitBox.x+1, object.hitBox.y-1, object.hitBox.width-1, object.hitBox.height+1);

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


    public static Comparator<Entity> ObjectsComparator
            = new Comparator<Entity>() {

        public int compare(Entity e1, Entity e2) {

            return (int)(e1.y - e2.y);
        }

    };

    public void moveCamera(int k) {
        cameraX += (player.graphicX + player.hitBox.getWidth()/2 - cameraX)/k;
        cameraY += (player.graphicY + player.hitBox.getHeight()/2 - cameraY)/k;
        if (cameraX - Gdx.graphics.getWidth()/zoom/2 < 1) {
            cameraX = Gdx.graphics.getWidth()/zoom/2+1;
        } else if (cameraX + Gdx.graphics.getWidth()/zoom/2 > TILE_WIDTH*(width)) {
            cameraX = TILE_WIDTH*(width) - Gdx.graphics.getWidth()/zoom/2;
        }
        if (cameraY - Gdx.graphics.getHeight()/zoom/2 + TILE_HEIGHT < 0) {
            cameraY = Gdx.graphics.getHeight()/zoom/2 - TILE_HEIGHT;
        } else if (cameraY + Gdx.graphics.getHeight()/zoom/2+FLOOR_HEIGHT+2 > TILE_HEIGHT*(height-2)) {
            cameraY = TILE_HEIGHT*(height-2) - Gdx.graphics.getHeight()/zoom/2 - FLOOR_HEIGHT-2;
        }
    }

    public void draw(SpriteBatch batch, float offsetX, float offsetY, boolean drawPlayer) {
        //camera.s
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
        if (offsetX == 0 && offsetY == 0) {
            if (player.z < 0.5f) {
                if (!platformMode) {
                    player.move();
                } else {
                    player.platformMove();
                }
            }

            for (int i=0; i<objects.size(); ++i) {
                if (!platformMode) {
                    objects.get(i).fall();
                } else if (objects.get(i).getClass()!=Player.class) {
                    objects.get(i).platformFall();
                    objects.get(i).platformFall();
                }
                float old = objects.get(i).hitBox.y;
                if (objects.get(i).movable && objects.get(i).getClass()!=Player.class && !objects.get(i).falling) {
                    invalidate(objects.get(i), objects.get(i).oldX, objects.get(i).oldY);
                }
                if (objects.get(i).hitBox.y < old) {
                    objects.get(i).pSpeed = 0;
                }
            }

            if (!platformMode) {
                player.fall();
            } else {
                player.platformFall();
                player.platformFall();
            }

            invalidate(player, player.oldX, player.oldY);
            playerTileX = (int)(player.hitBox.x/(TILE_WIDTH))+1;
            playerTileY = (int)((player.hitBox.y)/(TILE_HEIGHT))+1;

            for (int i=0; i<objects.size(); ++i) {
                if (objects.get(i).movable && objects.get(i).getClass() != Player.class && !objects.get(i).falling) {
                    invalidate(objects.get(i), objects.get(i).oldX, objects.get(i).oldY);
                }
            }

            for (int i=objects.size()-1; i>=0; --i) {
                if (objects.get(i).movable && (objects.get(i).getClass()!=Player.class || platformMode) && !objects.get(i).falling) {
                    invalidate(objects.get(i), objects.get(i).oldX, objects.get(i).oldY);
                }
                if (!platformMode) {
                    checkFall(objects.get(i));
                    if (objects.get(i).z > cameraY + Gdx.graphics.getHeight()) {
                        if (objects.get(i).getClass() != Player.class) {
                            objects.remove(i);
                        } else {
                            respawnPlayer(null, 0, 0, 0, 0);
                        }
                    }
                }
            }

        }
        offsetX += -cameraX + Gdx.graphics.getWidth()/2;
        offsetY += cameraY + Gdx.graphics.getHeight()/2;

        Collections.sort(objects, ObjectsComparator);
        if (!platformMode) {
            for (int i = -1; i <= height + 1; ++i) {
                for (int t = 0; t < width; ++t) {
                    if (i >= 0 && i < height) {

                        if (blocks.get(t).get(i) == 1 || blocks.get(t).get(i) == 2) {
                            boolean up = i==0 || blocks.get(t).get(i - 1)!=0;
                            boolean down = i==height-1 || blocks.get(t).get(i+1)!=0;
                            boolean left = t==0 || blocks.get(t-1).get(i)!=0;
                            boolean right = t==width-1 || blocks.get(t+1).get(i)!=0;
                            batch.draw(white.getTile(up, down, left, right), offsetX + t * (TILE_WIDTH), offsetY - i * TILE_HEIGHT, white.getWidth(), white.getHeight());
                        }

                    }
                    if (i > 0 && i < height+1 && blocks.get(t).get(i - 1) == 2) {
                        boolean up = i==1 || blocks.get(t).get(i - 2)==2;
                        boolean down = i==height || blocks.get(t).get(i)==2;
                        boolean left = t==0 || blocks.get(t-1).get(i - 1)==2;
                        boolean right = t==width-1 || blocks.get(t+1).get(i - 1)==2;
                        batch.draw(black.getTile(up, down, left, right), offsetX + t * (TILE_WIDTH), offsetY - (i - 1) * TILE_HEIGHT + 4, black.getWidth(), black.getHeight());
                    }

                }
                for (int z = 0; z < objects.size(); ++z) {
                    if (objects.get(z).falling && objects.get(z).getClass() != Player.class) {
                        int objectTileY = (int) ((objects.get(z).getRect().y + FLOOR_HEIGHT) / (TILE_HEIGHT));
                        int objectTileX = (int) ((objects.get(z).getRect().x + objects.get(z).getRect().getWidth() / 2) / (TILE_WIDTH));
                        if (i == objectTileY + 1) {
                            //System.out.println(blocks.get(objectTileX).get(i - 1) + " " + blocks.get(objectTileX).get(i) + " " + blocks.get(objectTileX).get(i + 1));
                            //System.out.println(blocks.get(objectTileX + 1).get(i - 1) + " " + blocks.get(objectTileX + 1).get(i) + " " + blocks.get(objectTileX + 1).get(i + 1));
                            boolean up = i==2 || blocks.get(objectTileX).get(i - 1)==2;
                            boolean w_up = i==2 || blocks.get(objectTileX).get(i - 1)!=0;
                            boolean down = i==height+1 || blocks.get(objectTileX).get(i+1)==2;
                            boolean w_down = i==height+1 || blocks.get(objectTileX).get(i+1)!=0;
                            boolean left = objectTileX==0 || blocks.get(objectTileX-1).get(i)==2;
                            boolean w_left = objectTileX==0 || blocks.get(objectTileX-1).get(i)!=0;
                            boolean right = objectTileX==width-1 || blocks.get(objectTileX+1).get(i)==2;
                            boolean w_right = objectTileX==width-1 || blocks.get(objectTileX+1).get(i)!=0;
                            objects.get(z).draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT));

                            if (i > 0 && blocks.get(objectTileX).get(i) == 2) {
                                batch.draw(black.getTile(up, down, left, right), offsetX + objectTileX * (TILE_WIDTH), offsetY - (i) * TILE_HEIGHT + 4, black.getWidth(), black.getHeight());
                            } else if (i > 0 && blocks.get(objectTileX).get(i) == 1) {
                                batch.draw(white.getTile(w_up, w_down, w_left, w_right), offsetX + objectTileX * (TILE_WIDTH), offsetY - (i) * TILE_HEIGHT, white.getWidth(), white.getHeight());
                            }
                        }
                    }
                }
                for (int z = 0; z < objects.size(); ++z) {
                    float objectTileY;
                    if (!objects.get(z).falling) {
                        objectTileY = (int) ((objects.get(z).getRect().y + FLOOR_HEIGHT) / (TILE_HEIGHT)) + 1;
                    } else {
                        objectTileY = (int) ((objects.get(z).getRect().y + FLOOR_HEIGHT) / (TILE_HEIGHT));
                    }
                    if (i == objectTileY/* || (objects.get(z).falling && objectTileY < playerTileY && objects.get(z).y > player.y && i == playerTileY)*/ && (!objects.get(z).falling || objects.get(z).getClass() == Player.class)) {
                        if (objects.get(z).getClass() == Player.class) {
                            if (drawPlayer) {
                                player.draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT));
                            }
                        } else {
                            objects.get(z).draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT));
                        }
                    }

                }
                for (int z = 0; z < objects.size(); ++z) {
                    if (objects.get(z).falling)
                        //System.out.println(objects.get(z).hitBox.y - objects.get(z).hitBox.height + " " + player.hitBox.y);
                    if (objects.get(z).falling && objects.get(z).getClass() != Player.class && objects.get(z).hitBox.y - objects.get(z).hitBox.height / 2 >= player.hitBox.y) {
                        float objectTileY = (int) ((objects.get(z).getRect().y + FLOOR_HEIGHT) / (TILE_HEIGHT) + 1);
                        if (i == objectTileY) {
                            objects.get(z).draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT));
                        }

                    }
                }

            }
            //System.out.println(player.hitBox.y);
        } else {
            for (int i = -1; i <= height + 1; ++i) {
                for (int t = 0; t < width; ++t) {
                    //if (i >= 0 && i < height) {
                        if (i > 0 && i < height+1 && blocks.get(t).get(i - 1) == 2) {
                            boolean up = i==1 || blocks.get(t).get(i - 2)==2;
                            boolean down = i==height || blocks.get(t).get(i)==2;
                            boolean left = t==0 || blocks.get(t-1).get(i - 1)==2;
                            boolean right = t==width-1 || blocks.get(t+1).get(i - 1)==2;
                            batch.draw(black.getTile(up, down, left, right), offsetX + t * (TILE_WIDTH), offsetY - (i - 1) * TILE_HEIGHT + 4, black.getWidth(), black.getHeight());
                        }
                    //}
                }
            }
            for (int z = 0; z < objects.size(); ++z) {
                if (objects.get(z).getClass() == Player.class) {
                    if (drawPlayer) {
                        player.draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT));
                    }
                } else {
                    objects.get(z).draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT));
                }

            }
        }
        //fps.log();
        transform = new Matrix4();
        batch.setTransformMatrix(transform);
    }

}
