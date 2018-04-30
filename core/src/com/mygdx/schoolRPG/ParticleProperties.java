package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.schoolRPG.menus.GameMenu;
import com.mygdx.schoolRPG.tools.AnimationSequence;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by Kraft on 16.07.2015.
 */
public class ParticleProperties {
    public enum MovePattern{
        NONE,
        NORMAL,
        RANDOM
    }
    public enum ChangeConditionType{
        NONE,
        BOUNCE,
        TIMER,
        LOOPS,
        RANDOM,
        RANDOM_BOUNCE
    }
    public class StateChangeCondition{
        ChangeConditionType conditionType;
        int conditionParam;
        int nextStateId;
        public StateChangeCondition(String str) {
            String[] words = str.split("-");
            if (words.length < 3) {
                conditionType = ChangeConditionType.NONE;
                conditionParam = -1;
                nextStateId = -1;
            } else {
                if (words[0].equals("BOUNCE")) conditionType = ChangeConditionType.BOUNCE;
                else if (words[0].equals("TIMER")) conditionType = ChangeConditionType.TIMER;
                else if (words[0].equals("LOOPS")) conditionType = ChangeConditionType.LOOPS;
                else if (words[0].equals("RANDOM")) conditionType = ChangeConditionType.RANDOM;
                else if (words[0].equals("RBOUNCE")) conditionType = ChangeConditionType.RANDOM_BOUNCE;
                else conditionType = ChangeConditionType.NONE;
                this.conditionParam = Integer.parseInt(words[1]);
                this.nextStateId = Integer.parseInt(words[2]);
            }
        }
    }
    public class ParticleSpawnProperties{
        String particleName;
        float spawnX;
        float spawnY;
        float spawnZ;
        float spawnDir;
        float spawnSpeed;
        float spawnImpulse;
        float dirSpread;
        float speedSpread;
        float impulseSpread;
        Sound spawnSound;
        public ParticleSpawnProperties(AssetManager assets, String spawnSoundPath, String particleName, float spawnX, float spawnY, float spawnZ, float spawnDir, float spawnSpeed, float spawnImpulse, float dirSpread, float speedSpread, float impulseSpread) {
            this.particleName = particleName;
            this.spawnX = spawnX;
            this.spawnY = spawnY;
            this.spawnZ = spawnZ;
            this.spawnDir = spawnDir;
            this.spawnSpeed = spawnSpeed;
            this.spawnImpulse = spawnImpulse;
            this.dirSpread = dirSpread;
            this.speedSpread = speedSpread;
            this.impulseSpread = impulseSpread;
            if (assets.isLoaded(spawnSoundPath)) spawnSound = assets.get(spawnSoundPath, Sound.class);
        }
        public ParticleSpawnProperties(String particleName, float spawnX, float spawnY, float spawnZ) {
            this.particleName = particleName;
            this.spawnX = spawnX;
            this.spawnY = spawnY;
            this.spawnZ = spawnZ;
            this.spawnDir = (float)Math.random() * 6.28f;
            this.spawnSpeed = (float)Math.random() * 3.0f;
            this.spawnImpulse = (float)Math.random() * 3.0f;
            this.dirSpread = 0;
            this.speedSpread = 0;
            this.impulseSpread = 0;
        }
    }
    public float h=0, floorHeight=0, r = 1;
    GameMenu menu;
    ArrayList<Texture> statesTexes;
    ArrayList<AnimationSequence> statesAnims;
    ArrayList<Integer> statesIds;
    ArrayList<String> statesTexNames;
    ArrayList<Integer> statesFramesNumbers;
    ArrayList<Float> statesWeights;
    ArrayList<Float> statesBounciness;
    ArrayList<Float> statesInertia;
    ArrayList<Float> statesAlphaSteps;
    ArrayList<MovePattern> statesMovePatterns;
    ArrayList<Boolean> statesFloors;
    ArrayList<Integer> statesCollisionGroups;
    ArrayList<Integer> statesSpawnIntervals;
    ArrayList<Integer> statesSpawnsCounts;
    ArrayList<Sound> statesFlySoundLoops;
    ArrayList<Sound> statesBounceSounds;
    ArrayList<String> statesFlySoundLoopsNames;
    ArrayList<String> statesBounceSoundsNames;
    ArrayList<ArrayList<StateChangeCondition>> statesChangeConditions;
    ArrayList<ArrayList<ParticleSpawnProperties>> statesparticleSpawns;

    public ParticleProperties() {
    }

