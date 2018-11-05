package com.mygdx.schoolRPG.battleSystem.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.mygdx.schoolRPG.Particle;
import com.mygdx.schoolRPG.ParticleProperties;
import com.mygdx.schoolRPG.World;
import com.mygdx.schoolRPG.battleSystem.Skill;
import com.mygdx.schoolRPG.battleSystem.Unit;
import com.mygdx.schoolRPG.tools.AnimationSequence;
import com.mygdx.schoolRPG.tools.GlobalSequence;

import java.security.Key;
import java.util.ArrayList;

public class UnitsDrawGroup {

    ArrayList<Unit> units;
    ArrayList<ArrayList<Unit>> layers;
    float layerDist;
    float layerCoeff;
    AnimationSequence markerAnimTop, markerAnimBottom;
    public boolean showSelector;
    int selectedRow = 0, selectedRowUnit = 0;

    public Skill curSkill;
    Unit skillTarget;
    Unit skillAttacker;

    ArrayList<Particle> particles;

    public UnitsDrawGroup(World w, ArrayList<Unit> units, float layerDist, float layerCoeff) {
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
        markerAnimTop = new AnimationSequence(new GlobalSequence(w.assets, w.assets.get("battle-select-top-marker.png", Texture.class), 6), 10, true);
        markerAnimBottom = new AnimationSequence(new GlobalSequence(w.assets, w.assets.get("battle-select-marker.png", Texture.class), 17), 20, true);
        particles = new ArrayList<Particle>();
    }

    public void setCurSkill(Skill skill, Unit attacker, Unit target) {
        curSkill = skill;
        skillAttacker = attacker;
        skillTarget = target;
        curSkill.finished = false;
    }

    public Unit getSelectedUnit() {
        return layers.get(selectedRow).get(selectedRowUnit);
    }

    public void addParticle(World w, ParticleProperties props, ParticleProperties.ParticleSpawnProperties sprops, float x, float y, float z) {
        particles.add(new Particle(w.assets, props, sprops, false, x, y, z));
    }

    private void updateParticles(float centerX, float centerY) {
        for (int i = 0; i < particles.size(); ++i) {
            Particle prt = particles.get(i);
            prt.fall();
            if (prt.alpha <= 0) particles.remove(prt);
        }
    }

    private void drawParticlesUpTo(SpriteBatch batch, float from, float upTo) {
        ArrayList<Particle> prtToDraw = new ArrayList<Particle>();
        for (int i = 0; i < particles.size(); ++i) {
            Particle prt = particles.get(i);
            prt.fall();
            if (prt.y <= upTo && prt.y > from) prtToDraw.add(prt);
        }
        for (int i = 0; i < prtToDraw.size(); ++i) {
            Particle prt1 = prtToDraw.get(i);
            for (int j = i + 1; j < prtToDraw.size(); ++j) {
                Particle prt2 = prtToDraw.get(j);
                if (prt1.y < prt2.y) {
                    int id = prtToDraw.indexOf(prt2);
                    prtToDraw.set(prtToDraw.indexOf(prt1), prt2);
                    prtToDraw.set(id, prt1);
                }
            }
        }

        for (int i = 0; i < prtToDraw.size(); ++i) {
            Particle prt = prtToDraw.get(i);
            prt.drawBattle(batch);
        }
    }

