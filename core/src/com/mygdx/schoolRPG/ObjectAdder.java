package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.schoolRPG.tools.CharacterMaker;
import com.mygdx.schoolRPG.tools.IntCoords;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Kraft on 11.01.2016.
 */
public class ObjectAdder {
    Area area;
    ArrayList<ArrayList<ArrayList<Integer>>> blocks;
    WorldObjectsHandler worldObjectsHandler;
    AssetManager assets;

    public ObjectAdder(WorldObjectsHandler worldObjectsHandler) {
        this.worldObjectsHandler = worldObjectsHandler;
        this.area = worldObjectsHandler.area;
        this.blocks = worldObjectsHandler.blocks;
    }

    public void initialiseWorldObjects(AssetManager assets, World world, CharacterMaker characterMaker) {
        if (this.assets == null) {
            this.assets = assets;
        }
        for (int i = 0; i < world.tiles.size(); ++i) {
            world.tiles.get(i).initialiseIfNeeded(assets);
        }
        for (int i = 0; i < area.height; ++i) {
            for (int t = 0; t < area.width; ++t) {

                int type = blocks.get(4).get(t).get(i);
                int img = blocks.get(3).get(t).get(i);

                if (img == -1 && type != 10 && type != 11 && type != 13 && (type < 20 || type > 25) && type > 0) continue;
                float x = 0;
                if (type != 10 && type != 11 && type != 13 && (type < 20 || type > 25) && type > 0) {
                    if(world.tileTypes.get(img) == 0){
                        x = t*area.TILE_WIDTH+area.TILE_WIDTH/2-world.sprites.get(world.tileIndices.get(img)).getWidth()/2 + blocks.get(6).get(t).get(i);
                    } else if (world.tileTypes.get(img) == 1) {
                        x = t*area.TILE_WIDTH+area.TILE_WIDTH/2-world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionWidth()/2 + blocks.get(6).get(t).get(i);
                    } else {
                        x = t*area.TILE_WIDTH+area.TILE_WIDTH/2-world.animations.get(world.tileIndices.get(img)).getFirstFrame().getRegionWidth()/2 + blocks.get(6).get(t).get(i);
                    }
                    if (area.platformMode) {
                        if (blocks.get(5).get(t).get(i) == 1) {
                            x = t*area.TILE_WIDTH-world.sprites.get(world.tileIndices.get(img)).getWidth()/2;
                        } else if (blocks.get(5).get(t).get(i) == 3) {
                            x = t*area.TILE_WIDTH+area.TILE_WIDTH-world.sprites.get(world.tileIndices.get(img)).getWidth()/2;
                        }
                    }
                } else {
                    x = t*area.TILE_WIDTH;
                }

                if (type == 1) {
                    float y = (i)* area.TILE_HEIGHT-area.TILE_HEIGHT/2;
                    if (area.platformMode) {
                        y += area.FLOOR_HEIGHT/2+1;
                    }
                    if(world.tileTypes.get(img) == 0){
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 2) {
                                y-=area.TILE_HEIGHT;
                            } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                                y-=area.TILE_HEIGHT/2;
                            }
                        }
                        y+=blocks.get(7).get(t).get(i);
                        Entity e = new Entity(assets, world.sprites.get(world.tileIndices.get(img)), x, y, 0, 0, blocks.get(5).get(t).get(i));
                        e.setFloor(true);
                        worldObjectsHandler.addNonSolid(e, -1);
                    } else if (world.tileTypes.get(img) == 1) {
                        Entity e = new Entity(assets, world.tiles.get(world.tileIndices.get(img)).getSingleTile(), t * area.TILE_WIDTH + area.TILE_WIDTH / 2 - world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionWidth() / 2, y, 0, 0, blocks.get(5).get(t).get(i));
                        e.setFloor(true);
                        worldObjectsHandler.addNonSolid(e, -1);
                    } else {
                        Entity e = new Entity(assets, world.animations.get(world.tileIndices.get(img)), t * area.TILE_WIDTH + area.TILE_WIDTH / 2 - world.animations.get(world.tileIndices.get(img)).getFirstFrame().getRegionWidth() / 2, y, 0, 0, blocks.get(5).get(t).get(i));
                        e.setFloor(true);
                        worldObjectsHandler.addNonSolid(e, -1);
                    }
                } else if (type == 2) {
                    float y = (i)* area.TILE_HEIGHT;
                    if (area.platformMode) y += area.FLOOR_HEIGHT/2+1;
                    if(world.tileTypes.get(img) == 0){
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 2) {
                                y-=area.TILE_HEIGHT;
                            } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                                y-=area.TILE_HEIGHT/2;
                            }
                        }
                        y+=blocks.get(7).get(t).get(i);
                        worldObjectsHandler.addNonSolid(new Entity(assets, world.sprites.get(world.tileIndices.get(img)), x, y, 0, 0, blocks.get(5).get(t).get(i)), -1);
                    } else if (world.tileTypes.get(img) == 1) {
                        worldObjectsHandler.addNonSolid(new Entity(assets, world.tiles.get(world.tileIndices.get(img)).getSingleTile(), t*area.TILE_WIDTH+area.TILE_WIDTH/2-world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionWidth()/2, y, 0, 0, blocks.get(5).get(t).get(i)), -1);
                    } else {
                        worldObjectsHandler.addNonSolid(new Entity(assets, world.animations.get(world.tileIndices.get(img)), t*area.TILE_WIDTH+area.TILE_WIDTH/2-world.animations.get(world.tileIndices.get(img)).getFirstFrame().getRegionWidth()/2, y, 0, 0, blocks.get(5).get(t).get(i)), -1);
                    }
                } else if (type == 3) {
                    float y = (i)* area.TILE_HEIGHT-area.TILE_HEIGHT/2;
                    if (area.platformMode) y += area.FLOOR_HEIGHT/2+1;
                    Entity e;
                    if(world.tileTypes.get(img) == 0){
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 2) {
                                y-=area.TILE_HEIGHT;
                            } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                                y-=area.TILE_HEIGHT/2;
                            }
                        }
                        y+=blocks.get(7).get(t).get(i);

                        worldObjectsHandler.addNonSolid(new Entity(assets, world.sprites.get(world.tileIndices.get(img)), x, y, 0, 0, blocks.get(5).get(t).get(i)), -1);
                    } else if (world.tileTypes.get(img) == 1) {
                        worldObjectsHandler.addNonSolid(new Entity(assets, world.tiles.get(world.tileIndices.get(img)).getSingleTile(), t*area.TILE_WIDTH+area.TILE_WIDTH/2-world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionWidth()/2, y, 0, 0, blocks.get(5).get(t).get(i)), -1);
                    } else {
                        worldObjectsHandler.addNonSolid(new Entity(assets, world.animations.get(world.tileIndices.get(img)), t*area.TILE_WIDTH+area.TILE_WIDTH/2-world.animations.get(world.tileIndices.get(img)).getFirstFrame().getRegionWidth()/2, y, 0, 0, blocks.get(5).get(t).get(i)), -1);
                    }


                }  else if (type == 6) {
                    float y = (i)* area.TILE_HEIGHT-area.TILE_HEIGHT/2;
                    if (area.platformMode) y += area.FLOOR_HEIGHT/2+1;
                    if(world.tileTypes.get(img) == 0){
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 2) {
                                y-=area.TILE_HEIGHT;
                            } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                                y-=area.TILE_HEIGHT/2;
                            }
                        }
                        y+=blocks.get(7).get(t).get(i);

                        worldObjectsHandler.addNonSolid(new Entity(assets, world.sprites.get(world.tileIndices.get(img)), x, y, 0, 0, blocks.get(5).get(t).get(i)), -1);
                        worldObjectsHandler.addObstacle(new DeathZone(assets, world.sprites.get(world.tileIndices.get(img)), x, y, 0, 0, blocks.get(5).get(t).get(i), DeathZone.ZoneShape.RECT, world.sprites.get(world.tileIndices.get(img)).getWidth(), world.sprites.get(world.tileIndices.get(img)).getHeight()), -1);
                    }
                }  else if (type == 7) {
                    float y = (i)* area.TILE_HEIGHT-area.TILE_HEIGHT/2;
                    if (area.platformMode) y += area.FLOOR_HEIGHT/2+1;
                    if(world.tileTypes.get(img) == 0){
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 2) {
                                y-=area.TILE_HEIGHT;
                            } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                                y-=area.TILE_HEIGHT/2;
                            }
                        }
                        y+=blocks.get(7).get(t).get(i);

                        worldObjectsHandler.addNonSolid(new Entity(assets, world.sprites.get(world.tileIndices.get(img)), x, y, 0, 0, blocks.get(5).get(t).get(i)), -1);
                        worldObjectsHandler.addObstacle(new DeathZone(assets, world.sprites.get(world.tileIndices.get(img)), x, y, 0, 0, blocks.get(5).get(t).get(i), DeathZone.ZoneShape.TRIANGLE, world.sprites.get(world.tileIndices.get(img)).getWidth(), world.sprites.get(world.tileIndices.get(img)).getHeight()), -1);
                    }
                }  else if (type == 8) {
                    float y = (i)* area.TILE_HEIGHT-area.TILE_HEIGHT/2;
                    if (area.platformMode) y += area.FLOOR_HEIGHT/2+1;
                    if(world.tileTypes.get(img) == 0){
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 2) {
                                y-=area.TILE_HEIGHT;
                            } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                                y-=area.TILE_HEIGHT/2;
                            }
                        }
                        y+=blocks.get(7).get(t).get(i);

                        worldObjectsHandler.addNonSolid(new Entity(assets, world.sprites.get(world.tileIndices.get(img)), x, y, 0, 0, blocks.get(5).get(t).get(i)), -1);
                        worldObjectsHandler.addObstacle(new DeathZone(assets, world.sprites.get(world.tileIndices.get(img)), x, y, 0, 0, blocks.get(5).get(t).get(i), DeathZone.ZoneShape.CIRCLE, world.sprites.get(world.tileIndices.get(img)).getWidth(), world.sprites.get(world.tileIndices.get(img)).getHeight()), -1);
                    }
                }  else if (type == 9) {
                    float y = (i)* area.TILE_HEIGHT-area.TILE_HEIGHT/2;
                    if (area.platformMode) y += area.FLOOR_HEIGHT/2+1;
                    if(world.tileTypes.get(img) == 0){
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 2) {
                                y-=area.TILE_HEIGHT;
                            } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                                y-=area.TILE_HEIGHT/2;
                            }
                        }
                        y+=blocks.get(7).get(t).get(i);

                        worldObjectsHandler.addNonSolid(new Entity(assets, world.sprites.get(world.tileIndices.get(img)), x, y, 0, 0, blocks.get(5).get(t).get(i)), -1);
                        worldObjectsHandler.addObstacle(new DeathZone(assets, world.sprites.get(world.tileIndices.get(img)), x, y, 0, 0, blocks.get(5).get(t).get(i), DeathZone.ZoneShape.DOT, world.sprites.get(world.tileIndices.get(img)).getWidth(), world.sprites.get(world.tileIndices.get(img)).getHeight()), -1);
                    }
                }  else if (type == 10) {
                    x = t*area.TILE_WIDTH+area.TILE_WIDTH/2-area.TILE_WIDTH/2 + blocks.get(6).get(t).get(i);
                    if (blocks.get(5).get(t).get(i) == 1) {
                        x = t*area.TILE_WIDTH-area.TILE_WIDTH/2;
                    } else if (blocks.get(5).get(t).get(i) == 3) {
                        x = t*area.TILE_WIDTH+area.TILE_WIDTH-area.TILE_WIDTH/2;
                    }
                    float y = (i)* area.TILE_HEIGHT-area.TILE_HEIGHT/2;
                    if (area.platformMode) y += area.FLOOR_HEIGHT/2+1;
                    //if(world.tileTypes.get(img) == 0){
                    if (area.platformMode) {
                        if (blocks.get(5).get(t).get(i) == 2) {
                            y-=area.TILE_HEIGHT;
                        } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                            y-=area.TILE_HEIGHT/2;
                        }
                    }
                    y+=blocks.get(7).get(t).get(i);

                    CheckPoint cp = new CheckPoint(assets, world.folderPath, x, y, 0, 0, blocks.get(5).get(t).get(i));
                    worldObjectsHandler.addNonSolid(cp, -1);
                    worldObjectsHandler.addCheckPoint(cp, -1);
                    //}
                } else if (type == 4) {
                    float y = (i * area.TILE_HEIGHT - area.TILE_HEIGHT/2) + blocks.get(7).get(t).get(i);
                    float width = 0;
                    float height = 0;
                    float floorHeight = 0;
                    if (area.platformMode) {
                        if(world.tileTypes.get(img) == 0){
                            width = world.sprites.get(world.tileIndices.get(img)).getWidth();
                            height = world.sprites.get(world.tileIndices.get(img)).getHeight();
                        } else {
                            width = world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionWidth();
                            height = world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionHeight();
                        }
                        y -= (height-area.TILE_HEIGHT)-2;
                    } else {
                        if(world.tileTypes.get(img) == 0){
                            width = world.sprites.get(world.tileIndices.get(img)).getWidth();
                            height = world.sprites.get(world.tileIndices.get(img)).getWidth()/2;
                            floorHeight = world.sprites.get(world.tileIndices.get(img)).getWidth()/4-2;
                        } else if (world.tileTypes.get(img) == 1) {
                            //?y+=2;
                            width = world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionWidth();
                            height = world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionWidth()/2;
                            floorHeight = world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionWidth()/4-2;
                        } else {
                            width = world.animations.get(world.tileIndices.get(img)).getFirstFrame().getRegionWidth();
                            height = world.animations.get(world.tileIndices.get(img)).getFirstFrame().getRegionWidth()/2;
                        }
                    }
                    if(world.tileTypes.get(img) == 0){
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 2) {
                                y=i * area.TILE_HEIGHT- area.TILE_HEIGHT/2;
                                if (height%2 == 0) y+= 2;
                                else y += 1;
                            } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                                float tmp = width;
                                width = height;
                                height = tmp;
                                y-= width/2 - (width - height)*2 - height/2 + area.TILE_HEIGHT/2;
                            }

                        }
                        float xx = t*area.TILE_WIDTH + area.TILE_WIDTH/2-blocks.get(6).get(t).get(i)/2;
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 1) {
                                //xx-=area.TILE_WIDTH/2-(world.sprites.get(world.tileIndices.get(img)).getHeight()/2-world.sprites.get(world.tileIndices.get(img)).getWidth()/2);
                                xx = t*area.TILE_WIDTH+(world.sprites.get(world.tileIndices.get(img)).getHeight()/2-world.sprites.get(world.tileIndices.get(img)).getWidth()/2);//-world.sprites.get(world.tileIndices.get(img)).getWidth()/2;
                            } else if (blocks.get(5).get(t).get(i) == 3) {
                                xx-=(world.sprites.get(world.tileIndices.get(img)).getHeight()/2-world.sprites.get(world.tileIndices.get(img)).getWidth()/2);//xx += area.TILE_WIDTH/2-world.sprites.get(world.tileIndices.get(img)).getWidth()/2;
                            }
                        }
                        xx += blocks.get(6).get(t).get(i);
                        width = blocks.get(8).get(t).get(i);
                        height = blocks.get(9).get(t).get(i);
                        if (height >= 15) {
                            floorHeight = 4;
                        } else {
                            floorHeight = -2;
                        }
                        floorHeight = height - 8;
                        worldObjectsHandler.addSolid(new HittableEntity(assets, world.sprites.get(world.tileIndices.get(img)), xx, y, width, height, floorHeight, false, blocks.get(5).get(t).get(i)), world, -1, null);
                    } else {
                        width = blocks.get(8).get(t).get(i);
                        height = blocks.get(9).get(t).get(i);
                        if (height >= 15) {
                            floorHeight = 4;
                        } else {
                            floorHeight = -2;
                        }
                        if (world.tileTypes.get(img) == 1) {
                            worldObjectsHandler.addSolid(new HittableEntity(assets, world.tiles.get(world.tileIndices.get(img)).getSingleTile(), t * area.TILE_WIDTH + area.TILE_WIDTH / 2 + blocks.get(6).get(t).get(i)/2, y,
                                    width, height, floorHeight, false, blocks.get(5).get(t).get(i)), world, -1, null);
                        } else {
                            worldObjectsHandler.addSolid(new HittableEntity(assets, world.animations.get(world.tileIndices.get(img)), t * area.TILE_WIDTH + area.TILE_WIDTH / 2  + blocks.get(6).get(t).get(i)/2, y,
                                    width, height, floorHeight, false, blocks.get(5).get(t).get(i)), world, -1, null);
                        }
                    }
                } else if (type == 5) {
                    float y = (i * area.TILE_HEIGHT - area.TILE_HEIGHT/2);
                    float width = 0;
                    float height = 0;
                    float floorHeight = -2;
                    if (area.platformMode) {
                        if(world.tileTypes.get(img) == 0){
                            width = world.sprites.get(world.tileIndices.get(img)).getWidth();
                            height = world.sprites.get(world.tileIndices.get(img)).getHeight();
                        } else {
                            width = world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionWidth();
                            height = world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionHeight();
                        }
                        y -= (height-area.TILE_HEIGHT)-2;
                    } else {
                        if(world.tileTypes.get(img) == 0){
                            width = world.sprites.get(world.tileIndices.get(img)).getWidth();
                            height = 2*world.sprites.get(world.tileIndices.get(img)).getWidth()/3;
                        } else if(world.tileTypes.get(img) == 1){
                            //?y+=2;
                            width = world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionWidth();
                            height = 2*world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionWidth()/3;
                        } else {
                            width = world.animations.get(world.tileIndices.get(img)).getFirstFrame().getRegionWidth();
                            height = 2*world.animations.get(world.tileIndices.get(img)).getFirstFrame().getRegionWidth()/3;
                        }
                    }
                    if(world.tileTypes.get(img) == 0){
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 2) {
                                y=i * area.TILE_HEIGHT- area.TILE_HEIGHT/2;
                                if (height%2 == 0) y+= 2;
                                else y += 1;
                            } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                                float tmp = width;
                                width = height;
                                height = tmp;
                                y-= width/2 - (width - height)*2 - height/2 + area.TILE_HEIGHT/2;
                            }

                        }
                        float xx = t*area.TILE_WIDTH + area.TILE_WIDTH/2-world.sprites.get(world.tileIndices.get(img)).getWidth()/2;
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 1) {
                                xx-=area.TILE_WIDTH/2-(world.sprites.get(world.tileIndices.get(img)).getHeight()/2-world.sprites.get(world.tileIndices.get(img)).getWidth()/2);
                                //xx = t*area.TILE_WIDTH+(world.sprites.get(world.tileIndices.get(img)).getHeight()/2-world.sprites.get(world.tileIndices.get(img)).getWidth()/2) + blocks.get(6).get(t).get(i);//-world.sprites.get(world.tileIndices.get(img)).getWidth()/2;
                            } else if (blocks.get(5).get(t).get(i) == 3) {
                                xx-=(world.sprites.get(world.tileIndices.get(img)).getHeight()/2-world.sprites.get(world.tileIndices.get(img)).getWidth()/2);//xx += area.TILE_WIDTH/2-world.sprites.get(world.tileIndices.get(img)).getWidth()/2;
                            }
                        }
                        width = blocks.get(6).get(t).get(i);
                        height = blocks.get(7).get(t).get(i);
                        if (height >= 15) {
                            floorHeight = 4;
                        }
                        worldObjectsHandler.addSolid(new HittableEntity(assets, world.sprites.get(world.tileIndices.get(img)), xx, y, width, height, floorHeight, true, blocks.get(5).get(t).get(i)), world, -1, null);
                    } else {
                        width = blocks.get(6).get(t).get(i);
                        height = blocks.get(7).get(t).get(i);
                        if (height >= 15) {
                            floorHeight = 4;
                        }
                        if (world.tileTypes.get(img) == 1) {
                            worldObjectsHandler.addSolid(new HittableEntity(assets, world.tiles.get(world.tileIndices.get(img)).getSingleTile(), t * area.TILE_WIDTH + area.TILE_WIDTH / 2 - world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionWidth() / 2, y,
                                    width, height, floorHeight, true, blocks.get(5).get(t).get(i)), world, -1, null);
                        } else {
                            worldObjectsHandler.addSolid(new HittableEntity(assets, world.animations.get(world.tileIndices.get(img)), t * area.TILE_WIDTH + area.TILE_WIDTH / 2 - world.animations.get(world.tileIndices.get(img)).getFirstFrame().getRegionWidth() / 2, y,
                                    width, height, floorHeight, true, blocks.get(5).get(t).get(i)), world, -1, null);
                        }
                    }
                    world.movables.add(worldObjectsHandler.solids.get(worldObjectsHandler.solids.size()-1));
                    world.movablesAreas.add(world.areas.indexOf(area));
                } else if (type == 11) {
                    float y = (i * area.TILE_HEIGHT - area.TILE_HEIGHT/2);
                    float width = area.TILE_WIDTH;
                    float height = area.TILE_HEIGHT;
                    float floorHeight = 0;
                    int surfacesCount = 0;
                    while (blocks.get(4).get(t + surfacesCount).get(i) == 11) {
                        surfacesCount++;
                    }
                    if (img != -1) {
                        if (area.platformMode) {
                            if(world.tileTypes.get(img) == 0){
                                width = world.sprites.get(world.tileIndices.get(img)).getWidth();
                                height = world.sprites.get(world.tileIndices.get(img)).getHeight();
                            } else {
                                width = world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionWidth();
                                height = world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionHeight();
                            }
                            y -= (height-area.TILE_HEIGHT)-2;
                        } else {
                            if(world.tileTypes.get(img) == 0){
                                width = world.sprites.get(world.tileIndices.get(img)).getWidth();
                                height = world.sprites.get(world.tileIndices.get(img)).getWidth()/2;
                                floorHeight = world.sprites.get(world.tileIndices.get(img)).getWidth()/4-2;
                            } else {
                                //?y+=2;
                                width = world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionWidth();
                                height = world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionWidth()/2;
                                floorHeight = world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionWidth()/4-2;
                            }
                        }
                    }
                    if(world.tileTypes.get(img) == 0){
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 2) {
                                y=i * area.TILE_HEIGHT- area.TILE_HEIGHT/2;
                                if (height%2 == 0) y+= 2;
                                else y += 1;
                            } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                                float tmp = width;
                                width = height;
                                height = tmp;
                                y-= width/2 - (width - height)*2 - height/2 + area.TILE_HEIGHT/2;
                            }
                            y+=blocks.get(7).get(t).get(i);
                        }
                        float xx;
                        if (img != -1) xx = t*area.TILE_WIDTH + area.TILE_WIDTH/2-world.sprites.get(world.tileIndices.get(img)).getWidth()/2 + blocks.get(6).get(t).get(i);
                        else xx = t*area.TILE_WIDTH;
                        if (area.platformMode && img != -1) {
                            if (blocks.get(5).get(t).get(i) == 1) {
                                xx-=area.TILE_WIDTH/2-(world.sprites.get(world.tileIndices.get(img)).getHeight()/2-world.sprites.get(world.tileIndices.get(img)).getWidth()/2);
                                //xx = t*area.TILE_WIDTH+(world.sprites.get(world.tileIndices.get(img)).getHeight()/2-world.sprites.get(world.tileIndices.get(img)).getWidth()/2) + blocks.get(6).get(t).get(i);//-world.sprites.get(world.tileIndices.get(img)).getWidth()/2;
                            } else if (blocks.get(5).get(t).get(i) == 3) {
                                xx-=(world.sprites.get(world.tileIndices.get(img)).getHeight()/2-world.sprites.get(world.tileIndices.get(img)).getWidth()/2);//xx += area.TILE_WIDTH/2-world.sprites.get(world.tileIndices.get(img)).getWidth()/2;
                            }
                        }
                        if (img != -1) {
                            Entity e = new Entity(assets, world.sprites.get(world.tileIndices.get(img)), xx, y + (height - area.TILE_HEIGHT) + 2, 0, 0, blocks.get(5).get(t).get(i));
                            e.floor = true;
                            worldObjectsHandler.addNonSolid(e, -1);
                        } else y+=2;
                        HittableEntity he = new HittableEntity(assets, (Texture)null, xx, y, width * surfacesCount, height, floorHeight, false, blocks.get(5).get(t).get(i));
                        he.setSides(false, false, true, false);
                        he.isPlatform = true;
                        if (t == 0 || blocks.get(4).get(t-1).get(i) != 11) worldObjectsHandler.addSolid(he, world, -1, null);
                    } else {
                        if (img != -1) {
                            Entity e = new Entity(assets, world.tiles.get(world.tileIndices.get(img)).getSingleTile(), t * area.TILE_WIDTH + area.TILE_WIDTH / 2 - world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionWidth() / 2, y+2-world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionHeight()/2, 0, 0, blocks.get(5).get(t).get(i));
                            e.floor = true;
                            worldObjectsHandler.addNonSolid(e, -1);
                        } else y+=2;
                        HittableEntity he = new HittableEntity(assets, (TextureRegion)null, t*area.TILE_WIDTH + area.TILE_WIDTH/2-world.tiles.get(world.tileIndices.get(img)).getSingleTile().getRegionWidth()/2, y,
                                width * surfacesCount, height, floorHeight, false, blocks.get(5).get(t).get(i));
                        he.setSides(false, false, true, false);
                        he.isPlatform = true;
                        if (t == 0 || blocks.get(4).get(t-1).get(i) != 11) worldObjectsHandler.addSolid(he, world, -1, null);
                    }
                } else if ((type >= 20 && type <= 25) && (t == 0 || blocks.get(4).get(t-1).get(i) != type)) {
                    int surfacesCount = 0;
                    while (blocks.get(4).get(t+surfacesCount).get(i) == type) {
                        surfacesCount++;
                    }
                    LiquidSurface ls;
                    //if (blocks.get(4).get(t).get(i-1) != 20) ls = new LiquidSurface(assets, t*area.TILE_WIDTH, i*area.TILE_HEIGHT, surfacesCount, area.TILE_WIDTH, 0.025f, 0.035f, 0.025f);
                    if (blocks.get(4).get(t).get(i-1) != type) {
                        if (type == 20) {
                            ls = new LiquidSurface(assets, t*area.TILE_WIDTH, i*area.TILE_HEIGHT, surfacesCount, area.TILE_WIDTH, LiquidSurface.LiquidType.WATER, false);
                        } else if (type == 21) {
                            ls = new LiquidSurface(assets, t*area.TILE_WIDTH, i*area.TILE_HEIGHT, surfacesCount, area.TILE_WIDTH, LiquidSurface.LiquidType.GOO, false);
                        } else {
                            ls = new LiquidSurface(assets, t*area.TILE_WIDTH, i*area.TILE_HEIGHT, surfacesCount, area.TILE_WIDTH, LiquidSurface.LiquidType.NONE, false);
                        }
                    }
                    else {
                        if (type == 20) {
                            ls = new LiquidSurface(assets, t*area.TILE_WIDTH, i*area.TILE_HEIGHT, surfacesCount, area.TILE_WIDTH, LiquidSurface.LiquidType.WATER, true);
                        } else if (type == 21) {
                            ls = new LiquidSurface(assets, t*area.TILE_WIDTH, i*area.TILE_HEIGHT, surfacesCount, area.TILE_WIDTH, LiquidSurface.LiquidType.GOO, true);
                        } else {
                            ls = new LiquidSurface(assets, t*area.TILE_WIDTH, i*area.TILE_HEIGHT, surfacesCount, area.TILE_WIDTH, LiquidSurface.LiquidType.NONE, true);
                        }
                    }
                    worldObjectsHandler.addLiquidSurface(ls, -1);
                } else if (type == 13) {
                    float y = (i)* area.TILE_HEIGHT-area.TILE_HEIGHT/2;
                    if (area.platformMode) {
                        y -= area.FLOOR_HEIGHT/2+1;
                        /*if (blocks.get(5).get(t).get(i) == 2) {
                            y-=area.TILE_HEIGHT;
                        } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                            y-=area.TILE_HEIGHT/2;
                        }*/
                    }
                    y+=blocks.get(7).get(t).get(i);
                    Entity e = new Entity(assets, assets.get(world.worldDir.path() + "/sign.png", Texture.class), x, y, 0, 0, 0);
                    e.floor = true;
                    worldObjectsHandler.addNonSolid(e, -1);
                    worldObjectsHandler.signs.add(e);
                    worldObjectsHandler.signTexts.add(new ArrayList<String>());
                    try {
                        BufferedReader in = new BufferedReader(new FileReader(world.worldDir.path() + "/signs"));
                        int signsCount = Integer.parseInt(in.readLine());
                        if (signsCount > blocks.get(5).get(t).get(i)) {
                            int skipCount = 6 * blocks.get(5).get(t).get(i);
                            for (int ii = 0; ii < skipCount; ++ii) {
                                String line = in.readLine();
                            }
                            String line1 = in.readLine();
                            String line2 = in.readLine();
                            String line3 = in.readLine();
                            String line4 = in.readLine();
                            String line5 = in.readLine();
                            worldObjectsHandler.signTexts.get(worldObjectsHandler.signTexts.size()-1).add(line1);
                            worldObjectsHandler.signTexts.get(worldObjectsHandler.signTexts.size()-1).add(line2);
                            worldObjectsHandler.signTexts.get(worldObjectsHandler.signTexts.size()-1).add(line3);
                            worldObjectsHandler.signTexts.get(worldObjectsHandler.signTexts.size()-1).add(line4);
                            worldObjectsHandler.signTexts.get(worldObjectsHandler.signTexts.size()-1).add(line5);
                            String line = in.readLine();
                            skipCount = 6 * (signsCount -1);
                            for (int ii = 0; ii < skipCount; ++ii) {
                                line = in.readLine();
                            }
                            line1 = in.readLine();
                            line2 = in.readLine();
                            line3 = in.readLine();
                            line4 = in.readLine();
                            line5 = in.readLine();
                            worldObjectsHandler.signTexts.get(worldObjectsHandler.signTexts.size()-1).add(line1);
                            worldObjectsHandler.signTexts.get(worldObjectsHandler.signTexts.size()-1).add(line2);
                            worldObjectsHandler.signTexts.get(worldObjectsHandler.signTexts.size()-1).add(line3);
                            worldObjectsHandler.signTexts.get(worldObjectsHandler.signTexts.size()-1).add(line4);
                            worldObjectsHandler.signTexts.get(worldObjectsHandler.signTexts.size()-1).add(line5);
                        }
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                else if (type < 0 && type >= -56 && type != -1) {
                    int dir = blocks.get(5).get(t).get(i);
                    if (dir == 0) {
                        int playerWidth = 16;
                        int playerHeight = 5;
                        int playerFloor = 10;
                        NPC npc = new NPC(assets, null, (t*area.TILE_WIDTH), ((i)*area.TILE_HEIGHT), playerWidth, playerHeight, playerFloor, true, characterMaker, type + 56, world);
                        characterMaker.setDirection(dir, type + 56);
                        npc.movable = true;
                        //ObjectCell soc = worldObjectsHandler.addSolid(npc, world, -1, null);
                        worldObjectsHandler.addNPC(npc, world, -1);
                    } else {
                        //itemGlow.floor = true;
                        FileHandle itemDir =  Gdx.files.internal(world.folderPath + "/items");
                        FileHandle itemXML = null;
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = null;
                        Document xml = null;
                        try {
                            dBuilder = dbFactory.newDocumentBuilder();
                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();
                        }
                        boolean spawned = false;
                        for (FileHandle entry: itemDir.list()) {
                            if (entry.extension().equals("xml")) {
                                itemXML = entry;
                                try {
                                    xml = dBuilder.parse(itemXML.file());
                                } catch (SAXException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                xml.getDocumentElement().normalize();
                                int id = Integer.parseInt(xml.getDocumentElement().getAttribute("id"));
                                if (id == type + 56) {
                                    Entity itemGlow = new Entity(assets, "item.png", t*area.TILE_WIDTH + area.TILE_WIDTH/2 - 7 + blocks.get(6).get(t).get(i), i * area.TILE_HEIGHT + blocks.get(7).get(t).get(i), 0 ,0 ,0);
                                    itemGlow.containingItem = new Item(assets, world.folderPath, entry.nameWithoutExtension());
                                    area.worldObjectsHandler.addNonSolid(itemGlow, -1);
                                    world.itemsOnFloor.add(itemGlow);
                                    world.itemsOnFloorAreas.add(world.areas.indexOf(area));
                                    spawned = true;
                                }
                            }
                            if (spawned) break;
                        }
                        //loadTextInfo(assets, worldPath, language);

                    }
                }
            }
        }
        for (int i =0 ;i < area.worldObjectsHandler.objects.size(); ++i) {
            world.objects.add(area.worldObjectsHandler.objects.get(i));
        }

    }

}
