package com.mygdx.schoolRPG;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.schoolRPG.menus.GameMenu;

import java.util.ArrayList;

/**
 * Created by Kraft on 11.01.2016.
 */


public class WorldObjectsHandler {
    ArrayList<ArrayList<ArrayList<Integer>>> blocks;
    ArrayList<Entity> nonSolids;
    ArrayList<Entity> fallingObjects;
    ArrayList<DeathZone> obstacles;
    ArrayList<HittableEntity> solids;
    ArrayList<Particle> particles;
    ArrayList<CheckPoint> checkPoints;
    ArrayList<LiquidSurface> liquidSurfaces;
    ArrayList<NPC> NPCs;
    ArrayList<ArrayList<ArrayList<ObjectCell>>> objectCells;
    ArrayList<ObjectCell> objects;
    ArrayList<String> flagNames;
    ArrayList<Boolean> flags;
    Texture staticFloor;
    Texture dynamicFloor;
    Dialog currentDialog = null;
    public ObjectCell activeObject = null;
    public NPC activeNPC = null;
    //Player player;
    Area area;
    public WorldObjectsHandler(Area area, ArrayList<ArrayList<ArrayList<Integer>>> blocks, ArrayList<String> flagNames, ArrayList<Boolean> flags) {
        this.area = area;
        this.blocks = blocks;
        objects = new ArrayList<ObjectCell>();
        nonSolids = new ArrayList<Entity>();
        obstacles = new ArrayList<DeathZone>();
        checkPoints = new ArrayList<CheckPoint>();
        liquidSurfaces = new ArrayList<LiquidSurface>();
        particles = new ArrayList<Particle>();
        solids = new ArrayList<HittableEntity>();
        fallingObjects = new ArrayList<Entity>();
        this.flags = flags;
        this.flagNames = flagNames;
        NPCs = new ArrayList<NPC>();
        area.lastSpawnTileX = area.playerTileX;
        area.lastSpawnTileY = area.playerTileY;
        objectCells = new ArrayList<ArrayList<ArrayList<ObjectCell>>>();
        for (int i = -1; i <= area.width; ++i) {
            objectCells.add(new ArrayList<ArrayList<ObjectCell>>());
            for (int t = -1; t <= area.height; ++t) {
                objectCells.get(i+1).add(new ArrayList<ObjectCell>());
            }
        }
    }


    private void addObjectCell(ObjectCell oc, int state) {
        int tileX = (int)Math.floor(oc.entity.x/area.TILE_WIDTH)+2;
        if (oc.entity.floor) {
            tileX = (int)Math.floor(oc.entity.x/area.TILE_WIDTH);
        }
        int tileY;
        if (oc.hIsY) {

            tileY = (int)Math.floor(oc.entity.h/area.TILE_HEIGHT)+2;
        } else {
            tileY = (int)Math.floor(oc.entity.y/area.TILE_HEIGHT)+2;
        }
        oc.x = tileX;
        oc.y = tileY;
        if (tileX < 0) {
            tileX = 0;
        } else if (tileX >= area.width) {
            tileX = area.width-1;
        }
        if (tileY < 0) {
            tileY = 0;
        } else if (tileY >= area.height) {
            tileY = area.height - 1;
        }
        oc.objectCheck(area.worldPath, area.assets, state);
        if (oc.isObject) {
            objects.add(oc);
        }
        objectCells.get(tileX).get(tileY).add(oc);
    }

    /*public void setPlayer(Player player) {
        deleteObjectCellsForEntity(player);
        addObjectCell(new ObjectCell(area.TILE_WIDTH, area.TILE_HEIGHT, player, ObjectType.PLAYER, 0, true));
        this.player = player;
    }*/

    public int removeSolid(int id) {
        int state = -1;
        for (int i = 0; i < objectCells.size(); ++i) {
            for (int j = 0; j < objectCells.get(i).size(); ++j) {
                for (int k = 0; k < objectCells.get(i).get(j).size(); ++k) {
                    if (objectCells.get(i).get(j).get(k).entity == solids.get(id)) {
                        state = objectCells.get(i).get(j).get(k).currentState;
                        objectCells.get(i).get(j).remove(k);
                    }
                }

            }
        }
        for (int i =0; i < objects.size(); ++i) {
            if (objects.get(i).entity == solids.get(id)) {
                objects.remove(i);
            }
        }
        solids.remove(id);
        return state;
    }

    public void addSolid(HittableEntity he, int state) {
        he.x = he.hitBox.x;
        he.y = he.hitBox.y;
        if (he.floor) {
            he.h = -999999;
        } else {
            he.h = he.y + he.floorHeight;
        }
        addObjectCell(new ObjectCell(area.TILE_WIDTH, area.TILE_HEIGHT, he, ObjectType.SOLID, solids.size(), true), state);
        solids.add(he);
    }

    public void addNonSolid(Entity e, int state) {
        if (e.floor) {
            addObjectCell(new ObjectCell(area.TILE_WIDTH, area.TILE_HEIGHT, e, ObjectType.NONSOLID, nonSolids.size(), false), state);
        } else {
            addObjectCell(new ObjectCell(area.TILE_WIDTH, area.TILE_HEIGHT, e, ObjectType.NONSOLID, nonSolids.size(), true), state);
        }
        nonSolids.add(e);
    }

    public void addObstacle(DeathZone dz, int state) {
        addObjectCell(new ObjectCell(area.TILE_WIDTH, area.TILE_HEIGHT, dz, ObjectType.OBSTACLE, obstacles.size(), true), state);
        obstacles.add(dz);
    }

