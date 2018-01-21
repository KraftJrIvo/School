package com.mygdx.schoolRPG;

import com.mygdx.schoolRPG.tools.IntCoords;
import com.mygdx.schoolRPG.tools.PathFinder;

import java.util.ArrayList;

/**
 * Created by Kraft on 23.05.2017.
 */
enum ExitDirection{
    WEST,
    EAST,
    NORTH,
    SOUTH,
    UP,
    DOWN
}

class Exit{
    public RoomNode room;
    public ExitDirection direction;
    public int x;
    public int y;
    public int offsetX = 0;
    public int offsetY = 0;
    public int size;
    public Exit otherExit;
    public ArrayList<Exit> reachableExits;
    public boolean useRequired = false;

    public Exit(RoomNode room, ExitDirection direction, int centerX, int centerY, int size) {
        this.room = room;
        this.direction = direction;
        this.x = centerX;
        this.y = centerY;
        this.size = size;
        reachableExits = new ArrayList<Exit>();
        if (direction == ExitDirection.WEST) {
            offsetX = -1;
        } else if (direction == ExitDirection.EAST) {
            offsetX = 1;
        } else if (direction == ExitDirection.NORTH) {
            offsetY = -1;
        } else if (direction == ExitDirection.SOUTH) {
            offsetY = 1;
        }
    }
}

class RoomNode{
    public int roomX, roomY, roomZ;
    public String name;
    public ArrayList<Exit> exits;
    private RoomsMap map;
    public ArrayList<ArrayList<Boolean>> walkables;
    int width;
    int height;

    public RoomNode(int roomX, int roomY, int roomZ, int width, int height, String name, RoomsMap map) {
        this.roomX = roomX;
        this.roomY = roomY;
        this.roomZ = roomZ;
        this.name = name;
        this.exits = new ArrayList<Exit>();
        this.map = map;
        this.width = width;
        this.height = height;
    }

    public void addExit(Exit exit, Area area, boolean useRequired) {
        exit.useRequired = useRequired;
        exits.add(exit);
        /*if (exit.direction == ExitDirection.DOWN || exit.direction == ExitDirection.UP) {
            int remainingXDist = (exit.x/32)%map.world.firtsAreaWidth;
            int wholeRoomXBlocks = (exit.x/32 - remainingXDist) / map.world.firtsAreaWidth;
            int remainingYDist = (exit.y/16)%map.world.firtsAreaHeight;
            int wholeRoomYBlocks = (exit.y/16 - remainingYDist) / map.world.firtsAreaHeight;
            int newRoomX = roomX + wholeRoomXBlocks;
            int newRoomY = roomY + height - wholeRoomYBlocks - 1;
            int newRoomZ;
            if (exit.direction == ExitDirection.DOWN) {
                newRoomZ = roomZ - 1;
            } else {
                newRoomZ = roomZ + 1;
            }
            int areaId = map.world.areaIds.get(newRoomX).get(newRoomY).get(newRoomZ);
            Area newArea = map.world.areas.get(areaId);
            RoomNode newRoom = map.getRoomByName(newArea.name);
            newRoom.addExit();
        }*/
        for (int i = 0; i < exits.size(); ++i) {
            PathFinder pathFinder = new PathFinder();
            Exit exit1 = exit;
            Exit exit2 = exits.get(i);
            IntCoords exit1Coords = new IntCoords((int)Math.floor(exit1.x/area.TILE_WIDTH), (int)Math.floor(exit1.y/area.TILE_HEIGHT));
            IntCoords exit2Coords = new IntCoords((int)Math.floor(exit2.x/area.TILE_WIDTH), (int)Math.floor(exit2.y/area.TILE_HEIGHT));
            ArrayList<IntCoords> path = pathFinder.getAStarPath(walkables, exit1Coords, exit2Coords);
            if (path != null && path.size() > 0) {
                exit1.reachableExits.add(exit2);
                exit2.reachableExits.add(exit1);
                /*ArrayList<ArrayList<Integer>> blockTextures =  area.blocks.get(0);
                for (int k = 0; k < path.size(); ++k) {
                    IntCoords curcor = path.get(k);
                    blockTextures.get(curcor.x).set(curcor.y, 1);
                }*/
            }
        }
    }

}

public class RoomsMap {
    World world;
    ArrayList<RoomNode> rooms;
    ArrayList<ArrayList<Boolean>> walkables;

