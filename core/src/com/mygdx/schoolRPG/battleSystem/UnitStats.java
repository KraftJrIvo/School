package com.mygdx.schoolRPG.battleSystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mygdx.schoolRPG.World;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class UnitStats {
    public boolean dead = false;
    public int level;
    public int baseHp;
    public int hp;
    public int maxHp;
    public int baseAp;
    public int ap;
    public int maxAp;
    public int exp;
    public int nextLvlExp;
    public int prevLvlExp;
    public int baseStr;
    public int str;
    public int baseVit;
    public int vit;
    public int baseDex;
    public int dex;
    public int baseExpReward;

    public UnitStats(World w, String unitName, int level) {
        FileHandle statsXML = Gdx.files.internal(w.worldDir + "/units/" + unitName + "/stats.xml");
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

        NodeList stateList = doc.getElementsByTagName("stats");
        Element stats = (Element) stateList.item(0);

        this.level = level;

        baseHp = Integer.parseInt(stats.getAttribute("baseHp"));
        maxHp = baseHp + level * (baseHp / (20 + (level / 5)));
        hp = maxHp;
        baseAp = Integer.parseInt(stats.getAttribute("baseAp"));
        maxAp = baseAp + level;
        ap = maxAp;
        baseStr = Integer.parseInt(stats.getAttribute("baseStr"));
        str = baseStr;
        baseVit = Integer.parseInt(stats.getAttribute("baseVit"));
        vit = baseVit;
        baseDex = Integer.parseInt(stats.getAttribute("baseDex"));
        dex = baseDex;
        for (int i = 0; i < level; ++i)
        {
            if (Math.random() < 0.333333f) str++;
            else if (Math.random() < 0.5f) vit++;
            else dex++;
        }
        baseExpReward = Integer.parseInt(stats.getAttribute("baseExpReward"));
        exp = 0;
        prevLvlExp = 0;
        nextLvlExp = 100 * level + ((100 * level) / 10);
    }

    public int getLevelXp(int level) {
        return 100 * level + ((100 * level) / 10);
    }

    public void levelUp() {
        prevLvlExp = nextLvlExp;
        level++;
        nextLvlExp = getLevelXp(level);
    }

    public void checkLevelUp() {
        if (exp > nextLvlExp)
        {
            levelUp();
        }
    }
}