    public void addLiquidSurface(LiquidSurface ls, int state) {
        addObjectCell(new ObjectCell(area.TILE_WIDTH, area.TILE_HEIGHT, ls, ObjectType.LIQUID, liquidSurfaces.size(), true), state);
        liquidSurfaces.add(ls);
    }

    public void addCheckPoint(CheckPoint cp, int state) {
        addObjectCell(new ObjectCell(area.TILE_WIDTH, area.TILE_HEIGHT, cp, ObjectType.CHECKPOINT, checkPoints.size(), true), state);
        checkPoints.add(cp);
    }

    public void addParticle(Particle prt, int state) {
        addObjectCell(new ObjectCell(area.TILE_WIDTH, area.TILE_HEIGHT, prt, ObjectType.PARTICLE, particles.size(), true), state);
        particles.add(prt);
    }

    public void addNPC(NPC npc, World world, int state) {
        addObjectCell(new ObjectCell(area.TILE_WIDTH, area.TILE_HEIGHT, npc, ObjectType.NPC, NPCs.size(), true), state);
        NPCs.add(npc);
        world.synchronizeFlags();
    }

    public void invalidateParticlesCollisions(Particle p) {
        if (!p.pp.bouncing) return;
        int tileX = (int)(p.x/(area.TILE_WIDTH));
        int tileY = (int)((p.y)/(area.TILE_HEIGHT));
        for (int i = tileY - 2; i <= tileY + 2; ++i) {
            for (int t = tileX - 2; t <= tileX + 2; ++t) {
                if (t < 0 || t >= blocks.get(2).size() || i < 0 || i >= blocks.get(2).get(0).size()) continue;
                if (blocks.get(2).get(t).get(i) == 2) {
                    Rectangle collisionRect = new Rectangle(t*(area.TILE_WIDTH), i* area.TILE_HEIGHT -6 - 10, area.TILE_WIDTH, area.TILE_HEIGHT);
                    if (p.curBounces > 0) {
                        if (((collisionRect.contains(p.x-p.r, p.y) && p.XSpeed < 0) || (collisionRect.contains(p.x+p.r, p.y) && p.XSpeed > 0))) {
                            p.bounce(false, true);
                        }
                        if (!area.platformMode) {
                            if (((collisionRect.contains(p.x, p.y+p.r) && p.YSpeed > 0) || (collisionRect.contains(p.x, p.y-p.r) && p.YSpeed < 0))) {
                                p.YSpeed = -p.YSpeed;
                                //p.curBounces--;
                            }
                        } else {
                            if (((collisionRect.contains(p.x, p.y-p.r) && p.YSpeed < 0))) {
                                p.bounce(false, false);
                            } else if (collisionRect.contains(p.x, p.y+p.r) && p.YSpeed > 0) {
                                p.bounce(true, false);
                            }
                        }
                    }
                }
            }
        }
        for (int z=0; z<solids.size(); ++z) {
            if (solids.get(z).getClass() == HittableEntity.class || solids.get(z).getClass() == Player.class) {
                if (p.z >= solids.get(z).hitBox.width) continue;
                Rectangle collisionRect = new Rectangle(((HittableEntity)(solids.get(z))).getRect());
                collisionRect.y-=10;
                //collisionRect.height+=10;
                if (p.curBounces > 0) {
                    if (((collisionRect.contains(p.x-p.r, p.y) && p.XSpeed < 0) || (collisionRect.contains(p.x+p.r, p.y) && p.XSpeed > 0))) {
                        if (area.platformMode) p.bounce(false, true);
                        else p.XSpeed = -p.XSpeed;
                    }
                    if (((collisionRect.contains(p.x, p.y+p.r*2) && p.YSpeed > 0) || (collisionRect.contains(p.x, p.y) && p.YSpeed < 0))) {
                        if (area.platformMode) p.bounce(false, false);
                        else p.YSpeed = -p.YSpeed;
                    }
                }
            }
        }
    }

