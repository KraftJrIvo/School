package com.mygdx.schoolRPG.battleSystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.schoolRPG.*;
import com.mygdx.schoolRPG.battleSystem.ui.UnitsDrawGroup;
import com.mygdx.schoolRPG.tools.AnimationSequence;
import com.mygdx.schoolRPG.tools.ConditionParser;
import javafx.util.Pair;
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
    public UnitStats stats;
    ArrayList<Skill> skills;
    public ArrayList<Item> inventory;
    ArrayList<ArrayList<Item>> dropGroups;
    ArrayList<Float> dropGroupsProbs;
    ArrayList<ArrayList<Float>> dropProbs;
    ArrayList<ArrayList<Integer>> dropMins;
    ArrayList<ArrayList<Integer>> dropMaxs;
    Item heldItem;
    int damageStatesNum;
    ArrayList<Susceptibility> susceptibilities;
    ArrayList<StatusEffect> effects;

    int currentState;
    ArrayList<String> statesConditions;
    public ArrayList<AnimationSequence> statesIdleTexes;
    ArrayList<AnimationSequence> statesHitTexes;
    ArrayList<Sound> statesIdleLoops;
    ArrayList<Sound> statesHitSounds;
    ArrayList<ArrayList<ParticleProperties.ParticleSpawnProperties>> statesIdlePrt;
    public ArrayList<ArrayList<ParticleProperties.ParticleSpawnProperties>> statesHitPrt;
    ArrayList<ArrayList<Pair<Integer, Integer>>> statesIdlePrtMinMax;
    ArrayList<ArrayList<Pair<Integer, Integer>>> statesHitPrtMinMax;

    AnimationSequence fighterTex;

    ArrayList<ArrayList<String>> startSpeeches;
    ArrayList<ArrayList<String>> roundSpeeches;
    ArrayList<ArrayList<String>> hitSpeeches;

    int baseHeight;

    ArrayList<Integer> hitDamages;
    ArrayList<DamageType> hitDTs;
    public ArrayList<Item> drops;
    boolean wasHit = false;
    boolean wasHealed = false;
    float hitAlphaHit = 0;
    float hitAlphaHeal = 0;
    float hitOffset = 0;
    float hitOffsetSpeed = 0;
    float hitShake = 0f;

    ObjectLoader loader;

    String name;
    ArrayList<String> nickname;

    ConditionParser parser;

    public Unit(World w, String name, int level) {
        this.name = name;
        loader = new ObjectLoader();
        stats = new UnitStats(w, name, level);

        parser = new ConditionParser(w.npcs, null, 0);
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


        Element unitElem = (Element) doc.getElementsByTagName("unit").item(0);
        baseHeight = Integer.parseInt(unitElem.getAttribute("baseHeight"));

        Element nameElem = (Element) doc.getElementsByTagName("name").item(0);
        nickname = new ArrayList<String>();
        nickname.add(nameElem.getAttribute("eng"));
        nickname.add(nameElem.getAttribute("rus"));

        skills = new ArrayList<Skill>();
        NodeList skillList = doc.getElementsByTagName("skill");
        for (int i = 0; i < skillList.getLength(); ++i) {
            Node nNode = skillList.item(i);
            Element eElement = (Element) nNode;
            float probability = Float.parseFloat(eElement.getAttribute("prob"));
            if (Math.random() < probability) {
                skills.add(new Skill(w.battleSystem.getSkillByName(eElement.getAttribute("name"))));
            }
        }

        inventory = new ArrayList<Item>();
        NodeList itemList = doc.getElementsByTagName("item");
        for (int i = 0; i < itemList.getLength(); ++i) {
            Node nNode = itemList.item(i);
            Element eElement = (Element) nNode;
            float probability = Float.parseFloat(eElement.getAttribute("prob"));
            if (Math.random() < probability) {
                inventory.add(new Item(w, w.worldDir.path(), eElement.getAttribute("name")));
            }
        }

        String heldItemName = ((Element)doc.getElementsByTagName("held-item").item(0)).getAttribute("name");
        if (!heldItemName.equals("")) heldItem = new Item(w, w.worldDir.path(), heldItemName);

        dropGroups = new ArrayList<ArrayList<Item>>();
        dropGroupsProbs = new ArrayList<Float>();
        dropProbs = new ArrayList<ArrayList<Float>>();
        dropMins = new ArrayList<ArrayList<Integer>>();
        dropMaxs = new ArrayList<ArrayList<Integer>>();
        drops = new ArrayList<Item>();
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
        statesIdlePrtMinMax = new ArrayList<ArrayList<Pair<Integer, Integer>>>();
        statesHitPrtMinMax = new ArrayList<ArrayList<Pair<Integer, Integer>>>();
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
            statesIdlePrtMinMax.add(new ArrayList<Pair<Integer, Integer>>());
            statesHitPrtMinMax.add(new ArrayList<Pair<Integer, Integer>>());
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
            NodeList particlesSpawns = null;
            if (idle != null) {
                particlesSpawns = idle.getElementsByTagName("particle");
                for (int j = 0; j < particlesSpawns.getLength(); ++j) {
                    Element spawnParams = (Element) particlesSpawns.item(j);
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
                    statesIdlePrtMinMax.get(i).add(new Pair<Integer, Integer>(Integer.parseInt(spawnParams.getAttribute("minNum")), Integer.parseInt(spawnParams.getAttribute("maxNum"))));
                    loader.loadParticle(w, spawnParams.getAttribute("name"));
                }
            }
            if (hit != null) {
                particlesSpawns = hit.getElementsByTagName("particle");
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
                    statesHitPrtMinMax.get(i).add(new Pair<Integer, Integer>(Integer.parseInt(spawnParams.getAttribute("minNum")), Integer.parseInt(spawnParams.getAttribute("maxNum"))));
                    loader.loadParticle(w, spawnParams.getAttribute("name"));
                }
            }
            if (hit == null && idle == null) {
                particlesSpawns = eElement.getElementsByTagName("particle");
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
                    statesHitPrtMinMax.get(i).add(new Pair<Integer, Integer>(Integer.parseInt(spawnParams.getAttribute("minNum")), Integer.parseInt(spawnParams.getAttribute("maxNum"))));
                    loader.loadParticle(w, spawnParams.getAttribute("name"));
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

        susceptibilities = new ArrayList<Susceptibility>();
        NodeList succList = doc.getElementsByTagName("susceptibility");
        for (int i = 0; i < succList.getLength(); ++i) {
            Element succ = (Element) succList.item(i);
            susceptibilities.add(new Susceptibility(w, w.battleSystem.getDamageTypeByName(succ.getAttribute("damageType" +
                    "")), Float.parseFloat(succ.getAttribute("percent"))));
        }

        effects = new ArrayList<StatusEffect>();
        hitDamages = new ArrayList<Integer>();
        hitDTs = new ArrayList<DamageType>();
    }

    public void playHitSound(World w) {
        statesHitSounds.get(currentState).play(w.menu.soundVolume / 100.0f);
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
            if (idle == null && hit == null) w.assets.load(w.worldDir + "/units/" + name + "/" + eElement.getAttribute("tex") + ".png", Texture.class);
            if (idle != null && !idle.getAttribute("soundLoop").equals("")) {
                w.assets.load(w.worldDir + "/units/" + name + "/" + idle.getAttribute("soundLoop"), Sound.class);
            }
            if (hit != null && !hit.getAttribute("sound").equals("")) {
                w.assets.load(w.worldDir + "/units/" + name + "/" + hit.getAttribute("sound"), Sound.class);
            }
            if (hit == null && idle == null) {
                w.assets.load(w.worldDir + "/units/" + name + "/" + eElement.getAttribute("sound"), Sound.class);
            }
        }
        Element fighterSpriteElement = (Element) doc.getElementsByTagName("fighter").item(0);
        w.assets.load(w.worldDir + "/units/" + name + "/" + fighterSpriteElement.getAttribute("tex") + ".png", Texture.class);

        NodeList dgList = doc.getElementsByTagName("dropGroup");
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
            if (idle == null && hit == null) {
                AnimationSequence deadAnim = new AnimationSequence(w.assets, w.assets.get(w.worldDir + "/units/" + name + "/" + eElement.getAttribute("tex") + ".png", Texture.class), Integer.parseInt(eElement.getAttribute("animFps")), Boolean.parseBoolean(eElement.getAttribute("animLoop")), Integer.parseInt(eElement.getAttribute("animFrames")));
                statesIdleTexes.set(i, deadAnim);
                statesHitTexes.set(i, deadAnim);
            }
            if (idle != null && !idle.getAttribute("soundLoop").equals("")) {
                statesIdleLoops.set(i, w.assets.get(w.worldDir + "/units/" + name + "/" + idle.getAttribute("soundLoop"), Sound.class));
            }
            if (hit != null && !hit.getAttribute("sound").equals("")) {
                statesHitSounds.set(i, w.assets.get(w.worldDir + "/units/" + name + "/" + hit.getAttribute("sound"), Sound.class));
            }
            if (idle == null && hit == null) {
                statesHitSounds.set(i, w.assets.get(w.worldDir + "/units/" + name + "/" + eElement.getAttribute("sound"), Sound.class));
            }
        }
        Element fighterSpriteElement = (Element) doc.getElementsByTagName("fighter").item(0);
        fighterTex = new AnimationSequence(w.assets, w.assets.get(w.worldDir + "/units/" + name + "/" + fighterSpriteElement.getAttribute("tex") + ".png", Texture.class), Integer.parseInt(fighterSpriteElement.getAttribute("animFps")), Boolean.parseBoolean(fighterSpriteElement.getAttribute("animLoop")), Integer.parseInt(fighterSpriteElement.getAttribute("animFrames")));

        NodeList dgList = doc.getElementsByTagName("dropGroup");
        for (int i = 0; i < dgList.getLength(); ++i) {
            Node nNode = dgList.item(i);
            Element eElement = (Element) nNode;
            NodeList dropList = eElement.getElementsByTagName("drop");
            for (int j = 0; j < dropList.getLength(); ++j) {
                Node nNode2 = dropList.item(j);
                Element eElement2 = (Element) nNode2;
                dropGroups.get(i).add(new Item(w, w.worldDir.path(), eElement2.getAttribute("name")));
            }
        }

        loader.initializeObjects(w.assets, w);
    }

    void drop() {
        for (int i = 0; i < dropGroups.size(); ++i) {
            float groupProb = dropGroupsProbs.get(i);

            if (Math.random() < groupProb) {
                for (int j = 0; j < dropGroups.get(i).size(); ++j) {
                    float dropProb = dropProbs.get(i).get(j);
                    if (Math.random() < dropProb) {
                        Item item = new Item(dropGroups.get(i).get(j));
                        item.stack = dropMins.get(i).get(j) + (int)Math.floor(dropMaxs.get(i).get(j) - dropMins.get(i).get(j) + 1);
                        drops.add(item);
                    }
                }
            }
        }
    }

    public void hit(UnitsDrawGroup udg, ArrayList<DamageType> dts, ArrayList<Integer> damages) {
        hitDTs = (ArrayList<DamageType>)dts.clone();
        int amount = 0;
        for (int i = 0; i < dts.size(); ++i) {
            boolean found = false;
            for (int j = 0; j < susceptibilities.size(); ++j) {
                if (susceptibilities.get(j).damageType.equals(dts.get(i))) {
                    hitDamages.add((int)(damages.get(i) * susceptibilities.get(j).percent));
                    amount += damages.get(i) * susceptibilities.get(j).percent;
                    found = true;
                }
            }
            if (!found) {
                hitDamages.add(damages.get(i));
                amount += damages.get(i);
            }
        }
        udg.battle.notifyStatChange(this, 0, amount);
        wasHit = true;
    }

    public void heal(World w, int baseHeal, boolean percent, UnitsDrawGroup udg, float x, float y) {
        String healStr;
        int amount;
        if (percent) {
            amount = (int)((float)stats.maxHp * (float)baseHeal / 100.0f);
        }
        else {
            amount = baseHeal;
        }
        stats.hp += amount;
        healStr = "+" + amount;
        udg.addParticle(new Particle(w.assets, healStr, new Color(0.2f, 1.0f, 0.2f, 1.0f), false, x, y, 1.0f));
        if (stats.hp > stats.maxHp) stats.hp = stats.maxHp;
        checkStates();
        udg.battle.notifyStatChange(this, 0, amount);
        wasHealed = true;
    }

    public ArrayList<Pair<String, Integer>> prepareSpecialVars() {
        ArrayList<Pair<String, Integer>> vars = new ArrayList<Pair<String, Integer>>();
        vars.add(new Pair<String, Integer>("hp", stats.hp));
        vars.add(new Pair<String, Integer>("maxHp", stats.maxHp));
        vars.add(new Pair<String, Integer>("ap", stats.ap));
        vars.add(new Pair<String, Integer>("maxAp", stats.maxAp));
        return vars;
    }

    public void checkStates() {
        if (stats.hp <= 0) {
            currentState = statesIdleTexes.size() - 1;
            stats.dead = true;
            drop();
        } else {
            for (int i = 0; i < statesConditions.size(); ++i) {
                if (statesConditions.get(i) != null && parser.parseCondition(statesConditions.get(i), prepareSpecialVars())) {
                    currentState = i;
                    break;
                }
            }
        }
    }

    public void getDamage(int dmg) {
        stats.hp -= dmg;
        if (stats.hp < 0) stats.hp = 0;
        checkStates();
    }

    float getHeight() {
        return statesHitTexes.get(0).getFirstFrame().getRegionHeight();
    }

    float getWidth() {
        return statesHitTexes.get(0).getFirstFrame().getRegionWidth();
    }

    public void draw(World w, UnitsDrawGroup udg, SpriteBatch batch, float x, float y, float scale) {
        TextureRegion tex;
        float shakeX = 0, shakeY = 0;
        if ((wasHit || wasHealed) || (!statesHitTexes.get(currentState).hasEnded() && !statesHitTexes.get(currentState).looping) || ((hitAlphaHeal != 0 || hitAlphaHit != 0) && hitShake != 0)) {
            shakeX = hitShake * (int)Math.floor(Math.random() * 2.0f) - 1.0f;
            shakeY = hitShake * (int)Math.floor(Math.random() * 2.0f) - 1.0f;
            batch.setColor(new Color(1.0f - hitAlphaHeal, 1.0f - hitAlphaHit, 1.0f - hitAlphaHit - hitAlphaHeal, 1.0f));
            if (!stats.dead && hitAlphaHeal == 0) tex = statesHitTexes.get(currentState).getCurrentFrame(false);
            else tex = statesIdleTexes.get(currentState).getCurrentFrame(false);
        } else {
            tex = statesIdleTexes.get(currentState).getCurrentFrame(false);
        }
        if (wasHit) {
            for (int i = 0; i < hitDamages.size(); ++i) {
                getDamage(hitDamages.get(i));
            }
            hitOffsetSpeed = 1.0f;
            hitAlphaHit =  1.0f;
            hitShake = 5.0f;
            for (int i = 0; i < statesHitPrt.get(currentState).size(); ++i) {
                ParticleProperties.ParticleSpawnProperties pp = statesHitPrt.get(currentState).get(i);
                int r = (int)Math.floor(Math.random() * (statesHitPrtMinMax.get(currentState).get(i).getValue() - statesHitPrtMinMax.get(currentState).get(i).getKey()) + 1) + statesHitPrtMinMax.get(currentState).get(i).getKey();
                float xSpread = ((float)Math.random() * 2.0f - 1.0f) * tex.getRegionWidth()/2.0f;
                float ySpread = ((float)Math.random()) * tex.getRegionHeight()/2.0f;
                for (int j = 0; j < r; ++j) {
                    udg.addParticle(w, w.getParticleByName(pp.particleName), statesHitPrt.get(currentState).get(i), x + scale * tex.getRegionWidth()/2.0f + xSpread, y - tex.getRegionHeight() - baseHeight - 20, ySpread);
                }
            }
            for (int i = 0; i < hitDamages.size(); ++i) {
                udg.addParticle(new Particle(w.assets, "" + hitDamages.get(i), hitDTs.get(i).color, false, x + scale * tex.getRegionWidth()/2.0f, y - tex.getRegionHeight() - baseHeight - 20, 1.0f));
            }
        }
        if (wasHealed) {
            hitAlphaHeal =  1.0f;
            hitShake = 5.0f;
        }
        batch.draw(tex, x + shakeX, y - tex.getRegionHeight() - baseHeight + shakeY, 0, 0, tex.getRegionWidth(), tex.getRegionHeight(), scale, scale, 0);
        batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        hitOffset += hitOffsetSpeed;
        if (hitAlphaHit > 0) hitAlphaHit -= 0.05f;
        else hitAlphaHit = 0;
        if (hitAlphaHeal > 0) hitAlphaHeal -= 0.05f;
        else hitAlphaHeal = 0;
        if (hitShake > 0) hitShake -= 0.5f;
        else hitShake = 0;
        if (hitOffsetSpeed > 0) hitOffsetSpeed -= 0.1f;
        else hitOffset -= 0.1f;
        if ((wasHit || wasHealed)) {
            hitDamages.clear();
            hitDTs.clear();
            wasHit = false;
            wasHealed = false;
        }
    }

}
