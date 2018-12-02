package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.schoolRPG.battleSystem.Skill;
import com.mygdx.schoolRPG.tools.AnimationSequence;
import com.mygdx.schoolRPG.tools.GlobalSequence;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Kraft on 04.09.2016.
 */
public class Item {
    enum EquipSlot {
        HEAD,
        BODY,
        NONE
    }
    EquipSlot equipSlot;
    float mass;
    public String fileName;
    ArrayList<String> namesInLanguages;
    ArrayList<String> descriptionsInLanguages;
    String replaces;
    public int stack;
    boolean stackable;
    int maxStack;
    String varName;
    Texture icon;
    Texture bigIcon;
    GlobalSequence sides;
    public boolean consumable = false;
    public Skill effect;

    public String getName(int language) {
        return namesInLanguages.get(language);
    }

    public String getDescription(int language) {
        return descriptionsInLanguages.get(language);
    }

    public Item(Item item) {
        fileName = item.fileName;
        icon = item.icon;
        bigIcon = item.bigIcon;
        sides = item.sides;
        mass = item.mass;
        stack = 1;
        equipSlot = item.equipSlot;
        stackable = item.stackable;
        maxStack = item.maxStack;
        varName = item.varName;
        replaces = item.replaces;
        namesInLanguages = item.namesInLanguages;
        descriptionsInLanguages = item.descriptionsInLanguages;
        consumable = item.consumable;
        effect = item.effect;
    }

    public Item(World w, String worldPath, String name) {
        fileName = name;
        icon = w.assets.get(worldPath + "/items/icons/" + fileName + ".png");
        if (w.assets.isLoaded(worldPath + "/items/big_icons/" + fileName + ".png")) {
            bigIcon = w.assets.get(worldPath + "/items/big_icons/" + fileName + ".png");
        }
        sides = new GlobalSequence(w.assets,worldPath + "/items/sides/" + fileName + ".png", 3);
        FileHandle itemDir =  Gdx.files.internal(worldPath + "/items");
        FileHandle itemXML = null;
        for (FileHandle entry: itemDir.list()) {
            if (entry.nameWithoutExtension().equals(fileName)) {
                itemXML = entry;
            }
        }
        //loadTextInfo(assets, worldPath, language);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document xml = null;
        try {
            xml = dBuilder.parse(itemXML.read());
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        xml.getDocumentElement().normalize();
        mass = Integer.parseInt(xml.getDocumentElement().getAttribute("mass"));
        stack = 1;
        String slot = xml.getDocumentElement().getAttribute("equip_slot");
        if (slot.equals("HEAD")) {
            equipSlot = EquipSlot.HEAD;
        } else if (slot.equals("BODY")) {
            equipSlot = EquipSlot.BODY;
        } else {
            equipSlot = EquipSlot.NONE;
        }
        if (!xml.getDocumentElement().getAttribute("consumable").equals("")) {
            consumable = Boolean.parseBoolean(xml.getDocumentElement().getAttribute("consumable"));
        }
        stackable = Boolean.parseBoolean(xml.getDocumentElement().getAttribute("stackable"));
        maxStack = Integer.parseInt(xml.getDocumentElement().getAttribute("maxStack"));
        varName = xml.getDocumentElement().getAttribute("var");
        replaces = xml.getDocumentElement().getAttribute("replaces");
        NodeList nList = xml.getElementsByTagName("eng");
        Node nNode = nList.item(0);
        Element eElement = (Element) nNode;
        namesInLanguages = new ArrayList<String>();
        namesInLanguages.add(eElement.getAttribute("name"));
        descriptionsInLanguages = new ArrayList<String>();
        descriptionsInLanguages.add(eElement.getAttribute("description"));
        nList = xml.getElementsByTagName("rus");
        nNode = nList.item(0);
        eElement = (Element) nNode;
        namesInLanguages.add(eElement.getAttribute("name"));
        descriptionsInLanguages.add(eElement.getAttribute("description"));
        nList = xml.getElementsByTagName("effect");
        nNode = nList.item(0);
        eElement = (Element) nNode;
        if (eElement != null) {
            effect = w.battleSystem.getSkillByName(eElement.getAttribute("name"));
        }
    }
}