    public void invalidateCollisions(HittableEntity he, float oldX, float oldY) {
        boolean isPlayer = false;
        if (he.getClass() == Player.class) isPlayer = true;

        int tileX1 = (int)(he.hitBox.x/(area.TILE_WIDTH))+2;
        int tileY1 = (int)(he.hitBox.y/(area.TILE_HEIGHT))+2;
        int tileX2 = (int)((he.hitBox.x + he.hitBox.width)/(area.TILE_WIDTH))-2;
        int tileY2 = (int)((he.hitBox.y + he.hitBox.height)/(area.TILE_HEIGHT))-2;
        /*if (tileX1 < 1 || tileY1 < 1 || tileX1 >= blocks.get(2).size()+1 || tileY1 >= blocks.get(0).size()) {
            return;
        }
        if (tileX2 < 1 || tileY2 < 1 || tileX2 >= blocks.get(2).size()+1 || tileY2 >= blocks.get(0).size()) {
            return;
        }*/
        if (area.platformMode && he.pSpeed >= 0) {
            tileY2++;
        }

        Rectangle oldRect = new Rectangle(he.hitBox);
        for (int z=0; z<solids.size(); ++z) {
            if (!solids.get(z).falling) {
                if (area.platformMode) {
                    he.hitBox = solids.get(z).pushOutSolidObjects(he);
                    if (he.hitBox.y < oldRect.y && he.pSpeed > 0) {
                        he.pSpeed=0;
                    } else if (he.hitBox.y > oldRect.y && he.pSpeed < 0) {
                        he.pSpeed=1;
                    }
                } else {
                    he.hitBox = solids.get(z).pushOutSolidObjects(he, area, area.player.oldX, area.player.oldY);
                }
            }
        }
        oldRect = new Rectangle(he.hitBox);
        for (int i = tileY1-2; i <= tileY2+3; ++i) {
            for (int t = tileX1-2; t <= tileX2+2; ++t) {
                if (t < 0 || t >= blocks.get(2).size() || i < 0 || i >= blocks.get(2).get(0).size()) break;
                if (blocks.get(2).get(t).get(i) == 2/* || blocks.get(t).get(i) == 0*/) {
                    HittableEntity tmp = new HittableEntity(area.assets, (String)null, t*(area.TILE_WIDTH), i* area.TILE_HEIGHT -6, area.TILE_WIDTH, area.TILE_HEIGHT, 3, false, 0);
                    boolean left = t == 0 || blocks.get(2).get(t - 1).get(i) != 2/* && blocks.get(t-1).get(i) != 0*/;
                    boolean right = (t >= area.width-1 || blocks.get(2).get(t + 1).get(i) != 2)/* && blocks.get(t+1).get(i) != 0*/;
                    boolean up = (i >= area.height-1 || blocks.get(2).get(t).get(i+1) != 2)/* && blocks.get(t).get(i+1) != 0*/;
                    boolean down = i == 0 || blocks.get(2).get(t).get(i-1) != 2/* && blocks.get(t).get(i-1) != 0*/;
                    tmp.setSides(left, right, down, up);
                    if (isPlayer) {
                        he.hitBox = tmp.pushOutSolidObjects(he, area, area.player.oldX, area.player.oldY);
                    } else {
                        if (!area.platformMode) {
                            he.hitBox = tmp.pushOutSolidObjects(he, area, oldX, oldY);
                            he.hitBox = tmp.pushOutSolidObjects(he, area, area.player.oldX, he.oldY);
                        } else {
                            he.hitBox = tmp.pushOutSolidObjects(he);
                        }
                    }
                    if (he.hitBox == area.player.hitBox && (he.hitBox.x != oldRect.x || he.hitBox.y != oldRect.y)) {
                        if (area.player.speedX != 0 && he.hitBox.x != oldRect.x && Math.abs(he.hitBox.x - oldRect.x)/(he.hitBox.x - oldRect.x) != Math.abs(area.player.speedX)/area.player.speedX) {
                            area.player.speedX=0;
                        }
                        if (!area.platformMode) {
                            if (area.player.speedY != 0 && he.hitBox.y != oldRect.y && (Math.abs(he.hitBox.y - oldRect.y)/(he.hitBox.y - oldRect.y) != -Math.abs(area.player.speedY)/area.player.speedY)) {
                                area.player.speedY=0;
                            }
                        } else {
                            if (he.hitBox.y < oldRect.y && area.player.pSpeed > 0) {
                                area.player.pSpeed=0;
                            } else if (he.hitBox.y > oldRect.y && area.player.pSpeed < 0) {
                                area.player.pSpeed=1;
                            }
                        }
                        //}
                        //return;
                    } /*else {
                        player.push = false;
                    }*/
                }
            }
        }
        /*if ((he.hitBox.x == oldX && he.hitBox.y == oldY)) {
            he.hitBox.x = Math.round(he.hitBox.x);
            he.hitBox.y = Math.round(he.hitBox.y);
        }*/
        return;
    }

    public void invalidateLiquids() {

        for (int i = 0; i < solids.size(); ++i) {
            boolean inWater = false;
            boolean inGoo = false;
            for (int t = 0; t < liquidSurfaces.size(); ++t) {
                float k = solids.get(i).getTexRect().x + solids.get(i).getTexRect().width/2;
                if (k < liquidSurfaces.get(t).getRect().x + liquidSurfaces.get(t).getRect().width && k > liquidSurfaces.get(t).getRect().x) {
                    if (solids.get(i).getTexRect().y < liquidSurfaces.get(t).getRect().y && solids.get(i).getTexRect().y > liquidSurfaces.get(t).getRect().y - liquidSurfaces.get(t).getRect().height) {
                        if (liquidSurfaces.get(t).type == LiquidSurface.LiquidType.GOO) inGoo = true;
                        else inWater = true;
                        if (!solids.get(i).inWater && !solids.get(i).inGoo) {
                            liquidSurfaces.get(t).splash(k, 6);
                        }
                        break;
                    } else if (solids.get(i).getPreviousY() < liquidSurfaces.get(t).getRect().y && solids.get(i).getPreviousY() > liquidSurfaces.get(t).getRect().y - liquidSurfaces.get(t).getRect().height) {
                        liquidSurfaces.get(t).splash(k, -6);
                    }
                }
            }
            solids.get(i).inWater = inWater;
            solids.get(i).inGoo = inGoo;
        }
    }

    public void checkFall(HittableEntity object) {
        if (object.falling) return;
        boolean fall = true;
        for (int i = 0; i < area.height; ++i) {
            for (int t = 0; t < area.width; ++t) {
                if (blocks.get(2).get(t).get(i) == 1) {
                    Rectangle tmp = new Rectangle(t * (area.TILE_WIDTH), i * area.TILE_HEIGHT - 6, area.TILE_WIDTH, area.TILE_HEIGHT);
                    Rectangle objRect = new Rectangle(object.hitBox.x, object.hitBox.y+1, object.hitBox.width, object.hitBox.height-1);

                    if (tmp.overlaps(objRect)) {
                        fall = false;
                        break;
                    }
                }
            }
            if (!fall) break;
        }
        if (fall) {
            object.falling = true;
            object.fallY = object.y;
            //if (object.getClass() != Player.class)
            fallingObjects.add(object);
        }
    }

