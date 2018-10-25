package com.mygdx.schoolRPG.battleSystem.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.schoolRPG.Particle;
import com.mygdx.schoolRPG.battleSystem.Unit;

import java.util.ArrayList;

public class UnitsDrawGroup {

    ArrayList<Unit> units;
    ArrayList<ArrayList<Unit>> layers;
    float layerDist;
    float layerCoeff;

    public UnitsDrawGroup(ArrayList<Unit> units, float layerDist, float layerCoeff) {
        this.units = units;
        layers = new ArrayList<ArrayList<Unit>>();
        this.layerCoeff = layerCoeff;
        this.layerDist = layerDist;
        int layersCount = 0;
        int placesCount = 0;
        for (layersCount = 0; placesCount < units.size(); ++layersCount){
            placesCount += layersCount + 1;
            layers.add(new ArrayList<Unit>());
        }
        ArrayList<Boolean> unitsPlaced = new ArrayList<Boolean>();
        for (int i = 0; i < units.size(); ++i) {
            unitsPlaced.add(false);
        }
        boolean allPlaced = false;
        for (int i = 0; i < layers.size(); ++i) {
            while (layers.get(i).size() < (i + 1) && !allPlaced) {
                float maxHigh = 0;
                int maxHighId = -1;
                for (int j = 0; j < units.size(); ++j) {
                    if (!unitsPlaced.get(j) && units.get(j).statesIdleTexes.get(0).getFirstFrame().getRegionHeight() > maxHigh) {
                        maxHigh = units.get(j).statesIdleTexes.get(0).getFirstFrame().getRegionHeight();
                        maxHighId = j;
                    }
                }
                if (maxHighId == -1) {
                    allPlaced = true;
                } else {
                    layers.get(i).add(units.get(maxHighId));
                    unitsPlaced.set(maxHighId, true);
                }
            }
        }
    }

    public void draw(SpriteBatch batch, float centerX, float centerY, ArrayList<Particle> particles) {
        float height = 0;
        float maxHigh = 0;
        if (layers.size() > 0) {
            for (int j = 0; j < layers.get(0).size(); ++j) {
                float scale = (float)Math.pow(layerCoeff, layers.size() - 1);
                float h = layers.get(0).get(j).statesIdleTexes.get(0).getFirstFrame().getRegionHeight() * scale;
                if (h > maxHigh) {
                    maxHigh = h;
                }
            }
            height = maxHigh + layerDist * layers.size() - 1;
        }
        float globalY = centerY - height / 2.0f;
        for (int i = layers.size() - 1; i >= 0; --i) {
            float curW = 0;
            float scale = 2 * (float)Math.pow(layerCoeff, i);
            for (int j = 0; j < layers.get(i).size(); ++j) {
                Unit unit = layers.get(i).get(j);
                curW += unit.statesIdleTexes.get(0).getFirstFrame().getRegionWidth() * scale;
                if (j < layers.get(i).size() - 1) curW += 8 * scale;
            }
            float globalX = centerX - curW / 2.0f;
            curW = 0;
            for (int j = 0; j < layers.get(i).size(); ++j) {
                Unit unit = layers.get(i).get(j);
                unit.draw(batch, globalX + curW, globalY + maxHigh + i * layerDist, scale);
                curW += unit.statesIdleTexes.get(0).getFirstFrame().getRegionWidth() * scale;
                if (j < layers.get(i).size() - 1) curW += 8 * scale;
            }
        }
    }

}