    public void draw(World w, SpriteBatch batch, float centerX, float centerY, Unit curUnit) {
        if (showSelector) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                if (selectedRowUnit > 0) selectedRowUnit--;
                else if (selectedRow >= 0) {
                    selectedRow++;
                    selectedRowUnit = 0;
                }
            }
            else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                if (selectedRowUnit < layers.get(selectedRow).size() - 1) selectedRowUnit++;
                else if (selectedRow < layers.size() - 1) {
                    selectedRow++;
                    selectedRowUnit = layers.get(selectedRow).size() - 1;
                }
            }
            else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                if (selectedRow > 0) selectedRow--;
                if (selectedRowUnit >= layers.get(selectedRow).size()) selectedRowUnit = layers.get(selectedRow).size() - 1;
            }
            else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                if (selectedRow < layers.size() - 1) selectedRow++;
                selectedRowUnit %= layers.get(selectedRow).size();
                if (selectedRowUnit >= layers.get(selectedRow).size()) selectedRowUnit = layers.get(selectedRow).size() - 1;
            }
            if (selectedRow < 0) selectedRow = 0;
            if (selectedRow >= layers.size()) selectedRow = layers.size()-1;
            if (selectedRowUnit < 0) selectedRowUnit = 0;
            if (selectedRowUnit >= layers.get(selectedRow).size()) selectedRowUnit = layers.get(selectedRow).size()-1;
        }
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
        float rememberX = 0;
        float rememberScale = 0;
        float rememberI = 0;
        for (int i = layers.size() - 1; i >= 0; --i) {
            if (i == layers.size() - 1) {
                drawParticlesUpTo(batch,globalY + layerDist * (layers.size() - 1) - layerDist / 2.0f, 999);
            } else {
                drawParticlesUpTo(batch,globalY + layerDist * (i) - layerDist / 2.0f, globalY + layerDist * (i + 1) - layerDist / 2.0f);
            }
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
                if (unit == skillTarget || unit == skillAttacker) {
                    rememberX = globalX + curW;
                    rememberScale = scale;
                    rememberI = i;
                }
                float from;
                if (showSelector && i == selectedRow && j == selectedRowUnit) {
                    batch.setColor(new Color(1.0f, 105.0f/255.0f, 82.0f/255.0f, 0.5f));
                    batch.draw(markerAnimBottom.getCurrentFrame(false), globalX + curW + scale * unit.statesIdleTexes.get(0).getFirstFrame().getRegionWidth()/2.0f - 2.0f * markerAnimBottom.getFirstFrame().getRegionWidth() - 18 - 7.5f * (float)Math.sin((double) System.currentTimeMillis()/100), globalY + maxHigh + i * layerDist - unit.statesIdleTexes.get(0).getFirstFrame().getRegionHeight() - 2.0f * markerAnimBottom.getFirstFrame().getRegionHeight() - 3.25f * (float)Math.sin((double) System.currentTimeMillis()/100), 72 + 15 * (float)Math.sin((double) System.currentTimeMillis()/100), 36 + 7.5f * (float)Math.sin((double) System.currentTimeMillis()/100));
                    batch.draw(markerAnimTop.getCurrentFrame(false), globalX + curW + scale * unit.statesIdleTexes.get(0).getFirstFrame().getRegionWidth()/2.0f - 2.0f * markerAnimTop.getFirstFrame().getRegionWidth(), globalY + maxHigh + i * layerDist + 50 - 2.0f * markerAnimTop.getFirstFrame().getRegionHeight() + 10 * (float)Math.sin((double) System.currentTimeMillis()/100), 36, 36);
                    batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                } else if (curUnit == unit) {
                    batch.setColor(new Color(0, 150.0f/255.0f, 173.0f/255.0f, 0.5f));
                    batch.draw(markerAnimBottom.getCurrentFrame(false), globalX + curW + scale * unit.statesIdleTexes.get(0).getFirstFrame().getRegionWidth()/2.0f - 2.0f * markerAnimBottom.getFirstFrame().getRegionWidth() - 18 - 7.5f * (float)Math.sin((double) System.currentTimeMillis()/100), globalY + maxHigh + i * layerDist - unit.statesIdleTexes.get(0).getFirstFrame().getRegionHeight() - 2.0f * markerAnimBottom.getFirstFrame().getRegionHeight() - 3.25f * (float)Math.sin((double) System.currentTimeMillis()/100), 72 + 15 * (float)Math.sin((double) System.currentTimeMillis()/100), 36 + 7.5f * (float)Math.sin((double) System.currentTimeMillis()/100));
                    batch.draw(markerAnimTop.getCurrentFrame(false), globalX + curW + scale * unit.statesIdleTexes.get(0).getFirstFrame().getRegionWidth()/2.0f - 2.0f * markerAnimTop.getFirstFrame().getRegionWidth(), globalY + maxHigh + i * layerDist + 50 - 2.0f * markerAnimTop.getFirstFrame().getRegionHeight() + 10 * (float)Math.sin((double) System.currentTimeMillis()/100), 36, 36);
                    batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                }
                unit.draw(w, this, batch, globalX + curW, globalY + maxHigh + i * layerDist, scale);

                curW += unit.statesIdleTexes.get(0).getFirstFrame().getRegionWidth() * scale;
                if (j < layers.get(i).size() - 1) curW += 8 * scale;
            }
        }
        if (curSkill != null) {
            if (skillAttacker != null) {
                curSkill.draw(w, batch, true, rememberX + rememberScale * skillAttacker.statesIdleTexes.get(0).getFirstFrame().getRegionWidth()/2.0f, globalY + maxHigh + rememberI * layerDist - rememberScale * skillAttacker.statesIdleTexes.get(0).getFirstFrame().getRegionHeight()/2.0f, skillAttacker, rememberScale);
            } else if (skillTarget != null) {
                curSkill.draw(w, batch, false, rememberX + rememberScale * skillTarget.statesIdleTexes.get(0).getFirstFrame().getRegionWidth()/2.0f, globalY + maxHigh + rememberI * layerDist - rememberScale * skillTarget.statesIdleTexes.get(0).getFirstFrame().getRegionHeight(), skillTarget, rememberScale);
            }
            if (curSkill.finished) {
                curSkill.turnUpdate();
                curSkill = null;
                skillTarget = null;
                skillAttacker = null;
            }
        }
        drawParticlesUpTo(batch,-999, globalY - layerDist / 2.0f);
        updateParticles(centerX, centerY);
    }

}
