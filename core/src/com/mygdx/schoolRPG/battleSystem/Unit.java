package com.mygdx.schoolRPG.battleSystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.schoolRPG.Item;
import com.mygdx.schoolRPG.ObjectLoader;
import com.mygdx.schoolRPG.ParticleProperties;
import com.mygdx.schoolRPG.World;
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

public class Unit {
    UnitStats stats;
    ArrayList<Skill> skills;
    ArrayList<Item> inventory;
    ArrayList<ArrayList<Item>> dropGroups;
    ArrayList<Float> dropGroupsProbs;
    ArrayList<ArrayList<Float>> dropProbs;
    ArrayList<ArrayList<Integer>> dropMins;
    ArrayList<ArrayList<Integer>> dropMaxs;
    Item heldItem;
    int damageStatesNum;
    ArrayList<Succeptibility> succeptibilities;
    ArrayList<StatusEffect> effects;

    int currentState;
    ArrayList<String> statesConditions;
    public ArrayList<AnimationSequence> statesIdleTexes;
    ArrayList<AnimationSequence> statesHitTexes;
    ArrayList<Sound> statesIdleLoops;
    ArrayList<Sound> statesHitSounds;
    ArrayList<ArrayList<ParticleProperties.ParticleSpawnProperties>> statesIdlePrt;
    ArrayList<ArrayList<ParticleProperties.ParticleSpawnProperties>> statesHitPrt;

    AnimationSequence fighterTex;

    ArrayList<ArrayList<String>> startSpeeches;
    ArrayList<ArrayList<String>> roundSpeeches;
    ArrayList<ArrayList<String>> hitSpeeches;

    String name;

