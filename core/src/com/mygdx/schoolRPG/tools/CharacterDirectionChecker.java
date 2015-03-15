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

    public CharacterDirectionChecker() {
    }

    public void update() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            lookDir = LookDirection.up;
            walkDir = WalkDirection.up;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            lookDir = LookDirection.down;
            walkDir = WalkDirection.down;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            lookDir = LookDirection.right;
            walkDir = WalkDirection.right;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            lookDir = LookDirection.left;
            walkDir = WalkDirection.left;
        } else if (lookDir == LookDirection.none) {
            lookDir = LookDirection.down;
        }

        stand = false;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (lookDir == LookDirection.down) {
                walkDir = WalkDirection.up_back;
            } else {
                walkDir = WalkDirection.up;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (lookDir == LookDirection.up) {
                walkDir = WalkDirection.down_back;
            } else {
                walkDir = WalkDirection.down;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (lookDir == LookDirection.left) {
                walkDir = WalkDirection.right_back;
            } else {
                walkDir = WalkDirection.right;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (lookDir == LookDirection.right) {
                walkDir = WalkDirection.left_back;
            } else {
                walkDir = WalkDirection.left;
            }
        } else if (walkDir == WalkDirection.none) {
            walkDir = WalkDirection.down;
        } else {
            if (lookDir == LookDirection.left) {
                walkDir = WalkDirection.left;
            } else if (lookDir == LookDirection.right) {
                walkDir = WalkDirection.right;
            } else if (lookDir == LookDirection.up) {
                walkDir = WalkDirection.up;
            } else if (lookDir == LookDirection.down) {
                walkDir = WalkDirection.down;
            }
            stand = true;
        }

    }

}
