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
    ArrayList<String> changedFlagsNames;
    ArrayList<Boolean> changedFlagsVals;
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

    public void reload(int language, int startId) {
        parseXMLDialog(language, startId);
    }


    private boolean parseCondition(String condition) {
        if (condition.equals("")) return true;
        String nodes[] = condition.split(";");
        int openCount = 0;
        for (int i = 0; i < nodes.length; ++i) {
            if (nodes[i].equals("LEFT")) {
                for (int j = i + 1; j < nodes.length; ++j) {
                    if (nodes[i].equals("LEFT")) openCount++;
                    else if (nodes[i].equals("RIGHT")) openCount--;
                    if (nodes[j].equals("RIGHT") && openCount == 0) {
                        String subStr = "";
                        for (int z = i+1; z < j; ++z) {
                            subStr += nodes[z];
                            if (z < j-1) subStr += ";";
                        }
                        boolean out = parseCondition(subStr);
                        String split[] = condition.split(subStr);
                        String left = split[0].substring(0, split[0].length() - 6);
                        String right = split[1].substring(6, split[1].length() - 1);
                        if (out) {
                            condition = left + "TRUE" + right;
                        } else {
                            condition = left + "FALSE" + right;
                        }
                        nodes = condition.split(";");
                        i = 0;
                        j = 1;
                    }
                }
            }
        }
        for (int i = 0; i < nodes.length; ++i) {
            if (nodes[i].startsWith("f ")) {
                String arguments[] = nodes[i].split(" ");
                //int flagChar = Integer.parseInt(arguments[1]);
                String flagName = arguments[1];
                boolean flagVal = Integer.parseInt(arguments[2]) == 1;
                boolean val = false;
                if (mainCharId == 0) {
                    val = (player.world.flags.get(player.world.flagNames.indexOf(flagName)) == flagVal);
                } else {
                    for (int j = 0; j < npcs.size(); ++j) {
                        if (npcs.get(j).charId == mainCharId) {
                            int flagId = npcs.get(j).flagNames.indexOf(flagName);
                            if (flagId == -1) continue;
                            val = (npcs.get(j).flags.get(flagId) == flagVal);
                            break;
                        }
                    }
                }
                if (val) {
                    nodes[i] = "TRUE";
                } else {
                    nodes[i] = "FALSE";
                }
            } else if (nodes[i].startsWith("i ")) {
                String arguments[] = nodes[i].split(" ");
                int ownerId = Integer.parseInt(arguments[1]);
                String itemName = arguments[2];
                int itemsCount = Integer.parseInt(arguments[3]);
                boolean not = Integer.parseInt(arguments[4]) != 1;
                boolean val = false;
                if (ownerId == 0) {
                    int itemsCountFound = 0;
                    for (int z = 0; z < player.inventory.size(); ++z) {
                        if (player.inventory.get(z).fileName.equals(itemName)) {
                            itemsCountFound += player.inventory.get(z).stack;
                        }
                    }
                    val = ((!not && itemsCountFound >= itemsCount)||(not && itemsCountFound < itemsCount));
                } else {
                    for (int j = 0; j < npcs.size(); ++j) {
                        if (npcs.get(j).charId == ownerId) {
                            int itemsCountFound = 0;
                            for (int z = 0; z < npcs.get(j).inventory.size(); ++z) {
                                if (npcs.get(j).inventory.get(z).fileName.equals(itemName)) {
                                    itemsCountFound += npcs.get(j).inventory.get(z).stack;
                                }
                            }
                            val = ((!not && itemsCountFound >= itemsCount)||(not && itemsCountFound < itemsCount));
                            break;
                        }
                    }
                }
                if (val) {
                    nodes[i] = "TRUE";
                } else {
                    nodes[i] = "FALSE";
                }
            }
        }
        for (int i = 0; i < nodes.length-2; ++i) {
            if (i >= nodes.length) break;
            if (nodes[i+1].equals("AND")) {
                if (nodes[i].equals("TRUE") && nodes[i].equals("TRUE")) {
                    nodes[i] = "TRUE";
                } else {
                    nodes[i] = "FALSE";
                }
                for (int j = i+1; i < nodes.length-2; ++i) {
                    nodes[j] = nodes[j+2];
                }
                i = 0;
            }
        }
        for (int i = 0; i < nodes.length-2; ++i) {
            if (i >= nodes.length) break;
            if (nodes[i+1].equals("OR")) {
                if (nodes[i].equals("TRUE") || nodes[i].equals("TRUE")) {
                    nodes[i] = "TRUE";
                } else {
                    nodes[i] = "FALSE";
                }
                for (int j = i+1; i < nodes.length-2; ++i) {
                    nodes[j] = nodes[j+2];
                }
                i = 0;
            }
        }
        return nodes[0].equals("TRUE");
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
                boolean flagChange = false;
                int flagOwner = -1;
                int flagId = -1;
                boolean flagVal = false;
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
                    } else if (property.getNodeName().equals("flagChange")) {
                        flagChange = true;
                        flagOwner = Integer.parseInt(((Element)property).getAttribute("charId"));
                        flagId = Integer.parseInt(((Element)property).getAttribute("Id"));
                        flagVal = Boolean.parseBoolean(((Element)property).getAttribute("value"));
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
                        if (parseCondition(speechTransitionsConditions.get(i).get(j))) {
                            phrases.add(speechTransitionsText.get(i).get(j).get(language));
                        }
                    }
                    Choice choice = new Choice(this, speaker[language], phrases, assets, charPath + "/" + mainCharId + "/graphics/" + spriteFileName + ".png", speakerId, npcs, player, parent);
                    speeches.add(choice);
                } else {
                    if (itemTransfer) {
                        phrases.add(okGivePhrases[language]);
                        //phrases.add(notOkGivePhrases[language]);
                        phrases.add("];[" + itemName + ";" + itemsCount + ";" + giveToId);
                    }
                    Speech speech = new Speech(this, speaker[language], phrases, assets, charPath + "/" + mainCharId + "/graphics/" + spriteFileName + ".png", speakerId, flagOwner, flagId, flagVal, npcs, player, parent);
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
                if (parseCondition(speechTransitionsConditions.get(id).get(maxPriorityId))) {
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
        changedFlagsVals = new ArrayList<Boolean>();
        changedFlagsNames = new ArrayList<String>();
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
                int goodId = findTransitionByPriority(currentSpeechId);
                if (goodId > 0) {
                    currentSpeech.doActions();
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
