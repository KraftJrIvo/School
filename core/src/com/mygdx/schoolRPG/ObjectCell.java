package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.XmlReader;
import com.mygdx.schoolRPG.tools.AnimationSequence;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Kraft on 11.01.2016.
 */
enum ObjectType {
    NONE,
    PLAYER,
    SOLID,
    NONSOLID,
    OBSTACLE,
    PARTICLE,
    CHECKPOINT,
    LIQUID,
    NPC
}

public class ObjectCell {
    ObjectType type;
    float h;
    int id;
    Entity entity;
    float x, y;
    float width, height;
    float entityX, entityY;
    int cellOffsetX, cellOffsetY;
    boolean transfer;
    public boolean hIsY;
    private long startTime;
    private long lastSpawned;

    public int radius;
    public boolean isObject = false;
    public int statesCount = 0;
    public int currentState = -1;
    public int offsetX = 0;
    public int offsetY = 0;
    public ArrayList<Boolean> statesSwitchables;
    public ArrayList<String> statesConditionFlags;
    public ArrayList<Boolean> statesConditionFlagVals;
    public ArrayList<String> statesTexTypes;
    public ArrayList<String> statesTex;
    public ArrayList<String> statesFlags;
    public ArrayList<Boolean> statesFlagVals;
    public ArrayList<Integer> statesParticles;
    public ArrayList<ArrayList<Float>> statesParticlesCoords;
    public ArrayList<Integer> statesIntervals;
    public ArrayList<Boolean> statesHidePlayer;
    public ArrayList<Integer> statesFPS;
    public ArrayList<Boolean> statesLooping;
    public ArrayList<Integer> statesFramesCount;

    public ObjectCell(float width, float height, Entity entity, ObjectType type, int id, boolean hIsY) {
        this.type = type;
        this.id = id;
        this.entity = entity;
        this.width = width;
        this.height = height;
        entityX = entity.x % width;
        entityY = entity.h % height;
        transfer = false;
        cellOffsetX = 0;
        cellOffsetY = 0;
        this.hIsY = hIsY;
    }

    public boolean isTransfer() {
        return transfer;
    }

    public void reset() {
        x+=cellOffsetX;
        y+=cellOffsetY;
        entityX = entity.x % width;
        if (hIsY) {
            entityY = entity.h % height;
        } else {
            entityY = entity.y % height;
        }
        transfer = false;
        cellOffsetX = 0;
        cellOffsetY = 0;
    }

    public void invalidate() {
        cellOffsetX = (int)Math.floor(entityX / width);
        cellOffsetY = (int)Math.floor(entityY / height);
        if (cellOffsetX == 0 && cellOffsetY == 0) {
            entityX = entity.x - (x-1) * width;
            if (hIsY) {
                entityY = entity.h - (y-1) * height;
            } else {
                entityY = entity.y - (y-1) * height;
            }
        } else {
            transfer = true;
        }
        h = entity.h;

        invalidateState();
    }

    private void invalidateState() {
        if (!isObject) return;
    }

    public void activate(String worldDir, AssetManager assets, ArrayList<String> flagNames, ArrayList<Boolean> flags, Area area) {
        if (statesSwitchables.get(currentState) && (flagNames.contains(statesConditionFlags.get(currentState))
                && statesConditionFlagVals.get(currentState) != flags.get(flagNames.indexOf(statesConditionFlags.get(currentState))))) return;
        currentState++;
        currentState = currentState % statesCount;
        if (statesTexTypes.get(currentState).equals("sprite")) {
            entity.anim = null;
            entity.tex = assets.get(worldDir + "/" + statesTex.get(currentState) + ".png", Texture.class);
        } else if (statesTexTypes.get(currentState).equals("anim")) {
            entity.tex = null;
            entity.anim = new AnimationSequence(assets, worldDir + "/anim/" + statesTex.get(currentState) + ".png", statesFPS.get(currentState), statesLooping.get(currentState), statesFramesCount.get(currentState));
        }
        for (int i = 0; i < flagNames.size(); ++i) {
            if (flagNames.get(i).equals(statesFlags.get(currentState))) {
                flags.set(i, statesFlagVals.get(currentState));
            }
        }
        area.playerHidden = statesHidePlayer.get(currentState);
        startTime = System.currentTimeMillis();
        lastSpawned = 0;
    }

    public float getParticleCoord(int n) {
        return statesParticlesCoords.get(currentState).get(n);
    }

    public void checkFlags(String worldDir, AssetManager assets, ArrayList<String> flagNames, ArrayList<Boolean> flags, Area area) {
        if ((flagNames.contains(statesConditionFlags.get(currentState)) && statesConditionFlagVals.get(currentState) == flags.get(flagNames.indexOf(statesConditionFlags.get(currentState))))) {
            activate(worldDir, assets, flagNames, flags, area);
        }
    }

    public boolean isSwitchable() {
        return statesSwitchables.get(currentState);
    }