    public void checkFall(Entity e) {
        if (e.falling) return;
        boolean fall = true;
        for (int i = 0; i < area.height; ++i) {
            for (int t = 0; t < area.width; ++t) {
                if (blocks.get(2).get(t).get(i) == 1 || blocks.get(2).get(t).get(i) == 2) {
                    Rectangle tmp = new Rectangle(t * (area.TILE_WIDTH)-e.r, (i-1) * area.TILE_HEIGHT-2, area.TILE_WIDTH+e.r*2, area.TILE_HEIGHT+2+e.r);
                    if (tmp.contains(e.x, e.y) || e.z > 0) {
                        fall = false;
                        break;
                    }
                }
            }
            if (!fall) break;
        }
        if (fall) {
            e.falling = true;
            e.fallY = e.y;
            fallingObjects.add(e);
        }

    }


    public void killPlayer(World world) {
        ParticleProperties pp;
        Particle prt;
        for (int i = 0; i < 20; ++i) {
            prt = new Particle(area.assets, new ParticleProperties(world.assets, world.particles.get(6)), area.platformMode, area.player.x+3, area.player.y-6, 1);
            particles.add(prt);
            addParticle(prt, -1);
        }
        prt = new Particle(area.assets, new ParticleProperties(world.assets, world.particles.get(4)), area.platformMode, area.player.x+3, area.player.y-6, 1);
        particles.add(prt);
        addParticle(prt, -1);
        prt = new Particle(area.assets, new ParticleProperties(world.assets, world.particles.get(1)), area.platformMode, area.player.x+3, area.player.y-3, 1);
        particles.add(prt);
        addParticle(prt, -1);
        for (int i = 0; i < 4; ++i) {
            prt = new Particle(area.assets, new ParticleProperties(world.assets, world.particles.get(2)), area.platformMode, area.player.x+3, area.player.y-3, 1);
            particles.add(prt);
            addParticle(prt, -1);
        }
        area.player.blockControls();
        if (area.saved) {
            area.player.hitBox.x = (area.lastSpawnTileX*area.TILE_WIDTH);
            area.player.hitBox.y = (area.lastSpawnTileY*area.TILE_HEIGHT);
            if (area.platformMode) area.player.curPose = area.player.poses.getTile(PlayerMultiTile.PlayerPose.FRONT);
        } else {
            area.respawnPlayer(null, area.assets, 0, 0, 0, 0, null);
        }
    }

    public void invalidatePlayer(World world){
        if (!area.platformMode) {
            area.player.move(!area.playerHidden);
            area.player.invalidatePose(false, false);
            area.player.fall();
        } else {
            for (int i = 0; i < obstacles.size(); i++) {
                if (obstacles.get(i).collide(area.player.hitBox) && obstacles.get(i).shape == DeathZone.ZoneShape.RECT) {
                    killPlayer(world);
                } else if (obstacles.get(i).shape != DeathZone.ZoneShape.RECT && obstacles.get(i).collide(new Rectangle(area.player.hitBox.x+1, area.player.hitBox.y-area.player.hitBox.height, area.player.hitBox.width-3, area.player.hitBox.height))) {
                    killPlayer(world);
                }
            }
            area.player.platformMove();
            area.player.platformFall();
        }

    }

    public void invalidateNPCs() {

        for (int i = 0; i < NPCs.size(); ++i) {
            NPCs.get(i).invalidatePose(false, false);
        }
    }

    public void activateActiveObject(GameMenu menu, String worldPath, AssetManager assets, World world) {
        if (activeNPC == null && activeObject != null) {
            activeObject.activate(worldPath, assets, flagNames, flags, area);
            world.synchronizeFlags();
        } else if (activeNPC != null) {
            menu.drawPause = false;
            menu.paused = true;
            menu.unpausable = false;
            String str = "";
            for (int j = 0; j < activeNPC.flagsCount; ++j) {
                if (activeNPC.flags.get(j)) {
                    str += 1;
                } else {
                    str += 0;
                }
            }
            currentDialog = new Dialog(worldPath + "/chars/" + activeNPC.charId + "/dialog/" + str, false, NPCs, assets, worldPath + "/chars");
        }
    }

    public void invalidateObjects(String worldDir, AssetManager assets, World world) {
        for (int i = 0; i < objects.size(); ++i) {
            objects.get(i).checkFlags(worldDir, assets, world.flagNames, world.flags, area);
            float ox = objects.get(i).entity.x + objects.get(i).offsetX;
            if (objects.get(i).entity.getClass() != HittableEntity.class) {
                if (objects.get(i).entity.anim != null) {
                    ox += objects.get(i).entity.anim.getFirstFrame().getRegionWidth()/2;
                } else {
                    ox += objects.get(i).entity.tex.getWidth()/2;
                }
            }
            float oy = objects.get(i).entity.y + objects.get(i).offsetY;// + area.TILE_HEIGHT/2;
            if (objects.get(i).checkParticleEmission()) {
                ox += objects.get(i).getParticleCoord(0);
                oy += objects.get(i).getParticleCoord(1);
                Particle prt = new Particle(assets, world.particles.get(objects.get(i).statesParticles.get(objects.get(i).currentState)), area.platformMode, ox, oy, objects.get(i).getParticleCoord(2));
                addParticle(prt, -1);
            }
        }
    }