    public RoomsMap(World world) {
        this.world = world;
        rooms = new ArrayList<RoomNode>();
    }

    public void addRoom(Area area, int roomX, int roomY, int roomZ, int width, int height, String name) {
        RoomNode newRoom = new RoomNode(roomX, roomY, roomZ, width, height, name, this);
        rooms.add(newRoom);
        //ArrayList<ArrayList<Integer>> floor =  area.blocks.get(0);
        //ArrayList<ArrayList<Integer>> walls =  area.blocks.get(1);
        ArrayList<ArrayList<Integer>> blockTypes =  area.blocks.get(2);
        boolean curBlock = false;
        boolean prevBlock = false;
        walkables = new ArrayList<ArrayList<Boolean>>();
        for (int i = 0; i < area.width; ++i) {
            walkables.add(new ArrayList<Boolean>());
            for (int j = 0; j < area.height; ++j) {
                walkables.get(i).add(blockTypes.get(i).get(j) != -1 && blockTypes.get(i).get(j) != 2
                        && blockTypes.get(i).get(j) != 4);
            }
        }
        for (int i = 0; i < area.width; ++i) {
            for (int t = 0; t < area.height; t+=area.height-1) {
                curBlock = walkables.get(i).get(t);
                if (curBlock && !prevBlock) {
                    int blocksCount = 1;
                    int startBlock = i;
                    i++;
                    if (i >= walkables.size()) curBlock = false;
                    else curBlock = walkables.get(i).get(t);
                    while (curBlock) {
                        blocksCount++;
                        if (i >= walkables.size()) curBlock = false;
                        else curBlock = walkables.get(i).get(t);
                        i++;
                    }
                    i = startBlock;
                    if (t == 0) {
                        newRoom.exits.add(new Exit(newRoom, ExitDirection.NORTH, (startBlock + blocksCount/2) * area.TILE_WIDTH, area.TILE_HEIGHT/2, blocksCount));
                    } else {
                        newRoom.exits.add(new Exit(newRoom, ExitDirection.SOUTH, (startBlock + blocksCount/2) * area.TILE_WIDTH, area.height * area.TILE_HEIGHT - area.TILE_HEIGHT/2, blocksCount));
                    }
                }
                prevBlock = curBlock;
            }
            prevBlock = false;
        }
        for (int i = 0; i < area.height; ++i) {
            for (int t = 0; t < area.width; t+=area.width-1) {
                curBlock = walkables.get(t).get(i);
                if (curBlock && !prevBlock) {
                    int blocksCount = 1;
                    int startBlock = i;
                    i++;
                    if (i >= walkables.get(0).size()) curBlock = false;
                    else curBlock = walkables.get(t).get(i);
                    while (curBlock) {
                        blocksCount++;
                        if (i >= walkables.get(0).size()) curBlock = false;
                        else curBlock = walkables.get(t).get(i);
                        i++;
                    }
                    i = startBlock;
                    if (t == 0) {
                        newRoom.exits.add(new Exit(newRoom, ExitDirection.WEST, area.TILE_WIDTH/2, (startBlock + blocksCount/2) * area.TILE_HEIGHT, blocksCount));
                    } else {
                        newRoom.exits.add(new Exit(newRoom, ExitDirection.EAST, area.width * area.TILE_WIDTH - area.TILE_WIDTH/2, (startBlock + blocksCount/2) * area.TILE_HEIGHT, blocksCount));
                    }
                }
                prevBlock = curBlock;
            }
            prevBlock = false;
        }
        newRoom.walkables = walkables;
        for (int i = 0; i < newRoom.exits.size(); ++i) {
            for (int t = 0; t < newRoom.exits.size(); ++t) {
                Exit exit1 = newRoom.exits.get(i);
                Exit exit2 = newRoom.exits.get(t);
                if (exit1 != exit2 && exit1.x == exit2.x && exit1.y == exit2.y) {
                    int size = Math.max(exit1.size, exit2.size);
                    newRoom.exits.remove(exit2);
                    exit1.size = size;
                }
            }
        }
        for (int i = 0; i < newRoom.exits.size(); ++i) {
            for (int t = i + 1; t < newRoom.exits.size(); ++t) {
                if (i == t) continue;
                PathFinder pathFinder = new PathFinder();
                Exit exit1 = newRoom.exits.get(i);
                Exit exit2 = newRoom.exits.get(t);
                IntCoords exit1Coords = new IntCoords((int)Math.floor(exit1.x/area.TILE_WIDTH), (int)Math.floor(exit1.y/area.TILE_HEIGHT));
                IntCoords exit2Coords = new IntCoords((int)Math.floor(exit2.x/area.TILE_WIDTH), (int)Math.floor(exit2.y/area.TILE_HEIGHT));
                ArrayList<IntCoords> path = pathFinder.getAStarPath(walkables, exit1Coords, exit2Coords);
                if (path != null && path.size() > 0) {
                    exit1.reachableExits.add(exit2);
                    exit2.reachableExits.add(exit1);
                    /*ArrayList<ArrayList<Integer>> blockTextures =  area.blocks.get(0);
                    for (int k = 0; k < path.size(); ++k) {
                        IntCoords curcor = path.get(k);
                        blockTextures.get(curcor.x).set(curcor.y, 1);
                    }*/
                }
            }
        }
    }

