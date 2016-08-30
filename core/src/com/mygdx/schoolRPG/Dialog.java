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
    ArrayList<Choice> choices;
    ArrayList<Integer> speechTransitionsIds;
    ArrayList<ArrayList<Integer>> choiceTransitionsIds;
    boolean monologue;
    Texture overlay;
    Speech currentSpeech = null;
    Choice currentChoice = null;
    boolean finished = false;

    public Dialog(String filePath, boolean monologue, ArrayList<NPC> npcs, AssetManager assets, String charPath) {
        speeches = new ArrayList<Speech>();
        choices = new ArrayList<Choice>();
        choiceTransitionsIds = new ArrayList<ArrayList<Integer>>();
        speechTransitionsIds = new ArrayList<Integer>();
        this.monologue = monologue;
        overlay = assets.get("dialog_overlay1.png", Texture.class);
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            char c;
            String line = in.readLine();
            int charId = -1;
            int flagCharId = -1;
            int texCharId = -1;
            int flagId = -1;
            int transId = -1;
            String imageName = "";
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
                            imageName = in.readLine();
                        }
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

                        speeches.add(new Speech(name, phrases, assets, charPath + "/" + texCharId + "/graphics/" + imageName + ".png", charId, flagCharId, flagId, npcs));
                        speechTransitionsIds.add(transId);
                        line = in.readLine();
                        c = line.charAt(0);
                    } else if (c == '*') {
                        line = in.readLine();
                        flagCharId = Integer.parseInt(line);
                        line = in.readLine();
                        flagId = Integer.parseInt(line);
                        line = in.readLine();
                        c = line.charAt(0);
                        speeches.add(new Speech(name, phrases, assets, charPath + "/" + texCharId + "/graphics/" + imageName + ".png", charId, flagCharId, flagId, npcs));
                        speechTransitionsIds.add(speechTransitionsIds.size() + 1);
                        /*for (int i =0; i < npcs.size(); ++i) {
                            if (npcs.get(i).charId == charId) {
                                npcs.get(i).flags.set(flagId, true);
                            }
                        }*/
                    } else {
                        speeches.add(new Speech(name, phrases, assets, charPath + "/" + texCharId + "/graphics/" + imageName + ".png", charId, flagCharId, flagId, npcs));
                        speechTransitionsIds.add(speechTransitionsIds.size() + 1);
                    }
                } else if (c == '%') {
                    speechTransitionsIds.set(speechTransitionsIds.size()-1, -1 * (choiceTransitionsIds.size() + 1));
                    choiceTransitionsIds.add(new ArrayList<Integer>());
                    ArrayList<String> phrases = new ArrayList<String>();
                    line = in.readLine();
                    while (c != '#') {
                        transId = Integer.parseInt(line);
                        line = in.readLine();
                        phrases.add(line);
                        choiceTransitionsIds.get(choiceTransitionsIds.size()-1).add(transId);
                        line = in.readLine();
                        c = line.charAt(0);
                    }
                    choices.add(new Choice(speeches.get(speeches.size()-1).phrases.get(speeches.get(speeches.size()-1).phrases.size() - 1), phrases, assets, charPath + "/" + texCharId + "/graphics/" + imageName + ".png"));
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
