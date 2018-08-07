package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.mygdx.schoolRPG.menus.GameMenu;
import com.mygdx.schoolRPG.menus.Menu;
import com.mygdx.schoolRPG.tools.ConditionParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Kraft on 10.06.2016.
 */
public class Dialog implements InputProcessor {

    ArrayList<Speech> speeches;
    ArrayList<String> changedVarsNames;
    ArrayList<Integer> changedVarsVals;
    ArrayList<ArrayList<ArrayList<String>>> speechTransitionsText;
    ArrayList<ArrayList<Integer>> speechTransitionsIds;
    ArrayList<ArrayList<Integer>> speechTransitionsPriorities;
    ArrayList<ArrayList<String>> speechTransitionsConditions;
    boolean monologue;
    Texture overlay;
    Speech currentSpeech = null;
    int currentSpeechId = -1;
    boolean finished = false;
    public int language;
    public String charPath;
    Menu parent;
    int mainCharId = -1;
    AssetManager assets;
    ArrayList<NPC> npcs;
    Player player;
    String folder;
    String fileName;
    ConditionParser parser;

    public void reload(int language, int startId) {
        parseXMLDialog(language, startId);
    }

    private void parseXMLDialog(int language, int startId) {
        currentSpeech = null;
        this.language = language;
        speeches.clear();
        speechTransitionsIds.clear();
        speechTransitionsPriorities.clear();
        speechTransitionsConditions.clear();
        speechTransitionsText.clear();
        FileHandle charDir =  Gdx.files.internal(folder + "/" + fileName);
        FileHandle objectXML = null;
        boolean isDialog = false;
        if (charDir.exists()) {
            objectXML = charDir;
            isDialog = true;
        }
        if (isDialog) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = null;
            try {
                dBuilder = dbFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            org.w3c.dom.Document doc = null;
            try {
                doc = dBuilder.parse(objectXML.read());
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            doc.getDocumentElement().normalize();
            NodeList speechBlocks = doc.getElementsByTagName("speechBlock");
            parser = new ConditionParser(npcs, player, mainCharId);
            for (int i= 0; i< speechBlocks.getLength(); ++i) {
                Element curSpeechBlock = (Element) speechBlocks.item(i);
                NodeList curSpeechBlockProperties = curSpeechBlock.getChildNodes();
                String text[] = {curSpeechBlock.getAttribute("textEng"),curSpeechBlock.getAttribute("textRus")};
                String speaker[] = {curSpeechBlock.getAttribute("speakerNameEng"),curSpeechBlock.getAttribute("speakerNameRus")};
                int speakerId;
                try {
                    speakerId = Integer.parseInt(curSpeechBlock.getAttribute("speakerId"));
                } catch (Exception e) {
                    speakerId = mainCharId;
                }

                String spriteFileName = "";
                boolean varChange = false;
                int varOwner = -1;
                String varId = "";
                String varVal = "";
                boolean itemTransfer = false;
                int giveToId = -1;
                String itemName = "";
                int itemsCount = 0;
                String okGivePhrases[] = {"",""};
                String notOkGivePhrases[] = {"",""};
                boolean foundAnswer = false;

                speechTransitionsIds.add(new ArrayList<Integer>());
                speechTransitionsPriorities.add(new ArrayList<Integer>());
                speechTransitionsConditions.add(new ArrayList<String>());
                speechTransitionsText.add(new ArrayList<ArrayList<String>>());

                for (int j= 0; j < curSpeechBlockProperties.getLength(); ++j) {
                    Node property = curSpeechBlockProperties.item(j);
                    if (property.getNodeName().equals("sprite")) {
                        spriteFileName = ((Element)property).getAttribute("name");
                    } else if (property.getNodeName().equals("varChange")) {
                        varChange = true;
                        varOwner = Integer.parseInt(((Element)property).getAttribute("charId"));
                        varId = ((Element)property).getAttribute("Id");
                        varVal = ((Element)property).getAttribute("value");
                    } else if (property.getNodeName().equals("itemTransfer")) {
                        itemTransfer = true;
                        giveToId = Integer.parseInt(((Element)property).getAttribute("to"));
                        itemName = ((Element)property).getAttribute("itemName");
                        itemsCount = Integer.parseInt(((Element)property).getAttribute("count"));
                        okGivePhrases[0] = ((Element)property).getAttribute("canGiveEng");
                        okGivePhrases[1] = ((Element)property).getAttribute("canGiveRus");
                        notOkGivePhrases[0] = ((Element)property).getAttribute("cantGiveEng");
                        notOkGivePhrases[1] = ((Element)property).getAttribute("cantGiveRus");
                    } else if (property.getNodeName().equals("link")) {
                        Element link = (Element)property;
                        speechTransitionsIds.get(i).add(Integer.parseInt(link.getAttribute("goto")));
                        speechTransitionsPriorities.get(i).add(Integer.parseInt(link.getAttribute("priority")));
                        speechTransitionsConditions.get(i).add(link.getAttribute("condition"));
                        speechTransitionsText.get(i).add(new ArrayList<String>());
                        speechTransitionsText.get(i).get(speechTransitionsText.get(i).size()-1).add(link.getAttribute("textEng"));
                        speechTransitionsText.get(i).get(speechTransitionsText.get(i).size()-1).add(link.getAttribute("textRus"));
                        if (!foundAnswer) {
                            foundAnswer = speechTransitionsText.get(i).get(speechTransitionsText.get(i).size()-1).get(0).length() > 0 || speechTransitionsText.get(i).get(speechTransitionsText.get(i).size()-1).get(1).length() > 0;
                        }
                    }
                }
                ArrayList<String> phrases = new ArrayList<String>(Arrays.asList(text[language].split("\n")));
                for (int j = 0; j < phrases.size(); ++j) {
                    if (j >= phrases.size()) break;
                    if (phrases.get(j).equals("")) {
                        phrases.remove(j);
                    }
                }
                if (foundAnswer) {
                    for (int j = 0; j < speechTransitionsText.get(i).size(); ++j) {
                        if (parser.parseCondition(speechTransitionsConditions.get(i).get(j))) {
                            phrases.add(speechTransitionsText.get(i).get(j).get(language));
                        }
                    }
                    Choice choice = new Choice(this, speakerId, speaker[language], phrases, assets, charPath + "/" + mainCharId + "/graphics/" + spriteFileName + ".png", speakerId, npcs, player, parent, parser);
                    speeches.add(choice);
                } else {
                    if (itemTransfer) {
                        phrases.add(okGivePhrases[language]);
                        //phrases.add(notOkGivePhrases[language]);
                        phrases.add("];[" + itemName + ";" + itemsCount + ";" + giveToId);
                    }
                    Speech speech = new Speech(this, speakerId, speaker[language], phrases, assets, charPath + "/" + mainCharId + "/graphics/" + spriteFileName + ".png", speakerId, varOwner, varId, varVal, npcs, player, parent, parser);
                    speeches.add(speech);
                }
            }
            if (startId <= 0) {
                currentSpeechId = findTransitionByPriority(0);
                currentSpeech = speeches.get(currentSpeechId);
            } else {
                currentSpeechId = startId;
                currentSpeech = speeches.get(currentSpeechId);
            }
        }
    }

    private int findTransitionByPriority(int id) {
        ArrayList<Integer> checkedLinks = new ArrayList<Integer>();
        while (checkedLinks.size() < speechTransitionsIds.get(id).size()) {
            int maxPriority = 999999;
            int maxPriorityId = -1;
            for (int i = 0; i < speechTransitionsIds.get(id).size(); ++i) {
                if (checkedLinks.indexOf(i) != -1) continue;
                int priority = speechTransitionsPriorities.get(id).get(i);
                if (priority < maxPriority) {
                    maxPriority = priority;
                    maxPriorityId = i;
                }
            }
            if (maxPriorityId != -1) {
                checkedLinks.add(maxPriorityId);
                if (parser.parseCondition(speechTransitionsConditions.get(id).get(maxPriorityId))) {
                    return speechTransitionsIds.get(id).get(maxPriorityId);
                }
            } else {
                return -1;
            }
        }
        return -1;
    }

    public Dialog(String folder, String fileName, int mainCharId, boolean monologue, ArrayList<NPC> npcs, Player player, AssetManager assets, String charPath, int language, Menu parent) {
        this.parent = parent;
        this.charPath = charPath;
        this.assets = assets;
        this.npcs = npcs;
        this.player = player;
        this.mainCharId = mainCharId;
        speeches = new ArrayList<Speech>();
        speechTransitionsText = new ArrayList<ArrayList<ArrayList<String>>>();
        speechTransitionsIds = new ArrayList<ArrayList<Integer>>();
        speechTransitionsPriorities = new ArrayList<ArrayList<Integer>>();
        speechTransitionsConditions = new ArrayList<ArrayList<String>>();
        changedVarsVals = new ArrayList<Integer>();
        changedVarsNames = new ArrayList<String>();
        this.monologue = monologue;
        this.language = language;
        //this.fileName = folder + fileName;
        if (fileName.equals("")) {
            String ar[] = folder.split("/");
            fileName = ar[ar.length-1];
            folder = folder.substring(0, folder.length() - 2 - fileName.length());
        }
        this.folder = folder;
        this.fileName = fileName;
        overlay = assets.get("dialog_overlay1.png", Texture.class);
        parseXMLDialog(language, -1);
        Gdx.input.setInputProcessor(this);
    }

    public void draw(SpriteBatch batch, boolean paused) {
        float screenRatioX = Gdx.graphics.getWidth()/1280.0f;
        batch.draw(overlay, Gdx.graphics.getWidth()/screenRatioX/2 - overlay.getWidth()/2, 0);
        if (currentSpeech.isSpeech) {
            parent.dialogSkipping = true;
            currentSpeech.draw(batch, paused);
            if (currentSpeech.finished) {
                currentSpeech.doActions();
                int goodId = findTransitionByPriority(currentSpeechId);
                if (goodId > 0) {
                    speeches.get(goodId).prevSpeechId = currentSpeechId;
                    currentSpeechId = goodId;
                    currentSpeech = speeches.get(currentSpeechId);
                } else {
                    finished = true;
                }
            }
        } else {
            parent.dialogSkipping = false;
            Choice choice = (Choice)currentSpeech;
            choice.draw(batch, paused);
            if (choice.finished) {
                int goodId = speechTransitionsIds.get(currentSpeechId).get(choice.selector.getSelectedIndex());
                if (goodId <= 0 || goodId >= speeches.size()) finished = true;
                else {
                    speeches.get(goodId).prevSpeechId = currentSpeechId;
                    currentSpeechId = goodId;
                    currentSpeech = speeches.get(currentSpeechId);
                }
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if (amount == -1) {
            int newSpeechId = speeches.get(currentSpeechId).prevSpeechId;
            if (newSpeechId > -1) {
                currentSpeechId = newSpeechId;
                currentSpeech = speeches.get(currentSpeechId);
                currentSpeech.undoActions();
                currentSpeech.reset();
            }
        } else if (currentSpeech.isSpeech) {
            int newSpeechId = findTransitionByPriority(currentSpeechId);
            if (newSpeechId > -1 && newSpeechId < speeches.size()) {
                currentSpeech.doActions();
                speeches.get(newSpeechId).prevSpeechId = currentSpeechId;
                currentSpeechId = newSpeechId;
                currentSpeech = speeches.get(currentSpeechId);
                currentSpeech.reset();
            }
        }
        return false;
    }
}
