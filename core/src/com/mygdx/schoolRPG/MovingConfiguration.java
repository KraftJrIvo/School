package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

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

    public void updateMoving(int leftKey, int rightKey, int upKey, int downKey, int sprintKey, int jumpKey, int useKey) {
        movingLeft = getKeyState(leftKey);
        movingRight = getKeyState(rightKey);
        movingUp = getKeyState(upKey);
        movingDown = getKeyState(downKey);
        sprint = getKeyState(sprintKey);
        jump = getKeyState(jumpKey);
        use = getKeyState(useKey);
    }
}