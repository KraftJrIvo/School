package com.mygdx.schoolRPG.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Created by Kraft on 15.03.2015.
 */
public class CharacterDirectionChecker {
    public enum LookDirection {none, left, right, up, down}
    enum WalkDirection {none, left, right, up, down, left_back, right_back, up_back, down_back}
    public LookDirection lookDir = LookDirection.none;
    public WalkDirection walkDir = WalkDirection.none;
    boolean stand = true;
    boolean keepLookingForward = false;

    public CharacterDirectionChecker() {
    }

    public void setLookingForward(boolean b) {
        keepLookingForward = b;
    }

    public boolean update() {

        boolean look = true;
        WalkDirection newWalkDir = walkDir;
        LookDirection newLookDir = lookDir;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            newLookDir = LookDirection.up;
            newWalkDir = WalkDirection.up;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            newLookDir = LookDirection.down;
            newWalkDir = WalkDirection.down;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            newLookDir = LookDirection.right;
            newWalkDir = WalkDirection.right;
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            newLookDir = LookDirection.left;
            newWalkDir = WalkDirection.left;
        } else if (newLookDir == LookDirection.none) {
            newLookDir = LookDirection.down;
        } else {
            look = false;
        }

        stand = false;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (keepLookingForward && !look) newLookDir = LookDirection.right;
            if (newLookDir == LookDirection.left) {
                newWalkDir = WalkDirection.right_back;
            } else {
                newWalkDir = WalkDirection.right;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (keepLookingForward && !look) newLookDir = LookDirection.left;
            if (newLookDir == LookDirection.right) {
                newWalkDir = WalkDirection.left_back;
            } else {
                newWalkDir = WalkDirection.left;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (keepLookingForward && !look) newLookDir = LookDirection.up;
            if (newLookDir == LookDirection.down) {
                newWalkDir = WalkDirection.up_back;
            } else {
                newWalkDir = WalkDirection.up;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && !Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (keepLookingForward && !look) newLookDir = LookDirection.down;
            if (newLookDir == LookDirection.up) {
                newWalkDir = WalkDirection.down_back;
            } else {
                newWalkDir = WalkDirection.down;
            }
        }  else if (newWalkDir == WalkDirection.none) {
            newWalkDir = WalkDirection.down;
        } else {
            if (newLookDir == LookDirection.left) {
                newWalkDir = WalkDirection.left;
            } else if (newLookDir == LookDirection.right) {
                newWalkDir = WalkDirection.right;
            } else if (newLookDir == LookDirection.up) {
                newWalkDir = WalkDirection.up;
            } else if (newLookDir == LookDirection.down) {
                newWalkDir = WalkDirection.down;
            }
            stand = true;
        }
        boolean ret = (walkDir != newWalkDir || lookDir != newLookDir || stand);

        walkDir = newWalkDir;
        lookDir = newLookDir;

        return ret;
    }

}
