package com.mygdx.schoolRPG.battleSystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.schoolRPG.ObjectLoader;
import com.mygdx.schoolRPG.World;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

public class BattleSystem {

    ArrayList<DamageType> damageTypes;
    ArrayList<StatusEffect> statusEffects;
    ArrayList<Skill> skills;

    public static Color hex2Rgb(String colorStr) {
        return new Color(
                (float)Integer.valueOf( colorStr.substring( 1, 3 ), 16 ) / 255.0f,
                (float)Integer.valueOf( colorStr.substring( 3, 5 ), 16 ) / 255.0f,
                (float)Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) / 255.0f,
                1.0f );
    }

    public BattleSystem(World w) {
        FileHandle statsXML = Gdx.files.internal(w.worldDir + "/skills/skills.xml");
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

        damageTypes = new ArrayList<DamageType>();
        NodeList dtList = doc.getElementsByTagName("damage-type");
        for (int i = 0; i < dtList.getLength(); ++i) {
            Node nNode = dtList.item(i);
            Element eElement = (Element) nNode;
            damageTypes.add(new DamageType(eElement.getAttribute("name"), hex2Rgb(eElement.getAttribute("color"))));
        }

        statusEffects = new ArrayList<StatusEffect>();
        NodeList seList = doc.getElementsByTagName("damage-type");
        for (int i = 0; i < seList.getLength(); ++i) {
            Node nNode = seList.item(i);
            Element eElement = (Element) nNode;
            ArrayList<DamageType> seDts = new ArrayList<DamageType>();
            ArrayList<Integer> seBds = new ArrayList<Integer>();
            NodeList dList = eElement.getElementsByTagName("damage");
            for (int j = 0; j < dList.getLength(); ++j) {
                Node nNode2 = dtList.item(i);
                Element eElement2 = (Element) nNode2;
                seDts.add(getDamageTypeByName(eElement2.getAttribute("name")));
                seBds.add(Integer.parseInt(eElement2.getAttribute("baseDamage")));
            }
            statusEffects.add(new StatusEffect(eElement.getAttribute("name"), seDts, seBds));
        }

    }

    public void load(World w) {
        FileHandle statsXML = Gdx.files.internal(w.worldDir + "/skills/skills.xml");
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
        NodeList skList = doc.getElementsByTagName("skill");
        ObjectLoader loader = new ObjectLoader();
        for (int i = 0; i < skList.getLength(); ++i) {
            Node nNode = skList.item(i);
            Element eElement = (Element) nNode;
            Element eElement2 = (Element)eElement.getElementsByTagName("animation").item(0);
            if (eElement2.getAttribute("type").equals("melee-fist")) {
                if (!eElement2.getAttribute("name").equals("")) w.assets.load(w.worldDir.path() + "/skills/" + eElement2.getAttribute("name") + "_front.png", Texture.class);
                if (!eElement2.getAttribute("name").equals("")) w.assets.load(w.worldDir.path() + "/skills/" + eElement2.getAttribute("name") + "_back.png", Texture.class);
            } else {
                loader.loadParticle(w, eElement2.getAttribute("name"));
            }
            if (!eElement2.getAttribute("sound").equals("")) w.assets.load(w.worldDir.path() + "/skills/" + eElement2.getAttribute("sound"), Sound.class);
        }
    }

    public void initializeResources(World w) {
        FileHandle statsXML = Gdx.files.internal(w.worldDir + "/skills/skills.xml");
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
        NodeList skList = doc.getElementsByTagName("skill");
        for (int i = 0; i < skList.getLength(); ++i) {
            Node nNode = skList.item(i);
            Element eElement = (Element) nNode;
            ArrayList<DamageType> damageTypes = new ArrayList<DamageType>();
            ArrayList<Integer> baseDamages = new ArrayList<Integer>();
            NodeList dtList = eElement.getElementsByTagName("damage");
            for (int j = 0; j < dtList.getLength(); ++j) {
                Node nNode2 = dtList.item(j);
                Element eElement2 = (Element) nNode2;
                damageTypes.add(getDamageTypeByName(eElement2.getAttribute("damageType")));
                baseDamages.add(Integer.parseInt(eElement2.getAttribute("baseDamage")));
            }
            ArrayList<StatusEffect> statusEffects = new ArrayList<StatusEffect>();
            NodeList seList = eElement.getElementsByTagName("status-effect");
            for (int j = 0; j < seList.getLength(); ++j) {
                Node nNode2 = seList.item(j);
                Element eElement2 = (Element) nNode2;
                statusEffects.add(getStatusEffectByName(eElement2.getAttribute("name")));
            }
            Element nameElem = (Element) eElement.getElementsByTagName("name").item(0);
            ArrayList<String> title = new ArrayList<String>();
            title.add(nameElem.getAttribute("eng"));
            title.add(nameElem.getAttribute("rus"));
            Skill.SkillAnimationType sat = Skill.getSkillAnimationTypeFromString(((Element)eElement.getElementsByTagName("animation").item(0)).getAttribute("type"));
            Element anim = ((Element)eElement.getElementsByTagName("animation").item(0));
            skills.add(new Skill(w, eElement.getAttribute("name"), title, Integer.parseInt(eElement.getAttribute("APcost")), Integer.parseInt(eElement.getAttribute("cooldown")), Boolean.parseBoolean(eElement.getAttribute("positive")), damageTypes, baseDamages, sat, statusEffects,
                    anim.getAttribute("name"), Integer.parseInt(anim.getAttribute("fps")), Boolean.parseBoolean(anim.getAttribute("looping")), Integer.parseInt(anim.getAttribute("frames")), anim.getAttribute("sound")));
        }
    }

    Skill getSkillByName(String name) {
        for (int i = 0; i < skills.size(); ++i) {
            if (skills.get(i).name.equals(name)) {
                return skills.get(i);
            }
        }
        return null;
    }

    DamageType getDamageTypeByName(String name) {
        for (int i = 0; i < damageTypes.size(); ++i) {
            if (damageTypes.get(i).name.equals(name)) {
                return damageTypes.get(i);
            }
        }
        return null;
    }

    StatusEffect getStatusEffectByName(String name) {
        for (int i = 0; i < statusEffects.size(); ++i) {
            if (statusEffects.get(i).name.equals(name)) {
                return statusEffects.get(i);
            }
        }
        return null;
    }
}