    public Unit(World w, String name, int level) {
        this.name = name;
        stats = new UnitStats(w, name, level);
        FileHandle statsXML = Gdx.files.internal(w.worldDir + "/units/" + name + "/stats.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        org.w3c.dom.Document doc = null;
        try {
            doc = dBuilder.parse(statsXML.read());
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doc.getDocumentElement().normalize();

        skills = new ArrayList<Skill>();
        NodeList skillList = doc.getElementsByTagName("skill");
        for (int i = 0; i < skillList.getLength(); ++i) {
            Node nNode = skillList.item(i);
            Element eElement = (Element) nNode;
            float probability = Float.parseFloat(eElement.getAttribute("prob"));
            if (Math.random() < probability) {
                skills.add(w.battleSystem.getSkillByName(eElement.getAttribute("name")));
            }
        }

        inventory = new ArrayList<Item>();
        NodeList itemList = doc.getElementsByTagName("item");
        for (int i = 0; i < itemList.getLength(); ++i) {
            Node nNode = itemList.item(i);
            Element eElement = (Element) nNode;
            float probability = Float.parseFloat(eElement.getAttribute("prob"));
            if (Math.random() < probability) {
                inventory.add(new Item(w.assets, w.worldDir.path(), eElement.getAttribute("name")));
            }
        }

        String heldItemName = ((Element)doc.getElementsByTagName("held-item").item(0)).getAttribute("name");
        if (!heldItemName.equals("")) heldItem = new Item(w.assets, w.worldDir.path(), heldItemName);

        dropGroups = new ArrayList<ArrayList<Item>>();
        dropGroupsProbs = new ArrayList<Float>();
        dropProbs = new ArrayList<ArrayList<Float>>();
        dropMins = new ArrayList<ArrayList<Integer>>();
        dropMaxs = new ArrayList<ArrayList<Integer>>();
        NodeList dgList = doc.getElementsByTagName("dropGroup");
        for (int i = 0; i < dgList.getLength(); ++i) {
            Node nNode = dgList.item(i);
            dropGroups.add(new ArrayList<Item>());
            dropProbs.add(new ArrayList<Float>());
            dropMins.add(new ArrayList<Integer>());
            dropMaxs.add(new ArrayList<Integer>());
            Element eElement = (Element) nNode;
            dropGroupsProbs.add(Float.parseFloat(eElement.getAttribute("prob")));
            NodeList dropList = eElement.getElementsByTagName("drop");
            for (int j = 0; j < dropList.getLength(); ++j) {
                Node nNode2 = dropList.item(j);
                Element eElement2 = (Element) nNode2;
                //dropGroups.get(i).add(new Item(w.assets, w.worldDir.path(), eElement2.getAttribute("name")));
                dropProbs.get(i).add(Float.parseFloat(eElement2.getAttribute("prob")));
                dropMins.get(i).add(Integer.parseInt(eElement2.getAttribute("min")));
                dropMaxs.get(i).add(Integer.parseInt(eElement2.getAttribute("max")));
            }
        }

        statesConditions = new ArrayList<String>();
        statesIdleTexes = new ArrayList<AnimationSequence>();
        statesHitTexes = new ArrayList<AnimationSequence>();
        statesIdleLoops = new ArrayList<Sound>();
        statesHitSounds = new ArrayList<Sound>();
        statesIdlePrt = new ArrayList<ArrayList<ParticleProperties.ParticleSpawnProperties>>();
        statesHitPrt = new ArrayList<ArrayList<ParticleProperties.ParticleSpawnProperties>>();
        NodeList statesList = doc.getElementsByTagName("state");
        for (int i = 0; i < statesList.getLength() + 1; ++i) {
            Node nNode;
            if (i == statesList.getLength()) {
                nNode = doc.getElementsByTagName("death-state").item(0);
            } else {
                nNode = statesList.item(i);
            }
            Element eElement = (Element) nNode;
            statesConditions.add(eElement.getAttribute("condition"));
            statesIdleTexes.add(null);
            statesHitTexes.add(null);
            statesIdleLoops.add(null);
            statesHitSounds.add(null);
            statesIdlePrt.add(new ArrayList<ParticleProperties.ParticleSpawnProperties>());
            statesHitPrt.add(new ArrayList<ParticleProperties.ParticleSpawnProperties>());
            Element idle = (Element) eElement.getElementsByTagName("idle").item(0);
            Element hit = (Element) eElement.getElementsByTagName("hit").item(0);
            /*statesIdleTexes.set(i, new AnimationSequence(w.assets, w.assets.get(w.worldDir + "/units/" + name + "/" + idle.getAttribute("tex") + ".png", Texture.class), Integer.parseInt(idle.getAttribute("animFps")), Boolean.parseBoolean(idle.getAttribute("animLoop")), Integer.parseInt(idle.getAttribute("animFps"))));
            statesHitTexes.set(i, new AnimationSequence(w.assets, w.assets.get(w.worldDir + "/units/" + name + "/" + hit.getAttribute("tex") + ".png", Texture.class), Integer.parseInt(hit.getAttribute("animFps")), Boolean.parseBoolean(hit.getAttribute("animLoop")), Integer.parseInt(hit.getAttribute("animFps"))));
            if (!idle.getAttribute("soundLoop").equals("")) {
                statesIdleLoops.set(i, w.assets.get(w.worldDir + "/units/" + name + "/" + idle.getAttribute("soundLoop"), Sound.class));
            }
            if (!hit.getAttribute("sound").equals("")) {
                statesHitSounds.set(i, w.assets.get(w.worldDir + "/units/" + name + "/" + hit.getAttribute("sound"), Sound.class));
            }*/
            if (idle != null) {
                NodeList particlesSpawns = idle.getElementsByTagName("particle");
                for (int j = 0; j < particlesSpawns.getLength(); ++j) {
                    Element spawnParams = (Element)particlesSpawns.item(j);
                    statesIdlePrt.get(i).add(new ParticleProperties().new ParticleSpawnProperties(
                            w.assets,
                            w.worldDir + "/units/" + name + "/" + spawnParams.getAttribute("spawnSound"),
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
            if (hit != null) {
                NodeList particlesSpawns = hit.getElementsByTagName("particle");
                for (int j = 0; j < particlesSpawns.getLength(); ++j) {
                    Element spawnParams = (Element) particlesSpawns.item(j);
                    statesHitPrt.get(i).add(new ParticleProperties().new ParticleSpawnProperties(
                            w.assets,
                            w.worldDir + "/units/" + name + "/" + spawnParams.getAttribute("spawnSound"),
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
        damageStatesNum = statesIdleTexes.size();
        currentState = 0;

        //Element fighterSpriteElement = (Element) doc.getElementsByTagName("fighter").item(0);
        //fighterTex = new AnimationSequence(w.assets, w.assets.get(w.worldDir + "/units/" + name + "/" + fighterSpriteElement.getAttribute("tex") + ".png", Texture.class), Integer.parseInt(fighterSpriteElement.getAttribute("animFps")), Boolean.parseBoolean(fighterSpriteElement.getAttribute("animLoop")), Integer.parseInt(fighterSpriteElement.getAttribute("animFps")));

        startSpeeches = new ArrayList<ArrayList<String>>();
        NodeList startSpeechesNodes = ((Element)doc.getElementsByTagName("start-speech").item(0)).getElementsByTagName("speech");
        for (int i = 0; i < startSpeechesNodes.getLength(); ++i) {
            Element ss = (Element)startSpeechesNodes.item(i);
            startSpeeches.add(new ArrayList<String>());
            startSpeeches.get(i).add(ss.getAttribute("eng"));
            startSpeeches.get(i).add(ss.getAttribute("rus"));
        }
        roundSpeeches = new ArrayList<ArrayList<String>>();
        NodeList roundSpeechesNodes = ((Element)doc.getElementsByTagName("round-speech").item(0)).getElementsByTagName("speech");
        for (int i = 0; i < roundSpeechesNodes.getLength(); ++i) {
            Element rs = (Element)roundSpeechesNodes.item(i);
            roundSpeeches.add(new ArrayList<String>());
            roundSpeeches.get(i).add(rs.getAttribute("eng"));
            roundSpeeches.get(i).add(rs.getAttribute("rus"));
        }
        hitSpeeches = new ArrayList<ArrayList<String>>();
        NodeList hitSpeechesNodes = ((Element)doc.getElementsByTagName("hit-speech").item(0)).getElementsByTagName("speech");
        for (int i = 0; i < hitSpeechesNodes.getLength(); ++i) {
            Element hs = (Element)hitSpeechesNodes.item(i);
            hitSpeeches.add(new ArrayList<String>());
            hitSpeeches.get(i).add(hs.getAttribute("eng"));
            hitSpeeches.get(i).add(hs.getAttribute("rus"));
        }

        succeptibilities = new ArrayList<Succeptibility>();
        NodeList succList = doc.getElementsByTagName("susceptibility");
        for (int i = 0; i < succList.getLength(); ++i) {
            Element succ = (Element) succList.item(i);
            succeptibilities.add(new Succeptibility(w, w.battleSystem.getDamageTypeByName(succ.getAttribute("name")), Integer.parseInt(succ.getAttribute("percent"))));
        }

        effects = new ArrayList<StatusEffect>();
    }

    public void load(World w) {
        FileHandle statsXML = Gdx.files.internal(w.worldDir + "/units/" + name + "/stats.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        org.w3c.dom.Document doc = null;
        try {
            doc = dBuilder.parse(statsXML.read());
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doc.getDocumentElement().normalize();
        NodeList statesList = doc.getElementsByTagName("state");
        for (int i = 0; i < statesList.getLength() + 1; ++i) {
            Node nNode;
            if (i == statesList.getLength()) {
                nNode = doc.getElementsByTagName("death-state").item(0);
            } else {
                nNode = statesList.item(i);
            }
            Element eElement = (Element) nNode;
            Element idle = (Element) eElement.getElementsByTagName("idle").item(0);
            Element hit = (Element) eElement.getElementsByTagName("hit").item(0);
            if (idle != null) w.assets.load(w.worldDir + "/units/" + name + "/" + idle.getAttribute("tex") + ".png", Texture.class);
            if (hit != null) w.assets.load(w.worldDir + "/units/" + name + "/" + hit.getAttribute("tex") + ".png", Texture.class);
            if (idle != null && !idle.getAttribute("soundLoop").equals("")) {
                w.assets.load(w.worldDir + "/units/" + name + "/" + idle.getAttribute("soundLoop"), Sound.class);
            }
            if (hit != null && !hit.getAttribute("sound").equals("")) {
                w.assets.load(w.worldDir + "/units/" + name + "/" + hit.getAttribute("sound"), Sound.class);
            }
        }
        Element fighterSpriteElement = (Element) doc.getElementsByTagName("fighter").item(0);
        w.assets.load(w.worldDir + "/units/" + name + "/" + fighterSpriteElement.getAttribute("tex") + ".png", Texture.class);

        NodeList dgList = doc.getElementsByTagName("dropGroup");
        ObjectLoader loader = new ObjectLoader();
        for (int i = 0; i < dgList.getLength(); ++i) {
            Node nNode = dgList.item(i);
            Element eElement = (Element) nNode;
            NodeList dropList = eElement.getElementsByTagName("drop");
            for (int j = 0; j < dropList.getLength(); ++j) {
                Node nNode2 = dropList.item(j);
                Element eElement2 = (Element) nNode2;
                loader.loadItem(w.assets, w, eElement2.getAttribute("name"));
            }
        }
    }

    public void initializeResources(World w) {
        FileHandle statsXML = Gdx.files.internal(w.worldDir + "/units/" + name + "/stats.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        org.w3c.dom.Document doc = null;
        try {
            doc = dBuilder.parse(statsXML.read());
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doc.getDocumentElement().normalize();
        NodeList statesList = doc.getElementsByTagName("state");
        for (int i = 0; i < statesList.getLength() + 1; ++i) {
            Node nNode;
            if (i == statesList.getLength()) {
                nNode = doc.getElementsByTagName("death-state").item(0);
            } else {
                nNode = statesList.item(i);
            }
            Element eElement = (Element) nNode;
            Element idle = (Element) eElement.getElementsByTagName("idle").item(0);
            Element hit = (Element) eElement.getElementsByTagName("hit").item(0);
            if (idle != null) statesIdleTexes.set(i, new AnimationSequence(w.assets, w.assets.get(w.worldDir + "/units/" + name + "/" + idle.getAttribute("tex") + ".png", Texture.class), Integer.parseInt(idle.getAttribute("animFps")), Boolean.parseBoolean(idle.getAttribute("animLoop")), Integer.parseInt(idle.getAttribute("animFrames"))));
            if (hit != null) statesHitTexes.set(i, new AnimationSequence(w.assets, w.assets.get(w.worldDir + "/units/" + name + "/" + hit.getAttribute("tex") + ".png", Texture.class), Integer.parseInt(hit.getAttribute("animFps")), Boolean.parseBoolean(hit.getAttribute("animLoop")), Integer.parseInt(hit.getAttribute("animFrames"))));
            if (idle != null && !idle.getAttribute("soundLoop").equals("")) {
                statesIdleLoops.set(i, w.assets.get(w.worldDir + "/units/" + name + "/" + idle.getAttribute("soundLoop"), Sound.class));
            }
            if (hit != null && !hit.getAttribute("sound").equals("")) {
                statesHitSounds.set(i, w.assets.get(w.worldDir + "/units/" + name + "/" + hit.getAttribute("sound"), Sound.class));
            }
        }
        Element fighterSpriteElement = (Element) doc.getElementsByTagName("fighter").item(0);
        fighterTex = new AnimationSequence(w.assets, w.assets.get(w.worldDir + "/units/" + name + "/" + fighterSpriteElement.getAttribute("tex") + ".png", Texture.class), Integer.parseInt(fighterSpriteElement.getAttribute("animFps")), Boolean.parseBoolean(fighterSpriteElement.getAttribute("animLoop")), Integer.parseInt(fighterSpriteElement.getAttribute("animFrames")));

        NodeList dgList = doc.getElementsByTagName("dropGroup");
        ObjectLoader loader = new ObjectLoader();
        for (int i = 0; i < dgList.getLength(); ++i) {
            Node nNode = dgList.item(i);
            Element eElement = (Element) nNode;
            NodeList dropList = eElement.getElementsByTagName("drop");
            for (int j = 0; j < dropList.getLength(); ++j) {
                Node nNode2 = dropList.item(j);
                Element eElement2 = (Element) nNode2;
                dropGroups.get(i).add(new Item(w.assets, w.worldDir.path(), eElement2.getAttribute("name")));
            }
        }

    }



    public void draw(SpriteBatch batch, float x, float y, float scale) {
        TextureRegion tex = statesIdleTexes.get(currentState).getCurrentFrame(false);
        batch.draw(tex, x, y - tex.getRegionHeight(), tex.getRegionWidth()/2.0f, tex.getRegionHeight()/2.0f, tex.getRegionWidth(), tex.getRegionHeight(), scale, scale, 0);
    }

}
