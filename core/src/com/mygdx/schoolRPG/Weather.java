package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.mygdx.schoolRPG.menus.GameMenu;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventListener;

public class Weather {
    public float windDir;
    public float windForce;
    private float baseWindDir;
    private float baseWindForce;
    private float windDirSpred;
    private float windForceSpread;
    private float targetWindDir;
    private float targetWindForce;
    private long lastChangeTime;
    private long changeFreq;
    private float maxChange;
    private float changeSpeed;

    private ArrayList<ParticleProperties.ParticleSpawnProperties> spawns;
    private ArrayList<Long> lastSpawnTimes;
    private ArrayList<Long> spawnFreqs;
    private ArrayList<Integer> spawnMinZs;
    private ArrayList<Integer> spawnMaxZs;
    private ArrayList<Integer> spawnCounts;

    public Weather(String filePath, String roomName, AssetManager assets, World world) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        org.w3c.dom.Document doc = null;
        FileHandle xmlFile =  Gdx.files.internal(filePath);
        try {
            doc = dBuilder.parse(xmlFile.file());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.xml.sax.SAXException e) {
            e.printStackTrace();
        }
        doc.getDocumentElement().normalize();
        Element mainElement = (Element)doc.getElementsByTagName("specific-weather").item(0);
        NodeList specificWeathers = doc.getElementsByTagName("area-weather");
        Element chosenWeather = null;
        for (int i =0; i < specificWeathers.getLength(); ++i) {
            Element specificWeather = (Element) specificWeathers.item(i);
            if (specificWeather.getAttribute("name").equals(roomName)) {
                chosenWeather = specificWeather;
                break;
            }
        }
        if (chosenWeather == null) chosenWeather = (Element) doc.getElementsByTagName("default-weather").item(0);
        Element windParams = (Element) chosenWeather.getElementsByTagName("wind-params").item(0);
        baseWindDir = Float.parseFloat(windParams.getAttribute("dir"));
        windDirSpred = Float.parseFloat(windParams.getAttribute("dirSpread"));
        windDir = baseWindDir + windDirSpred * (float)Math.random() - windDirSpred/2.0f;
        baseWindForce = Float.parseFloat(windParams.getAttribute("force"));
        windForceSpread = Float.parseFloat(windParams.getAttribute("forceSpread"));
        windForce = baseWindForce + windForceSpread * (float)Math.random() - windForceSpread/2.0f;
        changeFreq = Long.parseLong(windParams.getAttribute("changeFrequency"));
        changeSpeed = Float.parseFloat(windParams.getAttribute("changeSpeed"));
        spawns = new ArrayList<ParticleProperties.ParticleSpawnProperties>();
        NodeList spawnParams = chosenWeather.getElementsByTagName("particleSpawn");
        for (int i = 0; i < spawnParams.getLength(); ++i) {
            Element spawnParam = (Element) spawnParams.item(i);
            spawns.add(new ParticleProperties().new ParticleSpawnProperties(assets,
                    world.worldDir + "/sounds/" + spawnParam.getAttribute("spawnSound"),
                    spawnParam.getAttribute("name"),
                    0,
                    0,
                    0,
                    Float.parseFloat(spawnParam.getAttribute("spawnDir")),
                    Float.parseFloat(spawnParam.getAttribute("spawnSpeed")),
                    Float.parseFloat(spawnParam.getAttribute("spawnImpulse")),
                    Float.parseFloat(spawnParam.getAttribute("dirSpread")),
                    Float.parseFloat(spawnParam.getAttribute("speedSpread")),
                    Float.parseFloat(spawnParam.getAttribute("impulseSpread"))));
        }
        lastSpawnTimes = new ArrayList<Long>();
        spawnFreqs = new ArrayList<Long>();
        spawnMinZs = new ArrayList<Integer>();
        spawnMaxZs = new ArrayList<Integer>();
        spawnCounts = new ArrayList<Integer>();
        for (int i = 0; i < spawns.size(); ++i) {
            lastSpawnTimes.add(0l);
            spawnFreqs.add(Long.parseLong(((Element) spawnParams.item(i)).getAttribute("frequency")));
            spawnMinZs.add(Integer.parseInt(((Element) spawnParams.item(i)).getAttribute("minZ")));
            spawnMaxZs.add(Integer.parseInt(((Element) spawnParams.item(i)).getAttribute("maxZ")));
            spawnCounts.add(Integer.parseInt(((Element) spawnParams.item(i)).getAttribute("count")));
        }
    }

    private void changeWind() {
        targetWindDir = baseWindDir + windDirSpred * (float)Math.random() - windDirSpred/2.0f;
        targetWindForce = baseWindForce + windForceSpread * (float)Math.random() - windForceSpread/2.0f;
        lastChangeTime = System.currentTimeMillis();
    }

    public void invalidateWind(boolean paused) {
        if (System.currentTimeMillis() - lastChangeTime > changeFreq) {
            if (paused) {
                lastChangeTime = System.currentTimeMillis();
            } else {
                changeWind();
            }
        }
        else {
            float realDir;
            if (Math.abs(targetWindDir-windDir) > Math.abs((targetWindDir + 6.28)-windDir)) realDir = targetWindDir + 6.28f;
            else realDir = targetWindDir;
            if (Math.abs(realDir) > Math.abs((targetWindDir - 6.28)-windDir)) realDir = targetWindDir - 6.28f;
            else realDir = targetWindDir;
            if (windDir < realDir) {
                if (Math.abs(realDir - windDir) < changeSpeed) windDir = realDir;
                else windDir += changeSpeed;
            } else if (windDir > realDir) {
                if (Math.abs(realDir - windDir) < changeSpeed) windDir = realDir;
                else windDir -= changeSpeed;
            }
            if (windForce < targetWindForce) {
                if (Math.abs(targetWindForce - windForce) < changeSpeed) windForce = targetWindForce;
                else windForce += changeSpeed;
            } else if (windForce > targetWindForce) {
                if (Math.abs(targetWindForce - windForce) < changeSpeed) windForce = targetWindForce;
                else windForce -= changeSpeed;
            }
        }
    }

    public ArrayList<ParticleProperties.ParticleSpawnProperties> invalidateParticleSpawns(boolean paused) {
        if (paused) {
            for (int i = 0; i < spawns.size(); ++i) {
                if (System.currentTimeMillis() - lastSpawnTimes.get(i) > spawnFreqs.get(i)) {
                    lastSpawnTimes.set(i, System.currentTimeMillis());
                }
            }
            return null;
        }
        ArrayList<ParticleProperties.ParticleSpawnProperties> curSpawns = new ArrayList<ParticleProperties.ParticleSpawnProperties>();
        for (int i = 0; i < spawns.size(); ++i) {
            if (System.currentTimeMillis() - lastSpawnTimes.get(i) > spawnFreqs.get(i)) {
                lastSpawnTimes.set(i, System.currentTimeMillis());
                ParticleProperties.ParticleSpawnProperties sp = spawns.get(i);
                sp.spawnZ = spawnMinZs.get(i) + (float)Math.random() * (spawnMaxZs.get(i)-spawnMinZs.get(i)) - (spawnMaxZs.get(i)-spawnMinZs.get(i))/2.0f;
                for (int j = 0; j < spawnCounts.get(i); ++j) {
                    curSpawns.add(sp);
                }
            }
        }
        return curSpawns;
    }
}
