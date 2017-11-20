package com.mygdx.schoolRPG.tools;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kraft on 14.08.2017.
 */
public class PathFinder {

    LinkedList<IntCoords> openList;
    LinkedList<IntCoords> closedList;
    boolean done;

    public PathFinder() {

    }

    private IntCoords lowestFInOpen() {
        float minF = 99999;
        int minId = -1;
        for (int i = 0; i < openList.size(); ++i) {
            float curCosts = openList.get(i).hCosts + openList.get(i).gCosts;
            if (curCosts < minF) {
                minF = curCosts;
                minId = i;
            }
        }
        if (minId >= 0) {
            return openList.get(minId);
        }
        return null;
    }

    private ArrayList<IntCoords> calcPath(IntCoords current) {
        ArrayList<IntCoords> list = new ArrayList<IntCoords>();
        list.add(current);
        while (current.previous != null) {
            list.add(current.previous);
            current = current.previous;
        }
        return list;
    }

    private ArrayList<IntCoords> getAdjacent(ArrayList<ArrayList<IntCoords>> field, ArrayList<ArrayList<Boolean>> boolField, IntCoords current) {
        ArrayList<IntCoords> list = new ArrayList<IntCoords>();
        if (current.x < 0 || current.y < 0 || current.x >= field.size() || current.y >= field.get(0).size()) return list;
        if (current.x > 0 && boolField.get(current.x - 1).get(current.y)) {
            if (field.get(current.x - 1).get(current.y) == null) {
                IntCoords newNode = new IntCoords(current.x - 1, current.y);
                list.add(newNode);
                field.get(current.x - 1).set(current.y, newNode);
            } else {
                list.add(field.get(current.x - 1).get(current.y));
            }
        }
        if (current.x < field.size() - 1 && boolField.get(current.x + 1).get(current.y)) {
            if (field.get(current.x + 1).get(current.y) == null) {
                IntCoords newNode = new IntCoords(current.x + 1, current.y);
                list.add(newNode);
                field.get(current.x + 1).set(current.y, newNode);
            } else {
                list.add(field.get(current.x + 1).get(current.y));
            }
        }
        if (current.y > 0 && boolField.get(current.x).get(current.y - 1)) {
            if (field.get(current.x).get(current.y - 1) == null) {
                IntCoords newNode = new IntCoords(current.x, current.y - 1);
                list.add(newNode);
                field.get(current.x).set(current.y - 1, newNode);
            } else {
                list.add(field.get(current.x).get(current.y - 1));
            }
        }
        if (current.y < field.get(0).size() - 1 && boolField.get(current.x).get(current.y + 1)) {
            if (field.get(current.x).get(current.y + 1) == null) {
                IntCoords newNode = new IntCoords(current.x, current.y + 1);
                list.add(newNode);
                field.get(current.x).set(current.y + 1, newNode);
            } else {
                list.add(field.get(current.x).get(current.y + 1));
            }
        }
        if (current.x > 0 && current.y > 0 && boolField.get(current.x - 1).get(current.y - 1)
                && boolField.get(current.x - 1).get(current.y)&& boolField.get(current.x).get(current.y - 1)) {
            if (field.get(current.x - 1).get(current.y - 1) == null) {
                IntCoords newNode = new IntCoords(current.x - 1, current.y - 1);
                list.add(newNode);
                field.get(current.x - 1).set(current.y - 1, newNode);
            } else {
                list.add(field.get(current.x - 1).get(current.y - 1));
            }
        }
        if (current.x < field.size() - 1 && current.y > 0 && boolField.get(current.x + 1).get(current.y - 1)
                && boolField.get(current.x + 1).get(current.y)&& boolField.get(current.x).get(current.y - 1)) {
            if (field.get(current.x + 1).get(current.y - 1) == null) {
                IntCoords newNode = new IntCoords(current.x + 1, current.y - 1);
                list.add(newNode);
                field.get(current.x + 1).set(current.y - 1, newNode);
            } else {
                list.add(field.get(current.x + 1).get(current.y - 1));
            }
        }
        if (current.x > 0 && current.y < field.get(0).size() - 1 && boolField.get(current.x - 1).get(current.y + 1)
                && boolField.get(current.x - 1).get(current.y)&& boolField.get(current.x).get(current.y + 1)) {
            if (field.get(current.x - 1).get(current.y + 1) == null) {
                IntCoords newNode = new IntCoords(current.x - 1, current.y + 1);
                list.add(newNode);
                field.get(current.x - 1).set(current.y + 1, newNode);
            } else {
                list.add(field.get(current.x - 1).get(current.y + 1));
            }
        }
        if (current.x < field.size() - 1 && current.y < field.get(0).size() - 1 && boolField.get(current.x + 1).get(current.y + 1)
                && boolField.get(current.x + 1).get(current.y)&& boolField.get(current.x).get(current.y + 1)) {
            if (field.get(current.x + 1).get(current.y + 1) == null) {
                IntCoords newNode = new IntCoords(current.x + 1, current.y + 1);
                list.add(newNode);
                field.get(current.x + 1).set(current.y + 1, newNode);
            } else {
                list.add(field.get(current.x + 1).get(current.y + 1));
            }
        }
        return list;
    }

