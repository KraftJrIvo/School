package com.mygdx.schoolRPG;

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
    public int tileX;
    public int tileY;
    public Exit otherExit;

    public Exit(RoomNode room, ExitDirection direction, int tileX, int tileY, Exit otherExit) {
        this.room = room;
        this.direction = direction;
        this.tileX = tileX;
        this.tileY = tileY;
        this.otherExit = otherExit;
    }
}

class RoomNode{
    public int roomX, roomY, roomZ;
    public String name;
    public ArrayList<Exit> exits;

    public RoomNode(int roomX, int roomY, int roomZ, String name) {
        this.roomX = roomX;
        this.roomY = roomY;
        this.roomZ = roomZ;
        this.name = name;
        this.exits = new ArrayList<Exit>();
    }
}

public class RoomsMap {
    World world;
    ArrayList<RoomNode> rooms;

    public RoomsMap(World world) {
        this.world = world;
        rooms = new ArrayList<RoomNode>();
    }

    public void addRoom(Area area, int roomX, int roomY, int roomZ) {
        RoomNode newRoom = null;//new RoomNode(roomX, roomY, roomZ, area.getExits());
        rooms.add(newRoom);
    }

    public Area getAreaByName(String name) {
        for (int i = 0; i < rooms.size(); ++i) {
            if (rooms.get(i).name.equals(name)) {
                RoomNode room = rooms.get(i);
                return world.areas.get(world.areaIds.get(room.roomX).get(room.roomY).get(room.roomZ));
            }
        }
        return null;
    }
}
