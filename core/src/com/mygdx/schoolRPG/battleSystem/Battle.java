package com.mygdx.schoolRPG.battleSystem;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.schoolRPG.Background;
import com.mygdx.schoolRPG.Particle;
import com.mygdx.schoolRPG.World;
import com.mygdx.schoolRPG.battleSystem.ui.FriendlyUnitsDrawGroup;
import com.mygdx.schoolRPG.battleSystem.ui.UnitsDrawGroup;

import java.util.ArrayList;

import static com.mygdx.schoolRPG.Area.SCREEN_HEIGHT;
import static com.mygdx.schoolRPG.Area.SCREEN_WIDTH;

public class Battle {

    Background bg;
    Sound ambient;
    String ambientPath;
    ArrayList<Unit> units;
    ArrayList<Unit> playersUnits;
    UnitsDrawGroup enemyGroup, friendlyGroup;
    ArrayList<Particle> particlesLeft, particlesRight;
    Texture divider;
    public boolean finished = false;
    public boolean loaded = false;
    public boolean initialized = false;

    public Battle(World w, ArrayList<Unit> units, ArrayList<Unit> playersUnits, String ambientPath) {
        this.units = units;
        this.playersUnits = playersUnits;
        this.ambientPath = ambientPath;
        particlesLeft = new ArrayList<Particle>();
        particlesRight = new ArrayList<Particle>();
    }

    public void load(World w) {
        w.assets.load(w.worldDir.path() + "/bg/battle_bg.png", Texture.class);
        w.assets.load(w.worldDir.path() + "/bg/battle_divider.png", Texture.class);
        w.assets.load(w.worldDir.path() + "/sounds/" + ambientPath, Sound.class);
        ArrayList<String> uniqueUnitNames = new ArrayList<String>();
        for (int i = 0; i < units.size(); ++i) {
            if (!uniqueUnitNames.contains(units.get(i).name)) {
                uniqueUnitNames.add(units.get(i).name);
                units.get(i).load(w);
            }
        }
        for (int i = 0; i < playersUnits.size(); ++i) {
            if (!uniqueUnitNames.contains(playersUnits.get(i).name)) {
                uniqueUnitNames.add(playersUnits.get(i).name);
                playersUnits.get(i).load(w);
            }
        }
        loaded = true;
    }

    public void initializeResources(World w) {
        if (!initialized && w.assets.update()) {
            for (int i = 0; i < units.size(); ++i) {
                units.get(i).initializeResources(w);
            }
            for (int i = 0; i < playersUnits.size(); ++i) {
                playersUnits.get(i).initializeResources(w);
            }
            this.bg = new Background();
            this.bg.addLayer(w.assets.get(w.worldDir.path() + "/bg/battle_bg.png", Texture.class), 0, 0,0 ,0);
            divider = w.assets.get(w.worldDir.path() + "/bg/battle_divider.png", Texture.class);
            ambient = w.assets.get(w.worldDir.path() + "/sounds/" + ambientPath, Sound.class);
            ambient.loop(w.menu.musicVolume / 100.0f);
            initialized = true;
            enemyGroup = new UnitsDrawGroup(units, 32, 0.9f);
            friendlyGroup = new UnitsDrawGroup(playersUnits, 32, 0.9f);
        }
    }

    public void draw(SpriteBatch batch) {
        bg.draw(batch, SCREEN_WIDTH / 2, SCREEN_HEIGHT, 0, 0);
        //bg.draw(batch, SCREEN_WIDTH / 2, SCREEN_HEIGHT, SCREEN_WIDTH / 2, 0);
        friendlyGroup.draw(batch, SCREEN_WIDTH / 4.0f, SCREEN_HEIGHT / 2.0f, particlesLeft);
        enemyGroup.draw(batch, 3.0f * SCREEN_WIDTH / 4.0f, SCREEN_HEIGHT / 2.0f, particlesRight);
        batch.draw(divider, 0, 0);

    }
}
