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
import com.mygdx.schoolRPG.battleSystem.Battle;
import com.mygdx.schoolRPG.battleSystem.Skill;
import com.mygdx.schoolRPG.battleSystem.Unit;
import com.mygdx.schoolRPG.tools.AnimationSequence;
import com.mygdx.schoolRPG.tools.GlobalSequence;
import org.omg.CORBA.BAD_CONTEXT;

import java.security.Key;
import java.util.ArrayList;

public class UnitsDrawGroup {

    ArrayList<Unit> units;
    ArrayList<ArrayList<Unit>> layers;
    public float layerDist;
    float layerCoeff;
    AnimationSequence markerAnimTop, markerAnimBottom;
    public boolean showSelector;
    int selectedRow = 0, selectedRowUnit = 0;

    public Skill curSkill;
    Unit skillTarget;
    Unit skillAttacker;

    ArrayList<Particle> particles;

    public Battle battle;

    public boolean skillPositive = false;

    public UnitsDrawGroup(Battle battle, World w, ArrayList<Unit> units, float layerDist, float layerCoeff) {
        this.units = units;
        this.battle = battle;
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

    public void addParticle(Particle p) {
        particles.add(p);
    }

    private void updateParticles() {
        for (int i = 0; i < particles.size(); ++i) {
            Particle prt = particles.get(i);
            prt.fall();
            if (prt.alpha <= 0) particles.remove(prt);
        }
    }

    private void drawParticleShadows(World w, SpriteBatch batch) {
        for (int i = 0; i < particles.size(); ++i) {
            Particle prt = particles.get(i);
            if (prt.text.length() == 0 &&  (prt.floor || prt.pp.statesMovePatterns.get(prt.curStateId) == ParticleProperties.MovePattern.NONE)) continue;
            float ww = 16.0f + prt.z / 2.0f;
            batch.setColor(new Color(1.0f, 1.0f, 1.0f, prt.alpha * (0.45f-prt.z/50)));
            batch.draw(w.assets.get("prt_shadow.png", Texture.class), prt.x - ww/2, prt.y + ww/2, ww, ww);
            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        }
    }

    private void drawParticlesUpTo(SpriteBatch batch, float from, float upTo) {
        ArrayList<Particle> prtToDraw = new ArrayList<Particle>();
        for (int i = 0; i < particles.size(); ++i) {
            Particle prt = particles.get(i);
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
        drawParticleShadows(w, batch);
        if (layers.get(selectedRow).get(selectedRowUnit).stats.dead) {
            boolean foundNotDead = false;
            for (int i = 0; i < layers.size(); ++i) {
                for (int j = 0; j < layers.get(i).size(); ++j) {
                    if (!layers.get(i).get(j).stats.dead) {
                        foundNotDead = true;
                        selectedRow = i;
                        selectedRowUnit = j;
                        break;
                    }
                }
                if (foundNotDead) break;
            }
            if (!foundNotDead) showSelector = false;
        }
        if (showSelector) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                if (selectedRowUnit > 0) {
                    int curRowUnit = selectedRowUnit;
                    int firstNotDead = selectedRowUnit;
                    //curRowUnit--;
                    while (curRowUnit > 0) {
                        if (!layers.get(selectedRow).get(curRowUnit-1).stats.dead) {
                            firstNotDead = curRowUnit-1;
                            break;
                        }
                        curRowUnit--;
                    }
                    selectedRowUnit = firstNotDead;
                }
                else if (selectedRow < layers.size() - 1 && !layers.get(selectedRow+1).get(0).stats.dead) {
                    selectedRow++;
                    selectedRowUnit = 0;
                }
            }
            else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                if (selectedRowUnit < layers.get(selectedRow).size() - 1) {
                    int curRowUnit = selectedRowUnit;
                    int firstNotDead = selectedRowUnit;
                    //curRowUnit++;
                    while (curRowUnit < layers.get(selectedRow).size()-1) {
                        if (!layers.get(selectedRow).get(curRowUnit+1).stats.dead) {
                            firstNotDead = curRowUnit+1;
                            break;
                        }
                        curRowUnit++;
                    }
                    selectedRowUnit = firstNotDead;
                }
                else if (selectedRow < layers.size() - 1 && !layers.get(selectedRow+1).get(layers.get(selectedRow+1).size() - 1).stats.dead) {
                    selectedRow++;
                    selectedRowUnit = layers.get(selectedRow).size() - 1;
                }
            }
            else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                if (selectedRow > 0) {
                    int curRow = selectedRow;
                    int firstNotDead = selectedRow;
                    //curRow--;
                    //if (selectedRowUnit >= layers.get(curRow).size()) selectedRowUnit = layers.get(curRow).size() - 1;
                    while (curRow > 0) {
                        if (!layers.get(curRow-1).get(Math.min(selectedRowUnit, layers.get(curRow-1).size()-1)).stats.dead) {
                            firstNotDead = curRow-1;
                            break;
                        }
                        curRow--;
                        if (selectedRowUnit >= layers.get(curRow).size()) selectedRowUnit = layers.get(curRow).size() - 1;
                    }
                    selectedRow = firstNotDead;
                }
            }
            else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                if (selectedRow < layers.size()) {
                    int curRow = selectedRow;
                    int firstNotDead = selectedRow;
                    //curRow++;
                    //if (selectedRowUnit >= layers.get(curRow).size()) selectedRowUnit = layers.get(curRow).size() - 1;
                    while (curRow < layers.size() - 1) {
                        if (!layers.get(curRow+1).get(selectedRowUnit).stats.dead) {
                            firstNotDead = curRow+1;
                            break;
                        }
                        curRow++;
                        if (selectedRowUnit >= layers.get(curRow).size()) selectedRowUnit = layers.get(curRow).size() - 1;
                    }
                    selectedRow = firstNotDead;
                }
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
        float rememberXAttacker = 0;
        float rememberScaleAttacker = 0;
        float rememberIAttacker = 0;
        float rememberXTarget = 0;
        float rememberScaleTarget = 0;
        float rememberITarget = 0;
        for (int i = layers.size() - 1; i >= 0; --i) {
            if (i == layers.size() - 1) {
                drawParticlesUpTo(batch,globalY + layerDist * (layers.size() - 1) - layerDist, 999);
            } else {
                drawParticlesUpTo(batch,globalY + layerDist * (i) - layerDist, globalY + layerDist * (i + 1) - layerDist);
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
                if (unit == skillTarget) {
                    rememberXTarget = globalX + curW;
                    rememberScaleTarget = scale;
                    rememberITarget = i;
                }
                if (unit == skillAttacker) {
                    rememberXAttacker = globalX + curW;
                    rememberScaleAttacker = scale;
                    rememberIAttacker = i;
                }
                float from;
                if (showSelector && i == selectedRow && j == selectedRowUnit) {
                    if (skillPositive) batch.setColor(new Color(0f, 1f, 0.443f, 0.5f));
                    else batch.setColor(new Color(1.0f, 105.0f/255.0f, 82.0f/255.0f, 0.5f));
                    batch.draw(markerAnimBottom.getCurrentFrame(false), globalX + curW + scale * unit.statesIdleTexes.get(0).getFirstFrame().getRegionWidth()/2.0f - 2.0f * markerAnimBottom.getFirstFrame().getRegionWidth() - 18 - 7.5f * (float)Math.sin((double) System.currentTimeMillis()/100), globalY + maxHigh + i * layerDist - unit.statesIdleTexes.get(0).getFirstFrame().getRegionHeight() - 2.0f * markerAnimBottom.getFirstFrame().getRegionHeight() - 3.25f * (float)Math.sin((double) System.currentTimeMillis()/100), 72 + 15 * (float)Math.sin((double) System.currentTimeMillis()/100), 36 + 7.5f * (float)Math.sin((double) System.currentTimeMillis()/100));
                    batch.draw(markerAnimTop.getCurrentFrame(false), globalX + curW + scale * unit.statesIdleTexes.get(0).getFirstFrame().getRegionWidth()/2.0f - 2.0f * markerAnimTop.getFirstFrame().getRegionWidth(), globalY + maxHigh + i * layerDist + 50 - 2.0f * markerAnimTop.getFirstFrame().getRegionHeight() + 10 * (float)Math.sin((double) System.currentTimeMillis()/100), 36, 36);
                    batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                } else if (curUnit == unit) {
                    batch.setColor(new Color(0, 150.0f/255.0f, 173.0f/255.0f, 0.5f));
                    batch.draw(markerAnimBottom.getCurrentFrame(false), globalX + curW + scale * unit.statesIdleTexes.get(0).getFirstFrame().getRegionWidth()/2.0f - 2.0f * markerAnimBottom.getFirstFrame().getRegionWidth() - 18 - 7.5f * (float)Math.sin((double) System.currentTimeMillis()/100), globalY + maxHigh + i * layerDist - unit.statesIdleTexes.get(0).getFirstFrame().getRegionHeight() - 2.0f * markerAnimBottom.getFirstFrame().getRegionHeight() - 3.25f * (float)Math.sin((double) System.currentTimeMillis()/100), 72 + 15 * (float)Math.sin((double) System.currentTimeMillis()/100), 36 + 7.5f * (float)Math.sin((double) System.currentTimeMillis()/100));
                    batch.draw(markerAnimTop.getCurrentFrame(false), globalX + curW + scale * unit.statesIdleTexes.get(0).getFirstFrame().getRegionWidth()/2.0f - 2.0f * markerAnimTop.getFirstFrame().getRegionWidth(), globalY + maxHigh + i * layerDist + 50 - 2.0f * markerAnimTop.getFirstFrame().getRegionHeight() + 10 * (float)Math.sin((double) System.currentTimeMillis()/100), 36, 36);
                    batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                }
                if (curSkill != null) {
                    if (skillTarget == unit) {
                        curSkill.predraw(w, this, batch, skillAttacker, skillTarget, false, globalX + curW + scale * skillTarget.statesIdleTexes.get(0).getFirstFrame().getRegionWidth() / 2.0f, globalY + maxHigh + i * layerDist - scale * skillTarget.statesIdleTexes.get(0).getFirstFrame().getRegionHeight() / 2.0f, scale);
                    } else if (skillAttacker == unit) {
                        curSkill.predraw(w, this, batch, skillAttacker, skillAttacker, true, globalX + curW + scale * skillAttacker.statesIdleTexes.get(0).getFirstFrame().getRegionWidth() / 2.0f, globalY + maxHigh + i * layerDist - scale * skillAttacker.statesIdleTexes.get(0).getFirstFrame().getRegionHeight() / 2.0f, scale);
                    }
                }
                unit.draw(w, this, batch, globalX + curW, globalY + maxHigh + i * layerDist, scale);
                if (curSkill != null) {
                    if (skillTarget == unit) {
                        curSkill.afterdraw(w, this, batch, skillAttacker, skillTarget, false, globalX + curW + scale * skillTarget.statesIdleTexes.get(0).getFirstFrame().getRegionWidth() / 2.0f, globalY + maxHigh + i * layerDist - scale * skillTarget.statesIdleTexes.get(0).getFirstFrame().getRegionHeight() / 2.0f, scale);
                    } else if (skillAttacker == unit) {
                        curSkill.afterdraw(w, this, batch, skillAttacker, skillAttacker, true, globalX + curW + scale * skillAttacker.statesIdleTexes.get(0).getFirstFrame().getRegionWidth() / 2.0f, globalY + maxHigh + i * layerDist - scale * skillAttacker.statesIdleTexes.get(0).getFirstFrame().getRegionHeight() / 2.0f, scale);
                    }
                }
                curW += unit.statesIdleTexes.get(0).getFirstFrame().getRegionWidth() * scale;
                if (j < layers.get(i).size() - 1) curW += 8 * scale;
            }
        }
        if (curSkill != null) {
            if (skillTarget != null) {
                curSkill.draw(w, this, batch, skillAttacker, skillTarget, false, rememberXTarget + rememberScaleTarget * skillTarget.statesIdleTexes.get(0).getFirstFrame().getRegionWidth()/2.0f, globalY + maxHigh + rememberITarget * layerDist - rememberScaleTarget * skillTarget.statesIdleTexes.get(0).getFirstFrame().getRegionHeight(), rememberScaleTarget);
            }
            if (skillAttacker != null) {
                curSkill.draw(w, this, batch, skillAttacker, skillAttacker, true, rememberXAttacker + rememberScaleAttacker * skillAttacker.statesIdleTexes.get(0).getFirstFrame().getRegionWidth()/2.0f, globalY + maxHigh + rememberIAttacker * layerDist - rememberScaleAttacker * skillAttacker.statesIdleTexes.get(0).getFirstFrame().getRegionHeight()/2.0f, rememberScaleAttacker);
            }
            if (curSkill.finished) {
                curSkill.turnUpdate();
                curSkill = null;
                skillTarget = null;
                skillAttacker = null;
            }
        }
        drawParticlesUpTo(batch,-999, globalY - layerDist);
        updateParticles();
        //if (units.size() == 3) addParticle(new Particle(w.assets, "" + (int)(Math.random()*10), new Color((float) Math.random(), (float)Math.random(),(float)Math.random(), 1.0f), false, Gdx.input.getX(), Gdx.input.getY(), 1.0f));
        //if (units.size() == 3) addParticle(w, w.getParticleByName(units.get(0).statesHitPrt.get(0).get(0).particleName), units.get(0).statesHitPrt.get(0).get(0), Gdx.input.getX(), Gdx.input.getY(), 0);
    }

}
