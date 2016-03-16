package com.mygdx.schoolRPG;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.schoolRPG.tools.CharacterMaker;

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

                if (img == -1 && type != 10 && type != 11 && (type < 20 || type > 25)) continue;
                float x = 0;
                if (type != 10 && type != 11 && (type < 20 || type > 25)) {
                    if(img < world.sprites.size()){
                        x = t*area.TILE_WIDTH+area.TILE_WIDTH/2-world.sprites.get(img).getWidth()/2 + blocks.get(6).get(t).get(i);
                    } else if (img < world.spritesCount + world.tilesetsCount) {
                        x = t*area.TILE_WIDTH+area.TILE_WIDTH/2-world.tiles.get(img-world.spritesCount).getSingleTile().getRegionWidth()/2 + blocks.get(6).get(t).get(i);
                    } else {
                        x = t*area.TILE_WIDTH+area.TILE_WIDTH/2-world.animations.get(img-world.spritesCount-world.tilesetsCount).getFirstFrame().getRegionWidth()/2 + blocks.get(6).get(t).get(i);
                    }
                    if (area.platformMode) {
                        if (blocks.get(5).get(t).get(i) == 1) {
                            x = t*area.TILE_WIDTH-world.sprites.get(img).getWidth()/2;
                        } else if (blocks.get(5).get(t).get(i) == 3) {
                            x = t*area.TILE_WIDTH+area.TILE_WIDTH-world.sprites.get(img).getWidth()/2;
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
                    if(img < world.sprites.size()){
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 2) {
                                y-=area.TILE_HEIGHT;
                            } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                                y-=area.TILE_HEIGHT/2;
                            }
                        }
                        y+=blocks.get(7).get(t).get(i);
                        Entity e = new Entity(assets, world.sprites.get(img), x, y, 0, 0, blocks.get(5).get(t).get(i));
                        e.setFloor(true);
                        worldObjectsHandler.addNonSolid(e);
                    } else {
                        Entity e = new Entity(assets, world.tiles.get(img - world.spritesCount).getSingleTile(), t * area.TILE_WIDTH + area.TILE_WIDTH / 2 - world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth() / 2, y, 0, 0, blocks.get(5).get(t).get(i));
                        e.setFloor(true);
                        worldObjectsHandler.addNonSolid(e);
                    }
                } else if (type == 2) {
                    float y = (i)* area.TILE_HEIGHT-area.TILE_HEIGHT/2;
                    if (area.platformMode) y += area.FLOOR_HEIGHT/2+1;
                    if(img < world.sprites.size()){
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 2) {
                                y-=area.TILE_HEIGHT;
                            } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                                y-=area.TILE_HEIGHT/2;
                            }
                        }
                        y+=blocks.get(7).get(t).get(i);
                        worldObjectsHandler.addNonSolid(new Entity(assets, world.sprites.get(img), x, y, 0, 0, blocks.get(5).get(t).get(i)));
                    } else {
                        worldObjectsHandler.addNonSolid(new Entity(assets, world.tiles.get(img - world.spritesCount).getSingleTile(), t*area.TILE_WIDTH+area.TILE_WIDTH/2-world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth()/2, y, 0, 0, blocks.get(5).get(t).get(i)));
                    }
                } else if (type == 3) {
                    float y = (i)* area.TILE_HEIGHT-area.TILE_HEIGHT/2;
                    if (area.platformMode) y += area.FLOOR_HEIGHT/2+1;
                    Entity e;
                    if(img < world.sprites.size()){
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 2) {
                                y-=area.TILE_HEIGHT;
                            } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                                y-=area.TILE_HEIGHT/2;
                            }
                        }
                        y+=blocks.get(7).get(t).get(i);

                        worldObjectsHandler.addNonSolid(new Entity(assets, world.sprites.get(img), x, y, 0, 0, blocks.get(5).get(t).get(i)));
                    } else if (img < world.spritesCount + world.tilesetsCount) {
                        worldObjectsHandler.addNonSolid(new Entity(assets, world.tiles.get(img - world.spritesCount).getSingleTile(), t*area.TILE_WIDTH+area.TILE_WIDTH/2-world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth()/2, y, 0, 0, blocks.get(5).get(t).get(i)));
                    } else {
                        worldObjectsHandler.addNonSolid(new Entity(assets, world.animations.get(img - world.spritesCount - world.tilesetsCount), t*area.TILE_WIDTH+area.TILE_WIDTH/2-world.animations.get(img - world.spritesCount - world.tilesetsCount).getFirstFrame().getRegionWidth()/2, y, 0, 0, blocks.get(5).get(t).get(i)));
                    }


                }  else if (type == 6) {
                    float y = (i)* area.TILE_HEIGHT-area.TILE_HEIGHT/2;
                    if (area.platformMode) y += area.FLOOR_HEIGHT/2+1;
                    if(img < world.sprites.size()){
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 2) {
                                y-=area.TILE_HEIGHT;
                            } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                                y-=area.TILE_HEIGHT/2;
                            }
                        }
                        y+=blocks.get(7).get(t).get(i);

                        worldObjectsHandler.addNonSolid(new Entity(assets, world.sprites.get(img), x, y, 0, 0, blocks.get(5).get(t).get(i)));
                        worldObjectsHandler.addObstacle(new DeathZone(assets, world.sprites.get(img), x, y, 0, 0, blocks.get(5).get(t).get(i), DeathZone.ZoneShape.RECT, world.sprites.get(img).getWidth(), world.sprites.get(img).getHeight()));
                    }
                }  else if (type == 7) {
                    float y = (i)* area.TILE_HEIGHT-area.TILE_HEIGHT/2;
                    if (area.platformMode) y += area.FLOOR_HEIGHT/2+1;
                    if(img < world.sprites.size()){
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 2) {
                                y-=area.TILE_HEIGHT;
                            } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                                y-=area.TILE_HEIGHT/2;
                            }
                        }
                        y+=blocks.get(7).get(t).get(i);

                        worldObjectsHandler.addNonSolid(new Entity(assets, world.sprites.get(img), x, y, 0, 0, blocks.get(5).get(t).get(i)));
                        worldObjectsHandler.addObstacle(new DeathZone(assets, world.sprites.get(img), x, y, 0, 0, blocks.get(5).get(t).get(i), DeathZone.ZoneShape.TRIANGLE, world.sprites.get(img).getWidth(), world.sprites.get(img).getHeight()));
                    }
                }  else if (type == 8) {
                    float y = (i)* area.TILE_HEIGHT-area.TILE_HEIGHT/2;
                    if (area.platformMode) y += area.FLOOR_HEIGHT/2+1;
                    if(img < world.sprites.size()){
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 2) {
                                y-=area.TILE_HEIGHT;
                            } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                                y-=area.TILE_HEIGHT/2;
                            }
                        }
                        y+=blocks.get(7).get(t).get(i);

                        worldObjectsHandler.addNonSolid(new Entity(assets, world.sprites.get(img), x, y, 0, 0, blocks.get(5).get(t).get(i)));
                        worldObjectsHandler.addObstacle(new DeathZone(assets, world.sprites.get(img), x, y, 0, 0, blocks.get(5).get(t).get(i), DeathZone.ZoneShape.CIRCLE, world.sprites.get(img).getWidth(), world.sprites.get(img).getHeight()));
                    }
                }  else if (type == 9) {
                    float y = (i)* area.TILE_HEIGHT-area.TILE_HEIGHT/2;
                    if (area.platformMode) y += area.FLOOR_HEIGHT/2+1;
                    if(img < world.sprites.size()){
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 2) {
                                y-=area.TILE_HEIGHT;
                            } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                                y-=area.TILE_HEIGHT/2;
                            }
                        }
                        y+=blocks.get(7).get(t).get(i);

                        worldObjectsHandler.addNonSolid(new Entity(assets, world.sprites.get(img), x, y, 0, 0, blocks.get(5).get(t).get(i)));
                        worldObjectsHandler.addObstacle(new DeathZone(assets, world.sprites.get(img), x, y, 0, 0, blocks.get(5).get(t).get(i), DeathZone.ZoneShape.DOT, world.sprites.get(img).getWidth(), world.sprites.get(img).getHeight()));
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
                    //if(img < world.sprites.size()){
                    if (area.platformMode) {
                        if (blocks.get(5).get(t).get(i) == 2) {
                            y-=area.TILE_HEIGHT;
                        } else if (blocks.get(5).get(t).get(i)%2 == 1) {
                            y-=area.TILE_HEIGHT/2;
                        }
                    }
                    y+=blocks.get(7).get(t).get(i);

                    CheckPoint cp = new CheckPoint(assets, world.worldDir+"/sprites/", x, y, 0, 0, blocks.get(5).get(t).get(i));
                    worldObjectsHandler.addNonSolid(cp);
                    worldObjectsHandler.addCheckPoint(cp);
                    //}
                } else if (type == 4) {
                    float y = (i * area.TILE_HEIGHT - area.TILE_HEIGHT/2);
                    float width = 0;
                    float height = 0;
                    float floorHeight = 0;
                    if (area.platformMode) {
                        if(img < world.sprites.size()){
                            width = world.sprites.get(img).getWidth();
                            height = world.sprites.get(img).getHeight();
                        } else {
                            width = world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth();
                            height = world.tiles.get(img - world.spritesCount).getSingleTile().getRegionHeight();
                        }
                        y -= (height-area.TILE_HEIGHT)-2;
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
                        float xx = t*area.TILE_WIDTH + area.TILE_WIDTH/2-world.sprites.get(img).getWidth()/2 + blocks.get(6).get(t).get(i);
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 1) {
                                xx-=area.TILE_WIDTH/2-(world.sprites.get(img).getHeight()/2-world.sprites.get(img).getWidth()/2);
                                //xx = t*area.TILE_WIDTH+(world.sprites.get(img).getHeight()/2-world.sprites.get(img).getWidth()/2) + blocks.get(6).get(t).get(i);//-world.sprites.get(img).getWidth()/2;
                            } else if (blocks.get(5).get(t).get(i) == 3) {
                                xx-=(world.sprites.get(img).getHeight()/2-world.sprites.get(img).getWidth()/2);//xx += area.TILE_WIDTH/2-world.sprites.get(img).getWidth()/2;
                            }
                        }
                        worldObjectsHandler.addSolid(new HittableEntity(assets, world.sprites.get(img), xx, y, width, height, floorHeight, false, blocks.get(5).get(t).get(i)));
                    } else {
                        worldObjectsHandler.addSolid(new HittableEntity(assets, world.tiles.get(img - world.spritesCount).getSingleTile(), t * area.TILE_WIDTH + area.TILE_WIDTH / 2 - world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth() / 2, y,
                                width, height, floorHeight, false, blocks.get(5).get(t).get(i)));
                    }
                } else if (type == 5) {
                    float y = (i * area.TILE_HEIGHT - area.TILE_HEIGHT/2);
                    float width = 0;
                    float height = 0;
                    if (area.platformMode) {
                        if(img < world.sprites.size()){
                            width = world.sprites.get(img).getWidth();
                            height = world.sprites.get(img).getHeight();
                        } else {
                            width = world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth();
                            height = world.tiles.get(img - world.spritesCount).getSingleTile().getRegionHeight();
                        }
                        y -= (height-area.TILE_HEIGHT)-2;
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
                        float xx = t*area.TILE_WIDTH + area.TILE_WIDTH/2-world.sprites.get(img).getWidth()/2 + blocks.get(6).get(t).get(i);
                        if (area.platformMode) {
                            if (blocks.get(5).get(t).get(i) == 1) {
                                xx-=area.TILE_WIDTH/2-(world.sprites.get(img).getHeight()/2-world.sprites.get(img).getWidth()/2);
                                //xx = t*area.TILE_WIDTH+(world.sprites.get(img).getHeight()/2-world.sprites.get(img).getWidth()/2) + blocks.get(6).get(t).get(i);//-world.sprites.get(img).getWidth()/2;
                            } else if (blocks.get(5).get(t).get(i) == 3) {
                                xx-=(world.sprites.get(img).getHeight()/2-world.sprites.get(img).getWidth()/2);//xx += area.TILE_WIDTH/2-world.sprites.get(img).getWidth()/2;
                            }
                        }
                        worldObjectsHandler.addSolid(new HittableEntity(assets, world.sprites.get(img), xx, y, width, height, world.sprites.get(img).getWidth() / 4 - 2, true, blocks.get(5).get(t).get(i)));
                    } else {
                        worldObjectsHandler.addSolid(new HittableEntity(assets, world.tiles.get(img - world.spritesCount).getSingleTile(), t * area.TILE_WIDTH + area.TILE_WIDTH / 2 - world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth() / 2, y,
                                width, height, area.TILE_WIDTH / 2 - world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth() / 4 - 2, true, blocks.get(5).get(t).get(i)));
                    }
                } else if (type == 11) {
                    float y = (i * area.TILE_HEIGHT - area.TILE_HEIGHT/2);
                    float width = area.TILE_WIDTH;
                    float height = area.TILE_HEIGHT;
                    float floorHeight = 0;
                    if (img != -1) {
                        if (area.platformMode) {
                            if(img < world.sprites.size()){
                                width = world.sprites.get(img).getWidth();
                                height = world.sprites.get(img).getHeight();
                            } else {
                                width = world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth();
                                height = world.tiles.get(img - world.spritesCount).getSingleTile().getRegionHeight();
                            }
                            y -= (height-area.TILE_HEIGHT)-2;
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
                    }
                    if(img < world.sprites.size()){
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
                        if (img != -1) xx = t*area.TILE_WIDTH + area.TILE_WIDTH/2-world.sprites.get(img).getWidth()/2 + blocks.get(6).get(t).get(i);
                        else xx = t*area.TILE_WIDTH;
                        if (area.platformMode && img != -1) {
                            if (blocks.get(5).get(t).get(i) == 1) {
                                xx-=area.TILE_WIDTH/2-(world.sprites.get(img).getHeight()/2-world.sprites.get(img).getWidth()/2);
                                //xx = t*area.TILE_WIDTH+(world.sprites.get(img).getHeight()/2-world.sprites.get(img).getWidth()/2) + blocks.get(6).get(t).get(i);//-world.sprites.get(img).getWidth()/2;
                            } else if (blocks.get(5).get(t).get(i) == 3) {
                                xx-=(world.sprites.get(img).getHeight()/2-world.sprites.get(img).getWidth()/2);//xx += area.TILE_WIDTH/2-world.sprites.get(img).getWidth()/2;
                            }
                        }
                        if (img != -1) {
                            worldObjectsHandler.addNonSolid(new Entity(assets, world.sprites.get(img), xx, y + (height - area.TILE_HEIGHT) + 2, 0, 0, blocks.get(5).get(t).get(i)));
                        } else y+=2;
                        HittableEntity he = new HittableEntity(assets, (Texture)null, xx, y, width, height, floorHeight, false, blocks.get(5).get(t).get(i));
                        he.setSides(false, false, true, false);
                        he.isPlatform = true;
                        //he.setFloor(true);
                        worldObjectsHandler.addSolid(he);
                    } else {
                        if (img != -1) {
                            worldObjectsHandler.addNonSolid(new Entity(assets, world.tiles.get(img - world.spritesCount).getSingleTile(), t * area.TILE_WIDTH + area.TILE_WIDTH / 2 - world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth() / 2, y+2, 0, 0, blocks.get(5).get(t).get(i)));
                        } else y+=2;
                        HittableEntity he = new HittableEntity(assets, (TextureRegion)null, t*area.TILE_WIDTH + area.TILE_WIDTH/2-world.tiles.get(img - world.spritesCount).getSingleTile().getRegionWidth()/2, y,
                                width, height, floorHeight, false, blocks.get(5).get(t).get(i));
                        he.setSides(false, false, true, false);
                        he.isPlatform = true;
                        //he.setFloor(true);
                        worldObjectsHandler.addSolid(he);
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
                    worldObjectsHandler.addLiquidSurface(ls);
                }
            }
        }
    }

}