    public RoomNode getRoomByName(String name) {
        for (int i = 0; i < rooms.size(); ++i) {
            if (rooms.get(i).name.equals(name)) {
                RoomNode room = rooms.get(i);
                return room;
            }
        }
        return null;
    }

    public Area getAreaByName(String name) {
        RoomNode room =  getRoomByName(name);
        return world.areas.get(world.areaIds.get(room.roomX).get(room.roomY).get(room.roomZ));
    }

    public void connectExits() {
        for (int i = 0; i < rooms.size(); ++i) {
            RoomNode room = rooms.get(i);
            Area cArea = getAreaByName(room.name);
            //System.out.println();
            for (int t = 0; t < room.exits.size(); ++t) {
                Exit exit = room.exits.get(t);
                if (exit.otherExit != null) continue;
                int remainingXDist = (exit.x/cArea.TILE_WIDTH)%world.firtsAreaWidth;
                int wholeRoomXBlocks = (exit.x/cArea.TILE_WIDTH - remainingXDist) / world.firtsAreaWidth;
                int remainingYDist = (exit.y/cArea.TILE_HEIGHT)%world.firtsAreaHeight;
                int wholeRoomYBlocks = (exit.y/cArea.TILE_HEIGHT - remainingYDist) / world.firtsAreaHeight;
                int newRoomX = room.roomX + wholeRoomXBlocks;
                int newRoomY = room.roomY + room.height - wholeRoomYBlocks - 1;
                int newRoomZ = room.roomZ;
                if (exit.direction == ExitDirection.EAST) {
                    newRoomX = room.roomX + room.width;
                } else if (exit.direction == ExitDirection.WEST) {
                    newRoomX = room.roomX - 1;
                } else if (exit.direction == ExitDirection.NORTH) {
                    newRoomY = room.roomY + room.height;
                } else if (exit.direction == ExitDirection.SOUTH) {
                    newRoomY = room.roomY - 1;
                } else if (exit.direction == ExitDirection.DOWN) {
                    newRoomZ -= 1;
                } else if (exit.direction == ExitDirection.UP) {
                    newRoomZ += 1;
                }
                int areaId = -1;
                if (newRoomX < world.areaIds.size() && newRoomY < world.areaIds.get(0).size() && newRoomZ < world.areaIds.get(0).get(0).size()) {
                    areaId = world.areaIds.get(newRoomX).get(newRoomY).get(newRoomZ);
                }
                if (areaId == -1) continue;
                Area area = world.areas.get(areaId);
                RoomNode newRoom = getRoomByName(area.name);
                int newRoomDiffX = newRoom.roomX - room.roomX;
                int newRoomDiffY = newRoom.roomY - room.roomY;
                Exit closestExit = null;
                int closestDiff = 9999999;
                int diff = 0;
                for (int k = 0; k < newRoom.exits.size(); ++k) {
                    Exit exit2 = newRoom.exits.get(k);
                    if (exit.direction == ExitDirection.EAST && exit2.direction == ExitDirection.WEST ||
                            exit.direction == ExitDirection.WEST && exit2.direction == ExitDirection.EAST) {
                        diff = Math.abs(exit.y - (exit2.y + newRoomDiffY * world.firtsAreaHeight * area.TILE_HEIGHT));
                    } else if (exit.direction == ExitDirection.NORTH && exit2.direction == ExitDirection.SOUTH ||
                            exit.direction == ExitDirection.SOUTH && exit2.direction == ExitDirection.NORTH) {
                        diff = Math.abs(exit.x - (exit2.x + newRoomDiffX * world.firtsAreaWidth * area.TILE_WIDTH));
                    } else if (exit.direction == ExitDirection.DOWN && exit2.direction == ExitDirection.UP ||
                            exit.direction == ExitDirection.UP && exit2.direction == ExitDirection.DOWN) {
                        int diffX = Math.abs(exit.x - (exit2.x + newRoomDiffX * world.firtsAreaWidth * area.TILE_WIDTH));
                        int diffY = Math.abs(exit.y - (exit2.y + newRoomDiffY * world.firtsAreaHeight * area.TILE_HEIGHT));
                        diff = (int)Math.sqrt(diffX * diffX + diffY * diffY);
                    } else {
                        diff = closestDiff;
                    }
                    if (diff < closestDiff) {
                        closestDiff = diff;
                        closestExit = exit2;
                    }
                }
                if (closestExit != null) {
                    exit.otherExit = closestExit;
                    closestExit.otherExit = exit;
                }
            }
        }
        //System.out.println();
    }