    public void checkObjects(String worldDir, AssetManager assets, World world) {
        invalidateObjects(worldDir, assets, world);
        if (activeNPC != null) {
            activeObject = null;
            return;
        }
        if (area.playerHidden) {
            return;
            /*for (int i = 0; i < objects.size(); ++i) {
                if (objects.get(i).statesHidePlayer.get(objects.get(i).currentState)) {
                    objects.get(i).activate(worldPath, assets, flagNames, flags, area);
                }
            }*/
        } else {
            float minDist = 9999;
            int minDistID = -1;
            for (int i = 0; i < objects.size(); ++i) {
                float px = area.player.x;
                float py = area.player.y;
                float ox = objects.get(i).entity.x + objects.get(i).offsetX;
                if (objects.get(i).entity.getClass() != HittableEntity.class) {
                    if (objects.get(i).entity.anim != null) {
                        ox += objects.get(i).entity.anim.getFirstFrame().getRegionWidth()/2;
                    } else {
                        ox += objects.get(i).entity.tex.getWidth()/2;
                    }
                }
                float oy = objects.get(i).entity.y + objects.get(i).offsetY;// + area.TILE_HEIGHT/2;
                float dist = (float)Math.sqrt((px - ox) * (px - ox) + (py - oy) * (py - oy));
                if (dist < objects.get(i).radius && dist < minDist) {
                    minDist = dist;
                    minDistID = i;
                }
                if (objects.get(i).checkParticleEmission()) {
                    ox += objects.get(i).getParticleCoord(0);
                    oy += objects.get(i).getParticleCoord(1);
                    Particle prt = new Particle(assets, world.particles.get(objects.get(i).statesParticles.get(objects.get(i).currentState)), area.platformMode, ox, oy, objects.get(i).getParticleCoord(2));
                    addParticle(prt, -1);
                }
            }

            if (minDistID >= 0) {
                activeObject = objects.get(minDistID);

                //objects.get(minDistID).activate(worldPath, assets, flagNames, flags, area);
                //world.synchronizeFlags();
                return;
            }
            activeObject = null;
        }

    }

    public void checkNPCs(GameMenu menu, String worldPath, AssetManager assets) {
        if (currentDialog != null || area.playerHidden) {
            activeNPC = null;
            return;
        }
        float minDist = 9999;
        int minDistID = -1;
        for (int i = 0; i < NPCs.size(); ++i) {
            float dist = (float)Math.sqrt((area.player.x - NPCs.get(i).x) * (area.player.x - NPCs.get(i).x) + (area.player.y - NPCs.get(i).y) * (area.player.y - NPCs.get(i).y));
            if (dist < minDist) {
                minDist = dist;
                minDistID = i;
            }
        }
        if (minDist < 20) {
            activeNPC =  NPCs.get(minDistID);
            /*menu.drawPause = false;
            menu.paused = true;
            menu.unpausable = false;
            String str = "";
            for (int j = 0; j < NPCs.get(minDistID).flagsCount; ++j) {
                if (NPCs.get(minDistID).flags.get(j)) {
                    str += 1;
                } else {
                    str += 0;
                }
            }
            currentDialog = new Dialog(worldPath + "/chars/" + NPCs.get(minDistID).charId + "/dialog/" + str, false, NPCs, assets, worldPath + "/chars");*/
            return;
        }
        activeNPC = null;
    }

    public void invalidateSolids() {
        for (int i=0; i<solids.size(); ++i) {
            float old = solids.get(i).y;
            if (!area.platformMode) {
                solids.get(i).fall();
            } else {
                solids.get(i).platformFall();
            }

            if (solids.get(i).movable && !solids.get(i).falling) {
                invalidateCollisions(solids.get(i), solids.get(i).oldX, solids.get(i).oldY);
            }
            if (solids.get(i).hitBox.y < old) {
                solids.get(i).pSpeed = 0;
            }
        }
        for (int i=0; i<solids.size(); ++i) {
            if (solids.get(i).movable && !solids.get(i).falling) {
                invalidateCollisions(solids.get(i), solids.get(i).oldX, solids.get(i).oldY);
            }
        }
        if (!area.platformMode) {
            area.player.fall();
        }
        /*if (player.hitBox.y < old) {
            player.pSpeed = 0;
        }*/
        /*if (player.pusher) {
            Player pushField = new Player(assets, null, player.hitBox.x, player.hitBox.y, player.hitBox.width, player.hitBox.height, 3, false, player.characterMaker);
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
        }*/
        for (int i=solids.size()-1; i>=0; --i) {
            if (solids.get(i).movable && !solids.get(i).falling) {
                invalidateCollisions(solids.get(i), solids.get(i).oldX, solids.get(i).oldY);
            }
            if (!area.platformMode) {
                checkFall(solids.get(i));
                if (solids.get(i).z > area.cameraY + area.SCREEN_HEIGHT) {
                    deleteObjectCellsForEntity((Entity)solids.get(i));
                    fallingObjects.remove(solids.get(i));
                    solids.remove(solids.get(i));
                }
            } else {
                invalidateCollisions(area.player, area.player.oldX, area.player.oldY);
            }
        }
        checkFall(area.player);
        for (int i = 0; i < particles.size(); i++) {
            if (!area.platformMode) {
                checkFall(particles.get(i));
            }
            particles.get(i).fall();
            invalidateParticlesCollisions(particles.get(i));
            if (particles.get(i).alpha <= 0 || particles.get(i).x > area.width*area.TILE_WIDTH || particles.get(i).y > area.height*area.TILE_HEIGHT ||
                    particles.get(i).x < 0 || (particles.get(i).y < -area.TILE_HEIGHT && !area.platformMode) || particles.get(i).z > 500) {
                deleteObjectCellsForEntity(particles.get(i));
                fallingObjects.remove(particles.get(i));
                particles.remove(i);
            }
        }
        if (liquidSurfaces != null) {
            invalidateLiquids();
            for (int i = 0; i < liquidSurfaces.size(); i++) {
                liquidSurfaces.get(i).invalidate();
            }
        }
        if (checkPoints == null) return;
        for (int i = 0; i < checkPoints.size(); i++) {
            if (!checkPoints.get(i).on && checkPoints.get(i).collide(area.player.hitBox)) {
                checkPoints.get(i).turnOn(checkPoints);
                area.lastSpawnTileX = (int) ((checkPoints.get(i).x) / (area.TILE_WIDTH));
                area.lastSpawnTileY = (int) ((checkPoints.get(i).y) / (area.TILE_HEIGHT))+1;
                if (checkPoints.get(i).angle == 2) area.lastSpawnTileY++;
                area.saved = true;
            }
        }
    }

