package com.mygdx.schoolRPG;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.schoolRPG.tools.MultiTile;

/**
 * Created by Kraft on 31.01.2015.
 */
public class PlayerMultiTile extends MultiTile {

    enum PlayerPose {
        LEFT, RIGHT, FRONT, BACK, JUMP, JUMP_LEFT, JUMP_RIGHT
    }

    public PlayerMultiTile(String path, AssetManager assets) {
        super(path, assets, 4, 3);
    }

    public TextureRegion getTile(PlayerPose playerPose) {
        if (playerPose == PlayerPose.LEFT) {
            return getTile(1, 2, false, false);
        }
        if (playerPose == PlayerPose.RIGHT) {
            return getTile(1, 0, false, false);
        }
        if (playerPose == PlayerPose.FRONT) {
            return getTile(0, 0, false, false);
        }
        if (playerPose == PlayerPose.BACK) {
            return getTile(0, 1, false, false);
        }
        if (playerPose == PlayerPose.JUMP) {
            return getTile(1, 1, false, false);
        }
        if (playerPose == PlayerPose.JUMP_RIGHT) {
            return getTile(0, 2, false, false);
        }
        if (playerPose == PlayerPose.JUMP_LEFT) {
            return getTile(0, 2, true, false);
        }
        return getTile(0, 0, false, false);
    }
}
