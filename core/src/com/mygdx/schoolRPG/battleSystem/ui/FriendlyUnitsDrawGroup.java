package com.mygdx.schoolRPG.battleSystem.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.schoolRPG.battleSystem.Unit;

import java.util.ArrayList;

public class FriendlyUnitsDrawGroup {

    ArrayList<Unit> units;
    int curUnitId = 0;
    boolean startedGoingDown;
    boolean startedGoingUp;
    boolean ready;
    float shownPersent = 1.0f;
    float moveSpeed = 0;
    int nextId = 0;

    public FriendlyUnitsDrawGroup(ArrayList<Unit> units) {
        this.units = units;
    }

    public void changeUnit(int id) {
        ready = false;
        if (curUnitId == 0) {
            moveSpeed = 10.0f;
            startedGoingUp = true;
        } else {
            startedGoingDown = true;
            startedGoingUp = false;
        }
        nextId = id;
    }

    public void draw(SpriteBatch batch)
    {
        //units.get(curUnitId).drawFriend(batch, shownPersent);
        if (startedGoingDown) {
            shownPersent -= moveSpeed;
            moveSpeed += 0.1f;
            if (shownPersent <= 0) {
                startedGoingUp = true;
                startedGoingDown = false;
                curUnitId = nextId;
            }
        } else if (startedGoingUp) {
            shownPersent += (1.0f - shownPersent) / 10.0f;
            if (shownPersent >= 0.95f) {
                startedGoingUp = false;
                ready = true;
            }
        }
    }
}