    private void sortObjectCells(ArrayList<ObjectCell> cells) {
        for (int i = 0; i < cells.size(); ++i) {
            for (int t = cells.size()-1; t >= 1; --t) {
                if (cells.get(t).entity.h < cells.get(t-1).entity.h) {
                    ObjectCell temp = cells.get(t);
                    cells.set(t, cells.get(t-1));
                    cells.set(t - 1, temp);
                }
            }
        }
    }

    public void invalidateObjectCells() {
        for (int i = 0; i < area.width; i++) {
            for (int t = 0; t < area.height; t++) {
                for (int z = 0; z < objectCells.get(i).get(t).size(); z++) {
                    ObjectCell temp = objectCells.get(i).get(t).get(z);
                    objectCells.get(i).get(t).get(z).invalidate();
                    if (temp.isTransfer()) {
                        if (i + temp.cellOffsetX < area.width && i + temp.cellOffsetX >= 0 && t+temp.cellOffsetY < area.height && t+temp.cellOffsetY >= 0) {
                            objectCells.get(i + temp.cellOffsetX).get(t+temp.cellOffsetY).add(temp);
                            objectCells.get(i).get(t).remove(temp);
                            temp.reset();
                        } else {
                            temp.cellOffsetX = 0;
                            temp.cellOffsetY = 0;
                            temp.transfer = false;
                        }
                    }
                }
                //sortObjectCells(objectCells.get(i).get(t));
            }
        }
    }

    public void deleteObjectCellsForEntity(Entity e) {
        for (int i = 0; i < area.width; i++) {
            for (int t = 0; t < area.height; t++) {
                for (int z = 0; z < objectCells.get(i).get(t).size(); z++) {
                    if (objectCells.get(i).get(t).get(z).entity.equals(e)) {
                        objectCells.get(i).get(t).remove(z);
                    }
                }
            }
        }
    }

    public void resetCheckPoints() {
        if (checkPoints == null) return;
        for (int i = 0; i < checkPoints.size(); i++) {
            checkPoints.get(i).turnOff();
        }
        area.saved = false;
    }


    public void removeParticles() {
        while (particles.size() > 0) {
            fallingObjects.remove(particles.get(particles.size()-1));
            deleteObjectCellsForEntity(particles.get(particles.size()-1));
            particles.remove(particles.size()-1);
        }
    }

