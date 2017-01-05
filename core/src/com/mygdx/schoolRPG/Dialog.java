package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Kraft on 10.06.2016.
 */
public class Dialog {

    ArrayList<Speech> speeches;
    ArrayList<String> changedFlagsNames;
    ArrayList<Boolean> changedFlagsVals;
    ArrayList<Choice> choices;
    ArrayList<Integer> speechTransitionsIds;
    ArrayList<ArrayList<Integer>> choiceTransitionsIds;
    boolean monologue;
    Texture overlay;
    Speech currentSpeech = null;
    Choice currentChoice = null;
    boolean finished = false;
    public int language;
    public String fileName;

    public void reload(String folderPath, int language, int startId) {
        this.language = language;
        try {
            BufferedReader in = new BufferedReader(new FileReader(folderPath + fileName));
            char c;
            String line = in.readLine();
            int choiceId = 0;
            for (int i = 0; i < speeches.size(); ++i) {
                while (!line.startsWith("#")) {
                    line = in.readLine();
                    if (line == null) {
                            return;
                    }
                }
                in.readLine();
                in.readLine();
                in.readLine();
                for (int j = 0; j < speeches.get(i).phrases.size(); ++j) {
                    line = in.readLine();
                    speeches.get(i).phrases.set(j, line);
                }
                line = in.readLine();
                if (line.charAt(0) == '%') {
                    line = in.readLine();
                    choices.get(choiceId).question = line;
                    for (int j = 0; j < choices.get(choiceId).phrases.size(); ++j) {
                        in.readLine();
                        line = in.readLine();
                        choices.get(choiceId).phrases.set(j, line);
                    }
                    choiceId++;
                }
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (startId < 0) {
            currentSpeech = choices.get(- startId - 1);
        } else {
            currentSpeech = speeches.get(startId);
        }
    }

    public int getCurrentSpeechId() {
        int index = speeches.indexOf(currentSpeech);
        if (index == -1) index = -(choices.indexOf(currentSpeech) + 1);
        return index;
    }

    public Dialog(String folder, String fileName, boolean monologue, ArrayList<NPC> npcs, Player player, AssetManager assets, String charPath, int language) {
        speeches = new ArrayList<Speech>();
        choices = new ArrayList<Choice>();
        choiceTransitionsIds = new ArrayList<ArrayList<Integer>>();
        speechTransitionsIds = new ArrayList<Integer>();
        changedFlagsVals = new ArrayList<Boolean>();
        changedFlagsNames = new ArrayList<String>();
        this.monologue = monologue;
        this.language = language;
        this.fileName = fileName;
        overlay = assets.get("dialog_overlay1.png", Texture.class);
        try {
            BufferedReader in = new BufferedReader(new FileReader(folder + fileName));
            char c;
            String line = in.readLine();
            int charId = -1;
            int flagCharId = -1;
            int texCharId = -1;
            int flagId = -1;
            int transId = -1;
            String imageName = "";
            String talkPostfix = "";
            do {
                c = line.charAt(0);

                if (c == '\uFEFF' || c == '#') {
                    //Speech spc = new Speech();
                    ArrayList<String> phrases = new ArrayList<String>();
                    String name = in.readLine();

                    line = in.readLine();
                    c = line.charAt(0);
                    if (c >= '0' && c < '9') {
                        charId = Integer.parseInt(line);
                        if (charId != 0) {
                            texCharId = charId;
                        }
                        if (charId == texCharId) {
                            talkPostfix = "_speaking";
                        } else {
                            talkPostfix = "_listening";
                        }
                        imageName = in.readLine();
                        line = in.readLine();
                    }
                    do {
                        phrases.add(line);
                        line = in.readLine();
                        c = line.charAt(0);
                    } while (c != '&' && c != '*' && c != '%' && c != '\uFEFF' && c != '$' && c != '#');

                    if (c == '&') {
                        line = in.readLine();
                        transId = Integer.parseInt(line);

                        speeches.add(new Speech(this, name, phrases, assets, charPath + "/" + texCharId + "/graphics/" + imageName + talkPostfix + ".png", charId, flagCharId, flagId, false, npcs, player));
                        speechTransitionsIds.add(transId);
                        line = in.readLine();
                        c = line.charAt(0);
                    } else if (c == '*') {
                        line = in.readLine();
                        flagCharId = Integer.parseInt(line);
                        line = in.readLine();
                        flagId = Integer.parseInt(line);
                        line = in.readLine();
                        int flagVal = Integer.parseInt(line);
                        boolean flagV = true;
                        if (flagVal == 0) {
                            flagV = false;
                        }
                        line = in.readLine();
                        c = line.charAt(0);
                        speeches.add(new Speech(this, name, phrases, assets, charPath + "/" + texCharId + "/graphics/" + imageName + talkPostfix + ".png", charId, flagCharId, flagId, flagV, npcs, player));
                        speechTransitionsIds.add(speechTransitionsIds.size() + 1);
                    } else {
                        speeches.add(new Speech(this, name, phrases, assets, charPath + "/" + texCharId + "/graphics/" + imageName + talkPostfix + ".png", charId, flagCharId, flagId, false, npcs, player));
                        speechTransitionsIds.add(speechTransitionsIds.size() + 1);
                    }
                } else if (c == '%') {
                    speechTransitionsIds.set(speechTransitionsIds.size()-1, -1 * (choiceTransitionsIds.size() + 1));
                    choiceTransitionsIds.add(new ArrayList<Integer>());
                    talkPostfix = "_listening";
                    ArrayList<String> phrases = new ArrayList<String>();
                    line = in.readLine();
                    phrases.add(line);
                    line = in.readLine();
                    while (c != '#') {
                        transId = Integer.parseInt(line);
                        line = in.readLine();
                        phrases.add(line);
                        choiceTransitionsIds.get(choiceTransitionsIds.size()-1).add(transId);
                        line = in.readLine();
                        c = line.charAt(0);
                    }
                    choices.add(new Choice(this, speeches.get(speeches.size()-1).speaker, phrases, assets, charPath + "/" + texCharId + "/graphics/" + imageName + talkPostfix + ".png", charId, npcs, player));
                } else if (c == '$') {
                    speechTransitionsIds.set(speechTransitionsIds.size()-1, 999999);
                    line = in.readLine();
                    if (line != null) {
                        c = line.charAt(0);
                    }
                }
            } while (line != null);

            //flagsCount = Integer.parseInt(line);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentSpeech = speeches.get(0);
    }

    private void checkFinished(int id) {
        if (id >= 0) {
            if (id >= speechTransitionsIds.size()) {
                finished = true;
                return;
            }
            currentSpeech = speeches.get(id);
            currentChoice = null;
        } else {
            id = -1 * id - 1;
            if (id >= choiceTransitionsIds.size()) {
                finished = true;
                return;
            }
            currentChoice = choices.get(id);
            currentSpeech = null;
        }
    }

    public void draw(SpriteBatch batch, boolean paused) {
        float screenRatioX = Gdx.graphics.getWidth()/1280.0f;
        batch.draw(overlay, Gdx.graphics.getWidth()/screenRatioX/2 - overlay.getWidth()/2, 0);
        if (currentSpeech != null) {
            currentSpeech.draw(batch, paused);
            if (currentSpeech.finished) {
                int id = speechTransitionsIds.get(speeches.indexOf(currentSpeech));
                checkFinished(id);
            }
        } else {
            currentChoice.draw(batch, paused);
            if (currentChoice.finished) {
                int id = choiceTransitionsIds.get(choices.indexOf(currentChoice)).get(currentChoice.selector.getSelectedIndex());
                checkFinished(id);
            }
        }
    }
}
