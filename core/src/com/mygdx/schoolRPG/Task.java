package com.mygdx.schoolRPG;

import com.mygdx.schoolRPG.tools.CharacterDirectionChecker;
import com.mygdx.schoolRPG.tools.IntCoords;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Kraft on 14.08.2017.
 */
enum TaskType {
    PATROL,
    USE
}

public class Task {
    public String destinationRoomName;
    public TaskType type;
    public long time;
    public boolean started = false;
    public boolean destinationReached = false;
    public boolean startPointReached = false;
    public long startedTime;
    public boolean finished = false;
    public boolean random = false;
    public int toUse;
    public int currentUses;
    public String objectName;
    ArrayList<IntCoords> coords;
    ArrayList<CharacterDirectionChecker.LookDirection> directions;
    ArrayList<Long> intervals;

    public TaskType taskTypeFromString(String str) {
        if (str.equals("patrol")) {
            return TaskType.PATROL;
        }
        if (str.equals("use")) {
            return TaskType.USE;
        }
        return null;
    }

    public CharacterDirectionChecker.LookDirection lookDirectionFromString(String str) {
        if (str.equals("up")) {
            return CharacterDirectionChecker.LookDirection.up;
        }
        if (str.equals("down")) {
            return CharacterDirectionChecker.LookDirection.down;
        }
        if (str.equals("left")) {
            return CharacterDirectionChecker.LookDirection.left;
        }
        if (str.equals("right")) {
            return CharacterDirectionChecker.LookDirection.right;
        }
        return null;
    }

    public Task(BufferedReader in, World world) {
        try {
            destinationRoomName = in.readLine();
            String line = in.readLine();
            type = taskTypeFromString(line);
            coords = new ArrayList<IntCoords>();
            directions = new ArrayList<CharacterDirectionChecker.LookDirection>();
            intervals = new ArrayList<Long>();
            line = in.readLine();
            objectName = null;
            if (type == TaskType.PATROL) {
               if (line.equals("random")) {
                   random = true;
                   String interval = in.readLine();
                   intervals.add(Long.parseLong(interval));
               } else {
                   int placesNum = Integer.parseInt(line);
                   for (int i = 0; i < placesNum; ++i) {
                       String x = in.readLine();
                       String y = in.readLine();
                       coords.add(new IntCoords(Integer.parseInt(x), Integer.parseInt(y)));
                       String dir = in.readLine();
                       directions.add(lookDirectionFromString(dir));
                       String interval = in.readLine();
                       intervals.add(Long.parseLong(interval));
                   }
               }
               String totalTime = in.readLine();
               time = Long.parseLong(totalTime);
            } else if (type == TaskType.USE) {
                objectName = in.readLine();
                /*Area area = world.map.getAreaByName(destinationRoomName);
                ArrayList<ObjectCell> objects = area.worldObjectsHandler.objects;
                for (int i = 0; i < objects.size(); ++i) {
                    if (objects.get(i).statesTex.get(0).equals(objectName + ".png")) {
                        coords.add(new IntCoords((int)(objects.get(0).entity.x / area.TILE_WIDTH), (int)(objects.get(0).entity.y / area.TILE_HEIGHT)));
                        break;
                    }
                }*/
                String uses = in.readLine();
                toUse = Integer.parseInt(uses);
                currentUses = toUse;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