    public void draw(GameMenu menu, SpriteBatch batch, World world, float offsetX, float offsetY, boolean drawPlayer, float baseAlpha) {
        if (area.zoom == 2.0f) {
            offsetX -= 75;
            offsetY -= 54;
        }
        ArrayList<ObjectCell> objectsOnLevel = new ArrayList<ObjectCell>();
        if (!area.platformMode) {
            area.playerTileX = (int)Math.floor(area.player.x/area.TILE_WIDTH);
            area.playerTileY = (int)Math.floor((area.player.h - 2)/area.TILE_HEIGHT)+2;
            if (area.playerTileX < 0) {
                area.playerTileX = 0;
            } else if (area.playerTileX > area.width) {
                area.playerTileX = area.width-1;
            }
            if (area.playerTileY < 0) {
                area.playerTileY = 0;
            } else if (area.playerTileY > area.height) {
                area.playerTileY = area.height-1;
            }

            boolean playerDrawn = false;
            int playerDrawNow = -1;

            for (int i = 0; i <= area.height + 1; ++i) {
                objectsOnLevel.clear();
                for (int t = 0; t < area.width; ++t) {
                    /*
                    if (i < area.height && blocks.get(0).get(t).get(i) >= 0) {
                        drawLayer(batch, world, 0, offsetX, offsetY, i, t);
                    }*/
                    if (i < area.height && blocks.get(0).get(t).get(i) >= 0 && (i == 0 || (blocks.get(0).get(t).get(i-1) == -1) ||
                            t == 0 || (blocks.get(0).get(t-1).get(i) == -1 && blocks.get(1).get(t-1).get(i) == -1) || t == area.width-1 || (blocks.get(0).get(t+1).get(i) == -1 && blocks.get(1).get(t+1).get(i) == -1) /*|| blocks.get(0).get(t+1).get(i) == -1*/)) {
                        int iPlus = 0;
                        while (i + iPlus < area.height && blocks.get(0).get(t).get(i + iPlus) >= 0) {
                            drawLayer(batch, world, 0, offsetX, offsetY, i + iPlus, t);
                            iPlus++;
                        }
                    }

                    /*if (i < area.height) {
                        for (int z =0; z < objectCells.get(t).get(i).size(); ++z) {
                            if (objectCells.get(t).get(i).get(z).entity.floor || objectCells.get(t).get(i).get(z).entity.falling) {
                                objectCells.get(t).get(i).get(z).entity.draw(batch, offsetX, offsetY, area.TILE_WIDTH, area.TILE_HEIGHT, area.platformMode);
                            }
                        }
                    }*/
                    if (i > 0 && i <= area.height && blocks.get(2).get(t).get(i - 1) == 2) {
                        drawLayer(batch, world, 1, offsetX, offsetY, i - 1, t);
                    }

                    /*if (i == area.height) {
                        continue;
                    }*/
                    if (i > 0) {
                        for (int z =0; z < objectCells.get(t).get(i - 1).size(); ++z) {
                            if (objectCells.get(t).get(i - 1).get(z) != null) {
                                if (objectCells.get(t).get(i - 1).get(z).entity.floor) {
                                    objectCells.get(t).get(i - 1).get(z).entity.draw(batch, offsetX, offsetY, area.TILE_WIDTH, area.TILE_HEIGHT, area.platformMode, objectCells.get(t).get(i - 1).get(z) == activeObject, 0, 0);
                                } else {
                                    objectsOnLevel.add(objectCells.get(t).get(i - 1).get(z));
                                }
                            }
                        }
                    }
                    /*if (i == area.playerTileY && t == area.playerTileX) {
                        objectsOnLevel.add(new ObjectCell(area.TILE_WIDTH, area.TILE_HEIGHT, area.player, ObjectType.PLAYER, 0, true));
                    }*/
                }
                sortObjectCells(objectsOnLevel);
                if (!playerDrawn) {
                    for (int z =0; z < objectsOnLevel.size(); ++z) {
                        if ((objectsOnLevel.get(z).hIsY && objectsOnLevel.get(z).entity.h > area.player.h) || (!objectsOnLevel.get(z).hIsY && objectsOnLevel.get(z).entity.y > area.player.h)) {
                            //objectsOnLevel.add();
                            objectsOnLevel.add(new ObjectCell(area.TILE_WIDTH, area.TILE_HEIGHT, area.player, ObjectType.PLAYER, 0, true));
                            playerDrawNow = z;
                            break;
                        }
                    }
                    if (playerDrawNow == -1 && i == area.playerTileY) {
                        objectsOnLevel.add(new ObjectCell(area.TILE_WIDTH, area.TILE_HEIGHT, area.player, ObjectType.PLAYER, 0, true));
                        playerDrawNow = objectsOnLevel.size() - 1;
                    }
                }
                /*if (i == area.height && !playerDrawn) {
                    playerDrawNow = objectsOnLevel.size() - 1;
                }*/
                for (int z =0; z < objectsOnLevel.size(); ++z) {
                    //int id = objectsOnLevel.get(z).id;
                    if (drawPlayer && z == playerDrawNow/* && objectsOnLevel.get(z).type == ObjectType.PLAYER*/) {
                        if (!area.platformMode) {
                            //ObjectCell player = new ObjectCell(area.TILE_WIDTH, area.TILE_HEIGHT, area.player, ObjectType.PLAYER, 0, true);
                            ObjectCell player = objectsOnLevel.get(objectsOnLevel.size() - 1);
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 0.3f));
                            float w = ((Player)player.entity).hitBox.width * 0.75f;
                            float w2 = 0;//((Player)objectsOnLevel.get(z).entity).hitBox.width*0.10f;
                            batch.draw(area.shadow, offsetX + ((Player) player.entity).hitBox.x + w2 / 2, offsetY - (((Player)player.entity).hitBox.y + ((Player)player.entity).hitBox.height / 2) + w2 / 2, w * 1.3f, w * 1.3f);
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                            area.player.draw(batch, offsetX, offsetY, false);
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, baseAlpha));
                            playerDrawn = true;
                            playerDrawNow = -1;
                        } else {
                            area.player.draw(batch, offsetX, offsetY, area.TILE_WIDTH, area.TILE_HEIGHT, area.platformMode, false, 0, 0);
                        }
                    }
                    boolean active = (!area.playerHidden && objectsOnLevel.get(z) == activeObject && activeObject.isSwitchable());
                    int activeX = 0;
                    int activeY = 0;
                    if (active) {
                        activeX = activeObject.offsetX;
                        activeY = activeObject.offsetY;
                    }

