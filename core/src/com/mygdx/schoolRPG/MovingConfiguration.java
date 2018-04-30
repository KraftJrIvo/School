package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.schoolRPG.menus.GameMenu;

/**
 * Created by Kraft on 01.02.2017.
 */
public class MovingConfiguration{
    public int movingLeft;
    public int movingRight;
    public int movingUp;
    public int movingDown;
    public int sprint;
    public int jump;
    public int use;

    public MovingConfiguration() {
        movingLeft = 0;
        movingRight = 0;
        movingUp = 0;
        movingDown = 0;
        sprint = 0;
        jump = 0;
        use = 0;
    }

    public boolean allZeroesPlatform() {
        return movingLeft == 0 && movingRight == 0 && jump == 0;
    }

    private int getKeyState(int key) {
        if (Gdx.input.isKeyJustPressed(key)) return 1;
        else if (Gdx.input.isKeyPressed(key)) return 2;
        else return 0;
    }

    private int getKeyStateAndroid(int minX, int minY, int maxX, int maxY) {
        if (Gdx.input.getX() >= minX && Gdx.input.getY() >= minY && Gdx.input.getX() <= maxX && Gdx.input.getY() <= maxY) {
            if (Gdx.input.justTouched()) return 1;
            if (Gdx.input.isTouched()) return 2;
        }
        return 0;
    }

    public void updateMoving(int leftKey, int rightKey, int upKey, int downKey, int sprintKey, int jumpKey, int useKey) {
        movingLeft = getKeyState(leftKey);
        movingRight = getKeyState(rightKey);
        movingUp = getKeyState(upKey);
        movingDown = getKeyState(downKey);
        sprint = getKeyState(sprintKey);
        jump = getKeyState(jumpKey);
        use = getKeyState(useKey);
    }

    public void updateMovingAndroid(GameMenu menu) {
        float offX = (float)menu.rightGameJoy.joyOffsetX();
        float offY = (float)menu.rightGameJoy.joyOffsetY();
        if (offX > 0.5f) {
            if (movingRight == 1)movingRight = 2;
            else movingRight = 1;
            movingLeft = 0;
        } else if (offX < -0.5f) {
            if (movingLeft == 1)movingLeft = 2;
            else movingLeft = 1;
            movingRight = 0;
        } else {
            movingRight = 0;
            movingLeft = 0;
        }
        if (offY > 0.5f) {
            if (movingUp == 1)movingUp = 2;
            else movingUp = 1;
            movingDown = 0;
        } else if (offY < -0.5f) {
            if (movingDown == 1)movingDown = 2;
            else movingDown = 1;
            movingUp = 0;
        } else {
            movingUp = 0;
            movingDown = 0;
        }
        jump = getKeyStateAndroid(0, 0, 900, 300);
        use = getKeyStateAndroid(300, 300, 600, 600);
    }
}