    public boolean checkParticleEmission() {
        if (statesParticles.get(currentState) < 0) return false;
        boolean res = (System.currentTimeMillis() - startTime - lastSpawned) > statesIntervals.get(currentState);
        if (res) {
            lastSpawned = System.currentTimeMillis() - startTime;
        }
        return res;
    }

    public void objectCheck(String worldDir, AssetManager assets, int state) {
        if (entity.texPath == null) return;
        String baseTex = entity.texPath.substring(entity.texPath.lastIndexOf("/") + 1, entity.texPath.length() - 4);;

        FileHandle charDir =  Gdx.files.internal(worldDir + "/objects");
        FileHandle objectXML = null;
        for (FileHandle entry: charDir.list()) {
            if (entry.nameWithoutExtension().equals(baseTex)) {
                isObject = true;
                objectXML = entry;
            }
        }
        if (isObject) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = null;
            try {
                dBuilder = dbFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            org.w3c.dom.Document doc = null;
            try {
                doc = dBuilder.parse(objectXML.file());
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            doc.getDocumentElement().normalize();
            statesCount = Integer.parseInt(doc.getDocumentElement().getAttribute("statesCount"));
            currentState = state;
            if (currentState == -1) {
                currentState = Integer.parseInt(doc.getDocumentElement().getAttribute("startState"));
            }
            radius = Integer.parseInt(doc.getDocumentElement().getAttribute("radius"));
            offsetX = Integer.parseInt(doc.getDocumentElement().getAttribute("offsetX"));
            offsetY = Integer.parseInt(doc.getDocumentElement().getAttribute("offsetY"));
            NodeList nList = doc.getElementsByTagName("state");
            statesSwitchables = new ArrayList<Boolean>();
            statesConditionFlags = new ArrayList<String>();
            statesConditionFlagVals = new ArrayList<Boolean>();
            statesTexTypes = new ArrayList<String>();
            statesTex = new ArrayList<String>();
            statesFlags = new ArrayList<String>();
            statesFlagVals = new ArrayList<Boolean>();
            statesParticles = new ArrayList<Integer>();
            statesParticlesCoords = new ArrayList<ArrayList<Float>>();
            statesIntervals = new ArrayList<Integer>();
            statesHidePlayer = new ArrayList<Boolean>();
            statesFPS = new ArrayList<Integer>();
            statesLooping = new ArrayList<Boolean>();
            statesFramesCount = new ArrayList<Integer>();
            for (int i= 0; i< nList.getLength(); ++i) {
                Node nNode = nList.item(i);
                Element eElement = (Element) nNode;
                statesSwitchables.add(Boolean.parseBoolean(eElement.getAttribute("switchable")));
                String condition = eElement.getAttribute("switchCondition");
                if (condition.length() > 0 && condition.charAt(0) == '!') {
                    condition = condition.substring(1);
                    statesConditionFlagVals.add(false);
                } else {
                    statesConditionFlagVals.add(true);
                }
                statesConditionFlags.add(condition);
                statesTex.add(eElement.getAttribute("tex"));
                statesTexTypes.add(eElement.getAttribute("texType"));
                statesFlags.add(eElement.getAttribute("flagName"));
                statesFlagVals.add(Boolean.parseBoolean(eElement.getAttribute("flagVal")));
                statesParticles.add(Integer.parseInt(eElement.getAttribute("emitsParticle")));
                statesParticlesCoords.add(new ArrayList<Float>());
                statesParticlesCoords.get(statesParticlesCoords.size() - 1).add(Float.parseFloat(eElement.getAttribute("emitX")));
                statesParticlesCoords.get(statesParticlesCoords.size() - 1).add(Float.parseFloat(eElement.getAttribute("emitY")));
                statesParticlesCoords.get(statesParticlesCoords.size() - 1).add(Float.parseFloat(eElement.getAttribute("emitZ")));
                statesIntervals.add(Integer.parseInt(eElement.getAttribute("emitInterval")));
                statesHidePlayer.add(Boolean.parseBoolean(eElement.getAttribute("hidePlayer")));
                statesFPS.add(Integer.parseInt(eElement.getAttribute("fps")));
                statesLooping.add(Boolean.parseBoolean(eElement.getAttribute("looping")));
                statesFramesCount.add(Integer.parseInt(eElement.getAttribute("framesCount")));
            }
            if (statesTexTypes.get(currentState) == "sprite") {
                entity.anim = null;
                entity.tex = assets.get(worldDir + "/" + statesTex.get(currentState) + ".png", Texture.class);
            } else if (statesTexTypes.get(currentState) == "anim") {
                entity.tex = null;
                entity.anim = new AnimationSequence(assets, worldDir + "/anim/" + statesTex.get(currentState) + ".png", statesFPS.get(currentState), statesLooping.get(currentState), statesFramesCount.get(currentState));
            }
            startTime = System.currentTimeMillis();
            lastSpawned = 0;
        }
    }
}