                    if (objectsOnLevel.get(z).type == ObjectType.NPC) {
                        if (!area.platformMode) {
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 0.3f));
                            float w = ((NPC)objectsOnLevel.get(z).entity).hitBox.width*0.75f;
                            float w2 = 0;//((Player)objectsOnLevel.get(z).entity).hitBox.width*0.10f;
                            NPC npc = ((NPC)objectsOnLevel.get(z).entity);
                            batch.draw(area.shadow, offsetX + npc.hitBox.x+w2/2, offsetY - (((NPC)objectsOnLevel.get(z).entity).hitBox.y + npc.hitBox.height/2)+w2/2, w*1.3f, w*1.3f);
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, baseAlpha));
                            npc.draw(batch, offsetX, offsetY, npc == activeNPC);
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, baseAlpha));
                        } else {
                            NPC npc = ((NPC)objectsOnLevel.get(z).entity);
                            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                            npc.draw(batch, offsetX, offsetY, area.TILE_WIDTH, area.TILE_HEIGHT, area.platformMode, npc == activeNPC, 0, 0);
                        }
                    }
                    else if (objectsOnLevel.get(z).type == ObjectType.SOLID) {
                            (objectsOnLevel.get(z).entity).draw(batch, offsetX, offsetY, area.TILE_WIDTH, area.TILE_HEIGHT, area.platformMode, active, activeX, activeY);
                        } else if (objectsOnLevel.get(z).type == ObjectType.NONSOLID) {
                            (objectsOnLevel.get(z).entity).draw(batch, offsetX, offsetY, area.TILE_WIDTH, area.TILE_HEIGHT, area.platformMode, active, activeX, activeY);
                        } else if (objectsOnLevel.get(z).type == ObjectType.PARTICLE) {
                            (objectsOnLevel.get(z).entity).draw(batch, offsetX, offsetY, area.TILE_WIDTH, area.TILE_HEIGHT, area.platformMode, active, activeX, activeY);
                            if (!objectsOnLevel.get(z).entity.floor && !((Particle)objectsOnLevel.get(z).entity).fallen && objectsOnLevel.get(z).entity.z > 0) {
                                float w = objectsOnLevel.get(z).entity.getTexRect().getWidth()/1.0f+objectsOnLevel.get(z).entity.z/3;
                                batch.setColor(new Color(1.0f, 1.0f, 1.0f, baseAlpha*(0.45f-objectsOnLevel.get(z).entity.z/50)));
                                batch.draw(area.shadow, offsetX + objectsOnLevel.get(z).entity.x - w/2, offsetY - (objectsOnLevel.get(z).entity.y + w/2), w, w);
                                batch.setColor(new Color(1.0f, 1.0f, 1.0f, baseAlpha));
                            }
                        } else if (objectsOnLevel.get(z).type == ObjectType.OBSTACLE) {
                            (objectsOnLevel.get(z).entity).draw(batch, offsetX, offsetY, area.TILE_WIDTH, area.TILE_HEIGHT, area.platformMode, active, activeX, activeY);
                        }

                }
            }

        } else {
            for (int i = 0; i < area.height; ++i) {
                for (int t = 0; t < area.width; ++t) {
                    drawLayer(batch, world, 0, offsetX, offsetY, i, t);
                }
            }

            for (int i = 0; i < area.height; ++i) {

                for (int t = 0; t < area.width; ++t) {
                    for (int z =0; z < objectCells.get(t).get(i).size(); ++z) {
                        objectsOnLevel.add(objectCells.get(t).get(i).get(z));
                        //objectCells.get(t).get(i).get(z).entity.draw(batch, offsetX, offsetY, area.TILE_WIDTH, area.TILE_HEIGHT, area.platformMode);
                    }
                    if (i == area.playerTileY && t == area.playerTileX) {
                        objectsOnLevel.add(new ObjectCell(area.TILE_WIDTH, area.TILE_HEIGHT, area.player, ObjectType.PLAYER, 0, true));
                    }
                }

            }
            for (int i = 0; i < area.height; ++i) {
                for (int t = 0; t < area.width; ++t) {
                    drawLayer(batch, world, 1, offsetX, offsetY, i, t);
                }
            }

            for (int z =0; z < objectsOnLevel.size(); ++z) {
                objectsOnLevel.get(z).entity.draw(batch, offsetX, offsetY, area.TILE_WIDTH, area.TILE_HEIGHT, area.platformMode, objectsOnLevel.get(z) == activeObject, 0, 0);
            }
        }
    }

    private void drawLayer(SpriteBatch batch, World world, int layer, float offsetX, float offsetY, int i, int t) {
        int type = blocks.get(layer).get(t).get(i);
        if (type == -1) return;
        boolean up = i==0 || blocks.get(layer).get(t).get(i - 1)==type;
        boolean down = i==area.height-1 || blocks.get(layer).get(t).get(i+1)==type;
        boolean left = t==0 || blocks.get(layer).get(t - 1).get(i)==type;
        boolean right = t==area.width-1 || blocks.get(layer).get(t + 1).get(i)==type;
        try {
            //
            //type = blocks.get(0).get(t).get(i)-world.spritesCount;
            if (world.tileTypes.get(blocks.get(layer).get(t).get(i)) != 0/*blocks.get(layer).get(t).get(i) >= world.spritesCount*/) {
                TextureRegion img = world.tiles.get(world.tileIndices.get(blocks.get(layer).get(t).get(i))/*blocks.get(layer).get(t).get(i)-world.spritesCount*/).getTile(up, down, left, right);
                float x = offsetX + t * (area.TILE_WIDTH) + area.TILE_WIDTH/2 - img.getRegionWidth()/2;
                float y = offsetY - i * area.TILE_HEIGHT-img.getRegionHeight()+area.TILE_HEIGHT;
                float y2 = 0;
                if (!area.platformMode) {
                    y2 = offsetY - i * area.TILE_HEIGHT;
                } else {
                    y+=area.FLOOR_HEIGHT/2+1;
                    if (layer == 0) {
                        y-=area.TILE_HEIGHT-img.getRegionHeight();
                    }
                    y2 = offsetY - i * area.TILE_HEIGHT+area.FLOOR_HEIGHT/2+1;
                }
                if (layer == 0)batch.draw(img, x, y, img.getRegionWidth(), img.getRegionHeight());
                else batch.draw(img, x, y2, img.getRegionWidth(), img.getRegionHeight());
            } else {
                Texture img = world.sprites.get(world.tileIndices.get(blocks.get(layer).get(t).get(i)));
                float x = offsetX + t * (area.TILE_WIDTH) + area.TILE_WIDTH/2 - img.getWidth()/2;
                float y = offsetY - i * area.TILE_HEIGHT-img.getHeight()+area.TILE_HEIGHT;
                float y2 = 0;
                if (!area.platformMode) {
                    y2 = offsetY - i * area.TILE_HEIGHT;
                } else {
                    y+=area.FLOOR_HEIGHT/2+1;
                    if (layer == 0) {
                        y-=area.TILE_HEIGHT-img.getHeight();
                    }
                    y2 = offsetY - i * area.TILE_HEIGHT+area.FLOOR_HEIGHT/2+1;
                }
                if (layer == 0) batch.draw(img, x, y, img.getWidth(), img.getHeight());
                else  {
                    batch.draw(img, x, y2, img.getWidth(), img.getHeight());
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