    public ArrayList<IntCoords> getAStarPath(ArrayList<ArrayList<Boolean>> field, IntCoords start, IntCoords goal) {
        openList = new LinkedList<IntCoords>();
        closedList = new LinkedList<IntCoords>();
        openList.add(new IntCoords(start.x, start.y)); // add starting node to open list
        done = false;
        IntCoords current;
        ArrayList<ArrayList<IntCoords>> checkField = new ArrayList<ArrayList<IntCoords>>();
        for (int i = 0; i < field.size(); ++i) {
            checkField.add(new ArrayList<IntCoords>());
            for (int t = 0; t < field.get(i).size(); ++t) {
                checkField.get(i).add(null);
            }
        }
        if (start.x < 0) start.x = 0;
        if (start.y < 0) start.y = 0;
        if (start.x >= checkField.size()) start.x = checkField.size() - 1;
        if (start.y >= checkField.get(0).size()) start.x = checkField.get(0).size() - 1;
        checkField.get(start.x).set(start.y, start);
        while (!done) {
            current = lowestFInOpen();// get node with lowest fCosts from openList
            closedList.add(current);
            openList.remove(current);
            if ((current.x == goal.x) && (current.y == goal.y)) { // found goal
                return calcPath(current);
            }
            ArrayList<IntCoords> adjacentNodes = getAdjacent(checkField, field, current);
            for (int i = 0; i < adjacentNodes.size(); i++) {
                IntCoords currentAdj = adjacentNodes.get(i);
                if (!openList.contains(currentAdj) && !closedList.contains(currentAdj)) { // node is not in openList
                    currentAdj.setPrevious(current); // set current node as previous for this node
                    currentAdj.sethCosts(goal); // set h costs of this node (estimated costs to goal)
                    currentAdj.setgCosts(current); // set g costs of this node (costs from start to this node)
                    openList.add(currentAdj); // add node to openList
                } else { // node is in openList
                    if (currentAdj.gCosts > currentAdj.calculategCosts(current)) { // costs from current node are cheaper than previous costs
                        currentAdj.setPrevious(current); // set current node as previous for this node
                        currentAdj.setgCosts(current); // set g costs of this node (costs from start to this node)
                    }
                }
            }
            if (openList.isEmpty()) { // no path exists
                return new ArrayList<IntCoords>(); // return empty list
            }
        }
        return null;
    }

    /*public final List<T> findPath(int oldX, int oldY, int newX, int newY) {
        openList = new LinkedList<IntCoords>();
        closedList = new LinkedList<IntCoords>();
        openList.add(nodes[oldX][oldY]); // add starting node to open list

        T current;
        while (!done) {
            current = lowestFInOpen(); // get node with lowest fCosts from openList
            closedList.add(current); // add current node to closed list
            openList.remove(current); // delete current node from open list

            if ((current.getxPosition() == newX)
                    && (current.getyPosition() == newY)) { // found goal
                return calcPath(nodes[oldX][oldY], current);
            }

            // for all adjacent nodes:
            List<T> adjacentNodes = getAdjacent(current);
            for (int i = 0; i < adjacentNodes.size(); i++) {
                T currentAdj = adjacentNodes.get(i);
                if (!openList.contains(currentAdj)) { // node is not in openList
                    currentAdj.setPrevious(current); // set current node as previous for this node
                    currentAdj.sethCosts(nodes[newX][newY]); // set h costs of this node (estimated costs to goal)
                    currentAdj.setgCosts(current); // set g costs of this node (costs from start to this node)
                    openList.add(currentAdj); // add node to openList
                } else { // node is in openList
                    if (currentAdj.getgCosts() > currentAdj.calculategCosts(current)) { // costs from current node are cheaper than previous costs
                        currentAdj.setPrevious(current); // set current node as previous for this node
                        currentAdj.setgCosts(current); // set g costs of this node (costs from start to this node)
                    }
                }
            }

            if (openList.isEmpty()) { // no path exists
                return new LinkedList<T>(); // return empty list
            }
        }
        return null; // unreachable
    }*/
}
