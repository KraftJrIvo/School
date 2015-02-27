package com.mygdx.schoolRPG;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.schoolRPG.tools.MultiTile;

/**
 * Created by Kraft on 31.01.2015.
 */
public class BlockMultiTile extends MultiTile {

    public BlockMultiTile(String path, AssetManager assets) {
        super(path, assets, 3, 4);
    }

    public TextureRegion getTile(boolean up, boolean down, boolean left, boolean right) {
        if (up && down && left && right) {
            return getTile(2, 3, false, false);
        }

        if (up && down && left) {
            return getTile(0, 3, true, false);
        }
        if (up && down && right) {
            return getTile(0, 3, false, false);
        }
        if (up && right && left) {
            return getTile(2, 2, false, false);
        }
        if (right && down && left) {
            return getTile(1, 3, false, false);
        }

        if (up && down) {
            return getTile(0, 2, false, false);
        }
        if (left && right) {
            return getTile(1, 2, false, false);
        }

        if (up && left) {
            return getTile(1, 1, true, false);
        }
        if (up && right) {
            return getTile(1, 1, false, false);
        }
        if (left && down) {
            return getTile(0, 1, true, false);
        }
        if (right && down) {
            return getTile(0, 1, false, false);
        }

        if (up) {
            return getTile(2, 1, false, false);
        }
        if (down) {
            return getTile(2, 0, false, false);
        }
        if (left) {
            return getTile(1, 0, true, false);
        }
        if (right) {
            return getTile(1, 0, false, false);
        }
        return getTile(0, 0, false, false);
    }
}
