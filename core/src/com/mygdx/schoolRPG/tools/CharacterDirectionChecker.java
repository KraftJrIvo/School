package com.mygdx.schoolRPG.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.schoolRPG.MovingConfiguration;

/**
 * Created by Kraft on 15.03.2015.
 */
public class CharacterDirectionChecker {
    public enum LookDirection {none, left, right, up, down}
    enum WalkDirection {none, left, right, up, down, left_back, right_back, up_back, down_back}
    public LookDirection lookDir = LookDirection.down;
    public WalkDirection walkDir = WalkDirection.down;
    boolean stand = true;
    boolean keepLookingForward = false;
    boolean keyboardControlled;
    public CharacterDirectionChecker(boolean keyboardControlled) {
        this.keyboardControlled = keyboardControlled;
    }

    public void setLookingForward(boolean b) {
        keepLookingForward = b;
    }

    public boolean update(MovingConfiguration mc) {
        //if (!keyboardControlled) return false;
        boolean look = false;
        WalkDirection newWalkDir = walkDir;
        LookDirection newLookDir = lookDir;

        keepLookingForward = true;
        /*if (Gdx.input.isKeyPressed(Input.Keys.W)) {
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
        }*/

        stand = false;
        if (mc.movingRight > 0 && mc.movingLeft == 0) {
            if (keepLookingForward) newLookDir = LookDirection.right;
            if (newLookDir == LookDirection.left) {
                newWalkDir = WalkDirection.right_back;
            } else {
                newWalkDir = WalkDirection.right;
            }
        } else if (mc.movingRight == 0 && mc.movingLeft > 0) {
            if (keepLookingForward) newLookDir = LookDirection.left;
            if (newLookDir == LookDirection.right) {
                newWalkDir = WalkDirection.left_back;
            } else {
                newWalkDir = WalkDirection.left;
            }
        } else if (mc.movingUp > 0 && mc.movingDown == 0) {
            if (keepLookingForward) newLookDir = LookDirection.up;
            if (newLookDir == LookDirection.down) {
                newWalkDir = WalkDirection.up_back;
            } else {
                newWalkDir = WalkDirection.up;
            }
        } else if (mc.movingUp == 0 && mc.movingDown > 0) {
            if (keepLookingForward) newLookDir = LookDirection.down;
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
