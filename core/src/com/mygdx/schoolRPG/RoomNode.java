package com.mygdx.schoolRPG;

import com.mygdx.schoolRPG.tools.IntCoords;
import com.mygdx.schoolRPG.tools.PathFinder;

import java.util.ArrayList;

public class RoomNode{
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