    public ArrayList<Exit> findPathToRoom(RoomNode start, IntCoords startCoords, RoomNode goal, IntCoords goalCoords) {
        ArrayList<Exit> firstlyReachableExits = new ArrayList<Exit>();
        Area startArea = getAreaByName(start.name);
        PathFinder pathFinder = new PathFinder();
        ArrayList<Integer> exitQueueLevels = new ArrayList<Integer>();
        for (int i = 0; i < start.exits.size(); ++i) {
            Exit exit = start.exits.get(i);
            IntCoords exitCoords = new IntCoords((int)Math.floor(exit.x/startArea.TILE_WIDTH), (int)Math.floor(exit.y/startArea.TILE_HEIGHT));
            ArrayList<IntCoords> path = pathFinder.getAStarPath(start.walkables, startCoords, exitCoords);
            if (path != null && path.size() > 0) {
                firstlyReachableExits.add(exit);
                exitQueueLevels.add(0);
            }
        }
        ArrayList<ArrayList<Exit>> paths;
        RoomNode curRoom;
        boolean pathFound = false;
        ArrayList<Exit> exitQueue = firstlyReachableExits;
        ArrayList<Exit> doneExits = new ArrayList<Exit>();
        ArrayList<Exit> curExits = new ArrayList<Exit>();
        Exit curExit;
        int curLevel = 0;
        while (!pathFound) {
            Exit bestExit = null;
            int bestDist = 999999;
            for (int i = 0; i < exitQueue.size(); ++i) {
                Exit exit = exitQueue.get(i);
                if (curLevel > 0 && (doneExits.contains(exit) || exitQueueLevels.get(i) != curLevel)) {
                    continue;
                }
                RoomNode room = exit.otherExit.room;
                int dist = Math.abs(goal.roomX - room.roomX) + Math.abs(goal.roomY - room.roomY) + Math.abs(goal.roomZ - room.roomZ);
                if (dist < bestDist) {
                    bestDist = dist;
                    bestExit = exit;
                }
            }
            if (bestExit == null) {
                if (curLevel == 0) {
                    break;
                } else {
                    curLevel--;
                    curExits.remove(curExits.size() - 1);
                }
            } else {
                curLevel++;
                curExits.add(bestExit);
                for (int i = 0; i < bestExit.otherExit.reachableExits.size(); ++i) {
                    Exit exit = bestExit.otherExit.reachableExits.get(i);
                    exitQueue.add(exit);
                    exitQueueLevels.add(curLevel);
                }
                doneExits.add(bestExit);
                exitQueueLevels.remove(exitQueue.indexOf(bestExit));
                exitQueue.remove(bestExit);
                curExit = bestExit;
                if (curExit.otherExit.room == goal) {
                    if (goalCoords.x == -1 && goalCoords.y == -1) {
                        pathFound = true;
                    } else {
                        Area area = getAreaByName(goal.name);
                        IntCoords exitCoords = new IntCoords((int)Math.floor(curExit.otherExit.x/area.TILE_WIDTH), (int)Math.floor(curExit.otherExit.y/area.TILE_HEIGHT));
                        ArrayList<IntCoords> path = pathFinder.getAStarPath(goal.walkables, exitCoords, goalCoords);
                        pathFound = (path != null && path.size() > 0);
                    }
                }
            }
        }
        if (pathFound) {
            return curExits;
        }
        return null;
    }

}