    public ParticleProperties(AssetManager assets, ParticleProperties pp) {
        h=0;
        floorHeight=0;
        r = pp.r;
        statesTexes = pp.statesTexes;
        statesAnims = pp.statesAnims;
        statesIds = pp.statesIds;
        statesTexNames = pp.statesTexNames;
        statesFramesNumbers = pp.statesFramesNumbers;
        statesWeights = pp.statesWeights;
        statesBounciness = pp.statesBounciness;
        statesInertia = pp.statesInertia;
        statesAlphaSteps = pp.statesAlphaSteps;
        statesMovePatterns = pp.statesMovePatterns;
        statesFloors = pp.statesFloors;
        statesCollisionGroups = pp.statesCollisionGroups;
        statesSpawnIntervals = pp.statesSpawnIntervals;
        statesSpawnsCounts = pp.statesSpawnsCounts;
        statesFlySoundLoops = pp.statesFlySoundLoops;
        statesBounceSounds = pp.statesBounceSounds;
        statesFlySoundLoopsNames = pp.statesFlySoundLoopsNames;
        statesBounceSoundsNames = pp.statesBounceSoundsNames;
        statesChangeConditions = pp.statesChangeConditions;
        statesparticleSpawns = pp.statesparticleSpawns;
    }

    public ParticleProperties(World world, AssetManager assets, String path, GameMenu menu) {
        this.menu = menu;
        h=0;
        floorHeight=0;
        r = 4;
        statesTexes = new ArrayList<Texture>();
        statesAnims = new ArrayList<AnimationSequence>();
        statesIds = new ArrayList<Integer>();
        statesTexNames = new ArrayList<String>();
        statesFramesNumbers = new ArrayList<Integer>();
        statesWeights = new ArrayList<Float>();
        statesBounciness = new ArrayList<Float>();
        statesInertia = new ArrayList<Float>();
        statesAlphaSteps = new ArrayList<Float>();
        statesMovePatterns = new ArrayList<MovePattern>();
        statesFloors = new ArrayList<Boolean>();
        statesCollisionGroups = new ArrayList<Integer>();
        statesSpawnIntervals = new ArrayList<Integer>();
        statesSpawnsCounts = new ArrayList<Integer>();
        statesFlySoundLoops = new ArrayList<Sound>();
        statesBounceSounds = new ArrayList<Sound>();
        statesFlySoundLoopsNames = new ArrayList<String>();
        statesBounceSoundsNames = new ArrayList<String>();
        statesChangeConditions = new ArrayList<ArrayList<StateChangeCondition>>();
        statesparticleSpawns = new ArrayList<ArrayList<ParticleSpawnProperties>>();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        org.w3c.dom.Document doc = null;
        FileHandle xmlFile =  Gdx.files.internal(path);
        try {
            doc = dBuilder.parse(xmlFile.read());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.xml.sax.SAXException e) {
            e.printStackTrace();
        }
        doc.getDocumentElement().normalize();
        Element mainElement = (Element)doc.getElementsByTagName("particle-properties").item(0);
        r = Float.parseFloat(mainElement.getAttribute("radius"));
        NodeList states = doc.getElementsByTagName("state");
        for (int i =0; i < states.getLength(); ++i) {
            Element state = (Element) states.item(i);
            statesIds.add(Integer.parseInt(state.getAttribute("id")));
            String str = state.getAttribute("tex");
            if (str.equals("")) {
                statesTexNames.add(statesTexNames.get(statesTexNames.size()-1));
            } else {
                statesTexNames.add(str);
            }
            str = state.getAttribute("frames");
            if (str.equals("")) {
                statesFramesNumbers.add(1);
            } else {
                statesFramesNumbers.add(Integer.parseInt(str));
            }
            if (statesFramesNumbers.get(statesFramesNumbers.size()-1) == 1) {
                statesTexes.add(assets.get(path.replace("stats.xml", statesTexNames.get(statesTexNames.size()-1) + ".png"), Texture.class));
                statesAnims.add(null);
            } else {
                statesAnims.add(new AnimationSequence(assets, path.replace("stats.xml", statesTexNames.get(statesTexNames.size()-1) + ".png"), Integer.parseInt(state.getAttribute("fps")), Boolean.parseBoolean(state.getAttribute("looping")), statesFramesNumbers.get(statesFramesNumbers.size()-1)));
                statesTexes.add(null);
            }
            str = state.getAttribute("weight");
            if (str.equals("")) {
                if (statesWeights.size() == 0) {
                    statesWeights.add(0f);
                } else {
                    statesWeights.add(statesWeights.get(statesWeights.size()-1));
                }
            } else {
                statesWeights.add(Float.parseFloat(str));
            }
            str = state.getAttribute("bounciness");
            if (str.equals("")) {
                if (statesBounciness.size() == 0) {
                    statesBounciness.add(0f);
                } else{
                    statesBounciness.add(statesBounciness.get(statesBounciness.size()-1));
                }
            } else {
                statesBounciness.add(Float.parseFloat(str));
            }
            str = state.getAttribute("inertia");
            if (str.equals("")) {
                if (statesInertia.size() == 0) {
                    statesInertia.add(0f);
                } else {
                    statesInertia.add(statesInertia.get(statesInertia.size()-1));
                }
            } else {
                statesInertia.add(Float.parseFloat(str));
            }
            str = state.getAttribute("alphaStep");
            if (str.equals("")) {
                statesAlphaSteps.add(0f);
            } else {
                statesAlphaSteps.add(Float.parseFloat(str));
            }
            MovePattern mp = MovePattern.NONE;
            str = state.getAttribute("movePattern");
            if (str.equals("NORMAL")) {
                mp = MovePattern.NORMAL;
            } else if (str.equals("RANDOM")) {
                mp = MovePattern.RANDOM;
            }
            statesMovePatterns.add(mp);
            str = state.getAttribute("floor");
            if (str.equals("")) {
                statesFloors.add(false);
            } else {
                statesFloors.add(Boolean.parseBoolean(str));
            }
            str = state.getAttribute("collisionGroup");
            if (str.equals("")) {
                if (statesCollisionGroups.size() == 0) {
                    statesCollisionGroups.add(statesCollisionGroups.get(statesCollisionGroups.size()-1));
                } else {
                    statesCollisionGroups.add(0);
                }
            } else {
                statesCollisionGroups.add(Integer.parseInt(str));
            }
            str = state.getAttribute("spawnInterval");
            if (str.equals("")) {
                statesSpawnIntervals.add(0);
            } else {
                statesSpawnIntervals.add(Integer.parseInt(str));
            }
            str = state.getAttribute("spawnCount");
            if (str.equals("")) {
                statesSpawnsCounts.add(0);
            } else {
                statesSpawnsCounts.add(Integer.parseInt(str));
            }
            statesFlySoundLoopsNames.add(state.getAttribute("flySoundLoop"));
            if (statesFlySoundLoopsNames.get(statesFlySoundLoopsNames.size()-1).equals("")) {
                statesFlySoundLoops.add(null);
            } else {
                statesFlySoundLoops.add(assets.get(world.worldDir.path() + "/sounds/" + statesFlySoundLoopsNames.get(statesFlySoundLoopsNames.size()-1), Sound.class));
            }
            statesBounceSoundsNames.add(state.getAttribute("bounceSound"));
            if (statesBounceSoundsNames.get(statesBounceSoundsNames.size()-1).equals("")) {
                statesBounceSounds.add(null);
            } else {
                statesBounceSounds.add(assets.get(world.worldDir.path() + "/sounds/" + statesBounceSoundsNames.get(statesBounceSoundsNames.size()-1), Sound.class));
            }
            String[] changeConditions = state.getAttribute("nextStateCondition").split(";");
            statesChangeConditions.add(new ArrayList<StateChangeCondition>());
            for (int j = 0; j < changeConditions.length; ++j) {
                statesChangeConditions.get(statesChangeConditions.size()-1).add(new StateChangeCondition(changeConditions[j]));
            }
            NodeList childParticlesSpawns = state.getElementsByTagName("child-particle");
            statesparticleSpawns.add(new ArrayList<ParticleSpawnProperties>());
            for (int j = 0; j < childParticlesSpawns.getLength(); ++j) {
                Element spawnParams = (Element)childParticlesSpawns.item(j);
                statesparticleSpawns.get(statesparticleSpawns.size()-1).add(new ParticleSpawnProperties(
                        assets,
                        world.worldDir.path() + "/sounds/" + spawnParams.getAttribute("spawnSound"),
                        spawnParams.getAttribute("name"),
                        Integer.parseInt(spawnParams.getAttribute("spawnX")),
                        Integer.parseInt(spawnParams.getAttribute("spawnY")),
                        Integer.parseInt(spawnParams.getAttribute("spawnZ")),
                        Float.parseFloat(spawnParams.getAttribute("spawnDir")),
                        Float.parseFloat(spawnParams.getAttribute("spawnSpeed")),
                        Float.parseFloat(spawnParams.getAttribute("spawnImpulse")),
                        Float.parseFloat(spawnParams.getAttribute("dirSpread")),
                        Float.parseFloat(spawnParams.getAttribute("speedSpread")),
                        Float.parseFloat(spawnParams.getAttribute("impulseSpread"))
                ));
            }
        }

    }
}