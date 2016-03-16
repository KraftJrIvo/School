package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.schoolRPG.particles.*;
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


    float lastSpawnPos;
    Texture shadow;
    boolean saved = false;

    public Area(int x, int y, int z, int w, int h, byte[] map, int width, int height , int tileWidth, int tileHeight, boolean platformMode) {
        TILE_WIDTH = tileWidth;
        TILE_HEIGHT = tileHeight;
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

        worldObjectsHandler = new WorldObjectsHandler(this, blocks);
        adder = new ObjectAdder(worldObjectsHandler);
    }

    public void initialiseResources(AssetManager assets, World world, CharacterMaker characterMaker) {
        this.assets = world.assets;

        if (!initialised) {

            adder.initialiseWorldObjects(assets, world, characterMaker);

            if (player == null) {
                if (platformMode) {
                    player = new Player(assets, world.worldDir+"/sprites/char.png", (playerTileX*TILE_WIDTH), ((playerTileY)*TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true, characterMaker);
                } else {
                    player = new Player(assets, null, (playerTileX * TILE_WIDTH), ((playerTileY) * TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true, characterMaker);
                }
                lastSpawnPos = 0;
            }
            cameraX = player.graphicX + player.hitBox.getWidth()/2;
            cameraY = player.graphicY + player.hitBox.getHeight()/2;
            //worldObjectsHandler.setPlayer(player);

        }
        shadow = assets.get("particles/shadow.png");
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
        if (TILE_WIDTH*(width)*zoom > camera.getWidth()) {
            if (cameraX - SCREEN_WIDTH/zoom/2 < 1) {
                cameraX = SCREEN_WIDTH/zoom/2+1;
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
        if (TILE_HEIGHT*(height-off)*zoom > camera.getHeight()) {
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


    public void invalidate() {
        worldObjectsHandler.invalidateObjectCells();
        worldObjectsHandler.invalidatePlayer();
        worldObjectsHandler.invalidateSolids();
        worldObjectsHandler.invalidateCollisions(player, player.oldX, player.oldY);


   }

    /*public void respawnPlayerZ(String worldDir, AssetManager assets, int tileX, int tileY, CharacterMaker characterMaker) {
        worldObjectsHandler.respawnPlayerZ(worldDir, assets, tileX, tileY, characterMaker);
    }

    public void respawnPlayer(String worldDir, AssetManager assets, int tileX, int tileY, float pos, int speed, CharacterMaker characterMaker) {
        worldObjectsHandler.respawnPlayer(worldDir, assets, tileX, tileY, pos, speed, characterMaker);
    }*/
    public void respawnPlayerZ(String worldDir, AssetManager assets, int tileX, int tileY, CharacterMaker characterMaker) {
        if (player == null) {
            if (platformMode) {
                player = new Player(assets, worldDir+"/sprites/char.png", (playerTileX*TILE_WIDTH), ((playerTileY)*TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true, characterMaker);
            } else {
                player = new Player(assets, null, (playerTileX*TILE_WIDTH), ((playerTileY)*TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true, characterMaker);
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
                player = new Player(assets, worldDir+"/sprites/char.png", (playerTileX*TILE_WIDTH), ((playerTileY)*TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true, characterMaker);
            } else {
                player = new Player(assets, null, (playerTileX*TILE_WIDTH), ((playerTileY)*TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true, characterMaker);
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

    public void resetCheckPoints() {
        worldObjectsHandler.resetCheckPoints();
    }


    public void removeParticles() {
        worldObjectsHandler.removeParticles();
    }


    public void draw(SpriteBatch batch, World world, float offsetX, float offsetY, boolean drawPlayer, boolean drawBG) {

        if (drawBG) {
            batch.draw(world.bg, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
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

        offsetX += -cameraX + SCREEN_WIDTH /2;
        offsetY += cameraY + SCREEN_HEIGHT /2;


        if (Gdx.input.isTouched()) {
            //ParticleProperties pp = new WaterSplash(assets, (float)cameraX+(Gdx.input.getX()-SCREEN_WIDTH/2)/zoom, (float)cameraY+(Gdx.input.getY()-SCREEN_HEIGHT/2)/zoom, 1);
            //ParticleProperties pp = new GooSplash(assets, (float)cameraX+(Gdx.input.getX()-SCREEN_WIDTH/2)/zoom, (float)cameraY+(Gdx.input.getY()-SCREEN_HEIGHT/2)/zoom, 1);
            ParticleProperties pp = new TestParticle(world.assets, (float)cameraX+(Gdx.input.getX()-SCREEN_WIDTH/2)/zoom, (float)cameraY+(Gdx.input.getY()-SCREEN_HEIGHT/2)/zoom, 1);
            Particle prt = new Particle(assets, pp, platformMode);
            worldObjectsHandler.addParticle(prt);
            //objects.add(prt);
        }

        worldObjectsHandler.draw(batch, world, offsetX, offsetY, drawPlayer);
        //Collections.sort(objects);
        /*if (!platformMode) {

            *//*for (int z = 0; z < fallingObjects.size(); ++z) {
                if (fallingObjects.get(z).getClass() == Particle.class && fallingObjects.get(z).z < -fallingObjects.get(z).r*2) {
                    fallingObjects.get(z).draw(batch, offsetX, offsetY, TILE_WIDTH, TILE_HEIGHT, false);
                }
            }*//*
            for (int i = -1; i <= height + 3; ++i) {
                *//*for (int z = 0; z < fallingObjects.size(); ++z) {
                    if (fallingObjects.get(z).getClass() == Particle.class && fallingObjects.get(z).z < -fallingObjects.get(z).r*2) continue;
                    int objectTileY = (int) ((objects.get(z).fallY + FLOOR_HEIGHT) / (TILE_HEIGHT));
                    if (objectTileY == i) {
                        fallingObjects.get(z).draw(batch, offsetX, offsetY, TILE_WIDTH, TILE_HEIGHT, false);
                    }
                }*//*
                for (int t = 0; t < width; ++t) {
                    if (i >= 0) {

                        if (i < height && blocks.get(0).get(t).get(i) >= 0) {
                            drawLayer(batch, world, 0, offsetX, offsetY, i, t);
                        }

                        for (int z = 0; z < objects.size(); z++) {
                            int objectTileX = (int) ((objects.get(z).getRect().x) / (TILE_WIDTH));
                            int objectTileY = (int) ((objects.get(z).y) / (TILE_HEIGHT))+2;
                            if (objects.get(z).h == -999999 && !objects.get(z).falling && i == objectTileY && t == objectTileX) {
                                objects.get(z).draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT), platformMode);
                            }
                        }
                        batch.setColor(new Color(1.0f, 1.0f, 1.0f, 0.2f));
                        for (int z = 0; z < particles.size(); ++z) {

                            int objectTileX = (int) ((particles.get(z).getRect().x) / (TILE_WIDTH));
                            int objectTileY = (int) ((particles.get(z).y) / (TILE_HEIGHT))+2;

                            if (i == objectTileY && t == objectTileX) {
                                if (particles.get(z).falling || particles.get(z).fallen) continue;
                                float w = particles.get(z).getTexRect().getWidth()/1.5f+particles.get(z).z/3;
                                batch.setColor(new Color(1.0f, 1.0f, 1.0f, 0.35f-particles.get(z).z/50));
                                batch.draw(shadow, offsetX + particles.get(z).x - w/2, offsetY - (particles.get(z).y + w/2), w, w);
                            }
                        }
                        batch.setColor(new Color(1.0f, 1.0f, 1.0f, 0.2f));
                        if (!player.falling) {
                            int objectTileY = (int) ((player.hitBox.y + FLOOR_HEIGHT) / (TILE_HEIGHT))+1;
                            int objectTileX = (int) ((player.hitBox.x + player.hitBox.getWidth()) / (TILE_WIDTH));
                            if (i == objectTileY && t == objectTileX) {
                                float w = player.hitBox.width*0.75f;
                                float w2 = player.hitBox.width*0.25f;
                                batch.draw(shadow, offsetX + player.hitBox.x+w2/2, offsetY - (player.hitBox.y)+w2/2, w, w);
                            }
                        }
                        batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));

                        if (i > 0 && i < height+1 && blocks.get(2).get(t).get(i - 1) == 2) {
                            drawLayer(batch, world, 1, offsetX, offsetY, i-1, t);
                        }

                    }
                }


                for (int z = 0; z < fallingObjects.size(); ++z) {
                    //if (fallingObjects.get(z).getClass() == Particle.class && fallingObjects.get(z).z < -fallingObjects.get(z).r*2) continue;
                    int objectTileY = (int) ((fallingObjects.get(z).fallY + FLOOR_HEIGHT) / (TILE_HEIGHT));
                    if (i == objectTileY) {
                        if (fallingObjects.get(z).getClass() != Player.class) {
                            fallingObjects.get(z).draw(batch, offsetX, offsetY, TILE_WIDTH, TILE_HEIGHT, false);
                        } else if (drawPlayer) {
                            player.draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT));
                        }
                    }
                }




                for (int z = 0; z < objects.size(); ++z) {
                    //if (objects.get(z).getClass() == Entity.class) continue;
                    if (objects.get(z).getClass() == Entity.class || objects.get(z).getClass() == Particle.class || ((HittableEntity)objects.get(z)).falling && objects.get(z).getClass() != Player.class) {
                        int objectTileY=0;
                        int objectTileX=0;
                        if (objects.get(z).getClass() != Entity.class && !objects.get(z).falling) {
                            objectTileY = (int) ((objects.get(z).getRect().y + FLOOR_HEIGHT) / (TILE_HEIGHT));
                            objectTileX = (int) ((objects.get(z).getRect().x + objects.get(z).getRect().getWidth() / 2) / (TILE_WIDTH));
                            if (i == objectTileY + 1) {
                                //System.out.println(blocks.get(objectTileX).get(i - 1) + " " + blocks.get(objectTileX).get(i) + " " + blocks.get(objectTileX).get(i + 1));
                                //System.out.println(blocks.get(objectTileX + 1).get(i - 1) + " " + blocks.get(objectTileX + 1).get(i) + " " + blocks.get(objectTileX + 1).get(i + 1));
                                if (i <= 0 || i >= height -1|| objectTileX <= 0 || objectTileX >= width-1) continue;
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
                                        //batch.draw(white.getTile(w_up, w_down, w_left, w_right), offsetX + objectTileX * (TILE_WIDTH), offsetY - (i) * TILE_HEIGHT, white.getWidth(), white.getHeight());
                                    }
                                }
                            }
                        }
                    }
                }
                for (int z = 0; z < objects.size(); ++z) {
                    if (objects.get(z).h == -999999 || objects.get(z).falling) continue;
                    float objectTileY=0;
                    //if (objects.get(z).getClass() == Entity.class) continue;
                    if (objects.get(z).getClass() == Entity.class || objects.get(z).getClass() == Particle.class || !((HittableEntity)objects.get(z)).falling) {
                        if (objects.get(z).getClass() != Entity.class) {
                            objectTileY = (int) ((objects.get(z).getRect().y + FLOOR_HEIGHT) / (TILE_HEIGHT)) + 1;
                        } else {
                            objectTileY = (int) ((objects.get(z).y) / (TILE_HEIGHT))+2;
                        }
                    } else {
                        objectTileY = (int) ((objects.get(z).getRect().y + FLOOR_HEIGHT) / (TILE_HEIGHT));
                    }
                    if (i == objectTileY*//* || (objects.get(z).falling && objectTileY < playerTileY && objects.get(z).y > player.y && i == playerTileY)*//* &&  (objects.get(z).getClass() == Particle.class ||objects.get(z).getClass() == Entity.class || (!((HittableEntity)objects.get(z)).falling || objects.get(z).getClass() == Player.class))) {
                        if (objects.get(z).getClass() == Player.class) {
                            if (drawPlayer) {
                                //player.draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT));
                                player.draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT));
                                //characterMaker.draw(batch, 0, 100, 100);
                            }
                        } else {
                            objects.get(z).draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT), platformMode);
                        }
                    }

                }
                for (int z = 0; z < objects.size(); ++z) {
                    if (objects.get(z).h == -999999 || objects.get(z).falling) continue;
                    if (objects.get(z).getClass() != Particle.class && objects.get(z).getClass() != Entity.class && ((HittableEntity)objects.get(z)).falling && objects.get(z).getClass() != Player.class && ((HittableEntity)objects.get(z)).hitBox.y - ((HittableEntity)objects.get(z)).hitBox.height / 2 >= player.hitBox.y) {
                        float objectTileY = 0;
                        if (objects.get(z).getClass() != Entity.class) objectTileY = (int) ((objects.get(z).getRect().y + FLOOR_HEIGHT) / (TILE_HEIGHT) + 1);
                        else objectTileY = (int) ((objects.get(z).y + FLOOR_HEIGHT) / (TILE_HEIGHT) + 1);
                        if (i == objectTileY) {
                            objects.get(z).draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT), platformMode);
                        }

                    }
                }
            }
        } else {
            for (int i = -1; i <= height + 1; ++i) {
                for (int t = 0; t < width; ++t) {
                    if (i >= 0 && i < height) {

                        if (blocks.get(0).get(t).get(i) >= 0) {
                            drawLayer(batch, world, 0, offsetX, offsetY, i, t);
                        }

                    }


                }
            }
            for (int i = -1; i <= height + 1; ++i) {
                for (int t = 0; t < width; ++t) {
                    if (i > 0 && i < height + 1 && blocks.get(2).get(t).get(i - 1) == 2) {
                        drawLayer(batch, world, 1, offsetX, offsetY, i - 1, t);
                    }

                }
            }
            for (int z = 0; z < objects.size(); ++z) {
                if (objects.get(z).getClass() == Particle.class && ((Particle)objects.get(z)).pp.front) continue;
                if (objects.get(z).getClass() == Player.class) {
                    if (drawPlayer) {
                        player.draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT), platformMode);
                    }
                } else {
                    objects.get(z).draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT), platformMode);
                }

            }
            for (int z = 0; z < objects.size(); ++z) {
                if (objects.get(z).getClass() == Particle.class && ((Particle)objects.get(z)).pp.front) {
                    objects.get(z).draw(batch, offsetX, offsetY, (int) (TILE_WIDTH), (int) (TILE_HEIGHT), platformMode);
                }
            }
            for (int i = 0; i < liquidSurfaces.size(); i++) {
                liquidSurfaces.get(i).draw(batch, offsetX, offsetY);
            }
        }*/
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
